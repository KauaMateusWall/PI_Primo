package org.example.pi_primo.service;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import org.example.pi_primo.HelloApplication;
import org.example.pi_primo.config.ConexaoDB;
import org.example.pi_primo.model.Session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.example.pi_primo.config.ConexaoDB.conn;
import static org.example.pi_primo.config.ConexaoDB.showAlert;

public class TelaProduto {

    public TextField tempoText;
    ConexaoDB helloController = new ConexaoDB();

    @FXML
    public Scene mainScene;
    @FXML
    public Label NomeTXT;
    @FXML
    public Label PrecoTXT;
    @FXML
    public Label ProTXT;
    @FXML
    public Label DescricaoTXT;
    @FXML
    public Button alugarButton;

    @FXML
    public void initialize() throws SQLException {
        helloController.conection();
        PrecoTXT.setText(String.valueOf(Session.produto.getPreco()));
        NomeTXT.setText(Session.produto.getNome());
        ProTXT.setText(Session.produto.getProprietario());
        DescricaoTXT.setText(Session.produto.getDescricao());

        if(Session.usuario.getid() == Session.produto.getidProprietario()){
            alugarButton.setText("Você é o dono!!");
            alugarButton.setDisable(true);
            tempoText.setDisable(true);
        }

        if(Session.produto.getSituacao().equals("Indisponível")){
            tempoText.setDisable(true);
            tempoText.setText("Já alugado!");
            alugarButton.setDisable(true);
            alugarButton.setText("Indisponível");
        }
        if(Session.produto.getidProprietario()!=0 && Session.produto.getidProprietario()==Session.usuario.getid()){
            tempoText.setDisable(true);
            tempoText.setText("Você é o dono!");
            alugarButton.setDisable(true);
            alugarButton.setText("Dono");
        }

        String testAlugado="SELECT * FROM emprestimo WHERE id_cliente_receptor=? AND data_devolucao>NOW();";
        try(PreparedStatement pstmt= conn.prepareStatement(testAlugado)) {
            pstmt.setInt(1,Session.usuario.getid());
            ResultSet rs=pstmt.executeQuery();

            if (rs.next()) {
                tempoText.setDisable(true);
                tempoText.setText("Em seus pedidos!");
                alugarButton.setDisable(true);
                alugarButton.setText("Usando...");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void alugarClicked() {
        helloController.conection();
        String testPedido="SELECT * FROM emprestimo WHERE id_cliente_fornecedor=?;";

        try(PreparedStatement pstmt = conn.prepareStatement(testPedido)){
            pstmt.setInt(1,Session.produto.getidProprietario());
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                initialize();
                showAlert("VK","O produto já foi alugado, desculpe!", Alert.AlertType.ERROR);
                return;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        String INSERTPedido="INSERT INTO emprestimo (id_cliente_fornecedor," +
                " id_cliente_receptor," +
                " id_produto, data_emprestimo," +
                " data_devolucao) " +
                "VALUES (?, ?, ?, NOW(), ?);";
        try(PreparedStatement pstmt = conn.prepareStatement(INSERTPedido)){
            if(tempoText.getText().isEmpty()){
                showAlert("Necessário a quantidade","Coloque a quantidade de meses que se deseja alugar no campo acima do botão \"Alugar\"!", Alert.AlertType.WARNING);
                return;
            }
            long meses=Long.parseLong(tempoText.getText());
            pstmt.setInt(1,Session.produto.getidProprietario());
            pstmt.setInt(2,Session.usuario.getid());
            pstmt.setInt(3,Session.produto.getId());
            pstmt.setDate(4, java.sql.Date.valueOf(LocalDate.now().plusMonths(meses)));

            pstmt.executeUpdate();
        } catch (SQLException | NumberFormatException e){
            e.printStackTrace();
        }
    }

    public void mesesmask(KeyEvent mouseEvent) {
        TextFieldFormatter tff = new TextFieldFormatter();
        tff.setMask("##");
        tff.setCaracteresValidos("0123456789");
        tff.setTf(tempoText);
        tff.formatter();
    }
}
