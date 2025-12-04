package com.mycompany.projeto;

import dao.Dao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import modelo.Motorista;
import modelo.Utilidade;
import modelo.Veiculo;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;

public class ControladorRetirada {
    
    @FXML private ComboBox<String> comboVeiculo;
    @FXML private ComboBox<String> comboMotorista;
    @FXML private DatePicker dateData;
    @FXML private Label lblAviso;
    
    @FXML
    public void initialize() {
        try {
            dateData.setValue(LocalDate.now());
            carregarVeiculosDisponiveis();
            carregarMotoristas();
            lblAviso.setText("Sistema carregado. Selecione veículo e motorista.");
            lblAviso.setStyle("-fx-text-fill: #000000;");
        } catch (Exception e) {
            mostrarErro("Erro ao inicializar tela: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void carregarVeiculosDisponiveis() {
        try {
            System.out.println("Carregando veículos disponíveis...");
            
            Dao<Veiculo> daoVeiculo = new Dao<>(Veiculo.class);
            ObservableList<String> veiculosDisponiveis = FXCollections.observableArrayList();

            List<Veiculo> veiculosDisponiveisList = buscarVeiculosDisponiveisManualmente();
            
            System.out.println("Veículos disponíveis encontrados: " + veiculosDisponiveisList.size());

            for (Veiculo v : veiculosDisponiveisList) {
                if (v.getPlaca() != null && v.getModelo() != null) {
                    veiculosDisponiveis.add(v.getPlaca() + " - " + v.getModelo());
                    System.out.println("Veículo disponível: " + v.getPlaca() + " - " + v.getModelo());
                }
            }
            
            comboVeiculo.setItems(veiculosDisponiveis);
            
            if (veiculosDisponiveis.isEmpty()) {
                lblAviso.setText("Nenhum veículo disponível no momento!");
                lblAviso.setStyle("-fx-text-fill: #DC143C;");
            } else {
                lblAviso.setText("Veículos disponíveis: " + veiculosDisponiveis.size());
                lblAviso.setStyle("-fx-text-fill: #90EE90;");
            }
        } catch (Exception e) {
            System.err.println("Erro específico ao carregar veículos: " + e.getMessage());
            e.printStackTrace();
            carregarTodosVeiculosComoFallback();
        }
    }
    
    private List<Veiculo> buscarVeiculosDisponiveisManualmente() {
        List<Veiculo> veiculosDisponiveis = new ArrayList<>();
        
        try {
            // Buscar todos os veículos
            Dao<Veiculo> daoVeiculo = new Dao<>(Veiculo.class);
            List<Veiculo> todosVeiculos = daoVeiculo.listarTodos();
            
            // Buscar retiradas ativas (sem data de devolução)
            Dao<Utilidade> daoUtilidade = new Dao<>(Utilidade.class);
            List<Utilidade> retiradasAtivas = daoUtilidade.buscarPorAtributo("dataHoraDevolucao", null);
            
            System.out.println("Total de veículos: " + todosVeiculos.size());
            System.out.println("Retiradas ativas: " + retiradasAtivas.size());
            
            // Coletar placas dos veículos em uso
            List<String> placasEmUso = new ArrayList<>();
            for (Utilidade utilidade : retiradasAtivas) {
                if (utilidade.getPlacaVeiculo() != null) {
                    placasEmUso.add(utilidade.getPlacaVeiculo().trim());
                    System.out.println("Veículo em uso: " + utilidade.getPlacaVeiculo());
                }
            }
            
            // 4. Filtrar veículos disponíveis
            for (Veiculo veiculo : todosVeiculos) {
                if (veiculo.getPlaca() != null) {
                    String placa = veiculo.getPlaca().trim();
                    if (!placasEmUso.contains(placa)) {
                        veiculosDisponiveis.add(veiculo);
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao buscar veículos disponíveis manualmente: " + e.getMessage());
            e.printStackTrace();
        }
        
        return veiculosDisponiveis;
    }
    
    private void carregarTodosVeiculosComoFallback() {
        try {
            System.out.println("Usando fallback: carregando todos os veículos...");
            Dao<Veiculo> daoVeiculo = new Dao<>(Veiculo.class);
            ObservableList<String> todosVeiculos = FXCollections.observableArrayList();

            List<Veiculo> listaVeiculos = daoVeiculo.listarTodos();
            
            for (Veiculo v : listaVeiculos) {
                if (v.getPlaca() != null && v.getModelo() != null) {
                    todosVeiculos.add(v.getPlaca() + " - " + v.getModelo());
                }
            }
            
            comboVeiculo.setItems(todosVeiculos);
            lblAviso.setText("AVISO: Carregados todos os veículos (" + todosVeiculos.size() + "). Verifique disponibilidade manualmente.");
            lblAviso.setStyle("-fx-text-fill: #FFA500;");
        } catch (Exception e) {
            mostrarErro("Erro crítico ao carregar veículos: " + e.getMessage());
        }
    }
    
    private void carregarMotoristas() {
        try {
            Dao<Motorista> dao = new Dao<>(Motorista.class);
            ObservableList<String> motoristas = FXCollections.observableArrayList();
            
            List<Motorista> listaMotoristas = dao.listarTodos();
            
            System.out.println("Motoristas encontrados: " + listaMotoristas.size());

            for (Motorista m : listaMotoristas) {
                if (m.getCodigo() != null && m.getNome() != null) {
                    motoristas.add(m.getCodigo() + " - " + m.getNome());
                }
            }
            
            comboMotorista.setItems(motoristas);
        } catch (Exception e) {
            mostrarErro("Erro ao carregar motoristas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    public void gravar() {
        try {
            lblAviso.setText("");
            
            if (comboVeiculo.getValue() == null || comboVeiculo.getValue().isEmpty()) {
                mostrarAlerta("Selecione um veículo!");
                return;
            }
            
            if (comboMotorista.getValue() == null || comboMotorista.getValue().isEmpty()) {
                mostrarAlerta("Selecione um motorista!");
                return;
            }
            
            if (dateData.getValue() == null) {
                mostrarAlerta("Selecione uma data!");
                return;
            }
            
            String veiculoSelecionado = comboVeiculo.getValue();
            String placa = veiculoSelecionado.split(" - ")[0];
            
            String motoristaSelecionado = comboMotorista.getValue();
            String codigoMotorista = motoristaSelecionado.split(" - ")[0];
            
            LocalDate dataSelecionada = dateData.getValue();
            LocalDateTime dataHoraRetirada = dataSelecionada.atTime(LocalTime.now());
            
            System.out.println("Tentando registrar retirada:");
            System.out.println("Placa: " + placa);
            System.out.println("Motorista: " + codigoMotorista);
            System.out.println("Data/Hora: " + dataHoraRetirada);
            
            // Verificar disponibilidade do veículo
            boolean veiculoDisponivel = verificarDisponibilidadeVeiculo(placa);
            if (!veiculoDisponivel) {
                mostrarAlerta("Veículo " + placa + " já está em uso!");
                carregarVeiculosDisponiveis();
                return;
            }
            
            // Verificar disponibilidade do motorista
            boolean motoristaDisponivel = verificarDisponibilidadeMotorista(codigoMotorista);
            if (!motoristaDisponivel) {
                mostrarAlerta("Motorista já possui um veículo em uso!");
                return;
            }
            
            // REGISTRA A RETIRADA
            Utilidade registro = new Utilidade();
            registro.setPlacaVeiculo(placa);
            registro.setCodigoMotorista(codigoMotorista);
            registro.setDataHoraRetirada(dataHoraRetirada);
            registro.setDataHoraDevolucao(null); // Explicitar que não foi devolvido
            
            System.out.println("Registro criado: " + registro);
            
            Dao<Utilidade> dao = new Dao<>(Utilidade.class);
            dao.inserir(registro);
            
            lblAviso.setText(" Retirada registrada com sucesso! Veículo: " + placa);
            lblAviso.setStyle("-fx-text-fill: #90EE90;");
            
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Retirada registrada com sucesso!\nVeículo: " + placa);
            alert.showAndWait();
            
            limparCampos();
            carregarVeiculosDisponiveis();
            
        } catch (Exception e) {
            mostrarErro("Erro ao registrar retirada: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean verificarDisponibilidadeVeiculo(String placa) {
        try {
            System.out.println("Verificando disponibilidade do veículo: " + placa);
            
            Dao<Utilidade> daoUtilidade = new Dao<>(Utilidade.class);
            
            // Busca retiradas para esta placa
            List<Utilidade> retiradas = daoUtilidade.buscarPorAtributo("placaVeiculo", placa);
            
            System.out.println("Retiradas encontradas para placa " + placa + ": " + retiradas.size());
            
            for (Utilidade utilidade : retiradas) {
                System.out.println("Analisando retirada: " + utilidade);
                // Se não tem data de devolução, está em uso
                if (utilidade.getDataHoraDevolucao() == null) {
                    System.out.println("Veículo " + placa + " está EM USO (sem data de devolução)");
                    return false; // Veículo está em uso
                }
            }
            
            System.out.println("Veículo " + placa + " está DISPONÍVEL");
            return true; // Veículo disponível
        } catch (Exception e) {
            System.err.println("Erro ao verificar disponibilidade do veículo: " + e.getMessage());
            e.printStackTrace();
            return false; 
        }
    }
    
    private boolean verificarDisponibilidadeMotorista(String codigoMotorista) {
        try {
            System.out.println("Verificando disponibilidade do motorista: " + codigoMotorista);
            
            Dao<Utilidade> daoUtilidade = new Dao<>(Utilidade.class);
            
            // Busca retiradas para este motorista
            List<Utilidade> retiradas = daoUtilidade.buscarPorAtributo("codigoMotorista", codigoMotorista);
            
            System.out.println("Retiradas encontradas para motorista " + codigoMotorista + ": " + retiradas.size());
            
            for (Utilidade utilidade : retiradas) {
                System.out.println("Analisando retirada do motorista: " + utilidade);
                // Se não tem data de devolução, está em uso
                if (utilidade.getDataHoraDevolucao() == null) {
                    System.out.println("Motorista " + codigoMotorista + " já tem veículo EM USO");
                    return false; // Motorista já tem veículo em uso
                }
            }
            
            System.out.println("Motorista " + codigoMotorista + " está DISPONÍVEL");
            return true; // Motorista disponível
        } catch (Exception e) {
            System.err.println("Erro ao verificar disponibilidade do motorista: " + e.getMessage());
            e.printStackTrace();
            return false; 
        }
    }
    
    private void mostrarAlerta(String mensagem) {
        lblAviso.setText(mensagem);
        lblAviso.setStyle("-fx-text-fill: #DC143C;");
        
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Atenção");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    private void mostrarErro(String mensagem) {
        lblAviso.setText("ERRO: " + mensagem);
        lblAviso.setStyle("-fx-text-fill: #DC143C;");
        
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText("Ocorreu um erro");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    @FXML
    public void cancelar() throws IOException {
        App.setRoot("TelaFuncionalidade");
    }
    
    @FXML
    public void limparCampos() {
        comboVeiculo.setValue(null);
        comboMotorista.setValue(null);
        dateData.setValue(LocalDate.now());
    }
}