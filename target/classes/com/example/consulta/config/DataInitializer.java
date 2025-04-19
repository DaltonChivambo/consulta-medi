package com.example.consulta.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.example.consulta.model.Usuario;
import com.example.consulta.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Verifica se já existe um usuário admin
        if (!usuarioRepository.findByEmail("admin@consulta.com").isPresent()) {
            logger.info("Criando usuário administrador...");
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail("admin@consulta.com");
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setTipo("ADMIN");
            admin.setUsuarioId("ADMIN");
            admin.setAtivo(true);
            usuarioRepository.save(admin);
            logger.info("Usuário administrador criado com sucesso!");
        } else {
            // Atualiza o usuário admin existente
            logger.info("Atualizando usuário administrador...");
            Usuario admin = usuarioRepository.findByEmail("admin@consulta.com").get();
            admin.setNome("Administrador");
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setTipo("ADMIN");
            admin.setUsuarioId("ADMIN");
            admin.setAtivo(true);
            usuarioRepository.save(admin);
            logger.info("Usuário administrador atualizado com sucesso!");
        }

        // Atualiza as senhas de todos os usuários existentes
        logger.info("Atualizando senhas de todos os usuários...");
        List<Usuario> usuarios = usuarioRepository.findAll();
        for (Usuario usuario : usuarios) {
            if (!usuario.getEmail().equals("admin@consulta.com")) {
                String senhaAtual = usuario.getSenha();
                if (!senhaAtual.startsWith("$2a$")) { // Verifica se a senha não está criptografada
                    usuario.setSenha(passwordEncoder.encode(senhaAtual));
                    usuarioRepository.save(usuario);
                    logger.info("Senha atualizada para o usuário: {}", usuario.getEmail());
                }
            }
        }
        logger.info("Atualização de senhas concluída!");
    }
} 