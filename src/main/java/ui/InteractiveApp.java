package ui;

import java.io.IOException;
import java.util.Scanner;

import ui.menus.MainMenu;
import ui.menus.Menu;

public class InteractiveApp {
    private static AppState state;
    private static Menu currentMenu;
    private static Scanner inputScanner;

    /**
     * Initializes the app with the MainMenu class as the default menu
     */
    public static void init() {
        init(new MainMenu());
        System.out.println("Music Analyzer\n");
    }

    public static void init(Menu menu) {
        state = AppState.OPEN;
        inputScanner = new Scanner(System.in);
        currentMenu = menu;
    }

    /**
     * starts the app loop
     */
    public static void run() {
        while (state != AppState.CLOSED) {
            try {
                appLoop();
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
                break;
            }
        }
        inputScanner.close();
    }

    static void appLoop() throws IOException {
        switch (currentMenu.getState()) {
            case UNOPENED:
                currentMenu.render();
                break;
            case OPEN:
                currentMenu = currentMenu.input();
                break;
            case CLOSED:
                state = AppState.CLOSED;
                break;
            default:
                break;
        }
    }

    static AppState getState() {
        return state;
    }

    public static Scanner getInputScanner() {
        return inputScanner;
    }
}
