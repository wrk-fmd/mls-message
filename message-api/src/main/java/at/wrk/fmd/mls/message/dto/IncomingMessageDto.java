package at.wrk.fmd.mls.message.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class IncomingMessageDto {

    private String type;
    private String channel;
    private String sender;
    private Instant timestamp;
    private boolean emergency;
    private Object payload;
}
