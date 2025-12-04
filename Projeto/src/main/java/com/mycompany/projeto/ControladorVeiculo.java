package com.mycompany.projeto;

import dao.Dao;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import modelo.Veiculo;

public class ControladorVeiculo {
    
    @FXML private TextField txtPlaca;
    @FXML private TextField txtMarca;
    @FXML private TextField txtModelo;
    @FXML private Label lblAviso;
    
    public Veiculo veiculo;
    
    @FXML
    public void initialize() {
        veiculo = new Veiculo();
    }
    
    @FXML
    public void registrar_veiculo(){
        if(txtPlaca.getText().trim().isEmpty() || 
           txtMarca.getText().trim().isEmpty() || 
           txtModelo.getText().trim().isEmpty()){
            
            lblAviso.setText("Todos os campos devem ser preenchidos !!");
            lblAviso.setStyle("-fx-text-fill: #DC143C;");
            return;
        }
        
        lblAviso.setText("");
        
        String placa = txtPlaca.getText().trim().toUpperCase();
        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        
        Dao<Veiculo> dao = new Dao<Veiculo>(Veiculo.class);
        
        Veiculo veiculoExistente = dao.buscarPorChave("placa", placa);
        
        if(veiculoExistente != null){
            lblAviso.setText("Já existe um veículo com essa placa!");
            lblAviso.setStyle("-fx-text-fill: #DC143C;");
            return;
        }
        
        veiculo = new Veiculo(placa, marca, modelo);
        dao.inserir(veiculo);
        
        limparCampos();
        lblAviso.setText("Veículo cadastrado com sucesso !!");
        lblAviso.setStyle("-fx-text-fill: #90EE90");
    }
    
    public void limparCampos(){
        txtPlaca.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
    }
    
    public void voltar() throws IOException {
        App.setRoot("TelaFuncionalidade");
    }
}