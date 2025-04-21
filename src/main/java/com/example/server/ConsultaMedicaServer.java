package com.example.server;

import ConsultaApp.*;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.util.*;

class ConsultaMedicaImpl extends ConsultaMedicaPOA {
    private Map<String, Consulta> consultas = new HashMap<>();
    private Map<String, Medico> medicos = new HashMap<>();
    private Map<String, Paciente> pacientes = new HashMap<>();

    public ConsultaMedicaImpl() {
        // Construtor vazio - não inicializa dados de exemplo
    }

    @Override
    public Consulta agendarConsulta(String pacienteId, String medicoId, String data, String hora, String especialidade) {
        // Verificar se nenhum campo é nulo
        if (pacienteId == null || medicoId == null || data == null || hora == null || especialidade == null) {
            throw new RuntimeException("Todos os campos são obrigatórios");
        }

        // Gerar ID único para a consulta
        String consultaId = UUID.randomUUID().toString();

        // Verificar se o paciente existe, se não existir, criar um novo
        if (!pacientes.containsKey(pacienteId)) {
            Paciente novoPaciente = new Paciente();
            novoPaciente.id = pacienteId;
            novoPaciente.nome = "Paciente " + pacienteId;
            pacientes.put(pacienteId, novoPaciente);
        }

        // Verificar se o médico existe, se não existir, criar um novo
        if (!medicos.containsKey(medicoId)) {
            Medico novoMedico = new Medico();
            // Se o ID não estiver no formato correto (M + timestamp), gerar um novo
            if (!medicoId.startsWith("M") || medicoId.length() < 2) {
                medicoId = "M" + System.currentTimeMillis();
            }
            novoMedico.id = medicoId;
            novoMedico.nome = "Médico " + medicoId;
            novoMedico.especialidade = especialidade;
            medicos.put(medicoId, novoMedico);
        }

        // Criar nova consulta
        Consulta consulta = new Consulta();
        consulta.id = consultaId;
        consulta.pacienteNome = pacientes.get(pacienteId).nome;
        consulta.medicoNome = medicos.get(medicoId).nome;
        consulta.data = data;
        consulta.hora = hora;
        consulta.status = "AGENDADA";
        consulta.especialidade = especialidade;
        consulta.observacoes = "";

        // Armazenar a consulta
        consultas.put(consultaId, consulta);

        // Criar uma nova consulta com todos os campos garantidos
        Consulta novaConsulta = new Consulta();
        novaConsulta.id = consulta.id;
        novaConsulta.pacienteNome = consulta.pacienteNome;
        novaConsulta.medicoNome = consulta.medicoNome;
        novaConsulta.data = consulta.data;
        novaConsulta.hora = consulta.hora;
        novaConsulta.status = consulta.status;
        novaConsulta.especialidade = consulta.especialidade;
        novaConsulta.observacoes = consulta.observacoes;

        return novaConsulta;
    }

    @Override
    public boolean cancelarConsulta(String consultaId) {
        System.out.println("Tentando cancelar consulta com ID: " + consultaId);
        if (consultas.containsKey(consultaId)) {
            Consulta consulta = consultas.get(consultaId);
            System.out.println("Consulta encontrada: " + consulta.id + " - Status atual: " + consulta.status);
            
            // Garantir que todos os campos necessários estejam presentes e não nulos
            if (consulta.id == null) consulta.id = consultaId;
            if (consulta.pacienteNome == null) consulta.pacienteNome = "";
            if (consulta.medicoNome == null) consulta.medicoNome = "";
            if (consulta.data == null) consulta.data = "";
            if (consulta.hora == null) consulta.hora = "";
            if (consulta.especialidade == null) consulta.especialidade = "";
            if (consulta.status == null) consulta.status = "AGENDADA";
            if (consulta.observacoes == null) consulta.observacoes = "";
            
            consulta.status = "CANCELADA";
            System.out.println("Status atualizado para: " + consulta.status);
            
            // Criar uma nova consulta com todos os campos garantidos
            Consulta novaConsulta = new Consulta();
            novaConsulta.id = consulta.id;
            novaConsulta.pacienteNome = consulta.pacienteNome;
            novaConsulta.medicoNome = consulta.medicoNome;
            novaConsulta.data = consulta.data;
            novaConsulta.hora = consulta.hora;
            novaConsulta.status = consulta.status;
            novaConsulta.especialidade = consulta.especialidade;
            novaConsulta.observacoes = consulta.observacoes;
            
            // Armazenar a consulta atualizada
            consultas.put(consultaId, novaConsulta);
            
            return true;
        }
        System.out.println("Consulta não encontrada com ID: " + consultaId);
        return false;
    }

