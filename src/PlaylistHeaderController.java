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

public class PlaylistHeaderController implements Initializable {
    @FXML
    private Button addFolderButton;

    @FXML
    private Button addTrackButton;

    @FXML
    private Button deletePlaylistButton;

    @FXML
    private ImageView playlistCover;

    @FXML
    private TextField titleTextField;

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
        playlistCover.setOnMousePressed(event -> {
            File file = mainController.imageFileChooser();

            if (file != null) {
                player.getCurrPlaylist().setPlaylistImage(file.getAbsolutePath());
                playlistCover.setImage(player.getCurrPlaylist().getPlaylistCover());
            }
        });

        addTrackButton.setOnAction(event -> {
            File file = mainController.audioFileChooser();

            if (file != null) {
                player.getCurrPlaylist().addTrack(file.getAbsolutePath());
                /* update view when another track is added */
                mainController.addTrackItem(player.getCurrPlaylist().getTracks().get(player.getCurrPlaylist().getNumOfTracks() - 1));
                loadPlaylistData();
            }
        });

        addFolderButton.setOnAction(event -> {
            File selectedDir = mainController.directoryChooser();

            if (selectedDir != null) {
                player.getPlaylistMan().addTracksFromPath(player.getCurrPlaylist(), selectedDir.getAbsolutePath());
                mainController.loadPlaylistView(player.getCurrPlaylist(), HeaderType.PLAYLIST);
            }
        });

        deletePlaylistButton.setOnAction(event -> {
            mainController.deletePlaylistItem();
            mainController.removeTrackItems();
            player.getPlaylistMan().deletePlaylist(player.getCurrPlaylist());
            mainController.loadPlaylistView(player.getDefaultPlaylist(), HeaderType.DIR);
        });

        titleTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                titleTextField.setEditable(true);
                titleTextField.setStyle("-fx-background-color: RGBA(18,18,18,0.4);");
            } else {
                /* reset text if user does not press save */
                titleTextField.setText(player.getCurrPlaylist().getTitle());
                titleTextField.setStyle("-fx-background-color: transparent;");
            }
        });

        /* save new title on enter */
        titleTextField.setOnAction(event -> {
            String newTitle = titleTextField.getText();
            /* save changes */
            player.getCurrPlaylist().setTitle(newTitle);
            mainController.changePlaylistItemTitle(newTitle);

            /* reset view */
            titleTextField.setEditable(false);
            titleTextField.setStyle("-fx-background-color: transparent;");
        });
    }

    public void loadHeader(Playlist playlist) {
        playlistCover.setImage(playlist.getPlaylistCover());
        playlistViewHeader.setStyle("-fx-background-color: linear-gradient(to top right, #080808, " + HelperClass.getAvgColorHex(playlist.getPlaylistCover()) + ");");
        titleTextField.setText(playlist.getTitle());
        playlistType.setText("PLAYLIST");
        loadPlaylistData();
    }

    public void loadPlaylistData() {
        playlistViewData.setText(String.format("%d Tracks", player.getCurrPlaylist().getNumOfTracks()) + ", " + HelperClass.formatTotalTime(player.getCurrPlaylist().totalDuration()));
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    public void setPlayer(MP3Player player) {
        this.player = player;
    }
}
