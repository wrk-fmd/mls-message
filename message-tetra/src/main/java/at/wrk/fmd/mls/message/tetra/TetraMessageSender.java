package at.wrk.fmd.mls.message.tetra;

import at.wrk.fmd.mls.message.dto.OutgoingMessageDto;
import at.wrk.fmd.mls.message.handler.OutgoingMessageHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Slf4j
@Component
public class TetraMessageSender implements OutgoingMessageHandler {

    private final TetraIssiNormalizer normalizer;
    private final RestTemplate restTemplate;

    @Autowired
    public TetraMessageSender(final TetraIssiNormalizer normalizer, final RestTemplate restTemplate) {
        this.normalizer = normalizer;
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendMessage(final OutgoingMessageDto message) {
        message.getRecipients().stream()
                .map(normalizer::normalize)
                .filter(Objects::nonNull)
                .forEach(recipient -> sendMessage(recipient, message.getPayload()));
    }

    private void sendMessage(final String recipient, final String payload) {
        try {
            SdsRequest request = new SdsRequest(recipient, payload);
            ResponseEntity<String> response = restTemplate.postForEntity("/sds/send", request, String.class);

            log.debug("Successfully written alarm text to Tetra gateway.");
            log.trace("Tetra Gateway returned: {}", response.getBody());
        } catch (Throwable e) {
            log.warn("Failed to send alarm text to Tetra gateway. Message: {}", e.getMessage());
            log.debug("Underlying exception", e);
            throw e;
        }
    }

    @Getter
    @AllArgsConstructor
    private static class SdsRequest {

        private final String calledParty;
        private final String messageContent;
        private final String type = "ack";
    }
}
