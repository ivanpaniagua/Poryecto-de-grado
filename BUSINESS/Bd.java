//descomentar en buscar2
//descomentar en jbInit
//desomentar en TodoLogs

package BUSINESS;

import java.sql.*;
import java.util.*;
import com.borland.dx.dataset.*;
import com.borland.dx.sql.dataset.*;

/**
 * Title:        Business Services
 * Description:  Clases encargadas de la aplicacion o logica de negocios
 * Copyright:    Copyright (c) 2003
 * Company:      Entidad Financiera
 * @author Ivan Paniagua
 * @version 1.0
 */

/**
 *  Clase encargada de todo el manejo de datos.
 *  Si esta clase se implementara como DataModule debe existir un metodo
 *  getDataModule cuya implementacion es exactamente igual a la que se tiene
 *  en el metodo getBd.
 *  <P>
 */

public class Bd /*implements DataModule*/ {

  private ConnectionPool cp;
  private static Bd bd;  // la misma clase es estatica

/*  private static final String Driver = "org.gjt.mm.mysql.Driver";
  private static final String URL = "jdbc:mysql://www.ivan.com/mio";//estaba en localhost
  private static final String username = "root";
  private static final String password = "";*/

/* SI SE DESCOMENTA ESTO CAMBIAR TB EN LOS MENSAJES DE ERRORES*/
  private static final String Driver = "org.postgresql.Driver";
  private static final String URL = "jdbc:postgresql://www.ivan.com/mio";
  private static final String username = "ivan"; //estaba en IVAN
  private static final String password = "idddqd";


  private Database database1    = new Database();
  private QueryDataSet usuarios = new QueryDataSet();
  private QueryDataSet usu_b    = new QueryDataSet();
  private QueryDataSet logs     = new QueryDataSet();
  private QueryDataSet dpf      = new QueryDataSet();
  private QueryDataSet creditos = new QueryDataSet();
  private QueryDataSet ahorros  = new QueryDataSet();
  private QueryDataSet mov      = new QueryDataSet();

  private static final long tiempo_dis  = 24*60*60; //busqueda de 24 hrs atras en la bd para los intentos discontinuos
  private static final long tiempo_cont = 5*60;     //busqueda de 5 min atras en la bd para los intentos continuos
  public  static final long tiempo_log  = 5*60;     //busqueda de 5 min atras en la bd para los registros en el log de autentificacion exitosa

  public static final int AUTE= 1;
  public static final int CREA= 2;
  public static final int INVA= 3;
  public static final int MODI= 4;
  public static final int SALI= 5;
  public static final int ELIM= 6;
  public static final int VACI= 7;

  public static final String [][] e_usuario={{"id_i","ID DE USUARIO"},{"ci","CI O RUC"},{"login","LOGIN"},
  {"password","PASSWORD"},{"nombre","NOMBRE"},{"email_1","E-MAIL 1"},{"email_2","E-MAIL 2"},{"bloqueo","BLOQUEO"},
  {"intentos_c","INTENTOS CONTINUOS"},{"intentos_d","INTENTOS DISCONTINUOS"},{"creacion","CREACION"}};
  private static final String q_usuario = "SELECT "+formar(e_usuario)+" FROM t_usuario ";

  public static final String [][] e_log={{"id_log","ID LOG"},{"id_i","ID DE USUARIO"},{"t_tipo.descripcion","TIPO DE OPERACION"},
  {"datos","DATOS DE LA OPERACION"},{"ip","IP"},{"fecha","FECHA Y HORA"}};
  private static final String q_log = "SELECT "+formar(e_log)+" FROM t_registro, t_tipo WHERE t_registro.tipo = t_tipo.tipo ";

  public static final String [][] e_dpf={{"t_dpf.dpf","# de DPF"},{"apertura","APERTURA"},{"plazo","PLAZO EN DIAS"},
  {"vcto","VENCIMIENTO"},{"capital","CAPITAL"},{"tasa","TASA DE INTERES (%)"},{"periodo","PERIODO (PAGO DE INTERESES)"},{"moneda","MONEDA"},{"caja_a","CAJA DE AHORRO"}};
  private static final String q_dpf = "SELECT "+formar(e_dpf)+" FROM t_dpf ";

  public static final String [][] e_cred={{"t_cred.cred","# de CREDITO"},{"monto","MONTO"},{"tasa","TASA DE INTERES (%)"},
  {"plazo","PLAZO EN DIAS"},{"tipocuota","TIPO DE CUOTA"},{"tipoamort","TIPO DE AMORTIZACION"},{"diasamort","DIAS DE AMORTIZACION"},{"f_desemb","FECHA DE DESEMBOLSO"},{"saldo","SALDO"},{"caja_a","CAJA DE AHORRO"}};
  private static final String q_cred = "SELECT "+formar(e_cred)+" FROM t_cred ";

  public static final String [][] e_cda={{"t_cda.cda","# CAJA DE AHORRO"},{"moneda","MONEDA"},{"interes","INTERES (%)"},
  {"periodo","PERIODO CAPITALIZABLE"},{"saldo","SALDO"},{"inpp","INTERES PENDIENTE DE PAGO"},{"fecha","APERTURA"}};
  private static final String q_cda = "SELECT "+formar(e_cda)+" FROM t_cda ";

  public static final String [][] e_dpfq={{"dpf","# DE DPF"},{"fecha","FECHA"},{"operacion","OPERACION"},
  {"d_h","DEBE O HABER"},{"monto","MONTO"}};
  private static final String q_dpfq = "SELECT "+formar(e_dpfq)+" FROM t_dpfq ";

  Vector log_tree = new Vector();

