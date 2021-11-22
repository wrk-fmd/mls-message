import logging
import re
from typing import Callable

import serial.threaded

from message_handler import MessageHandler


class TransceiverProtocol(serial.threaded.Packetizer):
    PACKET_START = b'\x02'
    CALL_START = b'I1'
    EMERGENCY_START = b'E'
    TERMINATOR = b'\x03'
    ANI_PATTERN = re.compile(r'^\d{7}$')

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

        message = packet[start + 1:]
        if message.startswith(self.CALL_START):
            ani = message[len(self.CALL_START):].decode()
            emergency = False
        elif message.startswith(self.EMERGENCY_START):
            ani = message[len(self.EMERGENCY_START):].decode()
            emergency = True
        else:
            self.log.info('Dropping unknown message %s', message)
            return

        if not self.ANI_PATTERN.match(ani):
            self.log.info('Dropping invalid ANI %s', ani)
            return

        self.handler.handle_message(self.port, ani, emergency)

    def connection_lost(self, ex):
        self.log.info('Connection lost: %s', ex)
        self.onclose(self.port)
