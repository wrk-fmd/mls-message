import logging

import serial.tools.list_ports

from message_handler import MessageHandler
from transceiver import Transceiver


class TransceiverManager:
    def __init__(self, handler: MessageHandler, port_filter='/dev/ttyUSB[0-9]+'):
        self.log = logging.getLogger('manager')

        self.handler = handler
        self.port_filter = port_filter
        self.transceivers = {}

    def check_ports(self):
        self.log.debug('Looking for transceivers...')
        ports = [d for d, p, i in serial.tools.list_ports.grep(self.port_filter)]
        for port in set(self.transceivers).difference(ports):
            self.unregister_port(port)
        for port in sorted(set(ports).difference(self.transceivers)):
            self.register_port(port)

    def register_port(self, port: str):
        try:
            self.log.info('Registering transceiver: %s', port)
            self.transceivers[port] = Transceiver(self.handler, port, onclose=self.unregister_port)
        except Exception as ex:
            self.log.error('Error registering transceiver %s: %s', port, ex)

    def unregister_port(self, port: str):
        self.log.info('Removing transceiver: %s', port)
        try:
            self.transceivers[port].close()
            del self.transceivers[port]
        except KeyError:
            pass

    def close(self):
        self.log.info('Shutting down transceiver manager')
        ports = list(self.transceivers.keys())
        for port in ports:
            self.unregister_port(port)
