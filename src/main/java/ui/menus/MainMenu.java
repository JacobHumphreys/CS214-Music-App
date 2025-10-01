package ui.menus;

import java.io.IOException;

import ui.widgets.OptionSelect;

public class MainMenu extends Menu {
    private OptionSelect optionSelect;

    public MainMenu() {
        super();
        optionSelect = new OptionSelect(new String[] { "Load Folder", "Exit" }, "Select an option");
    }

    @Override
    public Menu input() throws IOException {
        int input;
        try {
            input = optionSelect.getSelection();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            state = MenuState.UNOPENED;
            return this;
        }

        try {
            optionSelect.validateSelection(input);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            state = MenuState.UNOPENED;
            return this;
        }

        switch (input) {
            case 1:
                state = MenuState.CLOSED;
                return new FolderSelectMenu();
            case 2:
                state = MenuState.CLOSED;
                break;
            default:
        }
        return this;
    }

    @Override
    public void render() {
        state = MenuState.OPEN;
        optionSelect.render();
    }
}
