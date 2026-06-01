# Arquitetura

## Visão geral

A plataforma é composta por microserviços independentes que se comunicam via HTTP (OpenFeign) e mensageria assíncrona (RabbitMQ). O Gateway é o único ponto de entrada externo — todos os clientes se comunicam exclusivamente por ele.

```mermaid
graph LR
    subgraph Cliente
        Browser(["Browser / App"])
    end

    subgraph "Camada de Entrada"
        GW["Gateway\nSpring Cloud Gateway\n:8080"]
    end

    subgraph "Serviços de Identidade"
        Auth["Auth Service\nJava 25 / Spring Boot\n:8080"]
        Account["Account Service\nJava 25 / Spring Boot\n:8080"]
    end

    subgraph "Serviços de Negócio"
        Order["Order Service\nJava 25 / Spring Boot\n:8081"]
        Product["Product Service\nJava 25 / Spring Boot\n:8080"]
        Exchange["Exchange Service\nPython 3 / FastAPI\n:8080"]
    end

    subgraph "Infraestrutura"
        RabbitMQ[("RabbitMQ\n:5672")]
        Redis[("Redis\n:6379")]
        OrderDB[("PostgreSQL\norder")]
        ProductDB[("PostgreSQL\nproduct")]
        AccountDB[("PostgreSQL\naccount")]
    end

    Browser --> GW
    GW --> Auth
    GW --> Account
    GW --> Order
    GW --> Product
    GW --> Exchange
    Auth --> Account
    Order --> Product
    Order --> Exchange
    Order -->|"order.exchange"| RabbitMQ
    RabbitMQ -->|"order.queue"| Order
    Order <--> Redis
    Order --- OrderDB
    Product --- ProductDB
    Account --- AccountDB
```

---

## Stack tecnológica

| Serviço | Linguagem | Framework | Versão |
|---------|-----------|-----------|--------|
| Gateway | Java 25 | Spring Cloud Gateway | 4.0.5 |
| Auth | Java 25 | Spring Boot | 4.0.5 |
| Account | Java 25 | Spring Boot | 4.0.5 |
| Order | Java 25 | Spring Boot | 4.0.5 |
| Product | Java 25 | Spring Boot | 4.0.5 |
| Exchange | Python 3 | FastAPI | — |

**Spring Cloud:** 2025.1.1 (OpenFeign, Gateway)  
**Banco de dados:** PostgreSQL 16/17 (schema isolado por serviço)  
**Migrations:** Flyway  
**Mensageria:** RabbitMQ 3  
**Cache:** Redis 7  

---

## Padrões adotados

### Módulo de contrato

Cada serviço Java é dividido em dois módulos Maven:

- **`<service>`** — módulo de contrato: interface OpenFeign + DTOs (`*In`, `*Out`). Publicado no repositório Maven local para ser consumido por outros serviços.
- **`<service>-service`** — implementação: lógica de negócio, persistência, configuração.

Isso garante que o consumidor de um serviço dependa apenas do contrato, sem acoplamento à implementação.

### Comunicação entre serviços

- **Síncrona:** OpenFeign (HTTP/REST) — usada quando a resposta é necessária imediatamente (ex: Order consulta Product e Exchange).
- **Assíncrona:** RabbitMQ — usada para processamento que pode ocorrer depois do retorno ao cliente (ex: enriquecimento de preços do pedido).

### Autenticação

O Gateway valida o JWT em todas as rotas protegidas antes de encaminhar ao serviço destino. Os serviços internos confiam no Gateway e não revalidam o token.

### Banco de dados

Cada serviço tem seu próprio banco PostgreSQL isolado. O Order e o Product usam schemas nomeados (`orders`, `products`) dentro de databases dedicadas para separação lógica.

---

## Fluxo de criação de pedido

```mermaid
sequenceDiagram
    participant C as Cliente
    participant GW as Gateway
    participant O as Order Service
    participant MQ as RabbitMQ
    participant P as Product Service
    participant Redis

    C->>GW: POST /orders
    GW->>O: POST /orders (com id-account)
    O->>O: salva pedido (status=PENDING)
    O-->>MQ: publica OrderMessage (após commit)
    O-->>GW: 202 Accepted
    GW-->>C: 202 Accepted

    MQ->>O: consome OrderMessage
    O->>Redis: GET product-prices::{id}
    alt cache miss
        O->>P: GET /products/{id}
        P-->>O: ProductOut
        O->>Redis: SET product-prices::{id}
    end
    O->>O: calcula total, status=CONFIRMED
    O->>O: salva pedido atualizado
```

---

## Infraestrutura AWS

```mermaid
graph TD
    subgraph "AWS us-east-2"
        subgraph "VPC eks-vpc"
            subgraph "Subnets Públicas"
                LB["Load Balancer"]
            end
            subgraph "Subnets Privadas"
                subgraph "EKS eks-store"
                    N1["Node t3.medium"]
                    N2["Node t3.medium"]
                end
            end
        end
        ECR["ECR / Docker Hub"]
        Jenkins["Jenkins CI/CD"]
    end

    Dev["Desenvolvedor"] --> Jenkins
    Jenkins --> ECR
    ECR --> N1
    ECR --> N2
    Internet --> LB --> N1
    Internet --> LB --> N2
```
