package logic;

import com.mpatric.mp3agic.*;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Track {

    private String filePath;

    private String filename;
    private String title;
    private String artist;
    private String albumTitle;
    private byte[] imageData;
    private Mp3File mp3file;

    private long duration;

    private static final Map<Track, Image> albumImageCache = new HashMap<>();

    public Track(String filePath) throws InvalidDataException, UnsupportedTagException, IOException {
        if (filePath == null || filePath.isEmpty()) {
            return;
        }
        this.filePath = filePath;

        try {
            this.mp3file = new Mp3File(filePath);
            this.duration = mp3file.getLengthInSeconds();
        } catch (InvalidDataException | UnsupportedTagException | IOException e) {
            e.printStackTrace();
        }

        this.filename = Paths.get(filePath).getFileName().toString();

        if (mp3file.hasId3v1Tag()) {
            ID3v1 id3v1Tag = mp3file.getId3v1Tag();
            this.title = id3v1Tag.getTitle();
            this.artist = id3v1Tag.getArtist();
            this.albumTitle = id3v1Tag.getAlbum();
        }

        if (mp3file.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3file.getId3v2Tag();
            this.imageData = id3v2Tag.getAlbumImage();
        }
    }

    public Image getAlbumImage() {
        if (albumImageCache.containsKey(this)) {
            return albumImageCache.get(this);
        }

        Image image;
        if (imageData != null) {
            image = new Image(new ByteArrayInputStream(imageData));
        } else {
            image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/no_cover.png")));
        }
        albumImageCache.put(this, image);
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getDuration() {
        return duration;
    }

    public String getFilename() {
        return filename;
    }
}
