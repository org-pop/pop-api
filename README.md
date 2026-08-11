# POP E-Commerce API

API RESTful em Java + Spring Boot para uma loja de Funko Pops, com foco em acessibilidade.

- Autenticação JWT + autorização por dono do recurso e por papel (`ROLE_USER` / `ROLE_ADMIN`)
- CRUD de usuários, produtos, carrinho, pedidos e pagamentos
- Checkout com controle de estoque concorrente (pessimistic lock)
- Configurações de acessibilidade por usuário (idioma, contraste, leitor de tela)
- Descrição acessível de produtos (paleta de cores, alt-text, tradução)

## Sumário

- [Stack](#stack)
- [Pré-requisitos](#pré-requisitos)
- [Configuração](#configuração)
- [Rodando](#rodando)
- [Endpoints](#endpoints)
- [Estrutura](#estrutura)
- [Integrantes](#integrantes)
- [Uso de IA](#uso-de-ia)

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

A aplicação sobe em `http://localhost:8080`. O Flyway aplica as migrations de `src/main/resources/db/migrations/` automaticamente na primeira execução (V001 → V005).

## Endpoints

Documentação completa com payloads e exemplos em [`docs/docs.md`](docs/docs.md).

Resumo:

| Recurso | Base path | Acesso |
|:--------|:----------|:-------|
| Autenticação | `/api/auth` | Público |
| Usuários | `/api/users` | Dono do recurso; `GET /api/users` e busca por email de terceiro só `ROLE_ADMIN` |
| Produtos | `/products` | Leitura: qualquer autenticado. Escrita: `ROLE_ADMIN` |
| Carrinho | `/cart` | Dono do recurso |
| Pedidos | `/orders` | Dono do recurso; mudança de status só `ROLE_ADMIN` |
| Pagamentos | `/payments` | Dono do pedido para leitura/criação; mudanças de status só `ROLE_ADMIN` |
| Endereços | `/addresses` | Dono do recurso |
| Telefones | `/phones` | Dono do recurso |
| Acessibilidade | `/api/accessibility` | `/languages` público; resto exige dono |

Todos os endpoints protegidos exigem o header:

```
Authorization: Bearer <token>
```

**Regras de autorização:**

- Endpoints com `{userId}` no path validam ownership contra o token: só o dono acessa. Caso contrário → **403 Forbidden**.
- Endpoints com `{orderId}` / `{paymentId}` / `{addressId}` / `{phoneId}` validam ownership pelo dono do recurso associado.
- Operações administrativas exigem `ROLE_ADMIN`: criar/editar/excluir produtos, alterar status de pedido, transitar status de pagamento (approve/decline/process/refund), listar todos os usuários. Novos usuários são criados com `ROLE_USER`; para promover, atualize `users.role` no banco.
- `password` e `role` são `@JsonIgnore` na entidade `User` — não vazam nas respostas mesmo quando o `User` vem embutido em `Order`, `Payment` etc.
- Token inválido ou expirado → **401** em JSON com o mesmo formato dos demais erros.

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
└── db/migrations/   # Flyway (V001–V005)
```

## Integrantes

<!-- Preencher antes da entrega. Rubrica §8.9 exige "integrantes com função". -->

| Nome                 | Função | GitHub               |
|:---------------------|:-------|:---------------------|
| _Davi Arakaki_       | Backend | @dvarakaki           |
| _Eduardo Policarpo_  | Frontend | @_edwardpolicarpo_   |
| _Felipe Boregio_     | _preencher_ | @_Lipe-to_           |
| _Lorraine Frontelli_ | _preencher_ | @_LorraineFrontelli_ |

## Uso de IA

Este backend foi desenvolvido com apoio de **Claude (Anthropic, modelo Opus 5)**, usado como par de programação para revisão de código, refatoração e documentação. Todo código foi lido, ajustado e commitado pelo autor — nada foi copiado sem revisão.

**Onde a IA foi usada:**

- **Revisão de segurança** que identificou os bloqueadores das migrations (falha silenciosa do Flyway por causa do underscore único nos nomes) e as colunas `users.role` e `product.version` faltantes.
- **Refatoração da camada de segurança**: `@JsonIgnore` em `User.password`/`role`, ownership em `AddressController`/`PhoneController`, remoção do `/cart-items` (que era duplicata insegura de `/cart`), `@PreAuthorize("hasRole('ADMIN')")` nas mutações de pagamento.
- **Máquina de estados de pagamento** (`PaymentService.ALLOWED_TRANSITIONS`).
- **Padronização** dos services legados (`AddressService`, `PhoneService`) para o padrão do restante do projeto (`@RequiredArgsConstructor`, `ResourceNotFoundException`).
- **Documentação**: revisão de `docs/docs.md` e atualização deste README.

**O que não foi feito por IA:** modelagem de dados original (users/orders/products/etc.), decisões de escopo, escolha de stack, integração com PostgreSQL Aiven, e a estrutura inicial do projeto — anteriores a esta rodada de revisão.

Detalhamento por arquivo em [`docs/uso-de-ia.md`](docs/uso-de-ia.md).

Cada integrante responde pelo código que aparece na arguição, independentemente da origem — em linha com a política do edital §10.1.