  /**
   * El contructor de la clase llama al metodo jbInit donde se inicializa toda
   * la conexion a la Bd, y si algo falla en este proceso directamente finalizamos
   * la maquina virtual. Esto pasa generalmente si no se tiene la conexion
   * a la Bd o si falla la comunicacion durante el handshake
   */
  public Bd() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
      System.exit(0);
    }
  }

  private void jbInit() throws Exception {
    try {
    cp = new ConnectionPool(Driver, URL, username, password, 2/*10*/, 7/*50*/, true);

    database1.setConnection(new com.borland.dx.sql.dataset.ConnectionDescriptor(URL,username,password,false,Driver));//false no pide constrase�a
    usuarios.setQuery(new com.borland.dx.sql.dataset.QueryDescriptor(database1, q_usuario, null, true, Load.ALL));
    usu_b.setQuery(new com.borland.dx.sql.dataset.QueryDescriptor(database1, q_usuario, null, true, Load.AS_NEEDED));//busquedas
    logs.setQuery(new com.borland.dx.sql.dataset.QueryDescriptor(database1, q_log, null, true, Load.ALL));
    dpf.setQuery(new com.borland.dx.sql.dataset.QueryDescriptor(database1, q_dpf, null, true, Load.ALL));
    creditos.setQuery(new com.borland.dx.sql.dataset.QueryDescriptor(database1, q_cred, null, true, Load.ALL));
    ahorros.setQuery(new com.borland.dx.sql.dataset.QueryDescriptor(database1, q_cda, null, true, Load.ALL));
    mov.setQuery(new com.borland.dx.sql.dataset.QueryDescriptor(database1, q_dpfq, null, true, Load.ALL));
//        System.out.println("cual soporta " + connection.getTransactionIsolation() );
//        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
//        System.out.println("cual soporta ahora" + connection.getTransactionIsolation() );

//    System.out.println("max de conecciones " + database1.getMetaData().getMaxConnections() + "soprta transacciones:" + database1.getMetaData().supportsTransactions());
//    System.out.println("isolation level " + database1.getMetaData().supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED  ) );
//    System.out.println("metadata " + connection.getMetaData().getNumericFunctions() );
//  getStringFunctions()  getTimeDateFunctions()  getSystemFunctions()
    }
    catch(SQLException sqle) {
      throw new Exception("ERROR EN EL POOL: " + sqle);
    }
    catch (Exception e){
      throw new Exception("ERROR EN LA CONEXION A LA BD: " + e);
    }
  }

  /**
   * Puesto que crear conexiones a la bd es costoso se ha implementado
   * esta forma de conexion con un metodo estatico donde se verifica
   * si ya existe un objeto bd de esta clase si no existe se crea
   * una nueva conexion en caso contrario ya existia un objeto y lo unico
   * que se hace es devolver ese objeto.
   */
  public static Bd getBd() {
    if (bd == null) {
      bd = new Bd();
      System.out.println("Creado un nuevo objeto Bd");
    }
    return bd;
  }

  /**
   * Metodo que se conecta a la Base de datos a traves del objeto connection
   * y cuya cadena de consulta es pasada a traves de statement de la clase preparedStatement
   * aunque pudo tambien haber sido de la clase Statement pero es menos eficiente (mejora la performance).
   *
   * Algo que es necesario recalcar es la facilidad que otorga la portabilidad de java a la
   * hora de manejar fechas, debido a que debe conteplar el manejo portable de datos entre motores de
   * Base de datos, ya que parace ser que cada RDBMS parece tener su propia manera de representar
   * la informacion de fechas.
   * Aqui se puede observar por ejemplo que se utiliza un getTimestamp para obtener el campo
   * de "creacion" de la Bd. y no da importancia a que formato tenga el mismo dentro del RDBMS.
   * Luego se lo transforma a un String lo cual lo hace mas manipulable.
   */
  public Usuario getUsuario(String login) {
    Connection connection = null;
    PreparedStatement statement = null;
    Usuario usuario = null;
    try {
        String query = "SELECT * FROM t_usuario WHERE login = ?";
        connection = cp.getConnection();
        statement = connection.prepareStatement(query);
        statement.setString(1,login);
        ResultSet r= statement.executeQuery();
        while (r.next()){
          usuario = new Usuario(r.getInt("id_i"),r.getString("ci"),r.getString("login"),r.getString("password"),
          r.getString("nombre"),r.getString("email_1"),r.getString("email_2"),r.getString("bloqueo"),
          r.getInt("intentos_c"),r.getInt("intentos_d"),Util.Convertir(r.getTimestamp("creacion"))/*r.getString("creacion")*/);
        }
        r.close();
        statement.close();
        cp.free(connection);
    }
    catch(SQLException sqle) {
        System.out.println("ERROR EN LA OBTENCION DEL USUARIO" + sqle );
        return null;
    }
    return usuario;
  }

  /**
   * Similar al anterior metodo pero con la diferencia que se devuelve el usuario
   * basado en el id interno (int) y no en el login(String).
   */
  public Usuario getUsuario_id(int id_i) {
    Connection connection = null;
    PreparedStatement statement = null;
    Usuario usuario = null;
    try {
        String query = "SELECT * FROM t_usuario WHERE id_i = ?";
        connection = cp.getConnection();
        statement = connection.prepareStatement(query);
    	statement.setInt(1,id_i);
    	ResultSet r= statement.executeQuery();
        while (r.next()){
          usuario = new Usuario(r.getInt("id_i"),r.getString("ci"),r.getString("login"),r.getString("password"),
          r.getString("nombre"),r.getString("email_1"),r.getString("email_2"),r.getString("bloqueo"),
          r.getInt("intentos_c"),r.getInt("intentos_d"),Util.Convertir(r.getTimestamp("creacion"))/*r.getString("creacion")*/);
        }
    	r.close();
    	statement.close();
        cp.free(connection);
    }
    catch(SQLException sqle) {
        System.out.println("ERROR EN LA OBTENCION DEL USUARIO" + sqle );
        return null;
    }
    return usuario;
  }

  /**
   * Crea un nuevo usuario pasado como argumento un objeto usuario
   * pero se debe asegurar que el login no haya sido ingresado anteriormente a la bd,
   * puesto que se maneja como una clave o indice dentro de la base de datos.
   * Si esto ocurre se genera una exepcion con un codigo de error especifico de cada manejador de bd (DBMS).
   */
  public void setUsuario(Usuario usuario) throws Exception {
    Connection connection = null;
    PreparedStatement statement = null;
    //antes de nada se encripta el password, como se trata de un usuario nuevo
    //el password que se tiene esta en texto plano.
    usuario.password_digest();
    //necesitamos este try porque en caso de error necesitamos cerrar la conexion aqui,
    //situacion que no se podria efectuar desde fuera de la funcion si es que algo saliera mal
    try {
      String query = "INSERT INTO t_usuario (ci,login,password,nombre,email_1,email_2,bloqueo,intentos_c,intentos_d)" +/*creacion*/
                    "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?)";/*NOW() | CURRENT_TIMESTAMP | SYSDATE()*/
      connection = cp.getConnection();
      statement = connection.prepareStatement(query);
      statement.setString(1,usuario.getCi());
      statement.setString(2,usuario.getLogin());
      statement.setString(3,usuario.getPassword());
      statement.setString(4,usuario.getNombre());
      statement.setString(5,usuario.getEmail1());
      statement.setString(6,usuario.getEmail2());
      statement.setString(7,usuario.getBloqueo());
      statement.setInt(8,usuario.getIntentos_c());
      statement.setInt(9,usuario.getIntentos_d());
      statement.execute();
    }
    catch(SQLException sqle) {// encapsualacion.- mantener los errores especificos de este metodo como ser SQLException dentro del mismo
      if (sqle.getMessage().indexOf("login_index") > -1) {//PostgreSQL ("login_index") Mysql ("key 3")
        throw new Exception("ERROR AL INSERTAR EL NUEVO USUARIO: YA EXISTE EL LOGIN: " + usuario.getLogin()+ "\n POR FAVOR CAMBIE DE LOGIN");
      }
      else if(sqle.getMessage().indexOf("nombre_index") > -1) {//PostgreSQL ("nombre_index") Mysql ("key 2")
      throw new Exception("ERROR AL INSERTAR EL NUEVO USUARIO: YA EXISTE EL NOMBRE: " + usuario.getNombre() + "\n POR FAVOR CAMBIE DE NOMBRE");
      }
      else throw new Exception("ERROR AL INSERTAR EL NUEVO USUARIO " +sqle.getMessage());
    }
    finally {
      statement.close();
      cp.free(connection);
      getUsuarios().refresh();
    }
  }

  /**
   * Se debe tener encuenta que especificamente en MYSQL con la tabla t_usuario y el campo
   * de fecha de creacion en TIMESTAMP (cuyo valor no se puede colocar con un valor por defecto como DEFAULT NOW())
   * colocan su valor con la fecha actual en querys con UPDATE e INSERT (para insert esta bien, ya que coloca
   * la fecha de creacion) pero cuando quiero actualizarlo con UPDATE no deberia colocar la fecha de actual
   *
   * update t_usuario set creacion="1999-03-30 12:01:22" where id_i="3"
   */
  public void setUpdate(Usuario usuario) throws Exception {
    Connection connection = null;
    PreparedStatement statement = null;
    //necesitamos este try porque en caso de error necesitamos cerrar la conexion aqui,
    //cosa que no se podria desde fuera de la funcion si es que algo saliera mal
    try {
      String query = "UPDATE t_usuario SET ci=?, login=?, password=?, nombre=?, email_1=?, " +
      "email_2=?, bloqueo=?, intentos_c=?, intentos_d=?, creacion=?  WHERE id_i=?";
      connection = cp.getConnection();
      statement = connection.prepareStatement(query);
      statement.setString(1,usuario.getCi());
      statement.setString(2,usuario.getLogin());
      statement.setString(3,usuario.getPassword());
      statement.setString(4,usuario.getNombre());
      statement.setString(5,usuario.getEmail1());
      statement.setString(6,usuario.getEmail2());
      statement.setString(7,usuario.getBloqueo());
      statement.setInt(8,usuario.getIntentos_c());
      statement.setInt(9,usuario.getIntentos_d());
      statement.setTimestamp(10,Util.Convertir(usuario.getCreacion()));
      statement.setInt(11,usuario.getId_i());
      statement.execute();
    }
    catch(SQLException sqle) {// encapsualacion.- mantener los errores especificos de este metodo como ser SQLException dentro del mismo
      if (sqle.getMessage().indexOf("key 3") > -1) {// ("login_index")  ("key 3")
        throw new Exception("ERROR AL MODIFICAR EL USUARIO: YA EXISTE EL LOGIN: " + usuario.getLogin()+ "\n POR FAVOR CAMBIE DE LOGIN");
      }
      else if(sqle.getMessage().indexOf("key 2") > -1) {// ("nombre_index") ("key 2")
        throw new Exception("ERROR AL MODIFICAR EL USUARIO: YA EXISTE EL NOMBRE: " + usuario.getNombre() + "\n POR FAVOR CAMBIE DE NOMBRE");
      }
      else throw new Exception("ERROR AL MODIFICAR USUARIO " +sqle.getMessage());
    }
    finally {
      statement.close();
      cp.free(connection);
      getUsuarios().refresh();
    }
  }

  /**
   * Necesito un metodo que solo bloquee la cuenta y no modifique nigun otro valor,
   * ya que al hacerlo el password podria no contener ningun valor en usuario.getPassword
   * o si existe algun valor este es el que se almacenaria
   */
  public void Bloqueo(int id_i, boolean bloquear) {
    Connection connection = null;
    PreparedStatement statement = null;
    //necesitamos este try porque en caso de error necesitamos cerrar la conexion aqui,
    //cosa que no se podria desde fuera de la funcion si es que algo saliera mal
    try {
      String query = "UPDATE t_usuario SET bloqueo=? WHERE id_i=?";
      connection = cp.getConnection();
      statement = connection.prepareStatement(query);
      if(bloquear)
        statement.setString(1,"s");
      else
        statement.setString(1,"n");
      statement.setInt(2,id_i);
      statement.execute();
    }
    catch(SQLException sqle) {// encapsualacion.- mantener los errores especificos de este metodo como ser SQLException dentro del mismo
      System.out.println("SE GENERO UN ERROR AL QUERER BLOQUEAR LA CUENTA"+sqle);
    }
    finally {
      try {statement.close();}
      catch (Exception ex) {System.out.println("NO SE PUDO CERRAR LA CONEXION (Error aparte del error SQL)");    }
      cp.free(connection);
    }
  }

  /**
   * Observar que se utiliza aqui la clase StringBuffer para unir las cadenas
   * debido a que es mas eficiente que al hacerlo con String y utlizar el
   * operador + para la concatenacion de cadenas.
   */
  public void Buscar_u(String criterio, String valor){
//    StringBuffer mensaje = new StringBuffer("SELECT * FROM t_usuario WHERE ");
    for (int i=0; i < e_usuario.length; i++)
      if (e_usuario[i][1].equals(criterio))
       criterio=e_usuario[i][0];

    StringBuffer mensaje = new StringBuffer(q_usuario);
    mensaje.append("WHERE ");//RECIEN A�ADIDO
    mensaje.append("UPPER(");
    mensaje.append(criterio);
    mensaje.append(") ");
    mensaje.append("LIKE \'%");
    mensaje.append(valor);
    mensaje.append("%\' ORDER BY ");
    mensaje.append(criterio);
    getUsu_b().closeStatement();
    getUsu_b().setQuery(new QueryDescriptor (database1,mensaje.toString()));
    getUsu_b().refresh();
  }

  /**
   * Metodo publico que llama primeramente al metodo t_eliminar
   * que registra la operacion de eliminacion y pasa los datos a la
   * tabla de eliminados, luego este metodo se encarga de eliminar en si
   * de la tanla de usuarios dicho usuario.
   */
  public void Eliminar(int id_i) throws Exception {
    try {
        t_eliminar(id_i);
        String query = "DELETE FROM t_usuario WHERE id_i = ?";
        Connection connection = cp.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1,id_i);
        statement.execute();
        statement.close();
        cp.free(connection);
        getUsuarios().refresh();
    }
    catch (SQLException sqle) {
      throw new Exception("ERROR AL ELIMINAR EL USUARIO: "+ id_i +" Y CUYO MENSAJE DE ERROR ES: \n" + sqle);
    }
  }

  /**
   * Metodo que recupera de la Bd el usuario que se desea eliminar con el
   * metodo getUsuario_id(); Una vez obtenido el usuario se registra la
   * operacion de eliminacion, colocando  en la descripcion
   * de la operacion el nombre del usuario que se esta eliminando.
   * Luego se pasan todos los datos del objeto devuelto a la tabla de eliminados.
   */
  private void t_eliminar(int id_i) throws Exception {
    Connection connection = null;
    PreparedStatement statement = null;
    Usuario usuario=getUsuario_id(id_i);
    setLog(id_i,ELIM,"Eliminacion del Usuario: " +usuario.getNombre(),"local");//Se coloca en la descripcion el nombre del usuario
    try {
      String query = "INSERT INTO t_eliminados (id_i,ci,login,password,nombre,email_1,email_2,bloqueo,intentos_c,intentos_d,creacion)"+
                    "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? ,? ,? )";
      connection = cp.getConnection();
      statement = connection.prepareStatement(query);
      statement.setInt(1,usuario.getId_i());
      statement.setString(2,usuario.getCi());
      statement.setString(3,usuario.getLogin());
      statement.setString(4,usuario.getPassword());
      statement.setString(5,usuario.getNombre());
      statement.setString(6,usuario.getEmail1());
      statement.setString(7,usuario.getEmail2());
      statement.setString(8,usuario.getBloqueo());
      statement.setInt(9,usuario.getIntentos_c());
      statement.setInt(10,usuario.getIntentos_d());
      statement.setString(11,usuario.getCreacion());
      statement.execute();
    }
    catch(SQLException sqle) {// encapsualacion.- mantener los errores especificos de este metodo como ser SQLException dentro del mismo
      throw new Exception("ERROR AL INSERTAR EL USUARIO ELIMINADO EN LA TABLA DE ELIMINADOS \n" +sqle.getMessage());
    }
    finally {
      statement.close();
      cp.free(connection);
    }
  }

