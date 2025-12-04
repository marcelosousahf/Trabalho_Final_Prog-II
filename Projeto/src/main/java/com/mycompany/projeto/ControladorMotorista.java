package com.mycompany.projeto;

import dao.Dao;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import modelo.Motorista;

public class ControladorMotorista {
    
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNome;
    @FXML private TextField txtCNH;
    @FXML private TextField txtSetor;
    @FXML private Label lblAviso;
    
    public Motorista motorista;
    
    @FXML
    public void initialize() {
        motorista = new Motorista();
    }
    
    @FXML
    public void registrar_motorista(){
        if(txtCodigo.getText().trim().isEmpty() || 
           txtNome.getText().trim().isEmpty() || 
           txtCNH.getText().trim().isEmpty() || 
           txtSetor.getText().trim().isEmpty()){
            
            lblAviso.setText("Todos os campos devem ser preenchidos !!");
            lblAviso.setStyle("-fx-text-fill: #DC143C;");
            return;
        }
        
        lblAviso.setText("");
        
        String codigo = txtCodigo.getText().trim();
        String nome = txtNome.getText().trim();
        String cnh = txtCNH.getText().trim();
        String setor = txtSetor.getText().trim();
        
        Dao<Motorista> dao = new Dao<Motorista>(Motorista.class);
        
        Motorista motoristaPorCodigo = dao.buscarPorChave("codigo", codigo);
        
        if(motoristaPorCodigo != null){
            lblAviso.setText("Já existe um motorista com esse código!");
            lblAviso.setStyle("-fx-text-fill: #DC143C;");
            return;
        }
        
        Motorista motoristaPorCNH = dao.buscarPorChave("CNH", cnh);
        
        if(motoristaPorCNH != null){
            lblAviso.setText("Já existe um motorista com essa CNH!");
            lblAviso.setStyle("-fx-text-fill: #DC143C;");
            return;
        }

        motorista = new Motorista(codigo, nome, cnh, setor);
        dao.inserir(motorista);
        
        limparCampos();
        lblAviso.setText("Motorista cadastrado com sucesso !!");
        lblAviso.setStyle("-fx-text-fill: #90EE90");
    }
    
    public void limparCampos(){
        txtCodigo.setText("");
        txtNome.setText("");
        txtCNH.setText("");
        txtSetor.setText("");
    }
    @FXML
    
    public void voltar() throws IOException {
        App.setRoot("TelaFuncionalidade");
    }
}