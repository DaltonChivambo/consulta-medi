package com.example.consulta.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.consulta.model.MedicoMongo;
import com.example.consulta.repository.MedicoMongoRepository;

@Service
public class MedicoMongoService {
    private static final Logger logger = LoggerFactory.getLogger(MedicoMongoService.class);

    @Autowired
    private MedicoMongoRepository medicoRepository;

    public List<MedicoMongo> findAll() {
        try {
            logger.info("Buscando todos os médicos no MongoDB");
            return medicoRepository.findAll();
        } catch (Exception e) {
            logger.error("Erro ao buscar médicos no MongoDB: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao buscar médicos", e);
        }
    }

    public Optional<MedicoMongo> findById(String id) {
        try {
            logger.info("Buscando médico por ID: {}", id);
            return medicoRepository.findById(id);
        } catch (Exception e) {
            logger.error("Erro ao buscar médico por ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Falha ao buscar médico", e);
        }
    }

    public Optional<MedicoMongo> findByCrm(String crm) {
        try {
            logger.info("Buscando médico por CRM: {}", crm);
            return medicoRepository.findByCrm(crm);
        } catch (Exception e) {
            logger.error("Erro ao buscar médico por CRM {}: {}", crm, e.getMessage(), e);
            throw new RuntimeException("Falha ao buscar médico", e);
        }
    }

    public MedicoMongo save(MedicoMongo medico) {
        try {
            logger.info("Salvando médico: {}", medico.getNome());
            if (medico.getId() == null || medico.getId().isEmpty()) {
                String novoId = "M" + System.currentTimeMillis();
                medico.setId(novoId);
                logger.info("ID gerado para novo médico: {}", medico.getId());
            }
            return medicoRepository.save(medico);
        } catch (Exception e) {
            logger.error("Erro ao salvar médico: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao salvar médico", e);
        }
    }

    public void deleteById(String id) {
        try {
            logger.info("Excluindo médico com ID: {}", id);
            medicoRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Erro ao excluir médico com ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Falha ao excluir médico", e);
        }
    }

    public MedicoMongo getMedicoById(String id) {
        return medicoRepository.findById(id).orElse(null);
    }

    public void atualizarMedico(MedicoMongo medico) {
        medicoRepository.save(medico);
    }
} 