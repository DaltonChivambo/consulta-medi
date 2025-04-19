# Sistema de Consultas Médicas

Este é um sistema de gerenciamento de consultas médicas desenvolvido em Java utilizando Spring Boot, MongoDB e CORBA.

## Requisitos do Sistema

- Java 8 ou superior
- Maven 3.6 ou superior
- MongoDB instalado e rodando
- Terminal/Console para executar comandos

## Estrutura do Projeto

O projeto é dividido em três componentes principais:

1. **Servidor CORBA (ConsultaMedicaServer)**: Responsável por gerenciar as consultas médicas
2. **Serviço de Nomes CORBA (orbd)**: Necessário para registro e descoberta de serviços
3. **Aplicação Web (Spring Boot)**: Interface web para interação com o sistema

## Configuração e Execução

### 1. Iniciar o MongoDB

Certifique-se que o MongoDB está instalado e rodando. Por padrão, o sistema espera que o MongoDB esteja rodando em `localhost:27017`.

### 2. Iniciar o Serviço de Nomes CORBA (orbd)

Execute o seguinte comando em um terminal:

```bash
orbd -ORBInitialPort 1050 &
```

Este comando inicia o serviço de nomes CORBA na porta 1050.

### 3. Iniciar o Servidor de Consultas

Em um novo terminal, execute:

```bash
java -cp target/classes com.example.server.ConsultaMedicaServer -ORBInitialPort 1050 &
```

Você deve ver a mensagem "Servidor ConsultaMedica pronto e aguardando requisições..." quando o servidor estiver pronto.

### 4. Iniciar a Aplicação Web

Em um novo terminal, execute:

```bash
mvn spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`

## Funcionalidades

- Agendamento de consultas
- Cancelamento de consultas
- Atualização de status de consultas
- Visualização de consultas por paciente
- Visualização de consultas por médico
- Gerenciamento de médicos e pacientes

## Arquitetura

O sistema utiliza uma arquitetura distribuída com:

- **Frontend**: Thymeleaf (templates HTML)
- **Backend**: Spring Boot
- **Banco de Dados**: MongoDB
- **Comunicação Distribuída**: CORBA

## Dependências Principais

- Spring Boot 2.7.0
- Spring Data MongoDB
- GlassFish CORBA ORB
- Lombok
- Thymeleaf

## Solução de Problemas

### Erro "Falha ao atualizar consulta"

Este erro geralmente ocorre quando:
1. O serviço de nomes CORBA (orbd) não está rodando
2. O servidor de consultas não está rodando
3. Há problemas de conexão entre os componentes

Para resolver:
1. Verifique se o orbd está rodando: `ps aux | grep orbd`
2. Verifique se o servidor de consultas está rodando: `ps aux | grep ConsultaMedicaServer`
3. Se necessário, reinicie os serviços na ordem correta

### Erro de Conexão com MongoDB

Verifique se:
1. O MongoDB está instalado e rodando
2. A conexão está configurada corretamente no `application.properties`

## Contribuição

Para contribuir com o projeto:
1. Faça um fork do repositório
2. Crie uma branch para sua feature
3. Faça commit das suas alterações
4. Push para a branch
5. Abra um Pull Request

## Licença

Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes. 