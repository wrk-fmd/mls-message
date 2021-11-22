import time

from message import IncomingMessage
from message_forwarder import MessageForwarder


class MessageHandler:
    def __init__(self, service_name: str, forwarders: list[MessageForwarder]):
        self.service_name = service_name
        self.forwarders = forwarders

    def handle_message(self, channel: str, sender: str, emergency: bool):
        message = IncomingMessage(self.service_name, channel, sender, emergency, time.time())
        for forwarder in self.forwarders:
            forwarder.forward_message(message)

    def close(self):
        for forwarder in self.forwarders:
            forwarder.close()
