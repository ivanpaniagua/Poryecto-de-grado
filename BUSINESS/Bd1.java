package BUSINESS;

import java.io.*;

/**
 * Title:        Business Services
 * Description:  Clases encargadas de la aplicacion o logica de negocios
 * Copyright:    Copyright (c) 2003
 * Company:      Entidad Financiera
 * @author Ivan Paniagua
 * @version 1.0
 */

public class Bd1 extends ClassLoader {
  String keyFilename;
  String appName;
  byte llave_[];
  private static Bd1 bd1;
  Mio m;

  public Bd1() {
    try { jbInit(); }
    catch(Exception e) { e.printStackTrace(); }
  }

  private void jbInit() throws Exception {
    System.out.println("se crea un bd1 donde esta un newInstance");
    keyFilename = "llave.key";
    appName = "Borrar";// name.replace ('.', File.separatorChar)
    llave_ = Util.leerArchivo( keyFilename );
    //Class clasz = loadClass( appName );
   // m = (Mio) clasz.newInstance();
    Borrar algo= new Borrar();
    //Method main = clasz.getMethod( "main", Args );
    //main.invoke( null, argsArray );
  }

  public static Bd1 getBd1(){
    if (bd1 == null) {
      bd1 = new Bd1();
    }
    return bd1;
  }

  public Usuario getUsuario(String login) {
    return m.getUsuario(login);
  }

  public synchronized Class loadClass( String name, boolean resolve ) throws ClassNotFoundException {
  try{
    // Obligatory step 1: if the class is already in the system cache, we don't need to load it again
    Class c = findLoadedClass( name );
    if (c == null) {
      // Now we get to the custom part
      try {
        // Read the encrypted class file
        byte classData[] = Util.leerArchivo( name + ".class" );
        if (classData != null) {
          byte decryptedClassData[] = null;//Encriptacion.desencrip(classData , llave_);
          //metodo magico
          c = defineClass( "business."+name, decryptedClassData,0, decryptedClassData.length );
        }
      }
      catch( FileNotFoundException fnfe ) {
        // It's probably a system file, so this isn't an error
      }
    }
    // Obligatory step 2: if our decryption didn't work, maybe the class is to be found on the filesystem,
    // so we try to load it using the default ClassLoader
    if (c == null)
      c = findSystemClass( name );//originalmente esta solo findClass

    // Obligatory step 3: if we've been asked to, resolve the class
    if (resolve && c != null)
      resolveClass( c );

    return c;
  }
  catch (IOException ie ) { throw new ClassNotFoundException( ie.toString() ); }
  catch (Exception e ) { throw new ClassNotFoundException( e.toString() ); }// es lanazado por Security
  }
}
/*  protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
	// First, check if the class has already been loaded
	Class c = findLoadedClass(name);
	if (c == null) {
	    try {
		if (parent != null) {
		    c = parent.loadClass(name, false);
		} else {
		    c = findBootstrapClass0(name);
		}
	    } catch (ClassNotFoundException e) {
	        // If still not found, then call findClass in order
	        // to find the class.
	        c = findClass(name);
	    }
	}
	if (resolve) {
	    resolveClass(c);
	}
  return c;
  }
*/
/*
  public Class mio(String name) throws Exception {
  Class clasz= null;
  try {
    byte classData[] = Util.leerArchivo( name+".class" );
    byte decryptedClassData[] = cipher.doFinal( classData );
    clasz = defineClass( "business."+name, decryptedClassData,0, decryptedClassData.length );
  }
  catch (IOException io) {
    System.out.println("Error al leer el archivo" + io);
  }
  catch (Exception e) {
    System.out.println("Error al desencriptar o al convertir a clase el archivo" + e);
  }
  return clasz;
  }
*/