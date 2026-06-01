# Account Service

**Responsável:** Rafael Ken  
**Repositórios:** [insper-rafaken/account](https://github.com/insper-rafaken/account) · [insper-rafaken/account-service](https://github.com/insper-rafaken/account-service)  
**Stack:** Java 25 · Spring Boot 4.0.5 · PostgreSQL 17  
**Porta:** `8080`

---

## Responsabilidade

Gerenciamento de contas de usuário. Armazena credenciais e dados de perfil. É consumido pelo Auth Service durante o processo de autenticação.

---

## Banco de dados

- **Database:** `store`
- **Schema:** `accounts`
- **Migrations:** Flyway

---

## Variáveis de ambiente

| Variável | Descrição |
|----------|-----------|
| `DATABASE_HOST` | Host do PostgreSQL |
| `DATABASE_PORT` | Porta (padrão: `5432`) |
| `DATABASE_DB` | Nome do banco (`store`) |
| `DATABASE_USERNAME` | Usuário (`store`) |
| `DATABASE_PASSWORD` | Senha (injetada via secret) |
