package com.example.consulta.controller;

import com.example.consulta.model.ConsultaMongo;
import com.example.consulta.service.ConsultaMongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaMongoController {

    @Autowired
    private ConsultaMongoService consultaMongoService;

    @GetMapping
    public ResponseEntity<List<ConsultaMongo>> getAllConsultas() {
        return ResponseEntity.ok(consultaMongoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaMongo> getConsultaById(@PathVariable String id) {
        return consultaMongoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<ConsultaMongo>> getConsultasByPacienteId(@PathVariable String pacienteId) {
        return ResponseEntity.ok(consultaMongoService.findByPacienteId(pacienteId));
    }

    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<List<ConsultaMongo>> getConsultasByMedicoId(@PathVariable String medicoId) {
        return ResponseEntity.ok(consultaMongoService.findByMedicoId(medicoId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ConsultaMongo>> getConsultasByStatus(@PathVariable String status) {
        return ResponseEntity.ok(consultaMongoService.findByStatus(status));
    }

    @PostMapping
    public ResponseEntity<ConsultaMongo> createConsulta(@RequestBody ConsultaMongo consulta) {
        return ResponseEntity.ok(consultaMongoService.save(consulta));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultaMongo> updateConsulta(@PathVariable String id, @RequestBody ConsultaMongo consulta) {
        if (!consultaMongoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        consulta.setId(id);
        return ResponseEntity.ok(consultaMongoService.save(consulta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsulta(@PathVariable String id) {
        if (!consultaMongoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        consultaMongoService.deleteById(id);
        return ResponseEntity.ok().build();
    }
} 