import java.io.IOException;
import java.util.Arrays;

import internal.files.FileManager;
import ui.InteractiveApp;

/*
 * Project Entry Point
 */
public class Cs214Project {
    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("-i")) {
            InteractiveApp.init();
            InteractiveApp.run();
            return;
        }

        String inputPath = args.length >= 2 ? args[0] : null;
        if (inputPath == null) {
            System.err.println("Error: Must Provide Input And Output Path");
            return;
        }

        try {
            FileManager.loadDataFromCSV(inputPath);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return;
        }

        String outputPath = args[1];
        String[] flags = Arrays.copyOfRange(args, 2, args.length);
        try {
            handleFlags(outputPath, flags);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            return;
        }
    }

    /**
     * Used to handle branching flag argument paths without deep nesting in main
     *
     * @param outputPath path of output csv
     * @param flags      the array of flags formatted as hyphen letter
     * @throws IOException in the event of an error from the csv parser or unknown
     *                     flag
     */
    private static void handleFlags(String outputPath, String[] flags) throws IOException {
        if (flags.length == 0) {
            FileManager.outputSongDataToCSV(outputPath);
            return;
        }
        switch (flags[0]) {
            case "-u":
                FileManager.outputUserDataToCSV(outputPath);
                break;
            case "-p":
                FileManager.outputPredictedUserRatingsToCSV(outputPath);
                break;
            case "-r":
                if (flags.length == 1) {
                    throw new IOException("must select at lease one song for recommendations");
                }
                String[] songNames = Arrays.copyOfRange(flags, 1, flags.length);
                FileManager.outputSongReccomendationsToCSV(outputPath, songNames);
                break;
            default:
                throw new IOException(String.format("unsupported argument '%s'", flags[0]));
        }
    }
}
