# Vincula POA

Sistema de gestão e acompanhamento de pacientes e unidades de saúde do município de Porto Alegre.

O **Vincula POA** tem como objetivo melhorar a continuidade do cuidado em saúde, identificando pacientes que iniciam atendimentos nas UBS e auxiliando no acompanhamento de demandas, tratamentos e indicadores.

## Arquitetura

```
Frontend (React)  ←→  Backend (Spring Boot API)  ←→  PostgreSQL
```
## Tecnologias utilizadas

### Frontend
- React
- React Router DOM
- Axios
- CSS puro

### Backend
- Java 17+
- Spring Boot
- Spring Security
- JPA / Hibernate
- PostgreSQL
- JWT

## Funcionalidades

- Autenticação de servidores e gerenciamento de perfil
- Controle de perfis de acesso (gestão municipal e usuários comuns)
- Cadastro e listagem de:
  - Usuários
  - UBS (Unidades Básicas de Saúde)
  - Servidores
  - Demandas
- Mapa de UBS
- Indicadores de acompanhamento
- Auditoria de ações do sistema

## Como executar o projeto

### 1. Clonar o repositório
```
git clone https://github.com/lucassoller/vincula-poa.git
```

### 2. Backend

```bash
cd backend
./mvnw spring-boot:run
```

Configurar `application.properties`:

```properties
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/seu_banco
spring.datasource.username=seu_user
spring.datasource.password=sua_senha
frontend.url=http://localhost:5173
mail.token=(gerado no site Resend)
jwt.secret=(texto-chave responsável por criptografar as senhas do usuário, podendo ser qualquer texto)
```

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

ou

```bash
npm start
```

Configurar `.env`:

```env
VITE_API_URL=http://localhost:8080
```

---

## Deploy

- Frontend → Vercel
- Backend → Railway
- Banco → PostgreSQL (Railway)

Atenção: em produção, ajustar URL da API no frontend como VITE_API_URL e configurar as váriaveis do arquivo application.properties do bacnkend

## Autor

**Lucas Soller**

Projeto desenvolvido como sistema fullstack para o Trabalho de Conclusão de Curso da Gradução em Ciência da Computação na UFGRS - 2026

---
