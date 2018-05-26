package BUSINESS;

/**
 * Title:        Business Services
 * Description:  Clases encargadas de la aplicacion o logica de negocios
 * Copyright:    Copyright (c) 2003
 * Company:      Entidad Financiera
 * @author Ivan Paniagua
 * @version 1.0
 */

public class Log {

  public Log() {
  }
  private String id_i;
  private String datos;
  private String fecha;
  private String ip;
  private String descripcion;
  public void setId_i(String newId_i) {
    id_i = newId_i;
  }
  public String getId_i() {
    return id_i;
  }
  public void setDatos(String newDatos) {
    datos = newDatos;
  }
  public String getDatos() {
    return datos;
  }
  public void setFecha(String newFecha) {
    fecha = newFecha;
  }
  public String getFecha() {
    return fecha;
  }
  public void setIp(String newIp) {
    ip = newIp;
  }
  public String getIp() {
    return ip;
  }

  public boolean equals(Log otro) {
  boolean igual= false;
  if (id_i.equals(otro.getId_i()) && datos.equals(otro.getDatos()) && ip.equals(otro.getIp()) )
    igual = true;
  return igual;
  }
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }
  public String getDescripcion() {
    return descripcion;
  }

}