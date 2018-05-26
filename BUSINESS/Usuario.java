package BUSINESS;
import java.util.*;


/**
 * Title:        Business Services
 * Description:  Clases encargadas de la aplicacion o logica de negocios
 * Copyright:    Copyright (c) 2003
 * Company:      Entidad Financiera
 * @author Ivan Paniagua
 * @version 1.0
 */

public class Usuario {

  private int id_i;
  private String ci;
  private String login;
  private String password;
  private String nombre;
  private String bloqueo;
  private String email1;
  private String email2;
  private int intentos_c;
  private int intentos_d;
  private StringBuffer errores = new StringBuffer();
  private String creacion;

  /**
   * Este constructor se emplea en la creacion de un objeto usuario pero con datos obtenidos de la
   * base de datos con el metodo getUsuario(). De otra forma si se quiere crear un objeto usuario
   * sin este constructor (el segundo constructor que inicializa las variables con valores vacios)
   * se tendrian que emplear los metodos setXXX. Pero al emplear los metodos setXXX se realiza por ejemplo
   * un la validez de los datos, o en el caso de el password se realiza la encriptacion de la contraseña pasada
   * como argumento pero estos pasos no son necerarios cuando se obtienen de la Bd porque se supone que
   * tienen que ser validos.
   */
  public Usuario(int id_i,String ci,String login,String password,String nombre,String email1,String email2,
    String bloqueo,int intentos_c, int intentos_d,String creacion) {

    this.id_i = id_i;
    this.ci = ci;
    this.login = login;
    this.nombre = nombre;
    this.password = password;  //  this.password= Encriptacion.Digest(password);
    this.email1 = email1;
    this.email2 = email2;
    this.bloqueo = bloqueo;
    this.intentos_c = intentos_c;
    this.intentos_d = intentos_d;
    this.creacion = creacion;
    errores.append("");
  }

  // El constructor inicializa las variables en nulo
  public Usuario() {
    id_i = 0;
    ci = "";
    login = "";
    password = "";
    bloqueo = "n";//defecto es "n"
    email1 = "";
    email2 = "";
    creacion = Util.Convertir(new java.sql.Timestamp(System.currentTimeMillis()));
    intentos_c = 0;
    intentos_d = 0;
//    errores = new StringBuffer();//PERO QUE PASA CUANDO SE CREA UN USUARIO DESDE LA BD U LUEGO SE QUIEREN UTILIZAR errores
    errores.append("");
  }

  public boolean validar() {
    boolean valido = true;

    if (login.equals("")) {
      errores.append("INGRESE SU LOGIN");
      valido = false;
    }

    if (password.equals("") || password.length() != 5){
      errores.append("LA CONTRASEÑA DEBE SER MAS GRANDE QUE 6 CARACTERES");
      valido=false;
    } else {
      try {
        int convertir= Integer.parseInt(password);
        }
      catch (NumberFormatException e) {
        errores.append("INGRESE UN LOGIN VALIDO");
        valido=false;
      }
    }
    return valido;
  }

  public String getErrores() {
    return errores.toString(); //(error == null) ? "" : error;
  }

  public boolean Errores() {
    return (!getErrores().equals(""));
  }

  /**
   * Si ocurre algun error entonces se hace un return del metodo ya que se
   * acumularian los errores en cadena.
   */
  public void setId_i(String newid_i) {
    int temp=0;
    if (newid_i == null || newid_i.equals("")) {
      errores.append("INGRESE EL VALOR DE id_i (IDENTIFICADOR INTERNO) \n");
      return;
    }
    try {
      temp = Integer.parseInt(newid_i);
    }
    catch (NumberFormatException e) {
      errores.append("EL VALOR DEL id_i INGRESADO NO ES UN NUMERO \n");
      return;
    }
    id_i = temp;
  }

  public int getId_i() {
    return id_i;
  }

  public void setCi(String newCi) {
    if (newCi == null || newCi.equals("")) {
      errores.append("NO SE ESPECIFICO EL CI o RUC \n");
      return;
    }
    ci = newCi;
  }

  public String getCi() {
    return ci;
  }

  public void setLogin(String newlogin) {
    if (newlogin == null || newlogin.equals("")) {
      errores.append("NO SE ESPECIFICO EL LOGIN \n");
      return;
    }

    if (!Util.Valido(newlogin)) {
      errores.append("EL LOGIN INGRESADO CONTIENE CARACTERES NO VALIDOS \n" +
      "NO PUEDE CONTENER LOS SIGUIENTES CARACTERES: \n   &;`'|*?~<>^()[]{}$%+\" \n");
      return;
    }
    login = newlogin;
  }

  public String getLogin() {
    return login;
  }

