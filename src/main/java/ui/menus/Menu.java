package ui.menus;

import java.io.IOException;

public abstract class Menu {
    MenuState state;

    public Menu(){
        state = MenuState.UNOPENED;
        System.out.println();
    }

    public MenuState getState() {
        return state;
    }

    public abstract void render();

    public abstract Menu input() throws IOException;
}
