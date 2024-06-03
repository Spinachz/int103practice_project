/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import domain.Playlist;
import domain.User;
import exception.InvalidInputException;
import exception.PlaylistNotFoundException;
import exception.UserNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import service.UserService;

/**
 *
 * @author User
 */
public class PlaylistUI extends UserUI {

    protected long totalPlaylist = 0;

    public PlaylistUI() {}

//    protected void uiPlaylistMenu(Scanner sc, User user) {
//        String prompt = """
//                        Please select:
//                        1. Create playlist
//                        2. Delete playlist
//                        3. Select playlist
//                        4. Return to user menu
//                        Press [1|2|3|4]: """;
//        System.out.print(prompt);
//        while (true) {
//            String input = getUserInput(sc, prompt);
//            Scanner ans = new Scanner(input);
//            if (ans.hasNext("[1|2|3|4]")) {
//                int i = ans.nextInt();
//                switch (i) {
//                    case 1 -> {
//                        createPlaylist(sc, user);
//                    }
//                    case 2 -> {
//                        if (totalPlaylist != 0) {
//                            deletePlaylist(sc, user);
//                        }
//                        System.out.println("You don't have any playlist to delete.");
//                        continue;
//                    }
//                    case 3 -> {
//                        if (totalPlaylist != 0) {
//                            selectPlaylist(sc, user);
//                        }
//                        System.out.println("You don't have any playlist to select.");
//                        continue;
//                    }
//                    case 4 -> {
//                        startUserUI();
//                    }
//                }
//                break;
//            } else {
//                System.out.println("Invalid input");
//                System.out.print(prompt);
//            }
//        }
//    }
//
//    private void createPlaylist(Scanner sc, User user) {
//        Playlist playlist;
//        String prompt = """
//                        You are in creating playlist process.
//                        Please type your playlist name or press [q] to return to view user menu: 
//                        """;
//        System.out.print(prompt);
//        while (true) {
//            String input = getUserInput(sc, prompt);
//            if (input.equalsIgnoreCase("q")) {
//                uiViewUser(sc, user);
//            } else {
//                try {
//                    playlist = userService.createPlaylist(user.getUserId(), input);
//                    totalPlaylist++;
//                    System.out.println("Create playlist success!");
//                    System.out.println("This is your new playlist: " + playlist.getPlaylistName());
//                    viewPlaylistOrQuit(sc, playlist, user);
//                } catch (UserNotFoundException  | InvalidInputException ex) {
//                    System.out.println(ex.getMessage());
//                    uiViewUser(sc, user);
//                }
//            }
//            break;
//        }
//    }
//
//    protected void viewPlaylistOrQuit(Scanner sc, Playlist playlist, User user) { //can be merge with createPlaylist()
//        String prompt = "Please press [v] to view your playlist or [q] to return to view user menu: ";
//        System.out.print(prompt);
//        while (true) {
//            String input = getUserInput(sc, prompt);
//            if (input.equalsIgnoreCase("v")) {
//                uiPlaylist(sc, playlist, user);
//            } else if (input.equalsIgnoreCase("q")) {
//                uiViewUser(sc, user);
//            } else {
//                System.out.println("Invalid input, please try again");
//                System.out.println(prompt);
//                continue;
//            }
//            break;
//        }
//    }
//
//    private void uiPlaylist(Scanner sc, Playlist playlist, User user) {
//        System.out.println("Playlist id: " + playlist.getPlaylistId());
//        System.out.println("Playlist name: " + playlist.getPlaylistName());
//        System.out.println("Total song: " + playlist.getCount());
//        if (playlist.getCount() != 0) {
//            playlist.getAllSong().forEach(System.out::println);
//        }
//        String prompt = """
//                        Please select:
//                        1. add song
//                        2. delete song
//                        3. return to view user menu
//                        Press [1|2|3]:
//                        """;
//        System.out.println(prompt);
//        while (true) {
//            String input = getUserInput(sc, prompt);
//            Scanner ans = new Scanner(input);
//            if (ans.hasNext("[1|2|3]")) {
//                int i = ans.nextInt();
//                switch (i) {
//                    case 1 -> {
//                        addSong(sc, user);
//                    }
//                    case 2 -> {
//                        if (playlist.getCount() != 0) {
//                            deleteSong(sc, playlist, user);
//                        }
//                        System.out.println("You don't have any song to delete.");
//                        continue;
//                    }
//                    case 3 -> {
//                        uiViewUser(sc, user);
//                    }
//                }
//                break;
//            } else {
//                System.out.println("Invalid input");
//                System.out.print(prompt);
//            }
//        }
//    }
//
//    private void deletePlaylist(Scanner sc, User user) {
//        Playlist selectedPlaylist = selectPlaylist(sc, user);
//        if (selectedPlaylist != null) {
//            try {
//                userService.deletePlaylist(user.getUserId(), selectedPlaylist);
//                System.out.println("Delete playlist success!");
//                uiViewUser(sc, user);
//            } catch (UserNotFoundException | InvalidInputException | PlaylistNotFoundException ex) {
//                ex.getMessage();
//            }
//        } else {
//            System.out.println("Can not find this playlist, please try again.");
//        }
//
//    }
//
//    private Playlist selectPlaylist(Scanner sc, User user) {
//        Stream playlists = null;
//        try {
//            playlists = userService.listAllPlaylistByUser(user.getUserId());
//        } catch (UserNotFoundException ex) {
//            ex.getMessage();
//        }
//        if (playlists != null) {
//            playlists.forEach(System.out::println);
//            String prompt = "Please type your playlist id that shown above or press [q] to return to view user menu:";
//            System.out.print(prompt);
//            while (true) {
//                String input = getUserInput(sc, prompt);
//                Playlist selectedPlaylist = userService.getPlaylist(input);
//                if (selectedPlaylist != null) {
//                    return selectedPlaylist;
//                } else if (input.equalsIgnoreCase("q")) {
//                    uiViewUser(sc, user);
//                } else {
//                    System.out.println("Invalid input, please try again");
//                    System.out.println(prompt);
//                }
//                break;
//            }
//        }
//        return null;
//    }
//
//    private void addSong(Scanner sc, User user) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
//    }
//
//    private void deleteSong(Scanner sc, Playlist playlist, User user) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
//    }
}
