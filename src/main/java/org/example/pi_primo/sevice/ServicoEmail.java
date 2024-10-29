package org.example.pi_primo.sevice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.SimpleEmail;
import org.example.pi_primo.HelloApplication;
import org.example.pi_primo.HelloController;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.example.pi_primo.HelloController.conn;
import static org.example.pi_primo.HelloController.showAlert;

public class ServicoEmail {

    @FXML
    private TextField emailText;
    @FXML
    public TextField usuarioText;
    @FXML
    public Button enviarButton;
    @FXML
    public Button voltarButton;

    HelloApplication helloApplication = new HelloApplication();
    HelloController helloController = new HelloController();

    public static void EnviarDaSenhaPorEmail(String para, String titulo, String conteudo) {
        String MeuEmail = "vk.alugueis@gmail.com";
        String MinhaSenha = "VK-99088";

        SimpleEmail email = new SimpleEmail();
        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(465);
        email.setAuthenticator(new DefaultAuthenticator(MeuEmail, MinhaSenha));
        email.setSSLOnConnect(true);

        try {
            email.setFrom(MeuEmail);
            email.setSubject(titulo);
            email.setMsg(conteudo);
            email.addTo(para);
            email.send();
            System.out.println("E-mail enviado com sucesso");
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail: " + e.getMessage());
        }
    }

    public void enviarClicked() {
        try {
            helloController.conection();
            String query = "SELECT senha FROM Cliente WHERE nome = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, usuarioText.getText());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String senha = rs.getString("senha");
                String para = emailText.getText();
                EnviarDaSenhaPorEmail(para, "Recuperar Senha", "Sua senha é: " + senha);
                showAlert("Sucesso", "Senha enviada ao e-mail.");
            } else {
                showAlert("Erro", "Usuário não encontrado.");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            showAlert("Erro", "Erro ao enviar senha: " + e.getMessage());
        } finally {
            try {
                helloController.closeConection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void voltarClicked(ActionEvent event) {
        helloApplication.loadScreen("paginaLogin.fxml", "Vk", event);
    }
}