package BUSINESS;

/**
 * Title:        Business Services
 * Description:  Clases encargadas de la aplicacion o logica de negocios
 * Copyright:    Copyright (c) 2003
 * Company:      Entidad Financiera
 * @author Ivan Paniagua
 * @version 1.0
 */

public class Movimientos {

  public Movimientos() {
  }
  private String fecha;
  private String operacion;
  private String d_h;
  private double monto;
  private double amort;
  private double interes;
  private double otros;
  private double monto_pag;
  private double saldo_cap;
  public String getFecha() {
    return fecha;
  }
  public void setFecha(String newFecha) {
    fecha = newFecha;
  }
  public void setOperacion(String newOperacion) {
    operacion = newOperacion;
  }
  public String getOperacion() {
    return operacion;
  }
  public void setD_h(String newD_h) {
    d_h = newD_h;
  }
  public String getD_h() {
    return d_h;
  }
  public void setMonto(double newMonto) {
    monto = newMonto;
  }
  public double getMonto() {
    return monto;
  }
  public void setAmort(double newAmort) {
    amort = newAmort;
  }
  public double getAmort() {
    return amort;
  }
  public void setInteres(double newInteres) {
    interes = newInteres;
  }
  public double getInteres() {
    return interes;
  }
  public void setOtros(double newOtros) {
    otros = newOtros;
  }
  public double getOtros() {
    return otros;
  }
  public void setMonto_pag(double newMonto_pag) {
    monto_pag = newMonto_pag;
  }
  public double getMonto_pag() {
    return monto_pag;
  }
  public void setSaldo_cap(double newSaldo_cap) {
    saldo_cap = newSaldo_cap;
  }
  public double getSaldo_cap() {
    return saldo_cap;
  }
}
