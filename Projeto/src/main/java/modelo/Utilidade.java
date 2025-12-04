package modelo;

import java.time.LocalDateTime;
import org.bson.types.ObjectId;

public class Utilidade {
    private ObjectId id; 
    private String placaVeiculo;
    private String codigoMotorista;
    private LocalDateTime dataHoraRetirada;
    private LocalDateTime dataHoraDevolucao;
    
    public Utilidade() {
    }
   
    public ObjectId getId() {
        return id;
    }
    
    public void setId(ObjectId id) {
        this.id = id;
    }
    
    public String getIdString() {
        return id != null ? id.toString() : null;
    }
    
    public String getPlacaVeiculo() {
        return placaVeiculo;
    }
    
    public void setPlacaVeiculo(String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }
    
    public String getCodigoMotorista() {
        return codigoMotorista;
    }
    
    public void setCodigoMotorista(String codigoMotorista) {
        this.codigoMotorista = codigoMotorista;
    }
    
    public LocalDateTime getDataHoraRetirada() {
        return dataHoraRetirada;
    }
    
    public void setDataHoraRetirada(LocalDateTime dataHoraRetirada) {
        this.dataHoraRetirada = dataHoraRetirada;
    }
    
    public LocalDateTime getDataHoraDevolucao() {
        return dataHoraDevolucao;
    }
    
    public void setDataHoraDevolucao(LocalDateTime dataHoraDevolucao) {
        this.dataHoraDevolucao = dataHoraDevolucao;
    }
    
    @Override
    public String toString() {
        return "Utilidade{" +
                "id=" + (id != null ? id.toString() : "null") +
                ", placaVeiculo='" + placaVeiculo + '\'' +
                ", codigoMotorista='" + codigoMotorista + '\'' +
                ", dataHoraRetirada=" + dataHoraRetirada +
                ", dataHoraDevolucao=" + dataHoraDevolucao +
                '}';
    }
}