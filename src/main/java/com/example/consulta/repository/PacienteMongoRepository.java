package com.example.consulta.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.consulta.model.PacienteMongo;

@Repository
public interface PacienteMongoRepository extends MongoRepository<PacienteMongo, String> {
    Optional<PacienteMongo> findByCpf(String cpf);
} 