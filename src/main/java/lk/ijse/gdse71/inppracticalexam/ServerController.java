package lk.ijse.gdse71.inppracticalexam;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lk.ijse.gdse71.inppracticalexam.Client.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ServerController implements Initializable {

    @FXML
    private Button btnAddNewClinet;

    @FXML
    private VBox chatPain;

    @FXML
    private Label lblTopic;

    @FXML
    private TextField txtClinetName;

    private String massage = "";
    private ServerSocket serverSocket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private ArrayList<Client> clients = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // server connect
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(5000);
                chatPain.getChildren().add(new Label("Server is started...!"));

                while (true) {
                    Socket socket = serverSocket.accept();

                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    Client client = new Client();
                    client.setClientName(txtClinetName.getText());
                    client.setSocket(socket);
                    client.setDataInputStream(dataInputStream);
                    client.setDataOutputStream(dataOutputStream);

                    clients.add(client);

                    Platform.runLater(()->{
                        chatPain.getChildren().add(new Label(client.getClientName() + "  Client is connected...!"));

                    });
                    clientHandler(client);

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

//    fileter massage and sender
    private void clientHandler(Client client) {
        new Thread(() -> {
            DataInputStream inputStream = client.getDataInputStream();
            try {
                massage = inputStream.readUTF();
                while (true) {
                    if (massage.equals("BYE")) {
                        serverSocket.close();
                    }

                    brotCast(massage, client);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }).start();

    }

    // send all clients massage
    private void brotCast(String massage, Client sender) {
        synchronized (clients) {
            for (Client client1 : clients) {
                if (client1 != sender) {
                    try {
                        client1.getDataOutputStream().writeUTF(sender.getClientName() + ":   " + massage);
                        client1.getDataOutputStream().flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }

    }

    @FXML
    void btnAddNewClinetONAction(ActionEvent event) throws IOException {
        String name = txtClinetName.getText();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Client.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Client!");
        stage.setScene(scene);
        stage.show();
        txtClinetName.clear();
    }

}
