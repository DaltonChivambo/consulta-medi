package com.example.consulta.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.config.CorbaConfig;
import ConsultaApp.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.example.consulta.exception.ConsultaNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import com.example.consulta.model.ConsultaMongo;
import com.example.consulta.model.MedicoMongo;
import com.example.consulta.model.PacienteMongo;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import com.example.consulta.model.Consulta;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class ConsultaService {
    private static final Logger logger = LoggerFactory.getLogger(ConsultaService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    private CorbaConfig corbaConfig;

    @Autowired
    private ORB orb;

    @Autowired
    private NamingContextExt namingContext;

    @Autowired
    private ConsultaMongoService consultaMongoService;

    @Autowired
    private MedicoMongoService medicoMongoService;

    @Autowired
    private PacienteMongoService pacienteMongoService;

    private ConsultaMedica consultaMedica;

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("Inicializando ConsultaService...");
        try {
            logger.debug("Tentando obter referência do serviço ConsultaMedica");
            consultaMedica = corbaConfig.getConsultaMedica(orb, namingContext);
            logger.info("Serviço ConsultaMedica inicializado com sucesso");
        } catch (Exception e) {
            logger.error("Falha ao inicializar serviço ConsultaMedica: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao inicializar serviço ConsultaMedica", e);
        }
    }

    private ConsultaMedica getConsultaMedica() {
        if (consultaMedica == null) {
            logger.error("Serviço ConsultaMedica não está inicializado");
            throw new RuntimeException("Serviço ConsultaMedica não está inicializado");
        }
        return consultaMedica;
    }

    public List<Consulta> getTodasConsultas() {
        try {
            logger.debug("Buscando todas as consultas no MongoDB");
            List<ConsultaMongo> consultasMongo = consultaMongoService.findAll();
            logger.debug("Encontradas {} consultas no MongoDB", consultasMongo.size());
            
            return consultasMongo.stream()
                .map(this::convertMongoToModelConsulta)
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Erro ao buscar todas as consultas no MongoDB: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao buscar consultas", e);
        }
    }

    private Consulta convertMongoToModelConsulta(ConsultaMongo mongoConsulta) {
        try {
            logger.debug("Convertendo ConsultaMongo para Consulta: {}", mongoConsulta.getId());
            Consulta modelConsulta = new Consulta();
            modelConsulta.setId(mongoConsulta.getId());
            modelConsulta.setPacienteId(mongoConsulta.getPacienteId());
            modelConsulta.setPacienteNome(mongoConsulta.getPacienteNome());
            modelConsulta.setMedicoId(mongoConsulta.getMedicoId());
            modelConsulta.setMedicoNome(mongoConsulta.getMedicoNome());
            modelConsulta.setEspecialidade(mongoConsulta.getEspecialidade());
            modelConsulta.setData(mongoConsulta.getData().format(DATE_FORMATTER));
            modelConsulta.setHora(mongoConsulta.getHora().format(TIME_FORMATTER));
            
            // Validar o status
            String status = mongoConsulta.getStatus();
            if (status == null || status.isEmpty()) {
                logger.warn("Status do MongoDB é nulo ou vazio para consulta {}, usando AGENDADA como padrão", mongoConsulta.getId());
                modelConsulta.setStatus("AGENDADA");
            } else {
                logger.debug("Usando status do MongoDB: {}", status);
                modelConsulta.setStatus(status);
            }
            
            return modelConsulta;
        } catch (Exception e) {
            logger.error("Erro ao converter ConsultaMongo para Consulta: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao converter consulta", e);
        }
    }

    private ConsultaApp.Consulta convertMongoToCorbaConsulta(ConsultaMongo mongoConsulta) {
        try {
            logger.debug("Convertendo ConsultaMongo para ConsultaApp.Consulta: {}", mongoConsulta.getId());
            ConsultaApp.Consulta corbaConsulta = new ConsultaApp.Consulta();
            
            // Garantir que todos os campos obrigatórios sejam inicializados com valores seguros
            corbaConsulta.id = mongoConsulta.getId() != null ? mongoConsulta.getId() : "";
            corbaConsulta.pacienteNome = mongoConsulta.getPacienteNome() != null ? mongoConsulta.getPacienteNome() : "";
            corbaConsulta.medicoNome = mongoConsulta.getMedicoNome() != null ? mongoConsulta.getMedicoNome() : "";
            corbaConsulta.especialidade = mongoConsulta.getEspecialidade() != null ? mongoConsulta.getEspecialidade() : "";
            corbaConsulta.data = mongoConsulta.getData() != null ? mongoConsulta.getData().format(DATE_FORMATTER) : "";
            corbaConsulta.hora = mongoConsulta.getHora() != null ? mongoConsulta.getHora().format(TIME_FORMATTER) : "";
            corbaConsulta.status = mongoConsulta.getStatus() != null ? mongoConsulta.getStatus() : "AGENDADA";
            corbaConsulta.observacoes = "Consulta agendada via sistema"; // Valor padrão para observações
            
            // Validação adicional para garantir que nenhum campo seja null
            if (corbaConsulta.id == null) corbaConsulta.id = "";
            if (corbaConsulta.pacienteNome == null) corbaConsulta.pacienteNome = "";
            if (corbaConsulta.medicoNome == null) corbaConsulta.medicoNome = "";
            if (corbaConsulta.data == null) corbaConsulta.data = "";
            if (corbaConsulta.hora == null) corbaConsulta.hora = "";
            if (corbaConsulta.status == null) corbaConsulta.status = "AGENDADA";
            if (corbaConsulta.especialidade == null) corbaConsulta.especialidade = "";
            if (corbaConsulta.observacoes == null) corbaConsulta.observacoes = "";
            
            logger.debug("Consulta convertida com sucesso. ID: {}, Status: {}, Paciente: {}, Médico: {}", 
                corbaConsulta.id, corbaConsulta.status, corbaConsulta.pacienteNome, corbaConsulta.medicoNome);
            return corbaConsulta;
        } catch (Exception e) {
            logger.error("Erro ao converter ConsultaMongo para ConsultaApp.Consulta: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao converter consulta", e);
        }
    }

    public com.example.consulta.model.Consulta getConsulta(String id) {
        try {
            logger.debug("Buscando consulta por ID: {}", id);
            
            // Primeiro tenta buscar no MongoDB
            Optional<ConsultaMongo> mongoConsultaOpt = consultaMongoService.findById(id);
            if (mongoConsultaOpt.isPresent()) {
                ConsultaMongo mongoConsulta = mongoConsultaOpt.get();
                logger.debug("Consulta encontrada no MongoDB: {}", mongoConsulta);
                
                // Tenta buscar no CORBA
                try {
                    ConsultaApp.Consulta corbaConsulta = getConsultaMedica().getConsulta(id);
                    if (corbaConsulta != null) {
                        logger.debug("Consulta encontrada no CORBA: {}", corbaConsulta);
                        
                        // Criar modelo com os dados do CORBA
                        com.example.consulta.model.Consulta modelConsulta = new com.example.consulta.model.Consulta();
                        modelConsulta.setId(corbaConsulta.id);
                        modelConsulta.setPacienteId(mongoConsulta.getPacienteId());
                        modelConsulta.setPacienteNome(mongoConsulta.getPacienteNome());
                        modelConsulta.setMedicoId(mongoConsulta.getMedicoId());
                        modelConsulta.setMedicoNome(mongoConsulta.getMedicoNome());
                        modelConsulta.setEspecialidade(corbaConsulta.especialidade);
                        modelConsulta.setData(corbaConsulta.data);
                        modelConsulta.setHora(corbaConsulta.hora);
                        
                        // SEMPRE usar o status do MongoDB, pois é a fonte de verdade
                        logger.debug("Usando status do MongoDB: {}", mongoConsulta.getStatus());
                        modelConsulta.setStatus(mongoConsulta.getStatus());
                        
                        modelConsulta.setObservacoes(corbaConsulta.observacoes != null ? corbaConsulta.observacoes : "");
                    
                    return modelConsulta;
                }
            } catch (Exception e) {
                    logger.error("Erro ao buscar consulta no CORBA: {}", e.getMessage(), e);
                }
                
                // Se não encontrou no CORBA ou houve erro, converte a consulta do MongoDB
                return convertMongoToModelConsulta(mongoConsulta);
            }
            
            logger.warn("Consulta não encontrada no MongoDB: {}", id);
            return null;
        } catch (Exception e) {
            logger.error("Erro ao buscar consulta: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao buscar consulta", e);
        }
    }

    public void agendarConsulta(com.example.consulta.model.Consulta consulta) {
        try {
            logger.debug("Agendando consulta para paciente {} com médico {}", consulta.getPacienteId(), consulta.getMedicoId());
            
            // Buscar paciente e médico no MongoDB primeiro
            PacienteMongo pacienteMongo = pacienteMongoService.findById(consulta.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
            
            MedicoMongo medicoMongo = medicoMongoService.findById(consulta.getMedicoId())
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));
            
            // Gerar ID para consulta
            String consultaId = "C" + System.currentTimeMillis();
            
            // Converter data e hora de String para LocalDate e LocalTime
            LocalDate data = LocalDate.parse(consulta.getData(), DATE_FORMATTER);
            LocalTime hora = LocalTime.parse(consulta.getHora(), TIME_FORMATTER);

            // Definir status como AGENDADA
            String status = "AGENDADA";
            if (consulta.getStatus() != null && !consulta.getStatus().isEmpty()) {
                status = consulta.getStatus();
            }

            // Tentar agendar no CORBA primeiro
            ConsultaApp.Consulta consultaCorba = null;
            try {
                // Agendar no CORBA usando o ID gerado
                consultaCorba = getConsultaMedica().agendarConsultaComId(
                    consultaId,
                    pacienteMongo.getId(),
                    pacienteMongo.getNome(),
                    medicoMongo.getId(),
                    medicoMongo.getNome(),
                    consulta.getData(),
                    consulta.getHora(),
                    medicoMongo.getEspecialidade(),
                    status
                );
                
                if (consultaCorba == null) {
                    logger.error("CORBA retornou null ao tentar agendar consulta");
                    throw new RuntimeException("Falha ao agendar consulta no CORBA");
                }
                
                // Verificar se o ID retornado é o mesmo que enviamos
                if (!consultaId.equals(consultaCorba.id)) {
                    logger.error("CORBA retornou um ID diferente do enviado. Enviado: {}, Recebido: {}", consultaId, consultaCorba.id);
                    throw new RuntimeException("Inconsistência de IDs entre sistemas");
                }
                
                logger.debug("Consulta agendada com sucesso no CORBA com ID: {}", consultaId);
            } catch (Exception e) {
                logger.error("Erro ao agendar consulta no CORBA: {}", e.getMessage());
                throw new RuntimeException("Falha ao agendar consulta no CORBA", e);
            }

            // Se chegou aqui, o CORBA foi bem sucedido. Agora salvar no MongoDB
            ConsultaMongo consultaMongo = new ConsultaMongo(
                consultaId,
                consulta.getPacienteId(),
                pacienteMongo.getNome(),
                consulta.getMedicoId(),
                medicoMongo.getNome(),
                medicoMongo.getEspecialidade(),
                data,
                hora,
                status
            );
            
            // Salvar no MongoDB
            consultaMongoService.save(consultaMongo);
            logger.debug("Consulta salva no MongoDB com ID: {} e status: {}", consultaId, status);
            
        } catch (Exception e) {
            logger.error("Erro ao agendar consulta: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao agendar consulta: " + e.getMessage(), e);
        }
    }

    public boolean cancelarConsulta(String id) {
        try {
            logger.info("Tentando cancelar consulta com ID: {}", id);
            
            // Primeiro verifica se a consulta existe no MongoDB
            ConsultaMongo consultaMongo = consultaMongoService.findById(id)
                .orElseThrow(() -> new ConsultaNotFoundException("Consulta não encontrada"));
            
            // Atualiza o status no MongoDB primeiro
            consultaMongo.setStatus("CANCELADA");
            consultaMongoService.save(consultaMongo);
            logger.info("Status atualizado no MongoDB para CANCELADA");
            
            // Tenta cancelar no CORBA
            try {
                // Garante que todos os campos necessários estejam preenchidos
                ConsultaApp.Consulta consultaCorba = convertMongoToCorbaConsulta(consultaMongo);
                boolean resultado = getConsultaMedica().cancelarConsulta(id);
                logger.info("Resultado do cancelamento no CORBA: {}", resultado);
                
                // Verifica se o status foi atualizado no CORBA
                ConsultaApp.Consulta consultaAtualizada = getConsultaMedica().getConsulta(id);
                if (consultaAtualizada != null && !"CANCELADA".equals(consultaAtualizada.status)) {
                    logger.warn("Status no CORBA não foi atualizado para CANCELADA. Atualizando manualmente.");
                    getConsultaMedica().atualizarConsulta(id, "CANCELADA");
                }
                
                return resultado;
            } catch (Exception e) {
                logger.warn("Erro ao cancelar consulta no CORBA: {}", e.getMessage());
                // Se falhar no CORBA, ainda retorna true pois o MongoDB foi atualizado
                return true;
            }
        } catch (ConsultaNotFoundException e) {
            logger.error("Consulta não encontrada: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao cancelar consulta: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao cancelar consulta", e);
        }
    }

    public List<com.example.consulta.model.Consulta> getConsultasPorMedico(String medicoId) {
        try {
            logger.debug("Buscando consultas do médico {} no MongoDB", medicoId);
            List<ConsultaMongo> consultasMongo = consultaMongoService.findByMedicoId(medicoId);
            logger.debug("Encontradas {} consultas no MongoDB", consultasMongo.size());
            
            // Converter consultas do MongoDB para o modelo
            List<com.example.consulta.model.Consulta> consultasMongoModel = consultasMongo.stream()
                .map(this::convertMongoToModelConsulta)
                .collect(Collectors.toList());
            
            // Buscar consultas do CORBA
            try {
                ConsultaApp.Consulta[] consultasCorba = getConsultaMedica().getConsultasPorMedico(medicoId);
                logger.debug("Encontradas {} consultas no CORBA", consultasCorba.length);
                
                // Converter consultas do CORBA para o modelo
                List<com.example.consulta.model.Consulta> consultasCorbaModel = Arrays.stream(consultasCorba)
                    .map(this::convertToModelConsulta)
                    .collect(Collectors.toList());
                
                // Combinar as listas, priorizando as consultas do MongoDB
                Set<String> ids = new HashSet<>();
                List<com.example.consulta.model.Consulta> todasConsultas = new ArrayList<>();
                
                // Adicionar consultas do MongoDB primeiro (prioridade)
                for (com.example.consulta.model.Consulta c : consultasMongoModel) {
                    if (ids.add(c.getId())) {
                        todasConsultas.add(c);
                    }
                }
                
                // Adicionar consultas do CORBA que não estão no MongoDB
                for (com.example.consulta.model.Consulta c : consultasCorbaModel) {
                    if (ids.add(c.getId())) {
                        todasConsultas.add(c);
                    }
                }
                
                return todasConsultas;
            } catch (Exception e) {
                logger.warn("Erro ao buscar consultas do CORBA: {}", e.getMessage());
                // Se falhar no CORBA, retorna apenas as consultas do MongoDB
                return consultasMongo.stream()
                    .map(this::convertMongoToModelConsulta)
                    .collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar consultas do médico {}: {}", medicoId, e.getMessage(), e);
            throw new RuntimeException("Falha ao buscar consultas do médico", e);
        }
    }

    public List<com.example.consulta.model.Consulta> getConsultasPorPaciente(String pacienteId) {
        try {
            logger.debug("Buscando consultas do paciente {} no MongoDB", pacienteId);
            List<ConsultaMongo> consultasMongo = consultaMongoService.findByPacienteId(pacienteId);
            logger.debug("Encontradas {} consultas no MongoDB", consultasMongo.size());
            
            return consultasMongo.stream()
                .map(this::convertMongoToModelConsulta)
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Erro ao buscar consultas do paciente {}: {}", pacienteId, e.getMessage(), e);
            throw new RuntimeException("Falha ao buscar consultas do paciente", e);
        }
    }

    public List<com.example.consulta.model.Medico> getTodosMedicos() {
        try {
            logger.debug("Buscando todos os médicos no MongoDB");
            List<MedicoMongo> medicosMongo = medicoMongoService.findAll();
            logger.debug("Encontrados {} médicos no MongoDB", medicosMongo.size());
            
            return medicosMongo.stream()
                .map(mongo -> {
                    com.example.consulta.model.Medico medico = new com.example.consulta.model.Medico();
                    medico.setId(mongo.getId());
                    medico.setNome(mongo.getNome());
                    medico.setEspecialidade(mongo.getEspecialidade());
                    medico.setCrm(mongo.getCrm());
                    medico.setEmail(mongo.getEmail());
                    medico.setTelefone(mongo.getTelefone());
                    return medico;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os médicos no MongoDB: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao buscar médicos", e);
        }
    }

    public List<com.example.consulta.model.Paciente> getTodosPacientes() {
        try {
            logger.debug("Buscando todos os pacientes no MongoDB");
            List<PacienteMongo> pacientesMongo = pacienteMongoService.findAll();
            logger.debug("Encontrados {} pacientes no MongoDB", pacientesMongo.size());
            
            return pacientesMongo.stream()
                .map(mongo -> {
                    com.example.consulta.model.Paciente paciente = new com.example.consulta.model.Paciente();
                    paciente.setId(mongo.getId());
                    paciente.setNome(mongo.getNome());
                    paciente.setCpf(mongo.getCpf());
                    paciente.setEmail(mongo.getEmail());
                    paciente.setTelefone(mongo.getTelefone());
                    paciente.setDataNascimento(new SimpleDateFormat("yyyy-MM-dd").format(mongo.getDataNascimento()));
                    return paciente;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os pacientes no MongoDB: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao buscar pacientes", e);
        }
    }

    public List<com.example.consulta.model.Medico> getMedicosPorEspecialidade(String especialidade) {
        ConsultaApp.Medico[] medicos = getConsultaMedica().getMedicosPorEspecialidade(especialidade);
        return Arrays.stream(medicos)
            .map(this::convertToModelMedico)
            .collect(Collectors.toList());
    }

    private com.example.consulta.model.Consulta convertToModelConsulta(ConsultaApp.Consulta corbaConsulta) {
        try {
            logger.debug("Convertendo ConsultaApp.Consulta para model.Consulta: {}", corbaConsulta.id);
            com.example.consulta.model.Consulta modelConsulta = new com.example.consulta.model.Consulta();
            modelConsulta.setId(corbaConsulta.id);
            
            // Buscar paciente e médico pelo nome para obter seus IDs
            try {
                // Buscar paciente pelo nome
                ConsultaApp.Paciente[] pacientes = getConsultaMedica().getPacientes();
                for (ConsultaApp.Paciente p : pacientes) {
                    if (p.nome.equals(corbaConsulta.pacienteNome)) {
                        modelConsulta.setPacienteId(p.id);
                        break;
                    }
                }
                
                // Buscar médico pelo nome
                ConsultaApp.Medico[] medicos = getConsultaMedica().getMedicos();
                for (ConsultaApp.Medico m : medicos) {
                    if (m.nome.equals(corbaConsulta.medicoNome)) {
                        modelConsulta.setMedicoId(m.id);
                        break;
                    }
                }
            } catch (Exception e) {
                logger.warn("Erro ao buscar IDs de paciente/médico: {}", e.getMessage());
            }
            
            modelConsulta.setPacienteNome(corbaConsulta.pacienteNome);
            modelConsulta.setMedicoNome(corbaConsulta.medicoNome);
            modelConsulta.setData(corbaConsulta.data);
            modelConsulta.setHora(corbaConsulta.hora);
            
            // Tentar buscar o status do MongoDB
            try {
                Optional<ConsultaMongo> mongoConsultaOpt = consultaMongoService.findById(corbaConsulta.id);
                if (mongoConsultaOpt.isPresent()) {
                    ConsultaMongo mongoConsulta = mongoConsultaOpt.get();
                    logger.debug("Usando status do MongoDB: {}", mongoConsulta.getStatus());
                    modelConsulta.setStatus(mongoConsulta.getStatus());
                } else {
                    // Se não encontrar no MongoDB, usar o status do CORBA
                    if (corbaConsulta.status == null || corbaConsulta.status.isEmpty()) {
                        logger.warn("Status do CORBA é nulo ou vazio, usando AGENDADA como padrão");
                        modelConsulta.setStatus("AGENDADA");
                    } else {
                        logger.debug("Usando status do CORBA: {}", corbaConsulta.status);
                        modelConsulta.setStatus(corbaConsulta.status);
                    }
                }
            } catch (Exception e) {
                logger.warn("Erro ao buscar status do MongoDB: {}", e.getMessage());
                // Em caso de erro, usar o status do CORBA
                if (corbaConsulta.status == null || corbaConsulta.status.isEmpty()) {
                    logger.warn("Status do CORBA é nulo ou vazio, usando AGENDADA como padrão");
                    modelConsulta.setStatus("AGENDADA");
                } else {
                    logger.debug("Usando status do CORBA: {}", corbaConsulta.status);
            modelConsulta.setStatus(corbaConsulta.status);
                }
            }
            
            modelConsulta.setEspecialidade(corbaConsulta.especialidade);
            modelConsulta.setObservacoes(corbaConsulta.observacoes != null ? corbaConsulta.observacoes : "");
            return modelConsulta;
        } catch (Exception e) {
            logger.error("Erro ao converter ConsultaApp.Consulta para model.Consulta: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao converter consulta", e);
        }
    }

    private com.example.consulta.model.Medico convertToModelMedico(ConsultaApp.Medico corbaMedico) {
        com.example.consulta.model.Medico modelMedico = new com.example.consulta.model.Medico();
        modelMedico.setId(corbaMedico.id);
        modelMedico.setNome(corbaMedico.nome);
        modelMedico.setEspecialidade(corbaMedico.especialidade);
        modelMedico.setCrm(corbaMedico.crm);
        modelMedico.setEmail(corbaMedico.email);
        modelMedico.setTelefone(corbaMedico.telefone);
        return modelMedico;
    }

    private com.example.consulta.model.Paciente convertToModelPaciente(ConsultaApp.Paciente corbaPaciente) {
        com.example.consulta.model.Paciente modelPaciente = new com.example.consulta.model.Paciente();
        modelPaciente.setId(corbaPaciente.id);
        modelPaciente.setNome(corbaPaciente.nome);
        modelPaciente.setCpf(corbaPaciente.cpf);
        modelPaciente.setEmail(corbaPaciente.email);
        modelPaciente.setTelefone(corbaPaciente.telefone);
        modelPaciente.setDataNascimento(corbaPaciente.dataNascimento);
        return modelPaciente;
    }

    public void adicionarMedico(String nome, String especialidade, String crm, String email, String telefone) {
        try {
            logger.info("Adicionando novo médico: {}", nome);
            
            // Criar objeto MedicoMongo
            MedicoMongo medicoMongo = new MedicoMongo();
            medicoMongo.setId("M" + System.currentTimeMillis());
            medicoMongo.setNome(nome);
            medicoMongo.setEspecialidade(especialidade);
            medicoMongo.setCrm(crm);
            medicoMongo.setEmail(email);
            medicoMongo.setTelefone(telefone);
            
            // Salvar no MongoDB
            medicoMongoService.save(medicoMongo);
            logger.info("Médico adicionado com sucesso no MongoDB");
            
            // Criar objeto CORBA para sincronização
            Medico medicoCorba = new Medico();
            medicoCorba.id = medicoMongo.getId();
            medicoCorba.nome = medicoMongo.getNome();
            medicoCorba.especialidade = medicoMongo.getEspecialidade();
            medicoCorba.crm = medicoMongo.getCrm();
            medicoCorba.email = medicoMongo.getEmail();
            medicoCorba.telefone = medicoMongo.getTelefone();
            
            // Salvar no CORBA
            consultaMongoService.saveMedico(medicoCorba);
            logger.info("Médico adicionado com sucesso no CORBA");
        } catch (Exception e) {
            logger.error("Erro ao adicionar médico: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao adicionar médico", e);
        }
    }

    public void adicionarPaciente(String nome, String cpf, String email, String telefone, String dataNascimento) {
        try {
            logger.info("Adicionando novo paciente: {}", nome);
            
            // Criar objeto PacienteMongo
            PacienteMongo pacienteMongo = new PacienteMongo();
            pacienteMongo.setId("P" + System.currentTimeMillis());
            pacienteMongo.setNome(nome);
            pacienteMongo.setCpf(cpf);
            pacienteMongo.setEmail(email);
            pacienteMongo.setTelefone(telefone);
            
            // Converter a String de data para Date
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date data = sdf.parse(dataNascimento);
                pacienteMongo.setDataNascimento(data);
            } catch (ParseException e) {
                logger.warn("Erro ao converter data de nascimento: {}", e.getMessage());
                pacienteMongo.setDataNascimento(new Date());
            }
            
            // Salvar no MongoDB
            pacienteMongoService.save(pacienteMongo);
            logger.info("Paciente adicionado com sucesso no MongoDB");
            
            // Criar objeto CORBA para sincronização
            Paciente pacienteCorba = new Paciente();
            pacienteCorba.id = pacienteMongo.getId();
            pacienteCorba.nome = pacienteMongo.getNome();
            pacienteCorba.cpf = pacienteMongo.getCpf();
            pacienteCorba.email = pacienteMongo.getEmail();
            pacienteCorba.telefone = pacienteMongo.getTelefone();
            pacienteCorba.dataNascimento = dataNascimento;
            
            // Salvar no CORBA
            consultaMongoService.savePaciente(pacienteCorba);
            logger.info("Paciente adicionado com sucesso no CORBA");
        } catch (Exception e) {
            logger.error("Erro ao adicionar paciente: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao adicionar paciente", e);
        }
    }

    public void atualizarConsulta(Consulta consulta) {
        try {
            logger.info("Atualizando consulta com ID: {}", consulta.getId());
            
            // Buscar a consulta existente no MongoDB
            ConsultaMongo consultaMongo = consultaMongoService.findById(consulta.getId())
                .orElseThrow(() -> new ConsultaNotFoundException("Consulta não encontrada"));
            
            // Atualizar os campos, preservando os valores existentes se não forem fornecidos
            if (consulta.getPacienteId() != null && !consulta.getPacienteId().isEmpty()) {
                consultaMongo.setPacienteId(consulta.getPacienteId());
            }
            if (consulta.getPacienteNome() != null && !consulta.getPacienteNome().isEmpty()) {
                consultaMongo.setPacienteNome(consulta.getPacienteNome());
            }
            if (consulta.getMedicoId() != null && !consulta.getMedicoId().isEmpty()) {
                consultaMongo.setMedicoId(consulta.getMedicoId());
            }
            if (consulta.getMedicoNome() != null && !consulta.getMedicoNome().isEmpty()) {
                consultaMongo.setMedicoNome(consulta.getMedicoNome());
            }
            if (consulta.getEspecialidade() != null && !consulta.getEspecialidade().isEmpty()) {
                consultaMongo.setEspecialidade(consulta.getEspecialidade());
            }
            
            // Preservar data e hora existentes se não forem fornecidas
            if (consulta.getData() != null && !consulta.getData().isEmpty()) {
                try {
                    consultaMongo.setData(LocalDate.parse(consulta.getData(), DATE_FORMATTER));
                } catch (Exception e) {
                    logger.warn("Erro ao fazer parse da data: {}. Mantendo data existente.", e.getMessage());
                }
            }
            if (consulta.getHora() != null && !consulta.getHora().isEmpty()) {
                try {
                    consultaMongo.setHora(LocalTime.parse(consulta.getHora(), TIME_FORMATTER));
                } catch (Exception e) {
                    logger.warn("Erro ao fazer parse da hora: {}. Mantendo hora existente.", e.getMessage());
                }
            }
            
            // Atualizar status se fornecido
            if (consulta.getStatus() != null && !consulta.getStatus().isEmpty()) {
                consultaMongo.setStatus(consulta.getStatus());
            }
            
            // Salvar no MongoDB primeiro
            consultaMongoService.save(consultaMongo);
            logger.info("Consulta atualizada com sucesso no MongoDB");
            
            // Tentar atualizar no CORBA, mas não falhar se não conseguir
            try {
                // Primeiro tenta obter a consulta do CORBA
                ConsultaApp.Consulta consultaCorba = getConsultaMedica().getConsulta(consulta.getId());
                
                // Se a consulta não existe no CORBA, cria uma nova com todos os dados
                if (consultaCorba == null) {
                    logger.info("Consulta não encontrada no CORBA, criando nova consulta");
                    consultaCorba = getConsultaMedica().agendarConsulta(
                        consultaMongo.getPacienteId(),
                        consultaMongo.getMedicoId(),
                        consultaMongo.getData().format(DATE_FORMATTER),
                        consultaMongo.getHora().format(TIME_FORMATTER),
                        consultaMongo.getEspecialidade()
                    );
                    
                    // Verifica se a consulta foi criada com sucesso
                    if (consultaCorba == null) {
                        logger.warn("Falha ao criar consulta no CORBA, mas a consulta já foi atualizada no MongoDB");
                    } else {
                        logger.info("Nova consulta criada no CORBA");
                    }
                }
                
                // Atualiza o status no CORBA
                ConsultaApp.Consulta consultaAtualizada = getConsultaMedica().atualizarConsulta(consulta.getId(), consultaMongo.getStatus());
                if (consultaAtualizada == null) {
                    logger.warn("Falha ao atualizar status da consulta no CORBA, mas a consulta já foi atualizada no MongoDB");
                } else {
                    logger.info("Status da consulta atualizado no CORBA");
                }
            } catch (Exception e) {
                logger.warn("Erro ao atualizar consulta no CORBA: {}. A consulta foi atualizada apenas no MongoDB.", e.getMessage());
                // Não lança exceção, apenas registra o erro
            }
        } catch (ConsultaNotFoundException e) {
            logger.error("Consulta não encontrada: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao atualizar consulta: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao atualizar consulta", e);
        }
    }
} 