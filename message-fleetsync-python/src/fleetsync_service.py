import time

from message_handler import MessageHandler
from transceiver_manager import TransceiverManager


def create_forwarder(config):
    if config['type'] == 'amqp':
        from amqp_forwarder import AmqpForwarder
        return AmqpForwarder(config['name'], config['host'], config['username'], config['password'], config['exchange'])

    if config['type'] == 'http':
        from http_forwarder import HttpForwarder
        return HttpForwarder(config['name'], config['url'], config['headers'])

    raise Exception('Invalid forwarder config: %s' % config)


class FleetsyncService:

    def __init__(self, config):
        self.handler = MessageHandler(config['service-name'], list(map(create_forwarder, config['forwarders'])))

        if 'device-filter' in config:
            self.manager = TransceiverManager(self.handler, config['device-filter'])
        else:
            self.manager = TransceiverManager(self.handler)

        if 'interval' in config:
            self.interval = config['interval']
        else:
            self.interval = 60

    def run(self):
        while True:
            try:
                self.manager.check_ports()
                time.sleep(self.interval)
            except KeyboardInterrupt:
                break
            except SystemExit:
                raise

    def close(self):
        self.handler.close()
        self.manager.close()
