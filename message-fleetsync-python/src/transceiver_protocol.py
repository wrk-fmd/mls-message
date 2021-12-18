import logging
import re
from typing import Callable

import serial.threaded

from message_handler import MessageHandler


class TransceiverProtocol(serial.threaded.Packetizer):
    PACKET_START = b'\x02'
    CALL_START = b'I1'
    EMERGENCY_START = b'E'
    IGNORED_START = [b'T0', b'T1']
    TERMINATOR = b'\x03'
    ANI_PATTERN = re.compile(r'^\d{7}$')

    last_seq = None

    def __init__(self, handler: MessageHandler, port: str, onclose: Callable[[str], None]):
        super().__init__()
        self.log = logging.getLogger(port)

        self.handler = handler
        self.port = port
        self.onclose = onclose

    def handle_packet(self, packet: bytearray):
        packet = bytes(packet)
        self.log.debug('Handling packet %s', packet)

        start = packet.rfind(self.PACKET_START)
        if start < 0:
            self.log.info('Dropping data without start byte %s', packet)
            return

        if start > 0:
            self.log.info('Dropping data up to start byte %s', packet[0:start])

        # Check sequence number at the end of the packet (does it even matter? just log it for now)
        current_seq = int(packet[-2:])
        if self.last_seq is not None:
            expected_seq = (self.last_seq + 1) % 100
            if current_seq != expected_seq:
                self.log.info('Got sequence number %02d, expected %02d', current_seq, expected_seq)
        self.last_seq = current_seq

        # Handle the actual message
        self.handle_message(packet[start + 1:-2])

    def handle_message(self, message: bytes):
        if message.startswith(self.CALL_START):
            # Got a regular PTT event
            ani = message[len(self.CALL_START):].decode()
            emergency = False
        elif message.startswith(self.EMERGENCY_START):
            # Got an emergency event
            ani = message[len(self.EMERGENCY_START):].decode()
            emergency = True
        elif message in self.IGNORED_START:
            # These are always sent before/after a message (do we even care?)
            self.log.debug('Ignoring message %s', message)
            return
        else:
            # Got something else, log it
            self.log.info('Dropping unknown message %s', message)
            return

        # ANI is sent twice in one message
        ani_mid = len(ani) // 2
        ani_first = ani[:ani_mid]
        ani_second = ani[ani_mid:]
        if not ani_first == ani_second:
            # Just log if ANIs don't match
            self.log.info('Got two different ANIs in one message: %s, %s', ani_first, ani_second)

        if not self.ANI_PATTERN.match(ani_first):
            self.log.info('Dropping invalid ANI %s', ani_first)
            return

        self.handler.handle_message(self.port, ani_first, emergency)

    def connection_lost(self, ex):
        self.log.info('Connection lost: %s', ex)
        self.onclose(self.port)
