# Apresentação da Aplicação de Consultas Médicas

Este documento apresenta as principais telas da aplicação de consultas médicas, mostrando o fluxo de uso para os diferentes tipos de usuários: pacientes, médicos e administradores.

## Índice

1. [Tela Inicial](#tela-inicial)
2. [Autenticação](#autenticação)
3. [Área do Paciente](#área-do-paciente)
4. [Área do Médico](#área-do-médico)
5. [Área do Administrador](#área-do-administrador)

## Tela Inicial

A tela inicial apresenta a página principal da aplicação, com informações sobre o sistema de consultas médicas.

![Tela Inicial](PrintScreen/000_tela_inicial.png)

## Autenticação

### Login

A tela de login permite que os usuários acessem o sistema com suas credenciais.

![Login](PrintScreen/001_login.png)

### Cadastro

A tela de cadastro permite que novos usuários se registrem no sistema.

![Cadastro](PrintScreen/002_cadastro.png)

## Área do Paciente

### Tela Inicial do Paciente

Após o login, o paciente é direcionado para sua área personalizada.

![Tela Inicial do Paciente](PrintScreen/003_paciente_inicial.png)

### Minhas Consultas

O paciente pode visualizar todas as suas consultas agendadas.

![Minhas Consultas](PrintScreen/004_paciente_minhas_consultas.png)

### Agendamento de Nova Consulta

O paciente pode agendar uma nova consulta médica.

![Nova Consulta](PrintScreen/005_paciente_nova_consulta.png)

### Detalhes da Consulta

O paciente pode visualizar os detalhes de uma consulta específica.

![Detalhes da Consulta](PrintScreen/006_paciente_detalhes_consulta.png)

## Área do Médico

### Tela Inicial do Médico

Após o login, o médico é direcionado para sua área personalizada.

![Tela Inicial do Médico](PrintScreen/007_medico_inicial.png)

### Consultas do Médico

O médico pode visualizar todas as consultas agendadas para ele.

![Consultas do Médico](PrintScreen/008_medico_consultas.png)

### Alteração de Status da Consulta

O médico pode alterar o status de uma consulta (confirmada, realizada, cancelada, etc.).

![Alterar Status da Consulta](PrintScreen/009_medico_alterar_status_consulta.png)

## Área do Administrador

### Tela Inicial do Administrador

Após o login, o administrador é direcionado para sua área de gerenciamento.

![Tela Inicial do Administrador](PrintScreen/010_admin_inicial.png)

### Gerenciamento de Médicos

O administrador pode visualizar e gerenciar todos os médicos cadastrados no sistema.

![Gerenciamento de Médicos](PrintScreen/011_admin_medicos.png)

### Cadastro de Médico

O administrador pode cadastrar novos médicos no sistema.

![Cadastro de Médico](PrintScreen/012_admin_cadastrar_medico.png)

### Detalhes do Médico

O administrador pode visualizar os detalhes de um médico específico.

![Detalhes do Médico](PrintScreen/013_admin_detalhes_medico.png)

### Gerenciamento de Pacientes

O administrador pode visualizar e gerenciar todos os pacientes cadastrados no sistema.

![Gerenciamento de Pacientes](PrintScreen/014_admin_pacientes.png)

### Cadastro/Edição de Paciente

O administrador pode cadastrar ou editar informações de pacientes.

![Edição de Paciente](PrintScreen/015_admin_editar_paciente.png)

### Detalhes do Paciente

O administrador pode visualizar os detalhes de um paciente específico.

![Detalhes do Paciente](PrintScreen/016_admin_detalhes_paciente.png)

## Fluxo de Uso da Aplicação

### Fluxo do Paciente

1. Acessa a tela inicial
2. Faz login ou cadastro
3. Visualiza suas consultas
4. Agenda novas consultas
5. Acompanha o status das consultas

### Fluxo do Médico

1. Acessa a tela inicial
2. Faz login
3. Visualiza suas consultas agendadas
4. Atualiza o status das consultas

### Fluxo do Administrador

1. Acessa a tela inicial
2. Faz login
3. Gerencia médicos (cadastro, edição, visualização)
4. Gerencia pacientes (cadastro, edição, visualização)
5. Monitora o sistema como um todo

## Tecnologias Utilizadas

- **Frontend**: HTML, CSS, JavaScript
- **Backend**: Spring Boot
- **Comunicação Distribuída**: CORBA
- **Banco de Dados**: MongoDB
- **Autenticação**: Spring Security

## Conclusão

A aplicação de consultas médicas oferece uma interface intuitiva e funcional para pacientes, médicos e administradores, facilitando o agendamento e gerenciamento de consultas médicas de forma eficiente e segura. 