/**************************************************************************************
 **********************************LOGS************************************************
 *************************************************************************************/

  /**
   * Registra todas las operaciones efectuadas, basadas
   * primeramente en quien la efectuo (id_i), luego el tipo de
   * operacion efectuada, alguna informacion extra de la operacion,
   * y desde donde se efectuo la operacion, esta puede local lo cual
   * indica que fue realizada por el subsistema de administracion o puede
   * ser directamente el ip del usuario que realiza distintas operaciones
   * desde Internet.
   */
  public void setLog1(int id_i, int tipo, String datos, String ip) {
    try {
        String query = "INSERT INTO t_registro (id_i, tipo, datos, ip) VALUES (?,?,?,?)";
        Connection connection = cp.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
    	statement.setInt(1,id_i);
      	statement.setInt(2,tipo);
        statement.setString(3,datos);
        statement.setString(4,ip);
        statement.execute();
        statement.close();
        cp.free(connection);
/**EXPERIMENTAL*
        synchronized(getLogs()) {*/
          getLogs().refresh();
//        }
    }
    catch (SQLException sqle) {
      System.err.println("ERROR EN EL REGISTRO DEL LOG: " + sqle);
    }
    catch (Exception e) {
     System.err.println("ERROR EN EL DATASET (Logs) AL ACTUALIZAR LOS LOGS: " + e);
    }
  }

  /**
   * Sobre la misma conexion sin cerrarla  (caso mencionado en JDBC doc)
   * o realmente tendria que habilitar otra conexion?
   */
  public void setLog(int id_i, int tipo, String datos, String ip) {
    try {
        String query = "INSERT INTO t_registro (id_i, tipo, datos, ip) VALUES (?,?,?,?)";
        Connection connection = cp.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
    	statement.setInt(1,id_i);
      	statement.setInt(2,tipo);
        statement.setString(3,datos);
        statement.setString(4,ip);
        statement.execute();
        statement.close();
        cp.free(connection);
//        System.out.println("cuanto es ?" + aux());
        getLogs().refresh();

    }
    catch (SQLException sqle) {
      System.err.println("ERROR EN EL REGISTRO DEL LOG: " + sqle);
    }
    catch (Exception e) {
     System.err.println("ERROR EN EL DATASET (Logs) AL ACTUALIZAR LOS LOGS: " + e);
    }
  }

  public int aux() {
    int id_log=0;
    try {
//        String query = "SELECT LAST_INSERT_ID() AS id_log";
        String query = "SELECT currval('t_registro_id_log_seq') AS id_log";

        Connection connection = cp.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
    	ResultSet r= statement.executeQuery();
        while (r.next()){
          id_log = r.getInt("id_log");
        }
    	r.close();
    	statement.close();
        cp.free(connection);
    }
    catch (SQLException sqle) {
      System.err.println("ERROR EN LA OBTENCION DEL ULTIMO ID: " + sqle);
    }
   return id_log;
  }


 /**
 * SERA: "SELECT id_i, datos, ip, fecha FROM t_registro WHERE tipo = 1 AND fecha >= ? " ;
 * +" AND id_i NOT IN (SELECT id_i FROM t_registro WHERE tipo = 5 AND fecha >= ?)
 * SELECT id_i, datos, ip, fecha FROM t_registro WHERE tipo = 1 AND fecha >= '20020304120000'
 * AND id_i NOT IN (SELECT id_i FROM t_registro WHERE tipo = 5 AND fecha >= '20020303120000')
 */
  public Vector Logs_tree() {
    log_tree.clear();
    long a = System.currentTimeMillis() - (tiempo_log*1000);
    Timestamp tiempo= new Timestamp(a);
    try {//igual a 1 es que logro ingresar
        String query = "SELECT id_i, datos, ip, fecha FROM t_registro WHERE tipo = 1 AND fecha >= ? ";
        Connection connection = cp.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setTimestamp(1,tiempo);
      	ResultSet r= statement.executeQuery();
        while (r.next()){
          Log log = new Log();
          log.setId_i(r.getString("id_i"));
          log.setDatos(r.getString("datos"));
          log.setIp(r.getString("ip"));
          log.setFecha(Util.Convertir(r.getTimestamp("fecha")));
          log_tree.add(log);
        }
      	r.close();
    	statement.close();
        cp.free(connection);
    }
    catch (SQLException sqle) {
      System.err.println("ERROR EN LA OBTENCION DE LOS LOGS PARA TREE: " + sqle);
    }
    return log_tree;
  }

  /**
   * Metodo que cambia el registro de (tipo) autentificacion por el
   * tipo de salida basado en el id_i los datos y el ip junto con la fecha
   * esta tiene que ser de hace 5 minutos o segun especificado en tiempo_log.
   */
  public void log_salida(int id_i, String datos, String ip) {
    long a = System.currentTimeMillis() - (tiempo_log*1000);
    Timestamp tiempo= new Timestamp(a);
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      String query = "UPDATE t_registro SET tipo=5 WHERE id_i=? AND ip=? AND datos=? AND fecha >= ? ";
      connection = cp.getConnection();
      statement = connection.prepareStatement(query);
      statement.setInt(1,id_i);
      statement.setString(2,ip);
      statement.setString(3,datos);
      statement.setTimestamp(4,tiempo);
      statement.execute();
    }
    catch(SQLException sqle) {
      System.out.println("SE GENERO UN ERROR AL QUERER ACTUALIZAR EL LOG "+sqle);
    }
    finally {
      try {statement.close();}
      catch (Exception ex) {System.out.println("NO SE PUDO CERRAR LA CONEXION (Error aparte del error SQL)");    }
      cp.free(connection);
    }
  }

  //Calendar currenttime=Calendar.getInstance();
  //int minutos=currentime.get(Calendar.MINUTE)
  //int horas=currentime.get(Calendar.HOUR_OF_DAY)
  //java.util.Date currentdate=currenttime.getTime();
  //pero hace algo como currenttime.add(Calendar.DATE, 5);//aumentando 5 dias?
  public int getIngresos(int id_i,boolean continuos) {
    int ingresos=-1;
    long a = System.currentTimeMillis();
    long b = a - (tiempo_dis*1000);

    if(continuos) {
      b= a - (tiempo_cont*1000);//cambia a 5 minutos atras
    }

    Timestamp tiempo= new Timestamp(b);
    try {//no interesan id_i pero si que tipo='invalido' ; el alisas de la columna COUNT(id_i) en mysql funciona sin el AS
        String query = "SELECT COUNT(id_i) AS ingresos FROM t_registro WHERE id_i = ? AND tipo = 3 AND fecha >= ? GROUP BY id_i";
        Connection connection = cp.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
    	statement.setInt(1,id_i);
        statement.setTimestamp(2,tiempo);
      	ResultSet r= statement.executeQuery();
        while (r.next()){
          ingresos = r.getInt("ingresos"); //se trabaja con el alias
        }
      	r.close();
    	statement.close();
        cp.free(connection);
    }
    catch (SQLException sqle) {
      System.err.println("ERROR EN LA OBTENCION DEL NUMERO DE INGRESOS DE UN USUARIO : " + sqle);
    }
    return ingresos;
  }

  private void Borrar_logs(int id_log) throws Exception {
    try {
        String query = "DELETE FROM t_registro WHERE id_log = ?";
        Connection connection = cp.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1,id_log);
        statement.execute();
        statement.close();
        cp.free(connection);
        getLogs().refresh();
    }
    catch (SQLException sqle) {
      throw new Exception("ERROR AL ELIMINAR LOS DATOS DEL LOGS CUYO ID DE LOG ES: "+ id_log +"\n Y CUYO ERROR ES: " + sqle);
    }
  }

  public String Borrar_logs() throws Exception {
    StringBuffer sbf = new StringBuffer();
    try {
        String query = q_log +"AND t_registro.tipo != "+AUTE;//obtengo todos los registros excepto los que estan en el sistema actualmente
        Connection connection = cp.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
       	ResultSet r= statement.executeQuery();
        while (r.next()) {
          int id_log = r.getInt(e_log[0][1]); //o sera ID LOG
          sbf.append(id_log);
          sbf.append("\t");
          sbf.append(r.getString(e_log[1][1]));
          sbf.append("\t");
          sbf.append(r.getString(e_log[2][1]));
          sbf.append("\t");
          sbf.append(r.getString(e_log[3][1]));
          sbf.append("\t");
          sbf.append(r.getString(e_log[4][1]));
          sbf.append("\t");
          sbf.append(r.getString(e_log[5][1]));//Util.Convertir(r.getTimestamp("fecha")));
          sbf.append("\n");
          Borrar_logs(id_log);
        }
       	r.close();
    	statement.close();
        cp.free(connection);

        setLog(0,VACI,"Vaciado de la tabla de logs","local"); //0 tomado como administrador
    }
    catch (SQLException sqle) {
      throw new Exception("ERROR AL OBTENER LOS DATOS DEL LOGS Y CUYO ERROR ES: " + sqle);
    }
    catch (Exception e) {
      throw new Exception("ERROR DESCONOCIDO AL OBTENER LOS DATOS DEL LOGS Y CUYO ERROR ES: " + e);
    }

    return sbf.toString();
  }

  public String getUltimo_Vaciado() {
    String ret = "No existe";
    try {
        String query = "SELECT fecha FROM t_registro WHERE tipo = "+ VACI;
        Connection connection = cp.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
      	ResultSet r= statement.executeQuery();
        while (r.next()) {
          ret = Util.Convertir(r.getTimestamp("fecha"));
        }
      	r.close();
    	statement.close();
        cp.free(connection);
    }
    catch (SQLException sqle) {
      System.err.println("ERROR EN LA OBTENCION DEL ULTIMO VACIADO : " + sqle);
    }
    return ret;
  }

  public int getNumero_Logs() {
    int ret = 0;
    try {
        String query = "SELECT COUNT(id_log) AS numero FROM t_registro";
        Connection connection = cp.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
      	ResultSet r= statement.executeQuery();
        while (r.next()){
          ret = r.getInt("numero");
        }
      	r.close();
    	statement.close();
        cp.free(connection);
    }
    catch (SQLException sqle) {
      System.err.println("ERROR EN LA OBTENCION DEL NUEMRO DE LOGS : " + sqle);
    }
    return ret;
  }


  /**
   * Buscar_log busca en dentro de la tabla t_registro con la columna como criterio
   * de busqueda y el valor. Si se pasa como valor="" generara como resultado todos
   * los regsitros dentro de la tabla.
   *
   * Observar que se utiliza aqui la clase StringBuffer para unir las cadenas
   * debido a que es mas eficiente que al hacerlo con String y utlizar el
   * operador + para la concatenacion de cadenas.
   */
  public void Buscar_log(String criterio, String valor){
//    if(criterio.equals("tipo")) criterio="t_tipo.descripcion";
//    StringBuffer mensaje = new StringBuffer("SELECT id_log, id_i, t_tipo.descripcion, datos, ip, fecha  FROM t_registro, t_tipo WHERE ");
//    mensaje.append("t_registro.tipo = t_tipo.tipo AND UPPER(");
    for (int i=0; i < e_log.length; i++)
      if (e_log[i][1].equals(criterio))
       criterio=e_log[i][0];

    StringBuffer mensaje = new StringBuffer(q_log);
    mensaje.append("AND UPPER(");
    mensaje.append(criterio);
    mensaje.append(") ");
    mensaje.append("LIKE \'%");
    mensaje.append(valor);
    mensaje.append("%\' ORDER BY ");
    mensaje.append(criterio);

    if(criterio.equals("id_i")) {
      mensaje.delete(0,mensaje.length());
      mensaje.append(q_log);
      mensaje.append("AND ");
      mensaje.append(criterio);
      mensaje.append("=");
      mensaje.append(valor);
      mensaje.append(" ORDER BY ");
      mensaje.append(e_log[5][0]);//ordenado por fecha
    }
    getLogs().closeStatement();
    getLogs().setQuery(new QueryDescriptor(database1,mensaje.toString()));
    getLogs().refresh();
  }

  /**
   *
   * @param id_i
   * @return
   */
   public Vector Log_show(int id_i) {
    Vector log_t= new Vector();
    try {
        String query = "SELECT t_tipo.descripcion, datos, ip, fecha FROM t_registro, t_tipo WHERE t_registro.tipo = t_tipo.tipo  AND id_i=? ORDER BY fecha DESC";

        Connection connection = cp.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
    	statement.setInt(1,id_i);
      	ResultSet r= statement.executeQuery();
        while (r.next()){
          Log log = new Log();
          log.setDescripcion(r.getString("descripcion"));
          log.setDatos(r.getString("datos"));
          log.setIp(r.getString("ip"));
          log.setFecha(Util.Convertir(r.getTimestamp("fecha")));
          log_t.add(log);
        }
      	r.close();
    	statement.close();
        cp.free(connection);
    }
    catch (SQLException sqle) {
      System.err.println("ERROR EN LA OBTENCION DE LOS LOGS : " + sqle);
    }
    return log_t;
  }

