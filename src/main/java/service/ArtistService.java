/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import domain.*;
import exception.*;
import java.util.stream.Stream;
import repository.SongRepository;
import repository.ArtistRepository;
import repository.PlaylistRepository;
import repository.UserRepository;

/**
 *
 * @author User
 */
public class ArtistService {

    private final ArtistRepository artistRepo;
    protected final SongRepository songRepo;

    public ArtistService(ArtistRepository artistRepo, SongRepository songRepo) {
        this.songRepo = songRepo;
        this.artistRepo = artistRepo;
    }

    public Artist signUpArtist(String artistName) throws InvalidInputException {
        return artistRepo.create(artistName);
    }

    public long countAllSongById(String artistId) throws ArtistNotFoundException {
        if (artistId == null || artistRepo.retrieve(artistId) == null) {
            throw new ArtistNotFoundException("Can not find this artist, please try again.");
        }
        return songRepo.stream().filter(s -> s.getArtist().getId().equals(artistId)).count();
    }

    public Song publishSong(String artistId, String songName) throws InvalidInputException, ArtistNotFoundException {
        var artist = artistRepo.retrieve(artistId);
        return songRepo.create(artist, songName);
    }

    public boolean deleteSong(String artistId, Song song) throws SongNotFoundException, ArtistNotFoundException {
        var artist = artistRepo.retrieve(artistId);
        return songRepo.delete(artist, song);
    }

    public String ChangeTitle(String newTitle, String songId) throws InvalidInputException, SongNotFoundException {
        var song = songRepo.retrieve(songId);
        song.setTitle(newTitle);
        songRepo.update(song);
        return newTitle;
    }

    public Stream listAllSongByArtist(String artistId) throws ArtistNotFoundException {
        if (artistId == null || artistRepo.retrieve(artistId) == null) {
            throw new ArtistNotFoundException("Can not find this artist, please try again.");
        }
        return songRepo.stream().filter(s -> s.getArtist().equals(artistRepo.retrieve(artistId)));
    }

    public User getArtist(String artistId) {
        return artistRepo.retrieve(artistId);
    }

    public Song getSong(String songId) {
        return songRepo.retrieve(songId);
    }

}
