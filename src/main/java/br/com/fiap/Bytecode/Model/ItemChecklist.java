package br.com.fiap.Bytecode.Model;

public class ItemChecklist {

    private int idItemChecklist;
    private String nomeItemChecklist;
    private String sugestaoChecklist;

    // Construtor vazio
    public ItemChecklist() {
    }

    // Construtor com todos os campos (exceto ID)
    public ItemChecklist(String nomeItemChecklist, String sugestaoChecklist) {
        this.nomeItemChecklist = nomeItemChecklist;
        this.sugestaoChecklist = sugestaoChecklist;
    }

    // --- Getters e Setters ---

    public int getIdItemChecklist() {
        return idItemChecklist;
    }

    public void setIdItemChecklist(int idItemChecklist) {
        this.idItemChecklist = idItemChecklist;
    }

    public String getNomeItemChecklist() {
        return nomeItemChecklist;
    }

    public void setNomeItemChecklist(String nomeItemChecklist) {
        this.nomeItemChecklist = nomeItemChecklist;
    }

    public String getSugestaoChecklist() {
        return sugestaoChecklist;
    }

    public void setSugestaoChecklist(String sugestaoChecklist) {
        this.sugestaoChecklist = sugestaoChecklist;
    }

    @Override
    public String toString() {
        return "ItemChecklist{" +
                "idItemChecklist=" + idItemChecklist +
                ", nomeItemChecklist='" + nomeItemChecklist + '\'' +
                '}';
    }
}