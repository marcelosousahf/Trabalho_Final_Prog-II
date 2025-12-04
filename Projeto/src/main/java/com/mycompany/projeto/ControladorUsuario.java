package com.mycompany.projeto;

import dao.Dao;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import modelo.Usuario;

public class ControladorUsuario {
    
    @FXML private TextField txtNome;
    @FXML private TextField txtLogin;
    @FXML private TextField txtSenha;
    @FXML private Label lblAviso;
    
    public Usuario usuario;
    
    @FXML
    public void initialize() {
        Usuario usuario = new Usuario();
    }
    
    @FXML
    public void registrar_usuario(){
        if(txtNome.getText().isEmpty()||txtLogin.getText().isEmpty()||txtSenha.getText().isEmpty()){
         lblAviso.setText("Todos os campos devem ser preenchidos !!");
         lblAviso.setStyle("-fx-text-fill: #DC143C;");
         return;
        }
        else{
            lblAviso.setText("");
        }
        String login = txtLogin.getText();
        Dao<Usuario> dao = new Dao<Usuario>(Usuario.class);
        Usuario usuarioExistente = dao.buscarPorChave("login", login);
        
        if(usuarioExistente != null){
            lblAviso.setText("Ja existe alguem com esse login");
            lblAviso.setStyle("-fx-text-fill: #DC143C;");
            return;
        }
        usuario = new Usuario(txtNome.getText(), login, txtSenha.getText());
        dao.inserir(usuario);
        limparCampos();
        lblAviso.setText("Cadastro feito com sucesso !!");
        lblAviso.setStyle("-fx-text-fill: #90EE90");
    }
    public void limparCampos(){
        txtNome.setText("");
        txtLogin.setText("");
        txtSenha.setText("");
    }
    
    @FXML
    private void voltar() throws IOException{
        App.setRoot("TelaFrota");
    }
}