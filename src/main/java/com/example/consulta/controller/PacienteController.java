package com.example.consulta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.consulta.model.PacienteMongo;
import com.example.consulta.service.PacienteMongoService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/pacientes")
public class PacienteController {
    private static final Logger logger = LoggerFactory.getLogger(PacienteController.class);

    @Autowired
    private PacienteMongoService pacienteService;

    @GetMapping
    public String listarPacientes(Model model) {
        try {
            logger.info("Buscando lista de pacientes");
            model.addAttribute("pacientes", pacienteService.findAll());
            logger.info("Lista de pacientes obtida com sucesso");
            return "pacientes/lista";
        } catch (Exception e) {
            logger.error("Erro ao listar pacientes: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao listar pacientes", e);
        }
    }

    @GetMapping("/novo")
    public String novoPaciente(Model model) {
        try {
            logger.info("Preparando formulário para novo paciente");
            model.addAttribute("paciente", new PacienteMongo());
            return "pacientes/form";
        } catch (Exception e) {
            logger.error("Erro ao preparar formulário de novo paciente: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao preparar formulário", e);
        }
    }

    @PostMapping("/salvar")
    public String salvarPaciente(PacienteMongo paciente) {
        try {
            logger.info("Salvando paciente: {}", paciente.getNome());
            pacienteService.save(paciente);
            logger.info("Paciente salvo com sucesso");
            return "redirect:/pacientes";
        } catch (Exception e) {
            logger.error("Erro ao salvar paciente: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao salvar paciente", e);
        }
    }

    @GetMapping("/{id}")
    public String visualizarPaciente(@PathVariable String id, Model model) {
        try {
            logger.info("Buscando paciente para visualização: {}", id);
            Optional<PacienteMongo> pacienteOpt = pacienteService.findById(id);
            if (pacienteOpt.isPresent()) {
                PacienteMongo paciente = pacienteOpt.get();
                logger.info("Paciente encontrado: {}", paciente.getNome());
                logger.info("Data de nascimento: {}", paciente.getDataNascimento());
                model.addAttribute("paciente", paciente);
                logger.info("Paciente adicionado ao modelo");
                return "pacientes/detalhes";
            } else {
                logger.warn("Paciente não encontrado: {}", id);
                return "redirect:/pacientes";
            }
        } catch (Exception e) {
            logger.error("Erro ao visualizar paciente: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao visualizar paciente", e);
        }
    }

    @GetMapping("/{id}/editar")
    public String editarPaciente(@PathVariable String id, Model model) {
        try {
            logger.info("Buscando paciente para edição: {}", id);
            Optional<PacienteMongo> pacienteOpt = pacienteService.findById(id);
            if (pacienteOpt.isPresent()) {
                logger.info("Paciente encontrado para edição: {}", pacienteOpt.get().getNome());
                model.addAttribute("paciente", pacienteOpt.get());
                return "pacientes/form";
            } else {
                logger.warn("Paciente não encontrado para edição: {}", id);
                return "redirect:/pacientes";
            }
        } catch (Exception e) {
            logger.error("Erro ao preparar edição de paciente: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao preparar edição", e);
        }
    }

    @PostMapping("/excluir")
    public String excluirPaciente(@RequestParam String id) {
        try {
            logger.info("Tentando excluir paciente com ID: {}", id);
            pacienteService.deleteById(id);
            logger.info("Paciente excluído com sucesso");
            return "redirect:/pacientes";
        } catch (Exception e) {
            logger.error("Erro ao excluir paciente: {}", e.getMessage(), e);
            return "redirect:/pacientes?error=delete";
        }
    }
} 