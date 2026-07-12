# 🚀 Guia de Setup e Execução - POP E-Commerce API

## 📋 Pré-requisitos

Certifique-se de ter instalado:

- **Java 17+** — [Download](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
  ```bash
  java -version  # Deve mostrar 17.x.x
  ```

- **Maven 3.9.0+** — [Download](https://maven.apache.org/download.cgi)
  ```bash
  mvn -version  # Deve mostrar Maven 3.9.0+
  ```

- **PostgreSQL 16+** — [Download](https://www.postgresql.org/download/)
  ```bash
  psql --version  # Deve mostrar 16.x
  ```

- **Git** — [Download](https://git-scm.com/)

---

## 🗄️ Configurar PostgreSQL

### 1. Criar banco e usuário

```bash
# No terminal do PostgreSQL (psql)
psql -U postgres

-- Dentro do psql:
CREATE DATABASE pop_ecommerce;
CREATE USER pop_user WITH PASSWORD 'pop_password_123';
GRANT ALL PRIVILEGES ON DATABASE pop_ecommerce TO pop_user;
\q
```

Ou via script direto:
```bash
psql -U postgres -c "CREATE DATABASE pop_ecommerce;"
psql -U postgres -c "CREATE USER pop_user WITH PASSWORD 'pop_password_123';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE pop_ecommerce TO pop_user;"
```

---

## ⚙️ Configurar Variáveis de Ambiente

### 1. Copiar `.env.example` para `.env`

```bash
cd pop-api
cp .env.example .env
```

### 2. Editar `.env` com suas credenciais

```env
# POSTGRESQL - Aiven
DB_HOST=localhost
DB_PORT=5432
DB_NAME=pop_ecommerce
DB_USERNAME=pop_user
DB_PASSWORD=pop_password_123
DB_URL=jdbc:postgresql://localhost:5432/pop_ecommerce

# JWT
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=86400000

# SPRING PROFILES
SPRING_PROFILES_ACTIVE=prod
```

> ⚠️ **Importante:**
> - `JWT_SECRET` deve ter mínimo 32 caracteres (use `openssl rand -base64 32` para gerar uma chave segura)
> - Nunca commite `.env` (já está no `.gitignore`)
> - `DB_PASSWORD` — use uma senha forte em produção

---

## 🔨 Compilar e Executar

### Opção 1: Maven direto (recomendado para desenvolvimento)

```bash
# Compilar
./mvnw clean compile

# Executar
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`.

### Opção 2: Gerar JAR e executar

```bash
# Build
./mvnw clean package -DskipTests

# Executar
java -jar target/pop-0.0.1-SNAPSHOT.jar
```

### Opção 3: No Windows (cmd.exe)

```cmd
# Compilar
mvnw.cmd clean compile

# Executar
mvnw.cmd spring-boot:run
```

---

## 🧪 Testar a API

### 1. Swagger UI (recomendado)

Abra no navegador:
```
http://localhost:8080/swagger-ui.html
```

Lá você vê todos os endpoints, testa direto na interface, e tem um botão **Authorize** para colar o JWT token.

### 2. Fluxo básico via curl

#### Registrar usuário
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@exemplo.com",
    "password": "senha123456"
  }'
```

**Resposta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "joao@exemplo.com",
  "name": "João Silva"
}
```

#### Login (se já existe)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@exemplo.com",
    "password": "senha123456"
  }'
```

#### Listar produtos (requer token)
```bash
curl -X GET http://localhost:8080/products \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

Substitua `SEU_TOKEN_AQUI` pelo valor de `token` retornado do `register`/`login`.

#### Visualizar carrinho do usuário
```bash
curl -X GET http://localhost:8080/cart/SEU_USER_ID \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

Substitua `SEU_USER_ID` pelo valor de `userId` retornado do `register`/`login`.

---

## 📚 Documentação Completa

- **Swagger/OpenAPI:** `http://localhost:8080/swagger-ui.html`
- **Endpoints:** Ver [README.md](README.md#-endpoints-principais)
- **API Docs:** Ver [docs/docs.md](docs/docs.md)

---

## 🐛 Troubleshooting

### Erro: "Could not connect to PostgreSQL"

```
Caused by: java.net.ConnectException: Connection refused: connect
```

**Solução:**
1. Verifique se PostgreSQL está rodando: `psql -U postgres` (não deve falhar)
2. Confira as credenciais no `.env` (host, port, user, password)
3. Verifique se o banco `pop_ecommerce` existe: `psql -U pop_user -d pop_ecommerce`

### Erro: "JWT secret is too short"

```
The secret key byte array must be at least 256 bits long
```

**Solução:**
- Gere uma chave segura: `openssl rand -base64 32`
- Copie o resultado na variável `JWT_SECRET` no `.env`

### Erro: "Database pop_ecommerce does not exist"

```
ERROR: database "pop_ecommerce" does not exist
```

**Solução:**
```bash
psql -U postgres -c "CREATE DATABASE pop_ecommerce;"
```

### Port 8080 já está em uso

```
Address already in use: bind
```

**Solução:**
```bash
# Mudar porta via .env (crie a variável):
SERVER_PORT=8081

# Ou via linha de comando:
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Falha na compilação: "Plugin execution not covered"

Essa é uma aviso do IDE, não erro. Ignore ou execute direto:
```bash
./mvnw clean compile -e
```

---

## 📝 Estrutura do Projeto

```
pop-api/
├── src/main/java/com/acessibiliadade/pop/
│   ├── controller/          # REST endpoints
│   ├── service/             # Lógica de negócio
│   ├── model/               # Entidades JPA
│   ├── repository/          # Data access
│   ├── dto/                 # Data Transfer Objects
│   ├── config/              # Spring configs
│   ├── exception/           # Exception handlers
│   ├── filter/              # JWT filter
│   └── security/            # Autorização
├── src/main/resources/
│   ├── application.properties    # Config principal
│   └── db/migrations/            # Flyway migrations (V001, V002, V003)
├── .env.example             # Template de variáveis
├── pom.xml                  # Dependências Maven
├── README.md                # Overview do projeto
├── docs/docs.md             # API documentation
└── SETUP.md                 # Este arquivo
```

---

## 🚀 Próximas ações

1. **Teste o fluxo completo:**
   - Register → Login → Add to cart → Checkout

2. **Explorar Swagger:**
   - Acesse `http://localhost:8080/swagger-ui.html`
   - Use o botão **Authorize** para colar token
   - Teste todos os endpoints

3. **Revisar banco:**
   ```bash
   psql -U pop_user -d pop_ecommerce
   \dt  # Listar tabelas
   ```

4. **Acompanhar logs:**
   A aplicação imprime tudo no console — procure por `ERROR`, `WARN` ou `Hibernate SQL:`.

---

## ℹ️ Informações úteis

| Recurso | URL |
|---------|-----|
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| Health Check | `http://localhost:8080/actuator/health` (se habilitado) |

---

## 📞 Suporte

Se encontrar problemas:
1. Confira as logs no console
2. Verifique o `.env` (host, credenciais, JWT_SECRET)
3. Confirme que PostgreSQL está rodando
4. Delete banco e recrie (`DROP DATABASE pop_ecommerce;` → `CREATE DATABASE pop_ecommerce;`)
