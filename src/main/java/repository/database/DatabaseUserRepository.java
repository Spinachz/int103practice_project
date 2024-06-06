package repository.database;

import domain.Artist;
import domain.User;
import exception.ArtistNotFoundException;
import exception.InvalidInputException;
import exception.UserNotFoundException;
import repository.UserRepository;

import java.sql.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class DatabaseUserRepository implements UserRepository {
    private long nextUserId = 1;
    Connection connect = DatabaseConnection.connect();
    private final Map<String, User> repo;

    public DatabaseUserRepository() {
        this.repo = new TreeMap<>();
        if (repo.isEmpty()) {
            var id = String.format("U%011d", nextUserId);
            try (PreparedStatement stmt = connect.prepareStatement("SELECT * FROM User")) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    User user = new User(rs.getString("userId"), rs.getString("userName"));
                    repo.put(id, user);
                    ++nextUserId;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public User retrieve(String userId) {
        return repo.get(userId);
    }

    @Override
    public User create(String userName) throws InvalidInputException {
        var id = String.format("U%011d", nextUserId);
        if (repo.containsKey(id)) throw new InvalidInputException("Id already exsisted, please try again.");
        User user = new Artist(id, userName);
        try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO AppProjectDB.User (userId, userName) VALUES (?, ?)")) {
            stmt.setString(1, id);
            stmt.setString(2, userName);
            stmt.executeUpdate();
            repo.put(id, user);
            ++nextUserId;
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(User user) throws UserNotFoundException {
        if (user == null) throw new UserNotFoundException("Can not find this artist, please try again.");
        try (PreparedStatement stmt = connect.prepareStatement("UPDATE User SET userName=?")) {
            stmt.setString(1, user.getName());
            stmt.executeUpdate();
            repo.replace(user.getId(), user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Stream<User> stream() {
        return repo.values().stream();
    }
}