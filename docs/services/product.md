# Product API

**Responsável:** Manoela  
**Repositórios:** [insper-rafaken/product](https://github.com/insper-rafaken/product) · [insper-rafaken/product-service](https://github.com/insper-rafaken/product-service)  
**Stack:** Java 25 · Spring Boot 4.0.5 · PostgreSQL 16  
**Porta:** `8080`

---

## Responsabilidade

Gerencia o catálogo de produtos disponíveis para compra, incluindo nome, preço e unidade de medida.

---

## Endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| `POST` | `/products` | Cria um produto |
| `GET` | `/products` | Lista todos os produtos |
| `GET` | `/products/{id}` | Busca produto por ID |
| `DELETE` | `/products/{id}` | Remove um produto |

### POST /products

**Request body:**
```json
{
  "name": "Camiseta",
  "price": 49.90,
  "unit": "un"
}
```

**Response `201`:**
```json
{
  "id": "uuid-do-produto",
  "name": "Camiseta",
  "price": 49.90,
  "unit": "un"
}
```

### GET /products/{id}

Retorna um produto pelo UUID. Utilizado internamente pelo Order Service via OpenFeign para buscar o preço de cada item durante o processamento de pedidos.

**Response `200`:**
```json
{
  "id": "uuid-do-produto",
  "name": "Camiseta",
  "price": 49.90,
  "unit": "un"
}
```

### DELETE /products/{id}

Remove um produto pelo UUID. Retorna `204 No Content`.

---

## Módulo de contrato

O módulo `product` contém:

- `ProductController` — interface OpenFeign com os quatro endpoints
- `ProductIn` — DTO de entrada (`name`, `price`, `unit`)
- `ProductOut` — DTO de saída (`id`, `name`, `price`, `unit`)

Outros serviços que consomem produtos importam apenas esse módulo como dependência Maven, sem acoplamento à implementação.

```java
@FeignClient(name = "product", url = "${product.url}")
public interface ProductController {
    @GetMapping("/products/{id}")
    ResponseEntity<ProductOut> findById(@PathVariable UUID id);
}
```

---

## Banco de dados

- **Database:** `product`
- **Schema:** `products`
- **Migrations:** Flyway

### Variáveis de ambiente

| Variável | Descrição |
|----------|-----------|
| `DATABASE_HOST` | Host do PostgreSQL |
| `DATABASE_PORT` | Porta (padrão: `5432`) |
| `DATABASE_DB` | Nome do banco (`product`) |
| `DATABASE_USERNAME` | Usuário (`product`) |
| `DATABASE_PASSWORD` | Senha (injetada via secret) |
