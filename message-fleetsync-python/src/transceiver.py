import logging
from typing import Callable

from serial import Serial, EIGHTBITS, STOPBITS_TWO, PARITY_NONE
from serial.threaded import ReaderThread

from message_handler import MessageHandler
from transceiver_protocol import TransceiverProtocol


class Transceiver:
    def __init__(self, handler: MessageHandler, port: str, onclose: Callable[[str], None]):
        self.log = logging.getLogger(port)
        self.log.info('Connecting to transceiver')

        self.serial = Serial(port=port, baudrate=9600, bytesize=EIGHTBITS, stopbits=STOPBITS_TWO, parity=PARITY_NONE, exclusive=True)
        self.worker = ReaderThread(self.serial, lambda: TransceiverProtocol(handler, port, onclose))
        self.worker.start()

    def close(self):
        self.log.info('Closing transceiver')
        try:
            self.worker.stop()
            self.serial.close()
        except Exception as ex:
            self.log.info('Exception on closing: %s', ex)
