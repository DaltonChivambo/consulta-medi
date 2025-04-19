package com.example.consulta.service;

import com.example.consulta.model.ConsultaMongo;
import com.example.consulta.model.MedicoMongo;
import com.example.consulta.model.PacienteMongo;
import com.example.consulta.exception.MongoDBException;
import com.example.consulta.repository.ConsultaMongoRepository;
import com.example.consulta.repository.MedicoMongoRepository;
import com.example.consulta.repository.PacienteMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ConsultaApp.Medico;
import ConsultaApp.Paciente;

import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

@Service
public class ConsultaMongoService {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaMongoService.class);

    @Autowired
    private ConsultaMongoRepository consultaMongoRepository;

    @Autowired
    private MedicoMongoRepository medicoMongoRepository;

    @Autowired
    private PacienteMongoRepository pacienteMongoRepository;

    public List<ConsultaMongo> findAll() {
        try {
            return consultaMongoRepository.findAll();
        } catch (Exception e) {
            logger.error("Erro ao buscar todas as consultas no MongoDB: " + e.getMessage(), e);
            throw new MongoDBException("Erro ao buscar todas as consultas", e);
        }
    }

    public Optional<ConsultaMongo> findById(String id) {
        try {
            return consultaMongoRepository.findById(id);
        } catch (Exception e) {
            logger.error("Erro ao buscar consulta por ID no MongoDB: " + e.getMessage(), e);
            throw new MongoDBException("Erro ao buscar consulta por ID: " + id, e);
        }
    }

    public List<ConsultaMongo> findByPacienteId(String pacienteId) {
        try {
            return consultaMongoRepository.findByPacienteId(pacienteId);
        } catch (Exception e) {
            logger.error("Erro ao buscar consultas por paciente no MongoDB: " + e.getMessage(), e);
            throw new MongoDBException("Erro ao buscar consultas por paciente: " + pacienteId, e);
        }
    }

    public List<ConsultaMongo> findByMedicoId(String medicoId) {
        try {
            return consultaMongoRepository.findByMedicoId(medicoId);
        } catch (Exception e) {
            logger.error("Erro ao buscar consultas por médico no MongoDB: " + e.getMessage(), e);
            throw new MongoDBException("Erro ao buscar consultas por médico: " + medicoId, e);
        }
    }

    public List<ConsultaMongo> findByStatus(String status) {
        try {
            return consultaMongoRepository.findByStatus(status);
        } catch (Exception e) {
            logger.error("Erro ao buscar consultas por status no MongoDB: " + e.getMessage(), e);
            throw new MongoDBException("Erro ao buscar consultas por status: " + status, e);
        }
    }

    public ConsultaMongo save(ConsultaMongo consulta) {
        try {
            return consultaMongoRepository.save(consulta);
        } catch (Exception e) {
            logger.error("Erro ao salvar consulta no MongoDB: " + e.getMessage(), e);
            throw new MongoDBException("Erro ao salvar consulta", e);
        }
    }

    public void deleteById(String id) {
        try {
            consultaMongoRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Erro ao deletar consulta no MongoDB: " + e.getMessage(), e);
            throw new MongoDBException("Erro ao deletar consulta: " + id, e);
        }
    }

    public void saveMedico(Medico medico) {
        try {
            logger.info("Salvando médico no MongoDB: {}", medico.nome);
            MedicoMongo medicoMongo = new MedicoMongo();
            medicoMongo.setId(medico.id);
            medicoMongo.setNome(medico.nome);
            medicoMongo.setEspecialidade(medico.especialidade);
            medicoMongo.setCrm(medico.crm);
            medicoMongo.setEmail(medico.email);
            medicoMongo.setTelefone(medico.telefone);
            medicoMongoRepository.save(medicoMongo);
            logger.info("Médico salvo com sucesso no MongoDB");
        } catch (Exception e) {
            logger.error("Erro ao salvar médico no MongoDB: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao salvar médico no MongoDB", e);
        }
    }

    public void savePaciente(Paciente paciente) {
        try {
            logger.info("Salvando paciente no MongoDB: {}", paciente.nome);
            PacienteMongo pacienteMongo = new PacienteMongo();
            pacienteMongo.setId(paciente.id);
            pacienteMongo.setNome(paciente.nome);
            pacienteMongo.setCpf(paciente.cpf);
            pacienteMongo.setEmail(paciente.email);
            pacienteMongo.setTelefone(paciente.telefone);
            
            // Converter a String de data para Date
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date dataNascimento = sdf.parse(paciente.dataNascimento);
                pacienteMongo.setDataNascimento(dataNascimento);
                logger.info("Data de nascimento convertida com sucesso: {}", dataNascimento);
            } catch (ParseException e) {
                logger.warn("Erro ao converter data de nascimento: {}", e.getMessage());
                pacienteMongo.setDataNascimento(new Date());
            }
            
            pacienteMongoRepository.save(pacienteMongo);
            logger.info("Paciente salvo com sucesso no MongoDB");
        } catch (Exception e) {
            logger.error("Erro ao salvar paciente no MongoDB: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao salvar paciente no MongoDB", e);
        }
    }
} 