package store.exchange;

import lombok.Builder;

@Builder
public record ExchangeOut(

    Double sell,
    Double buy,
    String date,
    String idAccount

) {
    
}
