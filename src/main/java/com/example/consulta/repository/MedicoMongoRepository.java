package com.example.consulta.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.consulta.model.MedicoMongo;
import java.util.Optional;

@Repository
public interface MedicoMongoRepository extends MongoRepository<MedicoMongo, String> {
    Optional<MedicoMongo> findByCrm(String crm);
} 