package at.wrk.fmd.mls.message.sms.request;

import java.util.Collection;

public interface RequestProvider {

    Object buildRequest(Collection<String> recipients, String message);
}
