package store.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record ExchangeOut(

    Double sell,
    Double buy,
    String date,
    @JsonProperty("id-account") String idAccount

) {

}
