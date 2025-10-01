package ui.menus;

import java.io.File;
import java.io.IOException;

import internal.data.DataBase;
import internal.files.FileManager;
import internal.files.FileManager.EmptyFileException;
import ui.widgets.TextPrompt;

public class FileOutputMenu extends Menu {
    private TextPrompt outputPrompt;
    private File file;
    private int selection;

    public FileOutputMenu(File file, int selection) {
        super();
        outputPrompt = new TextPrompt("Enter output path");
        this.selection = selection;
        this.file = file;
    }

    @Override
    public void render() {
        state = MenuState.OPEN;
        outputPrompt.render();
    }

    @Override
    public Menu input() throws IOException {
        try {
            FileManager.loadDataFromCSV(file.getPath());
        } catch (EmptyFileException e) {
            System.out.println("Error: " + e.getMessage());
            return new FileOptionSelectMenu(file);
        }

        String outputPath;
        try {
            outputPath = outputPrompt.getAnswer();
            handleSelection(selection, outputPath);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            DataBase.clearDataBase();
            return new FileOptionSelectMenu(file);
        }
        if (state == MenuState.CLOSED) {
            return new SongRecMenu(outputPath, file);
        }
        DataBase.clearDataBase();
        System.out.println("Output written to: " + outputPath);
        return new FileOptionSelectMenu(file);
    }

    public void handleSelection(int selection, String outputPath) throws IOException {
        switch (selection) {
            case 1:
                FileManager.outputSongDataToCSV(outputPath);
                break;
            case 2:
                FileManager.outputUserDataToCSV(outputPath);
                break;
            case 3:
                FileManager.outputPredictedUserRatingsToCSV(outputPath);
                break;
            case 4:
                state = MenuState.CLOSED;
                return;
            default:
                break;
        }
    }
}
