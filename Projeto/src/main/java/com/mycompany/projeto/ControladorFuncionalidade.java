package com.mycompany.projeto;

import java.io.IOException;
import javafx.fxml.FXML;

public class ControladorFuncionalidade {
    
    @FXML
    private void registrarMotorista() throws IOException {
        App.setRoot("TelaMotorista");
    }
    
    @FXML
    private void registrarVeiculo() throws IOException {
        App.setRoot("TelaVeiculo");
    }
    
    @FXML
    private void registrarRetirada() throws IOException {
        App.setRoot("TelaRetirada");
    }
    
    @FXML
    private void registrarDevolucao() throws IOException {
        App.setRoot("TelaDevolucao");
    }
    
    @FXML
    private void consultarUtilizacoes() throws IOException {
        App.setRoot("TelaConsulta");
    }
    
    @FXML
    private void sair() throws IOException {
        App.setRoot("TelaFrota");
    }
}