# 🐵  Documentação Oficial da API - POP E-Commerce

**Versão:** 1.0.0  
**Status:** Estável (Production Ready)  
**Base URL:** `http://localhost:8080`  
**Tecnologias:** Java 17, Spring Boot 3.x, Spring Security, JWT, Spring Data JPA, PostgreSQL/MySQL, Maven.

---

## Índice

1. [Introdução](#1-introdução)
2. [Autenticação e Segurança](#2-autenticação-e-segurança)
3. [Fluxo de Autenticação](#3-fluxo-de-autenticação)
4. [Formato de Dados e Headers](#4-formato-de-dados-e-headers)
5. [Códigos de Status HTTP](#5-códigos-de-status-http)
6. [Modelos de Dados (Schemas)](#6-modelos-de-dados-schemas)
7. [Endpoints - Autenticação](#7-endpoints-autenticação-apiauth)
8. [Endpoints - Usuários](#8-endpoints-usuários-apiusers)
9. [Endpoints - Produtos](#9-endpoints-produtos-products)
10. [Endpoints - Carrinho de Compras](#10-endpoints-carrinho-de-compras-cart)
11. [Endpoints - Itens do Carrinho](#11-endpoints-itens-do-carrinho-cart-items)
12. [Endpoints - Pedidos](#12-endpoints-pedidos-orders)
13. [Endpoints - Pagamentos](#13-endpoints-pagamentos-payments)
14. [Endpoints - Endereços](#14-endpoints-endereços-addresses)
15. [Endpoints - Telefones](#15-endpoints-telefones-phones)
16. [Glossário de Enums](#16-glossário-de-enums)
17. [Tratamento de Erros Globais](#17-tratamento-de-erros-globais)
18. [Changelog e Melhorias Futuras](#18-changelog-e-melho

---

## 1. Introdução

A API **POP** é a camada de serviço back-end para a plataforma de e-commerce de mesmo nome. Projetada com arquitetura
RESTful, ela fornece operações CRUD completas para gestão de usuários, catálogo de produtos (Funko Pops), carrinho de
compras, fluxo de checkout, processamento de pagamentos e gestão de endereços/telefones.

---

## 2. Autenticação e Segurança

A API utiliza **Spring Security** com **JWT (JSON Web Token)** para proteger os endpoints e garantir que apenas usuários autenticados possam acessar recursos protegidos.

### 2.1. Como funciona

1. O usuário se registra no sistema (`/api/auth/register`)
2. O usuário faz login com e-mail e senha (`/api/auth/login`)
3. O servidor valida as credenciais e retorna um token JWT
4. O token deve ser enviado em todas as requisições subsequentes no header `Authorization`
5. O servidor valida o token e libera ou bloqueia o acesso

### 2.2. Endpoints Públicos (Sem Autenticação)

Os seguintes endpoints são **publicamente acessíveis**:

- `POST /api/auth/register` - Registrar novo usuário
- `POST /api/auth/login` - Fazer login

### 2.3. Endpoints Protegidos

**Todos os outros endpoints** exigem autenticação via JWT, incluindo:

- `GET /api/users` - Listar usuários
- `GET /api/users/{id}` - Buscar usuário por ID
- `PUT /api/users/{id}` - Atualizar usuário
- `DELETE /api/users/{id}` - Deletar usuário
- `GET /products/*` - Operações com produtos
- `POST /products` - Criar produto (apenas ADMIN)
- `PUT /products/{id}` - Atualizar produto (apenas ADMIN)
- `DELETE /products/{id}` - Deletar produto (apenas ADMIN)
- `GET /cart/{userId}` - Visualizar carrinho
- `POST /cart/{userId}/add/{productId}` - Adicionar ao carrinho
- `PUT /cart/{userId}/item/{itemId}` - Atualizar carrinho
- `DELETE /cart/{userId}/item/{itemId}` - Remover do carrinho
- `POST /orders/{userId}/checkout` - Finalizar pedido
- `GET /orders/{userId}` - Listar pedidos
- `PUT /orders/{orderId}/status` - Atualizar status do pedido
- E todos os demais endpoints...

### 2.4. Roles e Permissões (Planejado para v1.1.0)

| Role    | Permissões                                                                 |
|:--------|:---------------------------------------------------------------------------|
| `ADMIN` | Acesso total a todos os endpoints, incluindo criação/edição/deleção de produtos |
| `USER`  | Acesso a endpoints de usuário, carrinho, pedidos e consulta de produtos    |

---

## 3. Fluxo de Autenticação

### 3.1. Registro de Usuário

**Passo 1:** O usuário envia uma requisição POST para `/api/auth/register` com nome, e-mail e senha.

**Passo 2:** O servidor valida os dados, criptografa a senha usando BCrypt e salva o usuário no banco.

**Passo 3:** O servidor retorna os dados do usuário criado (sem a senha).

### 3.2. Login

**Passo 1:** O usuário envia uma requisição POST para `/api/auth/login` com e-mail e senha.

**Passo 2:** O servidor valida as credenciais usando o `AuthenticationManager`.

**Passo 3:** Se as credenciais forem válidas, o servidor gera um token JWT com validade de 24 horas.

**Passo 4:** O servidor retorna o token e o e-mail do usuário.

### 3.3. Acessando Endpoints Protegidos

**Passo 1:** O usuário inclui o token JWT no header `Authorization` de todas as requisições.

**Passo 2:** O `JwtAuthenticationFilter` intercepta a requisição, extrai o token e valida sua autenticidade.

**Passo 3:** Se o token for válido, o Spring Security autentica o usuário e permite o acesso ao recurso.

**Passo 4:** Se o token for inválido ou expirado, o servidor retorna erro `401 Unauthorized`.

### 3.4. Exemplo Prático

```bash
# 1. Registrar usuário
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"João Silva","email":"joao@email.com","password":"senha123"}'

# 2. Fazer login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"joao@email.com","password":"senha123"}'

# 3. Usar o token para acessar endpoints protegidos
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 4. Formato de Dados e Headers

### 4.1. Headers Obrigatórios

| Header | Valor | Obrigatório |
|:-------|:------|:-----------|
| `Content-Type` | `application/json` | Sim (para POST/PUT/PATCH) |
| `Accept` | `application/json` | Sim |
| `Authorization` | `Bearer {token}` | Sim (para endpoints protegidos) |

### 4.2. Exemplo de Header Authorization

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2FvQGVtYWlsLmNvbSIsImlhdCI6MTcxNjkwNjIzMCwiZXhwIjoxNzE2OTkyNjMwfQ.abc123...
```

### 4.3. Regras Gerais

- Datas seguem o padrão ISO-8601 (`yyyy-MM-ddTHH:mm:ss`).
- Moeda representada em ponto flutuante (`Double`) com duas casas decimais.
- IDs do tipo `UUID` para usuários e `Long` para as demais entidades.
- Senhas são armazenadas com hash BCrypt (não são retornadas em nenhuma resposta).

---

## 5. Códigos de Status HTTP

A API utiliza os códigos padrão do protocolo HTTP para indicar o sucesso ou falha da requisição.

| Código | Descrição |
|:-------|:----------|
| **200 OK** | Requisição bem-sucedida (GET, PUT, PATCH). |
| **201 Created** | Recurso criado com sucesso (POST). |
| **204 No Content** | Recurso deletado com sucesso (DELETE). |
| **400 Bad Request** | Requisição inválida (parâmetros ausentes, formato incorreto ou violação de validação). |
| **401 Unauthorized** | Token JWT ausente, inválido ou expirado. |
| **403 Forbidden** | Usuário autenticado mas sem permissão para acessar o recurso. |
| **404 Not Found** | Recurso não encontrado (ID inválido ou URL incorreta). |
| **409 Conflict** | Conflito com o estado atual do recurso (ex: e-mail já cadastrado). |
| **422 Unprocessable Entity** | Entidade não processável (ex: estoque insuficiente). |
| **500 Internal Server Error** | Erro inesperado no servidor. |

---

## 6. Modelos de Dados (Schemas)

### 6.1. Usuário (`User`)

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "João Silva",
  "email": "joao@email.com",
  "accountBalance": 150.75,
  "createdAt": "2026-06-23T10:30:00"
}
```

### 6.2. Produto (`Product`)

```json
{
  "id": 1,
  "name": "Funko Pop! Harry Potter",
  "description": "Figure do Harry Potter com a varinha e a cicatriz.",
  "price": 89.90,
  "stock": 50,
  "imageUrl": "https://cdn.pop.com/harry_potter.jpg",
  "franchise": "Harry Potter",
  "rarity": "COMUM"
}
```

### 6.3. Carrinho (`Cart`)

```json
{
  "id": 5,
  "user": {
    "id": "550e8400-...",
    "name": "João Silva"
  },
  "createdAt": "2026-06-23T10:30:00"
}
```

### 6.4. Item do Carrinho (`CartItem`)

```json
{
  "id": 10,
  "cart": {
    "id": 5
  },
  "product": {
    "id": 1,
    "name": "Funko Pop! Harry Potter"
  },
  "quantity": 2
}
```

### 6.5. Pedido (`Order`)

```json
{
  "id": 100,
  "user": {
    "id": "550e8400-...",
    "name": "João Silva"
  },
  "total": 179.80,
  "status": "PENDING",
  "createdAt": "2026-06-23T10:35:00"
}
```

### 6.6. Pagamento (`Payment`)

```json
{
  "id": 200,
  "order": {
    "id": 100
  },
  "method": "CREDIT_CARD",
  "status": "APPROVED",
  "paymentDate": "2026-06-23T10:40:00"
}
```

### 6.7. Endereço (`Address`)

```json
{
  "id": 300,
  "user": {
    "id": "550e8400-..."
  },
  "street": "Rua das Flores",
  "number": "123",
  "complement": "Apto 45",
  "city": "São Paulo",
  "state": "SP",
  "zipCode": "01234-567"
}
```

### 6.8. Telefone (`Phone`)

```json
{
  "id": 400,
  "user": {
    "id": "550e8400-..."
  },
  "phoneNumber": "11999999999",
  "type": "MOBILE"
}
```

---

## 7. Endpoints - Autenticação (`/api/auth`)

Endpoints para registro e autenticação de usuários.

### 7.1. Registrar Usuário

> **POST** `/api/auth/register`

**Corpo da Requisição:**

```json
{
  "name": "Maria Oliveira",
  "email": "maria@email.com",
  "password": "senhaForte123"
}
```

| Campo | Tipo | Obrigatório | Validação |
|:------|:-----|:-----------|:----------|
| `name` | String | Sim | Mínimo 3 caracteres |
| `email` | String | Sim | Formato de e-mail válido e único |
| `password` | String | Sim | Mínimo 6 caracteres |

**Resposta (201 Created):**

```json
{
  "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
  "name": "Maria Oliveira",
  "email": "maria@email.com",
  "message": "Usuário registrado com sucesso!"
}
```

**Possíveis Erros:**

| Status | Descrição |
|:-------|:----------|
| `400 Bad Request` | Campos inválidos ou ausentes |
| `409 Conflict` | E-mail já está em uso |

---

### 7.2. Login

> **POST** `/api/auth/login`

**Corpo da Requisição:**

```json
{
  "email": "maria@email.com",
  "password": "senhaForte123"
}
```

| Campo | Tipo | Obrigatório |
|:------|:-----|:-----------|
| `email` | String | Sim |
| `password` | String | Sim |

**Resposta (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJtYXJpYUBlbWFpbC5jb20iLCJpYXQiOjE3MTY5MDYyMzAsImV4cCI6MTcxNjk5MjYzMH0.abc123...",
  "email": "maria@email.com"
}
```

**Possíveis Erros:**

| Status | Descrição |
|:-------|:----------|
| `401 Unauthorized` | Credenciais inválidas |

### 7.3. Regras de Segurança do Token

- **Algoritmo:** HS256 (HMAC SHA-256)
- **Validade:** 24 horas (86.400.000 milissegundos)
- **Chave Secreta:** Configurada via variável de ambiente `JWT_SECRET`
- **Claims:**
    - `sub` (subject): E-mail do usuário
    - `iat` (issued at): Data de emissão
    - `exp` (expiration): Data de expiração

---

## 8. Endpoints - Usuários (`/api/users`)

Gerencia o cadastro, consulta, atualização e remoção de usuários.

> ⚠️ **Aviso:** Todos os endpoints abaixo exigem autenticação via JWT.

### 8.1. Criar Usuário

> **POST** `/api/users`

**Corpo da Requisição:**

```json
{
  "name": "Maria Oliveira",
  "email": "maria@email.com",
  "password": "senhaForte123"
}
```

**Resposta (201 Created):**

```json
{
  "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
  "name": "Maria Oliveira",
  "email": "maria@email.com",
  "accountBalance": 0.0,
  "createdAt": "2026-06-23T11:00:00"
}
```

### 8.2. Listar Todos os Usuários

> **GET** `/api/users`

**Resposta (200 OK):** Array com todos os usuários.

### 8.3. Buscar Usuário por ID

> **GET** `/api/users/{id}`

**Parâmetros:** `id` (UUID) - Obrigatório.

**Resposta (200 OK):** Objeto `User`.

**Erro:** `404 Not Found` se o ID não existir.

### 8.4. Buscar Usuário por E-mail

> **GET** `/api/users/email/{email}`

**Resposta (200 OK):** Objeto `User`.

### 8.5. Atualizar Usuário

> **PUT** `/api/users/{id}`

**Corpo:** Campos a serem atualizados (nome, senha, etc.).

**Resposta (200 OK):** Usuário atualizado.

### 8.6. Deletar Usuário

> **DELETE** `/api/users/{id}`

**Resposta:** `204 No Content`.

---

## 9. Endpoints - Produtos (`/products`)

Gerencia o catálogo de produtos (Funko Pops).

> ⚠️ **Aviso:** Todos os endpoints abaixo exigem autenticação via JWT.

### 9.1. Criar Produto

> **POST** `/products`

**Corpo:**

```json
{
  "name": "Funko Pop! Darth Vader",
  "description": "Darth Vader com sabre de luz vermelho.",
  "price": 99.99,
  "stock": 30,
  "imageUrl": "https://cdn.pop.com/vader.jpg",
  "franchise": "Star Wars",
  "rarity": "RARO"
}
```

**Resposta (201 Created):** Produto criado.

### 9.2. Listar Produtos (com Filtros)

> **GET** `/products`

**Query Params Opcionais:**

- `page` (int): Número da página.
- `size` (int): Itens por página.

**Resposta (200 OK):** Lista paginada de produtos.

### 9.3. Buscar Produto por ID

> **GET** `/products/{id}`

**Erro:** `404 Not Found`.

### 9.4. Atualizar Produto

> **PUT** `/products/{id}`

**Corpo:** Objeto `Product` completo.

### 9.5. Deletar Produto

> **DELETE** `/products/{id}`

### 9.6. Buscar por Franquia

> **GET** `/products/franchise/{franchise}`

### 9.7. Buscar por Raridade

> **GET** `/products/rarity/{rarity}`

### 9.8. Buscar por Faixa de Preço

> **GET** `/products/price-range?min=50.00&max=150.00`

### 9.9. Produtos com Estoque Baixo

> **GET** `/products/low-stock?threshold=10`

### 9.10. Buscar Produtos por Nome (Search)

> **GET** `/products/search?name=Harry`

### 9.11. Atualizar Estoque (PATCH)

> **PATCH** `/products/{id}/stock?quantity=15`

---

## 10. Endpoints - Carrinho de Compras (`/cart`)

Gerencia o carrinho ativo de um usuário.

> ⚠️ **Aviso:** Todos os endpoints abaixo exigem autenticação via JWT.

### 10.1. Adicionar Item ao Carrinho

> **POST** `/cart/{userId}/add/{productId}?quantity=2`

### 10.2. Visualizar Carrinho do Usuário

> **GET** `/cart/{userId}`

### 10.3. Atualizar Quantidade de um Item

> **PUT** `/cart/{userId}/item/{itemId}?quantity=5`

### 10.4. Remover Item Específico

> **DELETE** `/cart/{userId}/item/{itemId}`

### 10.5. Limpar Todo o Carrinho

> **DELETE** `/cart/{userId}/clear`

---

## 11. Endpoints - Itens do Carrinho (`/cart-items`)

Endpoints alternativos para manipulação direta dos itens.

| Método | Endpoint | Descrição |
|:-------|:---------|:----------|
| `POST` | `/cart-items?cartId={cartId}&productId={productId}&quantity={q}` | Cria item diretamente |
| `GET` | `/cart-items/cart/{cartId}` | Lista itens de um carrinho específico |
| `PUT` | `/cart-items/{cartItemId}?quantity={q}` | Atualiza item |
| `DELETE` | `/cart-items/{cartItemId}` | Remove item |
| `DELETE` | `/cart-items/cart/{cartId}/clear` | Limpa carrinho por ID |

---

## 12. Endpoints - Pedidos (`/orders`)

Gerencia o ciclo de vida dos pedidos realizados a partir do carrinho.

> ⚠️ **Aviso:** Todos os endpoints abaixo exigem autenticação via JWT.

### 12.1. Realizar Checkout (Criar Pedido)

> **POST** `/orders/{userId}/checkout`

**Resposta (201 Created):**

```json
{
  "id": 101,
  "user": {
    "id": "550e...",
    "name": "João Silva"
  },
  "total": 299.97,
  "status": "PENDING",
  "createdAt": "2026-06-23T12:00:00"
}
```

### 12.2. Listar Pedidos do Usuário

> **GET** `/orders/{userId}`

### 12.3. Buscar Detalhes do Pedido

> **GET** `/orders/{orderId}/details`

### 12.4. Listar Itens de um Pedido

> **GET** `/orders/{orderId}/items`

### 12.5. Atualizar Status do Pedido

> **PUT** `/orders/{orderId}/status?status=SHIPPED`

### 12.6. Cancelar Pedido

> **DELETE** `/orders/{orderId}/cancel`

---

## 13. Endpoints - Pagamentos (`/payments`)

Gerencia as transações financeiras associadas aos pedidos.

> ⚠️ **Aviso:** Todos os endpoints abaixo exigem autenticação via JWT.

### 13.1. Criar Pagamento para Pedido

> **POST** `/payments/order/{orderId}?method=CREDIT_CARD`

### 13.2. Processar Pagamento (Simulação)

> **POST** `/payments/{paymentId}/process`

### 13.3. Aprovar Pagamento

> **POST** `/payments/{paymentId}/approve`

### 13.4. Recusar Pagamento

> **POST** `/payments/{paymentId}/decline`

### 13.5. Estornar Pagamento

> **POST** `/payments/{paymentId}/refund`

### 13.6. Buscar Pagamento por Pedido

> **GET** `/payments/order/{orderId}`

### 13.7. Buscar Pagamentos por Status

> **GET** `/payments/status/{status}`

### 13.8. Atualizar Status do Pagamento (Admin)

> **PUT** `/payments/{paymentId}/status?status=PROCESSING`

---

## 14. Endpoints - Endereços (`/addresses`)

Gerencia os endereços de entrega dos usuários.

> ⚠️ **Aviso:** Todos os endpoints abaixo exigem autenticação via JWT.

### 14.1. Adicionar Endereço

> **POST** `/addresses/user/{userId}`

### 14.2. Listar Endereços do Usuário

> **GET** `/addresses/user/{userId}`

### 14.3. Deletar Endereço

> **DELETE** `/addresses/{addressId}`

---

## 15. Endpoints - Telefones (`/phones`)

Gerencia os números de contato dos usuários.

> ⚠️ **Aviso:** Todos os endpoints abaixo exigem autenticação via JWT.

### 15.1. Adicionar Telefone

> **POST** `/phones/user/{userId}`

### 15.2. Listar Telefones do Usuário

> **GET** `/phones/user/{userId}`

### 15.3. Deletar Telefone

> **DELETE** `/phones/{phoneId}`

---

## 16. Glossário de Enums

### `ProductRarity`

- `COMUM`
- `INCOMUM`
- `RARO`
- `EPICO`
- `LENDARIO`

### `OrderStatus`

- `PENDING` - Aguardando pagamento/processamento
- `PROCESSING` - Em preparação
- `SHIPPED` - Enviado
- `DELIVERED` - Entregue
- `CANCELLED` - Cancelado

### `PaymentMethod`

- `CREDIT_CARD`
- `DEBIT_CARD`
- `PIX`
- `BOLETO`

### `PaymentStatus`

- `PENDING`
- `PROCESSING`
- `APPROVED`
- `DECLINED`
- `REFUNDED`
- `CANCELLED`

### `PhoneType`

- `HOME`
- `WORK`
- `MOBILE`

---

## 17. Tratamento de Erros Globais

A API utiliza um **Exception Handler Global** (`@ControllerAdvice`) que padroniza as respostas de erro no seguinte
formato:

```json
{
  "timestamp": "2026-06-23T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Produto com ID 999 não encontrado.",
  "path": "/products/999"
}
```

**Principais Exceções Mapeadas:**

| Exceção | Status | Descrição |
|:--------|:-------|:----------|
| `ResourceNotFoundException` | 404 | Recurso não encontrado |
| `InvalidRequestException` | 400 | Requisição inválida |
| `BusinessConflictException` | 409 | Conflito de negócio |
| `InsufficientStockException` | 422 | Estoque insuficiente |
| `AuthenticationException` | 401 | Token inválido ou não autorizado |

---

## 18. Changelog e Melhorias Futuras

### Versão 1.0.0 (Atual)

- ✅ Implementação completa de CRUD para todas as entidades.
- ✅ Fluxo de carrinho e checkout funcional.
- ✅ Simulação de pagamentos.
- ✅ **Spring Security com JWT implementado.**
- ✅ **BCrypt para criptografia de senhas.**

### Próximas Versões (Roadmap)

- **v1.1.0:**
    - Implementação de roles (`ADMIN`, `USER`)
    - Controle de acesso baseado em roles
    - Refresh Token

- **v1.2.0:**
    - Integração com gateway de pagamento real (Stripe/PagSeguro)
    - Webhooks para notificações de pagamento

- **v1.3.0:**
    - Sistema de cupons de desconto
    - Promoções e ofertas especiais

- **v1.4.0:**
    - Histórico de navegação
    - Sistema de recomendações de produtos

- **v1.5.0:**
    - Documentação interativa via Swagger UI (`/swagger-ui.html`)
    - OpenAPI 3.0

---

> **Suporte:** Para dúvidas ou reportar bugs, abra uma *issue* no repositório oficial ou entre em contato com a equipe
> de desenvolvimento.