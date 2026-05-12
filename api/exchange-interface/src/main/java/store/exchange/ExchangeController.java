package store.exchange;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
    name = "exchange",
    url = "${exchange.url}"
)
public interface ExchangeController {

    @GetMapping("/exchanges/{from}/{to}")
    ResponseEntity<ExchangeOut> getExchange(
        @PathVariable String from,
        @PathVariable String to,
        @RequestHeader("Authorization") String authorization
    );

}
