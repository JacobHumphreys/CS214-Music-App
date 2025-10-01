package ui.menus;

import java.io.File;
import java.io.IOException;
import ui.widgets.OptionSelect;

public class FileOptionSelectMenu extends Menu {
    private OptionSelect optionSelect;
    private File file;

    public FileOptionSelectMenu(File file) {
        super();
        this.file = file;
        optionSelect = new OptionSelect(new String[] { "Song Stats",
                "User Similarity",
                "User Prediction",
                "User Recommendation",
                "Edit File",
                "Return" }, "Select an option");
    }

    @Override
    public void render() {
        state = MenuState.OPEN;
        optionSelect.render();
    }

    @Override
    public Menu input() throws IOException {
        int selection;
        try {
            selection = optionSelect.getSelection();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            state = MenuState.UNOPENED;
            return this;
        }

        try {
            optionSelect.validateSelection(selection);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            state = MenuState.UNOPENED;
            return this;
        }

        if (selection == 5) {
            return new FileEditMenu(file);
        }

        if (selection == 6) {
            return new DirectoryOptionsMenu(file.getParentFile());
        }

        return new FileOutputMenu(file, selection);
    }

}
