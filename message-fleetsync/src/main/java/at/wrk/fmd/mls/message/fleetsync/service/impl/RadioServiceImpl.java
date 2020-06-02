package at.wrk.fmd.mls.message.fleetsync.service.impl;

import at.wrk.fmd.mls.message.config.MessageServiceProperties;
import at.wrk.fmd.mls.message.dto.IncomingMessageDto;
import at.wrk.fmd.mls.message.dto.OutgoingMessageDto;
import at.wrk.fmd.mls.message.handler.IncomingMessageHandler;
import at.wrk.fmd.mls.message.handler.OutgoingMessageHandler;
import at.wrk.fmd.mls.message.fleetsync.exception.PortException;
import at.wrk.fmd.mls.message.fleetsync.service.RadioService;
import gnu.io.CommPortIdentifier;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

@Service
public class RadioServiceImpl implements RadioService, OutgoingMessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final IncomingMessageHandler incomingHandler;
    private final String type;

    private final List<Transceiver> transceivers = new LinkedList<>();

    @Autowired
    public RadioServiceImpl(final IncomingMessageHandler incomingHandler, final MessageServiceProperties properties) {
        this.incomingHandler = incomingHandler;
        this.type = properties.getType();
    }

    @Override
    public void handleIncomingMessage(final String channel, final String sender, boolean emergency) {
        this.incomingHandler.forwardIncomingMessage(IncomingMessageDto.builder()
                .type(type)
                .channel(channel)
                .sender(sender)
                .emergency(emergency)
                .timestamp(Instant.now())
                .build()
        );
    }

    @Override
    public void sendMessage(OutgoingMessageDto message) {
        if (message == null || message.getRecipients() == null) {
            LOG.info("Call object or ANI is null");
            return;
        }

        synchronized (this) {
            // Technically, a ReadWriteLock could be used to make sure this does not happen during port reload
            // There's not really a point though, since the transceivers can't send multiple messages concurrently anyway
            Collection<String> recipients = message.getRecipients();
            LOG.debug("Trying to send RadioCall to {} on all ports", recipients);
            recipients.forEach(recipient -> transceivers.forEach(transceiver -> transceiver.sendCall(recipient)));
        }
    }

    @Override
    @PostConstruct
    public final synchronized void reloadPorts() {
        shutdown();

        LOG.info("(Re)detecting ports...");

        @SuppressWarnings("unchecked")
        Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();

        while (portEnum.hasMoreElements()) {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            if (portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                try {
                    transceivers.add(new Transceiver(portIdentifier, this));
                } catch (PortException e) {
                    LOG.error("Initialization of port '{}' failed", portIdentifier.getName(), e);
                    // Port in use
                    // intended NOP
                }
            }
        }
    }

    @PreDestroy
    public synchronized void shutdown() {
        LOG.info("Closing all ports...");
        transceivers.forEach(Transceiver::close);
        transceivers.clear();
    }
}
