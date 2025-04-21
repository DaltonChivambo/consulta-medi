package com.example.consulta.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import com.example.consulta.service.AuthService;
import com.example.consulta.service.PacienteMongoService;
import com.example.consulta.model.Usuario;
import com.example.consulta.model.PacienteMongo;
import com.example.consulta.model.AuthResult;
import javax.servlet.http.HttpSession;
import java.util.Optional;
import java.util.Map;
import java.util.Date;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.text.SimpleDateFormat;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final PacienteMongoService pacienteMongoService;

    public AuthController(AuthService authService, PacienteMongoService pacienteMongoService) {
        this.authService = authService;
        this.pacienteMongoService = pacienteMongoService;
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String error, Model model) {
        if ("unauthorized".equals(error)) {
            model.addAttribute("error", "Você não tem permissão para acessar esta página.");
        } else if ("medico_removido".equals(error)) {
            model.addAttribute("error", "Sua conta de médico foi removida do sistema. Entre em contato com o administrador.");
        }
        return "auth/login";
    }

    @GetMapping("/registro")
    public String registroForm(Model model) {
        return "auth/registro";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String senha, HttpSession session, Model model) {
        AuthResult result = authService.autenticar(email, senha);
        if (result.isSuccess()) {
            session.setAttribute("usuario", result.getUsuario());
            return "redirect:/";
        }
        model.addAttribute("error", result.getError());
        return "auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/registro/paciente")
    public String registrarPaciente(@RequestParam String nome,
                                  @RequestParam String cpf,
                                  @RequestParam String email,
                                  @RequestParam String senha,
                                  @RequestParam String telefone,
                                  @RequestParam String dataNascimento,
                                  RedirectAttributes redirectAttributes) {
        try {
            // Criar e salvar o paciente
            PacienteMongo paciente = new PacienteMongo();
            paciente.setNome(nome);
            paciente.setCpf(cpf);
            paciente.setEmail(email);
            paciente.setTelefone(telefone);
            
            // Converter a string de data para Date
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date data = sdf.parse(dataNascimento);
                paciente.setDataNascimento(data);
            } catch (Exception e) {
                paciente.setDataNascimento(new Date());
            }
            
            // Salvar o paciente no MongoDB
            PacienteMongo pacienteSalvo = pacienteMongoService.save(paciente);
            
            // Criar a conta de usuário para o paciente
            Usuario usuario = authService.registrarPaciente(nome, email, senha, pacienteSalvo.getId());
            
            if (usuario != null) {
                redirectAttributes.addFlashAttribute("success", "Registro realizado com sucesso! Faça login para continuar.");
                return "redirect:/auth/login";
            } else {
                redirectAttributes.addFlashAttribute("error", "Email já cadastrado");
                return "redirect:/auth/registro";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao realizar o registro: " + e.getMessage());
            return "redirect:/auth/registro";
        }
    }

    @PostMapping("/api/login")
    public ResponseEntity<?> loginApi(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String senha = credentials.get("senha");

        AuthResult result = authService.autenticar(email, senha);
        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getUsuario());
        }
        return ResponseEntity.badRequest().body(result.getError());
    }

    @PostMapping("/registro/medico")
    public ResponseEntity<?> registrarMedico(@RequestBody Map<String, String> dados) {
        String nome = dados.get("nome");
        String email = dados.get("email");
        String senha = dados.get("senha");
        String medicoId = dados.get("medicoId");
        String crm = dados.get("crm");

        Usuario usuario = authService.registrarMedico(nome, email, senha, medicoId, crm);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.badRequest().body("Email já cadastrado");
    }

    @PostMapping("/medico/ativar")
    public ResponseEntity<?> ativarMedico(@RequestBody Map<String, String> dados) {
        String email = dados.get("email");
        Usuario usuario = authService.ativarMedico(email);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.badRequest().body("Médico não encontrado");
    }

    @PostMapping("/medico/desativar")
    public ResponseEntity<?> desativarMedico(@RequestBody Map<String, String> dados) {
        String email = dados.get("email");
        Usuario usuario = authService.desativarMedico(email);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.badRequest().body("Médico não encontrado");
    }
} 