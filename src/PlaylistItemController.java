import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import logic.MP3Player;
import logic.Playlist;

import java.net.URL;
import java.util.ResourceBundle;

public class PlaylistItemController implements Initializable {

    @FXML
    private Label playlistItemTitle;

    @FXML
    private Button playlistButton;

    private Playlist playlist;

    private MP3Player player;

    private MainController mainController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playlistButton.setOnAction((event) -> {
            if (!player.getCurrPlaylist().equals(playlist)) {
                mainController.removeTrackItems();
                mainController.loadPlaylistView(playlist, HeaderType.PLAYLIST);
                mainController.setPlaylistItemController(this);
            }
        });
    }

    public Button getPlaylistButton() {
        return playlistButton;
    }

    public void setData(Playlist playlist) {
        this.playlist = playlist;
        playlistItemTitle.setText(playlist.getTitle());
        mainController.setPlaylistItemController(this);
    }

    public void setTitle(String title) {
        playlistItemTitle.setText(title);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setPlayer(MP3Player player) {
        this.player = player;
    }
}



