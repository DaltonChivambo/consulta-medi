package ConsultaApp;


/**
* ConsultaApp/MedicoSeqHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ConsultaMedica.idl
* Saturday, April 19, 2025 7:49:40 PM CAT
*/

public final class MedicoSeqHolder implements org.omg.CORBA.portable.Streamable
{
  public ConsultaApp.Medico value[] = null;

  public MedicoSeqHolder ()
  {
  }

  public MedicoSeqHolder (ConsultaApp.Medico[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = ConsultaApp.MedicoSeqHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    ConsultaApp.MedicoSeqHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return ConsultaApp.MedicoSeqHelper.type ();
  }

}
