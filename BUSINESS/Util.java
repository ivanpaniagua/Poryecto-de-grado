package BUSINESS;

import java.io.*;   //para la lectura y escritura de archivos
import java.text.*; //para SimpleDateFormat
import java.sql.Timestamp;  //para Timestamp
import java.util.*; //para Date

/**
 * Title:        Business Services
 * Description:  Clases encargadas de la aplicacion o logica de negocios
 * Copyright:    Copyright (c) 2003
 * Company:      Entidad Financiera
 * @author Ivan Paniagua
 * @version 1.0
 */


 /**
  * Los metodos de esta clase son declarados estaticos para que no sea
  * necesario declarar objetos de esta clase.
  */

public class Util {

  static java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  static java.text.SimpleDateFormat sdf_= new java.text.SimpleDateFormat("EEEEEEEE dd 'de' MMMMMMMM 'del' yyyy");

  /**
  * Devuelve true si la cadena "cad" contiene algun caracter de la cadena "validos"
  *
  * @param la cadena a verificar
  * @param validos Es el conjunto de caracteres a buscar
  * @return true si la cadena a verificar contiene por lo menos un caracter de la cadena de validos
  */
  private static boolean Contiene(String cad, String validos) {
    boolean contiene=false;
    char ch;
    int i=0,j=0;
    for (i = 0;  i < cad.length();  i++) {
      ch = cad.charAt(i);
      for (j = 0;  j < validos.length();  j++)
        if (ch == validos.charAt(j)) {
        contiene = true;
        return contiene;
        }
    }
    return contiene;
  }

  /**
  * Devuelve verdad si la cadena de email contiene el caracter ("@") y
  * al menos  un punto("."), ejm. "paniagur@estudiantes.ucbcba.edu.bo".
  * @param email una cadena represantando una direcccion de correo
  * @return true si es valido, false en caso contrario
  */
  public static boolean EmailValido(String email) {
    boolean valido = false;
    if (email != null && email.indexOf("@") != -1 && email.indexOf(".") != -1) {
        valido = true;
    }
    return valido;
  }

  /**
  * Algunas cadenas deben ser validadas o filtradas antes de ser usadas.
  * Primero la cadena no puede ser nula ni ser vacia y por ultimo
  * los caracteres señalados con la funcion Contiene son invalidos, de acuerdo al documento
  * WWW Security FAQ de Stein y Stewart http://www.w3.org/Security/Faq/ seccion Q37
  * @param cad la cadena a filtrar
  * @return devuelve true si es una cadena valida
  */
  public static boolean Valido(String cad){
    boolean valido = false;
    if(cad != null && !cad.equals("") && !Contiene(cad,"&;`'|*?~<>^()[]{}$%+\n\r\""))
      valido = true;
    return valido;
  }

  /**
  * El metodo controla que la cadena pasada como argumento no cotenga caracteres secuenciales.
  * Se busca si existe alguna coincidencia con la cadena sec que contiene todas
  * las secuencias en caracteres como en numeros, y si no existe la cadena no es secuencial
  */
  public static boolean Secuencial(String  cad){
    boolean valido = false;
    String sec = "abcdefghijklmnopqrstuvwxyz:ABCDEFGHIJKLMNOPQRSTUVWXYZ:zyxwvutsrqponmlkjihgfedcba:ZYXWVUTSRQPONMLKJIHGFEDCBA:0123456789:9876543210";
    if(sec.indexOf(cad) == -1){
      valido = true;
    }
    return valido;
  }

  public static boolean Repetidos(String cad){
    boolean valido = false;
    for (int i=1; i < cad.length(); i++) {
      if (cad.charAt(0) != cad.charAt(i))
        return valido;
    }
    return true;
  }

/*
  public static boolean Iguales(char [] a,char [] b){
    boolean iguales = false;
    int i=0;
    if (a.length != b.length)
      return iguales;
    for (i=0; i< a.length; i++)
      if (a[i] != b[i])
        break;
    if (i == a.length)
      iguales=true;
    return iguales;
  }
*/
  /** Devuelve true si el caracter c es un digito (0 .. 9). */
  public static boolean Digito(char c) {
    return Character.isDigit(c);
  }

