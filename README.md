# (Projeto) usuario-service

Este projeto é o microsserviço de gerenciamento de usuários para a API "Scripta" de biblioteca universitária. Ele é responsável pela autenticação (Login) e administração (CRUD) dos usuários da plataforma.

A aplicação é construída em **Java 21** com **Spring Boot 3.5.7** e utiliza **Spring Security** para proteção de endpoints via **JWT (JSON Web Tokens)**.

## Stack de Tecnologias

| Categoria | Tecnologia | Justificativa / Uso |
| :--- | :--- | :--- |
| **Core** | Java 21 | Linguagem principal da aplicação. |
| **Framework** | Spring Boot 3.5.7 | Framework base (Web, Data, Security). |
| **Segurança** | Spring Security | Gerenciamento de autenticação e autorização. |
| | JJWT (Java JWT) | Geração e validação de tokens JWT. |
| **Persistência** | Spring Data JPA | Camada de acesso a dados. |
| **Banco de Dados** | PostgreSQL | Banco de dados relacional para produção. |
| | H2 Database | Banco de dados em memória para testes. |
| **Utilitários** | Lombok | Redução de código boilerplate (ex: @Data, @Builder). |
| | Spring Validation | Validação de DTOs de entrada (ex: @NotBlank). |

## Arquitetura (Design)

A arquitetura do projeto foca na separação de responsabilidades (princípios **SOLID**). Ela separa o modelo de **Domínio** (a classe `Usuario`) da camada de **Dados** (a `@Entity` `UsuarioEntity`). A integração com o Spring Security é feita de forma desacoplada através da classe `CustomUsuarioDetails`, que implementa `UserDetails` e encapsula o objeto `Usuario`.

A camada de `Repository` (`UsuarioRepository`) atua como um **Adapter**, implementando a interface de serviço (`UsuarioService`) e usando um `UsuarioMapper` para traduzir entre o domínio e a entidade. Isso desacopla a lógica de negócio da implementação do JPA. A segurança é **Stateless** (sem sessão), gerenciada por Spring Security. Um `JwtAuthenticatedFilter` intercepta requisições, enquanto o `JwtTokenProvider` valida tokens JWT para autenticação.

## Configuração e Execução (IntelliJ IDEA)

### 1\. Pré-requisitos

  * Java 21 (JDK)
  * Uma instância do PostgreSQL rodando.

### 2\. Configurando o IntelliJ IDEA

1.  **Abra o projeto:**

      * Abra o projeto (a pasta `usuario-service`) no IntelliJ IDEA.
      * A IDE detectará o arquivo `pom.xml` e deve baixar automaticamente todas as dependências do Maven.

2.  **Configure as Variáveis de Ambiente:**

      * A aplicação precisa de variáveis de ambiente para se conectar ao banco e assinar tokens (definidas em `application.properties`).
      * No canto superior direito do IntelliJ, clique em `Edit Configurations...` (ou `Add Configuration...` se for a primeira vez).
      * Na janela que abrir, localize a aplicação `UsuarioServiceApplication` (a IDE geralmente a cria automaticamente).
      * No campo **"Environment variables"**, clique no ícone para adicionar ou cole o seguinte, substituindo pelos seus valores:

    <!-- end list -->

    ```bash
    DB_USERS=nome_do_seu_banco_de_dados;
    POSTGRE_USERNAME=seu_usuario_postgre;
    POSTGRE_PASSWORD=sua_senha_postgre;
    JWT_SECRECT_KEY=sua_chave_secreta_aqui
    ```

      * Clique em "OK" para salvar.

3.  **Execute a Aplicação:**

      * Navegue até o arquivo `src/main/java/br/com/scripta_api/usuario_service/UsuarioServiceApplication.java`.
      * Clique no ícone verde "Play" ao lado da declaração da classe `public class UsuarioServiceApplication`.
      * Selecione `Run 'UsuarioServiceApplication'`.
      * A aplicação iniciará no console, rodando na porta `8081`.

## API Endpoints (Contrato da API)

### Módulo: Autenticação (`/auth`)

#### `POST /auth/login`

Autentica um usuário e retorna um token JWT.

  * **Acesso**: Público

  * **Request Body** (`LoginRequest`):

    ```json
    {
      "matricula": "123456",
      "senha": "minhasenha123"
    }
    ```

  * **Response 200 OK** (`LoginResponse`):

    ```json
    {
      "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJyb2..."
    }
    ```

### Módulo: Usuários (`/usuarios`)

#### `POST /usuarios`

Cria um novo usuário (Aluno ou Bibliotecário).

  * **Acesso**: Autenticado. Requer Role: `BIBLIOTECARIO`.

  * **Request Body** (`CriarUsuarioRequest`):

    ```json
    {
      "nome": "Nome do Aluno",
      "matricula": "987654",
      "senha": "senhaforte123",
      "tipoDeConta": "ALUNO"
    }
    ```

  * **Response 201 Created** (`UsuarioResponse`):

    ```json
    {
      "id": 1,
      "nome": "Nome do Aluno",
      "matricula": "987654",
      "tipoDeConta": "ALUNO"
    }
    ```

#### `GET /usuarios`

Lista todos os usuários cadastrados no sistema.

  * **Acesso**: Autenticado. Requer Role: `BIBLIOTECARIO`.

  * **Response 200 OK** (`List<UsuarioResponse>`):

    ```json
    [
      {
        "id": 1,
        "nome": "Nome do Aluno",
        "matricula": "987654",
        "tipoDeConta": "ALUNO"
      }
    ]
    ```

#### `GET /usuarios/me`

Retorna os dados do usuário que está atualmente logado (baseado no token JWT enviado).

  * **Acesso**: Autenticado (Qualquer role: `ALUNO` ou `BIBLIOTECARIO`).

  * **Response 200 OK** (`UsuarioResponse`):

    ```json
    {
      "id": 1,
      "nome": "Nome do Aluno",
      "matricula": "987654",
      "tipoDeConta": "ALUNO"
    }
    ```

## Detalhes de Segurança

  * **Fluxo de Autenticação**: O cliente envia `matricula` e `senha` para `POST /auth/login`. O servidor valida e retorna um JWT.
  * **Uso do Token**: Para acessar endpoints protegidos, o cliente deve enviar o token no cabeçalho `Authorization`:
    `Authorization: Bearer <seu_token_jwt_aqui>`
  * **Expiração**: Os tokens expiram em 24 horas (86.400.000 ms).
  * **Armazenamento de Senha**: As senhas são hasheadas usando `BCryptPasswordEncoder` antes de serem salvas no banco de dados.
  * **Autorização (Roles)**: O sistema utiliza os `TipoDeConta` (`ALUNO`, `BIBLIOTECARIO`) para definir as permissões, convertidas para `ROLE_`. O `SecurityConfig` define quais rotas exigem qual "Role".

-----

### Links Úteis para Aprofundamento

  * [Spring Boot](https://spring.io/projects/spring-boot)
  * [Spring Security](https://spring.io/projects/spring-security)
  * [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
  * [JSON Web Tokens (JWT)](https://jwt.io/)
  * [Lombok](https://projectlombok.org/)
