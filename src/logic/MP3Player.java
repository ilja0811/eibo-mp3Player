package logic;

import ddf.minim.AudioPlayer;
import de.hsrm.mi.eibo.simpleplayer.SimpleMinim;
import javafx.application.Platform;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class MP3Player {
    private final SimpleMinim minim;
    private AudioPlayer audioPlayer;
    private Playlist currPlaylist;
    private int trackNo;
    private final PlaylistManager playlistMan;
    private boolean repeatOne;
    private final SimpleObjectProperty<Track> currTrack;
    private final SimpleIntegerProperty currTrackPos;
    private Thread playThread;
    private Thread posThread;

    private SimpleFloatProperty volume;

    private final Playlist defaultPlaylist;

    public MP3Player() {
        minim = new SimpleMinim();
        playlistMan = new PlaylistManager(this);
        repeatOne = false;
        currTrack = new SimpleObjectProperty<>();
        currTrackPos = new SimpleIntegerProperty();
        volume = new SimpleFloatProperty(-20);
        defaultPlaylist = playlistMan.createDefaultPlaylist();
    }

    public boolean getRepeatOne() {
        return repeatOne;
    }

    public void stopCurrPlaylist() {
        if (currPlaylist.hasTrack(currTrack.get())) {
            if (isPlaying()) {
                audioPlayer.pause();
                audioPlayer = null;
            }
            currPlaylist = defaultPlaylist;
        }
    }

    public Playlist getDefaultPlaylist() {
        return defaultPlaylist;
    }

    public SimpleFloatProperty volume() {
        return volume;
    }

    public void setVolume(float f, int max) {
        f -= max;
        if (audioPlayer != null) {
            audioPlayer.setGain(volume.get());
            volume.set(f);
        }
    }

    public SimpleObjectProperty<Track> currTrack() {
        return currTrack;
    }

    public SimpleIntegerProperty currTrackPos() {
        return currTrackPos;
    }

    public boolean isPlaying() {
        if (audioPlayer == null) {
            return false;
        }
        return audioPlayer.isPlaying();
    }


    public void play(Playlist playlist, int trackNoInView) {
        currPlaylist = playlist;
        trackNo = trackNoInView - 1;

        currTrack.set(currPlaylist.getTracks().get(trackNo));
        playTrack();
    }

    public void runPosThread() {
        if (posThread != null) {
            posThread.interrupt();
        }

        posThread = new Thread(() -> {
            while (isPlaying()) {
                currTrackPos.setValue(audioPlayer.position() / 1000);
            }
        });
        posThread.start();
    }

    public void playTrack() {
        if (playThread != null) {
            playThread.interrupt();
        }

        playThread = new Thread(() -> {
            if (isPlaying()) {
                audioPlayer.pause();
            }

            audioPlayer = minim.loadFile(currTrack.get().getFilePath());
            audioPlayer.setGain(volume.get());

            audioPlayer.play();

            runPosThread();

            Platform.runLater(() -> {
                if (!isPlaying()) {
                    skip();
                }
            });
        });
        playThread.start();


    }

    public synchronized void skipBack() {
        if (trackNo > 0) {
            if (!repeatOne) {
                trackNo--;
            }
            currTrack.set(currPlaylist.getTracks().get(trackNo));
        } else {
            trackNo = 0;
            currTrack.set(currPlaylist.getTracks().get(trackNo));
        }
        playTrack();
    }

    public synchronized void skip() {
        if (trackNo < currPlaylist.getNumOfTracks() - 1) {
            if (!repeatOne) {
                trackNo++;
            }
            currTrack.set(currPlaylist.getTracks().get(trackNo));
        } else {
            trackNo = 0;
            currTrack.set(currPlaylist.getTracks().get(trackNo));
        }
        playTrack();
    }

    public void resume() {
        if (audioPlayer != null) {
            audioPlayer.play();
            runPosThread();
        }
    }

    public void pause() {
        if (audioPlayer != null) {
            audioPlayer.pause();
        }
    }

    public void toggleRepeat() {
        repeatOne = !repeatOne;
    }

    public void quit() {
        audioPlayer.pause();
    }

    public PlaylistManager getPlaylistMan() {
        return playlistMan;
    }

    public Playlist getCurrPlaylist() {
        return currPlaylist;
    }

    public void setCurrPlaylist(Playlist p) {
        currPlaylist = p;
    }

    public void mute() {
        if (audioPlayer != null) {
            audioPlayer.mute();
        }
    }

    public void unmute() {
        if (audioPlayer != null) {
            audioPlayer.unmute();
        }
    }
}
