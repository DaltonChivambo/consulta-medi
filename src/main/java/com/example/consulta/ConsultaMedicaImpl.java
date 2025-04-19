package com.example.consulta;

import ConsultaApp.*;
import org.omg.CORBA.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ConsultaMedicaImpl extends ConsultaMedicaPOA {
    private ORB orb;
    private Map<String, Consulta> consultas = new ConcurrentHashMap<>();
    private Map<String, Medico> medicos = new ConcurrentHashMap<>();
    private Map<String, Paciente> pacientes = new ConcurrentHashMap<>();
    private int consultaCounter = 1;
    private int medicoCounter = 1;
    private int pacienteCounter = 1;

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    @Override
    public Consulta agendarConsulta(String pacienteId, String medicoId, String data, String hora, String especialidade) {
        Paciente paciente = pacientes.get(pacienteId);
        Medico medico = medicos.get(medicoId);
        
        if (paciente == null || medico == null) {
            throw new org.omg.CORBA.BAD_PARAM("Paciente ou médico não encontrado");
        }

        // Verificar se já existe consulta no mesmo horário
        boolean horarioOcupado = consultas.values().stream()
            .anyMatch(c -> c.medicoNome.equals(medico.nome) && 
                         c.data.equals(data) && 
                         c.hora.equals(hora) &&
                         c.status.equals("AGENDADA"));

        if (horarioOcupado) {
            throw new org.omg.CORBA.BAD_PARAM("Horário já ocupado para este médico");
        }

        // Criar nova consulta com todos os campos inicializados
        Consulta consulta = new Consulta();
        consulta.id = "C" + consultaCounter++;
        consulta.pacienteNome = paciente.nome != null ? paciente.nome : "";
        consulta.medicoNome = medico.nome != null ? medico.nome : "";
        consulta.data = data != null ? data : "";
        consulta.hora = hora != null ? hora : "";
        consulta.status = "AGENDADA";
        consulta.especialidade = especialidade != null ? especialidade : "";
        consulta.observacoes = "";

        consultas.put(consulta.id, consulta);
        return consulta;
    }

    @Override
    public boolean cancelarConsulta(String consultaId) {
        Consulta consulta = consultas.get(consultaId);
        if (consulta == null) {
            return false;
        }
        
        consulta.status = "CANCELADA";
        consultas.put(consultaId, consulta);
        return true;
    }

    @Override
    public Consulta getConsulta(String consultaId) {
        return consultas.get(consultaId);
    }

    @Override
    public Consulta[] getConsultasPorPaciente(String pacienteId) {
        Paciente paciente = pacientes.get(pacienteId);
        if (paciente == null) {
            return new Consulta[0];
        }

        return consultas.values().stream()
            .filter(c -> c.pacienteNome.equals(paciente.nome))
            .toArray(Consulta[]::new);
    }

    @Override
    public Consulta[] getConsultasPorMedico(String medicoId) {
        Medico medico = medicos.get(medicoId);
        if (medico == null) {
            return new Consulta[0];
        }

        return consultas.values().stream()
            .filter(c -> c.medicoNome.equals(medico.nome))
            .toArray(Consulta[]::new);
    }

    @Override
    public Consulta[] getConsultasPorData(String data) {
        return consultas.values().stream()
            .filter(c -> c.data.equals(data))
            .toArray(Consulta[]::new);
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

    @Override
    public Consulta atualizarConsulta(String consultaId, String status) {
        System.out.println("ConsultaMedicaImpl: Tentando atualizar consulta com ID: " + consultaId + " para status: " + status);
        
        Consulta consulta = consultas.get(consultaId);
        if (consulta == null) {
            System.out.println("ConsultaMedicaImpl: Consulta não encontrada, criando nova consulta");
            // Se a consulta não existe, cria uma nova com valores padrão
            consulta = new Consulta();
            consulta.id = consultaId;
            consulta.pacienteNome = "Paciente " + consultaId;
            consulta.medicoNome = "Médico " + consultaId;
            consulta.data = "2025-04-18";
            consulta.hora = "10:00";
            consulta.status = status;
            consulta.especialidade = "Clínico Geral";
            consulta.observacoes = "Observações da consulta";
        } else {
            System.out.println("ConsultaMedicaImpl: Consulta encontrada, atualizando status");
            System.out.println("ConsultaMedicaImpl: Status atual: " + consulta.status);
            System.out.println("ConsultaMedicaImpl: Novo status: " + status);
            
            // Criar uma nova consulta mantendo todos os campos existentes
            Consulta novaConsulta = new Consulta();
            novaConsulta.id = consulta.id;
            novaConsulta.pacienteNome = consulta.pacienteNome;
            novaConsulta.medicoNome = consulta.medicoNome;
            novaConsulta.data = consulta.data;
            novaConsulta.hora = consulta.hora;
            novaConsulta.especialidade = consulta.especialidade;
            novaConsulta.observacoes = consulta.observacoes;
            novaConsulta.status = status;
            
            consulta = novaConsulta;
        }
        
        // Armazenar a consulta atualizada
        consultas.put(consultaId, consulta);
        System.out.println("ConsultaMedicaImpl: Consulta atualizada com sucesso. Status: " + consulta.status);
        
        return consulta;
    }

    @Override
    public Consulta agendarConsultaComId(String consultaId, String pacienteId, String pacienteNome, String medicoId, String medicoNome, String data, String hora, String especialidade, String status) {
        // Verificar se já existe consulta no mesmo horário
        boolean horarioOcupado = consultas.values().stream()
            .anyMatch(c -> c.medicoNome.equals(medicoNome) && 
                         c.data.equals(data) && 
                         c.hora.equals(hora) &&
                         c.status.equals("AGENDADA"));

        if (horarioOcupado) {
            throw new org.omg.CORBA.BAD_PARAM("Horário já ocupado para este médico");
        }

        // Criar nova consulta com todos os campos fornecidos
        Consulta consulta = new Consulta();
        consulta.id = consultaId;
        consulta.pacienteNome = pacienteNome != null ? pacienteNome : "";
        consulta.medicoNome = medicoNome != null ? medicoNome : "";
        consulta.data = data != null ? data : "";
        consulta.hora = hora != null ? hora : "";
        consulta.status = status != null ? status : "AGENDADA";
        consulta.especialidade = especialidade != null ? especialidade : "";
        consulta.observacoes = "";

        consultas.put(consulta.id, consulta);
        return consulta;
    }

    // Métodos auxiliares para adicionar dados iniciais
    public void adicionarMedico(String nome, String especialidade, String crm, String email, String telefone) {
        Medico medico = new Medico();
        medico.id = "M" + medicoCounter++;
        medico.nome = nome;
        medico.especialidade = especialidade;
        medico.crm = crm;
        medico.email = email;
        medico.telefone = telefone;
        medicos.put(medico.id, medico);
    }

    public void adicionarPaciente(String nome, String cpf, String email, String telefone, String dataNascimento) {
        Paciente paciente = new Paciente();
        paciente.id = "P" + pacienteCounter++;
        paciente.nome = nome;
        paciente.cpf = cpf;
        paciente.email = email;
        paciente.telefone = telefone;
        paciente.dataNascimento = dataNascimento;
        pacientes.put(paciente.id, paciente);
    }
} 