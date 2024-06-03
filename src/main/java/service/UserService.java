/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import domain.*;
import exception.*;
import java.util.stream.Stream;
import repository.PlaylistRepository;
import repository.UserRepository;
import repository.SongRepository;

/**
 *
 * @author User
 */
public class UserService {

    private final UserRepository userRepo;
    private final PlaylistRepository playlistRepo;
    protected final SongRepository songRepo;

    public UserService(UserRepository userRepo, PlaylistRepository playlistRepo, SongRepository songRepo) {
        this.userRepo = userRepo;
        this.playlistRepo = playlistRepo;
        this.songRepo = songRepo;
    }

    public User signUpUser(String userName) throws InvalidInputException {
        return userRepo.create(userName);
    }

    public boolean rename(String userId, String newUserName) throws InvalidInputException, UserNotFoundException {
        var user = userRepo.retrieve(userId);
        if (user == null) {
            throw new UserNotFoundException();
        }
        user.rename(newUserName);
        userRepo.update(user);
        return true;
    }

    public Playlist createPlaylist(String userId, String playlistName) throws UserNotFoundException, InvalidInputException {
        var user = userRepo.retrieve(userId);
        if (user == null) {
            throw new UserNotFoundException("Can not find this user, please try again.");
        }
        return playlistRepo.create(user, playlistName);
    }

    public boolean deletePlaylist(String userId, Playlist playlist) throws UserNotFoundException, InvalidInputException, PlaylistNotFoundException {
        var user = userRepo.retrieve(userId);
        if (user == null) {
            throw new UserNotFoundException("Can not find this user, please try again.");
        }
        return playlistRepo.delete(user, playlist);
    }

    public Stream listAllPlaylistByUser(String userId) throws UserNotFoundException {
        if (userId == null || userRepo.retrieve(userId) == null) {
            throw new UserNotFoundException("Can not find this user, please try again.");
        }
        return playlistRepo.stream().filter(pl -> pl.getOwner().getId().equals(userId));
    }
    
    public long countAllPlaylistById(String userId) throws UserNotFoundException {
        if (userId == null || userRepo.retrieve(userId) == null) {
            throw new UserNotFoundException("Can not find this user, please try again.");
        }
        return playlistRepo.stream().filter(pl -> pl.getOwner().getId().equals(userId)).count();
    }

    public Song addSong(Song song, String playlistId) throws PlaylistNotFoundException {
        var playlist = playlistRepo.retrieve(playlistId);
        if (playlist == null) {
            throw new PlaylistNotFoundException("Can not find this playlist, please try again.");
        }
        playlistRepo.update(playlist);
        return playlist.addSong(song);
    }

    public Song removeSong(int index, String playlistId) throws PlaylistNotFoundException, SongNotFoundException {
        var playlist = playlistRepo.retrieve(playlistId);
        if (playlist == null) {
            throw new PlaylistNotFoundException("Can not find this playlist, please try again.");
        }
        var song = playlist.getSongByIndex(index);
        if (song == null) {
            throw new SongNotFoundException("Can not find this song, please try again.");
        }
        playlist.removeSong(index);
        playlistRepo.update(playlist);
        return song;
    }

    public Stream searhSongByTitle(String title) {
        return songRepo.stream().filter(s -> s.getTitle().equals(title));
    }

    public Stream searhSongByArtist(String artistName) {
        return songRepo.stream().filter(s -> s.getArtist().getName().equals(artistName));
    }

    public User getUser(String userId) {
        return userRepo.retrieve(userId);
    }
    
        public Playlist getPlaylist(String playlistId) {
        return playlistRepo.retrieve(playlistId);
    }

}
