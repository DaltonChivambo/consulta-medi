# 🏥 Sistema de Agendamento de Consultas Médicas OnlineUm sistema robusto para agendamento de consultas médicas online, desenvolvido com **Java 8**, **Spring Boot**, **MongoDB** e **CORBA** (JacORB). A aplicação permite agendar, cancelar, visualizar e gerenciar consultas de forma eficiente por meio de uma interface web amigável.---\
📋 Requisitos\
\

**Java 8** (com suporte ao JacORB para CORBA)\
**Maven** 3.6 ou superior\
**MongoDB** instalado e executando em `localhost:27017`\
**Terminal** ou console para execução de comandos---\

🗂 Estrutura do Projeto\
```plaintextconsulta-medi/├── ConsultaMedica.idl # Interface CORBA├── pom.xml # Configurações do Maven├── README.md # Documentação do projeto├── target/ # Artefatos gerados pelo build│ └── consulta-medica-1.0-SNAPSHOT.jar├── database/ # Dados exportados do MongoDB│ └── consulta_medica/ # Arquivos JSON para importação│ ├── consulta_medica.consultas.json # Consultas médicas│ ├── consulta_medica.medicos.json # Dados dos médicos│ ├── consulta_medica.pacientes.json # Dados dos pacientes│ ├── consulta_medica.usuarios.json # Dados dos usuários├── src/│ ├── main/│ │ ├── java/│ │ │ ├── com/example/ # Código-fonte (servidor, controladores, modelos, etc.)│ │ │ └── ConsultaApp/ # Código gerado pelo compilador IDL│ │ └── resources/│ │ ├── application.properties # Configurações da aplicação│ │ └── templates/ # Templates HTML (Thymeleaf)

⚙️ Configuração e Execução
1. Inicie o MongoDB
Certifique-se de que o MongoDB está ativo:
sudo systemctl start mongod

Verifique o status:
sudo systemctl status mongod

2. Importe os Dados para o MongoDB
A pasta database/consulta_medica/ contém arquivos JSON com dados pré-configurados do banco de dados consulta_medica. Esses arquivos foram incluídos para que qualquer desenvolvedor iniciando o projeto possa importar facilmente os dados iniciais e começar a usar o sistema. Para importar, use o comando mongoimport:
mongoimport --db consulta_medica --collection consultas --file database/consulta_medica/consulta_medica.consultas.json --jsonArray
mongoimport --db consulta_medica --collection medicos --file database/consulta_medica/consulta_medica.medicos.json --jsonArray
mongoimport --db consulta_medica --collection pacientes --file database/consulta_medica/consulta_medica.pacientes.json --jsonArray
mongoimport --db consulta_medica --collection usuarios --file database/consulta_medica/consulta_medica.usuarios.json --jsonArray


Nota: O MongoDB deve estar em execução. Esses comandos criam automaticamente o banco de dados consulta_medica e as coleções correspondentes (consultas, medicos, pacientes, usuarios).

Alternativamente, você pode importar os arquivos JSON usando o MongoDB Compass:

Abra o MongoDB Compass e conecte-se ao servidor (localhost:27017).
Crie um banco de dados chamado consulta_medica.
Para cada coleção (consultas, medicos, pacientes, usuarios), use a opção "Import Data" e selecione o respectivo arquivo JSON na pasta database/consulta_medica/.
Confirme a importação com a opção JSON Array.

3. Inicie o Serviço de Nomes CORBA (orbd)
orbd -ORBInitialPort 1050 &

4. Compile os Arquivos IDL (CORBA)
Gere os arquivos Java a partir do IDL usando o compilador idlj:
idlj -fall ConsultaMedica.idl

Isso cria o pacote ConsultaApp em src/main/java/ com os seguintes arquivos:

ConsultaMedica.java: Interface principal do CORBA.
ConsultaMedicaHelper.java: Auxiliar para manipulação da interface.
ConsultaMedicaHolder.java: Wrapper para passagem por referência.
_ConsultaMedicaStub.java: Stub para chamadas remotas.
_ConsultaMedicaImplBase.java: Classe base para o servidor.

Mova a pasta ConsultaApp/ gerada para src/main/java/.
5. Compile o Projeto com Maven
mvn clean install

6. Inicie o Servidor CORBA
java -cp target/classes com.example.server.ConsultaMedicaServer -ORBInitialPort 1050 &

Você verá:
Servidor ConsultaMedica pronto e aguardando requisições...

7. Inicie a Aplicação Web (Spring Boot)
mvn spring-boot:run

Acesse a aplicação em: http://localhost:8080 ou http://<ip_local>:8080.

📂 Dados do Banco de Dados
O sistema utiliza o MongoDB com o banco de dados consulta_medica. Para facilitar a configuração inicial, os dados de exemplo foram exportados como arquivos JSON na pasta database/consulta_medica/. Esses arquivos permitem popular o banco de dados com registros de consultas, médicos, pacientes e usuários, garantindo um ambiente pronto para testes e desenvolvimento. Veja o conteúdo de cada arquivo:

consulta_medica.consultas.json: Registros de consultas, incluindo ID, paciente, médico, especialidade, data, hora e status (ex.: "CONCLUÍDA", "CANCELADA").
consulta_medica.medicos.json: Dados dos médicos, com nome, CRM, especialidade, email, telefone e status de ativação.
consulta_medica.pacientes.json: Informações dos pacientes, como nome, CPF, email, telefone e data de nascimento.
consulta_medica.usuarios.json: Usuários do sistema (administradores, médicos e pacientes), com nome, email, senha criptografada, tipo (ADMIN, MEDICO, PACIENTE) e status de ativação.

Para importar esses dados, consulte a seção "Importe os Dados para o MongoDB" acima. Você pode usar o mongoimport ou o MongoDB Compass para carregar os dados.

✅ Funcionalidades

📅 Agendamento de Consultas: Usuários podem selecionar médicos e horários para marcar consultas.
❌ Cancelamento de Consultas: Pacientes ou médicos podem cancelar consultas agendadas.
🔄 Atualização de Status: Médicos podem atualizar o status das consultas (ex.: Concluída, Cancelada).
🔍 Visualização por Paciente: Pacientes acessam suas consultas agendadas e histórico.
👨‍⚕️ Visualização por Médico: Médicos consultam sua agenda de atendimentos.
👥 Gerenciamento de Usuários: Inclusão, edição, exclusão e visualização de médicos e pacientes.
🚫 Bloqueio de Médicos: Médicos podem ser temporariamente desativados, impedindo novos agendamentos.


🧱 Arquitetura

Frontend: Thymeleaf + HTML
Backend: Spring Boot (Java 8)
Banco de Dados: MongoDB
Comunicação: CORBA (JacORB)


📦 Dependências

Spring Boot 2.7.0
Spring Data MongoDB
Thymeleaf
Lombok


🧪 Ambiente Testado

✅ Ubuntu 24.04 LTS
✅ Java 8 com JacORB
✅ MongoDB 7.x


🌐 API REST (Principais Endpoints)
Base URL: http://localhost:8080/api



Método
Endpoint
Descrição



GET
/consultas
Lista todas as consultas


GET
/consultas/{id}
Detalhes de uma consulta por ID


POST
/consultas
Cria uma nova consulta


DELETE
/consultas/{id}
Cancela uma consulta


Use ferramentas como Postman ou Swagger para explorar outros endpoints.

🛠 Solução de Problemas
❗ Falha ao Atualizar Consulta

Verifique se o serviço orbd está ativo:
ps aux | grep orbd


Confirme que o servidor CORBA está rodando:
ps aux | grep ConsultaMedicaServer


Reinicie na ordem: orbd → servidor CORBA → aplicação Spring Boot.


❗ Erro de Conexão com MongoDB

Verifique se o MongoDB está ativo:
sudo systemctl status mongod


Confirme a URL no arquivo application.properties:
spring.data.mongodb.uri=mongodb://localhost:27017/consulta_db




🤝 Contribuição

Faça um fork do repositório.
Crie uma branch: git checkout -b feature/sua-feature.
Commit suas alterações: git commit -m "Adiciona sua feature".
Envie para o repositório: git push origin feature/sua-feature.
Abra um Pull Request.


📄 Licença
Distribuído sob a licença MIT. Veja o arquivo LICENSE para detalhes.


