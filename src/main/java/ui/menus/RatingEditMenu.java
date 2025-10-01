package ui.menus;

import java.io.IOException;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

import internal.data.DataBase;
import internal.data.DataBase.InvalidIDException;
import internal.types.Song;
import internal.types.Song.InvalidRatingException;
import internal.types.User;
import ui.widgets.OptionSelect;
import ui.widgets.TextPrompt;

public class RatingEditMenu extends Menu {
    private UUID[] users;
    private UUID[] songs;
    private FileEditMenu previous;
    private OptionSelect menuOptions;
    private TextPrompt ratingPrompt;

    public RatingEditMenu(UUID[] users, UUID[] songs, FileEditMenu previous) {
        super();
        this.users = users;
        this.songs = songs;
        this.previous = previous;
        menuOptions = new OptionSelect(new String[] { "Change Rating", "Return" });
        ratingPrompt = new TextPrompt("Enter New Rating");
    }

    @Override
    public void render() {
        state = MenuState.OPEN;
        System.out.println("\nEdit Rating\n");
        for (UUID userId : users) {
            for (UUID songId : songs) {
                Optional<Song> song = DataBase.getSong(songId);
                Optional<User> user = DataBase.getUser(userId);
                OptionalInt userRating = song.map(s -> s.getUserRating(userId)).orElseThrow();

                System.out.printf(
                        "SONG: %s USER: %s -> %s\n",
                        song.orElseThrow().getName(),
                        user.orElseThrow().getName(),
                        userRating.isPresent() ? String.valueOf(userRating.orElseThrow()) : "NONE");
            }
        }

        menuOptions.render();
    }

    @Override
    public Menu input() throws IOException {
        state = MenuState.UNOPENED;
        int selection;
        try {
            selection = menuOptions.getSelection();
            menuOptions.validateSelection(selection);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return this;
        }

        if (selection == 2) {
            return previous;
        }

        ratingPrompt.render();
        String answer;
        try {
            answer = ratingPrompt.getAnswer();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return this;
        }

        int newRating;
        try {
            newRating = Integer.valueOf(answer);
        } catch (Exception e) {
            System.out.println("Error: Invalid Input");
            return this;
        }

        try {
            for (UUID songId : songs) {
                for (UUID userId : users) {
                    DataBase.associateRatingToUserAndSong(songId, userId, newRating);
                }
            }
        } catch (InvalidRatingException e) {
            System.out.println("Error: Invalid Input");
            return this;
        } catch (InvalidIDException e) {
            System.out.println("Error: Unreachable Code");
            return this;
        }

        return this;
    }

}
