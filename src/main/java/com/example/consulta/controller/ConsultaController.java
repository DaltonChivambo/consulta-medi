package com.example.consulta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.example.consulta.service.ConsultaService;
import com.example.consulta.service.MedicoMongoService;
import com.example.consulta.model.Consulta;
import com.example.consulta.model.Medico;
import com.example.consulta.model.Paciente;
import com.example.consulta.model.Usuario;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;
    
    @Autowired
    private MedicoMongoService medicoMongoService;

    @GetMapping
    public String listarConsultas(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        if ("PACIENTE".equals(usuario.getTipo())) {
            return "redirect:/consultas/minhas";
        } else if ("MEDICO".equals(usuario.getTipo())) {
            return "redirect:/consultas/medico";
        }
        
        List<Consulta> consultas = consultaService.getTodasConsultas();
        model.addAttribute("consultas", consultas);
        return "consultas/lista";
    }

    @GetMapping("/minhas")
    public String minhasConsultas(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"PACIENTE".equals(usuario.getTipo())) {
            return "redirect:/auth/login";
        }
        
        List<Consulta> consultas = consultaService.getConsultasPorPaciente(usuario.getUsuarioId());
        model.addAttribute("consultas", consultas);
        return "consultas/lista";
    }

    @GetMapping("/medico")
    public String consultasMedico(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"MEDICO".equals(usuario.getTipo())) {
            return "redirect:/auth/login";
        }
        
        // Verificar se o médico ainda existe no MongoDB
        Optional<com.example.consulta.model.MedicoMongo> medicoOpt = medicoMongoService.findById(usuario.getUsuarioId());
        if (!medicoOpt.isPresent()) {
            // Se o médico não existir mais, invalidar a sessão e redirecionar para o login
            session.invalidate();
            return "redirect:/auth/login?error=medico_removido";
        }
        
        List<Consulta> consultas = consultaService.getConsultasPorMedico(usuario.getUsuarioId());
        model.addAttribute("consultas", consultas);
        return "consultas/medico";
    }

    @GetMapping("/nova")
    public String novaConsulta(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"PACIENTE".equals(usuario.getTipo())) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("medicos", consultaService.getTodosMedicos());
        model.addAttribute("consulta", new Consulta());
        return "consultas/nova";
    }

    @PostMapping
    public String salvarConsulta(@ModelAttribute Consulta consulta, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"PACIENTE".equals(usuario.getTipo())) {
            return "redirect:/auth/login";
        }
        
        consulta.setPacienteId(usuario.getUsuarioId());
        consultaService.agendarConsulta(consulta);
        return "redirect:/consultas/minhas";
    }

    @GetMapping("/{id}")
    public String verConsulta(@PathVariable String id, Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        Consulta consulta = consultaService.getConsulta(id);
        model.addAttribute("consulta", consulta);
        model.addAttribute("usuario", usuario);
        return "consultas/detalhes";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelarConsulta(@PathVariable String id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"PACIENTE".equals(usuario.getTipo())) {
            return "redirect:/auth/login";
        }
        
        consultaService.cancelarConsulta(id);
        return "redirect:/consultas/minhas";
    }

    @GetMapping("/{id}/editar")
    public String editarConsulta(@PathVariable String id, Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"MEDICO".equals(usuario.getTipo())) {
            return "redirect:/auth/login";
        }
        
        Consulta consulta = consultaService.getConsulta(id);
        if (!consulta.getMedicoId().equals(usuario.getUsuarioId())) {
            return "redirect:/consultas/medico";
        }
        
        model.addAttribute("consulta", consulta);
        return "consultas/editar";
    }

    @PutMapping("/{id}")
    public String atualizarConsulta(@PathVariable String id, @ModelAttribute Consulta consulta, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"MEDICO".equals(usuario.getTipo())) {
            return "redirect:/auth/login";
        }
        
        consulta.setId(id);
        consultaService.atualizarConsulta(consulta);
        return "redirect:/consultas/medico";
    }
} 