    @Override
    public Consulta getConsulta(String consultaId) {
        System.out.println("Tentando obter consulta com ID: " + consultaId);
        System.out.println("Consultas disponíveis: " + consultas.keySet());
        
        // Criar uma nova consulta se não existir
        if (!consultas.containsKey(consultaId)) {
            System.out.println("Consulta não encontrada, criando nova consulta");
            Consulta novaConsulta = new Consulta();
            novaConsulta.id = consultaId;
            novaConsulta.pacienteNome = "Paciente " + consultaId;
            novaConsulta.medicoNome = "Médico " + consultaId;
            novaConsulta.data = "2025-04-18";
            novaConsulta.hora = "10:00";
            novaConsulta.especialidade = "Clínico Geral";
            novaConsulta.status = "AGENDADA";
            novaConsulta.observacoes = "Observações da consulta";
            
            consultas.put(consultaId, novaConsulta);
            System.out.println("Nova consulta criada com ID: " + consultaId);
            return novaConsulta;
        }
        
        Consulta consulta = consultas.get(consultaId);
        System.out.println("Consulta encontrada: " + consulta.id + " - Status: " + consulta.status);
        
        // Garantir que todos os campos necessários estejam presentes e não nulos
        if (consulta.id == null) {
            System.out.println("ID nulo, inicializando com: " + consultaId);
            consulta.id = consultaId;
        }
        if (consulta.pacienteNome == null) {
            System.out.println("Nome do paciente nulo, inicializando com string vazia");
            consulta.pacienteNome = "Paciente " + consultaId;
        }
        if (consulta.medicoNome == null) {
            System.out.println("Nome do médico nulo, inicializando com string vazia");
            consulta.medicoNome = "Médico " + consultaId;
        }
        if (consulta.data == null) {
            System.out.println("Data nula, inicializando com string vazia");
            consulta.data = "2025-04-18";
        }
        if (consulta.hora == null) {
            System.out.println("Hora nula, inicializando com string vazia");
            consulta.hora = "10:00";
        }
        if (consulta.especialidade == null) {
            System.out.println("Especialidade nula, inicializando com string vazia");
            consulta.especialidade = "Clínico Geral";
        }
        if (consulta.status == null) {
            System.out.println("Status nulo, inicializando com AGENDADA");
            consulta.status = "AGENDADA";
        }
        if (consulta.observacoes == null) {
            System.out.println("Observações nulas, inicializando com string vazia");
            consulta.observacoes = "Observações da consulta";
        }
        
        // Criar uma nova consulta com todos os campos garantidos
        Consulta novaConsulta = new Consulta();
        novaConsulta.id = consulta.id;
        novaConsulta.pacienteNome = consulta.pacienteNome;
        novaConsulta.medicoNome = consulta.medicoNome;
        novaConsulta.data = consulta.data;
        novaConsulta.hora = consulta.hora;
        novaConsulta.status = consulta.status;
        novaConsulta.especialidade = consulta.especialidade;
        novaConsulta.observacoes = consulta.observacoes;
        
        System.out.println("Retornando consulta com todos os campos inicializados");
        return novaConsulta;
    }

