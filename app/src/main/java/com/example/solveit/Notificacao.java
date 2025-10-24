package com.example.solveit;

// Esta classe representa uma única notificação no sistema.
// É o modelo de dados usado pelo Adapter e pela Activity.
public class Notificacao {
    private String mensagem;    // Ex: "NomeAgente abriu o chamado"
    private String dataHoraId;  // Ex: "29/04/2025 às 14h28 - Chamado ID3"

    /**
     * Construtor para criar uma nova Notificação.
     * @param mensagem O corpo principal da notificação.
     * @param dataHoraId Data, hora e identificador do chamado/evento.
     */
    public Notificacao(String mensagem, String dataHoraId) {
        this.mensagem = mensagem;
        this.dataHoraId = dataHoraId;
    }

    /**
     * Obtém a mensagem principal da notificação.
     * @return A mensagem.
     */
    public String getMensagem() {
        return mensagem;
    }

    /**
     * Obtém a data, hora e ID do chamado associado.
     * @return A string formatada.
     */
    public String getDataHoraId() {
        return dataHoraId;
    }
}
