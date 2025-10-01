package ui.menus;

import java.io.File;
import java.io.IOException;

import ui.widgets.OptionSelect;

public class DirectoryOptionsMenu extends Menu {
    private OptionSelect select;
    private File directory;

    public DirectoryOptionsMenu(File directory) {
        super();
        this.select = new OptionSelect(new String[] { "Select File", "Create File", "Return" }, "Select an option");
        state = MenuState.UNOPENED;
        this.directory = directory;
    }

    @Override
    public void render() {
        state = MenuState.OPEN;
        select.render();
    }

    @Override
    public Menu input() throws IOException {
        int selection;
        try {
            selection = select.getSelection();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return new DirectoryOptionsMenu(directory);
        }

        try {
            select.validateSelection(selection);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            state = MenuState.UNOPENED;
        }

        switch (selection) {
            case 1:
                return new FileSelectMenu(directory);
            case 2:
                return new FileCreationMenu(directory);
            case 3:
                state = MenuState.CLOSED;
                return new MainMenu();
        }

        return this;
    }

}
