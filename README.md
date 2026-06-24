Aqui está o README completo para o seu projeto:

---

# 🎯 POP E-Commerce API

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-green?style=for-the-badge&logo=spring-boot)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-blue?style=for-the-badge&logo=spring-security)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql)
![H2 Database](https://img.shields.io/badge/H2-Database-004088?style=for-the-badge&logo=h2)
![Maven](https://img.shields.io/badge/Maven-3.9.0-red?style=for-the-badge&logo=apache-maven)
![JWT](https://img.shields.io/badge/JWT-Authentication-000000?style=for-the-badge&logo=json-web-tokens)

**API RESTful para gerenciamento de e-commerce de Funko Pops**

[**Documentação da API**](docs/docs.md) • [**Guia de Contribuição**](CONTRIBUTING.md) • [**Reportar Bug**](https://github.com/seu-usuario/pop-api/issues)

</div>

---

## 📋 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Funcionalidades](#-funcionalidades)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Pré-requisitos](#-pré-requisitos)
- [Instalação e Configuração](#-instalação-e-configuração)
- [Configuração do Ambiente](#-configuração-do-ambiente)
- [Executando a Aplicação](#-executando-a-aplicação)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Endpoints Principais](#-endpoints-principais)
- [Testes](#-testes)
- [Deploy](#-deploy)
- [Contribuição](#-contribuição)
- [Licença](#-licença)
- [Contato](#-contato)

---

## 🎯 Sobre o Projeto

**POP E-Commerce API** é uma aplicação back-end desenvolvida em **Java com Spring Boot** para gerenciar uma loja virtual de **Funko Pops**. A API oferece um sistema completo de e-commerce com:

- 📦 **Catálogo de produtos** com filtros por franquia, raridade e preço
- 🛒 **Carrinho de compras** totalmente funcional
- 📋 **Sistema de pedidos** com status tracking
- 💳 **Processamento de pagamentos** (simulado)
- 🔐 **Autenticação JWT** para proteção de endpoints
- 👥 **Gestão de usuários** com perfis

O sistema foi projetado com arquitetura RESTful, seguindo as melhores práticas de desenvolvimento e padrões de mercado.

---

## ✨ Funcionalidades

### 🔐 Autenticação e Segurança
- ✅ Registro de novos usuários
- ✅ Login com geração de JWT Token
- ✅ Criptografia de senhas com BCrypt
- ✅ Proteção de endpoints com Spring Security
- ✅ Token com validade de 24 horas

### 👤 Gestão de Usuários
- ✅ CRUD completo de usuários
- ✅ Busca por ID e e-mail
- ✅ Atualização de dados pessoais

### 🎁 Catálogo de Produtos
- ✅ CRUD completo de produtos
- ✅ Filtros por franquia, raridade e faixa de preço
- ✅ Busca por nome (case-insensitive)
- ✅ Controle de estoque
- ✅ Produtos com estoque baixo

### 🛒 Carrinho de Compras
- ✅ Adicionar/remover produtos
- ✅ Atualizar quantidade de itens
- ✅ Visualizar carrinho do usuário
- ✅ Limpar carrinho

### 📦 Pedidos
- ✅ Checkout a partir do carrinho
- ✅ Validação de estoque
- ✅ Histórico de pedidos do usuário
- ✅ Atualização de status (PENDING → PROCESSING → SHIPPED → DELIVERED)
- ✅ Cancelamento de pedidos

### 💳 Pagamentos
- ✅ Suporte a múltiplos métodos (Cartão de Crédito, Débito, PIX, Boleto)
- ✅ Processamento de pagamentos (simulado)
- ✅ Aprovação/recusa de pagamentos
- ✅ Estorno de pagamentos

### 📍 Endereços e Telefones
- ✅ Gestão de múltiplos endereços por usuário
- ✅ Tipos de telefone (Residencial, Comercial, Celular)

---

## 🛠 Tecnologias Utilizadas

| Tecnologia | Versão | Descrição |
|:-----------|:-------|:----------|
| **Java** | 17 | Linguagem de programação |
| **Spring Boot** | 3.4.3 | Framework principal |
| **Spring Security** | 6.x | Autenticação e autorização |
| **Spring Data JPA** | 3.x | ORM e persistência de dados |
| **Spring Web** | 6.x | API RESTful |
| **Spring DevTools** | 3.x | Desenvolvimento rápido |
| **JWT (JJWT)** | 0.11.5 | JSON Web Token |
| **BCrypt** | - | Criptografia de senhas |
| **PostgreSQL** | 16 | Banco de dados principal |
| **H2 Database** | 2.2.224 | Banco de dados em memória (dev/test) |
| **Maven** | 3.9.0 | Gerenciador de dependências |
| **Lombok** | 1.18.30 | Redução de boilerplate code |
| **Jackson** | 2.15 | Serialização/deserialização JSON |

---

## 📋 Pré-requisitos

Antes de começar, certifique-se de ter instalado em sua máquina:

- **Java 17** ou superior
- **Maven 3.9.0** ou superior
- **PostgreSQL 16** (opcional para produção)
- **Git** (para clonar o repositório)
- **Postman** ou **Insomnia** (para testar a API)

---

## 🚀 Instalação e Configuração

### 1. Clonar o repositório

```bash
git clone https://github.com/seu-usuario/pop-api.git
cd pop-api
```

### 2. Configurar o banco de dados

#### Opção A: Usando H2 (Recomendado para desenvolvimento)

O H2 já está configurado no `application.properties`. O banco será criado automaticamente em memória.

#### Opção B: Usando PostgreSQL (Produção)

Crie um banco de dados no PostgreSQL:

```sql
CREATE DATABASE pop_ecommerce;
CREATE USER pop_user WITH PASSWORD 'sua_senha_segura';
GRANT ALL PRIVILEGES ON DATABASE pop_ecommerce TO pop_user;
```

### 3. Configurar variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto (ou configure as variáveis no seu sistema):

```env
# Banco de Dados
DB_HOST=localhost
DB_PORT=5432
DB_NAME=pop_ecommerce
DB_USERNAME=pop_user
DB_PASSWORD=sua_senha_segura
DB_URL=jdbc:postgresql://localhost:5432/pop_ecommerce

# JWT
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=86400000

# Spring Profiles
SPRING_PROFILES_ACTIVE=dev
```

> ⚠️ **Importante:** Nunca commite o arquivo `.env` ou compartilhe sua `JWT_SECRET` publicamente!

### 4. Instalar as dependências

```bash
mvn clean install
```

---

## ⚙️ Configuração do Ambiente

### application.properties

O arquivo `src/main/resources/application.properties` contém as configurações principais:

```properties
# Spring Boot
spring.application.name=pop
server.port=8080

# Banco de Dados (H2 - Dev)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.use_sql_comments=true

# JWT (Configurado via .env)
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION}

# Spring Security
spring.security.user.name=admin
spring.security.user.password=admin123

# DevTools
spring.devtools.restart.enabled=true
spring.devtools.livereload.enabled=true
```

### Perfis Disponíveis

| Perfil | Descrição |
|:-------|:----------|
| `dev` | Ambiente de desenvolvimento (H2, logs detalhados) |
| `test` | Ambiente de testes |
| `prod` | Ambiente de produção (PostgreSQL, logs reduzidos) |

Para usar um perfil específico:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## ▶️ Executando a Aplicação

### Usando Maven

```bash
# Desenvolvimento (com H2)
mvn spring-boot:run

# Produção (com PostgreSQL)
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Usando o Wrapper do Maven

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

### Gerando o JAR

```bash
mvn clean package
java -jar target/pop-0.0.1-SNAPSHOT.jar
```

### Acessando a Aplicação

- **API:** `http://localhost:8080`
- **H2 Console:** `http://localhost:8080/h2-console`
- **Swagger UI:** (em breve) `http://localhost:8080/swagger-ui.html`

---

## 📁 Estrutura do Projeto

```
pop-api/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/acessibiliadade/pop/
│   │   │       ├── PopApplication.java          # Classe principal
│   │   │       ├── config/                      # Configurações
│   │   │       │   ├── SecurityConfig.java      # Configuração do Spring Security
│   │   │       │   ├── PasswordEncoderConfig.java # Configuração do BCrypt
│   │   │       │   └── WebConfig.java           # Configurações web
│   │   │       ├── controller/                  # Controllers REST
│   │   │       │   ├── AuthController.java      # Autenticação
│   │   │       │   ├── UserController.java      # Usuários
│   │   │       │   ├── ProductController.java   # Produtos
│   │   │       │   ├── CartController.java      # Carrinho
│   │   │       │   ├── OrderController.java     # Pedidos
│   │   │       │   └── PaymentController.java   # Pagamentos
│   │   │       ├── filter/                      # Filtros
│   │   │       │   └── JwtAuthenticationFilter.java # Validação JWT
│   │   │       ├── model/                       # Entidades JPA
│   │   │       │   ├── User.java
│   │   │       │   ├── Product.java
│   │   │       │   ├── Cart.java
│   │   │       │   └── ...
│   │   │       ├── repository/                  # Repositórios JPA
│   │   │       │   ├── UserRepository.java
│   │   │       │   ├── ProductRepository.java
│   │   │       │   └── ...
│   │   │       ├── service/                     # Serviços (Regras de negócio)
│   │   │       │   ├── UserService.java
│   │   │       │   ├── ProductService.java
│   │   │       │   └── JwtService.java
│   │   │       └── dto/                         # Data Transfer Objects
│   │   │           ├── LoginRequest.java
│   │   │           └── ...
│   │   └── resources/
│   │       ├── application.properties           # Configurações principais
│   │       └── static/                         # Recursos estáticos (se houver)
│   └── test/                                    # Testes unitários
│       └── java/                                # Testes JUnit
├── .env                                         # Variáveis de ambiente
├── pom.xml                                      # Dependências e build
├── README.md                                    # Este arquivo
└── docs/
    └── API.md                                   # Documentação completa da API
```

---

## 🚏 Endpoints Principais

### Autenticação

| Método | Endpoint | Descrição |
|:-------|:---------|:----------|
| `POST` | `/api/auth/register` | Registrar novo usuário |
| `POST` | `/api/auth/login` | Fazer login (retorna JWT) |

### Usuários (Protegido)

| Método | Endpoint | Descrição |
|:-------|:---------|:----------|
| `GET` | `/api/users` | Listar todos os usuários |
| `GET` | `/api/users/{id}` | Buscar usuário por ID |
| `PUT` | `/api/users/{id}` | Atualizar usuário |
| `DELETE` | `/api/users/{id}` | Deletar usuário |

### Produtos (Protegido)

| Método | Endpoint | Descrição |
|:-------|:---------|:----------|
| `GET` | `/products` | Listar produtos (com paginação) |
| `GET` | `/products/{id}` | Buscar produto por ID |
| `GET` | `/products/search?name={name}` | Buscar por nome |
| `GET` | `/products/franchise/{franchise}` | Buscar por franquia |
| `GET` | `/products/rarity/{rarity}` | Buscar por raridade |
| `GET` | `/products/price-range?min={min}&max={max}` | Buscar por faixa de preço |
| `POST` | `/products` | Criar produto (ADMIN) |

### Carrinho (Protegido)

| Método | Endpoint | Descrição |
|:-------|:---------|:----------|
| `GET` | `/cart/{userId}` | Visualizar carrinho |
| `POST` | `/cart/{userId}/add/{productId}?quantity={q}` | Adicionar item |
| `PUT` | `/cart/{userId}/item/{itemId}?quantity={q}` | Atualizar quantidade |
| `DELETE` | `/cart/{userId}/item/{itemId}` | Remover item |
| `DELETE` | `/cart/{userId}/clear` | Limpar carrinho |

### Pedidos (Protegido)

| Método | Endpoint | Descrição |
|:-------|:---------|:----------|
| `POST` | `/orders/{userId}/checkout` | Finalizar pedido |
| `GET` | `/orders/{userId}` | Listar pedidos do usuário |
| `GET` | `/orders/{orderId}/details` | Detalhes do pedido |
| `PUT` | `/orders/{orderId}/status?status={status}` | Atualizar status |
| `DELETE` | `/orders/{orderId}/cancel` | Cancelar pedido |

> 📚 **Documentação completa:** Consulte o arquivo [API.md](docs/API.md) para todos os endpoints e detalhes.

---

## 🧪 Testes

### Executando os testes

```bash
# Todos os testes
mvn test

# Testes específicos
mvn test -Dtest=UserServiceTest

# Com cobertura de código
mvn test jacoco:report
```

### Endpoints de Teste

A API inclui endpoints para simulação e teste:

```bash
# Registrar usuário
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"João","email":"joao@teste.com","password":"senha123"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"joao@teste.com","password":"senha123"}'

# Usar token (substitua TOKEN_AQUI)
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer TOKEN_AQUI"
```

---

## 🌐 Deploy

### Deploy no Heroku

```bash
# Criar app no Heroku
heroku create pop-api

# Configurar variáveis de ambiente
heroku config:set JWT_SECRET=seu_secret_aqui
heroku config:set JWT_EXPIRATION=86400000
heroku config:set SPRING_PROFILES_ACTIVE=prod

# Deploy
git push heroku main
```

### Deploy no Docker

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/pop-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# Build da imagem
docker build -t pop-api .

# Executar container
docker run -p 8080:8080 \
  -e JWT_SECRET=seu_secret \
  -e SPRING_PROFILES_ACTIVE=prod \
  pop-api
```

### Deploy na AWS (EC2)

```bash
# Transferir JAR
scp target/pop-0.0.1-SNAPSHOT.jar ec2-user@ip:/home/ec2-user/

# Executar
ssh ec2-user@ip
java -jar pop-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --jwt.secret=$JWT_SECRET
```

---

## 🤝 Contribuição

Contribuições são muito bem-vindas! Por favor, siga estas etapas:

1. **Fork** o projeto
2. Crie sua **branch de feature** (`git checkout -b feature/AmazingFeature`)
3. **Commit** suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. **Push** para a branch (`git push origin feature/AmazingFeature`)
5. Abra um **Pull Request**

### Padrões de Código

- Use **Java 17** features (records, switch expressions, etc.)
- Siga os padrões de nomenclatura do Java (CamelCase)
- Mantenha a cobertura de testes acima de 80%
- Documente todos os endpoints públicos
- Use DTOs para comunicação com o cliente

---

## 📄 Licença

Este projeto está sob a licença **MIT**. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 📞 Contato

**Equipe POP E-Commerce**

- 📧 **Email:** dev@pop-ecommerce.com
- 🌐 **Site:** https://pop-ecommerce.com
- 🐛 **Issues:** [GitHub Issues](https://github.com/seu-usuario/pop-api/issues)
- 📚 **Documentação:** [API Docs](docs/API.md)

---

## 🙏 Agradecimentos

- [Spring Boot](https://spring.io/projects/spring-boot) - Framework incrível
- [Funko Pop](https://www.funko.com/) - Pela inspiração
- Todos os contribuidores do projeto

---

<div align="center">

**Feito com ❤️ pela equipe POP**

[⬆ Voltar ao topo](#-pop-e-commerce-api)

</div>

---

## 📝 Notas Adicionais


### Sobre o JWT

O token JWT tem validade de **24 horas**. Para configurar um tempo diferente, altere a variável `JWT_EXPIRATION` no `.env`.

### Segurança em Produção

1. **Nunca** use a chave JWT padrão em produção
2. **Sempre** use HTTPS em produção
3. **Configure** CORS adequadamente
4. **Monitore** logs de acesso
5. **Faça** backup regular do banco de dados

---

**Última atualização:** 23 de Junho de 2026