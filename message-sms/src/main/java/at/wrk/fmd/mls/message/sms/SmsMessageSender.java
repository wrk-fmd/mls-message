package at.wrk.fmd.mls.message.sms;

import at.wrk.fmd.mls.message.dto.OutgoingMessageDto;
import at.wrk.fmd.mls.message.handler.OutgoingMessageHandler;
import at.wrk.fmd.mls.message.sms.request.RequestProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SmsMessageSender implements OutgoingMessageHandler {

    private final RequestProvider requestProvider;
    private final RestTemplate restTemplate;
    private final PhoneNumberNormalizer normalizer;

    @Autowired
    public SmsMessageSender(final RequestProvider requestProvider, final RestTemplate restTemplate,
            final PhoneNumberNormalizer normalizer) {
        this.requestProvider = requestProvider;
        this.restTemplate = restTemplate;
        this.normalizer = normalizer;
    }

    @Override
    public void sendMessage(final OutgoingMessageDto message) {
        try {
            Collection<String> recipients = filteredRecipients(message.getRecipients());
            if (recipients.isEmpty()) {
                log.info("Ignoring message with no valid recipients");
                return;
            }

            Object request = requestProvider.buildRequest(recipients, message.getPayload());
            ResponseEntity<String> response = restTemplate.postForEntity("/", request, String.class);

            log.debug("Successfully written alarm text to SMS gateway.");
            log.trace("SMS Gateway returned: {}", response.getBody());
        } catch (Throwable e) {
            log.warn("Failed to send alarm text to SMS gateway. Message: {}", e.getMessage());
            log.debug("Underlying exception", e);
            throw e;
        }
    }

    private Collection<String> filteredRecipients(Collection<String> recipients) {
        return recipients.stream()
                .map(normalizer::normalize)
                .filter(normalizer::filter)
                .collect(Collectors.toSet());
    }
}
