# Exchange API

**Responsável:** Vinícius  
**Repositórios:** [vin1ciusL/exchange](https://github.com/vin1ciusL/exchange) · [vin1ciusL/exchange-interface](https://github.com/vin1ciusL/exchange-interface)  
**Stack:** Python 3 · FastAPI  
**Porta:** `8080`

---

## Responsabilidade

Fornece taxas de câmbio em tempo real entre diferentes moedas, consumindo a API externa [ExchangeRate-API](https://www.exchangerate-api.com/). Utilizado pelo Order Service para converter o total do pedido na moeda solicitada pelo cliente.

---

## Endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| `GET` | `/exchanges/{from}/{to}` | Taxa de câmbio entre duas moedas |
| `GET` | `/health` | Health check |

### GET /exchanges/{from}/{to}

Retorna a taxa de câmbio de `from` para `to`. Os códigos de moeda devem ter exatamente 3 caracteres (ISO 4217).

**Header obrigatório:**
- `id-account` — ID da conta solicitante

**Exemplo:** `GET /exchanges/BRL/USD` com header `id-account: uuid-da-conta`

**Response `200`:**
```json
{
  "sell": 0.1923,
  "buy": 0.1885,
  "date": "2026-05-30 21:00:00",
  "id-account": "uuid-da-conta"
}
```

A taxa de **venda** (`sell`) aplica spread de +2% sobre a taxa base. A taxa de **compra** (`buy`) aplica -2%.

Quando `from` e `to` são iguais, retorna `sell=1.0` e `buy=1.0` sem chamar a API externa.

### GET /health

```json
{
  "status": "healthy",
  "service": "exchange",
  "timestamp": "2026-05-30T21:00:00+00:00"
}
```

---

## Módulo de contrato (Java)

O módulo `exchange-interface` contém a interface OpenFeign e o DTO consumidos pelo Order Service:

```java
@FeignClient(name = "exchange", url = "${exchange.url}")
public interface ExchangeController {
    @GetMapping("/exchanges/{from}/{to}")
    ResponseEntity<ExchangeOut> getExchange(
        @PathVariable String from,
        @PathVariable String to,
        @RequestHeader("id-account") String idAccount
    );
}
```

```java
@Builder
public record ExchangeOut(
    Double sell,
    Double buy,
    String date,
    String idAccount
) {}
```

---

## Variáveis de ambiente

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `EXCHANGE_API_KEY` | — | Chave da ExchangeRate-API (injetada via secret) |
| `AUTH_SERVICE_URL` | `http://auth:8080` | URL do Auth Service |
| `REQUEST_TIMEOUT` | `6.0` | Timeout das chamadas HTTP em segundos |
