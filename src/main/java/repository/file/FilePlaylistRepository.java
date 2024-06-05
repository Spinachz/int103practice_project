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
    private File f = new File(filename);

    public FilePlaylistRepository() {
        if (f.exists()) {
            try ( FileInputStream fi = new FileInputStream(f);
                  BufferedInputStream bfi = new BufferedInputStream(fi);
                  ObjectInputStream obi = new ObjectInputStream(bfi);) {
                while (obi.read() != -1) {
                try {
                    this.nextPlaylistId = obi.readLong();
                    this.repo = (Map<String, Playlist>) obi.readObject();
                }
                catch (EOFException e) {
                    this.nextPlaylistId = 1;
                    this.repo = new TreeMap<>();
                    break;
                }
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
        saveRepo();
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
        saveRepo();
        return repo.remove(playlist.getPlaylistId(), playlist);
    }

    @Override
    public boolean update(Playlist playlist) throws PlaylistNotFoundException {
        if (playlist == null) {
            throw new PlaylistNotFoundException("Can not find this playlist, please try again.");
        }
        repo.replace(playlist.getPlaylistId(), playlist);
        saveRepo();
        return true;
    }

    @Override
    public Stream<Playlist> stream() {
        return repo.values().stream();
    }

    private void saveRepo() {
        try ( FileOutputStream fi = new FileOutputStream(f);
              BufferedOutputStream bfi = new BufferedOutputStream(fi);
              ObjectOutputStream obi = new ObjectOutputStream(bfi);) {
            obi.writeLong(nextPlaylistId);
            obi.writeObject(repo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}