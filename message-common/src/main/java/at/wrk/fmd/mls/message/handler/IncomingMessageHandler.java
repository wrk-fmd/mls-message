package at.wrk.fmd.mls.message.handler;

import at.wrk.fmd.mls.message.dto.IncomingMessageDto;

public interface IncomingMessageHandler {

    void forwardIncomingMessage(IncomingMessageDto message);
}
