//forma extraña de Classloader encryp.pero con agoritmo DES seguramente funciona tambien con los demas pero solo
//es hipotesis
//    SecureRandom sr = new SecureRandom();
//    cipher = Cipher.getInstance( "DES" );
//    cipher.init( Cipher.DECRYPT_MODE, key, sr );//de encriptacion de clases

//en vez de throws Exception se podria utilizar GeneralSecurityException mas especifico, pero
//creia que las clases que lo utilizaban tenian que importar las clases de java.security
//pero no es asi ya que solo necesito capturar las excepciones con Exception (clase padre).

// 2003@ EN JAVA_HOME/lib/security AÑADIR EL ULTIMO
//security.provider.1=sun.security.provider.Sun
//security.provider.2=com.sun.crypto.provider.SunJCE
//security.provider.3=org.bouncycastle.jce.provider.BouncyCastleProvider

package BUSINESS;

import java.security.*;
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

public class Encriptacion {

//TripleDES -"DESede"   - 168 para mayor seguridad el valor mas alto (aunque para DESede es 192)
//Twofish   -"Twofish"  - 128 256
//Blowfish  -"Blowfish" - 128 448
  private static final String algoritmo = "Blowfish";//"DESede";//"Twofish";    //"DESede"
  private static final int longitud = 448;//192;//128;              //168

  /**
   * El texto plano pasado como argumento puede ser de cualquier longitud, pero produce
   * una cadena de tamaño fijo cuya longitud depende del algortimo utilizado.
   * Por conveniencia se usa String en la manipulacion de los datos tanto para recuperar
   * como al almacenar datos en la BD.
   *
   * MD5 posee 128 bits de seguridad y genera 16 bytes de salida (32 numeros en hexadecimal),
   * tambien puede ser SHA pero este posee 160 bits de seguridad generando 20 bytes de salida
   */
  protected static String Digest(String textoplano) { /*throws NoSuchAlgorithmException*/
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("MD5");
//    synchronized (this) {//no estoy seguro de esto
      md.reset();
      md.update(textoplano.getBytes());
      String s = convertir(md.digest());//String s=new String(md.digest()); no sirve, caracteres no legibles
      return s;
//    }
    }
    catch(NoSuchAlgorithmException e) {System.out.println("NO EXISTE LA IMPLEMENATCION DEL ALGORITMO"); return null;}
  }

  /**
   * Para obtener los bytes de una cadena se debe especificar el metodo de codificacion
   * de esta manera puede ser interpretados por otra aplicacion.
   */
  public static String Encriptar(String texto) throws Exception {
    byte llave[] = llave();
    byte[] textoplano = texto.getBytes("UTF8");
    System.out.println("texto plano: ");
    for (int i=0;i<textoplano.length;i++) { System.out.print(textoplano[i]+" "); }

    byte[] textoencrip = encrip(textoplano,llave);
    System.out.println("\ntexto encriptado: ");
    for (int i=0;i<textoencrip.length;i++) { System.out.print(textoencrip[i]+" "); }

    byte[] textodesenc = desencrip(textoencrip, llave);
    String output = new String(textodesenc,"UTF8");
    System.out.println("\ntexto Desencriptado: "+output);

    String retu = convertir(textoencrip);
    return retu;
  }

  /**
   * Creamos una llave binaria desde key (cadena) lo cual es en realidad un seed
   * String key = "algo";
   * SecureRandom sr = new SecureRandom(key.getBytes());
   * todo igual solo en    kg.init(sr);
   */
  public static byte[] llave() throws GeneralSecurityException {
    System.out.println("Generando la llave " + algoritmo + "...");
    KeyGenerator kg = KeyGenerator.getInstance(algoritmo);
    kg.init(longitud);
    long comienzo = System.currentTimeMillis();
    SecretKey llave = kg.generateKey();// Key llave = keyGenerator.generateKey();
    long fin = System.currentTimeMillis();
    double total = (fin - comienzo)/1000.0;
    System.out.println("Termino de generar la llave " +  total +"pero que hay en ecode "+ convertir(llave.getEncoded()));
    return llave.getEncoded();
  }

  /**
   * Esto es necesario ya que la llave es pasada a traves de bytes, por lo que se debe formar
   * a partir de estos una llave de acuerdo al tipo de algoritmo utilizado.
   * En casos mas sencillos lo unico que se hace es pasar la llave sin necesidad de hacer llave.Encoded(); en el
   * metodo llave().
   * SecretKeySpec es superclase de SecretKey por lo cual es lo mismo pasar como argumento la llave
   * cifrado.init(Cipher.ENCRYPT_MODE, llave); como SecretKey o SecretKeySpec
   */
  public static byte[] encrip(byte textoplano[], byte llave_ []) throws GeneralSecurityException {
    SecretKeySpec llave = new SecretKeySpec(llave_,algoritmo);
    Cipher cifrado = Cipher.getInstance(algoritmo+"/ECB/PKCS5Padding");
    cifrado.init(Cipher.ENCRYPT_MODE, llave);
    byte[] textoencrip = cifrado.doFinal(textoplano);
    return textoencrip;
  }
  // Re-incializamos el cifrado en modo de desencriptacion
  public static byte[] desencrip(byte textoencrip[], byte llave_ []) throws GeneralSecurityException {
    SecretKeySpec llave = new SecretKeySpec(llave_,algoritmo);
    Cipher cifrado = Cipher.getInstance(algoritmo+"/ECB/PKCS5Padding");
    cifrado.init(Cipher.DECRYPT_MODE, llave);
    byte[] textodesenc = cifrado.doFinal(textoencrip);
    return textodesenc;
  }

