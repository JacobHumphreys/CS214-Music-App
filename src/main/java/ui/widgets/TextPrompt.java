package ui.widgets;

import java.util.NoSuchElementException;

import ui.InteractiveApp;

public final class TextPrompt implements Widget {
    String prompt;

    public TextPrompt(String prompt) {
        this.prompt = prompt;
    }

    @Override
    public void render() {
        System.out.print(prompt + ": ");
    }

    public String getAnswer() throws Exception {
        String input;
        try {
            input = InteractiveApp.getInputScanner().nextLine();
        } catch (NoSuchElementException e) {
            if (e.getMessage() != null) {
                System.out.println("Error: invalid input for text prompt");
            }
            System.exit(1);
            return "";
        }
        return input;
    }
}
