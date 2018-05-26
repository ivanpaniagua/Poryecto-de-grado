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
public class Borrar /* implements Mio*/ {

  private ConnectionPool cp;
  private Usuario usuario;
  private static Borrar borrar;  // la misma clase es estatica
  private static final String Driver = "org.gjt.mm.mysql.Driver"; // ; //"com.mysql.jdbc.Driver";
  private static final String URL ="jdbc:mysql://localhost/mio";// "jdbc:mysql://ivanpaniagua.db.8299947.hostedresource.com";//"jdbc:mysql://ivan/mio";
  private static final String username = "jaquy";//ivanpaniagua";//"mio";
  private static final String password ="iddqd";// "P01nts3rv3!";//"iddqd";

  public Borrar() {
    System.out.println("se ha creado una nuevo objeto Entidad");
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
      try { cp = new ConnectionPool(Driver, URL, username, password, 10, 50, true);}
    catch(SQLException sqle) {
      cp = null;// NO ES NECESARIO O LO ES? NO LO ES PARA NINGUN OBJETO EN JAVA PERO ESTE OBJETO NO SOLO ES EN JAVA PURO (CONEXION A LA BD)
      throw new Exception("ERROR EN EL POOL: " + sqle);
    }
    catch (Exception e){
      throw new Exception("ERROR EN LA CONEXION A LA BD: " + e);
    }
  }

  public static Borrar getBorrar() {
    if (borrar == null) {
      borrar = new Borrar();
    }
    return borrar;
  }

   public Usuario getUsuario(String login) {
    Connection connection = null;
    PreparedStatement statement = null;
    Usuario usuario = null;
    try {
        String query = "SELECT * FROM t_usuario WHERE login = ?";
        connection = cp.getConnection();
        statement = connection.prepareStatement(query);
  // Necesitamos sincronizar para prevenir condiciones rapidas.
  //      synchronized(connection) {
          statement.setString(1,login);
          ResultSet r= statement.executeQuery();
          while (r.next()){
              usuario = new Usuario(r.getInt("id_i"),r.getString("ci"),r.getString("login"),r.getString("password"),
              r.getString("nombre"),r.getString("email_1"),r.getString("email_2"),r.getString("bloqueo"),
              r.getInt("intentos_c"),r.getInt("intentos_d"),Util.Convertir(r.getTimestamp("creacion"))/*r.getString("creacion")*/);
          }
          r.close();//final del synchronized
  //      }// fin de sync
        statement.close();
        cp.free(connection);
    }
    catch(SQLException sqle) {
        System.out.println("ERROR EN LA OBTENCION DEL USUARIO" + sqle );
        return null;
    }
    return usuario;
  }
}
