package com.mycompany.projeto;

import dao.Dao;
import javafx.scene.paint.Color;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import modelo.Usuario;

public class ControladorLogin {
    
    @FXML
    private TextField txtLogin;  
    
    @FXML
    private TextField txtSenha; 
    
    @FXML 
    private Label lblAviso;   
    
    @FXML
    private void initialize() {
        lblAviso.setText("");
    }
    
    @FXML
    private void entrar() throws IOException {
        String login = txtLogin.getText();
        String senha = txtSenha.getText();
        
        if (!validar()) {
            mostrarMensagem("Preencha login e senha!", Color.RED);
            return;
        }
        
        Dao<Usuario> dao = new Dao<>(Usuario.class);
        Usuario usuario = dao.buscarPorChave("login", login);
        
        if (usuario == null) {
            mostrarMensagem("Usuário não encontrado!", Color.RED);
            return;
        }
        
        if (usuario.getSenha().equals(senha)) {
            App.setRoot("TelaFuncionalidade");
        } else {
            mostrarMensagem("Senha incorreta!", Color.RED);
        }
    }
    
    public boolean validar() {
        return !txtLogin.getText().isEmpty() && 
               !txtSenha.getText().isEmpty();
    }

    @FXML
    public void voltar() throws IOException {
        App.setRoot("TelaFrota");
    }
    
    public void mostrarMensagem(String mensagem, Color cor) {
        lblAviso.setText(mensagem);
        lblAviso.setTextFill(cor);
    }
}