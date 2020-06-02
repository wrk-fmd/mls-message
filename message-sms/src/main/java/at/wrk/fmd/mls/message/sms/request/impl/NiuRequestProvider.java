package at.wrk.fmd.mls.message.sms.request.impl;

import at.wrk.fmd.mls.message.sms.request.RequestProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class NiuRequestProvider implements RequestProvider {

    @Override
    public Object buildRequest(Collection<String> recipients, String message) {
        return new NiuRequest(recipients, message);
    }

    @Getter
    @AllArgsConstructor
    private static class NiuRequest {

        private final Collection<String> numbers;
        private final String message;
        private final boolean waitForDelivery = false;
    }
}
