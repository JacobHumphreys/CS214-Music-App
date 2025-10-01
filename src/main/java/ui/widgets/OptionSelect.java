package ui.widgets;

import java.io.IOException;
import java.util.NoSuchElementException;

import ui.InteractiveApp;

public final class OptionSelect implements Widget {
    private String[] options;
    String selectMessage;

    public OptionSelect(String[] options, String selectMessage) {
        this.options = options;
        this.selectMessage = selectMessage;
    }

    public OptionSelect(String[] options) {
        this(options, "Select an Option");
    }

    public void render() {
        int i = 1;
        for (String option : options) {
            System.out.println(i + " - " + option);
            i++;
        }
        System.out.println("");
        System.out.print(selectMessage + ": ");
    }

    public int getSelection() throws Exception {
        int output;
        try {
            String rawOutput = InteractiveApp.getInputScanner().nextLine();
            rawOutput.replaceAll("\n", "").replaceAll(" ", "");
            output = Integer.valueOf(rawOutput);
        } catch (NoSuchElementException e) {
            if (e.getMessage() != null) {
                System.out.println("Error: invalid input for multiple choice");
            }
            System.exit(1);
            return -1;
        }
        return output;
    }

    public void validateSelection(int selection) throws IOException {
        if (selection > options.length) {
            throw new IOException("Invalid option chosen: " + selection);
        }
    }

}
