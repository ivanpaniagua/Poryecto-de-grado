package BUSINESS;

import java.sql.*;
import java.util.*;

/**
 * Title:        Business Services
 * Description:  Clases encargadas de la aplicacion o logica de negocios
 * Copyright:    Copyright (c) 2003
 * Company:      Entidad Financiera
 * @author Ivan Paniagua
 * @version 1.0
 */

/** Clase encargada de manejar, reciclar, precargar conexiones JDBC
 *   <P>
 */

public class ConnectionPool implements Runnable {
  private String driver, url, username, password;
  private int max;
  private boolean esperar;
  private Vector ConexionesLibres, ConexionesOcup;
  private boolean pendiente = false;

  /**
   * El constructor de la clase recibe como parametros el driver que comunica con
   * la Bd., tambien recibe la url donde se encuentra la Bd. en si, dentro
   * del manejador de Bd. luego recibe el login y password de la misma
   * asi como tambien el tamaño del Pool o sea el numero de conexiones
   * con que se comenzara, el maximo de conexiones que podran hacerse y por ultimo
   * si se llega al tope de conexiones, y llega una solicitud de conexion mas
   * esta debera esperar o no.
   */

  public ConnectionPool(String driver, String url, String username, String password, int iniciar, int max, boolean esperar) throws SQLException {
    this.driver = driver;
    this.url = url;
    this.username = username;
    this.password = password;
    this.max = max;
    this.esperar = esperar;
    if (iniciar > max) {
      iniciar = max;
    }
    ConexionesLibres = new Vector(iniciar);
    ConexionesOcup = new Vector();
    for(int i=0; i<iniciar; i++) {
      ConexionesLibres.addElement(NuevaConexion());
    }
  }

  public synchronized Connection getConnection() throws SQLException {
    if (!ConexionesLibres.isEmpty()) {
      Connection connection = (Connection)ConexionesLibres.lastElement();
      int ultimo = ConexionesLibres.size() - 1;
      ConexionesLibres.removeElementAt(ultimo);
      // Si una conexion en la lista de conexiones libres esta cerrado, ya sea por un timeout, o cerrado explicitamente,
      // entonces lo quitamos de la lista de conexiones libres  y repetimos el proceso de obtener una Conexion
      // Tambien despertamos a los threads que estuvieron esperando por una conexion ya que el max de conexiones fue
      // alcanzado.
      if (connection.isClosed()) {
        System.out.println("esta cerrada la conexion");
        notifyAll(); // Se envia una señal a cualquiera que estuviera esperando
        return(getConnection());
      } else {
        ConexionesOcup.addElement(connection);
        return(connection);
      }
    } else {

      // Tres posibles casos:
      // 1) se ha alcanzado el limite maximo (max). Asi que establecemos una conexion (una sola) en background si no existe ya
      //    una pendiente, entonces esperamos por la proxima conexion libre (ya sea si fue recientemente establecida o no).
      // 2) Se alcanza el limite maximo y "esperar" esta en falso. En tal caso lanzamos una nueva SQLException.
      // 3) Se alcanza el limite maximo y "esperar" esta en true lo que normalmente se deberia hacer es
      //    esperar por una conexion libre (). Entonces hacemos lo mismo que en la segunda parte del primer caso: esperar por la proxima conexion libre.

      if ((total() < max) && !pendiente) {
        BackgroundConnection();
      } else if (!esperar) {//no es lo habitual tener este caso. Pero en caso de darse si habria errores
        throw new SQLException("Se ha alcanzado el limite de Conexiones");
      }
      // esperamos por una nueva conexion a ser establecida (Si llamamos a BackgroundConnection)
      // o por una conexion existe que fue liberada.
      try {
        wait();
      } catch(InterruptedException ie) {}
      // Alguien libero una conexion asi que tratamos de nuevo.
      return(getConnection());
    }
  }

  /**
   *  No se pueden hacer solo nuevas conexiones en primer plano (foreground) cuando ninguna este libre,
   *  ya que tomaria muchos segundos con una conexion de red lenta. (Cuando por alguna razon otra aplicacion
   *  ya esta utilizando muchas conexiones a la Bd. y esta quiere utilizar mas conexiones)
   *  En su lugar comenzamos un thread que establece una nueva conexion y espera.
   *  Se despertara cuando se establezca una nueva conexion o si algien finaliza con una conexion existente.
   */
  private void BackgroundConnection() {
    pendiente = true;
    try {
      Thread hilo = new Thread(this);
      hilo.start();
    } catch(OutOfMemoryError oome) {
      // Rinde sobre una nueva conexion
    }
  }

  public void run() {
    try {
      Connection connection = NuevaConexion();
      synchronized(this) {
        ConexionesLibres.addElement(connection);
        pendiente = false;
        notifyAll();
      }
    } catch(Exception e) { // SQLException o OutOfMemory
      // Rinde sobre una nueva conexion y espera por la existencia de una a ser liberada.
    }
  }

  /**
   * Este metodo explicitamente realiza una nueva conexion. Llamado en primer plano (foreground )
   * cuando esta clase es inicializada y llamada en segundo plano( background) cuando esta corriendo
   */
  private Connection NuevaConexion() throws SQLException {
    try {
      // Cargamos el driver de la base de datos si no lo estaba anteriormente
      Class.forName(driver);
      // Establece la conexion en red a la base de datos.
      Connection connection = DriverManager.getConnection(url, username, password);
      return(connection);
    } catch(ClassNotFoundException cnfe) {
      // Simplificamos los bloques try/catch de las clases que usan este metodo cogiendo solo un tipo de exepcion.
      throw new SQLException("No se encuentran las clases del driver: " + driver);
    }
  }

  public synchronized void free(Connection connection) {
    ConexionesOcup.removeElement(connection);
    ConexionesLibres.addElement(connection);
    // despertamos a los threads que estuvieron esperando por una conexion
    notifyAll();
  }

  public synchronized int total() {
    return(ConexionesLibres.size() + ConexionesOcup.size());
  }

  /** Cerramos todas las conexiones. Se debe usar con cuidado ya que
   *  se debe asegurar que ninuna conexion este en uso.
   *  Notar que no es necesario llamar este metodo cuando se termina con esta clase
   *  ya que las conexiones estan garantizadas a ser cerradas cuando el garbage collector las recoga
   *  Pero este metodo otorga mas control cuando las conexiones son cerradas
   */

  public synchronized void CerrarTodos() {
    cerrarConexiones(ConexionesLibres);
    ConexionesLibres = new Vector();
    cerrarConexiones(ConexionesOcup);
    ConexionesOcup = new Vector();
  }

  private void cerrarConexiones(Vector connections) {
    try {
      for(int i=0; i<connections.size(); i++) {
        Connection connection =(Connection)connections.elementAt(i);
        if (!connection.isClosed()) {
          connection.close();
        }
      }
    } catch(SQLException sqle) {
      // Ignoramos los errores; el garbage collect se encargara de los mismos.
    }
  }

  public synchronized String toString() {
    String info ="Bd(" + url + "," + username + "), libres=" + ConexionesLibres.size() + ", ocupadas=" + ConexionesOcup.size() + ", max=" + max;
    return(info);
  }
}
