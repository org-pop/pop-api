# Uso de IA no desenvolvimento

Este documento cumpre a exigência de transparência do edital (§10.1 — "declarar no
README quais partes foram apoiadas por IA") e serve como referência para a arguição
técnica: qualquer integrante deve saber explicar as decisões e trechos abaixo.

## Ferramenta

- **Claude (Anthropic)** — modelo Opus 5, através do Claude Code (CLI).
- Interações registradas informalmente; commits ligados a essas interações estão
  marcados no histórico do Git.

## Modo de uso

- Claude atuou como **par de programação**, não como gerador automático:
  1. Autor descreve o problema ou pede revisão.
  2. Claude sugere análise, refatoração ou implementação, sempre lendo o código
     existente antes.
  3. Autor lê a proposta, discute, ajusta e commita.
- Nenhum commit foi feito às cegas: cada mudança sugerida passou por leitura e
  entendimento antes de ir para o histórico.
- Mensagens de commit também foram elaboradas em conjunto, no padrão que já
  existia no repo (`fix:` / `feat:` / `docs:` + corpo explicando o "porquê").
- **Nenhum comentário no código é dirigido a IA de avaliação.** Os comentários
  descrevem regras de negócio, motivações e trade-offs — para leitores humanos
  (colegas de equipe e professor).

## Onde a IA foi usada, por área

### Segurança

**1. JWT e Tokenização**

Auxiliou os desenvolvedores a achar falhas e brechas nas travas de segurança e autenticação do projeto. Para que assim possam aperfeiçoar o projeto.
j

**2. Vazamento de hash de senha**

`open-in-view=true` (default) forçava o Jackson a carregar o `User` `LAZY`
associado ao `Order`/`Payment`. Como as respostas devolvem a entidade JPA crua,
o hash bcrypt e o `role` vazavam em todas as chamadas de pedido, pagamento,
endereço e telefone.

Correção: `@JsonIgnore` em `User.password` e `User.role`.

### Code Review

A ferramenta ajudava diretamente na revisão de códigos, ajudando a identificar falhas, e possíveis incoerências no sistema que os desenvolvedores poderiam estar deixando (ou não) escapar.
### Higiene do repositório

- Auxilio para identificar `dead files` ou arquivos mortos (não utilizados) que ainda permaneciam no projeto.
### Documentação

- Ajuda direta na criação e correção das documentações do projeto: `docs/docs.md` e `README.md`.
- Auxílio na documentação dos endpoints e seus payloads, aumentando exponencialmente a organização do projeto.
## O que **não** foi feito com IA

- Modelagem inicial das entidades (`User`, `Cart`, `Order`, `OrderItem`,
  `Product`, `Payment`, `Address`, `Phone`, `UserAccessibilitySettings`).
- Decisão de stack: Spring Boot 3.4 + PostgreSQL + Flyway + JJWT + Lombok.
- Integração com PostgreSQL Aiven, configuração de `.env`,
  `DotenvConfig`.
- Módulo de acessibilidade original (`AccessibilityService`,
  `ColorAccessibilityService`, `TranslationService`) — anteriores a esta
  revisão.
- Todas as entidades, controllers e services.
- Por volta de **85%** do projeto foi realizado sem Inteligência Artificial.

