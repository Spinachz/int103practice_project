package ui;

import domain.Playlist;
import domain.User;
import exception.InvalidInputException;
import exception.PlaylistNotFoundException;
import exception.UserNotFoundException;
import java.util.Scanner;
import java.util.stream.Stream;
import service.*;
import repository.memory.*;

public class UserUI extends StartUI {

    protected final UserService userService;

    public UserUI() {
        /* if fromFile is true , load/save customer info into file */
        userService = new UserService(new MemoryUserRepository(), new MemoryPlaylistRepository(), new MemorySongRepository());
    }

    public void startUserUI() {
        Scanner sc = new Scanner(System.in);
        userMainMenu(sc);
    }

    protected void userMainMenu(Scanner sc) {
        String prompt = """
                    Hello, you are in normal user mode. Please select:
                        1. Create account
                        2. NULL
                        3. Return to main menu
                    Press [1|2|3]: """;
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            Scanner ans = new Scanner(input);
            if (ans.hasNext("[1|2|3]")) {
                int i = ans.nextInt();
                switch (i) {
                    case 1 -> {
                        uiNewUser(sc);
                    }
                    case 2 -> {
                        System.out.println("NULL");
                    }
                    case 3 -> {
                        start();
                    }
                }
                break;
            } else {
                System.out.println("Invalid input");
                System.out.print(prompt);
            }
        }
    }

    protected void uiNewUser(Scanner sc) {
        String prompt = """
                You are in normal account sign up process. 
                Please enter your user name or press q to return to user menu: """;
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            if (input.equalsIgnoreCase("q")) {
                userMainMenu(sc);
                break;
            } else if (!input.isBlank()) {
                signUpProcess(sc, input);
                break;
            } else {
                System.out.println("Invalid input, please try again");
                System.out.print(prompt);
            }
        }
    }

    protected void signUpProcess(Scanner sc, String userName) {
        User user = null;
        try {
            user = userService.signUpUser(userName);
            System.out.println("Sign up success!");
            System.out.println("Your username is: " + userName);
        } catch (InvalidInputException ex) {
            System.out.println(ex.getMessage());
            userMainMenu(sc);
        }
        viewAccountOrQuit(sc, user);
    }

    protected void viewAccountOrQuit(Scanner sc, User user) { //can be merge with signUpProcess()
        String prompt = "Please press [v] to view your account or [q] to return to the user menu: ";
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            if (input.equalsIgnoreCase("v")) {
                uiViewUser(sc, user);
                break;
            } else if (input.equalsIgnoreCase("q")) {
                userMainMenu(sc);
                break;
            } else {
                System.out.println("Invalid input, please try again");
                System.out.println(prompt);
            }
        }
    }

    protected void uiViewUser(Scanner sc, User user) {
        System.out.println("Yoy are in view user page");
        System.out.println("User id: " + user.getId());
        System.out.println("Username:" + user.getName());
        int totalPlaylist = 0;
        try {
            totalPlaylist = (int) userService.countAllPlaylistById(user.getId());
        } catch (UserNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println("Your total playlist: " + totalPlaylist);
        if (totalPlaylist != 0) {
            try {
                userService.listAllPlaylistByUser(user.getId()).forEach(System.out::println);
            } catch (UserNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        }
        uiPlaylistMenu(sc, user, totalPlaylist);
    }
    
    protected void uiPlaylistMenu(Scanner sc, User user, int totalPlaylist) {
        String prompt = String.format("User id: %s\nUser name: %s\nYour total playlist: %d\nPlease select:\n1. Create playlist\n2. Delete playlist\n3. Select playlist\n4. Return to user menu\nPress [1|2|3|4]: "
                                                                , user.getId(), user.getName(), totalPlaylist);
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            Scanner ans = new Scanner(input);
            if (ans.hasNext("[1|2|3|4]")) {
                int i = ans.nextInt();
                switch (i) {
                    case 1 -> {
                        createPlaylist(sc, user);
                    }
                    case 2 -> {
                        deletePlaylist(sc, user);
                    }
                    case 3 -> {
                        handleSelectedPlaylist(sc, user);
                    }
                    case 4 -> {
                        userMainMenu(sc);
                    }
                }
                break;
            } else {
                System.out.println("Invalid input");
                System.out.print(prompt);
            }
        }
    }
    
    private void createPlaylist(Scanner sc, User user) {
        Playlist playlist;
        String prompt = """
                        You are in creating playlist process.
                        Please type your playlist name or press [q] to return to view user menu: 
                        """;
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            if (input.equalsIgnoreCase("q")) {
                uiViewUser(sc, user);
            } else {
                try {
                    playlist = userService.createPlaylist(user.getId(), input);
                    System.out.println("Create playlist success!");
                    System.out.println("This is your new playlist: " + playlist.getPlaylistName());
                    viewPlaylistOrQuit(sc, playlist, user);
                } catch (UserNotFoundException | InvalidInputException ex) {
                    System.out.println(ex.getMessage());
                    uiViewUser(sc, user);
                }
            }
            break;
        }
    }

    protected void viewPlaylistOrQuit(Scanner sc, Playlist playlist, User user) { //can be merge with createPlaylist()
        String prompt = "Please press [v] to view your playlist or [q] to return to view user menu: ";
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            if (input.equalsIgnoreCase("v")) {
                uiViewPlaylist(sc, playlist, user);
                return;
            } else if (input.equalsIgnoreCase("q")) {
                uiViewUser(sc, user);
                return;
            } else {
                System.out.println("Invalid input, please try again");
                System.out.println(prompt);
            }
        }
    }



    private void uiViewPlaylist(Scanner sc, Playlist playlist, User user) {
        System.out.println("Playlist id: " + playlist.getPlaylistId());
        System.out.println("Playlist name: " + playlist.getPlaylistName());
        System.out.println("Total song: " + playlist.getCount());
        if (playlist.getCount() != 0) {
            playlist.getAllSong().forEach(System.out::println);
        }
        String prompt = """
                        Please select:
                        1. add song
                        2. delete song
                        3. return to view user menu
                        Press [1|2|3]:""";
        System.out.println(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            Scanner ans = new Scanner(input);
            if (ans.hasNext("[1|2|3]")) {
                int i = ans.nextInt();
                switch (i) {
                    case 1 -> {
                        addSong(sc, user, playlist);
                    }
                    case 2 -> {
                        deleteSong(sc, playlist, user);
                    }
                    case 3 -> {
                        uiViewUser(sc, user);
                    }
                }
                break;
            } else {
                System.out.println("Invalid input");
                System.out.print(prompt);
            }
        }
    }

    private void deletePlaylist(Scanner sc, User user) {
        Playlist selectedPlaylist = null;
        try {
            selectedPlaylist = selectPlaylist(sc, user);
        } catch (PlaylistNotFoundException | UserNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        if (selectedPlaylist != null) {
            try {
                userService.deletePlaylist(user.getId(), selectedPlaylist);
                System.out.println("Delete playlist success!");
                uiViewUser(sc, user);
            } catch (UserNotFoundException | InvalidInputException | PlaylistNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            uiViewUser(sc, user);
        }
    }

    private Playlist selectPlaylist(Scanner sc, User user) throws PlaylistNotFoundException, UserNotFoundException {
        Stream playlists = null;
        try {
            playlists = userService.listAllPlaylistByUser(user.getId());
        } catch (UserNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        if (userService.countAllPlaylistById(user.getId()) != 0) {
            playlists.forEach(System.out::println);
            String prompt = """
                            You are in selecting playlist process. 
                            Please type your playlist id to perform next action,
                            or press[q] to return to view user menu:""";
            System.out.print(prompt);
            while (true) {
                String input = getInput(sc, prompt);
                Playlist selectedPlaylist = userService.getPlaylist(input);
                if (selectedPlaylist != null) {
                    return selectedPlaylist;
                } else if (input.equalsIgnoreCase("q")) {
                    uiViewUser(sc, user);
                } else {
                    System.out.println("Can not find this playlist, please try again.");
                    System.out.println(prompt);
                    continue;
                }
                break;
            }
        }
        System.out.println("Can not find your playlist, please try again.");
        return null;
    }

    private void handleSelectedPlaylist(Scanner sc, User user) {
        Playlist selectedPlaylist = null;
        try {
            selectedPlaylist = selectPlaylist(sc, user);
        } catch (PlaylistNotFoundException | UserNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        if (selectedPlaylist == null) {
            uiViewUser(sc, user);
            return;
        }
        viewPlaylistOrQuit(sc, selectedPlaylist, user);
    }

    private void addSong(Scanner sc, User user, Playlist playlist) {
    }

    private void deleteSong(Scanner sc, Playlist playlist, User user) {
    }

}
