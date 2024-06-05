package repository.database;

import domain.Artist;
import domain.Song;
import exception.*;
import repository.ArtistRepository;
import repository.SongRepository;
import java.sql.*;
import java.util.*;
import java.util.stream.Stream;

public class DatabaseSongRepository implements SongRepository {

    private long nextSongId = 1;
    String url = "jdbc:mysql://localhost:3306/AppProjectDB";

    private final Map<String, Song> repo;
    public ArtistRepository repository;

    public DatabaseSongRepository() {
        this.repo = new TreeMap<>();
        if (repo.isEmpty()) {
            var id = String.format("S%011d", nextSongId);
            String sql = "SELECT songId, songName, artistId FROM Song";
            try (Connection connect = DriverManager.getConnection(url);
                 PreparedStatement stmt = connect.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Artist artist = repository.retrieve(rs.getString("artistId"));
                    Song song = new Song(rs.getString("songId"), rs.getString("songName"), artist);
                    repo.put(id, song);
                    ++nextSongId;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Song retrieve(String songId) {
        return repo.get(songId);
    }

    @Override
    public Song create(Artist artist, String songName) throws ArtistNotFoundException, InvalidInputException {
        var id = String.format("S%011d", nextSongId);
        if (repo.containsKey(id)) throw new InvalidInputException("Id already exsisted, please try again.");
        Song song = new Song(id, songName, artist);
        String sql = "INSERT INTO Song(songId, songName, artistId) values (?, ?, ?)";
        try (Connection connect = DriverManager.getConnection(url);
             PreparedStatement stmt = connect.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, songName);
            stmt.setString(3, artist.getId());
            stmt.executeUpdate();
            repo.put(id, song);
            ++nextSongId;
            return song;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(Song song) throws SongNotFoundException {
        if (song == null) throw new SongNotFoundException("Can not find this artist, please try again.");
        String sql = "UPDATE Song SET songName=?";
        try (Connection connect = DriverManager.getConnection(url);
             PreparedStatement stmt = connect.prepareStatement(sql)) {
            stmt.setString(1, song.getTitle());
            stmt.executeUpdate();
            repo.replace(song.getSongId(), song);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Artist artist, Song song) throws SongNotFoundException, ArtistNotFoundException {
        if (artist == null) throw new ArtistNotFoundException("Can not find this artist, please try again.");
        if (song == null) throw new SongNotFoundException("Can not find this song, please try again.");
        String sql = "DELETE FROM Song WHERE songId=?";
        try (Connection connect = DriverManager.getConnection(url);
             PreparedStatement stmt = connect.prepareStatement(sql)) {
            stmt.setString(1, song.getSongId());
            stmt.executeUpdate();
            return repo.remove(song.getSongId(), song);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Stream<Song> stream() {
        return repo.values().stream();
    }
}
