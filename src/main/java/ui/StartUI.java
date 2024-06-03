/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import java.util.Scanner;

/**
 *
 * @author User
 */
public class StartUI {
    
    public void start() {
        Scanner sc = new Scanner(System.in);
        uiMainMenu(sc);
    }
    
    //uiSelectRepotype();
    //log in with exsisted account(file, database only)
        // -> add password fields
    
    protected void uiMainMenu(Scanner sc) {
        String prompt = """
                    Hello, welcome to our application. Please select your accout's type:
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
                        UserUI userUi = new UserUI();
                        userUi.startUserUI();
                    }
                    case 2 -> {
                        ArtistUI artistUi = new ArtistUI();
                        artistUi.startArtistUI();
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
