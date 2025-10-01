package ui.widgets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import ui.InteractiveApp;

public class MultipleOptionSelect implements Widget {
    private OptionSelect singleOptionSelect;
    private String[] options;

    public MultipleOptionSelect(String[] options) {
        this(options, "Select an option(s): ");
    }

    public MultipleOptionSelect(String[] options, String Prompt) {
        singleOptionSelect = new OptionSelect(options, Prompt);
        this.options = options;
    }

    @Override
    public void render() {
        singleOptionSelect.render();
    }

    public Integer[] getSelection() throws Exception {
        Integer[] output;
        try {
            String rawOutput = InteractiveApp.getInputScanner().nextLine();
            rawOutput = rawOutput.replaceAll("\n", "").replaceAll(" ", "");
            output = parsePromptIndexes(rawOutput);
        } catch (NoSuchElementException e) {
            if (e.getMessage() != null) {
                System.out.println("Error: invalid input for multiple choice");
            }
            System.exit(1);
            return new Integer[] {};
        }

        return output;
    }

    public void validateSelection(Integer[] selection) throws IOException {
        for (Integer index : selection) {
            if (index > options.length) {
                throw new IOException("Invalid option chosen: " + selection);
            }
        }
    }

    private Integer[] parsePromptIndexes(String answer) throws IOException {
        var nextComma = answer.indexOf(",");
        ArrayList<Integer> indexes = new ArrayList<>();
        while (nextComma != -1) {
            String number = answer.substring(0, nextComma);
            try {
                indexes.add(Integer.valueOf(number));
            } catch (NumberFormatException e) {
                throw new IOException("Invalid input: " + number);
            }
            answer = answer.substring(nextComma + 1);
            nextComma = answer.indexOf(",");
            if (nextComma == answer.length() - 1) {
                answer = answer.substring(0, answer.length() - 1);
                break;
            }
        }
        try {
            indexes.add(Integer.valueOf(answer));
        } catch (NumberFormatException e) {
            throw new IOException("Invalid input: " + answer);
        }
        return indexes.stream().toArray(Integer[]::new);
    }

}
