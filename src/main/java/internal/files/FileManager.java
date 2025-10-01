package internal.files;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import internal.data.DataBase;
import internal.data.DataOpperator;
import internal.data.DataBase.InvalidIDException;
import internal.types.Song;

public class FileManager {

    /**
     * Loads data from csv into database given they match the specification
     *
     * @param filePath path of csv input file
     * @throws IOException           in the event that the file cannot be openned or
     *                               formatted correctly
     * @throws NumberFormatException in the event that a number is not formatted as
     *                               an int
     */
    public static void loadDataFromCSV(String filePath) throws IOException, EmptyFileException {
        validateCSVPath(filePath);

        Reader reader = new FileReader(filePath);
        CSVParser csvParser = CSVParser.parse(reader, CSVFormat.DEFAULT);

        var records = csvParser.getRecords();
        if (records.size() == 0) {
            throw new EmptyFileException();
        }

        for (CSVRecord record : records) {
            parseInputRecord(record);
        }
    }

    private static void parseInputRecord(CSVRecord record) throws IOException {
        if (record.size() != 3) {
            throw new IOException("input csv must have 3 fields per line");
        }

        validateRecord(record);

        String songName = record.get(0);
        String userName = record.get(1);
        Integer userRating = Integer.valueOf(record.get(2));

        var songID = DataBase.addSong(songName);
        var userID = DataBase.addUser(userName);
        try {
            DataBase.associateRatingToUserAndSong(songID, userID, userRating);
        } catch (Song.InvalidRatingException e) {
            throw new IOException("User Rating Must Be A Number Within Range 1-5");
        } catch (InvalidIDException e) {
            System.err.println("Unreachable Code");
        }
    }

    private static void validateRecord(CSVRecord record) throws IOException {
        if (record.get(0).length() == 0) {
            throw new IOException("song title must be atleast of length 1");
        }

        if (record.get(1).length() == 0) {
            throw new IOException("user name must be atleast of length 1");
        }

        try {
            Integer.valueOf(record.get(2));
        } catch (Exception e) {
            throw new IOException("User Rating Must Be A Number Within Range 1-5");
        }
    }

    public static void outputDataBaseToCSV(String outputPath) throws IOException {
        validateCSVPath(outputPath);

        List<String> records = DataOpperator.getDataBaseRecords();

        FileWriter filewriter = new FileWriter(outputPath);
        for (var record : records) {
            filewriter.write(record + "\n");
        }

        filewriter.close();
    }

    /**
     * outputs internal database song data to csv file
     *
     * @param outputPath path of output file
     * @throws IOException in the event the file cannot be outputted
     */
    public static void outputSongDataToCSV(String outputPath) throws IOException {
        validateCSVPath(outputPath);

        writeRecordsWithHeader("song,number of ratings,mean,standard deviation", outputPath,
                DataOpperator.getSongRecords());
    }

    /**
     * outputs user similarity data to csv
     *
     * @param outputPath path of file to output
     * @throws IOException if the output file cannot be written
     */
    public static void outputUserDataToCSV(String outputPath) throws IOException {
        validateCSVPath(outputPath);

        var userSimilarities = DataOpperator.getUserRecords();
        if (userSimilarities.size() == 0) {
            throw new IOException("at least two cooperative users are required for user similarity");
        }
        writeRecordsWithHeader("name1,name2,similarity", outputPath, userSimilarities);
    }

    public static void outputPredictedUserRatingsToCSV(String outputPath) throws IOException {
        validateCSVPath(outputPath);

        var predictions = DataOpperator.getSongRecordsWithPredictions();
        if (predictions.size() == 0) {
            throw new IOException("no predictions to be made");
        }

        writeRecordsWithHeader("song,user,predicted rating", outputPath, predictions);
    }

    public static void outputSongReccomendationsToCSV(String outputPath, String[] songNames) throws IOException {
        validateCSVPath(outputPath);
        List<String> reccomendations;

        try {
            reccomendations = DataOpperator.getSongReccomendationRecords(songNames);
        } catch (NoSuchElementException e) {
            throw new IOException(
                    "no songs to recommend." +
                            " Songs may have been removed. " +
                            "Try with a larger file or fewer selections.");
        } catch (DataOpperator.CannotRateException e) {
            throw new IOException(e.getMessage());
        }

        writeRecordsWithHeader("user choice,recommendation", outputPath, reccomendations);
    }

    private static void writeRecordsWithHeader(String header, String path, List<String> records)
            throws IOException {
        FileWriter filewriter = new FileWriter(path);
        filewriter.write(header + "\n");
        for (var record : records) {
            filewriter.write(record + "\n");
        }

        filewriter.close();
    }

    public static void createNewCSVFile(String path) throws IOException {
        validateCSVPath(path);
        File f = new File(path);
        f.createNewFile();
    }

    private static void validateCSVPath(String outputPath) throws IOException {
        if (!getFileExtension(outputPath).equals("csv")) {
            throw new IOException("input and output paths must have `.csv` extension");
        }
    }

    /**
     * @param filePath path of file in file system
     * @return a string representation of the file extension
     * @throws IndexOutOfBoundsException if the input file path is less than three
     *                                   characters long
     */
    private static String getFileExtension(String filePath) {
        filePath = filePath.replaceAll(" ", "");
        int dotIndex = filePath.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == filePath.length() - 1) {
            return "";
        }
        return filePath.substring(dotIndex + 1, filePath.length());
    }

    public static class EmptyFileException extends Exception {
        public EmptyFileException() {
            super("input data must not be empty");
        }
    };
}