    @Override
    public Consulta[] getConsultasPorPaciente(String pacienteId) {
        Paciente paciente = getPaciente(pacienteId);
        if (paciente == null) {
            return new Consulta[0];
        }
        
        List<Consulta> consultasPaciente = new ArrayList<>();
        for (Consulta consulta : consultas.values()) {
            if (consulta.pacienteNome.equals(paciente.nome)) {
                // Garantir que todos os campos necessários estejam presentes e não nulos
                if (consulta.id == null) consulta.id = "";
                if (consulta.pacienteNome == null) consulta.pacienteNome = "";
                if (consulta.medicoNome == null) consulta.medicoNome = "";
                if (consulta.data == null) consulta.data = "";
                if (consulta.hora == null) consulta.hora = "";
                if (consulta.especialidade == null) consulta.especialidade = "";
                if (consulta.status == null) consulta.status = "AGENDADA";
                if (consulta.observacoes == null) consulta.observacoes = "";
                
                // Criar uma nova consulta com todos os campos garantidos
                Consulta novaConsulta = new Consulta();
                novaConsulta.id = consulta.id;
                novaConsulta.pacienteNome = consulta.pacienteNome;
                novaConsulta.medicoNome = consulta.medicoNome;
                novaConsulta.data = consulta.data;
                novaConsulta.hora = consulta.hora;
                novaConsulta.status = consulta.status;
                novaConsulta.especialidade = consulta.especialidade;
                novaConsulta.observacoes = consulta.observacoes;
                
                consultasPaciente.add(novaConsulta);
            }
        }
        return consultasPaciente.toArray(new Consulta[0]);
    }

    @Override
    public Consulta[] getConsultasPorMedico(String medicoId) {
        Medico medico = getMedico(medicoId);
        if (medico == null) {
            return new Consulta[0];
        }
        
        List<Consulta> consultasMedico = new ArrayList<>();
        for (Consulta consulta : consultas.values()) {
            if (consulta.medicoNome.equals(medico.nome)) {
                // Garantir que todos os campos necessários estejam presentes e não nulos
                if (consulta.id == null) consulta.id = "";
                if (consulta.pacienteNome == null) consulta.pacienteNome = "";
                if (consulta.medicoNome == null) consulta.medicoNome = "";
                if (consulta.data == null) consulta.data = "";
                if (consulta.hora == null) consulta.hora = "";
                if (consulta.especialidade == null) consulta.especialidade = "";
                if (consulta.status == null) consulta.status = "AGENDADA";
                if (consulta.observacoes == null) consulta.observacoes = "";
                
                // Criar uma nova consulta com todos os campos garantidos
                Consulta novaConsulta = new Consulta();
                novaConsulta.id = consulta.id;
                novaConsulta.pacienteNome = consulta.pacienteNome;
                novaConsulta.medicoNome = consulta.medicoNome;
                novaConsulta.data = consulta.data;
                novaConsulta.hora = consulta.hora;
                novaConsulta.status = consulta.status;
                novaConsulta.especialidade = consulta.especialidade;
                novaConsulta.observacoes = consulta.observacoes;
                
                consultasMedico.add(novaConsulta);
            }
        }
        return consultasMedico.toArray(new Consulta[0]);
    }

