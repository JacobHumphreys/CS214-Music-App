package ui.menus;

import java.io.File;
import java.io.IOException;

import internal.files.FileManager;
import ui.widgets.TextPrompt;

public class FileCreationMenu extends Menu {
    private TextPrompt fileNamePrompt;
    private File directory;

    public FileCreationMenu(File directory) {
        super();
        this.directory = directory;
        fileNamePrompt = new TextPrompt("Enter New FileName");
    }

    @Override
    public void render() {
        state = MenuState.OPEN;
        fileNamePrompt.render();
    }

    @Override
    public Menu input() throws IOException {
        state = MenuState.UNOPENED;
        String path = directory.getPath();
        try {
            path += "/" + fileNamePrompt.getAnswer();
            System.out.println(path);
            FileManager.createNewCSVFile(path);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return this;
        }
        return new DirectoryOptionsMenu(directory);
    }

}
