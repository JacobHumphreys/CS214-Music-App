package ui.menus;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import internal.data.DataBase;
import internal.files.FileManager;
import ui.widgets.MultipleOptionSelect;

public class SongRecMenu extends Menu {
    private MultipleOptionSelect selectionPrompt;
    private List<String> songNames;
    private String outputPath;
    private File inputFile;

    public SongRecMenu(String outputPath, File inputFile) {
        this.state = MenuState.UNOPENED;
        this.outputPath = outputPath;
        this.inputFile = inputFile;

        var ids = DataBase.getAllSongIDs();
        this.songNames = Arrays.stream(ids)
                .map(id -> DataBase.getSong(id).get())
                .map(song -> song.getName())
                .collect(Collectors.toList());
        songNames.sort(Comparator.naturalOrder());

        this.selectionPrompt = new MultipleOptionSelect(
                songNames.toArray(String[]::new),
                "Enter selections (e.g. 2,5,7)");
    }

    @Override
    public void render() {
        state = MenuState.OPEN;
        selectionPrompt.render();
    }

    @Override
    public Menu input() throws IOException {
        try {
            Integer[] numbers = selectionPrompt.getSelection();
            selectionPrompt.validateSelection(numbers);
            var fileNames = getReccomendFileNames(numbers);
            FileManager.outputSongReccomendationsToCSV(outputPath, fileNames);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return new FileOptionSelectMenu(inputFile);
        }

        DataBase.clearDataBase();
        System.out.println("Output written to: " + outputPath);
        return new FileOptionSelectMenu(inputFile);
    }

    /**
     * @return
     */
    private String[] getReccomendFileNames(Integer[] numbers) throws IOException {
        return Arrays.stream(numbers).map(selectIndex -> songNames.get(selectIndex - 1)).toArray(String[]::new);
    }
}
