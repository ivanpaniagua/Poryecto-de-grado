package BUSINESS;

/**
 * Title:        Business Services
 * Description:  Clases encargadas de la aplicacion o logica de negocios
 * Copyright:    Copyright (c) 2003
 * Company:      Entidad Financiera
 * @author Ivan Paniagua
 * @version 1.0
 */

public class Dpf {

  public Dpf() {
  }
  private String dpf;
  private String apertura;
  private double capital;
  private double tasa;
  private double total;
  private String plazo;
  private String vcto;
  private String moneda;
  private String caja_a;
  private String periodo;

  public String getDpf() {
    return dpf;
  }
  public void setDpf(String newDpf) {
    dpf = newDpf;
  }
  public void setApertura(String newApertura) {
    apertura = newApertura;
  }
  public String getApertura() {
    return apertura;
  }
  public void setCapital(double newCapital) {
    capital = newCapital;
  }
  public String getCapital() {
    return Util.DobleString(capital);// String.valueOf(monto);
  }
  public void setTasa(double newTasa) {
    tasa = newTasa;
  }
  public String getTasa() {
    return Util.DobleString(tasa);
  }
  public void setTotal(double newTotal) {
    total = newTotal;
  }
  public String getTotal() {
    total=capital*tasa;/**SOLO AQUI SE MULIPLICA***/
    return Util.DobleString(total);
  }
  public void setPlazo(String newPlazo) {
    plazo = newPlazo;
  }
  public String getPlazo() {
    return plazo;
  }
  public void setVcto(String newVcto) {
    vcto = newVcto;
  }
  public String getVcto() {
    return vcto;
  }
  public void setMoneda(String newMoneda) {
    moneda = newMoneda;
  }
  public String getMoneda() {
    return moneda;
  }
  public void setCaja_a(String newCaja_a) {
    caja_a = newCaja_a;
  }
  public String getCaja_a() {
    return caja_a;
  }
  public void setPeriodo(String newPeriodo) {
    periodo = newPeriodo;
  }
  public String getPeriodo() {
    return periodo;
  }

}