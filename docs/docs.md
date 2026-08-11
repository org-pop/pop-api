# POP E-Commerce API — Documentação

**Base URL:** `http://localhost:8080`
**Formato:** JSON
**Auth:** JWT no header `Authorization: Bearer <token>`

## Sumário

1. [Autenticação](#1-autenticação)
2. [Usuários](#2-usuários)
3. [Produtos](#3-produtos)
4. [Carrinho](#4-carrinho)
5. [Pedidos](#5-pedidos)
6. [Pagamentos](#6-pagamentos)
7. [Endereços](#7-endereços)
8. [Telefones](#8-telefones)
9. [Acessibilidade](#9-acessibilidade)
10. [Enums](#10-enums)
11. [Códigos HTTP e erros](#11-códigos-http-e-erros)

---

## Convenções

- IDs de usuário são **UUID**. Demais IDs são **Long**.
- Timestamps em **ISO-8601** (`2026-07-12T14:30:00`).
- Dinheiro em `BigDecimal` (duas casas).
- Endpoints com `{userId}` no path validam ownership contra o token — dono errado retorna **403**.
- Endpoints com `{orderId}` / `{paymentId}` / `{addressId}` / `{phoneId}` validam pelo dono do recurso associado.
- Operações administrativas exigem `ROLE_ADMIN`: CRUD de produtos, alteração de status de pedido e de pagamento, listagem de todos os usuários. Todos os novos cadastros nascem com `ROLE_USER`; para promover:
  ```sql
  UPDATE users SET role = 'ROLE_ADMIN' WHERE email = 'admin@exemplo.com';
  ```
- `POST /api/auth/**` e `GET /api/accessibility/languages` são públicos. Todo o resto exige `Authorization: Bearer <token>`.
- Token inválido ou expirado → **401** com corpo JSON no mesmo formato dos demais erros.
- Campos sensíveis (`password`, `role`) são anotados com `@JsonIgnore` na entidade `User` e nunca aparecem em respostas — nem mesmo quando o `User` vem embutido em `Order`, `Payment` etc.

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

### 2.1 Listar usuários (admin)

`GET /api/users` — **exige `ROLE_ADMIN`**. Usuários comuns recebem **403**.

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

### 2.2 Buscar por ID (dono)

`GET /api/users/{id}` — `id` = UUID. Só o próprio usuário.
**404** se não existir; **403** se `id` for de outro usuário.

### 2.3 Buscar por email (dono ou admin)

`GET /api/users/email/{email}`

O próprio usuário sempre pode se buscar. Para consultar o email de **outra** pessoa é preciso `ROLE_ADMIN`.

A comparação **não diferencia maiúsculas de minúsculas** — `MARIA@email.com` e `maria@email.com` resolvem para o mesmo usuário.

**200 OK** — um `UserResponse` (mesmo formato de 2.1).

| Situação | Resposta |
|:---------|:---------|
| Email do próprio usuário | **200** |
| Email de terceiro, com `ROLE_ADMIN` e usuário existente | **200** |
| Email de terceiro, sem `ROLE_ADMIN` | **403** |
| Email de terceiro, com `ROLE_ADMIN` e usuário inexistente | **404** |
| Email fora do formato válido | **422** |

O **403** vem antes da consulta ao banco: para um usuário comum, email inexistente e email de terceiro respondem igual, então o endpoint não serve para descobrir quais emails estão cadastrados.

### 2.4 Atualizar usuário (dono)

`PUT /api/users/{id}` — só o próprio usuário.

```json
{
  "name": "Maria O.",
  "email": "maria.o@email.com",
  "password": "novaSenha123"
}
```

`password` é opcional — se omitido, senha não muda. Se enviado, mínimo 6 chars.

### 2.5 Deletar usuário (dono)

`DELETE /api/users/{id}` → **200 OK** (corpo vazio). Só o próprio usuário.

---

## 3. Produtos

`/products` — leitura para qualquer autenticado; escrita exige `ROLE_ADMIN`.

### 3.1 Criar produto (admin)

`POST /products` — **exige `ROLE_ADMIN`**.

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

### 3.4 Atualizar produto (admin)

`PUT /products/{id}` — mesmo corpo do POST. **Exige `ROLE_ADMIN`**.

### 3.5 Deletar produto (admin)

`DELETE /products/{id}` — **exige `ROLE_ADMIN`**.

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

### 3.11 Ajustar estoque (admin)

`PATCH /products/{id}/stock?quantity=5` — **exige `ROLE_ADMIN`**.

`quantity` positivo adiciona, negativo remove.

---

## 4. Carrinho

`/cart/{userId}` — protegido; **só o dono do carrinho** acessa.

### 4.1 Adicionar item

`POST /cart/{userId}/add/{productId}?quantity=2`

**200 OK** — retorna `CartItemResponse`:

```json
{
  "id": 10,
  "product": { "id": 1, "name": "Funko Pop! Darth Vader", "price": 99.90, "imageUrl": "https://cdn.pop.com/vader.jpg" },
  "quantity": 2
}
```

**Erros:** **400** se `quantity <= 0` ou se a soma no carrinho ultrapassar o estoque disponível do produto.

### 4.2 Ver carrinho

`GET /cart/{userId}` → array de `CartItem`.

### 4.3 Atualizar quantidade

`PUT /cart/{userId}/item/{itemId}?quantity=5`

`quantity <= 0` remove o item. `quantity` maior que o estoque disponível retorna **400**.

### 4.4 Remover um item

`DELETE /cart/{userId}/item/{itemId}`

### 4.5 Limpar carrinho

`DELETE /cart/{userId}/clear`

---

## 5. Pedidos

`/orders` — protegido; ownership verificado.

### 5.1 Checkout (criar pedido a partir do carrinho)

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

Erros: **400** se estoque insuficiente ou carrinho vazio. O checkout usa `SELECT FOR UPDATE` no produto, então dois checkouts simultâneos serializam e não driblam o estoque.

### 5.2 Listar pedidos do usuário

`GET /orders/{userId}`

### 5.3 Detalhes do pedido

`GET /orders/{orderId}/details`

### 5.4 Itens do pedido

`GET /orders/{orderId}/items` → array de `OrderItem`.

### 5.5 Atualizar status (admin)

`PUT /orders/{orderId}/status?status=SHIPPED` — **exige `ROLE_ADMIN`**.

Valores válidos: `PENDING`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`.

### 5.6 Cancelar pedido

`DELETE /orders/{orderId}/cancel`

O dono do pedido pode cancelar enquanto o status for `PENDING` ou `PROCESSING`. Pedidos já `SHIPPED` / `DELIVERED` retornam **400**. Cancelar um pedido já `CANCELLED` também retorna **400** (evita restaurar estoque duas vezes).

---

## 6. Pagamentos

`/payments` — leitura e criação pelo **dono do pedido**; alterações de status só por **`ROLE_ADMIN`** (representam a autoridade do gateway/administrador, não do comprador).

### 6.1 Ciclo de vida (máquina de estados)

Cada pagamento nasce em `PENDING` e só transita entre estados permitidos. Transições fora dessa tabela retornam **400** com mensagem `Transição de pagamento inválida: X -> Y`. Estados terminais (`DECLINED`, `REFUNDED`, `CANCELLED`) não têm saída.

| De | Para | Endpoint |
|:---|:---|:---|
| `PENDING` | `PROCESSING` | `POST /payments/{id}/process` |
| `PENDING` | `CANCELLED` | `PUT /payments/{id}/status?status=CANCELLED` |
| `PROCESSING` | `APPROVED` | `POST /payments/{id}/approve` |
| `PROCESSING` | `DECLINED` | `POST /payments/{id}/decline` |
| `APPROVED` | `REFUNDED` | `POST /payments/{id}/refund` |

Repetir a mesma transição (ex.: `approve` sobre pagamento já `APPROVED`) também retorna **400** com `Pagamento já está no status APPROVED`.

### 6.2 Criar pagamento (dono do pedido)

`POST /payments/order/{orderId}?method=CREDIT_CARD`

`method`: `CREDIT_CARD`, `DEBIT_CARD`, `PIX`, `BOLETO`.

Cada pedido só pode ter **um** pagamento. Chamar de novo para o mesmo `orderId` retorna **400**. A unique constraint no banco cobre corridas.

**Retorno:** `PaymentResponse`

```json
{
  "id": 200,
  "orderId": 101,
  "method": "CREDIT_CARD",
  "status": "PENDING"
}
```

### 6.3 Processar (admin)

`POST /payments/{paymentId}/process` — **exige `ROLE_ADMIN`**.

Transição `PENDING` → `PROCESSING`.

### 6.4 Aprovar (admin)

`POST /payments/{paymentId}/approve` — **exige `ROLE_ADMIN`**.

Transição `PROCESSING` → `APPROVED`.

### 6.5 Recusar (admin)

`POST /payments/{paymentId}/decline` — **exige `ROLE_ADMIN`**.

Transição `PROCESSING` → `DECLINED`.

### 6.6 Estornar (admin)

`POST /payments/{paymentId}/refund` — **exige `ROLE_ADMIN`**.

Transição `APPROVED` → `REFUNDED`.

### 6.7 Buscar por pedido (dono do pedido)

`GET /payments/order/{orderId}`

### 6.8 Listar por status (usuário logado)

`GET /payments/status/{status}` — ex.: `/payments/status/APPROVED`. Retorna **apenas os pagamentos do usuário logado** com aquele status.

### 6.9 Atualizar status manualmente (admin)

`PUT /payments/{paymentId}/status?status=PROCESSING` — **exige `ROLE_ADMIN`**.

Sujeito às mesmas transições permitidas em 6.1.

---

## 7. Endereços

`/addresses` — protegido; **só o dono** acessa.

### 7.1 Adicionar endereço

`POST /addresses/user/{userId}` — ownership pelo `userId` do path.

```json
{
  "street": "Rua das Flores",
  "number": "123",
  "city": "São Paulo",
  "state": "SP",
  "zipCode": "01234-567"
}
```

Qualquer `user` que venha no corpo é ignorado — o dono do endereço é sempre o do path.

### 7.2 Listar endereços do usuário

`GET /addresses/user/{userId}` — ownership pelo `userId` do path.

### 7.3 Deletar endereço

`DELETE /addresses/{addressId}` — ownership verificado pelo dono do próprio endereço: **403** se o endereço pertencer a outro usuário; **404** se não existir.

---

## 8. Telefones

`/phones` — protegido; **só o dono** acessa.

### 8.1 Adicionar telefone

`POST /phones/user/{userId}?number=11999999999`

O número vai como query param, **não** no corpo. Ownership pelo `userId` do path.

### 8.2 Listar telefones do usuário

`GET /phones/user/{userId}` — ownership pelo `userId` do path.

### 8.3 Atualizar telefone

`PUT /phones/{phoneId}?number=11988887777` — ownership pelo dono do próprio telefone.

### 8.4 Deletar um telefone

`DELETE /phones/{phoneId}` — ownership pelo dono do próprio telefone.

### 8.5 Deletar todos os telefones do usuário

`DELETE /phones/user/{userId}` — ownership pelo `userId` do path.

---

## 9. Acessibilidade

`/api/accessibility` — misto.

### 9.1 Idiomas suportados (público)

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

### 9.2 Ler configurações do usuário

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

### 9.3 Salvar configurações

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

### 9.4 Produto adaptado ao usuário

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

A tradução usa a API pública **LibreTranslate** (`translate.terraprint.co`) e os nomes das cores vêm da **TheColorAPI** (`thecolorapi.com`). Chamadas externas têm timeout curto; se qualquer uma falhar, o campo correspondente cai para o valor original em pt-BR.

---

## 10. Enums

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

Transições permitidas na seção 6.1.

### Métodos de pagamento (query param `method`)

- `CREDIT_CARD`
- `DEBIT_CARD`
- `PIX`
- `BOLETO`

---

## 11. Códigos HTTP e erros

| Código | Uso |
|:-------|:----|
| 200 | Sucesso em GET/PUT/PATCH e DELETE (corpo vazio) |
| 201 | Criado (POST em `/api/auth/register`) |
| 400 | Payload malformado, parâmetro inválido ou regra de negócio violada (estoque insuficiente, pagamento duplicado, transição inválida, pedido já cancelado etc.) |
| 401 | Token ausente, inválido ou expirado |
| 403 | Autenticado, mas sem permissão (ownership check ou papel `ROLE_ADMIN` ausente) |
| 404 | Recurso não encontrado |
| 409 | Conflito (ex.: email já usado no cadastro, violação de constraint de unicidade) |
| 422 | Erro de validação — em campos do payload (`@Valid`) ou em parâmetros de path/query anotados |
| 500 | Erro inesperado do servidor |

**Formato-padrão do corpo de erro:**

```json
{
  "timestamp": "2026-07-12T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Produto não encontrado: 999"
}
```

**Corpo do 422** — traz o mapa `fields` com o campo (ou parâmetro) e a respectiva mensagem:

```json
{
  "timestamp": "2026-07-12T12:00:00",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Erro de validação nos parâmetros enviados",
  "fields": {
    "getUserByEmail.email": "Email inválido"
  }
}
```
