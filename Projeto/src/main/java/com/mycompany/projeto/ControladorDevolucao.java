package com.mycompany.projeto;

import dao.Dao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import modelo.Utilidade;
import modelo.Veiculo;
import modelo.Motorista;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ControladorDevolucao {
    
    @FXML private ComboBox<String> comboRetiradas;
    @FXML private DatePicker dateDataDevolucao;
    @FXML private Label lblDetalhes;
    @FXML private Label lblAviso;
    
    private List<Utilidade> retiradasAtivas;
    
    @FXML
    public void initialize() {
        try {
            dateDataDevolucao.setValue(LocalDate.now());
            
            carregarRetiradasAtivas();
            
            comboRetiradas.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && !newValue.isEmpty()) {
                    exibirDetalhesRetirada(newValue);
                }
            });
            
            lblAviso.setText("Sistema carregado. Selecione uma retirada para devolução.");
            lblAviso.setStyle("-fx-text-fill: #000000;");
        } catch (Exception e) {
            mostrarErro("Erro ao inicializar tela: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void carregarRetiradasAtivas() {
        try {
            System.out.println("Carregando retiradas ativas...");
            
            Dao<Utilidade> daoUtilidade = new Dao<>(Utilidade.class);
            
            retiradasAtivas = daoUtilidade.buscarPorAtributo("dataHoraDevolucao", null);
            
            System.out.println("Retiradas ativas encontradas (buscarPorAtributo): " + retiradasAtivas.size());
            
            if (retiradasAtivas.isEmpty()) {
                System.out.println("Tentando método alternativo para buscar retiradas...");
                retiradasAtivas = daoUtilidade.listarTodos();
                retiradasAtivas.removeIf(utilidade -> 
                    utilidade.getDataHoraDevolucao() != null || 
                    utilidade.getPlacaVeiculo() == null || 
                    utilidade.getCodigoMotorista() == null
                );
                System.out.println("Retiradas ativas após filtro: " + retiradasAtivas.size());
            }
            
            ObservableList<String> retiradasLista = FXCollections.observableArrayList();
            
            for (Utilidade utilidade : retiradasAtivas) {
                if (utilidade.getDataHoraRetirada() != null && 
                    utilidade.getPlacaVeiculo() != null && 
                    utilidade.getCodigoMotorista() != null) {
                    
                    String dataRetirada = utilidade.getDataHoraRetirada().toLocalDate().toString();
                    String item = utilidade.getPlacaVeiculo() + " - Motorista: " + 
                                 utilidade.getCodigoMotorista() + " - Retirado: " + dataRetirada;
                    retiradasLista.add(item);
                    System.out.println("Adicionando retirada: " + item);
                } else {
                    System.out.println("Retirada com dados incompletos ignorada: " + utilidade);
                }
            }
            
            comboRetiradas.setItems(retiradasLista);
            
            if (retiradasLista.isEmpty()) {
                lblAviso.setText("Não há retiradas ativas para devolução.");
                lblAviso.setStyle("-fx-text-fill: #FFA500"); // Laranja
            } else {
                lblAviso.setText(retiradasLista.size() + " retirada(s) ativa(s) encontrada(s). Selecione uma para devolver.");
                lblAviso.setStyle("-fx-text-fill: #000000"); // Preto
            }
        } catch (Exception e) {
            mostrarErro("Erro ao carregar retiradas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void exibirDetalhesRetirada(String retiradaSelecionada) {
        try {
            System.out.println("Exibindo detalhes para: " + retiradaSelecionada);
            
            // Encontra a retirada correspondente na lista
            for (Utilidade utilidade : retiradasAtivas) {
                String item = utilidade.getPlacaVeiculo() + " - Motorista: " + 
                             utilidade.getCodigoMotorista() + " - Retirado: " + 
                             utilidade.getDataHoraRetirada().toLocalDate().toString();
                
                if (item.equals(retiradaSelecionada)) {
                    Dao<Veiculo> daoVeiculo = new Dao<>(Veiculo.class);
                    Veiculo veiculo = daoVeiculo.buscarPorChave("placa", utilidade.getPlacaVeiculo());
                    
                    Dao<Motorista> daoMotorista = new Dao<>(Motorista.class);
                    Motorista motorista = daoMotorista.buscarPorChave("codigo", utilidade.getCodigoMotorista());
                    
                    StringBuilder detalhes = new StringBuilder();
                    detalhes.append("Detalhes da Retirada:\n");
                    detalhes.append("Veículo: ").append(utilidade.getPlacaVeiculo());
                    if (veiculo != null) {
                        detalhes.append(" (").append(veiculo.getModelo()).append(")");
                    }
                    detalhes.append("\nMotorista: ");
                    if (motorista != null) {
                        detalhes.append(motorista.getNome()).append(" (Código: ").append(motorista.getCodigo()).append(")");
                    } else {
                        detalhes.append(utilidade.getCodigoMotorista());
                    }
                    detalhes.append("\nData/Hora Retirada: ").append(utilidade.getDataHoraRetirada());
                    detalhes.append("\n\nSelecione a data de devolução abaixo:");
                    
                    lblDetalhes.setText(detalhes.toString());
                    lblDetalhes.setStyle("-fx-text-fill: #000000");
                    System.out.println("Detalhes exibidos com sucesso");
                    break;
                }
            }
        } catch (Exception e) {
            mostrarErro("Erro ao exibir detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    public void gravar() {
        try {
            lblAviso.setText("");
            
            if (comboRetiradas.getValue() == null || comboRetiradas.getValue().isEmpty()) {
                mostrarAlerta("Selecione uma retirada para devolver!");
                return;
            }
            
            if (dateDataDevolucao.getValue() == null) {
                mostrarAlerta("Selecione a data de devolução!");
                return;
            }
            
            LocalDate dataDevolucao = dateDataDevolucao.getValue();

            String retiradaSelecionada = comboRetiradas.getValue();
            System.out.println("Processando devolução para: " + retiradaSelecionada);
            
            Utilidade retiradaParaDevolver = null;
            
            for (Utilidade utilidade : retiradasAtivas) {
                String item = utilidade.getPlacaVeiculo() + " - Motorista: " + 
                             utilidade.getCodigoMotorista() + " - Retirado: " + 
                             utilidade.getDataHoraRetirada().toLocalDate().toString();
                
                if (item.equals(retiradaSelecionada)) {
                    retiradaParaDevolver = utilidade;
                    break;
                }
            }
            
            if (retiradaParaDevolver == null) {
                mostrarAlerta("Erro: Retirada não encontrada!");
                return;
            }
            
            LocalDate dataRetirada = retiradaParaDevolver.getDataHoraRetirada().toLocalDate();
            if (dataDevolucao.isBefore(dataRetirada)) {
                mostrarAlerta("Data de devolução não pode ser anterior à data de retirada!");
                return;
            }
            
            LocalDateTime dataHoraDevolucao = dataDevolucao.atTime(LocalTime.now());
            retiradaParaDevolver.setDataHoraDevolucao(dataHoraDevolucao);
            
            System.out.println("Atualizando retirada com data de devolução: " + dataHoraDevolucao);
            System.out.println("Retirada para atualizar: " + retiradaParaDevolver);
            
            try {
                Dao<Utilidade> dao = new Dao<>(Utilidade.class);
                
                List<Utilidade> retiradasNoBanco = dao.buscarPorAtributo("placaVeiculo", retiradaParaDevolver.getPlacaVeiculo());
                
                for (Utilidade retiradaBanco : retiradasNoBanco) {
                    if (retiradaBanco.getCodigoMotorista().equals(retiradaParaDevolver.getCodigoMotorista()) &&
                        retiradaBanco.getDataHoraDevolucao() == null) {
                        
                        retiradaParaDevolver.setId(retiradaBanco.getId());
                        dao.alterar("_id", retiradaBanco.getId().toString(), retiradaParaDevolver);
                        
                        mostrarSucesso("✓ Devolução registrada com sucesso!\n" +
                                     "Veículo: " + retiradaParaDevolver.getPlacaVeiculo() + 
                                     " disponível para novas retiradas.");
                        
                        limparCampos();
                        
                        carregarRetiradasAtivas();
                        return;
                    }
                }
                
                mostrarAlerta("Não foi possível encontrar a retirada exata no banco de dados!");
                
            } catch (Exception e) {
                System.err.println("Erro ao usar alterar: " + e.getMessage());
                registrarDevolucaoAlternativa(retiradaParaDevolver);
            }
            
        } catch (Exception e) {
            mostrarErro("Erro ao registrar devolução: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void registrarDevolucaoAlternativa(Utilidade retiradaParaDevolver) {
        try {
            System.out.println("Usando método alternativo para registrar devolução...");
            
            Dao<Utilidade> dao = new Dao<>(Utilidade.class);
            dao.inserir(retiradaParaDevolver);
            
            mostrarSucesso("✓ Devolução registrada com sucesso (método alternativo)!\n" +
                         "Veículo: " + retiradaParaDevolver.getPlacaVeiculo() + 
                         " disponível para novas retiradas.");
            
            limparCampos();
            carregarRetiradasAtivas();
            
        } catch (Exception e) {
            mostrarErro("Erro no método alternativo: " + e.getMessage());
        }
    }
    
    private void mostrarAlerta(String mensagem) {
        lblAviso.setText(mensagem);
        lblAviso.setStyle("-fx-text-fill: #DC143C;");
        
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atenção");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    private void mostrarErro(String mensagem) {
        lblAviso.setText("ERRO: " + mensagem);
        lblAviso.setStyle("-fx-text-fill: #DC143C;");
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText("Ocorreu um erro");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    private void mostrarSucesso(String mensagem) {
        lblAviso.setText(mensagem);
        lblAviso.setStyle("-fx-text-fill: #90EE90;");
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    @FXML
    public void cancelar() throws IOException {
        App.setRoot("TelaFuncionalidade");
    }
    
    @FXML
    public void limparCampos() {
        comboRetiradas.setValue(null);
        dateDataDevolucao.setValue(LocalDate.now());
        lblDetalhes.setText("");
    }
    
    @FXML
    public void atualizarLista() {
        carregarRetiradasAtivas();
        lblAviso.setText("Lista de retiradas atualizada!");
        lblAviso.setStyle("-fx-text-fill: #90EE90");
    }
}