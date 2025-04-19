package com.example.consulta.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

@Configuration
public class MongoIndexConfig implements CommandLineRunner {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) {
        // Índices para a coleção de usuários
        mongoTemplate.indexOps("usuarios").ensureIndex(
            new Index().on("email", Direction.ASC).unique()
        );
        mongoTemplate.indexOps("usuarios").ensureIndex(
            new Index().on("usuarioId", Direction.ASC)
        );

        // Índices para a coleção de médicos
        mongoTemplate.indexOps("medicos").ensureIndex(
            new Index().on("email", Direction.ASC).unique()
        );
        mongoTemplate.indexOps("medicos").ensureIndex(
            new Index().on("crm", Direction.ASC).unique()
        );

        // Índices para a coleção de pacientes
        mongoTemplate.indexOps("pacientes").ensureIndex(
            new Index().on("email", Direction.ASC).unique()
        );

        // Índices para a coleção de consultas
        mongoTemplate.indexOps("consultas").ensureIndex(
            new Index().on("medicoId", Direction.ASC)
        );
        mongoTemplate.indexOps("consultas").ensureIndex(
            new Index().on("pacienteId", Direction.ASC)
        );
        mongoTemplate.indexOps("consultas").ensureIndex(
            new Index().on("data", Direction.ASC)
        );
    }
} 