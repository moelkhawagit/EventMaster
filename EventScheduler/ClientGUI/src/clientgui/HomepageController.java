
package clientgui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class HomepageController implements Initializable {

    @FXML private Text helloText;
    public void loadYourScheduledEventsScene(ActionEvent event) throws IOException{
        Parent root= FXMLLoader.load(getClass().getResource("usersScheuledEventsScene.fxml"));
        Scene receivingScene = new Scene(root);
        Stage window=(Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(receivingScene);
        window.show();  
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       helloText.setText("Hello " + LogInSceneController.full_name+"!");
    }    
    
}
