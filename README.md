# POP E-Commerce API

API RESTful em Java + Spring Boot para uma loja de Funko Pops, com foco em acessibilidade.

- Autenticação JWT
- CRUD de usuários, produtos, carrinho, pedidos e pagamentos
- Configurações de acessibilidade por usuário (idioma, contraste, leitor de tela)
- Descrição acessível de produtos (paleta de cores, alt-text, tradução)

## Sumário

- [Stack](#stack)
- [Pré-requisitos](#pré-requisitos)
- [Configuração](#configuração)
- [Rodando](#rodando)
- [Endpoints](#endpoints)
- [Estrutura](#estrutura)

## Stack

| Tecnologia | Versão |
|:-----------|:-------|
| Java | 17 |
| Spring Boot | 3.4.1 |
| Spring Security | 6.x |
| Spring Data JPA / Hibernate | 6.6 |
| PostgreSQL | 16 |
| Flyway | 10.x |
| JWT (JJWT) | 0.11.5 |
| Lombok | 1.18 |
| Maven Wrapper | incluso |

## Pré-requisitos

- **Java 17+**
- **PostgreSQL 16** (ou Aiven / Neon / qualquer cluster acessível via JDBC)
- Arquivo `.env` na raiz do projeto com as credenciais (ver [`SETUP.md`](SETUP.md))

## Configuração

O `application.properties` lê variáveis do `.env` através do `DotenvConfig`:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION}
```

**`.env` mínimo:**

```dotenv
DB_URL=jdbc:postgresql://localhost:5432/pop
DB_USERNAME=pop_user
DB_PASSWORD=troque_isso
JWT_SECRET=uma_string_com_no_minimo_32_caracteres_aleatorios
JWT_EXPIRATION=86400000
```

## Rodando

**Windows (PowerShell):**

```powershell
.\mvnw.cmd spring-boot:run
```

**Linux / macOS:**

```bash
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`. Flyway aplica as migrations de `src/main/resources/db/migrations/` automaticamente.

## Endpoints

Documentação completa com payloads e exemplos em [`docs/docs.md`](docs/docs.md).

Resumo:

| Recurso | Base path | Público |
|:--------|:----------|:--------|
| Autenticação | `/api/auth` | Sim |
| Usuários | `/api/users` | Não |
| Produtos | `/products` | Não |
| Carrinho | `/cart` | Não (só o dono) |
| Itens de carrinho | `/cart-items` | Não |
| Pedidos | `/orders` | Não (só o dono) |
| Pagamentos | `/payments` | Não |
| Endereços | `/addresses` | Não |
| Telefones | `/phones` | Não |
| Acessibilidade | `/api/accessibility` | `/languages` público; resto protegido |

Todos os endpoints protegidos exigem o header:

```
Authorization: Bearer <token>
```

Endpoints que carregam `{userId}` no path validam ownership: só o dono do token pode acessar. Caso contrário → **403 Forbidden**.

## Estrutura

```
src/main/java/com/acessibiliadade/pop/
├── PopApplication.java
├── config/          # SecurityConfig, DotenvConfig, PasswordEncoderConfig
├── controller/      # REST controllers
├── dto/             # Records de request/response
├── enums/           # AccessibilityProfile, OrderStatus, PaymentStatus
├── exception/       # Exceptions customizadas + GlobalExceptionHandler
├── filter/          # JwtAuthenticationFilter
├── model/           # Entidades JPA
├── repository/      # Repositórios Spring Data
├── security/        # AuthorizationService (ownership check)
└── service/         # Regras de negócio

src/main/resources/
├── application.properties
└── db/migrations/   # Flyway (V001, V002, V003...)
```
