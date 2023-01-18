package logic;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PlaylistManager {

    private List<Playlist> playlists;
    private int numOfPlaylists;
    private MP3Player player;

    public PlaylistManager(MP3Player player) {
        this.playlists = new LinkedList<>();
        this.numOfPlaylists = 0;
        this.player = player;
    }

    public Playlist createPlaylist(String title) {
        Playlist newPlaylist = new Playlist(title);
        playlists.add(newPlaylist);
        numOfPlaylists++;
        return newPlaylist;
    }

    public Playlist getPlaylist(String name) {
        Playlist playlist = null;
        for (Playlist pl : playlists) {
            if (pl.getTitle().equals(name)) {
                playlist = pl;
            }
        }
        return playlist;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void deletePlaylist(Playlist playlist) {
        player.stopCurrPlaylist();
        playlists.remove(playlist);
        playlist = null;
        numOfPlaylists--;

        /*
        File file = new File(playlist.getTitle());
        file.delete();
        */
    }

    public Playlist createDefaultPlaylist() {
        Playlist playlist = createPlaylistFromPath("Your Files", "mp3s");
        playlist.setPlaylistImage("/assets/no_cover.png");
        return playlist;
    }

    public void addTracksFromPath(Playlist currPlaylist, String path) {
        ArrayList<String> mp3Files = scanAllFiles(path);

        if (mp3Files != null) {
            for (String filePath : mp3Files) {
                currPlaylist.addTrack(filePath);
            }
        }
    }

    public Playlist createPlaylistFromPath(String title, String path) {
        Playlist newPlaylist = new Playlist(title);

        ArrayList<String> mp3Files = scanAllFiles(path);

        if (mp3Files != null) {
            for (String filePath : mp3Files) {
                newPlaylist.addTrack(filePath);
            }
        }
        return newPlaylist;
    }

    public ArrayList<String> scanAllFiles(String path) {
        File file = new File(path);
        FileFilter filter = new FileFilter();
        ArrayList<String> mp3s = new ArrayList<>();

        File[] files = file.listFiles();
        if (files == null)
            return null;

        for (File f : files) {
            if (!f.isDirectory() && f.exists()) {
                if (filter.accept(f)) {
                    mp3s.add(f.getAbsolutePath());
                }
            }
        }
        return mp3s;
    }

    private static class FileFilter {
        public boolean accept(File file) {
            return file.getName().endsWith(".mp3")
                    || file.getName().endsWith(".mp3");
        }
    }

    public String genPlaylistTitle() {
        return "Playlist #" + (numOfPlaylists + 1);
    }
}
