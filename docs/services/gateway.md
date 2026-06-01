# Gateway

**Responsável:** Rafael Ken  
**Repositório:** [insper-rafaken/gateway-service](https://github.com/insper-rafaken/gateway-service)  
**Stack:** Java 25 · Spring Cloud Gateway 2025.1.1  
**Porta:** `8080`

---

## Responsabilidade

Ponto de entrada único da plataforma. Responsável por roteamento, CORS e filtro de autenticação JWT. Nenhum serviço interno é exposto diretamente ao cliente.

---

## Rotas configuradas

| Rota | Destino | Descrição |
|------|---------|-----------|
| `/accounts/**` | `http://account:8080` | Gerenciamento de contas |
| `/auth/**` | `http://auth:8080` | Autenticação e tokens |
| `/orders/**` | `http://order:8081` | Pedidos |
| `/products/**` | `http://product:8080` | Produtos |
| `/exchanges/**` | `http://exchange:8080` | Taxas de câmbio |
| `/insper/**` | `https://www.insper.edu.br` | Redirecionamento externo |

---

## CORS

O Gateway configura CORS globalmente para todas as rotas:

```yaml
globalcors:
  corsConfigurations:
    '[/**]':
      allowedOrigins: ${CORS_ALLOWED_ORIGINS}
      allowedHeaders: "*"
      allowedMethods: "*"
      allowCredentials: ${CORS_ALLOWED_CREDENTIALS}
```

As origens permitidas são configuradas via variáveis de ambiente, permitindo ajuste por ambiente (dev, prod).

---

## Métricas

O Gateway expõe métricas Prometheus no endpoint:

```
/gateway/actuator/prometheus
```

---

## Variáveis de ambiente

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `CORS_ALLOWED_ORIGINS` | — | Origens permitidas (ex: `http://localhost`) |
| `CORS_ALLOWED_CREDENTIALS` | `true` | Permite cookies/credentials |
