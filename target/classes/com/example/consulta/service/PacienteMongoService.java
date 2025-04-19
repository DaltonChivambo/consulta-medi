package com.example.consulta.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.consulta.model.PacienteMongo;
import com.example.consulta.repository.PacienteMongoRepository;

@Service
public class PacienteMongoService {
    private static final Logger logger = LoggerFactory.getLogger(PacienteMongoService.class);

    @Autowired
    private PacienteMongoRepository pacienteRepository;

    public List<PacienteMongo> findAll() {
        try {
            logger.info("Buscando todos os pacientes no MongoDB");
            List<PacienteMongo> pacientes = pacienteRepository.findAll();
            logger.info("Encontrados {} pacientes", pacientes.size());
            return pacientes;
        } catch (Exception e) {
            logger.error("Erro ao buscar pacientes no MongoDB: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao buscar pacientes", e);
        }
    }

    public Optional<PacienteMongo> findById(String id) {
        try {
            logger.info("Buscando paciente por ID: {}", id);
            if (id == null || id.trim().isEmpty()) {
                logger.error("ID do paciente é nulo ou vazio");
                throw new IllegalArgumentException("ID do paciente não pode ser nulo ou vazio");
            }
            Optional<PacienteMongo> paciente = pacienteRepository.findById(id);
            if (paciente.isPresent()) {
                logger.info("Paciente encontrado: {}", paciente.get().getNome());
            } else {
                logger.warn("Paciente não encontrado para o ID: {}", id);
            }
            return paciente;
        } catch (Exception e) {
            logger.error("Erro ao buscar paciente por ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Falha ao buscar paciente", e);
        }
    }

    public Optional<PacienteMongo> findByCpf(String cpf) {
        try {
            logger.info("Buscando paciente por CPF: {}", cpf);
            if (cpf == null || cpf.trim().isEmpty()) {
                logger.error("CPF do paciente é nulo ou vazio");
                throw new IllegalArgumentException("CPF do paciente não pode ser nulo ou vazio");
            }
            Optional<PacienteMongo> paciente = pacienteRepository.findByCpf(cpf);
            if (paciente.isPresent()) {
                logger.info("Paciente encontrado: {}", paciente.get().getNome());
            } else {
                logger.warn("Paciente não encontrado para o CPF: {}", cpf);
            }
            return paciente;
        } catch (Exception e) {
            logger.error("Erro ao buscar paciente por CPF {}: {}", cpf, e.getMessage(), e);
            throw new RuntimeException("Falha ao buscar paciente", e);
        }
    }

    public PacienteMongo save(PacienteMongo paciente) {
        try {
            logger.info("Salvando paciente: {}", paciente.getNome());
            if (paciente == null) {
                logger.error("Paciente é nulo");
                throw new IllegalArgumentException("Paciente não pode ser nulo");
            }
            if (paciente.getId() == null || paciente.getId().trim().isEmpty()) {
                paciente.setId(UUID.randomUUID().toString());
                logger.info("ID gerado para novo paciente: {}", paciente.getId());
            }
            PacienteMongo savedPaciente = pacienteRepository.save(paciente);
            logger.info("Paciente salvo com sucesso: {}", savedPaciente.getId());
            return savedPaciente;
        } catch (Exception e) {
            logger.error("Erro ao salvar paciente: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao salvar paciente", e);
        }
    }

    public void deleteById(String id) {
        try {
            logger.info("Excluindo paciente com ID: {}", id);
            pacienteRepository.deleteById(id);
            logger.info("Paciente excluído com sucesso");
        } catch (Exception e) {
            logger.error("Erro ao excluir paciente com ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Falha ao excluir paciente", e);
        }
    }
} 