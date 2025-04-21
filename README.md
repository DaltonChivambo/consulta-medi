

```markdown
# 🏥 Sistema de Agendamento de Consultas Médicas Online

Sistema de Agendamento de Consultas Médicas Online utilizando **Java 8**, **Spring Boot**, **MongoDB** e **CORBA** (JacORB). Esta aplicação permite agendar, cancelar, visualizar e gerenciar consultas de forma eficiente por meio de uma interface web.

---

## 📋 Requisitos

- **Java** 8 (com suporte ao JacORB - CORBA puro)
- **Maven** 3.6 ou superior
- **MongoDB** instalado e rodando (`localhost:27017`)
- **Terminal** ou console para execução dos comandos

---

## 🗂 Estrutura do Projeto

```
consulta-medi/
├── ConsultaMedica.idl         # Interface CORBA
├── pom.xml                    # Configurações do Maven
├── README.md                  # Documentação do projeto
├── target/                    # Build da aplicação
│   └── consulta-medica-1.0-SNAPSHOT.jar
├── database/                  # Arquivos do banco de dados.
│   └── consulta_medica/       # Pasta com arquivos JSON para importação
│       ├── consulta_medica.consultas.json   # Dados das consultas
│       ├── consulta_medica.medicos.json     # Dados dos médicos
│       ├── consulta_medica.pacientes.json   # Dados dos pacientes
│       ├── consulta_medica.usuarios.json    # Dados dos usuários
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/example/   # Código fonte (server, controller, model, etc.)
│   │   │   └── ConsultaApp/   # Código gerado pelo compilador IDL
│   │   └── resources/
│   │       ├── application.properties
│   │       └── templates/     # HTMLs com Thymeleaf
```

---

## ⚙️ Configuração e Execução

### 1. Inicie o MongoDB

Certifique-se de que o MongoDB está em execução:

```bash
sudo systemctl start mongod
```

### 2. Importe os Dados para o MongoDB

Os arquivos JSON na pasta `database/consulta_medica/` contêm dados pré-configurados do banco de dados `consulta_medica`. Esses arquivos permitem que qualquer pessoa iniciando o projeto popule rapidamente o banco de dados MongoDB com dados de exemplo. Para importar os dados, utilize o comando `mongoimport`:

```bash
mongoimport --db consulta_medica --collection consultas --file database/consulta_medica/consulta_medica.consultas.json --jsonArray
mongoimport --db consulta_medica --collection medicos --file database/consulta_medica/consulta_medica.medicos.json --jsonArray
mongoimport --db consulta_medica --collection pacientes --file database/consulta_medica/consulta_medica.pacientes.json --jsonArray
mongoimport --db consulta_medica --collection usuarios --file database/consulta_medica/consulta_medica.usuarios.json --jsonArray
```

> **Nota**: Certifique-se de que o MongoDB está rodando antes de executar os comandos. Os comandos acima criam o banco de dados `consulta_medica` e as coleções correspondentes (`consultas`, `medicos`, `pacientes`, `usuarios`) automaticamente.
> **Ou pode usar o mongoDB Compass para importar os arquivos Json.

### 3. Inicie o Serviço de Nomes CORBA (orbd)

```bash
orbd -ORBInitialPort 1050 &
```

### 4. Compile os Arquivos IDL (CORBA)

Utilize o compilador `idlj` do Java 8 para gerar os arquivos Java a partir do IDL:

```bash
idlj -fall ConsultaMedica.idl
```

Isso criará o pacote `ConsultaApp` em `src/main/java/`, com os seguintes arquivos:

- `ConsultaMedica.java`: Interface CORBA principal.
- `ConsultaMedicaHelper.java`: Classe auxiliar para manipulação da interface.
- `ConsultaMedicaHolder.java`: Wrapper para passagem por referência.
- `_ConsultaMedicaStub.java`: Cliente stub que realiza as chamadas remotas.
- `_ConsultaMedicaImplBase.java`: Classe base que o servidor Java estende.

Certifique-se de mover a pasta `ConsultaApp/` gerada para `src/main/java/`.

### 5. Compile o Projeto com Maven

```bash
mvn clean install
```

### 6. Inicie o Servidor CORBA

```bash
java -cp target/classes com.example.server.ConsultaMedicaServer -ORBInitialPort 1050 &
```

Você verá a mensagem:

```
Servidor ConsultaMedica pronto e aguardando requisições...
```

### 7. Inicie a Aplicação Web (Spring Boot)

```bash
mvn spring-boot:run
```

Acesse: [http://localhost:8080](http://localhost:8080)  
Ou: `http://<ip_local>:8080`

