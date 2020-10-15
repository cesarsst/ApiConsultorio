package com.example.apiconsultorio.model;

import javax.persistence.Entity;

@Entity
public class Relatorio extends AbstractEntity{

    private int consultasConcluidas;
    private int consultasMarcadas;
    private int pagamentosPendentes;
    private String remumo;
    private int proxConsulta;

    public int getConsultasConcluidas() {
        return consultasConcluidas;
    }

    public void setConsultasConcluidas(int consultasConcluidas) {
        this.consultasConcluidas = consultasConcluidas;
    }

    public int getConsultasMarcadas() {
        return consultasMarcadas;
    }

    public void setConsultasMarcadas(int consultasMarcadas) {
        this.consultasMarcadas = consultasMarcadas;
    }

    public int getPagamentosPendentes() {
        return pagamentosPendentes;
    }

    public void setPagamentosPendentes(int pagamentosPendentes) {
        this.pagamentosPendentes = pagamentosPendentes;
    }

    public String getRemumo() {
        return remumo;
    }

    public void setRemumo(String remumo) {
        this.remumo = remumo;
    }

    public int getProxConsulta() {
        return proxConsulta;
    }

    public void setProxConsulta(int proxConsulta) {
        this.proxConsulta = proxConsulta;
    }
}
