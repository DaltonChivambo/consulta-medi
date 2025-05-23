package ConsultaApp;


/**
* ConsultaApp/ConsultaMedicaPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ConsultaMedica.idl
* Saturday, April 19, 2025 7:49:40 PM CAT
*/

public abstract class ConsultaMedicaPOA extends org.omg.PortableServer.Servant
 implements ConsultaApp.ConsultaMedicaOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("agendarConsulta", new java.lang.Integer (0));
    _methods.put ("agendarConsultaComId", new java.lang.Integer (1));
    _methods.put ("cancelarConsulta", new java.lang.Integer (2));
    _methods.put ("getConsulta", new java.lang.Integer (3));
    _methods.put ("getConsultasPorPaciente", new java.lang.Integer (4));
    _methods.put ("getConsultasPorMedico", new java.lang.Integer (5));
    _methods.put ("getConsultasPorData", new java.lang.Integer (6));
    _methods.put ("getMedicosPorEspecialidade", new java.lang.Integer (7));
    _methods.put ("getMedico", new java.lang.Integer (8));
    _methods.put ("getPaciente", new java.lang.Integer (9));
    _methods.put ("getPacientes", new java.lang.Integer (10));
    _methods.put ("getMedicos", new java.lang.Integer (11));
    _methods.put ("atualizarConsulta", new java.lang.Integer (12));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // ConsultaApp/ConsultaMedica/agendarConsulta
       {
         String pacienteId = in.read_string ();
         String medicoId = in.read_string ();
         String data = in.read_string ();
         String hora = in.read_string ();
         String especialidade = in.read_string ();
         ConsultaApp.Consulta $result = null;
         $result = this.agendarConsulta (pacienteId, medicoId, data, hora, especialidade);
         out = $rh.createReply();
         ConsultaApp.ConsultaHelper.write (out, $result);
         break;
       }

       case 1:  // ConsultaApp/ConsultaMedica/agendarConsultaComId
       {
         String consultaId = in.read_string ();
         String pacienteId = in.read_string ();
         String pacienteNome = in.read_string ();
         String medicoId = in.read_string ();
         String medicoNome = in.read_string ();
         String data = in.read_string ();
         String hora = in.read_string ();
         String especialidade = in.read_string ();
         String status = in.read_string ();
         ConsultaApp.Consulta $result = null;
         $result = this.agendarConsultaComId (consultaId, pacienteId, pacienteNome, medicoId, medicoNome, data, hora, especialidade, status);
         out = $rh.createReply();
         ConsultaApp.ConsultaHelper.write (out, $result);
         break;
       }

       case 2:  // ConsultaApp/ConsultaMedica/cancelarConsulta
       {
         String consultaId = in.read_string ();
         boolean $result = false;
         $result = this.cancelarConsulta (consultaId);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 3:  // ConsultaApp/ConsultaMedica/getConsulta
       {
         String consultaId = in.read_string ();
         ConsultaApp.Consulta $result = null;
         $result = this.getConsulta (consultaId);
         out = $rh.createReply();
         ConsultaApp.ConsultaHelper.write (out, $result);
         break;
       }

       case 4:  // ConsultaApp/ConsultaMedica/getConsultasPorPaciente
       {
         String pacienteId = in.read_string ();
         ConsultaApp.Consulta $result[] = null;
         $result = this.getConsultasPorPaciente (pacienteId);
         out = $rh.createReply();
         ConsultaApp.ConsultaSeqHelper.write (out, $result);
         break;
       }

       case 5:  // ConsultaApp/ConsultaMedica/getConsultasPorMedico
       {
         String medicoId = in.read_string ();
         ConsultaApp.Consulta $result[] = null;
         $result = this.getConsultasPorMedico (medicoId);
         out = $rh.createReply();
         ConsultaApp.ConsultaSeqHelper.write (out, $result);
         break;
       }

       case 6:  // ConsultaApp/ConsultaMedica/getConsultasPorData
       {
         String data = in.read_string ();
         ConsultaApp.Consulta $result[] = null;
         $result = this.getConsultasPorData (data);
         out = $rh.createReply();
         ConsultaApp.ConsultaSeqHelper.write (out, $result);
         break;
       }

       case 7:  // ConsultaApp/ConsultaMedica/getMedicosPorEspecialidade
       {
         String especialidade = in.read_string ();
         ConsultaApp.Medico $result[] = null;
         $result = this.getMedicosPorEspecialidade (especialidade);
         out = $rh.createReply();
         ConsultaApp.MedicoSeqHelper.write (out, $result);
         break;
       }

       case 8:  // ConsultaApp/ConsultaMedica/getMedico
       {
         String medicoId = in.read_string ();
         ConsultaApp.Medico $result = null;
         $result = this.getMedico (medicoId);
         out = $rh.createReply();
         ConsultaApp.MedicoHelper.write (out, $result);
         break;
       }

       case 9:  // ConsultaApp/ConsultaMedica/getPaciente
       {
         String pacienteId = in.read_string ();
         ConsultaApp.Paciente $result = null;
         $result = this.getPaciente (pacienteId);
         out = $rh.createReply();
         ConsultaApp.PacienteHelper.write (out, $result);
         break;
       }

       case 10:  // ConsultaApp/ConsultaMedica/getPacientes
       {
         ConsultaApp.Paciente $result[] = null;
         $result = this.getPacientes ();
         out = $rh.createReply();
         ConsultaApp.PacienteSeqHelper.write (out, $result);
         break;
       }

       case 11:  // ConsultaApp/ConsultaMedica/getMedicos
       {
         ConsultaApp.Medico $result[] = null;
         $result = this.getMedicos ();
         out = $rh.createReply();
         ConsultaApp.MedicoSeqHelper.write (out, $result);
         break;
       }

       case 12:  // ConsultaApp/ConsultaMedica/atualizarConsulta
       {
         String consultaId = in.read_string ();
         String status = in.read_string ();
         ConsultaApp.Consulta $result = null;
         $result = this.atualizarConsulta (consultaId, status);
         out = $rh.createReply();
         ConsultaApp.ConsultaHelper.write (out, $result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:ConsultaApp/ConsultaMedica:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public ConsultaMedica _this() 
  {
    return ConsultaMedicaHelper.narrow(
    super._this_object());
  }

  public ConsultaMedica _this(org.omg.CORBA.ORB orb) 
  {
    return ConsultaMedicaHelper.narrow(
    super._this_object(orb));
  }


} // class ConsultaMedicaPOA
