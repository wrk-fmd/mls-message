package at.wrk.fmd.mls.message.handler.impl;

import at.wrk.fmd.mls.message.dto.IncomingMessageDto;
import at.wrk.fmd.mls.message.dto.MessageExchangeNames;
import at.wrk.fmd.mls.message.handler.IncomingMessageHandler;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

/**
 * This class can be used by the service to publish incoming messages on the message broker
 */
@Component
class IncomingMessageForwarder implements IncomingMessageHandler {

    private final AmqpTemplate amqpTemplate;

    public IncomingMessageForwarder(final AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    @Override
    public void forwardIncomingMessage(final IncomingMessageDto message) {
        amqpTemplate.convertAndSend(MessageExchangeNames.MESSAGE_INCOMING, null, message);
    }
}
