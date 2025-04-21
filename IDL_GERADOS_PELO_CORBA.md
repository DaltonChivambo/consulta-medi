# Detalhamento Completo dos Arquivos do Pacote ConsultaApp

## 1. Consulta.java
Este arquivo contém a definição da estrutura de dados principal do sistema.

```java
public final class Consulta implements org.omg.CORBA.portable.IDLEntity {
    // Campos públicos que representam os dados da consulta
    public String id;              // Identificador único da consulta
    public String pacienteNome;    // Nome do paciente
    public String medicoNome;      // Nome do médico
    public String data;            // Data da consulta
    public String hora;            // Hora da consulta
    public String status;          // Status atual da consulta
    public String especialidade;   // Especialidade médica
    public String observacoes;     // Observações adicionais

    // Construtor padrão necessário para CORBA
    public Consulta() {}

    // Construtor completo para inicialização
    public Consulta(String id, String pacienteNome, String medicoNome,
                   String data, String hora, String status,
                   String especialidade, String observacoes) {
        this.id = id;
        this.pacienteNome = pacienteNome;
        this.medicoNome = medicoNome;
        this.data = data;
        this.hora = hora;
        this.status = status;
        this.especialidade = especialidade;
        this.observacoes = observacoes;
    }
}
```

**Características Importantes:**
- É uma classe final para evitar extensão
- Implementa `IDLEntity` para serialização CORBA
- Campos são públicos para permitir acesso direto
- Possui dois construtores: um vazio e um completo

## 2. ConsultaHelper.java
Classe auxiliar que fornece métodos para manipulação da estrutura Consulta.

```java
public class ConsultaHelper {
    // Identificador do tipo CORBA
    private static final String _id = "IDL:ConsultaApp/Consulta:1.0";
    
    // Método para inserir Consulta em um Any CORBA
    public static void insert(org.omg.CORBA.Any a, Consulta that) {
        org.omg.CORBA.portable.OutputStream out = a.create_output_stream();
        a.type(type());
        write(out, that);
        a.read_value(out.create_input_stream(), type());
    }
    
    // Método para extrair Consulta de um Any CORBA
    public static Consulta extract(org.omg.CORBA.Any a) {
        return read(a.create_input_stream());
    }
    
    // Método para obter o TypeCode da estrutura
    public static org.omg.CORBA.TypeCode type() {
        // Implementação do TypeCode
    }
    
    // Método para serializar Consulta
    public static void write(org.omg.CORBA.portable.OutputStream o, Consulta v) {
        o.write_string(v.id);
        o.write_string(v.pacienteNome);
        o.write_string(v.medicoNome);
        o.write_string(v.data);
        o.write_string(v.hora);
        o.write_string(v.status);
        o.write_string(v.especialidade);
        o.write_string(v.observacoes);
    }
    
    // Método para deserializar Consulta
    public static Consulta read(org.omg.CORBA.portable.InputStream i) {
        Consulta v = new Consulta();
        v.id = i.read_string();
        v.pacienteNome = i.read_string();
        v.medicoNome = i.read_string();
        v.data = i.read_string();
        v.hora = i.read_string();
        v.status = i.read_string();
        v.especialidade = i.read_string();
        v.observacoes = i.read_string();
        return v;
    }
}
```

**Funcionalidades Principais:**
- Serialização/deserialização de objetos Consulta
- Conversão entre tipos CORBA
- Gerenciamento de TypeCode
- Manipulação de streams CORBA

## 3. ConsultaHolder.java
Classe para passagem de parâmetros out/inout em chamadas CORBA.

```java
public final class ConsultaHolder implements org.omg.CORBA.portable.Streamable {
    // Valor mantido pelo holder
    public Consulta value;
    
    // Construtor padrão
    public ConsultaHolder() {
        value = null;
    }
    
    // Construtor com valor inicial
    public ConsultaHolder(Consulta initial) {
        value = initial;
    }
    
    // Implementação da interface Streamable
    public void _read(org.omg.CORBA.portable.InputStream i) {
        value = ConsultaHelper.read(i);
    }
    
    public void _write(org.omg.CORBA.portable.OutputStream o) {
        ConsultaHelper.write(o, value);
    }
    
    public org.omg.CORBA.TypeCode _type() {
        return ConsultaHelper.type();
    }
}
```

**Uso Principal:**
- Passagem de parâmetros por referência
- Modificação de valores durante chamadas remotas
- Suporte a parâmetros out/inout

## 4. ConsultaMedica.java
Interface principal que define o contrato do serviço CORBA.

```java
public interface ConsultaMedica extends ConsultaMedicaOperations,
    org.omg.CORBA.Object, org.omg.CORBA.portable.IDLEntity {
    // Interface vazia que herda funcionalidades de:
    // - ConsultaMedicaOperations: métodos do serviço
    // - CORBA.Object: funcionalidades básicas CORBA
    // - IDLEntity: serialização
}
```

**Características:**
- Define o contrato do serviço
- Herda funcionalidades CORBA essenciais
- Serve como ponto de entrada para o serviço

