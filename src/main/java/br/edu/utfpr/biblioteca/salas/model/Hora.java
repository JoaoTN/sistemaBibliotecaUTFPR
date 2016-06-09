/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.biblioteca.salas.model;

import br.edu.utfpr.biblioteca.salas.model.entity.ReservaPO;
import br.edu.utfpr.biblioteca.salas.tools.CalendarioHelper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Mapeia as tuplas hora das folhas da biblioteca. Cada tupla possui uma lista
 * de reservas, de tamanho máximo uma para cada sala.
 *
 * @author romulo
 */
public class Hora implements Comparable<Hora> {
    
    private Date hora;
    private List<ReservaPO> reservas;
    
    public Hora() {
        this.reservas = new ArrayList<>();
    }
    
    public String getId(int index) {
        if (reservas.get(index).getDataInicial() == null) {
            return "-1";
        } else {
            return String.valueOf(reservas.get(index).getId());
        }
    }
    
    public String getStatusReserva(int index) {
        if (reservas.get(index).getDataInicial() == null) {
            return "Livre";
        } else {
            return "Ocupado";
        }
    }
    
    public String exibeDetalhes(int index) {
        if (getId(index).equals("-1")) {
            return "false";
        }
        return "true";
    }
    
    public String getHora() {
        return CalendarioHelper.getHora(hora);
    }
    
    public void setHora(Date hora) {
        this.hora = hora;
    }
    
    public List<ReservaPO> getReservas() {
        return reservas;
    }
    
    public void setReservas(List<ReservaPO> reservas) {
        this.reservas = reservas;
    }
    
    public void addReserva(ReservaPO reserva) {
        if (reserva != null) {
            reservas.add(reserva);
        } else {
            reservas.add(new ReservaPO(null, null, null, 0));
        }
    }
    
    @Override
    public Object clone() {
        Hora hora = new Hora();
        hora.setHora(this.hora);
        hora.reservas.addAll(this.reservas);
        return hora;
    }
    
    @Override
    public int compareTo(Hora o) {
        return this.hora.compareTo(o.hora);
    }
    
}
