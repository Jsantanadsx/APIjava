package br.com.fiap.Bytecode.Model;

public class ChecklistPaciente {

    private int idConsulta;       // Chave estrangeira e parte da PK
    private int idItemChecklist;  // Chave estrangeira e parte da PK
    private String statusItem;

    // Construtor vazio
    public ChecklistPaciente() {
    }

    // Construtor com todos os campos
    public ChecklistPaciente(int idConsulta, int idItemChecklist, String statusItem) {
        this.idConsulta = idConsulta;
        this.idItemChecklist = idItemChecklist;
        this.statusItem = statusItem;
    }

    // --- Getters e Setters ---

    public int getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(int idConsulta) {
        this.idConsulta = idConsulta;
    }

    public int getIdItemChecklist() {
        return idItemChecklist;
    }

    public void setIdItemChecklist(int idItemChecklist) {
        this.idItemChecklist = idItemChecklist;
    }

    public String getStatusItem() {
        return statusItem;
    }

    public void setStatusItem(String statusItem) {
        this.statusItem = statusItem;
    }

    @Override
    public String toString() {
        return "ChecklistPaciente{" +
                "idConsulta=" + idConsulta +
                ", idItemChecklist=" + idItemChecklist +
                ", statusItem='" + statusItem + '\'' +
                '}';
    }
}