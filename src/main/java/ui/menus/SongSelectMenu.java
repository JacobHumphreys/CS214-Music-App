package ui.menus;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import internal.data.DataBase;
import ui.widgets.MultipleOptionSelect;
import ui.widgets.TextPrompt;

public class SongSelectMenu extends Menu {
    private FileEditMenu previous;
    private MultipleOptionSelect songSelectPrompt;
    private TextPrompt newSongPrompt;
    private String[] songNames;

    public SongSelectMenu(FileEditMenu previous) {
        super();
        this.previous = previous;

        songNames = Arrays.stream(DataBase.getAllSongIDs())
                .map(id -> DataBase.getSong(id).get().getName())
                .toArray(String[]::new);
        Arrays.sort(songNames);

        LinkedList<String> songSelectOptions = new LinkedList<String>(Arrays.asList(songNames));
        songSelectOptions.addAll(List.of("Add Song", "Return"));

        songSelectPrompt = new MultipleOptionSelect(songSelectOptions.toArray(String[]::new), "Select Song");
        newSongPrompt = new TextPrompt("Enter New Song Name");
    }

    @Override
    public void render() {
        state = MenuState.OPEN;
        songSelectPrompt.render();
    }

    @Override
    public Menu input() throws IOException {
        state = MenuState.UNOPENED;

        Integer[] songSelection;
        try {
            songSelection = getSongSelection();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return this;
        }

        if (songSelection.length > 1) {
            if (Arrays.stream(songSelection).filter(s -> s > songNames.length).toArray().length >= 1) {
                System.out.println("Error: Invalid Index Chosen");
                return this;
            }
        } else {
            int selection = songSelection[0];
            if (selection == songNames.length + 1) {
                try {
                    addNewSong();
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
                return new SongSelectMenu(previous);
            } else if (selection == songNames.length + 2) {
                return previous;
            }
        }

        UUID[] songId = getSelectedIds(songSelection);
        previous.setSongs(songId);

        return previous;
    }

    private void addNewSong() throws Exception {
        newSongPrompt.render();
        String newSongName = newSongPrompt.getAnswer();
        DataBase.addSong(newSongName);
    }

    private UUID[] getSelectedIds(Integer[] songSelections) throws IOException {
        List<UUID> songIds = new LinkedList<UUID>();
        for (Integer songSelection : songSelections) {
            String selectionName = songNames[songSelection - 1];
            Optional<UUID> songId = DataBase.getSongId(selectionName);
            if (songId.isEmpty()) {
                throw new IOException("Song Not Found");
            }

            songIds.add(songId.get());
        }
        return songIds.toArray(UUID[]::new);
    }

    private Integer[] getSongSelection() throws IOException {
        Integer[] songSelection;
        try {
            songSelection = songSelectPrompt.getSelection();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        songSelectPrompt.validateSelection(songSelection);
        return songSelection;
    }
}
