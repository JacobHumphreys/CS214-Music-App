package ui.menus;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import internal.data.DataBase;
import internal.files.FileManager;
import internal.files.FileManager.EmptyFileException;
import ui.widgets.OptionSelect;
import ui.widgets.TextPrompt;

public class FileEditMenu extends Menu {

    private File file;
    private UUID[] users, songs;

    private OptionSelect submenuSelect;

    public FileEditMenu(File file) {
        super();
        this.file = file;
        submenuSelect = new OptionSelect(
                new String[] { "Select a User", "Select a Song", "Change Rating", "Export Changes", "Return" });
        try {
            FileManager.loadDataFromCSV(file.getPath());
        } catch (EmptyFileException e) {
        } catch (IOException e) {
            System.out.println("Fatal error");
            state = MenuState.CLOSED;
            return;
        }
    }

    @Override
    public void render() {
        state = MenuState.OPEN;
        if (songs != null && users != null) {
            renderUserSongSelection();
        }
        submenuSelect.render();

    }

    private void renderUserSongSelection() {
        System.out.print("\nSelected Song(s): ");
        if (songs == null) {
            System.out.println("NONE");
        } else {
            Arrays.stream(songs)
                    .map(id -> DataBase.getSong(id).get().getName())
                    .forEach(name -> System.out.printf("%s ", name));
            System.out.println();
        }
        System.out.print("Selected User(s): ");
        if (users == null) {
            System.out.println("NONE");
        } else {
            Arrays.stream(users)
                    .map(id -> DataBase.getUser(id).get().getName())
                    .forEach(name -> System.out.printf("%s ", name));
            System.out.println();
        }
    }

    @Override
    public Menu input() throws IOException {
        state = MenuState.UNOPENED;

        int selection;
        try {
            selection = submenuSelect.getSelection();
            submenuSelect.validateSelection(selection);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return this;
        }
        try {
            return handleSelection(selection);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return this;
    }

    private Menu handleSelection(int selection) throws IOException {
        switch (selection) {
            case 1:
                return new UserSelectMenu(this);
            case 2:
                return new SongSelectMenu(this);
            case 3:
                validateStateForEditing();
                return new RatingEditMenu(users, songs, this);
            case 4:
                try {
                    promptOutput();
                } catch (Exception e) {
                    throw new IOException(e.getMessage());
                }
                return this;
            case 5:
                DataBase.clearDataBase();
                return new FileOptionSelectMenu(file);
        }
        return this;
    }

    private void validateStateForEditing() throws IOException {
        if (songs == null || users == null) {
            throw new IOException("User And Song Must Be Selected");
        } else if (songs.length > 1 && users.length > 1) {
            throw new IOException("Multiple Select Is Only Supported For Either Users or Songs, not both");
        }
    }

    private void promptOutput() throws Exception {
        TextPrompt outputPrompt = new TextPrompt("Enter Output Path");
        outputPrompt.render();
        String outputPath = outputPrompt.getAnswer();
        FileManager.outputDataBaseToCSV(outputPath);
    }

    public void setUsers(UUID[] users) {
        this.users = users;
    }

    public void setSongs(UUID[] songs) {
        this.songs = songs;
    }
}