/**************************************************************************************
 **********************************CUENTAS*********************************************
 *************************************************************************************/
  public int [] Buscar_cuentas(String ci){
    int num_cuentas[] = new int [3];
    Buscar_dpf("ci", ci, false);
    num_cuentas[0] = getDpf().getRowCount();
    Buscar_creditos("ci", ci, false);
    num_cuentas[1] = getCreditos().getRowCount();
    Buscar_ahorro("ci", ci, false);
    num_cuentas[2] = getAhorros().getRowCount();
    return num_cuentas;
  }

  public Vector Cuentas_dpf(/*int id_i*/String ci) {
    Vector cuentas= new Vector();
    try {
        String query = "SELECT t_dpf.dpf,apertura,plazo,vcto,capital,tasa,periodo,moneda,caja_a" + /*t_cuentas*/
        " FROM t_dpf, t_dpfr WHERE (t_dpf.dpf = t_dpfr.dpf) AND t_dpfr.ci = ?"; /*= t_cuentas.cuenta AND (t_cuentas.tipo = 'd' ) AND t_cuentas.id_i = ?"; */
        Connection connection = cp.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
      	statement.setString(1,ci);//setInt(id_i)
      	ResultSet r= statement.executeQuery();
        while (r.next()){
          Dpf dpf = new Dpf();
          dpf.setDpf(r.getString("dpf"));
          dpf.setApertura(r.getString("apertura"));
          dpf.setPlazo(r.getString("plazo"));
          dpf.setVcto(r.getString("vcto"));
          dpf.setCapital(r.getDouble("capital"));
          dpf.setTasa(r.getDouble("tasa"));
          dpf.setPeriodo(r.getString("periodo"));
          dpf.setMoneda(r.getString("moneda"));
          dpf.setCaja_a(r.getString("caja_a")); //en realidad deberia ser Int
          cuentas.add(dpf);
        }
      	r.close();
    	statement.close();
        cp.free(connection);
    }
    catch (SQLException sqle) {
      System.err.println("ERROR EN LA OBTENCION DE LAS CUENTAS DE DPF: " + sqle);
    }
    return cuentas;
  }

  /**
   * Metodo que realiza la busqueda de valores en la tabla de cuentas en dpf
   * y es utilizado por el sistema de administracion.
   */
  public void Buscar_dpf(String criterio, String valor, boolean todo) {
//    StringBuffer mensaje = new StringBuffer("SELECT t_dpf.dpf,apertura,plazo,vcto,capital,tasa,periodo,moneda,caja_a FROM t_dpf, t_dpfr "+
//      "WHERE (t_dpf.dpf = t_dpfr.dpf) AND ");// FROM t_dpf, t_cuentas WHERE (t_dpf.dpf = t_cuentas.cuenta) AND (t_cuentas.tipo = 'd' ) AND ");
    for (int i=0; i < e_dpf.length; i++)
      if (e_dpf[i][1].equals(criterio))
       criterio=e_dpf[i][0];
    StringBuffer mensaje = new  StringBuffer(q_dpf+",t_dpfr WHERE (t_dpf.dpf = t_dpfr.dpf) AND ");
    if(todo) {
      mensaje.delete(0,mensaje.length());
      mensaje.append(q_dpf);
      mensaje.append("WHERE ");
    }
    mensaje.append("UPPER(");
    mensaje.append(criterio);
    mensaje.append(") ");
    mensaje.append("LIKE \'%");
    mensaje.append(valor);
    mensaje.append("%\' ORDER BY ");
    mensaje.append(criterio);
    getDpf().closeStatement();
    getDpf().setQuery(new QueryDescriptor (database1,mensaje.toString()));
    getDpf().refresh();
  }

  public Vector Cuentas_cred(String ci) {
    Vector cuentas= new Vector();
    try {
        String query = "SELECT t_cred.cred, monto,tasa,plazo,tipocuota,tipoamort,diasamort,f_desemb,saldo,caja_a " +
        "FROM t_cred, t_credr WHERE (t_cred.cred = t_credr.cred) AND t_credr.ci = ?";
        Connection connection = cp.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
    	statement.setString(1,ci);
      	ResultSet r= statement.executeQuery();
        while (r.next()){
          Credito credito = new Credito();
          credito.setCred(r.getString("cred"));
          credito.setMonto(r.getDouble("monto"));
          credito.setTasa(r.getDouble("tasa"));
          credito.setPlazo(r.getString("plazo"));
          credito.setTipocuota(r.getString("tipocuota"));
          credito.setTipoamort(r.getString("tipoamort"));
          credito.setDiasamort(r.getString("diasamort"));
          credito.setF_desemb(r.getString("f_desemb"));
          credito.setSaldo(r.getDouble("saldo"));
          credito.setCaja_a(r.getString("caja_a")); //en realidad deberia ser Int
          cuentas.add(credito);
        }
      	r.close();
    	statement.close();
        cp.free(connection);
    }
    catch (SQLException sqle) {
      System.err.println("ERROR EN LA OBTENCION DE LAS CUENTAS: " + sqle);
    }
    return cuentas;
  }

  public void Buscar_creditos(String criterio, String valor, boolean todo ) {
    for (int i=0; i < e_cred.length; i++)
      if (e_cred[i][1].equals(criterio))
       criterio=e_cred[i][0];
    StringBuffer mensaje = new  StringBuffer(q_cred+",t_credr WHERE (t_cred.cred = t_credr.cred) AND ");
//    StringBuffer mensaje = new StringBuffer("SELECT t_cred.cred,monto,tasa,plazo,tipocuota,tipoamort,diasamort,f_desemb,saldo,caja_a " +
//    "FROM t_cred, t_credr WHERE (t_cred.cred = t_credr.cred) AND ");
    if(todo) {
      mensaje.delete(0,mensaje.length());
      mensaje.append(q_cred);
      mensaje.append("WHERE ");
    }
    mensaje.append("UPPER(");
    mensaje.append(criterio);
    mensaje.append(") ");
    mensaje.append("LIKE \'%");
    mensaje.append(valor);
    mensaje.append("%\' ORDER BY ");
    mensaje.append(criterio);
    getCreditos().closeStatement();
    getCreditos().setQuery(new QueryDescriptor (database1,mensaje.toString()));
    getCreditos().refresh();
  }

  public Vector Cuentas_ahorro(String ci) {
    Vector cuentas= new Vector();
    try {
        String query = "SELECT t_cda.cda, moneda,interes,periodo,saldo,inpp,fecha " +
        "FROM t_cda, t_cdar WHERE (t_cda.cda = t_cdar.cda) AND t_cdar.ci = ? ";
        Connection connection = cp.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
      	statement.setString(1,ci);
      	ResultSet r = statement.executeQuery();
        while (r.next()){//no interesan id_i ni tipo
            Ahorro ahorro = new Ahorro();
            ahorro.setCda(r.getString("cda"));
            ahorro.setMoneda(r.getString("moneda"));
            ahorro.setInteres(r.getDouble("interes"));
            ahorro.setPeriodo(r.getString("periodo"));
            ahorro.setSaldo(r.getDouble("saldo"));
            ahorro.setInpp(r.getDouble("inpp"));
            ahorro.setFecha(r.getString("fecha"));
            cuentas.add(ahorro);
            ahorro = null;
        }
      	r.close();
    	statement.close();
        cp.free(connection);
    }
    catch (SQLException sqle) {
      System.err.println("ERROR EN LA OBTENCION DE LAS CUENTAS DE AHORRO: " + sqle);
    }
    return cuentas;
  }

  public void Buscar_ahorro(String criterio, String valor, boolean todo ) {
    for (int i=0; i < e_cda.length; i++)
      if (e_cda[i][1].equals(criterio))
       criterio=e_cda[i][0];
    StringBuffer mensaje = new  StringBuffer(q_cda+",t_cdar WHERE (t_cda.cda = t_cdar.cda) AND ");
    if(todo) {
      mensaje.delete(0,mensaje.length());
      mensaje.append(q_cda);
      mensaje.append("WHERE ");
    }
    mensaje.append("UPPER(");
    mensaje.append(criterio);
    mensaje.append(") ");
    mensaje.append("LIKE \'%");
    mensaje.append(valor);
    mensaje.append("%\' ORDER BY ");
    mensaje.append(criterio);
    getAhorros().closeStatement();
    getAhorros().setQuery(new QueryDescriptor (database1,mensaje.toString()));
    getAhorros().refresh();
  }


  public Vector movimientos(String ci, int sel,String tipo) {
    Vector movimientos = new Vector();
    try {
//        String query = "SELECT * FROM t_dpfq WHERE dpf = ?";
        String query = "SELECT fecha,operacion,d_h,monto FROM t_dpfq,t_dpfr WHERE (t_dpfq.dpf = t_dpfr.dpf) AND t_dpfq.dpf=? AND ci=? ";
//        if(tipo.equals("cda")) query ="SELECT * FROM t_cdaq WHERE cda = ?";
        if(tipo.equals("cda")) query ="SELECT fecha,operacion,d_h,monto FROM t_cdaq,t_cdar WHERE (t_cdaq.cda = t_cdar.cda) AND t_cdaq.cda=? AND ci=?";
        else if (tipo.equals("cred")) return (mov_cred(sel));
        Connection connection = cp.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
    	statement.setInt(1,sel);
        statement.setString(2,ci);

      	ResultSet r= statement.executeQuery();
        while (r.next()){
          Movimientos tr = new Movimientos();
          tr.setFecha(r.getString("fecha"));
          tr.setOperacion(r.getString("operacion"));
          tr.setD_h(r.getString("d_h"));
          tr.setMonto(r.getDouble("monto"));
          movimientos.add(tr);
        }
      	r.close();
    	statement.close();
        cp.free(connection);
    }
    catch (SQLException sqle) {
      System.err.println("ERROR EN LA OBTENCION DE LA CUENTA AL OBTENER TRANSACCIONES: " + sqle);
    }
    return movimientos;
  }

  public Vector mov_cred(int sel) {
    Vector movimientos = new Vector();
    try {
        String query = "SELECT * FROM t_credq WHERE cred = ?";
        Connection connection = cp.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
    	statement.setInt(1,sel);
      	ResultSet r= statement.executeQuery();
        while (r.next()){
          Movimientos tr = new Movimientos();
          tr.setFecha(r.getString("fecha"));
          tr.setOperacion(r.getString("operacion"));
          tr.setMonto(r.getDouble("monto"));
          tr.setAmort(r.getDouble("amort"));
          tr.setInteres(r.getDouble("interes"));
          tr.setOtros(r.getDouble("otros"));
          tr.setMonto_pag(r.getDouble("monto_pag"));
          tr.setSaldo_cap(r.getDouble("saldo_cap"));
          movimientos.add(tr);
        }
      	r.close();
    	statement.close();
        cp.free(connection);
    }
    catch (SQLException sqle) {
      System.err.println("ERROR EN LA OBTENCION DE LA CUENTA AL OBTENER LOS MOVIMIENTOS EN CREDITOS: " + sqle);
    }
    return movimientos;
  }



  public void Buscar_mov(String tipo, String valor) {
    StringBuffer mensaje = new StringBuffer(q_dpfq+" WHERE dpf ");

    if(tipo.equals("cred")) {
      mensaje.delete(0,mensaje.length());
      mensaje.append("SELECT cred,fecha,operacion,d_h,monto,amort FROM t_credq WHERE cred ");
    }
    else if(tipo.equals("cda")) {
      mensaje.delete(0,mensaje.length());
      mensaje.append("SELECT cda,fecha,operacion,d_h,monto FROM t_cdaq WHERE cda ");
    }
    mensaje.append("=");
    mensaje.append(valor);
    mensaje.append(" ORDER BY fecha");
    getMov().closeStatement();
    getMov().setQuery(new QueryDescriptor(database1,mensaje.toString()));
    getMov().refresh();
  }

  public byte[] getLlave(){
  byte [] llave_= null;
    try {
      String query = "SELECT llave_log FROM t_adm ";
      Connection connection = cp.getConnection();
      PreparedStatement statement = connection.prepareStatement(query);
      ResultSet r= statement.executeQuery();
      while (r.next()){
        llave_= Encriptacion.convertir(r.getString("llave_log"));//getBytes("llave_log");
      }
      r.close();
      statement.close();
      cp.free(connection);
    }
    catch (SQLException sqle) {
      System.err.println("ERROR EN LA OBTENCION DE LA LLAVE DE LOGS ALMACENADA EN LA BD: " + sqle);
    }
    return llave_;
  }

  public void setLlave(byte [] llave_){
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      String query = "UPDATE t_adm SET llave_log=?";// WHERE id_i=?";
      connection = cp.getConnection();
      statement = connection.prepareStatement(query);
      statement.setString(1, Encriptacion.convertir(llave_));//setBytes(1,llave_);//  OOOOOOJOOOOOO
      statement.execute();
    }
    catch(SQLException sqle) {// encapsualacion.- mantener los errores especificos de este metodo como ser SQLException dentro del mismo
      System.out.println("SE GENERO UN ERROR AL QUERER ACUALIZAR LA LLAVE DE LOGS "+sqle);
    }
    finally {
      try {statement.close();/*if(connection != null)*/}
      catch (Exception ex) {System.out.println("NO SE PUDO CERRAR LA CONEXION (Error aparte del error SQL)");    }
      cp.free(connection);
    }

  }



  /**
   * Metodos cuyo proposito sirve para la utilizacion dentro del sistema
   * de administracion en todas las tablas, ya que estas esperan como parametro
   * un query.
   */
  public com.borland.dx.sql.dataset.QueryDataSet getUsuarios() {
    return usuarios;
  }

  public com.borland.dx.sql.dataset.QueryDataSet getLogs() {
    return logs;
  }

  public com.borland.dx.sql.dataset.QueryDataSet getUsu_b() {
    return usu_b;
  }

  public com.borland.dx.sql.dataset.QueryDataSet getDpf() {
    return dpf;
  }

  public com.borland.dx.sql.dataset.QueryDataSet getCreditos() {
    return creditos;
  }

  public com.borland.dx.sql.dataset.QueryDataSet getAhorros() {
    return ahorros;
  }

  public com.borland.dx.sql.dataset.QueryDataSet getMov() {
    return mov;
  }



  /**
   * Inserta un nuevo dato a la Base de datos

  public void setUsuario(Usuario usuario) throws Exception {
    Connection connection = null;
    PreparedStatement statement = null;
    //necesitamos este try porque en caso de error necesitamos cerrar la conexion aqui,
    //situacion que no se podria efectuar desde fuera de la funcion si es que algo saliera mal
    try {
      String query = "INSERT INTO t_usuario (ci,login,password,nombre,email_1,email_2,bloqueo,intentos_c,intentos_d)" +
                    "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?)";
      connection = cp.getConnection();
      statement = connection.prepareStatement(query);
      statement.setString(1,usuario.getCi());
      statement.setString(2,usuario.getLogin());
      statement.setString(3,usuario.getPassword());
      statement.setString(4,usuario.getNombre());
      statement.setString(5,usuario.getEmail1());
      statement.setString(6,usuario.getEmail2());
      statement.setString(7,usuario.getBloqueo());
      statement.setInt(8,usuario.getIntentos_c());
      statement.setInt(9,usuario.getIntentos_d());
      statement.execute();
    }
    catch(SQLException sqle) {// encapsualacion.- mantener los errores especificos de este metodo como ser SQLException dentro del mismo
      if (sqle.getMessage().indexOf("login_index") > -1) {// ("login_index")  ("key 3")
        throw new Exception("ERROR AL INSERTAR EL NUEVO USUARIO: YA EXISTE EL LOGIN: " + usuario.getLogin()+ "\n POR FAVOR CAMBIE DE LOGIN");
      }
      else if(sqle.getMessage().indexOf("nombre_index") > -1) {// ("nombre_index") ("key 2")
      throw new Exception("ERROR AL INSERTAR EL NUEVO USUARIO: YA EXISTE EL NOMBRE: " + usuario.getNombre() + "\n POR FAVOR CAMBIE DE NOMBRE");
      }
      else throw new Exception("ERROR AL INSERTAR EL NUEVO USUARIO " +sqle.getMessage());
    }
    finally {
      statement.close();
      cp.free(connection);
      getUsuarios().refresh();
    }
  }
*/






  /**
   * Cerramos tanto la conexion a la Bd. atraves del objeto database1
   * asi como tambien todas las conexiones reservadas en el Pool de Conexiones.
   */
  public void cerrar() {
    database1.closeConnection();
    cp.toString();
    cp.CerrarTodos();
  }

  /**
   * Este metodo es utilizado conjuntamente con la Base de datos y simplifica la construccion
   * de consultas (cadenas) apartir de un arreglo en dos dimensiones cuyos valores
   * van en parejas una contiene el nombre de una columna y el otro valor es el alias.
   * El metodo solo se lo utiliza en esta clase y es estatico ya que se lo necesita
   * para conformar consultas que son estaticas (inalterables durante la ejecucion del sistema).
   */
  static private String formar(String [][] array) {
    StringBuffer r = new StringBuffer();
    for (int i=0; i < array.length; i++) {
      for (int j=0; j < array[i].length-1 ; j++) {
        r.append(array[i][j]);
        r.append(" AS \"");
        r.append(array[i][j+1]);
        r.append("\"");
      }
      r.append(",");
    }
    r.deleteCharAt(r.length()-1);
    return r.toString();
  }
}

