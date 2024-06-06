package repository.database;

import domain.Playlist;
import domain.User;
import exception.InvalidInputException;
import exception.PlaylistNotFoundException;
import exception.UserNotFoundException;
import repository.PlaylistRepository;
import repository.UserRepository;

import java.sql.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class DatabasePlaylistRepository implements PlaylistRepository {
    private long nextPlaylistId = 1;
    Connection connect = DatabaseConnection.connect();
    private final Map<String, Playlist> repo;
    UserRepository userRepository = new DatabaseUserRepository();

    public DatabasePlaylistRepository() {
        this.repo = new TreeMap<>();
        if (repo.isEmpty()) {
            loadDB();
        }
    }

    @Override
    public Playlist retrieve(String playlistId) {
        return repo.get(playlistId);
    }

    @Override
    public Playlist create(User owner, String playlistName) throws InvalidInputException, UserNotFoundException {
        if (owner == null) throw new UserNotFoundException("Can not find this user, please try again.");
        var id = String.format("P%011d", nextPlaylistId);
        if (repo.containsKey(id)) return null;
        Playlist playlist = new Playlist(owner, id, playlistName);
        try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO playlist (playlistId, playlistName, ownerId) VALUES (?, ?, ?)")) {
            stmt.setString(1, id);
            stmt.setString(2, playlistName);
            stmt.setString(3, owner.getId());
            stmt.executeUpdate();
            repo.put(playlist.getPlaylistId(), playlist);
            ++nextPlaylistId;
            return playlist;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(Playlist playlist) throws PlaylistNotFoundException {
        if (playlist == null) throw new PlaylistNotFoundException("Can not find this playlist, please try again.");
        try (PreparedStatement stmt = connect.prepareStatement("UPDATE playlist SET playlistName=?")) {
            stmt.setString(1, playlist.getPlaylistName());
            stmt.executeUpdate();
            repo.replace(playlist.getPlaylistId(), playlist);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(User owner, Playlist playlist) throws UserNotFoundException, PlaylistNotFoundException {
        if (owner == null) throw new UserNotFoundException("Can not find this user, please try again.");
        if (playlist == null) throw new PlaylistNotFoundException("Can not find this playlist, please try again.");
        try (PreparedStatement stmt = connect.prepareStatement("DELETE FROM playlist WHERE playlistId=?")) {
            stmt.setString(1, playlist.getPlaylistId());
            stmt.executeUpdate();
            return repo.remove(playlist.getPlaylistId(), playlist);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Stream<Playlist> stream() {
        return repo.values().stream();
    }

    private boolean loadDB() {
        try (PreparedStatement stmt = connect.prepareStatement("SELECT playlistId, playlistName, ownerId FROM playlist")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = userRepository.retrieve(rs.getString(3));
                Playlist playlist = new Playlist(user, rs.getString("playlistId"), rs.getString("playlistName"));
                repo.put(rs.getString("PlaylistId"), playlist);
                ++nextPlaylistId;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}