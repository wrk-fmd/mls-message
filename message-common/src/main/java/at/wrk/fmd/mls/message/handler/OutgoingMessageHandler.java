package at.wrk.fmd.mls.message.handler;

import at.wrk.fmd.mls.message.dto.OutgoingMessageDto;

public interface OutgoingMessageHandler {

    void sendMessage(OutgoingMessageDto message);
}