  /**
  *  Devuelve true si todos los caracteres en la cadena s son numeros
  *  No acepta enteros con signo tampoco en punto flotante o con
  *  notacion exponencial, etc.
  */
  public static boolean esEntero(String s) {
    // Busca a traves de la cadena de caracteres uno por uno
    // hasta que encontramos caracteres no numericos
    // Cuando lo hacemos retornamos false; si no devolemos true.
    for(int i = 0; i < s.length(); i++)
    {
      // Se comprueba que el caracter actual es numero.
      char c = s.charAt(i);
      if(!Digito(c)) return false;
    }
    // todos los caracteres son numeros.
    return true;
  }

  /**
  * Metodo que lee un archivo cuyo nombre es pasado por parametro,
  * sin embargo este archivo debera tener los permisos correspondientes
  * @param nombre El nombre del archivo a leer
  * @return byte[] contiene los bytes del archivo leido.
  */
  static public byte[] leerArchivo( String nombre ) throws IOException {
    File archivo = new File( nombre );
    long tam = archivo.length();
    byte datos[] = new byte[(int)tam];
    FileInputStream fin = new FileInputStream( archivo );
    int r = fin.read( datos );
    if (r != tam)
      throw new IOException( "Solo lectura "+r+" de "+tam+" para "+archivo );
    fin.close();
    return datos;
  }

  /**
  * Metodo que escribe un archivo cuyo nombre junto con sus datos
  * (array de bytes) son pasados por parametros,
  * sin embargo este archivo debera tener los permisos correspondientes.
  * @param nombre El nombre del archivo a escribir
  * @param datos[] Contiene los bytes a ser escritos en el archivo.
  */
  static public void escribirArchivo( String nombre, byte datos[] ) throws IOException {
    FileOutputStream fout = new FileOutputStream( nombre );
    fout.write( datos );
    fout.close();
  }

  static public void escribirArchivo( File file, byte datos[] ) throws IOException {
    FileOutputStream fout = new FileOutputStream( file );
    fout.write( datos );
    fout.close();
  }

  /**
   * Metodo utilizado para fechas, cuyo proposito es convertir
   * un objeto Timestamp (objeto que contiene la fecha y hora)
   * en su correspondiente cadena.
   * Aqui se hace uso de una clase muy util SimpleDateFormat
   * donde se especifica que formato debera tener la fecha a ser convertida
   * @param t El objeto Timestamp a ser convertido
   * @return String La cadena representando al objeto Timestamp
   */

  static public String Convertir(Timestamp t) {
    return sdf.format(t);
  }

  static public String Fecha() {
    long a = System.currentTimeMillis();
    Timestamp tiempo= new Timestamp(a);
    return sdf_.format(tiempo).toUpperCase();

  }

  /**
   * Metodo utilizado para fechas, cuyo proposito es convertir
   * una cadena en un objeto Timestamp (objeto que contiene la fecha y hora)
   * Aqui se hace uso de una clase muy util SimpleDateFormat
   * donde se especifica que formato debera tener la fecha a ser convertida
   * @param t la cadena a ser convertida
   * @return Timestamp El objeto Timestamp
   */
  static public Timestamp Convertir(String t) {
    Date d= new Date();     //java.text.ParsePosition pos = new java.text.ParsePosition(0);
    try { d=sdf.parse(t); } //sdf.parse(fecha.getSelectedItem().toString(), pos);
    catch (Exception ex) {System.out.println("exepcion al converir una cadena a timestamp"); }//nunca deberia ocurrir una exepcion
    return (new Timestamp(d.getTime()));//como no manejamos Date creamos un objeto Timestamp
  }//d.getMinutes()
//    java.util.Calendar cal = Calendar.getInstance();
//    java.util.Date cal = new java.util.Date(System.currentTimeMillis());
//    fecha.setSelectedItem(sdf.format(cal));

  static public final String DobleString(double d) {
    if (d == 0.0) return "&nbsp;";
    DecimalFormat decimalformat = new DecimalFormat();// new DecimalFormat( "###,###,###.00" );de ch12 encryp
    decimalformat.setMinimumFractionDigits(2);
    decimalformat.setMaximumFractionDigits(2);
    return decimalformat.format(d);//"$" + decimalformat.format(d);
  }
}