package lk.ijse.gdse71.inppracticalexam;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    @FXML
    private Button btnSendMassage;

    @FXML
    private VBox chatPain;

    @FXML
    private Label lblClientName;

    @FXML
    private TextField txtMassage;

    private String massage = "";
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(() -> {
            try {
                socket = new Socket("localhost", 5000);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                while (!massage.equals("BYE")) {
                    massage = dataInputStream.readUTF();

                    if (massage.equals("TIME")){

                        String time = String.valueOf(LocalTime.now());
                        chatPain.getChildren().add(new Label(time));

                    }else if (massage.equals("DATE")){

                        String date = String.valueOf(LocalDate.now());
                        chatPain.getChildren().add(new Label(date));

                    }else if (massage.equals("BYE")){
                        socket.close();
                        System.out.println("Connection is closed...!");
                    }else{
                        Platform.runLater(() -> {
                            System.out.println("5");
                            chatPain.getChildren().add(new Label(massage));
                        });
                    }

                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }


    // Client send massage
    @FXML
    void btnSendMassageOnAction(ActionEvent event) {
        massage = txtMassage.getText();

        try {
            dataOutputStream.writeUTF(massage);
            dataOutputStream.flush();
            txtMassage.clear();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
