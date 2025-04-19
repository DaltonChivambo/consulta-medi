package com.example.consulta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.consulta.model.MedicoMongo;
import com.example.consulta.model.Usuario;
import com.example.consulta.service.MedicoMongoService;
import com.example.consulta.service.AuthService;
import com.example.consulta.repository.UsuarioRepository;
import java.util.Optional;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/medicos")
public class MedicoController {

    @Autowired
    private MedicoMongoService medicoService;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String listarMedicos(Model model, HttpSession session) {
        // Verificar se o usuário está logado e é administrador
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"ADMIN".equals(usuario.getTipo())) {
            return "redirect:/auth/login?error=unauthorized";
        }
        
        model.addAttribute("medicos", medicoService.findAll());
        return "medicos/lista";
    }

    @GetMapping("/novo")
    public String novoMedico(Model model, HttpSession session) {
        // Verificar se o usuário está logado e é administrador
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"ADMIN".equals(usuario.getTipo())) {
            return "redirect:/auth/login?error=unauthorized";
        }
        
        model.addAttribute("medico", new MedicoMongo());
        return "medicos/form";
    }

    @PostMapping("/salvar")
    public String salvarMedico(MedicoMongo medico, @RequestParam String senha, HttpSession session) {
        // Verificar se o usuário está logado e é administrador
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"ADMIN".equals(usuario.getTipo())) {
            return "redirect:/auth/login?error=unauthorized";
        }
        
        // Salvar o médico
        MedicoMongo medicoSalvo = medicoService.save(medico);
        
        // Criar conta de usuário para o médico
        authService.registrarMedico(medico.getNome(), medico.getEmail(), senha, medicoSalvo.getId(), medico.getCrm());
        
        return "redirect:/medicos";
    }

    @GetMapping("/{id}")
    public String visualizarMedico(@PathVariable String id, Model model, HttpSession session) {
        // Verificar se o usuário está logado e é administrador
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"ADMIN".equals(usuario.getTipo())) {
            return "redirect:/auth/login?error=unauthorized";
        }
        
        Optional<MedicoMongo> medicoOpt = medicoService.findById(id);
        if (medicoOpt.isPresent()) {
            MedicoMongo medico = medicoOpt.get();
            // Verificar se o status do médico está sincronizado com o usuário
            Optional<Usuario> usuarioMedico = usuarioRepository.findByUsuarioIdAndTipo(id, "MEDICO");
            if (usuarioMedico.isPresent()) {
                Usuario usuarioMed = usuarioMedico.get();
                if (medico.isAtivo() != usuarioMed.isAtivo()) {
                    // Sincronizar o status do médico com o usuário
                    medico.setAtivo(usuarioMed.isAtivo());
                    medicoService.save(medico);
                }
            }
            model.addAttribute("medico", medico);
            return "medicos/detalhes";
        } else {
            return "redirect:/medicos";
        }
    }

    @GetMapping("/{id}/editar")
    public String editarMedico(@PathVariable String id, Model model, HttpSession session) {
        // Verificar se o usuário está logado e é administrador
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"ADMIN".equals(usuario.getTipo())) {
            return "redirect:/auth/login?error=unauthorized";
        }
        
        Optional<MedicoMongo> medicoOpt = medicoService.findById(id);
        if (medicoOpt.isPresent()) {
            model.addAttribute("medico", medicoOpt.get());
            return "medicos/form";
        } else {
            return "redirect:/medicos";
        }
    }

    @PostMapping("/excluir")
    public String excluirMedico(@RequestParam String id, HttpSession session) {
        // Verificar se o usuário está logado e é administrador
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"ADMIN".equals(usuario.getTipo())) {
            return "redirect:/auth/login?error=unauthorized";
        }
        
        try {
            // Desativar o médico em vez de excluí-lo
            Optional<MedicoMongo> medicoOpt = medicoService.findById(id);
            if (medicoOpt.isPresent()) {
                MedicoMongo medico = medicoOpt.get();
                medico.setAtivo(false);
                medicoService.save(medico);
                
                // Desativar também a conta de usuário do médico
                Optional<Usuario> usuarioMedico = usuarioRepository.findByUsuarioIdAndTipo(id, "MEDICO");
                if (usuarioMedico.isPresent()) {
                    Usuario usuarioMed = usuarioMedico.get();
                    usuarioMed.setAtivo(false);
                    usuarioRepository.save(usuarioMed);
                }
                
                return "redirect:/medicos?success=deactivated";
            }
            return "redirect:/medicos?error=not_found";
        } catch (Exception e) {
            return "redirect:/medicos?error=update";
        }
    }
    
    @PostMapping("/bloquear")
    public String bloquearMedico(@RequestParam String id, @RequestParam boolean ativo, RedirectAttributes redirectAttributes, HttpSession session) {
        // Verificar se o usuário está logado e é administrador
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"ADMIN".equals(usuario.getTipo())) {
            return "redirect:/auth/login?error=unauthorized";
        }

        try {
            MedicoMongo medico = medicoService.getMedicoById(id);
            if (medico != null) {
                // Atualizar o status do médico no MongoDB
                medico.setAtivo(ativo);
                medicoService.atualizarMedico(medico);
                
                // Atualizar o status do usuário correspondente
                Optional<Usuario> usuarioMedico = usuarioRepository.findByUsuarioIdAndTipo(id, "MEDICO");
                if (usuarioMedico.isPresent()) {
                    Usuario usuarioMed = usuarioMedico.get();
                    usuarioMed.setAtivo(ativo);
                    usuarioRepository.save(usuarioMed);
                }
                
                redirectAttributes.addFlashAttribute("mensagem", "Status do médico atualizado com sucesso!");
            } else {
                redirectAttributes.addFlashAttribute("erro", "Médico não encontrado!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar status do médico: " + e.getMessage());
        }
        return "redirect:/medicos";
    }

    @PutMapping("/{id}")
    public String atualizarMedico(@PathVariable String id, @ModelAttribute MedicoMongo medico, HttpSession session) {
        // Verificar se o usuário está logado e é administrador
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"ADMIN".equals(usuario.getTipo())) {
            return "redirect:/auth/login?error=unauthorized";
        }
        
        try {
            // Atualizar o médico no MongoDB
            medico.setId(id);
            MedicoMongo medicoAtualizado = medicoService.save(medico);
            
            // Atualizar o email do usuário correspondente
            Optional<Usuario> usuarioMedico = usuarioRepository.findByUsuarioIdAndTipo(id, "MEDICO");
            if (usuarioMedico.isPresent()) {
                Usuario usuarioMed = usuarioMedico.get();
                usuarioMed.setEmail(medico.getEmail());
                usuarioRepository.save(usuarioMed);
            }
            
            return "redirect:/medicos?success=updated";
        } catch (Exception e) {
            return "redirect:/medicos?error=update";
        }
    }
} 