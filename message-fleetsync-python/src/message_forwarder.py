from message import IncomingMessage


class MessageForwarder:

    def forward_message(self, message: IncomingMessage):
        raise NotImplementedError

    def close(self):
        pass
