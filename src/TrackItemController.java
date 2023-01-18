import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import logic.MP3Player;
import logic.Track;

import java.net.URL;
import java.util.ResourceBundle;

public class TrackItemController implements Initializable {

    @FXML
    private Label itemAlbum;

    @FXML
    private Label itemArtist;

    @FXML
    private ImageView itemCover;

    @FXML
    private Label itemDuration;

    @FXML
    private Label itemNo;

    @FXML
    private Label itemTitle;

    @FXML
    private Button playlistItemButton;

    /* not FXML */
    private Track track;

    private MP3Player player;

    private int trackNo;

    public void setPlayer(MP3Player player) {
        this.player = player;
        player.currTrack().addListener(((observableValue, oldTrack, newTrack) -> {
            if (newTrack.equals(track)) {
                playlistItemButton.setStyle("-fx-background-color: #323232;");
            } else if (oldTrack != null && oldTrack.equals(track)) {
                playlistItemButton.setStyle("-fx-background-color: transparent;");
            }
        }));
    }

    public void setItemData(Track track, int trackNo) {
        this.track = track;
        this.trackNo = trackNo;
        String title = track.getTitle();
        if (title != null) {
            itemTitle.setText(title);
        } else {
            itemTitle.setText(track.getFilename());
        }
        itemNo.setText(Integer.toString(trackNo));
        itemArtist.setText(track.getArtist());
        itemArtist.setText(track.getArtist());
        itemAlbum.setText(track.getAlbumTitle());
        itemDuration.setText(HelperClass.formatTime(track.getDuration()));
        itemCover.setImage(track.getAlbumImage());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playlistItemButton.setOnAction(event -> {
            player.play(player.getCurrPlaylist(), trackNo);
        });

        playlistItemButton.setOnMouseEntered(mouseEvent -> {
            if (player.isPlaying()) {
                if (!player.currTrack().get().equals(track)) {
                    playlistItemButton.setStyle("-fx-background-color: rgba(40, 40, 40, 0.5);");
                }
            } else {
                playlistItemButton.setStyle("-fx-background-color: rgba(40, 40, 40, 0.5);");
            }
        });

        playlistItemButton.setOnMouseExited(mouseEvent -> {
            if (player.isPlaying()) {
                if (!player.currTrack().get().equals(track)) {
                    playlistItemButton.setStyle("-fx-background-color: transparent;");
                }
            } else {
                playlistItemButton.setStyle("-fx-background-color: transparent;");
            }
        });
    }
}
