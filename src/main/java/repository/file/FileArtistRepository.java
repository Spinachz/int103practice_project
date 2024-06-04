package repository.file;

import domain.Artist;
import java.io.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import exception.ArtistNotFoundException;
import exception.InvalidInputException;
import repository.ArtistRepository;

public class FileArtistRepository implements ArtistRepository{
    private String filename = "artist.dat";
    private long nextArtistId;
    private Map<String, Artist> repo;
    private File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
    private File f = new File(tempDirectory + filename);

    public FileArtistRepository() {
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
            this.nextArtistId = 1;
            this.repo = new TreeMap<>();
        }
    }

    @Override
    public Artist retrieve(String artistId) {
        return repo.get(artistId);
    }

    @Override
    public Artist create(String artistName) throws InvalidInputException {
        var id = String.format("A%011d", nextArtistId);
        if (repo.containsKey(id)) {
            throw new InvalidInputException("Id already exsisted, please try again.");
        }
        Artist artist = new Artist(id, artistName);
        repo.put(id, artist);
        ++nextArtistId;
        try ( FileOutputStream fi = new FileOutputStream(f);
              BufferedOutputStream bfi = new BufferedOutputStream(fi);
              ObjectOutputStream obi = new ObjectOutputStream(bfi);) {
            obi.writeLong(nextArtistId);
            obi.writeObject(repo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return artist;
    }

    @Override
    public boolean update(Artist artist) throws ArtistNotFoundException {
        if (artist == null) {
            throw new ArtistNotFoundException("Can not find this artist, please try again.");
        }
        repo.replace(artist.getId(), artist);
        try ( FileOutputStream fi = new FileOutputStream(f);
              BufferedOutputStream bfi = new BufferedOutputStream(fi);
              ObjectOutputStream obi = new ObjectOutputStream(bfi);) {
            obi.writeLong(nextArtistId);
            obi.writeObject(repo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public Stream<Artist> stream() {
        return repo.values().stream();
    }
}
