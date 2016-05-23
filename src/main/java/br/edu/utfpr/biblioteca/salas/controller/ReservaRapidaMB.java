package br.edu.utfpr.biblioteca.salas.controller;

import br.edu.utfpr.biblioteca.salas.view.BotaoHorario;
import br.edu.utfpr.biblioteca.salas.model.bo.SalaBO;
import br.edu.utfpr.biblioteca.salas.model.entity.EstudantePO;
import br.edu.utfpr.biblioteca.salas.model.entity.ReservaPO;
import br.edu.utfpr.biblioteca.salas.model.entity.SalaPO;
import br.edu.utfpr.biblioteca.salas.tools.CalendarioHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import java.io.Serializable;
import java.util.Map;

@Named(value = "reservaRapidaMB")
@ViewScoped
@ManagedBean
public class ReservaRapidaMB implements Serializable {

    private ReservaPO reserva;
    private String strHora;

    private List<BotaoHorario> botoesHorario;

    //Formatadores de data
    private final SimpleDateFormat formatoEmHoras;
    private final SimpleDateFormat formatoEmDia;

    public ReservaRapidaMB() {

        formatoEmHoras = new SimpleDateFormat("HH");
        formatoEmDia = new SimpleDateFormat("dd/MM/yyyy");
//        Aqui ficou com bug pq o método updateBotoesAtivosPorDia não retorna mais uma lista
//        Ver se é necessário retornar uma lista ou implematar o set do css dos botões no próprio método updateBotoesAtivosPorDia
        this.reserva = new ReservaPO(new EstudantePO(null, null, null, null), new SalaPO(0, true), new Date(), 0);
        this.botoesHorario = new ArrayList<>();
        updateBotoesAtivosPorDia(new Date());
    }

    public ReservaPO getReserva() {
        return this.reserva;
    }

    public String getStrHora() {
        return strHora;
    }

    public void setStrHora(String strHora) {
        this.strHora = strHora;
    }

    public List<BotaoHorario> getBotoesHorario() {
        return botoesHorario;
    }

    public Date getDataAtual() {
        return new Date();
    }

    /**
     * Método executado ao ser escolhida uma data no calendário
     *
     * @param event
     */
    public void onDateSelect(SelectEvent event) {
        Date dataSelecionada = (Date) event.getObject();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Data Selecionada", formatoEmDia.format(event.getObject())));
        updateBotoesAtivosPorDia(dataSelecionada);
    }

    /**
     * Responsável pelo wizard. Cada clique em next executa este método.
     *
     * @param event
     * @return
     */
    public String onFlowProcess(FlowEvent event) {
        return event.getNewStep();
    }

    public void click() {
        RequestContext requestContext = RequestContext.getCurrentInstance();

        requestContext.update("form:display");
        requestContext.execute("PF('dlg').show()");

    }

    /**
     * Este método solicita para a classe SalaBO uma lista de salas disponíveis
     * dado um dia e uma strHora. Exibe em um Select<html> as salas
     *
     * @return
     */
    public List<SalaPO> getSalasDisponiveis() {
        return SalaBO.getSalasDisponiveis(this.reserva.getDataInicial());
    }

    /**
     * Valida a entrada do usuário e faz a reserva invocando as camadas
     * inferiores.
     */
    public void reservarSala() {
        Date dataInicial = CalendarioHelper.parseDateTime(this.reserva.getStrDataInicial(), this.strHora);
        this.reserva.setDataInicial(dataInicial);
        FacesMessage msg;

        if (reserva.getDataFinal().equals(reserva.getDataInicial())) {
            msg = new FacesMessage("Data Final igual data inicial");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        if (reserva.getDataFinal().before(reserva.getDataInicial())) {
            msg = new FacesMessage("Data Final deve ser posterior à data inicial");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

        try {
            SalaBO.reservarSala(reserva);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Reservado", getReserva().getStrDataInicial());
        } catch (Exception ex) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fail", ex.getMessage());
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * Este método deve solicitar para a classe SalaBO um hash contendo as salas
     * que possuem horários disponíveis dado um dia. Ele é quem deve setar o css
     * dos botões.
     *
     * @param data
     */
    public void updateBotoesAtivosPorDia(Date data) {
        //hash com key inteiro (strHora -> 8, 9, 10...) e boolean se existe alguma sala disponível ou todas estão reservas.
        HashMap<Date, Boolean> salasDisponiveis = SalaBO.getHorariosDisponiveis(data);
        if (salasDisponiveis == null) {
            return;
        }
        for (Map.Entry<Date, Boolean> entry : salasDisponiveis.entrySet()) {
            if (entry.getValue()) {
                botoesHorario.add(new BotaoHorario(entry.getKey().getHours(), "verde", true));
            } else {
                botoesHorario.add(new BotaoHorario(entry.getKey().getHours(), "vermelho", false));
            }
        }
        //chamar método alteraEstilo ou implementar o método aqui
    }

    public void alterarEstilo() {
        String parametroUmAtivo = "";
        if (parametroUmAtivo.equals("btn btn-success")) {
            parametroUmAtivo = "ui-priority-primary";
        } else {
            parametroUmAtivo = "btn btn-success";
        }
    }

    public void cancelarReserva() {
        throw new UnsupportedOperationException();
    }

    public void ativarReserva() {
        throw new UnsupportedOperationException();
    }

    public List<ReservaPO> listarReservas(EstudantePO estudante) {
        throw new UnsupportedOperationException();
    }

}