package repository.database;

import domain.Artist;
import exception.ArtistNotFoundException;
import exception.InvalidInputException;
import repository.ArtistRepository;

import java.sql.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class DatabaseArtistRepository implements ArtistRepository {
    private long nextArtistId = 1 ;
    String url = "jdbc:mysql://localhost:3306/AppProjectDB";

    private final Map<String, Artist> repo;

    public DatabaseArtistRepository() {
        this.repo = new TreeMap<>();
        if (repo.isEmpty()) {
            var id = String.format("A%011d", nextArtistId);
            try (Connection connect = DriverManager.getConnection(url);
                 PreparedStatement stmt = connect.prepareStatement("SELECT * FROM Artist")) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Artist artist = new Artist(rs.getString("artistId"), rs.getString("artistName"));
                    repo.put(id, artist);
                    ++nextArtistId;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Artist retrieve(String artistId) {
        return repo.get(artistId);
    }

    @Override
    public Artist create(String artistName) throws InvalidInputException {
        var id = String.format("A%011d", nextArtistId);
        if (repo.containsKey(id)) throw new InvalidInputException("Id already exsisted, please try again.");
        Artist artist = new Artist(id, artistName);
        String sql = "INSERT INTO Artist(artistId, artistName) values (?, ?)";
        try (Connection connect = DriverManager.getConnection(url);
             PreparedStatement stmt = connect.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, artistName);
            stmt.executeUpdate();
            repo.put(id, artist);
            ++nextArtistId;
            return artist;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(Artist artist) throws ArtistNotFoundException {
        if (artist == null) throw new ArtistNotFoundException("Can not find this artist, please try again.");
        String sql = "UPDATE Artist SET artistName=?";
        try (Connection connect = DriverManager.getConnection(url);
             PreparedStatement stmt = connect.prepareStatement(sql)) {
            stmt.setString(1, artist.getName());
            stmt.executeUpdate();
            repo.replace(artist.getId(), artist);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Stream<Artist> stream() {
        return repo.values().stream();
    }
}