/*
  public void Asignar(int id_i, Vector cuentas, String tipo) throws Exception {
    try {
        String repetidas = Verificar(id_i,cuentas,tipo);
        String query = "INSERT INTO t_cuentas (id_i, cuenta, tipo) VALUES (?, ?, ?)";
        Connection connection = cp.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
    	for(int i=0;i<cuentas.size();i++) {
          statement.setInt(1,id_i);
          statement.setInt(2,((Integer)cuentas.elementAt(i)).intValue());
          statement.setString(3,tipo);
          statement.execute();
        }
        statement.close();
        cp.free(connection);
        if (!repetidas.equals("")) {
           throw new Exception("LAS SIGUIENTES CUENTAS NO FUERON ASIGNADAS POR QUE YA LO ESTAN AL USUARIO:" + id_i + " \n" + repetidas);
        }
    }
    catch (SQLException sqle) {
      throw new Exception("ERROR AL ASIGNAR UNA CUENTA AL USUARIO: "+id_i +"CUYO MENSAJE DE ERROR ES" +sqle.getMessage());
    }
  }

  public String Verificar(int id_i,Vector cuentas,String tipo) throws Exception {
  StringBuffer repetidas = new StringBuffer("");
    try {
        String query = "SELECT * FROM t_cuentas WHERE id_i = ? AND cuenta = ? AND tipo= ? ";
        Connection connection = cp.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
       	for(int i=0;i<cuentas.size();i++) {
          int cuenta = ((Integer)cuentas.elementAt(i)).intValue();
          statement.setInt(1,id_i);
          statement.setInt(2,cuenta);
          statement.setString(3,tipo);
          ResultSet r= statement.executeQuery();
          while (r.next()){//no interesan id_i ni tipo
            repetidas.append(cuenta);
            repetidas.append(":");
            cuentas.removeElementAt(i);
          }
          r.close();
        }
    	statement.close();
        cp.free(connection);
    }
    catch (SQLException sqle) {
      throw new Exception("ERROR AL COMPROBAR LA EXISTENCIA DE DICHA CUENTA PARA ESTE USUARIO: "+ id_i +" CUYO MENSAJE ES " + sqle);
    }
    return repetidas.toString();
  }

  public void Desasignar(int id_i, int cuentas[],String tipo) throws Exception {
    try {
        String query = "DELETE FROM t_cuentas WHERE id_i = ? AND tipo=? AND cuenta = ?";
        Connection connection = cp.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
    	for(int i=0;i<cuentas.length;i++) {
          statement.setInt(1,id_i);
          statement.setString(2,tipo);
          statement.setInt(3,cuentas[i]);
          statement.execute();
        }
        statement.close();
        cp.free(connection);
        getDpf().refresh();
        getCreditos().refresh();
    }
    catch (SQLException sqle) {
      throw new Exception("ERROR AL DESVINCULAR EL USUARIO" + id_i + " CON UNA CUENTA: " + sqle);
    }
  }
*/