  public void setPassword(String newPassword) {
    if (newPassword == null || newPassword.equals("")) {
      errores.append("NO SE ESPECIFICO EL PASSWORD \n");
      return;
    }
    if (!Util.Valido(newPassword)) {
      errores.append("LA CONSTRASEÑA INGRESADA CONTIENE CARACTERES NO VALIDOS \n" +
      "NO PUEDE CONTENER LOS SIGUIENTES CARACTERES: \n   &;`'|*?~<>^()[]{}$%+\" \n");
      return;
    }

    if (newPassword.length() < 6 ) {
      errores.append("LA CONTRASEÑA DEBE CONTENER MAS DE 6 CARACTERES \n");
      return;
    }

    if (newPassword.equals(getLogin())){
      errores.append("LA CONTRASEÑA NO DEBE SER IGUAL AL LOGIN \n");
      return;
    }

    if (!Util.Secuencial(newPassword)) {
      errores.append("LA CONSTRASEÑA INGRESADA NO PUEDE SER SECUENCIAL \n");
      return;
    }

    if (Util.Repetidos(newPassword)) {
      errores.append("LA CONSTRASEÑA INGRESADA NO PUEDE CONTENER CARACTERES REPETIDOS \n");
      return;
    }

    password = newPassword; //  password = Encriptacion.Digest(newPassword);
  }

  public void setPassword2(String newPassword) {
    if (!password.equals(newPassword)) {
      errores.append("LA CONTRASEÑA Y SU CONFIRMACION NO COINCIDEN \n");
      return;
    }
    //no se lo almacena en ningun lado solo es temporal
  }

  public void password_digest() {
    password = Encriptacion.Digest(password);
  }

  public String getPassword() {
    return password;
  }

  public boolean contraseña(String pass) {
    String s = Encriptacion.Digest(pass);
    return password.equals(s);
  }

  public void setNombre(String newNombre) {
    if (newNombre == null || newNombre.equals("")) {
      errores.append("NO SE ESPECIFICO EL NOMBRE \n");
      return;
    }
    if (!Util.Valido(newNombre)) {
      errores.append("EL NOMBRE INGRESADO CONTIENE CARACTERES NO VALIDOS \n" +
      "NO PUEDE CONTENER LOS SIGUIENTES CARACTERES: \n   &;`'|*?~<>^()[]{}$%+\" \n");
      return;
    }

    nombre = newNombre;
  }
  public String getNombre() {
    return nombre;
  }

  public void setEmail1(String newEmail1) {
    if (newEmail1 == null || newEmail1.equals("")) {
      errores.append("NO SE ESPECIFICO EL EMAIL 1 OBLIGATORIO\n");
      return;
    }

    if (!Util.Valido(newEmail1)) {
      errores.append("EL EMAIL 1 INGRESADO CONTIENE CARACTERES NO VALIDOS \n" +
      "NO PUEDE CONTENER LOS SIGUIENTES CARACTERES: \n   &;`'|*?~<>^()[]{}$%+\" \n");
      return;
    }

    if(!Util.EmailValido(newEmail1)) {
      errores.append("EL EMAIL 1 INGRESADO NO ES VALIDO \n");
      return;
    }
    email1 = newEmail1;
  }

  public String getEmail1() {
    return email1;
  }

  public void setEmail2(String newEmail2) {
    if(!Util.Valido(newEmail2) && !newEmail2.equals("") ){
      errores.append("EL EMAIL 1 INGRESADO CONTIENE CARACTERES NO VALIDOS \n" +
      "NO PUEDE CONTENER LOS SIGUIENTES CARACTERES: \n   &;`'|*?~<>^()[]{}$%+\" \n");
      return;
    }

    if(/*newEmail2 != null && */!newEmail2.equals("") && !Util.EmailValido(newEmail2)) {
      errores.append("EL SEGUNDO EMAIL INGRESADO NO ES VALIDO \n");
      return;
    }

    email2 = newEmail2;
  }

  public String getEmail2() {
    return email2;
  }

  public void setBloqueo(String newBloqueo) {
    bloqueo = newBloqueo;
  }

  public String getBloqueo() {
    return bloqueo;
  }

  public void setCreacion(String newCreacion) {
    creacion = newCreacion;
  }

  public String getCreacion() {
    return creacion;
  }

  public void setIntentos_c(int newIntentos_c) {
    intentos_c = newIntentos_c;
  }

  public void setIntentos_c(String newIntentos_c) {
    int temp=0;
    if (newIntentos_c == null || newIntentos_c.equals("")) {
      errores.append("INGRESE LA CANTIDAD DE INTENTOS CONTINUOS \n");
      return;
    }

    try {
      temp = Integer.parseInt(newIntentos_c);
    }
    catch (NumberFormatException e) {
      errores.append("EL VALOR INGRESADO DE INTENTOS CONTINUOS NO ES UN NUMERO \n");
      return;
    }
    setIntentos_c(temp);
  }