## 5. ConsultaMedicaOperations.java
Interface que define os métodos disponíveis no serviço.

```java
public interface ConsultaMedicaOperations {
    // Agendamento de consulta com ID específico
    Consulta agendarConsultaComId(
        String consultaId,
        String pacienteId,
        String pacienteNome,
        String medicoId,
        String medicoNome,
        String data,
        String hora,
        String especialidade,
        String status
    );
    
    // Cancelamento de consulta
    boolean cancelarConsulta(String consultaId);
    
    // Consulta de dados
    Consulta getConsulta(String consultaId);
}
```

**Métodos Disponíveis:**
- `agendarConsultaComId`: Cria nova consulta
- `cancelarConsulta`: Cancela consulta existente
- `getConsulta`: Recupera dados de uma consulta

## 6. ConsultaMedicaPOA.java
Classe base para implementação do servidor usando POA (Portable Object Adapter).

```java
public abstract class ConsultaMedicaPOA extends org.omg.PortableServer.Servant
    implements ConsultaMedicaOperations, org.omg.CORBA.portable.InvokeHandler {
    
    // Métodos abstratos a serem implementados
    public abstract Consulta agendarConsultaComId(
        String consultaId, String pacienteId, String pacienteNome,
        String medicoId, String medicoNome, String data,
        String hora, String especialidade, String status
    );
    
    public abstract boolean cancelarConsulta(String consultaId);
    public abstract Consulta getConsulta(String consultaId);
    
    // Método de invocação de operações
    public org.omg.CORBA.portable.OutputStream _invoke(
        String method,
        org.omg.CORBA.portable.InputStream in,
        org.omg.CORBA.portable.ResponseHandler handler) {
        // Implementação do roteamento de métodos
    }
}
```

**Características:**
- Define interface para implementação do servidor
- Gerencia invocações de métodos remotos
- Usa POA para adaptação de objetos

## 7. ConsultaMedicaPOATie.java
Classe para implementação do servidor usando o padrão Tie.

```java
public class ConsultaMedicaPOATie extends ConsultaMedicaPOA {
    private ConsultaMedicaOperations _delegate;
    
    public ConsultaMedicaPOATie() {
        _delegate = null;
    }
    
    public ConsultaMedicaPOATie(ConsultaMedicaOperations delegate) {
        _delegate = delegate;
    }
    
    public ConsultaMedicaOperations _delegate() {
        return _delegate;
    }
    
    public void _delegate(ConsultaMedicaOperations delegate) {
        _delegate = delegate;
    }
    
    // Implementação dos métodos delegando para o delegate
    public Consulta agendarConsultaComId(/*...*/) {
        return _delegate.agendarConsultaComId(/*...*/);
    }
    
    public boolean cancelarConsulta(String consultaId) {
        return _delegate.cancelarConsulta(consultaId);
    }
    
    public Consulta getConsulta(String consultaId) {
        return _delegate.getConsulta(consultaId);
    }
}
```

**Uso Principal:**
- Implementação alternativa do servidor
- Permite herança múltipla
- Separa interface de implementação

## 8. _ConsultaMedicaStub.java
Implementação do cliente (stub) para comunicação com o servidor.

```java
public class _ConsultaMedicaStub extends org.omg.CORBA.portable.ObjectImpl
    implements ConsultaMedica {
    
    // Identificador do serviço
    private static final String _type_ids[] = {
        "IDL:ConsultaApp/ConsultaMedica:1.0"
    };
    
    // Implementação dos métodos remotos
    public Consulta agendarConsultaComId(/*...*/) {
        // Serialização dos parâmetros
        // Chamada remota
        // Deserialização do resultado
    }
    
    public boolean cancelarConsulta(String consultaId) {
        // Implementação similar
    }
    
    public Consulta getConsulta(String consultaId) {
        // Implementação similar
    }
    
    // Métodos de suporte CORBA
    public String[] _ids() {
        return _type_ids.clone();
    }
}
```

**Funcionalidades:**
- Representa o serviço no cliente
- Gerencia comunicação remota
- Serializa/deserializa parâmetros

## Fluxo de Comunicação

1. **Cliente:**
   - Usa `_ConsultaMedicaStub` para fazer chamadas
   - Serializa parâmetros usando `ConsultaHelper`
   - Recebe e deserializa respostas

2. **Servidor:**
   - Implementa `ConsultaMedicaPOA` ou usa `ConsultaMedicaPOATie`
   - Processa chamadas remotas
   - Retorna resultados serializados

3. **Dados:**
   - Estrutura `Consulta` representa os dados
   - `ConsultaHolder` permite passagem por referência
   - Helpers gerenciam serialização

## Boas Práticas de Uso

1. **No Cliente:**
   - Sempre trate exceções CORBA
   - Use holders para parâmetros out/inout
   - Verifique resultados nulos

2. **No Servidor:**
   - Implemente todos os métodos abstratos
   - Valide parâmetros de entrada
   - Retorne exceções apropriadas

3. **Geral:**
   - Mantenha versões do IDL sincronizadas
   - Documente mudanças na interface
   - Teste comunicação isoladamente 