---

## 📂 Dados do Banco de Dados

O projeto utiliza o **MongoDB** com o banco de dados `consulta_medica`. Para facilitar a inicialização do projeto, os dados de exemplo foram exportados para arquivos JSON na pasta `database/consulta_medica/`. Esses arquivos permitem que qualquer desenvolvedor popule o banco de dados com dados iniciais, incluindo consultas, médicos, pacientes e usuários. Abaixo está a descrição de cada arquivo:

- **`consulta_medica.consultas.json`**: Contém registros de consultas médicas, com informações como ID da consulta, paciente, médico, especialidade, data, hora e status (ex.: "CONCLUIDA", "CANCELADA").
- **`consulta_medica.medicos.json`**: Armazena dados dos médicos, incluindo nome, CRM, especialidade, email, telefone e status de ativação.
- **`consulta_medica.pacientes.json`**: Inclui informações dos pacientes, como nome, CPF, email, telefone e data de nascimento.
- **`consulta_medica.usuarios.json`**: Registra os usuários do sistema (administradores, médicos e pacientes), com detalhes como nome, email, senha criptografada, tipo de usuário (ADMIN, MEDICO, PACIENTE) e status de ativação.

Para importar esses dados, siga as instruções na seção **"Importe os Dados para o MongoDB"** acima. Esses arquivos garantem que o projeto comece com um conjunto de dados consistente para testes e desenvolvimento.

- ou importe usando o MongoDB Compass (GUI)

---

## ✅ Funcionalidades

- 📅 **Agendamento de Consultas**  
  Usuários podem selecionar médicos e horários para marcar consultas.

- ❌ **Cancelamento de Consultas**  
  Permite ao paciente ou medicos cancelar consultas agendadas.

- 🔄 **Atualização de Status da Consulta**  
  O médico pode atualizar o status (Concluida, Cancelada, etc.).

- 🔍 **Visualização de Consultas por Paciente**  
  O paciente visualiza suas consultas agendadas e seu histórico.

- 👨‍⚕️ **Visualização de Consultas por Médico**  
  Médicos acessam sua agenda de atendimentos.

- 👥 **Gerenciamento de Médicos e Pacientes**  
  Inclusão, edição, exclusão e visualização de dados.

- 🚫 **Bloqueio de Médicos**  
  Médicos podem ser bloqueados - inibidos de acessar o sistema

---

## 🧱 Arquitetura

- **Frontend**: Thymeleaf + HTML
- **Backend**: Spring Boot (Java 8)
- **Banco de Dados**: MongoDB
- **Comunicação**: CORBA (JacORB - Java 8 nativo)

---

## 📦 Dependências

- Spring Boot 2.7.0
- Spring Data MongoDB
- Thymeleaf
- Lombok

---

## 🧪 Ambiente Testado

Este sistema foi testado e validado com sucesso em:

- ✅ **Ubuntu 24.04 LTS**
- ✅ **Java 8 com JacORB**
- ✅ **MongoDB 7.x**

---

## 🌐 API REST (Principais Endpoints)

> Base URL: `http://localhost:8080/api`

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET    | `/consultas` | Lista todas as consultas |
| GET    | `/consultas/{id}` | Consulta específica por ID |
| POST   | `/consultas` | Agendamento de nova consulta |
| DELETE | `/consultas/{id}` | Cancela uma consulta |

Outros endpoints podem ser explorados com ferramentas como Postman ou Swagger (se habilitado).

---

## 🛠 Solução de Problemas

### ❗ Falha ao atualizar consulta

1. Verifique se o serviço **orbd** está ativo:
   ```bash
   ps aux | grep orbd
   ```
2. Verifique se o servidor CORBA foi iniciado:
   ```bash
   ps aux | grep ConsultaMedicaServer
   ```
3. Reinicie os serviços na ordem: `orbd → servidor CORBA → aplicação Spring Boot`

### ❗ Erro de conexão com MongoDB

Verifique:

- Se o MongoDB está em execução:  
  `sudo systemctl status mongod`
- Se a URL está correta no arquivo `application.properties`:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/consulta_db
```

---

## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch: `feature/sua-feature`
3. Commit e push das alterações
4. Abra um Pull Request

---

## 📄 Licença

Distribuído sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.
```
