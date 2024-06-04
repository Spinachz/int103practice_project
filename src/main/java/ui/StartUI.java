/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import java.util.Scanner;
import repository.memory.MemorySongRepository;
import service.SongService;

/**
 *
 * @author User
 */
public class StartUI {

    UserUI userUi;
    ArtistUI artistUi;
    SongService songService;

      // -> add password fields
    
    public StartUI() {
    }

    public void start() {
        //uiSelectRepotype
        Scanner sc = new Scanner(System.in);
        sc.useDelimiter("\n");
        int repoType = uiSelectRepotype(sc);
        switch (repoType) {
            case 1 ->{
                songService = new SongService(new MemorySongRepository());
            }
            case 2 ->{
                songService = new SongService(null); //file
            }
            case 3 -> {
                songService = new SongService(null); //database
            }
        }
        userUi = new UserUI(songService, repoType);
        artistUi = new ArtistUI(songService, repoType);
        uiMainMenu(sc, songService);
    }

    protected int uiSelectRepotype(Scanner sc) {
        String prompt = """
                    Hello, welcome to our application. Please select Data managing method:
                        1. In memory
                        2. In file(under maintainance)
                        3. In database(under maintainance)
                    Press [1|2|3]: """;
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            Scanner ans = new Scanner(input);
            if (ans.hasNext("[1|2|3]")) {
                int userChoice = ans.nextInt();
                return userChoice;
            } else {
                System.out.println("Invalid input");
                System.out.print(prompt);
            }
        }
    }
    
    protected void uiMainMenu(Scanner sc, SongService songService) {
        String prompt = """
                    You are in main menu page, please select your accout's type:
                        1. Normal user account
                        2. Artist account(for publish your song)
                        3. Quit
                    Press [1|2|3]: """;
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            Scanner ans = new Scanner(input);
            if (ans.hasNext("[1|2|3]")) {
                int i = ans.nextInt();
                switch (i) {
                    case 1 -> {
                        userUi.startUserUI();
                        System.out.print(prompt);
                        continue;
                    }
                    case 2 -> {
                        artistUi.startArtistUI();
                        System.out.print(prompt);
                        continue;
                    }
                    case 3 -> {
                        System.out.println("Have a nice day!");
                    }
                }
                break;
            } else {
                System.out.println("Invalid input");
                System.out.print(prompt);
            }
        }
    }

    public String getInput(Scanner sc, String prompt) {
        String input = "";
        while (true) {
            input = sc.nextLine().trim();
            if (input.isBlank()) {
                System.out.println("Invalid input, please try again");
                System.out.print(prompt);
            } else {
                break;
            }
        }
        return input;
    }
}
