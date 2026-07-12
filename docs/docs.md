# POP E-Commerce API — Documentação

**Base URL:** `http://localhost:8080`
**Formato:** JSON
**Auth:** JWT no header `Authorization: Bearer <token>`

## Sumário

1. [Autenticação](#1-autenticação)
2. [Usuários](#2-usuários)
3. [Produtos](#3-produtos)
4. [Carrinho](#4-carrinho)
5. [Itens do carrinho](#5-itens-do-carrinho)
6. [Pedidos](#6-pedidos)
7. [Pagamentos](#7-pagamentos)
8. [Endereços](#8-endereços)
9. [Telefones](#9-telefones)
10. [Acessibilidade](#10-acessibilidade)
11. [Enums](#11-enums)
12. [Códigos HTTP e erros](#12-códigos-http-e-erros)

---

## Convenções

- IDs de usuário são **UUID**. Demais IDs são **Long**.
- Timestamps em **ISO-8601** (`2026-07-12T14:30:00`).
- Dinheiro em `BigDecimal` (duas casas).
- Endpoints com `{userId}` no path validam ownership contra o token — dono errado retorna **403**.
- `POST /api/auth/**` e `GET /api/accessibility/languages` são públicos. Todo o resto exige `Authorization: Bearer <token>`.

---

## 1. Autenticação

### 1.1 Registrar

`POST /api/auth/register`

```json
{
  "name": "Maria Oliveira",
  "email": "maria@email.com",
  "password": "senhaForte123"
}
```

**201 Created**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "userId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
  "email": "maria@email.com",
  "name": "Maria Oliveira"
}
```

**Validações:** `name` 2–100 chars, `email` formato válido, `password` mínimo 6 chars.

### 1.2 Login

`POST /api/auth/login`

```json
{
  "email": "maria@email.com",
  "password": "senhaForte123"
}
```

**200 OK** — mesmo formato de resposta que `/register`.

**Erros:** 401 se credenciais inválidas.

**Guarde o `userId` da resposta** — ele vai nos paths de `/cart`, `/orders`, `/api/accessibility/users/...`.

---

## 2. Usuários

`/api/users` — todos os endpoints protegidos.

### 2.1 Listar usuários

`GET /api/users`

**200 OK** — array de `UserResponse`:

```json
[
  {
    "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
    "name": "Maria Oliveira",
    "email": "maria@email.com",
    "createdAt": "2026-07-12T10:30:00",
    "accountBalance": 0.00
  }
]
```

### 2.2 Buscar por ID

`GET /api/users/{id}` — `id` = UUID
**404** se não existir.

### 2.3 Buscar por email

`GET /api/users/email/{email}`

### 2.4 Atualizar usuário

`PUT /api/users/{id}`

```json
{
  "name": "Maria O.",
  "email": "maria.o@email.com",
  "password": "novaSenha123"
}
```

`password` é opcional — se omitido, senha não muda. Se enviado, mínimo 6 chars.

### 2.5 Deletar usuário

`DELETE /api/users/{id}` → **204 No Content**

---

## 3. Produtos

`/products` — todos protegidos.

### 3.1 Criar produto

`POST /products`

```json
{
  "name": "Funko Pop! Darth Vader",
  "description": "Darth Vader com sabre de luz vermelho.",
  "price": 99.90,
  "stock": 30,
  "imageUrl": "https://cdn.pop.com/vader.jpg",
  "franchise": "Star Wars",
  "rarity": "RARO",
  "accessibleDescription": "Figura preta de 9cm com sabre vermelho iluminado, base cinza.",
  "imageAltText": "Funko Pop do Darth Vader empunhando sabre vermelho",
  "colorPalette": "#000000,#8B0000,#4A4A4A",
  "highContrast": true
}
```

**Retorno:** o produto criado (com `id`).

### 3.2 Listar produtos

`GET /products`

Retorna lista filtrada pelas configurações de acessibilidade do usuário logado (esconde produtos que não passam nos critérios de daltonismo/contraste do perfil).

### 3.3 Buscar por ID

`GET /products/{id}` — `id` = Long

### 3.4 Atualizar produto

`PUT /products/{id}` — mesmo corpo do POST.

### 3.5 Deletar produto

`DELETE /products/{id}`

### 3.6 Buscar por franquia

`GET /products/franchise/{franchise}`

Exemplo: `GET /products/franchise/Star%20Wars`

### 3.7 Buscar por raridade

`GET /products/rarity/{rarity}`

Exemplo: `GET /products/rarity/RARO`

### 3.8 Buscar por faixa de preço

`GET /products/price-range?min=50.00&max=150.00`

### 3.9 Produtos com estoque baixo

`GET /products/low-stock?threshold=10`

`threshold` opcional (default: 10).

### 3.10 Buscar por nome

`GET /products/search?name=Vader`

### 3.11 Ajustar estoque

`PATCH /products/{id}/stock?quantity=5`

`quantity` positivo adiciona, negativo remove.

---

## 4. Carrinho

`/cart/{userId}` — protegido; **só o dono do carrinho** acessa.

### 4.1 Adicionar item

`POST /cart/{userId}/add/{productId}?quantity=2`

**200 OK** — retorna `CartItem`:

```json
{
  "id": 10,
  "cart": { "id": 5 },
  "product": { "id": 1, "name": "Funko Pop! Darth Vader" },
  "quantity": 2
}
```

### 4.2 Ver carrinho

`GET /cart/{userId}` → array de `CartItem`.

### 4.3 Atualizar quantidade

`PUT /cart/{userId}/item/{itemId}?quantity=5`

### 4.4 Remover um item

`DELETE /cart/{userId}/item/{itemId}`

### 4.5 Limpar carrinho

`DELETE /cart/{userId}/clear`

---

## 5. Itens do carrinho

`/cart-items` — endpoints alternativos que trabalham diretamente com IDs numéricos de carrinho/item.

### 5.1 Criar item

`POST /cart-items?cartId=5&productId=1&quantity=2`

### 5.2 Listar itens de um carrinho

`GET /cart-items/cart/{cartId}`

### 5.3 Atualizar quantidade

`PUT /cart-items/{cartItemId}?quantity=3`

### 5.4 Deletar item

`DELETE /cart-items/{cartItemId}`

### 5.5 Limpar carrinho por ID

`DELETE /cart-items/cart/{cartId}/clear`

---

## 6. Pedidos

`/orders` — protegido; ownership verificado.

### 6.1 Checkout (criar pedido a partir do carrinho)

`POST /orders/{userId}/checkout`

**200 OK**

```json
{
  "id": 101,
  "user": { "id": "a0eebc99-...", "name": "Maria Oliveira" },
  "total": 199.80,
  "status": "PENDING",
  "createdAt": "2026-07-12T12:00:00"
}
```

Erros: **422** se estoque insuficiente, **400** se carrinho vazio.

### 6.2 Listar pedidos do usuário

`GET /orders/{userId}`

### 6.3 Detalhes do pedido

`GET /orders/{orderId}/details`

### 6.4 Itens do pedido

`GET /orders/{orderId}/items` → array de `OrderItem`.

### 6.5 Atualizar status

`PUT /orders/{orderId}/status?status=SHIPPED`

Valores válidos: `PENDING`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`.

### 6.6 Cancelar pedido

`DELETE /orders/{orderId}/cancel`

---

## 7. Pagamentos

`/payments` — protegido.

### 7.1 Criar pagamento

`POST /payments/order/{orderId}?method=CREDIT_CARD`

`method`: `CREDIT_CARD`, `DEBIT_CARD`, `PIX`, `BOLETO`.

**Retorno:**

```json
{
  "id": 200,
  "order": { "id": 101 },
  "method": "CREDIT_CARD",
  "status": "PENDING",
  "paymentDate": null
}
```

### 7.2 Processar (simulação)

`POST /payments/{paymentId}/process` → status vai para `PROCESSING`.

### 7.3 Aprovar

`POST /payments/{paymentId}/approve` → `APPROVED`.

### 7.4 Recusar

`POST /payments/{paymentId}/decline` → `DECLINED`.

### 7.5 Estornar

`POST /payments/{paymentId}/refund` → `REFUNDED`.

### 7.6 Buscar por pedido

`GET /payments/order/{orderId}`

### 7.7 Listar por status

`GET /payments/status/{status}` — ex.: `/payments/status/APPROVED`

### 7.8 Atualizar status manualmente

`PUT /payments/{paymentId}/status?status=PROCESSING`

---

## 8. Endereços

`/addresses` — protegido.

### 8.1 Adicionar endereço

`POST /addresses/user/{userId}`

```json
{
  "street": "Rua das Flores",
  "number": "123",
  "city": "São Paulo",
  "state": "SP",
  "zipCode": "01234-567"
}
```

### 8.2 Listar endereços do usuário

`GET /addresses/user/{userId}`

### 8.3 Deletar endereço

`DELETE /addresses/{addressId}`

---

## 9. Telefones

`/phones` — protegido.

### 9.1 Adicionar telefone

`POST /phones/user/{userId}?number=11999999999`

O número vai como query param, **não** no corpo.

### 9.2 Listar telefones do usuário

`GET /phones/user/{userId}`

### 9.3 Atualizar telefone

`PUT /phones/{phoneId}?number=11988887777`

### 9.4 Deletar um telefone

`DELETE /phones/{phoneId}`

### 9.5 Deletar todos os telefones do usuário

`DELETE /phones/user/{userId}`

---

## 10. Acessibilidade

`/api/accessibility` — misto.

### 10.1 Idiomas suportados (público)

`GET /api/accessibility/languages`

**200 OK**

```json
{
  "languages": [
    { "code": "pt-BR", "name": "Português (Brasil)" },
    { "code": "en-US", "name": "English (US)" },
    { "code": "es-ES", "name": "Español" },
    { "code": "fr-FR", "name": "Français" },
    { "code": "de-DE", "name": "Deutsch" }
  ]
}
```

### 10.2 Ler configurações do usuário

`GET /api/accessibility/users/{userId}/settings` — **só o dono**.

```json
{
  "profiles": ["LOW_VISION", "DYSLEXIA"],
  "preferredLanguage": "pt-BR",
  "simplifiedLanguage": true,
  "screenReaderMode": false,
  "fontSizePreference": "large",
  "colorTheme": "high-contrast"
}
```

### 10.3 Salvar configurações

`PUT /api/accessibility/users/{userId}/settings`

```json
{
  "profiles": ["COLOR_BLINDNESS_RED_GREEN"],
  "preferredLanguage": "en-US",
  "simplifiedLanguage": false,
  "screenReaderMode": true,
  "fontSizePreference": "extra-large",
  "colorTheme": "dark"
}
```

**Validações:**
- `preferredLanguage`: `pt-BR | en-US | es-ES | fr-FR | de-DE`
- `fontSizePreference`: `normal | large | extra-large`
- `colorTheme`: `default | high-contrast | dark`
- `profiles`: qualquer combinação dos valores do enum `AccessibilityProfile`

### 10.4 Produto adaptado ao usuário

`GET /api/accessibility/products/{productId}`

Retorna o produto **traduzido para o idioma preferido do usuário logado**, com descrição acessível e texto alternativo de imagem.

```json
{
  "id": 1,
  "name": "Funko Pop! Darth Vader",
  "franchise": "Star Wars",
  "rarity": "RARO",
  "price": 99.90,
  "stockQuantity": 30,
  "description": "Darth Vader com sabre de luz vermelho.",
  "accessibleDescription": "Figura preta de 9cm com sabre vermelho iluminado, base cinza.",
  "imageAltText": "Funko Pop do Darth Vader empunhando sabre vermelho",
  "imageUrl": "https://cdn.pop.com/vader.jpg",
  "colorPalette": "#000000,#8B0000,#4A4A4A",
  "highContrast": true,
  "translatedDescription": "Darth Vader with red lightsaber.",
  "language": "en-US"
}
```

---

## 11. Enums

### `AccessibilityProfile`

- `VISUAL_IMPAIRMENT` — Deficiência visual (leitor de tela)
- `LOW_VISION` — Baixa visão (alto contraste, texto ampliado)
- `COLOR_BLINDNESS_RED_GREEN` — Daltonismo vermelho-verde
- `COLOR_BLINDNESS_BLUE` — Daltonismo azul-amarelo
- `COLOR_BLINDNESS_FULL` — Daltonismo total (escala de cinza)
- `MOTOR_IMPAIRMENT` — Dificuldade motora
- `COGNITIVE_IMPAIRMENT` — Dificuldade cognitiva
- `DYSLEXIA` — Dislexia
- `HEARING_IMPAIRMENT` — Deficiência auditiva
- `NONE`

### `OrderStatus`

- `PENDING` — Aguardando processamento
- `PROCESSING` — Em processamento
- `SHIPPED` — Enviado
- `DELIVERED` — Entregue
- `CANCELLED` — Cancelado

### `PaymentStatus`

- `PENDING`
- `PROCESSING`
- `APPROVED`
- `DECLINED`
- `REFUNDED`
- `CANCELLED`

### Métodos de pagamento (query param `method`)

- `CREDIT_CARD`
- `DEBIT_CARD`
- `PIX`
- `BOLETO`

---

## 12. Códigos HTTP e erros

| Código | Uso |
|:-------|:----|
| 200 | Sucesso em GET/PUT/PATCH |
| 201 | Criado (POST) |
| 204 | Sucesso sem corpo (DELETE) |
| 400 | Parâmetros inválidos ou payload malformado |
| 401 | Token ausente, inválido ou expirado |
| 403 | Autenticado, mas sem permissão (ownership check) |
| 404 | Recurso não encontrado |
| 409 | Conflito (ex.: email já usado) |
| 422 | Regra de negócio violada (ex.: estoque insuficiente) |
| 500 | Erro inesperado do servidor |

**Formato do corpo de erro:**

```json
{
  "timestamp": "2026-07-12T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Produto com ID 999 não encontrado.",
  "path": "/products/999"
}
```
