# 🏥 Sistema de Consultas Médicas

Sistema de gerenciamento de consultas médicas utilizando Java, Spring Boot, MongoDB e CORBA, Java 8. Esta aplicação permite agendar, cancelar e visualizar consultas de forma eficiente por meio de uma interface web.

---

## 📋 Requisitos

- **Java** 8 (com suporte nativo a CORBA)  
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
├── src/
│   ├── main/
│   │   ├── java/com/example/  # Código fonte (server, controller, model, etc.)
│   │   ├── resources/
│   │   │   ├── application.properties
│   │   │   └── templates/     # HTMLs com Thymeleaf
```

---

## ⚙️ Configuração e Execução

### 1. Suba o MongoDB

Certifique-se de que o MongoDB está em execução no host padrão:

```bash
sudo systemctl start mongod
```

---

### 2. Inicie o Serviço de Nomes CORBA (orbd)

```bash
orbd -ORBInitialPort 1050 &
```

---

### 3. Compile o Projeto

```bash
mvn clean install
```

---

### 4. Inicie o Servidor CORBA

```bash
java -cp target/classes com.example.server.ConsultaMedicaServer -ORBInitialPort 1050 &
```

Você verá a mensagem:

```
Servidor ConsultaMedica pronto e aguardando requisições...
```

---

### 5. Inicie a Aplicação Web (Spring Boot)

```bash
mvn spring-boot:run
```

Acesse: [http://localhost:8080](http://localhost:8080)
ou (http://ip_do_seu_dispositivo_na_rede:8080)

---

## ✅ Funcionalidades

- 📅 Agendamento de consultas  
- ❌ Cancelamento de consultas  
- 🔄 Atualização de status  
- 🔍 Visualização por paciente  
- 👨‍⚕️ Visualização por médico  
- 👥 Gerenciamento de médicos e pacientes  

---

## 🧱 Arquitetura

- **Frontend**: Thymeleaf + HTML  
- **Backend**: Spring Boot (Java)  
- **Banco de Dados**: MongoDB  
- **Comunicação**: CORBA puro (Java 8)  

---

## 📦 Dependências

- Spring Boot 2.7.0  
- Spring Data MongoDB  
- Thymeleaf  
- Lombok  

---

## 🛠 Solução de Problemas

### ❗ Falha ao atualizar consulta

Verifique:
1. Se o serviço **orbd** está ativo:  
   `ps aux | grep orbd`
2. Se o servidor CORBA foi iniciado:  
   `ps aux | grep ConsultaMedicaServer`
3. Reinicie os serviços na ordem: orbd → servidor → aplicação

---

> 🧪 Testado e validado no **Ubuntu 24.04 LTS**



### ❗ Erro de conexão com MongoDB

Confirme:
- Se o MongoDB está rodando  
- Se a URL está correta no `application.properties`:  
  ```
  spring.data.mongodb.uri=mongodb://localhost:27017/consulta_db
  ```

---

## 🤝 Contribuição

1. Fork o projeto  
2. Crie uma branch: `feature/sua-feature`  
3. Commit e push das alterações  
4. Abra um Pull Request  

---

## 📄 Licença

Distribuído sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.


