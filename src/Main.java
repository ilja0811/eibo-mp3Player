import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/main-view.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1100, 600);

        MainController mainController = loader.getController();
        mainController.setPrimaryStage(stage);

        stage.setTitle("MP3 Player");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(windowEvent -> {
            mainController.stopPlaying();
        });
    }

    public static void main(String[] args) {
        launch();
    }
}