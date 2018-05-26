package BUSINESS;

import java.io.*;
import java.security.*;
import java.lang.reflect.*;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * Title:        Business Services
 * Description:  Clases encargadas de la aplicacion o logica de negocios
 * Copyright:    Copyright (c) 2003
 * Company:      Entidad Financiera
 * @author Ivan Paniagua
 * @version 1.0
 */

public class Cargar extends ClassLoader
{
  // These are set up in the constructor, and later
  // used in the loadClass() method for decrypting the classes
  private SecretKey key;
  private Cipher cipher;

  // Constructor: set up the objects we need for decryption
  public Cargar( SecretKey key ) throws GeneralSecurityException, IOException {
    this.key = key;
    String algorithm = "Twofish/ECB/PKCS5Padding";
    cipher = Cipher.getInstance( algorithm );
    cipher.init( Cipher.DECRYPT_MODE, key);
  }

  // Main routine: here, we read in the key, and create
  // an instance of DecryptRun, which is our custom ClassLoader.
  // After we've set up the ClassLoader, we use it to
  // load up an instance of the main class of the application.
  // Finally, we call the main method of this class via
  // the Java Reflection API
  static public void main( String args[] ) throws Exception {
    String keyFilename = "llave.key";//args[0];
    String appName = "Borrar";//"classes/business/Borrar";//args[1];
    // Read in the key
    System.err.println( "[Leyendo llave]" );
    byte rawKey[] = Util.leerArchivo( keyFilename );
    SecretKeySpec key = new SecretKeySpec(rawKey,"Twofish");
    // Create a decrypting ClassLoader
    Cargar dr = new Cargar( key );

    // Create an instance of the application's main class,
    // loading it through the ClassLoader
    System.err.println( "[DecryptRun: loading "+appName+"]" );
    Class clasz = dr.mio(appName);//dr.loadClass( appName );
    for (int i = 0; i < clasz.getMethods().length; i++) {
      System.out.println("este un metodo" + clasz.getMethods()[i]);
    }

    Mio algo = (Mio)clasz.newInstance();

    Usuario u= algo.getUsuario("ivan");
    if ( algo == null ) {
      System.out.println("la clase es nula");
    }

    System.out.println("es el password de mio"+u.getPassword());

    /*RECIEN
    Mio a = (Mio)clasz.newInstance();
    Usuario uu= a.getUsuario("mio");
    System.out.println("es el password de mio"+uu.getPassword());*/

    // Finally, call the main() routine of this instance
    // using the Reflection API

    // These are the arguments to the application itself
//    String realArgs[] = new String[args.length-2];
//    System.arraycopy( args, 2, realArgs, 0, args.length-2 );


    // Grab a reference to main()
//    String proto[] = new String[1];
//    Class mainArgs[] = { (new String[1]).getClass() };
//    Method main = clasz.getMethod( "main", mainArgs );
//
    // Create an array containing the arguments to main()
//    Object argsArray[] = { realArgs };
//    System.err.println( "[DecryptRun: running "+appName+".main()]" );
//
//    // Call main().  We've handed execution off to the
//    // application, and we're done!
//    main.invoke( null, argsArray );
  }

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

  public Class loadClass( String name, boolean resolve ) throws ClassNotFoundException {
    try {
      // This will be the Class object that we create.
      // Note that we call it "clasz" instead of "class"
      // because "class" is a reserved word in Java
      Class clasz = null;

      // Obligatory step 1: if the class is already in the
      // system cache, we don't need to load it again
      clasz = findLoadedClass( name );

      if (clasz != null)
        return clasz;

      // Now we get to the custom part
      try {
        // Read the encrypted class file
        byte classData[] = Util.leerArchivo( name+".class" );


//        RandomAccessFile randomaccessfile = new RandomAccessFile(getClass().getName() + ".bin", "r");
//        randomaccessfile.seek(randomaccessfile.readInt() != 0xdeadfeed ? 0 : randomaccessfile.readInt());
//        String s1;




        if (classData != null) {
          // decrypt it ...
          byte decryptedClassData[] = cipher.doFinal( classData );
          // ... and turn it into a class
          clasz = defineClass( name+".class", decryptedClassData,0, decryptedClassData.length );
          System.err.println( "[DecryptRun: decrypting class "+name+"]" );
        }
      } catch( FileNotFoundException fnfe ) {
        // It's probably a system file, so this isn't an error
      }

      // Obligatory step 2: if our decryption didn't work,
      // maybe the class is to be found on the filesystem, so we
      // try to load it using the default ClassLoader
      if (clasz == null)
        clasz = findSystemClass( name );

      // Obligatory step 3: if we've been asked to,
      // resolve the class
      if (resolve && clasz != null)
        resolveClass( clasz );

      // Return the class to the caller
      return clasz;
    } catch( IOException ie ) {
      throw new ClassNotFoundException( ie.toString() );
    } catch( GeneralSecurityException gse ) {
      throw new ClassNotFoundException( gse.toString() );
    }
  }
}
