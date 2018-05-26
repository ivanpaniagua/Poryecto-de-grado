package BUSINESS;

/**
 * Title:        Business Services
 * Description:  Clases encargadas de la aplicacion o logica de negocios
 * Copyright:    Copyright (c) 2003
 * Company:      Entidad Financiera
 * @author Ivan Paniagua
 * @version 1.0
 */

public class Credito {

  public Credito() {
  }
  private String cred;
  private String plazo;
  private String tiposerv;
  private String tipocuota;
  private String tipoamort;
  private String f_1cuota;
  private String diasamort;
  private String ctacble;
  private String f_desemb;
  private String f_ultmov;
  private double monto;
  private double tasa;
  private double saldo;
  private String caja_a;

  public String getCred() {
    return cred;
  }
  public void setCred(String newCred) {
    cred = newCred;
  }
  public void setMonto(double newMonto) {
    monto = newMonto;
  }
  public String getMonto() {
    return Util.DobleString(monto);
  }
  public void setTasa(double newTasa) {
    tasa = newTasa;
  }
  public String getTasa() {
    return Util.DobleString(tasa);
  }
  public void setPlazo(String newPlazo) {
    plazo = newPlazo;
  }
  public String getPlazo() {
    return plazo;
  }
  public void setTiposerv(String newTiposerv) {
    tiposerv = newTiposerv;
  }
  public String getTiposerv() {
    return tiposerv;
  }
  public void setTipocuota(String newTipocuota) {
    tipocuota = newTipocuota;
  }
  public String getTipocuota() {
    return tipocuota;
  }
  public void setTipoamort(String newTipoamort) {
    tipoamort = newTipoamort;
  }
  public String getTipoamort() {
    return tipoamort;
  }
  public void setF_1cuota(String newF_1cuota) {
    f_1cuota = newF_1cuota;
  }
  public String getF_1cuota() {
    return f_1cuota;
  }
  public void setDiasamort(String newDiasamort) {
    diasamort = newDiasamort;
  }
  public String getDiasamort() {
    return diasamort;
  }
  public void setCtacble(String newCtacble) {
    ctacble = newCtacble;
  }
  public String getCtacble() {
    return ctacble;
  }
  public void setF_desemb(String newF_desemb) {
    f_desemb = newF_desemb;
  }
  public String getF_desemb() {
    return f_desemb;
  }
  public void setSaldo(double newSaldo) {
    saldo = newSaldo;
  }
  public String getSaldo() {
    return Util.DobleString(saldo);
  }
  public void setF_ultmov(String newF_ultmov) {
    f_ultmov = newF_ultmov;
  }
  public String getF_ultmov() {
    return f_ultmov;
  }
  public void setCaja_a(String newCaja_a) {
    caja_a = newCaja_a;
  }
  public String getCaja_a() {
    return caja_a;
  }
}