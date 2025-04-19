package com.example.consulta.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.consulta.repository.UsuarioRepository;
import com.example.consulta.model.Usuario;
import com.example.consulta.model.MedicoMongo;
import com.example.consulta.model.AuthResult;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private MedicoMongoService medicoMongoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResult autenticar(String email, String senha) {
        logger.info("Tentando autenticar usuário: {}", email);
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            logger.info("Usuário encontrado: tipo={}, ativo={}", usuario.getTipo(), usuario.isAtivo());
            
            if (passwordEncoder.matches(senha, usuario.getSenha())) {
                logger.info("Senha correta para o usuário: {}", email);
                if ("MEDICO".equals(usuario.getTipo()) && !usuario.isAtivo()) {
                    logger.warn("Médico com ID {} bloqueado", usuario.getUsuarioId());
                    return new AuthResult("Sua conta está bloqueada. Entre em contato com o administrador.");
                }
                logger.info("Autenticação bem-sucedida para o usuário: {}", email);
                return new AuthResult(usuario);
            } else {
                logger.warn("Senha incorreta para o usuário: {}", email);
                return new AuthResult("Email ou senha inválidos");
            }
        } else {
            logger.warn("Usuário não encontrado: {}", email);
            return new AuthResult("Email ou senha inválidos");
        }
    }

    public Usuario registrarPaciente(String nome, String email, String senha, String pacienteId) {
        logger.info("Registrando novo paciente: {}", email);
        if (usuarioRepository.findByEmail(email).isPresent()) {
            return null;
        }

        Usuario usuario = new Usuario(nome, email, passwordEncoder.encode(senha), "PACIENTE", pacienteId);
        return usuarioRepository.save(usuario);
    }

    public Usuario registrarMedico(String nome, String email, String senha, String medicoId, String crm) {
        logger.info("Registrando novo médico: {}", email);
        if (usuarioRepository.findByEmail(email).isPresent()) {
            return null;
        }

        Usuario usuario = new Usuario(nome, email, passwordEncoder.encode(senha), "MEDICO", medicoId);
        usuario.setCrm(crm);
        usuario.setAtivo(true); // Médicos começam ativos
        return usuarioRepository.save(usuario);
    }

    public Usuario ativarMedico(String email) {
        logger.info("Ativando médico: {}", email);
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if ("MEDICO".equals(usuario.getTipo())) {
                usuario.setAtivo(true);
                return usuarioRepository.save(usuario);
            }
        }
        return null;
    }

    public Usuario desativarMedico(String email) {
        logger.info("Desativando médico: {}", email);
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if ("MEDICO".equals(usuario.getTipo())) {
                usuario.setAtivo(false);
                return usuarioRepository.save(usuario);
            }
        }
        return null;
    }
} 