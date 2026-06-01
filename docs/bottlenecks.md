# Bottlenecks

Cada membro do grupo implementou ao menos um bottleneck no seu microserviço para melhorar performance e resiliência sob carga.

---

## Order Service — Rafael Ken

### Bottleneck 1: Fila assíncrona com RabbitMQ

**Problema:** Ao criar um pedido, o serviço precisava consultar o Product Service para buscar os preços de cada item antes de retornar a resposta. Sob carga, isso criava um gargalo: o tempo de resposta do `POST /orders` dependia diretamente do tempo de resposta do Product Service.

**Solução:** O pedido é salvo imediatamente com status `PENDING` e o cliente recebe `202 Accepted` sem aguardar o processamento. Após o commit da transação, uma mensagem é publicada no RabbitMQ e o `OrderConsumer` processa o pedido de forma assíncrona — buscando preços e calculando o total.

```
POST /orders → salva PENDING → 202 Accepted (imediato)
                                    ↓ (assíncrono)
                            RabbitMQ → OrderConsumer → CONFIRMED
```

**Benefício:** O tempo de resposta do endpoint de criação cai de O(n×latência_product) para constante. O sistema continua processando mesmo se o Product Service estiver lento.

**Race condition evitada:** A publicação na fila ocorre **dentro do hook `afterCommit`** do `TransactionSynchronizationManager`, garantindo que o consumer nunca leia um pedido que ainda não foi commitado no banco.

---

### Bottleneck 2: Cache de preços com Redis

**Problema:** O `OrderConsumer` chamava o Product Service para cada item de cada pedido processado. Pedidos com produtos populares resultavam em chamadas redundantes ao mesmo produto.

**Solução:** Os preços de produtos são cacheados no Redis com TTL de 5 minutos usando `@Cacheable`:

```java
@Cacheable(value = "product-prices", key = "#productId")
public ProductOut getProduct(String productId) {
    return productController.findById(UUID.fromString(productId)).getBody();
}
```

**Resiliência:** O `CacheErrorHandler` implementado captura falhas do Redis e faz fallback silencioso para o Product Service — a aplicação nunca cai por indisponibilidade do cache.

**Benefício:** Chamadas ao Product Service reduzidas drasticamente para produtos consultados repetidamente. Latência do processamento de pedidos reduzida.

