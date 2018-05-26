package BUSINESS;

/**
 * Title:        Business Services
 * Description:  Clases encargadas de la aplicacion o logica de negocios
 * Copyright:    Copyright (c) 2003
 * Company:      Entidad Financiera
 * @author Ivan Paniagua
 * @version 1.0
 */

public class Main{
  public static void main(String []args) throws Exception {
//    String toEncrypt = "Holas!";
//    System.out.println("Encriptando...");
//    byte[] encrypted = Encriptacion.blowfishEncrypt(toEncrypt, "password");
//    System.out.println("Desencriptando...");
//    String decrypted = Encriptacion.blowfishDecrypt(encrypted, "password");
//    System.out.println("Texo desenecriptado: " + decrypted);
//    Encriptacion.Des(toEncrypt);    ../recursos/acerca.jpg"))
//    String archivo="classes/business/Borrar.class"; //CASE SENSITIVE
//    String arch_encr ="Borrar.class";
//    String arch_llave ="llave.key";
//    byte llave[] = Encriptacion.llave();
//    Util.escribirArchivo( arch_llave, llave );
//    byte datos[] = Util.leerArchivo( archivo );
//    byte encr[] = Encriptacion.encrip(datos,llave);
//    Util.escribirArchivo( arch_encr, encr );

    Bd1 bd = Bd1.getBd1();
    Usuario u= bd.getUsuario("ivan");
    System.out.println("es el password de mio"+u.getPassword());

//    Bd1 bd1 = Bd1.getBd1();
//    Usuario uu= bd1.getUsuario("segundo");
//    System.out.println("es el password de mio"+uu.getPassword());
    System.out.println("es el password de mio");
  }
}
/*
	Runtime runtime = Runtime.getRuntime();
	Process process1 = null;
	int r = 1234;
	try {
	   // String[] cmd = { "/bin/cat", "/feepyfoo" };
      String[] cmd = { "E:\\Program Files\\mysql\\bin\\mysql.exe", "-u", "root" ,"<", "bd.sql"};
      process1 = runtime.exec( cmd );
    }
	catch ( IOException e ) {
      System.err.println( "exec: " + e );
      System.exit( 1 );
    }
	try {
      InputStream e = process1.getErrorStream();
      byte[] buf = new byte[1024];//20];
      int len;
      boolean eEof = false;
      while ( ! eEof )  {
      len = e.read( buf );
      if ( len == -1 )
          eEof = true;
      else if ( len != 0 ) {
          System.err.println( "len=" + len );
          System.err.write( buf, 0, len );
      }
      }
      e.close();
    }
	catch ( IOException e )    {
	    System.err.println( "read: " + e );
	    System.exit( 1 );
    }
	System.out.println( "returned " + r );
	}*/
