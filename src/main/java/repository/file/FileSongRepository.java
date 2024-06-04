package repository.file;

import domain.Playlist;
import domain.Artist;
import java.io.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import domain.Song;
import exception.InvalidInputException;
import exception.ArtistNotFoundException;
import exception.SongNotFoundException;
import repository.SongRepository;

public class FileSongRepository implements SongRepository {
    private String filename = "song.dat";
    private long nextSongId;
    private Map<String, Song> repo;
    private File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
    private File f = new File(tempDirectory + filename);

    public FileSongRepository() {
        if (f.exists()) {
            try ( FileInputStream fi = new FileInputStream(f);
                  BufferedInputStream bfi = new BufferedInputStream(fi);
                  ObjectInputStream obi = new ObjectInputStream(bfi);) {
                while (obi.read() != -1) {
                    obi.readLong();
                    obi.readObject();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            this.nextSongId = 1;
            this.repo = new TreeMap<>();
        }
    }

    @Override
    public Song retrieve(String songId) {
        return repo.get(songId);
    }

    @Override
    public Song create(Artist artist, String title) throws ArtistNotFoundException, InvalidInputException {
        if (artist == null) {
            throw new ArtistNotFoundException();
        }
        var id = String.format("S%011d", nextSongId);
        if (repo.containsKey(id)) {
            return null;
        }
        var song = new Song(id, title, artist);
        repo.put(id, song);
        ++nextSongId;
        try ( FileOutputStream fi = new FileOutputStream(f);
              BufferedOutputStream bfi = new BufferedOutputStream(fi);
              ObjectOutputStream obi = new ObjectOutputStream(bfi);) {
            obi.writeLong(nextSongId);
            obi.writeObject(repo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return song;
    }

    @Override
    public boolean delete(Artist artist, Song song) throws SongNotFoundException, ArtistNotFoundException {
        if (artist == null) {
            throw new ArtistNotFoundException("Can not find this artist, please try again.");
        }
        if (song == null) {
            throw new SongNotFoundException("Can not find this song, please try again.");
        }
        try ( FileOutputStream fi = new FileOutputStream(f);
              BufferedOutputStream bfi = new BufferedOutputStream(fi);
              ObjectOutputStream obi = new ObjectOutputStream(bfi);) {
            obi.writeLong(nextSongId);
            obi.writeObject(repo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return repo.remove(song.getSongId(), song);
    }

    @Override
    public boolean update(Song song) throws SongNotFoundException {
        if (song == null) {
            throw new SongNotFoundException("Can not find this song, please try again.");
        }
        repo.replace(song.getSongId(), song);
        try ( FileOutputStream fi = new FileOutputStream(f);
              BufferedOutputStream bfi = new BufferedOutputStream(fi);
              ObjectOutputStream obi = new ObjectOutputStream(bfi);) {
            obi.writeLong(nextSongId);
            obi.writeObject(repo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public Stream<Song> stream() {
        return repo.values().stream();
    }
}

