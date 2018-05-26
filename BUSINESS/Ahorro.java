package BUSINESS;

/**
 * Title:        Business Services
 * Description:  Clases encargadas de la aplicacion o logica de negocios
 * Copyright:    Copyright (c) 2003
 * Company:      Entidad Financiera
 * @author Ivan Paniagua
 * @version 1.0
 */

public class Ahorro {

  public Ahorro() {
  }
  private String cda;
  private String moneda;
  private String periodo;
  private double saldo;
  private double inpp;
  private String fecha;
  private double interes;
  public String getCda() {
    return cda;
  }
  public void setCda(String newCda) {
    cda = newCda;
  }
  public void setMoneda(String newMoneda) {
    moneda = newMoneda;
  }
  public String getMoneda() {
    return moneda;
  }
  public void setInteres(double newInteres) {
    interes = newInteres;
  }
  public String getInteres() {
    return Util.DobleString(interes);
  }
  public void setPeriodo(String newPeriodo) {
    periodo = newPeriodo;
  }
  public String getPeriodo() {
    return periodo;
  }
  public void setSaldo(double newSaldo) {
    saldo = newSaldo;
  }
  public String getSaldo() {
    return Util.DobleString(saldo);
  }
  public void setInpp(double newInpp) {
    inpp = newInpp;
  }
  public String getInpp() {
    return Util.DobleString(inpp);
  }
  public void setFecha(String newFecha) {
    fecha = newFecha;
  }
  public String getFecha() {
    return fecha;
  }
}