package logic;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Playlist {

    private List<Track> tracks;
    private static String m3u = "#EXTM3U";
    private int counter;
    private SimpleStringProperty title;

    private static Map<Playlist, Image> playlistImageCache = new HashMap<>();
    private StringBuilder m3uBuilder;

    public Playlist(String title) {
        this.tracks = new ArrayList<>();
        this.counter = 0;
        this.title = new SimpleStringProperty(title);
        m3uBuilder = new StringBuilder(m3u);
    }

    public void setPlaylistImage(String filePath) {
        Image image = new Image(filePath);
        playlistImageCache.put(this, image);
    }

    public boolean hasTrack(Track t) {
        return tracks.contains(t);
    }

    public Image getPlaylistCover() {
        if (playlistImageCache.containsKey(this)) {
            return playlistImageCache.get(this);
        }
        return getRandomCover();
    }

    public Image getRandomCover() {
        for (Track t : tracks) {
            if (t.getAlbumImage() != null) {
                return t.getAlbumImage();
            }
        }
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/no_cover.png")));
    }

    public int getNumOfTracks() {
        return tracks.size();
    }

    public void addTrack(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return;
        }

        counter++;
        Track newTrack = null;
        try {
            newTrack = new Track(filePath);
            tracks.add(newTrack);
        } catch (IOException | InvalidDataException | UnsupportedTagException e) {
            e.printStackTrace();
        }

        if (newTrack.getTitle() != null && newTrack.getArtist() != null) {
            m3uBuilder.append("\n#EXTINF:").append(counter).append(",").append(newTrack.getArtist()).append(" - ").append(newTrack.getTitle()).append("\nmp3s/").append(newTrack.getFilePath());
        } else {
            m3uBuilder.append("\n#EXTINF:").append(counter).append(",").append(newTrack.getArtist()).append(" - ").append(newTrack.getFilename()).append("\nmp3s/").append(newTrack.getFilePath());
        }


        File m3uPlaylist = new File("mp3s/playlists/" + title.get() + ".m3u");

        try {
            BufferedWriter m3uWriter = new BufferedWriter(new FileWriter(m3uPlaylist.getAbsolutePath()));
            m3uWriter.write(m3uBuilder.toString());
            m3uWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public long totalDuration() {
        long total = 0;
        for (Track t : tracks) {
            total += t.getDuration();
        }
        return total;
    }

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty title() {
        return title;
    }

    public void setTitle(String newTitle) {
        title.set(newTitle);
    }
}
