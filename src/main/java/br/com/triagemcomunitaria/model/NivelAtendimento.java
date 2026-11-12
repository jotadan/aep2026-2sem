package br.com.triagemcomunitaria.model;

public enum NivelAtendimento {
    ORIENTACAO_DOMICILIAR("Fique em casa em repouso e beba bastante líquido. Observe os sintomas e procure "
            + "uma Unidade Básica de Saúde se eles piorarem ou continuarem por vários dias."),
    UBS("Procure a Unidade Básica de Saúde nos próximos dias para ser avaliado. "
            + "Se os sintomas piorarem, procure uma UPA."),
    UPA("Procure uma Unidade de Pronto Atendimento (UPA) o quanto antes para ser avaliado."),
    EMERGENCIA("Procure atendimento de emergência imediatamente ou ligue 192 (SAMU).");

    private final String orientacao;

    NivelAtendimento(String orientacao) {
        this.orientacao = orientacao;
    }

    public String getOrientacao() {
        return orientacao;
    }
}
