# 🐵Documentação Oficial da API - POP E-Commerce

**Versão:** 1.0.0  
**Status:** Estável (Production Ready)  
**Base URL:** `http://localhost:8080`  
**Tecnologias:** Java 17, Spring Boot 3.x, Spring Data JPA, PostgreSQL/MySQL, Maven.

---

## Índice

1. [Introdução](#1-introdução)
2. [Autenticação e Segurança](#2-autenticação-e-segurança)
3. [Formato de Dados e Headers](#3-formato-de-dados-e-headers)
4. [Códigos de Status HTTP](#4-códigos-de-status-http)
5. [Modelos de Dados (Schemas)](#5-modelos-de-dados-schemas)
6. [Endpoints - Usuários](#6-endpoints-usuários-apiusers)
7. [Endpoints - Produtos](#7-endpoints-produtos-products)
8. [Endpoints - Carrinho de Compras](#8-endpoints-carrinho-de-compras-cart)
9. [Endpoints - Itens do Carrinho](#9-endpoints-itens-do-carrinho-cart-items)
10. [Endpoints - Pedidos](#10-endpoints-pedidos-orders)
11. [Endpoints - Pagamentos](#11-endpoints-pagamentos-payments)
12. [Endpoints - Endereços](#12-endpoints-endereços-addresses)
13. [Endpoints - Telefones](#13-endpoints-telefones-phones)
14. [Glossário de Enums](#14-glossário-de-enums)
15. [Tratamento de Erros Globais](#15-tratamento-de-erros-globais)
16. [Changelog e Melhorias Futuras](#16-changelog-e-melhorias-futuras)

---

## 1. Introdução

A API **POP** é a camada de serviço back-end para a plataforma de e-commerce de mesmo nome. Projetada com arquitetura
RESTful, ela fornece operações CRUD completas para gestão de usuários, catálogo de produtos (Funko Pops), carrinho de
compras, fluxo de checkout, processamento de pagamentos e gestão de endereços/telefones.

---

## 2. Autenticação e Segurança

> ⚠️ **Aviso de Segurança:** Atualmente, a API **não implementa** nenhum mecanismo de autenticação (como JWT ou OAuth2).
> Todos os endpoints estão publicamente acessíveis.

**Recomendação para Produção:**  
Implementar Spring Security com JWT (JSON Web Token) para proteger rotas administrativas e dados sensíveis dos usuários.
Incluir suporte a roles (`ADMIN`, `USER`) para controle de acesso granular.

---

## 3. Formato de Dados e Headers

| Header         | Valor              | Obrigatório               |
|:---------------|:-------------------|:--------------------------|
| `Content-Type` | `application/json` | Sim (para POST/PUT/PATCH) |
| `Accept`       | `application/json` | Sim                       |

**Regras Gerais:**

- Datas seguem o padrão ISO-8601 (`yyyy-MM-ddTHH:mm:ss`).
- Moeda representada em ponto flutuante (`Double`) com duas casas decimais.
- IDs do tipo `UUID` para usuários e `Long` para as demais entidades.

---

## 4. Códigos de Status HTTP

A API utiliza os códigos padrão do protocolo HTTP para indicar o sucesso ou falha da requisição.

| Código                        | Descrição                                                                              |
|:------------------------------|:---------------------------------------------------------------------------------------|
| **200 OK**                    | Requisição bem-sucedida (GET, PUT, PATCH).                                             |
| **201 Created**               | Recurso criado com sucesso (POST).                                                     |
| **204 No Content**            | Recurso deletado com sucesso (DELETE).                                                 |
| **400 Bad Request**           | Requisição inválida (parâmetros ausentes, formato incorreto ou violação de validação). |
| **404 Not Found**             | Recurso não encontrado (ID inválido ou URL incorreta).                                 |
| **409 Conflict**              | Conflito com o estado atual do recurso (ex: e-mail já cadastrado).                     |
| **422 Unprocessable Entity**  | Entidade não processável (ex: estoque insuficiente).                                   |
| **500 Internal Server Error** | Erro inesperado no servidor.                                                           |

---

## 5. Modelos de Dados (Schemas)

### 5.1. Usuário (`User`)

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "João Silva",
  "email": "joao@email.com",
  "password": "********",
  // (não retornado em GETs, apenas na criação)
  "accountBalance": 150.75,
  "createdAt": "2026-06-23T10:30:00"
}
```

### 5.2. Produto (`Product`)

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
  // Valores: COMUM, INCOMUM, RARO, EPICO, LENDARIO
}
```

### 5.3. Carrinho (`Cart`)

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

### 5.4. Item do Carrinho (`CartItem`)

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

### 5.5. Pedido (`Order`)

```json
{
  "id": 100,
  "user": {
    "id": "550e8400-...",
    "name": "João Silva"
  },
  "total": 179.80,
  "status": "PENDING",
  // Valores: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
  "createdAt": "2026-06-23T10:35:00"
}
```

### 5.6. Pagamento (`Payment`)

```json
{
  "id": 200,
  "order": {
    "id": 100
  },
  "method": "CREDIT_CARD",
  // Valores: CREDIT_CARD, DEBIT_CARD, PIX, BOLETO
  "status": "APPROVED",
  // Valores: PENDING, PROCESSING, APPROVED, DECLINED, REFUNDED, CANCELLED
  "paymentDate": "2026-06-23T10:40:00"
}
```

### 5.7. Endereço (`Address`)

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

### 5.8. Telefone (`Phone`)

```json
{
  "id": 400,
  "user": {
    "id": "550e8400-..."
  },
  "phoneNumber": "11999999999",
  "type": "MOBILE"
  // Valores: HOME, WORK, MOBILE
}
```

---

## 6. Endpoints - Usuários (`/api/users`)

Gerencia o cadastro, consulta, atualização e remoção de usuários.

### 6.1. Criar Usuário

> **POST** `/api/users`

**Corpo da Requisição:**

```json
{
  "name": "Maria Oliveira",
  // Obrigatório, mínimo 3 caracteres
  "email": "maria@email.com",
  // Obrigatório, formato e-mail válido e único
  "password": "senhaForte123"
  // Obrigatório, mínimo 6 caracteres
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

**Possíveis Erros:**

- `400 Bad Request`: Campos inválidos ou ausentes.
- `409 Conflict`: E-mail já está em uso.

---

### 6.2. Listar Todos os Usuários

> **GET** `/api/users`

**Resposta (200 OK):** Array com todos os usuários.

---

### 6.3. Buscar Usuário por ID

> **GET** `/api/users/{id}`

**Parâmetros:** `id` (UUID) - Obrigatório.

**Resposta (200 OK):** Objeto `User`.

**Erro:** `404 Not Found` se o ID não existir.

---

### 6.4. Buscar Usuário por E-mail

> **GET** `/api/users/email/{email}`

**Resposta (200 OK):** Objeto `User`.

---

### 6.5. Atualizar Usuário

> **PUT** `/api/users/{id}`

**Corpo:** Campos a serem atualizados (nome, senha, etc.).

**Resposta (200 OK):** Usuário atualizado.

---

### 6.6. Deletar Usuário

> **DELETE** `/api/users/{id}`

**Resposta:** `204 No Content`.

---

## 7. Endpoints - Produtos (`/products`)

Gerencia o catálogo de produtos (Funko Pops).

### 7.1. Criar Produto

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
  // Enum: COMUM, INCOMUM, RARO, EPICO, LENDARIO
}
```

**Resposta (201 Created):** Produto criado.

---

### 7.2. Listar Produtos (com Filtros)

> **GET** `/products`

**Query Params Opcionais:**

- `page` (int): Número da página.
- `size` (int): Itens por página.

**Resposta (200 OK):** Lista paginada de produtos.

---

### 7.3. Buscar Produto por ID

> **GET** `/products/{id}`

**Erro:** `404 Not Found`.

---

### 7.4. Atualizar Produto

> **PUT** `/products/{id}`

**Corpo:** Objeto `Product` completo.

---

### 7.5. Deletar Produto

> **DELETE** `/products/{id}`

---

### 7.6. Buscar por Franquia

> **GET** `/products/franchise/{franchise}`  
*Exemplo: `/products/franchise/Marvel`*

---

### 7.7. Buscar por Raridade

> **GET** `/products/rarity/{rarity}`  
*Exemplo: `/products/rarity/EPICO`*

---

### 7.8. Buscar por Faixa de Preço

> **GET** `/products/price-range?min=50.00&max=150.00`

| Parâmetro | Tipo   | Obrigatório |
|:----------|:-------|:------------|
| `min`     | Double | Sim         |
| `max`     | Double | Sim         |

---

### 7.9. Produtos com Estoque Baixo

> **GET** `/products/low-stock?threshold=10`

Lista produtos cujo estoque é menor que o valor informado no `threshold`.

---

### 7.10. Buscar Produtos por Nome (Search)

> **GET** `/products/search?name=Harry`

Busca parcial por nome (case-insensitive).

---

### 7.11. Atualizar Estoque (PATCH)

> **PATCH** `/products/{id}/stock?quantity=15`

Atualiza a quantidade exata de estoque do produto.

**Resposta (200 OK):** Produto com estoque atualizado.

**Erro:** `400 Bad Request` se `quantity` for negativo.

---

## 8. Endpoints - Carrinho de Compras (`/cart`)

Gerencia o carrinho ativo de um usuário.

### 8.1. Adicionar Item ao Carrinho

> **POST** `/cart/{userId}/add/{productId}?quantity=2`

**Regras:**

- Se o produto já estiver no carrinho, a quantidade é somada.
- Valida se o estoque é suficiente.

**Resposta (201 Created):** `CartItem` criado/atualizado.

---

### 8.2. Visualizar Carrinho do Usuário

> **GET** `/cart/{userId}`

**Resposta:** Lista de `CartItem` com detalhes do produto.

---

### 8.3. Atualizar Quantidade de um Item

> **PUT** `/cart/{userId}/item/{itemId}?quantity=5`

Altera a quantidade de um item específico.

---

### 8.4. Remover Item Específico

> **DELETE** `/cart/{userId}/item/{itemId}`

**Resposta:** `204 No Content`.

---

### 8.5. Limpar Todo o Carrinho

> **DELETE** `/cart/{userId}/clear`

Remove todos os itens do carrinho do usuário.

---

## 9. Endpoints - Itens do Carrinho (`/cart-items`)

Endpoints alternativos para manipulação direta dos itens (geralmente usados internamente ou para operações
administrativas).

| Método   | Endpoint                                                         | Descrição                             |
|:---------|:-----------------------------------------------------------------|:--------------------------------------|
| `POST`   | `/cart-items?cartId={cartId}&productId={productId}&quantity={q}` | Cria item diretamente                 |
| `GET`    | `/cart-items/cart/{cartId}`                                      | Lista itens de um carrinho específico |
| `PUT`    | `/cart-items/{cartItemId}?quantity={q}`                          | Atualiza item                         |
| `DELETE` | `/cart-items/{cartItemId}`                                       | Remove item                           |
| `DELETE` | `/cart-items/cart/{cartId}/clear`                                | Limpa carrinho por ID                 |

---

## 10. Endpoints - Pedidos (`/orders`)

Gerencia o ciclo de vida dos pedidos realizados a partir do carrinho.

### 10.1. Realizar Checkout (Criar Pedido)

> **POST** `/orders/{userId}/checkout`

**Regras de Negócio:**

1. Verifica se o carrinho não está vazio.
2. Verifica estoque de todos os itens.
3. Calcula o valor total.
4. Cria o pedido com status `PENDING`.
5. Limpa o carrinho do usuário.

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

**Erro:** `422 Unprocessable Entity` se algum produto estiver sem estoque.

---

### 10.2. Listar Pedidos do Usuário

> **GET** `/orders/{userId}`

Retorna todos os pedidos do usuário ordenados por data decrescente.

---

### 10.3. Buscar Detalhes do Pedido

> **GET** `/orders/{orderId}/details`

Retorna o pedido completo com dados do usuário e total.

---

### 10.4. Listar Itens de um Pedido

> **GET** `/orders/{orderId}/items`

Retorna a lista de produtos comprados naquele pedido.

---

### 10.5. Atualizar Status do Pedido

> **PUT** `/orders/{orderId}/status?status=SHIPPED`

| Status Permitidos | Descrição                               |
|:------------------|:----------------------------------------|
| `PROCESSING`      | Em separação                            |
| `SHIPPED`         | Enviado ao transporte                   |
| `DELIVERED`       | Entregue ao cliente                     |
| `CANCELLED`       | Cancelado (apenas se estiver `PENDING`) |

**Resposta:** Pedido atualizado.

---

### 10.6. Cancelar Pedido

> **DELETE** `/orders/{orderId}/cancel`

Muda o status para `CANCELLED` e restaura o estoque dos produtos (se aplicável).

---

## 11. Endpoints - Pagamentos (`/payments`)

Gerencia as transações financeiras associadas aos pedidos.

### 11.1. Criar Pagamento para Pedido

> **POST** `/payments/order/{orderId}?method=CREDIT_CARD`

| Método        | Descrição             |
|:--------------|:----------------------|
| `CREDIT_CARD` | Cartão de Crédito     |
| `DEBIT_CARD`  | Cartão de Débito      |
| `PIX`         | Pagamento instantâneo |
| `BOLETO`      | Boleto bancário       |

**Resposta (201 Created):** Objeto `Payment` com status `PENDING`.

---

### 11.2. Processar Pagamento (Simulação)

> **POST** `/payments/{paymentId}/process`

Simula o processamento da transação. (Em ambiente real, integraria com gateway externo).

---

### 11.3. Aprovar Pagamento

> **POST** `/payments/{paymentId}/approve`

Altera status para `APPROVED`. Atualiza o pedido para `PROCESSING` (se ainda estiver pendente).

---

### 11.4. Recusar Pagamento

> **POST** `/payments/{paymentId}/decline`

Altera status para `DECLINED`. Mantém pedido como `PENDING` para nova tentativa.

---

### 11.5. Estornar Pagamento

> **POST** `/payments/{paymentId}/refund`

Altera status para `REFUNDED` e cancela o pedido associado.

---

### 11.6. Buscar Pagamento por Pedido

> **GET** `/payments/order/{orderId}`

---

### 11.7. Buscar Pagamentos por Status

> **GET** `/payments/status/{status}`  
*Exemplo: `/payments/status/APPROVED`*

---

### 11.8. Atualizar Status do Pagamento (Admin)

> **PUT** `/payments/{paymentId}/status?status=PROCESSING`

---

## 12. Endpoints - Endereços (`/addresses`)

Gerencia os endereços de entrega dos usuários.

### 12.1. Adicionar Endereço

> **POST** `/addresses/user/{userId}`

**Corpo:**

```json
{
  "street": "Av. Paulista",
  "number": "1000",
  "complement": "Sala 202",
  "city": "São Paulo",
  "state": "SP",
  "zipCode": "01310-100"
}
```

**Resposta (201 Created):** Endereço salvo.

---

### 12.2. Listar Endereços do Usuário

> **GET** `/addresses/user/{userId}`

---

### 12.3. Deletar Endereço

> **DELETE** `/addresses/{addressId}`

---

## 13. Endpoints - Telefones (`/phones`)

Gerencia os números de contato dos usuários.

### 13.1. Adicionar Telefone

> **POST** `/phones/user/{userId}`

**Corpo:**

```json
{
  "phoneNumber": "11988887777",
  "type": "MOBILE"
  // ou HOME, WORK
}
```

**Resposta (201 Created):** Telefone salvo.

---

### 13.2. Listar Telefones do Usuário

> **GET** `/phones/user/{userId}`

---

### 13.3. Deletar Telefone

> **DELETE** `/phones/{phoneId}`

---

## 14. Glossário de Enums

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

## 15. Tratamento de Erros Globais

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

- `ResourceNotFoundException` → 404
- `InvalidRequestException` → 400
- `BusinessConflictException` → 409
- `InsufficientStockException` → 422

---

## 16. Changelog e Melhorias Futuras

### Versão 1.0.0 (Atual)

- Implementação completa de CRUD para todas as entidades.
- Fluxo de carrinho e checkout funcional.
- Simulação de pagamentos.

### Próximas Versões (Roadmap)

- **v1.1.0:** Implementação de Spring Security com JWT.
- **v1.2.0:** Integração com gateway de pagamento real (Stripe/PagSeguro).
- **v1.3.0:** Sistema de cupons de desconto e promoções.
- **v1.4.0:** Histórico de navegação e recomendações de produtos.
- **v1.5.0:** Documentação interativa via Swagger UI (`/swagger-ui.html`).

---

> **Suporte:** Para dúvidas ou reportar bugs, abra uma *issue* no repositório oficial ou entre em contato com a equipe
> de desenvolvimento.

