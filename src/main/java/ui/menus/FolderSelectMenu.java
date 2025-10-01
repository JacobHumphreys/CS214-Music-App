package ui.menus;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import ui.widgets.TextPrompt;

public class FolderSelectMenu extends Menu {
    private TextPrompt textPrompt;

    public FolderSelectMenu() {
        super();
        state = MenuState.UNOPENED;
        this.textPrompt = new TextPrompt("Enter folder path");
    }

    @Override
    public void render() {
        state = MenuState.OPEN;
        textPrompt.render();
    }

    @Override
    public Menu input() throws IOException {
        String input;
        try {
            input = textPrompt.getAnswer();
        } catch (Exception e) {
            return this;
        }
        state = MenuState.CLOSED;
        try {
            File directory = validatePath(input);
            var dirFiles = Arrays.stream(directory.listFiles())
                    .filter(file -> !file.isDirectory())
                    .toArray();
            if (dirFiles.length == 0) {
                System.out.println("Error: No Files In Directory");
                return new MainMenu();
            }
            return new DirectoryOptionsMenu(directory);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            state = MenuState.UNOPENED;
            return this;
        }
    }

    private File validatePath(String input) throws IOException {
        File directory = new File(input);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IOException("No Directory Found: " + input);
        }
        return directory;
    }
}
