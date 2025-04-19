package com.example.consulta.repository;

import com.example.consulta.model.ConsultaMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultaMongoRepository extends MongoRepository<ConsultaMongo, String> {
    List<ConsultaMongo> findByPacienteId(String pacienteId);
    List<ConsultaMongo> findByMedicoId(String medicoId);
    List<ConsultaMongo> findByStatus(String status);
} 