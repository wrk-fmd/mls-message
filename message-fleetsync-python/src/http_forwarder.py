import json
import logging
import urllib.request

from message import IncomingMessage
from message_forwarder import MessageForwarder


class HttpForwarder(MessageForwarder):

    def __init__(self, name: str, url: str, headers: dict[str, str]):
        self.log = logging.getLogger(name)
        self.url = url
        self.headers = headers
        self.headers['Content-Type'] = 'application/json'

    def forward_message(self, message: IncomingMessage):
        try:
            body = json.dumps(message.__dict__)
            self.log.debug('Sending message: %s', body)
            request = urllib.request.Request(self.url, body.encode('utf-8'), self.headers)
            urllib.request.urlopen(request)
        except Exception as ex:
            self.log.error('Exception on sending message to HTTP endpoint: %s', ex)
