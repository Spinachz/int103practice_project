package repository.file;

import domain.Playlist;
import domain.User;
import java.io.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import exception.InvalidInputException;
import exception.PlaylistNotFoundException;
import exception.UserNotFoundException;
import repository.PlaylistRepository;

public class FilePlaylistRepository implements PlaylistRepository{
    private String filename = "playlist.dat";
    private long nextPlaylistId;
    private Map<String, Playlist> repo;
    private File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
    private File f = new File(tempDirectory + filename);

    public FilePlaylistRepository() {
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
            this.nextPlaylistId = 1;
            this.repo = new TreeMap<>();
        }
    }

    @Override
    public Playlist retrieve(String playlistId) {
        return repo.get(playlistId);
    }

    @Override
    public Playlist create(User owner, String playlistName) throws InvalidInputException,  UserNotFoundException {
        if (owner == null) {
            throw new UserNotFoundException("Can not find this user, please try again.");
        }
        String id = String.format("P%011d", nextPlaylistId);
        if (repo.containsKey(id)) {
            return null;
        }
        Playlist playlist = new Playlist(owner, id, playlistName);
        repo.put(id, playlist);
        ++nextPlaylistId;
        try ( FileOutputStream fi = new FileOutputStream(f);
              BufferedOutputStream bfi = new BufferedOutputStream(fi);
              ObjectOutputStream obi = new ObjectOutputStream(bfi);) {
            obi.writeLong(nextPlaylistId);
            obi.writeObject(repo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playlist;
    }

    @Override
    public boolean delete(User owner, Playlist playlist) throws UserNotFoundException, PlaylistNotFoundException {
        if (owner == null) {
            throw new UserNotFoundException("Can not find this user, please try again.");
        }
        if (playlist == null) {
            throw new PlaylistNotFoundException("Can not find this playlist, please try again.");
        }
        try ( FileOutputStream fi = new FileOutputStream(f);
              BufferedOutputStream bfi = new BufferedOutputStream(fi);
              ObjectOutputStream obi = new ObjectOutputStream(bfi);) {
            obi.writeLong(nextPlaylistId);
            obi.writeObject(repo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return repo.remove(playlist.getPlaylistId(), playlist);
    }

    @Override
    public boolean update(Playlist playlist) throws PlaylistNotFoundException {
        if (playlist == null) {
            throw new PlaylistNotFoundException("Can not find this playlist, please try again.");
        }
        repo.replace(playlist.getPlaylistId(), playlist);
        try ( FileOutputStream fi = new FileOutputStream(f);
              BufferedOutputStream bfi = new BufferedOutputStream(fi);
              ObjectOutputStream obi = new ObjectOutputStream(bfi);) {
            obi.writeLong(nextPlaylistId);
            obi.writeObject(repo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public Stream<Playlist> stream() {
        return repo.values().stream();
    }
}
