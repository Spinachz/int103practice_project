package repository.database;

import domain.Artist;
import domain.Song;
import exception.*;
import repository.ArtistRepository;
import repository.SongRepository;
import service.ArtistService;

import java.sql.*;
import java.util.*;
import java.util.stream.Stream;

public class DatabaseSongRepository implements SongRepository {
    private long nextSongId = 1;
    Connection connect = DatabaseConnection.connect();
    private final Map<String, Song> repo;

    public DatabaseSongRepository() {
        this.repo = new TreeMap<>();
        if (repo.isEmpty()) {
            var id = String.format("S%011d", nextSongId);
            try (PreparedStatement stmt = connect.prepareStatement("SELECT s.songId, s.title, s.artistId, a.artistName FROM song s join artist a WHERE s.artistId = a.artistId")) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Artist artist = new Artist(rs.getString(3), rs.getString(4));
                    Song song = new Song(rs.getString(1), rs.getString(2), artist);
                    repo.put(rs.getString("songId"), song);
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
        try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO song (songId, title, artistId) VALUES (?, ?, ?)")) {
            stmt.setString(1, id);
            stmt.setString(2, songName);
            stmt.setString(3, artist.getId());
            stmt.executeUpdate();
            repo.put(song.getSongId(), song);
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
        try (PreparedStatement stmt = connect.prepareStatement("UPDATE song SET title=?")) {
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
        try (PreparedStatement stmt = connect.prepareStatement("DELETE FROM song WHERE songId=?")) {
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