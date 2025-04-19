package com.example.consulta;

import ConsultaApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

public class ConsultaMedicaServer {
    public static void main(String args[]) {
        try {
            // Cria e inicializa o ORB
            ORB orb = ORB.init(args, null);

            // Obtém referência para o rootpoa e ativa o POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // Cria o servant e registra com o ORB
            ConsultaMedicaImpl consultaMedicaImpl = new ConsultaMedicaImpl();
            consultaMedicaImpl.setORB(orb);

            // Adiciona alguns dados iniciais para teste
            consultaMedicaImpl.adicionarMedico("Dr. João Silva", "Clínico Geral", "12345/SP", "joao.silva@medico.com", "(11) 99999-9999");
            consultaMedicaImpl.adicionarMedico("Dra. Maria Santos", "Cardiologista", "54321/SP", "maria.santos@medico.com", "(11) 98888-8888");
            consultaMedicaImpl.adicionarPaciente("José Oliveira", "123.456.789-00", "jose.oliveira@email.com", "(11) 97777-7777", "1980-01-01");
            consultaMedicaImpl.adicionarPaciente("Ana Costa", "987.654.321-00", "ana.costa@email.com", "(11) 96666-6666", "1990-05-15");

            // Obtém referência do objeto do servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(consultaMedicaImpl);
            ConsultaMedica href = ConsultaMedicaHelper.narrow(ref);

            // Obtém o root naming context
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // Liga o objeto ConsultaMedica ao naming
            String name = "ConsultaMedica";
            NameComponent path[] = ncRef.to_name(name);
            ncRef.rebind(path, href);

            System.out.println("ConsultaMedicaServer pronto e esperando ...");

            // Espera por invocações de clientes
            orb.run();
        } catch (Exception e) {
            System.err.println("ERRO: " + e);
            e.printStackTrace(System.out);
        }

        System.out.println("ConsultaMedicaServer finalizando ...");
    }
} 