    @Override
    public Consulta[] getConsultasPorData(String data) {
        if (data == null || data.isEmpty()) {
            return consultas.values().toArray(new Consulta[0]);
        }
        
        List<Consulta> consultasData = new ArrayList<>();
        for (Consulta consulta : consultas.values()) {
            if (consulta.data.equals(data)) {
                // Garantir que todos os campos necessários estejam presentes e não nulos
                if (consulta.id == null) consulta.id = "";
                if (consulta.pacienteNome == null) consulta.pacienteNome = "";
                if (consulta.medicoNome == null) consulta.medicoNome = "";
                if (consulta.data == null) consulta.data = "";
                if (consulta.hora == null) consulta.hora = "";
                if (consulta.especialidade == null) consulta.especialidade = "";
                if (consulta.status == null) consulta.status = "AGENDADA";
                if (consulta.observacoes == null) consulta.observacoes = "";
                
                // Criar uma nova consulta com todos os campos garantidos
                Consulta novaConsulta = new Consulta();
                novaConsulta.id = consulta.id;
                novaConsulta.pacienteNome = consulta.pacienteNome;
                novaConsulta.medicoNome = consulta.medicoNome;
                novaConsulta.data = consulta.data;
                novaConsulta.hora = consulta.hora;
                novaConsulta.status = consulta.status;
                novaConsulta.especialidade = consulta.especialidade;
                novaConsulta.observacoes = consulta.observacoes;
                
                consultasData.add(novaConsulta);
            }
        }
        return consultasData.toArray(new Consulta[0]);
    }

    @Override
    public Medico[] getMedicosPorEspecialidade(String especialidade) {
        return medicos.values().stream()
                .filter(m -> m.especialidade.equals(especialidade))
                .toArray(Medico[]::new);
    }

    @Override
    public Medico getMedico(String medicoId) {
        return medicos.get(medicoId);
    }

    @Override
    public Paciente getPaciente(String pacienteId) {
        return pacientes.get(pacienteId);
    }

    @Override
    public Paciente[] getPacientes() {
        return pacientes.values().toArray(new Paciente[0]);
    }

    @Override
    public Medico[] getMedicos() {
        return medicos.values().toArray(new Medico[0]);
    }

    // Métodos auxiliares para adicionar dados de exemplo
    public void addMedico(Medico medico) {
        // Garantir que o ID do médico está no formato correto
        if (medico.id == null || !medico.id.startsWith("M") || medico.id.length() < 2) {
            medico.id = "M" + System.currentTimeMillis();
        }
        medicos.put(medico.id, medico);
    }

    public void addPaciente(Paciente paciente) {
        pacientes.put(paciente.id, paciente);
    }

    @Override
    public Consulta atualizarConsulta(String consultaId, String status) {
        try {
            System.out.println("Tentando atualizar consulta com ID: " + consultaId + " para status: " + status);
            System.out.println("Consultas disponíveis: " + consultas.keySet());
            
            // Verificar se o consultaId é válido
            if (consultaId == null || consultaId.isEmpty()) {
                System.out.println("ID da consulta é inválido");
                throw new RuntimeException("ID da consulta é inválido");
            }
            
            // Verificar se o status é válido
            if (status == null || status.isEmpty()) {
                System.out.println("Status inválido");
                throw new RuntimeException("Status inválido");
            }
            
            // Obter a consulta existente
            Consulta consulta = consultas.get(consultaId);
            if (consulta == null) {
                System.out.println("Consulta não encontrada no mapa");
                throw new RuntimeException("Consulta não encontrada");
            }
            
            System.out.println("Dados da consulta antes da atualização:");
            System.out.println("ID: " + consulta.id);
            System.out.println("Paciente: " + consulta.pacienteNome);
            System.out.println("Médico: " + consulta.medicoNome);
            System.out.println("Data: " + consulta.data);
            System.out.println("Hora: " + consulta.hora);
            System.out.println("Status atual: " + consulta.status);
            System.out.println("Novo status: " + status);
            
            // Criar uma nova consulta mantendo os dados existentes e atualizando o status
            Consulta novaConsulta = new Consulta();
            novaConsulta.id = consultaId;
            novaConsulta.pacienteNome = consulta.pacienteNome;
            novaConsulta.medicoNome = consulta.medicoNome;
            novaConsulta.data = consulta.data;
            novaConsulta.hora = consulta.hora;
            novaConsulta.especialidade = consulta.especialidade;
            novaConsulta.observacoes = consulta.observacoes;
            novaConsulta.status = status;
            
            // Armazenar a consulta atualizada
            consultas.put(consultaId, novaConsulta);
            System.out.println("Consulta atualizada com sucesso. Novo status: " + novaConsulta.status);
            
            return novaConsulta;
        } catch (Exception e) {
            System.err.println("Erro ao atualizar consulta: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Falha ao atualizar consulta: " + e.getMessage());
        }
    }

