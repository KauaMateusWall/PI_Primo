package org.example.pi_primo.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.example.pi_primo.HelloApplication;
import org.example.pi_primo.config.ConexaoDB;
import org.example.pi_primo.model.Produto;
import org.example.pi_primo.model.Session;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class TelaMenu {

    ConexaoDB conexaoDB = new ConexaoDB();
    private final HelloApplication helloApplication = new HelloApplication();
    private final ObservableList<Produto> produtos = FXCollections.observableArrayList();

    @FXML
    public TableView<Produto> TabelaListaProduto;
    @FXML
    public Button meusProdutosButton;
    @FXML
    public Button meusPedidosButton;
    @FXML
    public Button pesquisarProdutosButton;
    @FXML
    public Button meuUsuarioButton;
    @FXML
    public Button sairAplicacaoButton;
    @FXML
    public Button sairContaButton;
    @FXML
    public MenuItem menu;

    @FXML
    public void initialize() throws SQLException {
        conexaoDB.conection();
        setupTableColumns();
        try {
            listarProduto();
        } catch (SQLException e) {
            ConexaoDB.showAlert("Erro", "Não foi possível listar os produtos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void meuUsuarioClicked(ActionEvent actionEvent) {
    }

    @FXML
    public void sairUsuarioClicked(ActionEvent event) throws IOException {
        helloApplication.loadScreen("paginaMeuUsuario.fxml", "Empréstimo VK", event);
    }

    @FXML
    public void meusPedidosClicked(ActionEvent actionEvent) {
    }

    @FXML
    public void meusProdutosClicked(ActionEvent actionEvent) {
    }

    @FXML
    public void sairAplicacaoClicked(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação de Saída");
        alert.setHeaderText("Deseja realmente fechar o programa?");
        alert.setContentText("Todas as alterações não salvas serão perdidas.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    @FXML
    public void sairContaClicked(ActionEvent event) {
        helloApplication.loadScreen("paginaLogin.fxml", "Empréstimo VK", event);
    }

    public void listarProduto() throws SQLException {
        String query = "SELECT * FROM produto p ORDER BY quantidadeDeEmprestimos ASC;";
        try (Connection conn = ConexaoDB.conn;
             PreparedStatement smt = conn.prepareStatement(query);
             ResultSet rs = smt.executeQuery()) {

            produtos.clear();

            while (rs.next()) {
                Produto produto = new Produto(
                        rs.getString("nome"),
                        rs.getString("categoria_Produto"),
                        rs.getString("descricao"),
                        rs.getInt("quantidadeDeEmprestimos"),
                        rs.getDouble("preco"),
                        rs.getInt("id")
                );
                produtos.add(produto);
            }

            TabelaListaProduto.setItems(produtos);
        }
    }

    private void setupTableColumns() {
        TableColumn<Produto, String> nomeColuna = new TableColumn<>("Nome");
        nomeColuna.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Produto, String> tipoColuna = new TableColumn<>("Tipo");
        tipoColuna.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        TableColumn<Produto, String> descricaoColuna = new TableColumn<>("Descrição");
        descricaoColuna.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        TableColumn<Produto, Integer> quantidadeColuna = new TableColumn<>("Quantidade de Empréstimos");
        quantidadeColuna.setCellValueFactory(new PropertyValueFactory<>("quantidadeDeEmprestimos"));

        TableColumn<Produto, Double> precoColuna = new TableColumn<>("Preço");
        precoColuna.setCellValueFactory(new PropertyValueFactory<>("preco"));

        TableColumn<Produto, Integer> idColuna = new TableColumn<>("ID");
        idColuna.setCellValueFactory(new PropertyValueFactory<>("id"));

        TabelaListaProduto.getColumns().clear();
        TabelaListaProduto.getColumns().addAll(nomeColuna, tipoColuna, descricaoColuna, quantidadeColuna, precoColuna, idColuna);
    }

    public void handleProductSelection(MouseEvent mouseEvent) throws Exception {
        Produto produto=TabelaListaProduto.getSelectionModel().getSelectedItem();
        Session.produto.setId(produto.getId());
        Session.produto.setNome(produto.getNome());
        Session.produto.setDescricao(produto.getDescricao());
        Session.produto.setPreco(produto.getPreco());
        Session.produto.setTipo(produto.getTipo());
        Session.produto.setQuantidadeDeEmprestimos(produto.getQuantidadeDeEmprestimos());

        if(produto == null){
            conexaoDB.showAlert("VK","O produto não existe!!", Alert.AlertType.ERROR);
        }

        helloApplication.loadScreenMouse("paginaProduto.fxml", "VK",mouseEvent);
    }

    public void CadastrarProduto(ActionEvent event) {
        helloApplication.loadScreen("","",event);
    }
}
