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
    String url = "jdbc:mysql://localhost:3306/AppProjectDB";
    private final Map<String, Playlist> repo;
    UserRepository userRepository;

    public DatabasePlaylistRepository() {
        this.repo = new TreeMap<>();
        if (repo.isEmpty()) {
            var id = String.format("P%011d", nextPlaylistId);
            String sql = "SELECT * FROM Playlist";
            try (Connection connect = DriverManager.getConnection(url);
                 PreparedStatement stmt = connect.prepareStatement(sql)) {
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
        String sql = "INSERT INTO Playlist(playlistId, playlistName, ownerId) VALUES (?, ?, ?)";
        try (Connection connect = DriverManager.getConnection(url);
             PreparedStatement stmt = connect.prepareStatement(sql)) {
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
        String sql = "UPDATE Playlist SET playlistName=?";
        try (Connection connect = DriverManager.getConnection(url);
             PreparedStatement stmt = connect.prepareStatement(sql)) {
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
        String sql = "DELETE FROM Playlist WHERE playlistId=?";
        try (Connection connect = DriverManager.getConnection(url);
             PreparedStatement stmt = connect.prepareStatement(sql)) {
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