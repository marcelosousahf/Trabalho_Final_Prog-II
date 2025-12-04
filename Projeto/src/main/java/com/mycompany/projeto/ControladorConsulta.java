package com.mycompany.projeto;

import dao.Dao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import modelo.Utilidade;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class ControladorConsulta {
    
    @FXML private ListView<String> listaRetiradas;
    @FXML private Label lblAviso;
    @FXML private Label lblContador;
    
    private ObservableList<String> retiradasList;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    @FXML
    public void initialize() {
        carregarRetiradas();
    }
    
    private void carregarRetiradas() {
        Dao<Utilidade> dao = new Dao<>(Utilidade.class);
        retiradasList = FXCollections.observableArrayList();
        
        List<Utilidade> todosRegistros = dao.listarTodos();
        
        todosRegistros.sort((r1, r2) -> {
            boolean r1EmUso = r1.getDataHoraDevolucao() == null;
            boolean r2EmUso = r2.getDataHoraDevolucao() == null;
            
            if (r1EmUso && !r2EmUso) return -1;
            if (!r1EmUso && r2EmUso) return 1;
            
            return r2.getDataHoraRetirada().compareTo(r1.getDataHoraRetirada());
        });
        
        for (Utilidade registro : todosRegistros) {
            String status = registro.getDataHoraDevolucao() != null ? 
                " Devolvido" : " Em uso";
            
            String dataDevolucao = "";
            if (registro.getDataHoraDevolucao() != null) {
                dataDevolucao = " | Devolução: " + registro.getDataHoraDevolucao().format(formatter);
            }
            
            String texto = String.format(
                "%s | %s | Retirada: %s%s | %s",
                registro.getPlacaVeiculo(),
                registro.getCodigoMotorista(),
                registro.getDataHoraRetirada().format(formatter),
                dataDevolucao,
                status
            );
            retiradasList.add(texto);
        }
        
        listaRetiradas.setItems(retiradasList);
        
        int total = todosRegistros.size();
        int emUso = (int) todosRegistros.stream()
            .filter(r -> r.getDataHoraDevolucao() == null)
            .count();
        int devolvidos = total - emUso;
        
        lblContador.setText(String.format("Total: %d | Em uso: %d | Devolvidos: %d", 
            total, emUso, devolvidos));
        
        if (total == 0) {
            lblAviso.setText("Nenhuma retirada registrada.");
            lblAviso.setStyle("-fx-text-fill: #DC143C;");
        } else {
            lblAviso.setText("");
        }
    }
    
    @FXML
    public void atualizar() {
        carregarRetiradas();
        lblAviso.setText("Lista atualizada!");
        lblAviso.setStyle("-fx-text-fill: #90EE90;");
    }
    
    @FXML
    public void voltar() throws IOException {
        App.setRoot("TelaFuncionalidade");
    }
    
    @FXML
    public void limpar() {
        listaRetiradas.getItems().clear();
        lblContador.setText("");
        lblAviso.setText("Lista limpa.");
        lblAviso.setStyle("-fx-text-fill: #DC143C;");
    }
}