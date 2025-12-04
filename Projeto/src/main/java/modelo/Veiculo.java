package modelo;

public class Veiculo {
    private String placa;
    private String Marca;
    private String Modelo;

    public Veiculo(){
    placa = "";
    Marca = "";
    Modelo = "";
    }
    
    public Veiculo(String placa, String Marca, String Modelo) {
        this.placa = placa;
        this.Marca = Marca;
        this.Modelo = Modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public String getMarca() {
        return Marca;
    }

    public String getModelo() {
        return Modelo;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public void setMarca(String Marca) {
        this.Marca = Marca;
    }

    public void setModelo(String Modelo) {
        this.Modelo = Modelo;
    }
    
}