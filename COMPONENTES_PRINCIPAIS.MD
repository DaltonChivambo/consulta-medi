# Componentes Principais do Sistema de Consultas Médicas

Este documento descreve os componentes principais do sistema de consultas médicas, explicando sua importância e como eles se integram para formar a arquitetura completa.

## 1. ConsultaMedicaServer

O `ConsultaMedicaServer` é o componente central da arquitetura CORBA. Ele implementa a interface definida no arquivo IDL e gerencia as operações distribuídas do sistema.

```java
// ConsultaMedicaServer.java
class ConsultaMedicaImpl extends ConsultaMedicaPOA {
    private Map<String, Consulta> consultas = new HashMap<>();
    private Map<String, Medico> medicos = new HashMap<>();
    private Map<String, Paciente> pacientes = new HashMap<>();

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

**Importância:**
- Implementa a interface CORBA definida no IDL
- Gerencia o estado distribuído do sistema
- Permite comunicação entre componentes distribuídos
- Mantém consistência dos dados entre diferentes instâncias do sistema

## 2. ConsultaMedica.idl

O arquivo IDL (Interface Definition Language) define a estrutura de dados e operações disponíveis no sistema CORBA.

```idl
// ConsultaMedica.idl
module ConsultaApp {
    struct Consulta {
        string id;
        string pacienteNome;
        string medicoNome;
        string data;
        string hora;
        string status; // "AGENDADA", "CONCLUIDA", "CANCELADA"
        string especialidade;
        string observacoes;
    };

    interface ConsultaMedica {
        Consulta agendarConsulta(in string pacienteId, in string medicoId, in string data, in string hora, in string especialidade);
        boolean cancelarConsulta(in string consultaId);
        Consulta getConsulta(in string consultaId);
        ConsultaSeq getConsultasPorPaciente(in string pacienteId);
        ConsultaSeq getConsultasPorMedico(in string medicoId);
        // ... outros métodos
    };
};
```

**Importância:**
- Define a estrutura de dados do sistema
- Especifica as operações disponíveis
- Garante consistência entre diferentes implementações
- Permite geração automática de código cliente/servidor

## 3. CorbaConfig

Este arquivo configura a conexão CORBA no Spring Boot.

```java
// CorbaConfig.java
@Configuration
public class CorbaConfig {
    @Value("${orb.initial.port:1050}")
    private int orbPort;

    @Bean
    public ORB orb() {
        String[] orbArgs = {
            "-ORBInitialPort", "1050",
            "-ORBInitialHost", "localhost"
        };
        return ORB.init(orbArgs, null);
    }

    @Bean
    public NamingContextExt namingContext(ORB orb) {
        try {
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            return NamingContextExtHelper.narrow(objRef);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ConsultaMedica getConsultaMedica(ORB orb, NamingContextExt namingContext) {
        try {
            NameComponent[] name = new NameComponent[] {
                new NameComponent("ConsultaMedica", "")
            };
            org.omg.CORBA.Object objRef = namingContext.resolve(name);
            return ConsultaMedicaHelper.narrow(objRef);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter ConsultaMedica", e);
        }
    }
}
```

**Importância:**
- Configura o ORB (Object Request Broker) CORBA
- Estabelece conexão com o serviço de nomes CORBA
- Obtém referência para o serviço ConsultaMedica
- Integra CORBA com Spring Boot

## 4. ConsultaService

Este serviço coordena as operações entre o Spring Boot, CORBA e MongoDB.

```java
// ConsultaService.java
@Service
public class ConsultaService {
    @Autowired
    private CorbaConfig corbaConfig;
    @Autowired
    private ORB orb;
    @Autowired
    private NamingContextExt namingContext;
    @Autowired
    private ConsultaMongoService consultaMongoService;
    
    private ConsultaMedica consultaMedica;

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        consultaMedica = corbaConfig.getConsultaMedica(orb, namingContext);
    }

    public void agendarConsulta(Consulta consulta) {
        // 1. Validação no MongoDB
        PacienteMongo pacienteMongo = pacienteMongoService.findById(consulta.getPacienteId())
            .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        
        // 2. Chamada CORBA
        ConsultaApp.Consulta consultaCorba = getConsultaMedica().agendarConsultaComId(
            consultaId,
            pacienteMongo.getId(),
            pacienteMongo.getNome(),
            // ... outros parâmetros
        );
        
        // 3. Persistência no MongoDB
        ConsultaMongo consultaMongo = new ConsultaMongo(/* ... */);
        consultaMongoService.save(consultaMongo);
    }
}
```

**Importância:**
- Coordena operações entre diferentes camadas
- Implementa a lógica de negócios
- Gerencia transações entre CORBA e MongoDB
- Trata erros e exceções

## 5. ConsultaController

Controla as requisições HTTP e renderiza as views.

```java
// ConsultaController.java
@Controller
@RequestMapping("/consultas")
public class ConsultaController {
    @Autowired
    private ConsultaService consultaService;
    
    @GetMapping
    public String listarConsultas(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        if ("PACIENTE".equals(usuario.getTipo())) {
            return "redirect:/consultas/minhas";
        } else if ("MEDICO".equals(usuario.getTipo())) {
            return "redirect:/consultas/medico";
        }
        
        List<Consulta> consultas = consultaService.getTodasConsultas();
        model.addAttribute("consultas", consultas);
        return "consultas/lista";
    }

