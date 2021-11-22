from dataclasses import dataclass


@dataclass
class IncomingMessage:
    type: str
    channel: str
    sender: str
    emergency: bool
    timestamp: float
