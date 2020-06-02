package at.wrk.fmd.mls.message.dto;

import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class OutgoingMessageDto {

    @NotNull
    private String type;

    @NotNull
    private Collection<String> recipients;

    @NotNull
    private String payload;
}
