package com.example.consulta.service;

import com.example.consulta.model.ConsultaMongo;
import com.example.consulta.model.Consulta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SincronizacaoService {
    
    private static final Logger logger = LoggerFactory.getLogger(SincronizacaoService.class);
    
    @Autowired
    private ConsultaService consultaService;
    
    @Autowired
    private ConsultaMongoService consultaMongoService;

    @Scheduled(fixedRate = 300000) // Executa a cada 5 minutos
    public void sincronizarConsultas() {
        logger.info("Iniciando sincronização de consultas...");
        
        try {
            // Obtém todas as consultas do CORBA
            List<Consulta> consultasCorba = consultaService.getTodasConsultas();
            
            // Para cada consulta do CORBA, atualiza ou cria no MongoDB
            for (Consulta consultaCorba : consultasCorba) {
                ConsultaMongo consultaMongo = new ConsultaMongo();
                consultaMongo.setId(consultaCorba.getId());
                consultaMongo.setPacienteNome(consultaCorba.getPacienteNome());
                consultaMongo.setMedicoNome(consultaCorba.getMedicoNome());
                consultaMongo.setEspecialidade(consultaCorba.getEspecialidade());
                
                // Converte a data e hora
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                
                consultaMongo.setData(LocalDate.parse(consultaCorba.getData(), dateFormatter));
                consultaMongo.setHora(LocalTime.parse(consultaCorba.getHora(), timeFormatter));
                consultaMongo.setStatus(consultaCorba.getStatus());
                
                consultaMongoService.save(consultaMongo);
            }
            
            logger.info("Sincronização concluída com sucesso!");
        } catch (Exception e) {
            logger.error("Erro durante a sincronização: " + e.getMessage(), e);
        }
    }
} 