  public int getIntentos_c() {
    return intentos_c;
  }

  public void setIntentos_d(int newIntentos_d) {
    intentos_d = newIntentos_d;
  }

  public void setIntentos_d(String newIntentos_d) {
    int temp=0;
    if (newIntentos_d == null || newIntentos_d.equals("")) {
      errores.append("INGRESE LA CANTIDAD DE INTENTOS DISCONTINUOS \n");
      return;
    }

    try {
      temp = Integer.parseInt(newIntentos_d);
    }
    catch (NumberFormatException e) {
      errores.append("EL VALOR INGRESADO DE INTENTOS DISCONTINUOS NO ES UN NUMERO \n");
      return;
    }
    setIntentos_d(temp);
  }

  public int getIntentos_d() {
    return intentos_d;
  }

  public void cambiar(String password, String nuevo, String nuevo_rep, String ip ) {
    if (!contraseña(password) ) {
      errores.append("La contraseña anterior no es la correcta");
      return;
    }
    //se encarga de verificar que no sea nulo y que no contenga caracteres peligrosos
    //esto se lo realiza fuera de este metodo pero tambien sirve para comprobar
    //que la nueva constraseña introducida sea igual que su confirmacion
    setPassword(nuevo);
    setPassword2(nuevo_rep);
    if (!Errores()){ //si no existen errores entonces hacer
      password_digest();
      try {
        Bd bd = Bd.getBd();
        bd.setUpdate(this); /*OJO*/
        bd.setLog(getId_i(),bd.MODI,"modificado:pass",ip);
      }
      catch (Exception e) {
        errores.append("ERROR AL  ACTUALIZAR EL PASSWORD DEL USUARIO: "+ id_i +" Y CUYO MENSAJE DE ERROR ES: \n" + e);
      }
    }
  }

  public java.util.Vector getCuentas_dpf() {
    Bd bd = Bd.getBd();
    return bd.Cuentas_dpf(ci);
  }

  public java.util.Vector getCuentas_cred() {
    Bd bd = Bd.getBd();
    return bd.Cuentas_cred(ci);
  }

  public java.util.Vector getCuentas_ahorro() {
    Bd bd = Bd.getBd();
    return bd.Cuentas_ahorro(ci);
  }

  public java.util.Vector getMovimientos(int sel,String tipo) {
    Bd bd = Bd.getBd();
    return bd.movimientos(ci,sel,tipo);
  }

  public java.util.Vector getLogs(){
    Bd bd = Bd.getBd();
    return bd.Log_show(id_i);
  }


  public boolean autentifica(String password, String datos, String ip) {
    boolean au = false;
    boolean contraseña = contraseña(password);
    Bd bd = Bd.getBd();
    if (bloqueo.equals("s")) {
      errores.append("Usuario Bloqueado");
      return au;
    }
    //el usuario no esta bloqueado pero la contraseña introducida no es valida
    if (bloqueo.equals("n") && !contraseña ) {
      bd.setLog(id_i, bd.INVA , datos, ip);
      //como esta regsitrado en la Bd, vemos si se ha alcanzado primero
      //los intentos discontinuos, es decir todos los intentos que se han efectuado hasta la fecha
      if (bd.getIngresos(id_i, false) >= /*<*/ intentos_d) {
        System.out.println("Se han alcanzado y se deberia bloquear esta cuenta con UPDATE");
        bd.Bloqueo(id_i,true);
//        System.out.println("No se han alcanzado todavia los intentos discontinuos que se pueden tratar");
      }
      else if (bd.getIngresos(id_i, true) >= /*<*/ intentos_c) {
        System.out.println("Se han alcanzado y se deberia bloquear esta cuenta con UPDATE");
        bd.Bloqueo(id_i,true);
//        System.out.println("No se han alcanzado todavia los intentos continuos que se pueden tratar");
      }
//      else {//if((bd.getIngresos(id_i, false) >= intentos_d) || (bd.getIngresos(id_i,true) >= intentos_c) )
//        System.out.println("Se han alcanzado y se deberia bloquear esta cuenta con UPDATE");
//        bd.Bloqueo(id_i,true);
//      }
      errores.append("Contraseña Erronea "+bd.getIngresos(id_i,false) +":"+bd.getIngresos(id_i,true));
      return au;
    }
    else if (bloqueo.equals("n") && contraseña ) {
      bd.setLog(id_i,bd.AUTE,datos,ip);
      au=true;
    }

   return au;
  }

  /**
   * Solo para observar cuando el gc se encarga de liberar este objeto
   */
  public void finalize() {
//  System.out.println("EL OBJETO USUARIO: "+ id_i +" HA SIDO DESTRUIDO");
  }

}