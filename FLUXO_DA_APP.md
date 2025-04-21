# Fluxo da Aplicação - Sistema de Consultas Médicas

## Visão Geral da Arquitetura

O sistema utiliza uma arquitetura distribuída baseada em CORBA, com Spring Boot para a interface web e MongoDB para persistência de dados. O fluxo completo da aplicação segue o padrão:

```
Cliente (Browser) → Spring Controller → ConsultaService → CORBA → MongoDB
  ↓                    ↓                  ↓              ↓         ↓
HTML Form    ConsultaController    ConsultaService   ConsultaMedicaServer  Dados Persistidos
```

## 1. Fluxo de Agendamento de Consulta

### 1.1 Cliente → Controller
```java
// ConsultaController.java
@PostMapping
public String salvarConsulta(@ModelAttribute Consulta consulta, HttpSession session) {
    // Validação da sessão
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || !"PACIENTE".equals(usuario.getTipo())) {
        return "redirect:/auth/login";
    }
    
    // Processamento da consulta
    consulta.setPacienteId(usuario.getUsuarioId());
    consultaService.agendarConsulta(consulta);
    return "redirect:/consultas/minhas";
}
```

### 1.2 Controller → Service
```java
// ConsultaService.java
public void agendarConsulta(Consulta consulta) {
    // 1. Validação no MongoDB
    PacienteMongo pacienteMongo = pacienteMongoService.findById(consulta.getPacienteId())
        .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
    
    MedicoMongo medicoMongo = medicoMongoService.findById(consulta.getMedicoId())
        .orElseThrow(() -> new RuntimeException("Médico não encontrado"));
    
    // 2. Geração de ID
    String consultaId = "C" + System.currentTimeMillis();
    
    // 3. Chamada CORBA
    ConsultaApp.Consulta consultaCorba = getConsultaMedica().agendarConsultaComId(
        consultaId,
        pacienteMongo.getId(),
        pacienteMongo.getNome(),
        medicoMongo.getId(),
        medicoMongo.getNome(),
        consulta.getData(),
        consulta.getHora(),
        medicoMongo.getEspecialidade(),
        "AGENDADA"
    );
    
    // 4. Persistência no MongoDB
    ConsultaMongo consultaMongo = new ConsultaMongo(
        consultaId,
        consulta.getPacienteId(),
        pacienteMongo.getNome(),
        consulta.getMedicoId(),
        medicoMongo.getNome(),
        medicoMongo.getEspecialidade(),
        data,
        hora,
        "AGENDADA"
    );
    consultaMongoService.save(consultaMongo);
}
```

### 1.3 Service → CORBA
```java
// ConsultaMedicaServer.java (Implementação CORBA)
class ConsultaMedicaImpl extends ConsultaMedicaPOA {
    @Override
    public Consulta agendarConsultaComId(String consultaId, String pacienteId, 
        String pacienteNome, String medicoId, String medicoNome, 
        String data, String hora, String especialidade, String status) {
        
        // Criação do objeto Consulta CORBA
        Consulta consulta = new Consulta();
        consulta.id = consultaId;
        consulta.pacienteNome = pacienteNome;
        consulta.medicoNome = medicoNome;
        consulta.data = data;
        consulta.hora = hora;
        consulta.status = status;
        consulta.especialidade = especialidade;
        
        // Armazenamento local (memória)
        consultas.put(consultaId, consulta);
        
        return consulta;
    }
}
```

### 1.4 CORBA → MongoDB
```java
// ConsultaMongoService.java
@Service
public class ConsultaMongoService {
    @Autowired
    private MongoRepository<ConsultaMongo, String> repository;
    
    public ConsultaMongo save(ConsultaMongo consulta) {
        return repository.save(consulta);
    }
}
```

## 2. Fluxo de Consulta de Dados

```
Cliente → Controller → Service → CORBA/MongoDB → Resposta
  ↓         ↓          ↓           ↓              ↓
GET      @GetMapping  getConsulta  Busca Dados    Renderiza View
```

### 2.1 Consulta de Dados
```java
// ConsultaService.java
public Consulta getConsulta(String id) {
    // 1. Busca no MongoDB
    Optional<ConsultaMongo> mongoConsultaOpt = consultaMongoService.findById(id);
    if (mongoConsultaOpt.isPresent()) {
        ConsultaMongo mongoConsulta = mongoConsultaOpt.get();
        
        // 2. Busca no CORBA
        try {
            ConsultaApp.Consulta corbaConsulta = getConsultaMedica().getConsulta(id);
            if (corbaConsulta != null) {
                // 3. Combina dados
                return convertToModelConsulta(corbaConsulta, mongoConsulta);
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar no CORBA", e);
        }
        
        // 4. Retorna dados do MongoDB se CORBA falhar
        return convertMongoToModelConsulta(mongoConsulta);
    }
    return null;
}
```

## 3. Fluxo de Atualização de Status

```
Cliente → Controller → Service → CORBA → MongoDB → Confirmação
  ↓         ↓          ↓         ↓        ↓          ↓
PUT      @PutMapping  update    update   update     Response
```

### 3.1 Atualização de Status
```java
// ConsultaService.java
public void atualizarConsulta(Consulta consulta) {
    // 1. Atualiza no CORBA
    ConsultaApp.Consulta corbaConsulta = getConsultaMedica()
        .atualizarConsulta(consulta.getId(), consulta.getStatus());
    
    // 2. Atualiza no MongoDB
    ConsultaMongo mongoConsulta = consultaMongoService.findById(consulta.getId())
        .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
    mongoConsulta.setStatus(consulta.getStatus());
    consultaMongoService.save(mongoConsulta);
}
```

## 4. Fluxo de Cancelamento

```
Cliente → Controller → Service → CORBA → MongoDB → Confirmação
  ↓         ↓          ↓         ↓        ↓          ↓
POST     @PostMapping cancel    cancel   update     Response
```

### 4.1 Cancelamento de Consulta
```java
// ConsultaService.java
public boolean cancelarConsulta(String id) {
    // 1. Cancela no CORBA
    boolean cancelado = getConsultaMedica().cancelarConsulta(id);
    
    if (cancelado) {
        // 2. Atualiza status no MongoDB
        ConsultaMongo consulta = consultaMongoService.findById(id)
            .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
        consulta.setStatus("CANCELADA");
        consultaMongoService.save(consulta);
        return true;
    }
    return false;
}
```

## 5. Pontos Importantes do Fluxo

### 5.1 Ordem de Operações
- Validação primeiro no MongoDB
- Operação via CORBA
- Persistência final no MongoDB

### 5.2 Tratamento de Erros
- Verificação de respostas CORBA
- Rollback em caso de falha
- Logging detalhado

### 5.3 Sincronização de Dados
- CORBA como sistema de distribuição
- MongoDB como fonte primária
- Verificação de consistência

### 5.4 Segurança
- Validação de sessão
- Verificação de permissões
- Proteção contra acesso não autorizado

## 6. Benefícios da Arquitetura

Esta arquitetura garante:
- Consistência dos dados
- Alta disponibilidade
- Distribuição eficiente
- Persistência confiável
- Tratamento adequado de erros

## 7. Componentes Principais

### 7.1 Frontend
- Thymeleaf templates
- Bootstrap para UI
- JavaScript para interatividade

### 7.2 Backend
- Spring Boot
- CORBA para distribuição
- MongoDB para persistência

### 7.3 Comunicação
- HTTP para web
- CORBA para distribuição
- MongoDB para dados 
