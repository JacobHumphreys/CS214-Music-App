package ui.menus;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import ui.widgets.OptionSelect;

public class FileSelectMenu extends Menu {
    private OptionSelect optionSelect;
    private File[] files;

    public FileSelectMenu(File directory) {
        super();
        files = directory.listFiles();
        Arrays.sort(files);
        String[] fileNames = getFileNames();
        this.optionSelect = new OptionSelect(
                fileNames,
                "Select file number");
    }

    @Override
    public void render() {
        state = MenuState.OPEN;
        optionSelect.render();
    }

    private String[] getFileNames() {
        return Arrays.stream(this.files)
                .filter(file -> file.isFile())
                .map(file -> file.getName())
                .toArray(String[]::new);
    }

    @Override
    public Menu input() throws IOException {
        int input;
        try {
            input = optionSelect.getSelection();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return new FileSelectMenu(files[0].getParentFile());
        }
        try {
            optionSelect.validateSelection(input);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return new FileSelectMenu(files[0].getParentFile());
        }
        File selection = files[input - 1];
        return new FileOptionSelectMenu(selection);
    }
}