/*  private */public static String convertir(byte bytes[]) {
    StringBuffer sb = new StringBuffer(bytes.length * 2);
	for (int i = 0; i < bytes.length; i++) {
	    sb.append(convertirDigito((int) (bytes[i] >> 4)));
	    sb.append(convertirDigito((int) (bytes[i] & 0x0f)));
	}
	return (sb.toString());
  }

  private static char convertirDigito(int valor) {
	valor &= 0x0f;
	if (valor >= 10)
	    return ((char) (valor - 10 + 'a'));
	else
	    return ((char) (valor + '0'));
  }

  public static byte[] convertir(String digitos) {
//	java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
    byte[] ret = new byte[digitos.length()/2];
    int j=0;
	for (int i = 0; i < digitos.length(); i += 2) {
	    char c1 = digitos.charAt(i);
	    if ((i+1) >= digitos.length())
    		throw new IllegalArgumentException ("error al convertir la cadena a byte");
	    char c2 = digitos.charAt(i + 1);
	    byte b = 0;
	    if ((c1 >= '0') && (c1 <= '9'))
		b += ((c1 - '0') * 16);
	    else if ((c1 >= 'a') && (c1 <= 'f'))
		b += ((c1 - 'a' + 10) * 16);
	    else if ((c1 >= 'A') && (c1 <= 'F'))
		b += ((c1 - 'A' + 10) * 16);
	    else
	    	throw new IllegalArgumentException ("error al convertir la cadena a byte");
	    if ((c2 >= '0') && (c2 <= '9'))
		b += (c2 - '0');
	    else if ((c2 >= 'a') && (c2 <= 'f'))
		b += (c2 - 'a' + 10);
	    else if ((c2 >= 'A') && (c2 <= 'F'))
		b += (c2 - 'A' + 10);
	    else
    		throw new IllegalArgumentException ("error al convertir la cadena a byte");//sm.getString("hexUtil.bad"));
//	    baos.write(b);
        ret[j++]=b;
	}
	return ret;//(baos.toByteArray());
  }


}

/*
  public static String Des(String texto) throws Exception {
    KeyGenerator kg = KeyGenerator.getInstance("Twofish");
    kg.init(128);

    long comienzo  = System.currentTimeMillis();
    SecretKey llave = kg.generateKey();// Key llave = keyGenerator.generateKey();
    long fin =System.currentTimeMillis();
    double total=  (fin - comienzo)/1000.0;

    System.out.println("Termino de generar la llave " +  total +"pero que hay en ecode "+ convertir(llave.getEncoded()));
    Cipher cifrado = Cipher.getInstance("Twofish/ECB/PKCS5Padding");
    cifrado.init(Cipher.ENCRYPT_MODE, llave);
    byte[] textoplano = texto.getBytes("UTF8");
    System.out.println("texto plano: ");
    for (int i=0;i<textoplano.length;i++) { System.out.print(textoplano[i]+" "); }
    // Donde se realiza la encriptacion
    byte[] textoencrip = cifrado.doFinal(textoplano);
    System.out.println("\ntexto encriptado: ");
    for (int i=0;i<textoencrip.length;i++) { System.out.print(textoencrip[i]+" "); }
    // Re-incializamos el cifrado en modo de desencriptacion
    cifrado.init(Cipher.DECRYPT_MODE, llave);
    // Donde se realiza la desencriptacion
    byte[] textodesenc = cifrado.doFinal(textoencrip);
    String output = new String(textodesenc,"UTF8");
    System.out.println("\ntexto Desencriptado: "+output);

    String retu = convertir(textoencrip);
    return retu;
  }

  public static byte[] blowfishEncrypt(String toEncrypt, String key) throws Exception {
  // create a binary key from the argument key (seed)
    SecureRandom sr = new SecureRandom(key.getBytes());
    KeyGenerator kg = KeyGenerator.getInstance("Blowfish");
    kg.init(sr);
    SecretKey sk = kg.generateKey();
    // do the encryption with that key
    Cipher cipher = Cipher.getInstance("Blowfish");
    cipher.init(Cipher.ENCRYPT_MODE, sk);
    byte[] encrypted = cipher.doFinal(toEncrypt.getBytes());
    return encrypted;
  }

  public static String blowfishDecrypt(byte[] toDecrypt, String key) throws Exception {
    // create a binary key from the argument key (seed)
    SecureRandom sr = new SecureRandom(key.getBytes());
    KeyGenerator kg = KeyGenerator.getInstance("Blowfish");
    kg.init(sr);
    SecretKey sk = kg.generateKey();
    // do the decryption with that key
    Cipher cipher = Cipher.getInstance("Blowfish");
    cipher.init(Cipher.DECRYPT_MODE, sk);
    byte[] decrypted = cipher.doFinal(toDecrypt);
    return new String(decrypted);
  }
*/