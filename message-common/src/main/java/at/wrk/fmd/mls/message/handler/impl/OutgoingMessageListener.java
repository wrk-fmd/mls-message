package at.wrk.fmd.mls.message.handler.impl;

import at.wrk.fmd.mls.message.dto.MessageExchangeNames;
import at.wrk.fmd.mls.message.dto.OutgoingMessageDto;
import at.wrk.fmd.mls.message.handler.OutgoingMessageHandler;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class listens for outgoing messages on the message broker and sends them using the service implementation
 */
@Component
class OutgoingMessageListener {

    private final OutgoingMessageHandler handler;

    @Autowired
    public OutgoingMessageListener(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") OutgoingMessageHandler handler) {
        this.handler = handler;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue,
            exchange = @Exchange(MessageExchangeNames.MESSAGE_OUTGOING),
            key = "#{messageServiceProperties.type}"
    ))
    public void listen(OutgoingMessageDto message) {
        handler.sendMessage(message);
    }
}
