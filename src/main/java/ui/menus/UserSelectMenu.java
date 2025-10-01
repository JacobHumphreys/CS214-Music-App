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

public class UserSelectMenu extends Menu {
    private FileEditMenu previous;
    private MultipleOptionSelect userSelectPrompt;
    private TextPrompt newUserPrompt;
    private String[] userNames;

    public UserSelectMenu(FileEditMenu previous) {
        super();
        this.previous = previous;

        userNames = Arrays.stream(DataBase.getAllUserIDs())
                .map(id -> DataBase.getUser(id).get().getName())
                .toArray(String[]::new);
        Arrays.sort(userNames);

        LinkedList<String> userSelectOptions = new LinkedList<String>(Arrays.asList(userNames));
        userSelectOptions.addAll(List.of("Add User", "Return"));

        userSelectPrompt = new MultipleOptionSelect(userSelectOptions.toArray(String[]::new), "Select User");
        newUserPrompt = new TextPrompt("Enter New User Name");
    }

    @Override
    public void render() {
        state = MenuState.OPEN;
        userSelectPrompt.render();
    }

    @Override
    public Menu input() throws IOException {
        state = MenuState.UNOPENED;

        Integer[] userSelection;
        try {
            userSelection = getUserSelection();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return this;
        }

        if (userSelection.length > 1) {
            if (Arrays.stream(userSelection).filter(s -> s > userNames.length).toArray().length >= 1) {
                System.out.println("Error: Invalid Index Chosen");
                return this;
            }
        } else {
            int selection = userSelection[0];
            if (selection == userNames.length + 1) {
                try {
                    addNewUser();
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
                return new UserSelectMenu(previous);
            } else if (selection == userNames.length + 2) {
                return previous;
            }
        }

        UUID[] userId = getSelectedIds(userSelection);
        previous.setUsers(userId);

        return previous;
    }

    private void addNewUser() throws Exception {
        newUserPrompt.render();
        String newUserName = newUserPrompt.getAnswer();
        DataBase.addUser(newUserName);
    }

    private UUID[] getSelectedIds(Integer[] userSelections) throws IOException {
        List<UUID> userIds = new LinkedList<UUID>();
        for (Integer userSelection : userSelections) {
            String selectionName = userNames[userSelection - 1];
            Optional<UUID> userId = DataBase.getUserId(selectionName);
            if (userId.isEmpty()) {
                throw new IOException("User Not Found");
            }

            userIds.add(userId.get());
        }
        return userIds.toArray(UUID[]::new);
    }

    private Integer[] getUserSelection() throws IOException {
        Integer[] userSelection;
        try {
            userSelection = userSelectPrompt.getSelection();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        userSelectPrompt.validateSelection(userSelection);
        return userSelection;
    }
}
