import json
import logging

import pika

from message import IncomingMessage
from message_forwarder import MessageForwarder


class AmqpForwarder(MessageForwarder):

    def __init__(self, name: str, host: str, username: str, password: str, exchange: str):
        self.log = logging.getLogger(name)
        self.log.info('Opening AMQP connection')

        # Connect to the AMQP broker
        conn_params = pika.ConnectionParameters(host=host, credentials=pika.PlainCredentials(username, password))
        self.connection = pika.BlockingConnection(conn_params)

        try:
            # Create a channel and declare the target exchange
            self.channel = self.connection.channel()
            self.channel.exchange_declare(exchange=exchange, exchange_type='fanout', durable=True)
            self.exchange = exchange
        except Exception as ex:
            # Make sure the connection is closed on exceptions
            self.close()
            raise ex

    def forward_message(self, message: IncomingMessage):
        try:
            body = json.dumps(message.__dict__)
            self.log.debug('Sending message: %s', body)
            self.channel.basic_publish(self.exchange, '', body)
        except Exception as ex:
            self.log.error('Exception on sending message to AMQP: %s', ex)

    def close(self):
        self.log.info('Closing AMQP forwarder')
        try:
            self.connection.close()
        except Exception as ex:
            self.log.warning('Exception on closing AMQP connection: %s', ex)
