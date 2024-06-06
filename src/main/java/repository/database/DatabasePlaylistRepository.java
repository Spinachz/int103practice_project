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
    UserRepository userRepository;

    public DatabasePlaylistRepository() {
        this.repo = new TreeMap<>();
        this.userRepository = new DatabaseUserRepository();
        if (repo.isEmpty()) {
            var id = String.format("P%011d", nextPlaylistId);
            try (PreparedStatement stmt = connect.prepareStatement("SELECT * FROM Playlist")) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    User user = userRepository.retrieve(rs.getString("ownerId"));
                    Playlist playlist = new Playlist(user, rs.getString("playlistId"), rs.getString("playlistName"));
                    repo.put(id, playlist);
                    ++nextPlaylistId;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO Playlist(playlistId, playlistName, ownerId) VALUES (?, ?, ?)")) {
            stmt.setString(1, id);
            stmt.setString(2, playlistName);
            stmt.setString(3, owner.getId());
            stmt.executeUpdate();
            repo.put(id, playlist);
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
        try (PreparedStatement stmt = connect.prepareStatement("UPDATE Playlist SET playlistName=?")) {
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
        try (PreparedStatement stmt = connect.prepareStatement("DELETE FROM Playlist WHERE playlistId=?")) {
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
}