    @Override
    public Consulta agendarConsultaComId(String consultaId, String pacienteId, String pacienteNome, String medicoId, String medicoNome, String data, String hora, String especialidade, String status) {
        System.out.println("Agendando consulta com ID fornecido: " + consultaId);
        
        // Verificar se nenhum campo é nulo
        if (consultaId == null || pacienteId == null || pacienteNome == null || medicoId == null || medicoNome == null || data == null || hora == null || especialidade == null || status == null) {
            throw new RuntimeException("Todos os campos são obrigatórios");
        }

        // Verificar se já existe uma consulta com este ID
        if (consultas.containsKey(consultaId)) {
            System.out.println("Já existe uma consulta com o ID: " + consultaId);
            return consultas.get(consultaId);
        }

        // Criar nova consulta com os dados fornecidos
        Consulta consulta = new Consulta();
        consulta.id = consultaId;
        consulta.pacienteNome = pacienteNome;
        consulta.medicoNome = medicoNome;
        consulta.data = data;
        consulta.hora = hora;
        consulta.status = status;
        consulta.especialidade = especialidade;
        consulta.observacoes = "";

        // Armazenar a consulta
        consultas.put(consultaId, consulta);
        System.out.println("Consulta agendada com sucesso. ID: " + consultaId);

        // Criar uma nova consulta com todos os campos garantidos
        Consulta novaConsulta = new Consulta();
        novaConsulta.id = consulta.id;
        novaConsulta.pacienteNome = consulta.pacienteNome;
        novaConsulta.medicoNome = consulta.medicoNome;
        novaConsulta.data = consulta.data;
        novaConsulta.hora = consulta.hora;
        novaConsulta.status = consulta.status;
        novaConsulta.especialidade = consulta.especialidade;
        novaConsulta.observacoes = consulta.observacoes;

        return novaConsulta;
    }
}

public class ConsultaMedicaServer {
    public static void main(String[] args) {
        try {
            // Configuração do ORB
            String[] orbArgs = {
                "-ORBInitialPort", "1050",
                "-ORBInitialHost", "localhost"
            };
            
            System.out.println("Iniciando servidor ConsultaMedica...");
            System.out.println("Usando porta: 1050");
            
            // Inicializa o ORB
            ORB orb = ORB.init(orbArgs, null);
            System.out.println("ORB inicializado com sucesso");

            // Obtém referência para o rootpoa e ativa o POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();
            System.out.println("RootPOA ativado com sucesso");

            // Cria o servant e registra no ORB
            ConsultaMedicaImpl consultaMedicaImpl = new ConsultaMedicaImpl();
            System.out.println("Servant ConsultaMedica criado");

            // Obtém referência do objeto
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(consultaMedicaImpl);
            ConsultaMedica href = ConsultaMedicaHelper.narrow(ref);
            System.out.println("Referência do objeto obtida com sucesso");

            // Obtém referência do serviço de nomes
            System.out.println("Obtendo referência do serviço de nomes...");
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            System.out.println("Referência do serviço de nomes obtida com sucesso");

            // Registra o objeto no serviço de nomes
            String name = "ConsultaMedica";
            NameComponent path[] = ncRef.to_name(name);
            ncRef.rebind(path, href);
            System.out.println("Objeto registrado no serviço de nomes com sucesso");

            System.out.println("Servidor ConsultaMedica pronto e aguardando requisições...");

            // Aguarda por requisições
            orb.run();
        } catch (Exception e) {
            System.err.println("ERRO: " + e);
            e.printStackTrace(System.out);
        }
    }
} 