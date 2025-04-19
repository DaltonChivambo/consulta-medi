package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import ConsultaApp.ConsultaMedica;
import ConsultaApp.ConsultaMedicaHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class CorbaConfig {
    private static final Logger logger = LoggerFactory.getLogger(CorbaConfig.class);

    @Value("${orb.initial.port:1050}")
    private int orbPort;

    @Value("${orb.initial.host:localhost}")
    private String orbHost;

    @Value("${naming.host:localhost}")
    private String namingHost;

    @Value("${naming.port:1050}")
    private int namingPort;

    @Bean
    public ORB orb() {
        String[] orbArgs = {
            "-ORBInitialPort", "1050",
            "-ORBInitialHost", "localhost"
        };
        
        System.out.println("Inicializando ORB com porta 1050 e host localhost");
        ORB orb = ORB.init(orbArgs, null);
        System.out.println("ORB inicializado com sucesso");
        return orb;
    }

    @Bean
    public NamingContextExt namingContext(ORB orb) {
        System.out.println("Obtendo referência para o serviço de nomes");
        try {
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            System.out.println("Referência do serviço de nomes obtida com sucesso");
            return ncRef;
        } catch (Exception e) {
            System.err.println("ERRO ao obter referência do serviço de nomes: " + e);
            throw new RuntimeException(e);
        }
    }

    public ConsultaMedica getConsultaMedica(ORB orb, NamingContextExt namingContext) {
        logger.info("Obtendo referência para o serviço ConsultaMedica");
        try {
            NameComponent[] name = new NameComponent[] {
                new NameComponent("ConsultaMedica", "")
            };
            org.omg.CORBA.Object objRef = namingContext.resolve(name);
            ConsultaMedica consultaMedica = ConsultaMedicaHelper.narrow(objRef);
            logger.info("Serviço ConsultaMedica obtido com sucesso");
            return consultaMedica;
        } catch (NotFound e) {
            logger.error("Serviço ConsultaMedica não encontrado: {}", e.getMessage(), e);
            throw new RuntimeException("Serviço ConsultaMedica não encontrado", e);
        } catch (CannotProceed e) {
            logger.error("Erro ao resolver ConsultaMedica (CannotProceed): {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao resolver ConsultaMedica", e);
        } catch (InvalidName e) {
            logger.error("Nome inválido para ConsultaMedica: {}", e.getMessage(), e);
            throw new RuntimeException("Nome inválido para ConsultaMedica", e);
        } catch (Exception e) {
            logger.error("Erro inesperado ao obter ConsultaMedica: {}", e.getMessage(), e);
            throw new RuntimeException("Erro inesperado ao obter ConsultaMedica", e);
        }
    }
} 