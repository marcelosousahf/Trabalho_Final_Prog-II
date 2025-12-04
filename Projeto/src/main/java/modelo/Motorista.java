package modelo;

public class Motorista {
    private String codigo;
    private String nome;
    private String CNH;
    private String setor;

    public Motorista(String codigo, String nome, String CNH, String setor) {
        this.codigo = codigo;
        this.nome = nome;
        this.CNH = CNH;
        this.setor = setor;
    }

    public Motorista() {
        codigo = "";
        nome = "";
        CNH = "";
        setor = "";
    }
    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public String getCNH() {
        return CNH;
    }

    public String getSetor() {
        return setor;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCNH(String CNH) {
        this.CNH = CNH;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }
    
}