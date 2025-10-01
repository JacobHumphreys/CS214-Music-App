package internal.files;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import internal.data.DataBase;

public class FileManagerTester {

    @AfterEach
    public void clearDB() {
        DataBase.clearDataBase();
    }

    @Test
    public void testFileLoading() {
        assertThrows(IOException.class, () -> FileManager.loadDataFromCSV(""));
        assertDoesNotThrow(() -> FileManager.loadDataFromCSV("./database/testfiles/file1.csv"));
    }

    @Test
    public void testPa5() {
        final String largeDataSetPath = "./database/testfiles/pa5/largeDataSet.csv";
        final String smallDataSetPath = "./database/testfiles/pa5/smallDataSet.csv";

        final String largeExpectedOutputPath = "./database/testfiles/pa5/largeExpectedOutput.csv";
        final String smallExpectedOutputPath = "./database/testfiles/pa5/smallExpectedOutput.csv";

        final String largeTestOutputPath = "./database/testfiles/pa5/largeTestFileOutput.csv";
        final String smallTestOutputPath = "./database/testfiles/pa5/smallTestFileOutput.csv";

        assertDoesNotThrow(() -> FileManager.loadDataFromCSV(smallDataSetPath));
        try {
            assertDoesNotThrow(
                    () -> FileManager.outputSongReccomendationsToCSV(smallTestOutputPath,
                            new String[] { "song3", "song5", "song6" }));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        DataBase.clearDataBase();

        assertDoesNotThrow(() -> FileManager.loadDataFromCSV(largeDataSetPath));
        assertDoesNotThrow(
                () -> FileManager.outputSongReccomendationsToCSV(largeTestOutputPath,
                        new String[] { "song11", "song07", "song13" }));
        DataBase.clearDataBase();

        List<String> smallOutputLines = assertDoesNotThrow(() -> getLinesFromFile(smallTestOutputPath));
        List<String> smallExpectedLines = assertDoesNotThrow(() -> getLinesFromFile(smallExpectedOutputPath));

        assertTrue(smallOutputLines.equals(smallExpectedLines), "Small File Did not Match");
        File smallOutputFile = new File(smallTestOutputPath);
        assertTrue(smallOutputFile.delete());

        List<String> largeOutputLines = assertDoesNotThrow(() -> getLinesFromFile(largeTestOutputPath));
        List<String> largeExpectedLines = assertDoesNotThrow(() -> getLinesFromFile(largeExpectedOutputPath));
        assertTrue(largeOutputLines.equals(largeExpectedLines), "Large File Did not Match");

        File largeOutputFile = new File(largeTestOutputPath);
        assertTrue(largeOutputFile.delete());
    }

    @Test
    public void testPA5Errors() {
        final String smallDataSetPath = "./database/testfiles/pa5/smallDataSet.csv";
        final String smallTestOutputPath = "./database/testfiles/pa5/smallTestFileOutput.csv";
        assertDoesNotThrow(() -> FileManager.loadDataFromCSV(smallDataSetPath));
        assertThrows(IOException.class, () -> FileManager.outputSongReccomendationsToCSV(smallTestOutputPath,
                new String[] { "file1", "file1" }));
        DataBase.clearDataBase();

        final String singleSongPath = "./database/testfiles/pa5/one_song.csv";
        assertDoesNotThrow(() -> FileManager.loadDataFromCSV(singleSongPath));
        assertThrows(IOException.class, () -> FileManager.outputSongReccomendationsToCSV(smallTestOutputPath,
                new String[] { "file1" }));
        DataBase.clearDataBase();
    }

    public static List<String> getLinesFromFile(String path) throws IOException {
        List<String> lines = new ArrayList<>();
        Reader reader = new FileReader(path);
        BufferedReader bufreader = new BufferedReader(reader);
        String line = bufreader.readLine();
        while (line != null) {
            lines.add(line);
            line = bufreader.readLine();
        }
        bufreader.close();
        reader.close();
        return lines;
    }
}