    @PostMapping
    public String salvarConsulta(@ModelAttribute Consulta consulta, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"PACIENTE".equals(usuario.getTipo())) {
            return "redirect:/auth/login";
        }
        
        consulta.setPacienteId(usuario.getUsuarioId());
        consultaService.agendarConsulta(consulta);
        return "redirect:/consultas/minhas";
    }
}
```

**Importância:**
- Gerencia requisições HTTP
- Controla o fluxo de navegação
- Implementa autenticação e autorização
- Renderiza views Thymeleaf

## 6. ConsultaMongoService

Gerencia a persistência de dados no MongoDB.

```java
// ConsultaMongoService.java
@Service
public class ConsultaMongoService {
    @Autowired
    private MongoRepository<ConsultaMongo, String> repository;
    
    public ConsultaMongo save(ConsultaMongo consulta) {
        return repository.save(consulta);
    }
    
    public Optional<ConsultaMongo> findById(String id) {
        return repository.findById(id);
    }
    
    public List<ConsultaMongo> findAll() {
        return repository.findAll();
    }
}
```

**Importância:**
- Gerencia operações de persistência
- Implementa acesso ao MongoDB
- Fornece métodos CRUD para consultas
- Garante consistência dos dados

## 7. Application.java

Ponto de entrada da aplicação Spring Boot.

```java
// Application.java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

**Importância:**
- Inicializa a aplicação Spring Boot
- Configura o contexto da aplicação
- Carrega configurações
- Inicia os componentes do sistema

## Fluxo de Execução Completo

### 1. Inicialização:
- `Application.java` inicia o Spring Boot
- `CorbaConfig` configura o ORB e obtém referência para o serviço CORBA
- `ConsultaService` inicializa a conexão com o CORBA

### 2. Requisição de Agendamento:
- `ConsultaController` recebe a requisição HTTP
- Valida a sessão do usuário
- Chama `ConsultaService.agendarConsulta()`
- `ConsultaService` valida dados no MongoDB
- `ConsultaService` chama o método CORBA via `ConsultaMedicaServer`
- O CORBA processa a requisição e retorna o resultado
- `ConsultaService` persiste os dados no MongoDB
- `ConsultaController` redireciona para a lista de consultas

### 3. Consulta de Dados:
- `ConsultaController` recebe a requisição HTTP
- Chama `ConsultaService.getConsulta()`
- `ConsultaService` busca dados no MongoDB
- `ConsultaService` busca dados complementares no CORBA
- O CORBA retorna os dados solicitados
- `ConsultaService` combina os dados
- `ConsultaController` renderiza a view com os dados

## Arquitetura do Sistema

```
+----------------+     +----------------+     +----------------+
|                |     |                |     |                |
|  Cliente Web   +---->+ Spring Boot    +---->+    CORBA       |
|                |     |                |     |                |
+----------------+     +----------------+     +----------------+
                              |
                              v
                        +----------------+
                        |                |
                        |    MongoDB     |
                        |                |
                        +----------------+
```

### Fluxo de Dados:

1. **Cliente Web → Spring Boot:**
   - O cliente web envia requisições HTTP para o Spring Boot
   - O Spring Boot valida a autenticação e autorização
   - O Spring Boot processa os dados da requisição

2. **Spring Boot → CORBA:**
   - O Spring Boot envia os dados processados para o CORBA
   - O CORBA executa a lógica de negócios distribuída
   - O CORBA retorna o resultado para o Spring Boot

3. **Spring Boot → MongoDB:**
   - O Spring Boot é responsável por persistir os dados no MongoDB
   - O Spring Boot gerencia o ciclo de vida dos dados no MongoDB
   - O Spring Boot valida e sincroniza os dados entre CORBA e MongoDB

4. **CORBA → Spring Boot:**
   - O CORBA processa as operações distribuídas
   - O CORBA retorna os resultados para o Spring Boot
   - O Spring Boot decide como persistir os dados no MongoDB

### Responsabilidades:

1. **Spring Boot:**
   - Gerencia a interface web
   - Controla o fluxo de dados
   - Persiste dados no MongoDB
   - Coordena a comunicação com CORBA

2. **CORBA:**
   - Executa a lógica de negócios distribuída
   - Processa operações em tempo real
   - Mantém o estado distribuído do sistema
   - Retorna resultados para o Spring Boot

3. **MongoDB:**
   - Armazena os dados persistentes
   - É acessado exclusivamente pelo Spring Boot
   - Mantém o histórico e estado do sistema

## Benefícios da Arquitetura

1. **Distribuição:**
   - O CORBA permite que o sistema seja distribuído em múltiplos servidores
   - Alta disponibilidade e escalabilidade

2. **Persistência Confiável:**
   - MongoDB fornece armazenamento robusto e flexível
   - Suporte a consultas complexas e agregações

3. **Interface Web Moderna:**
   - Spring Boot e Thymeleaf fornecem uma interface web responsiva
   - Experiência de usuário intuitiva

4. **Segurança:**
   - Autenticação e autorização em múltiplas camadas
   - Proteção contra acesso não autorizado

5. **Manutenibilidade:**
   - Separação clara de responsabilidades
   - Código modular e bem estruturado 
