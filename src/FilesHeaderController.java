import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import logic.MP3Player;
import logic.Playlist;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logic.MP3Player;
import logic.Playlist;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class FilesHeaderController implements Initializable {
    @FXML
    private ImageView playlistCover;

    @FXML
    private TextField playlistTextField;

    @FXML
    private Label playlistType;

    @FXML
    private Label playlistViewData;

    @FXML
    private HBox playlistViewHeader;

    private MainController mainController;
    private MP3Player player;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playlistTextField.setEditable(false);

        playlistCover.setOnMousePressed(event -> {
            File file = mainController.imageFileChooser();

            if (file != null) {
                player.getCurrPlaylist().setPlaylistImage(file.getAbsolutePath());
                playlistCover.setImage(player.getCurrPlaylist().getPlaylistCover());
            }
        });
    }

    public void loadPlaylistData() {
        playlistViewData.setText(String.format("%d Tracks", player.getCurrPlaylist().getNumOfTracks()) + ", " + HelperClass.formatTotalTime(player.getCurrPlaylist().totalDuration()));
    }

    public void loadFilesHeader() {
        playlistCover.setImage(player.getDefaultPlaylist().getPlaylistCover());
        playlistViewHeader.setStyle("-fx-background-color: linear-gradient(to top right, #080808, " + HelperClass.getAvgColorHex(player.getDefaultPlaylist().getPlaylistCover()) + ");");
        playlistTextField.setText(player.getDefaultPlaylist().getTitle());
        playlistType.setText("DIRECTORY");
        loadPlaylistData();
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    public void setPlayer(MP3Player player) {
        this.player = player;
    }
}


