package BUSINESS;

/**
 * Title:        Business Services
 * Description:  Clases encargadas de la aplicacion o logica de negocios
 * Copyright:    Copyright (c) 2003
 * Company:      Entidad Financiera
 * @author Ivan Paniagua
 * @version 1.0
 */

/*cuando se trata de interfaces todos los metodos de esta clase deben ser abstract
*/
public interface Mio {
//  public abstract Borrar getBorrar();

  public abstract Usuario getUsuario(String login);
}
