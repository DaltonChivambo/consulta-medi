# CORBA: Arquitetura de Comunicação Distribuída

## Definição

CORBA (Common Object Request Broker Architecture) é uma arquitetura padrão que permite a comunicação entre objetos distribuídos em diferentes linguagens de programação e plataformas. No nosso sistema de consultas médicas, o CORBA é utilizado para implementar a comunicação distribuída entre os componentes do sistema.

## Componentes Principais

### 1. ORB (Object Request Broker)

O ORB (Object Request Broker) é o componente central do CORBA que atua como um middleware de comunicação distribuída. Ele é responsável por:

- **Localização de Objetos:** Encontrar e conectar com objetos remotos em qualquer lugar da rede
- **Marshalling/Unmarshalling:** Converter parâmetros de chamadas de método entre diferentes formatos de dados
- **Roteamento de Requisições:** Encaminhar chamadas de método do cliente para o servidor correto
- **Gerenciamento de Ciclo de Vida:** Controlar a criação, ativação e destruição de objetos remotos
- **Transparência de Invocação:** Esconder a complexidade da comunicação distribuída do desenvolvedor
- **Gerenciamento de Concorrência:** Controlar múltiplas chamadas simultâneas aos objetos

O ORB implementa o padrão de design "Proxy" para criar uma representação local do objeto remoto, permitindo que o cliente chame métodos como se estivesse chamando um objeto local.

```java
@Configuration
public class CorbaConfig {
    @Bean
    public ORB orb() {
        String[] orbArgs = {
            "-ORBInitialPort", "1050",
            "-ORBInitialHost", "localhost"
        };
        return ORB.init(orbArgs, null);
    }
}
```

### 2. IDL (Interface Definition Language)

O IDL define a interface dos objetos distribuídos. No nosso sistema:

```idl
module ConsultaApp {
    struct Consulta {
        string id;
        string pacienteNome;
        string medicoNome;
        string data;
        string hora;
        string status;
        string especialidade;
    };

    interface ConsultaMedica {
        Consulta agendarConsulta(in string pacienteId, in string medicoId, 
                               in string data, in string hora, 
                               in string especialidade);
        boolean cancelarConsulta(in string consultaId);
        Consulta getConsulta(in string consultaId);
    };
};
```

### 3. Serviço de Nomes (Name Service)

O serviço de nomes permite localizar objetos CORBA por nome:

```java
@Bean
public NamingContextExt namingContext(ORB orb) {
    try {
        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
        return NamingContextExtHelper.narrow(objRef);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
```

## Fluxo de Comunicação

### 1. Inicialização
```
Cliente -> ORB -> Serviço de Nomes -> Servidor CORBA
```

### 2. Chamada de Método
```
Cliente -> ORB -> Servidor CORBA -> Processamento -> Resposta -> Cliente
```

## Implementação no Sistema

### 1. Servidor CORBA

```java
class ConsultaMedicaImpl extends ConsultaMedicaPOA {
    private Map<String, Consulta> consultas = new HashMap<>();

    @Override
    public Consulta agendarConsultaComId(String consultaId, String pacienteId, 
        String pacienteNome, String medicoId, String medicoNome, 
        String data, String hora, String especialidade, String status) {
        
        Consulta consulta = new Consulta();
        consulta.id = consultaId;
        consulta.pacienteNome = pacienteNome;
        consulta.medicoNome = medicoNome;
        consulta.data = data;
        consulta.hora = hora;
        consulta.status = status;
        consulta.especialidade = especialidade;
        
        consultas.put(consultaId, consulta);
        return consulta;
    }
}
```

### 2. Cliente CORBA

```java
@Service
public class ConsultaService {
    private ConsultaMedica consultaMedica;

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        consultaMedica = corbaConfig.getConsultaMedica(orb, namingContext);
    }

    public void agendarConsulta(Consulta consulta) {
        // Chamada CORBA
        ConsultaApp.Consulta consultaCorba = consultaMedica.agendarConsultaComId(
            consultaId,
            pacienteMongo.getId(),
            pacienteMongo.getNome(),
            // ... outros parâmetros
        );
    }
}
```

## Benefícios do CORBA

1. **Distribuição:**
   - Permite que o sistema funcione em múltiplos servidores
   - Alta disponibilidade e escalabilidade
   - Balanceamento de carga

2. **Independência de Linguagem:**
   - Os componentes podem ser escritos em diferentes linguagens
   - Facilita a integração com sistemas legados
   - Permite evolução independente dos componentes

3. **Transparência de Localização:**
   - Os clientes não precisam saber onde os objetos estão
   - Facilita a migração de componentes
   - Simplifica a manutenção

4. **Concorrência:**
   - Suporte nativo a operações concorrentes
   - Gerenciamento automático de threads
   - Alta performance em operações distribuídas

## Configuração e Execução

### 1. Requisitos
- JDK 8 ou superior
- ORB (Object Request Broker) implementação
- Serviço de nomes CORBA

### 2. Configuração do ORB
```properties
# application.properties
orb.initial.port=1050
orb.initial.host=localhost
```

### 3. Inicialização do Servidor
```bash
# Iniciar o serviço de nomes
tnameserv -ORBInitialPort 1050

# Iniciar o servidor CORBA
java -jar consulta-medica-server.jar
```

### 4. Inicialização do Cliente
```bash
# Iniciar a aplicação Spring Boot
java -jar consulta-medica-client.jar
```

## Tratamento de Erros

### 1. Verificação de Dados
```java
try {
    // Verifica se o paciente existe
    PacienteMongo paciente = pacienteMongoService.findById(consulta.getPacienteId());
    if (paciente == null) {
        throw new RuntimeException("Paciente não encontrado");
    }
} catch (Exception e) {
    logger.error("Erro ao verificar paciente", e);
    throw new RuntimeException("Erro ao verificar paciente", e);
}
```

### 2. Timeout e Retry
```java
@Retry(maxAttempts = 3, backoff = @Backoff(delay = 1000))
public Consulta getConsulta(String id) {
    return consultaMedica.getConsulta(id);
}
```

### 3. Logging
```java
@Slf4j
public class ConsultaService {
    public void agendarConsulta(Consulta consulta) {
        log.info("Iniciando agendamento via CORBA: {}", consulta);
        // ... implementação
    }
}
```

## Referências

1. [CORBA Specification](https://www.omg.org/spec/CORBA/)
2. [Java IDL Documentation](https://docs.oracle.com/javase/8/docs/technotes/guides/idl/)
3. [Spring CORBA Integration](https://docs.spring.io/spring-framework/docs/current/reference/html/) 
