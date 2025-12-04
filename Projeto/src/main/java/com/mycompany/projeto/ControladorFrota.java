package com.mycompany.projeto;

import java.io.IOException;
import javafx.fxml.FXML;

public class ControladorFrota {
    
    @FXML
    private void registrarUsuario() throws IOException{
        App.setRoot("TelaRegistrarUsuario");
    }
    
    @FXML
    private void login() throws IOException{
        App.setRoot("TelaLogin");
    }
}