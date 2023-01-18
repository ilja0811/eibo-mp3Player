
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import logic.MP3Player;
import logic.Playlist;
import logic.Track;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {
    @FXML
    private Label controlBarArtist;

    @FXML
    private ImageView controlBarImage;

    @FXML
    private Label controlBarTitle;

    @FXML
    private Button createPlaylistButton;

    @FXML
    private Label durLabel;

    @FXML
    private Button filesButton;

    @FXML
    private VBox playlistItemsContainer;

    @FXML
    private Button playPauseButton;

    @FXML
    private ImageView playPauseImage;

    @FXML
    private ScrollPane playlistScrollPane;

    @FXML
    private VBox trackItemsContainer;

    @FXML
    private VBox playlistViewContainer;

    @FXML
    private Label posLabel;

    @FXML
    private Slider posSlider;

    @FXML
    private Button skipNextButton;

    @FXML
    private Button skipPrevButton;

    @FXML
    private ImageView repeatButtonImg;

    @FXML
    private Slider volSlider;

    @FXML
    private Button repeatButton;

    @FXML
    private Button shuffleButton;

    private MP3Player player;

    private Stage primaryStage;

    private int nextTrackNo;

    private PlaylistItemController currPlaylistItemController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        player = new MP3Player();

        loadAllPlaylistItems(player.getPlaylistMan().getPlaylists());
        loadPlaylistView(player.getDefaultPlaylist(), HeaderType.DIR);

        Platform.setImplicitExit(false);

        playPauseButton.setOnAction(event -> {
            if (player.isPlaying()) {
                setViewToPaused();
                player.pause();
            } else {
                setViewToPlaying();
                player.resume();
            }
        });

        repeatButton.setOnAction(event -> {
            player.toggleRepeat();
            System.out.println(player.getRepeatOne());
            if (player.getRepeatOne()) {
                repeatButtonImg.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/repeat_one.png"))));
            } else {
                repeatButtonImg.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/repeat.png"))));
            }
        });

        skipPrevButton.setOnAction(event -> {
            player.skipBack();
        });

        skipNextButton.setOnAction(event -> {
            player.skip();
        });

        filesButton.setOnAction(event -> {
            if (!player.getCurrPlaylist().equals(player.getDefaultPlaylist())) {
                loadPlaylistView(player.getDefaultPlaylist(), HeaderType.DIR);
            }
        });

        createPlaylistButton.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Create Playlist");
            dialog.setHeaderText("Enter a name:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                if (result.get().equals("")) {
                    String defaultInput = player.getPlaylistMan().genPlaylistTitle();
                    Playlist newPlaylist = player.getPlaylistMan().createPlaylist(defaultInput);
                    addPlaylistItem(newPlaylist);
                    loadPlaylistView(newPlaylist, HeaderType.PLAYLIST);
                } else {
                    String userInput = result.get();
                    Playlist newPlaylist = player.getPlaylistMan().createPlaylist(userInput);
                    addPlaylistItem(newPlaylist);
                    loadPlaylistView(newPlaylist, HeaderType.PLAYLIST);
                }
            }
        });

        posSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> Platform.runLater(() -> {
            posLabel.setText(HelperClass.formatTime(newValue.longValue()));
        }));

        player.currTrackPos().addListener((observableValue, oldValue, newValue) -> {
            posSlider.adjustValue(newValue.doubleValue());
        });

        volSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() == 0) {
                player.mute();
            } else if (oldValue.intValue() == 0 && newValue.intValue() > 0) {
                player.unmute();
            }
            player.setVolume(newValue.floatValue(), (int) volSlider.maxProperty().get());
        });

        player.currTrack().addListener((observableValue, oldTrack, newTrack) -> {
            // weil GUI nur von Application thread geändert werden darf
            Platform.runLater(() -> {
                loadControlBarView(newTrack);
            });
        });
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void setViewToPaused() {
        playPauseImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/play.png"))));
    }

    public void setViewToPlaying() {
        playPauseImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/pause.png"))));
    }

    public void deletePlaylistItem() {
        playlistItemsContainer.getChildren().remove(currPlaylistItemController.getPlaylistButton());
    }

    public void setPlaylistItemController(PlaylistItemController controller) {
        currPlaylistItemController = controller;
    }

    public void addPlaylistItem(Playlist p) {
        Button button;
        PlaylistItemController playlistController;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("resources/playlist-item.fxml"));

        try {
            button = fxmlLoader.load();
            playlistController = fxmlLoader.getController();
            playlistController.setMainController(this);
            playlistController.setPlayer(player);
            playlistController.setData(p);
            playlistItemsContainer.getChildren().add(button);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadAllPlaylistItems(List<Playlist> allPlaylists) {
        for (Playlist p : allPlaylists) {
            addPlaylistItem(p);
        }
    }

    public void removeTrackItems() {
        if (trackItemsContainer.getChildren().size() > 0) {
            trackItemsContainer.getChildren().removeAll(trackItemsContainer.getChildren());
        }
    }

    public void changePlaylistItemTitle(String title) {
        currPlaylistItemController.setTitle(title);
    }

    public void addTrackItem(Track t) {
        Pane pane;
        FXMLLoader fxmlLoader;
        TrackItemController itemController;

        fxmlLoader = new FXMLLoader(getClass().getResource("resources/track-item.fxml"));

        try {
            pane = fxmlLoader.load();
            itemController = fxmlLoader.getController();
            itemController.setItemData(t, nextTrackNo);
            itemController.setPlayer(player);
            trackItemsContainer.getChildren().add(pane);
            nextTrackNo++;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadPlaylistView(Playlist playlist, HeaderType type) {
        player.setCurrPlaylist(playlist);
        nextTrackNo = 1;

        playlistScrollPane.setVvalue(0);

        removePlaylistHeader();
        if (type == HeaderType.PLAYLIST) {
            loadPlaylistHeader();
        } else {
            loadFilesHeader();
        }

        removeTrackItems();
        for (Track t : playlist.getTracks()) {
            addTrackItem(t);
        }
    }

    public void loadPlaylistHeader() {
        HBox hbox;
        FXMLLoader fxmlLoader;
        PlaylistHeaderController headerController;

        fxmlLoader = new FXMLLoader(getClass().getResource("resources/playlist-header.fxml"));

        try {
            hbox = fxmlLoader.load();
            headerController = fxmlLoader.getController();
            headerController.setMainController(this);
            headerController.setPlayer(player);
            headerController.loadHeader(player.getCurrPlaylist());
            playlistViewContainer.getChildren().add(0, hbox);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadFilesHeader() {
        HBox hbox;
        FXMLLoader fxmlLoader;
        FilesHeaderController headerController;

        fxmlLoader = new FXMLLoader(getClass().getResource("resources/files-header.fxml"));

        try {
            hbox = fxmlLoader.load();
            headerController = fxmlLoader.getController();
            headerController.setMainController(this);
            headerController.setPlayer(player);
            headerController.loadFilesHeader();
            playlistViewContainer.getChildren().add(0, hbox);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removePlaylistHeader() {
        if (playlistViewContainer.getChildren().size() > 1) {
            playlistViewContainer.getChildren().remove(0);
        }
    }

    public void loadControlBarView(Track track) {
        String title = track.getTitle();

        controlBarImage.setImage(track.getAlbumImage());

        if (title != null) {
            controlBarTitle.setText(title);
        } else {
            controlBarTitle.setText(track.getFilename());
        }
        controlBarArtist.setText(track.getArtist());

        posSlider.maxProperty().setValue(track.getDuration()); // max value to duration of track
        posSlider.majorTickUnitProperty().setValue(track.getDuration()); // no major tick unit property
        posSlider.minorTickCountProperty().setValue(track.getDuration() - 1); // snap to seconds

        volSlider.maxProperty().setValue(75);
        volSlider.setValue(player.volume().getValue() + volSlider.maxProperty().get());

        durLabel.setText(HelperClass.formatTime(track.getDuration()));
        posLabel.setText(HelperClass.formatTime(0));

        setViewToPlaying();
    }

    public File imageFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an image File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
        fileChooser.setSelectedExtensionFilter(extFilterPNG);

        return fileChooser.showOpenDialog(primaryStage);
    }

    public File audioFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select MP3 File");
        fileChooser.setInitialDirectory(new File("mp3s"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Audio Files", "*.mp3"));

        return fileChooser.showOpenDialog(primaryStage);
    }

    public File directoryChooser() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select folder with MP3 files");
        dirChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        return dirChooser.showDialog(primaryStage);
    }

    public void stopPlaying() {
        player.stopCurrPlaylist();
    }
}


