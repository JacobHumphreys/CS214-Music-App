package internal.data;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import internal.files.FileManager;
import internal.types.User;

public class DataBaseTester {

    @AfterEach
    public void clearDB() {
        DataBase.clearDataBase();
    }

    @Test
    public void testDataBaseInitialization() {
        assertDoesNotThrow(() -> FileManager.loadDataFromCSV("./database/testfiles/file1.csv"));
        DataBase.clearDataBase();
        assertThrows(IOException.class, () -> FileManager.loadDataFromCSV(""));
    }

    @Test
    public void testDataBaseSize() {
        DataBase.addUser("User1");
        DataBase.addUser("User2");
        DataBase.addSong("Song1");
        DataBase.addSong("Song2");
        assertEquals(2, DataBase.getSongCount());
        assertEquals(2, DataBase.getUserCount());
    }

    @Test
    public void testUserListNameSorting() {
        String[] unsortedNames = { "Tony", "Blake", "John" };
        String[] sortedNames = Arrays.copyOf(unsortedNames, unsortedNames.length);
        Arrays.sort(sortedNames);

        User[] unsortedUsers = Arrays.stream(unsortedNames)
                .map(name -> DataBase.addUser(name))
                .map(id -> DataBase.getUser(id).get())
                .toArray(User[]::new);

        var sortedUsers = UserDataHandler.sortUsersByName(unsortedUsers);

        for (int i = 0; i < unsortedUsers.length; i++) {
            var unsortedUser = unsortedUsers[i];
            var sortedUser = sortedUsers[i];
            assertNotEquals(unsortedUser, sortedUser);
        }

        for (int i = 0; i < sortedUsers.length; i++) {
            var user = sortedUsers[i];
            var name = sortedNames[i];
            assertEquals(name, user.getName());
        }
    }

    @Test
    public void testUserSimilaritySorting() {
        var john = DataBase.getUser(DataBase.addUser("John"));
        assertTrue(john.isPresent());
        var blake = DataBase.getUser(DataBase.addUser("Blake"));
        assertTrue(blake.isPresent());
        var tony = DataBase.getUser(DataBase.addUser("tony"));
        assertTrue(tony.isPresent());

        var hookedOnAFeelin = DataBase.getSong(DataBase.addSong("Hooked On A Feelin"));
        assertTrue(hookedOnAFeelin.isPresent());
        var fatherAndSon = DataBase.getSong(DataBase.addSong("Father and Son"));
        assertTrue(fatherAndSon.isPresent());
        var mrBlueSky = DataBase.getSong(DataBase.addSong("mrBlueSky"));
        assertTrue(mrBlueSky.isPresent());

        assertDoesNotThrow(() -> hookedOnAFeelin.get().addOrChangeRating(john.get().getId(), 3));
        assertDoesNotThrow(() -> hookedOnAFeelin.get().addOrChangeRating(blake.get().getId(), 2));
        assertDoesNotThrow(() -> hookedOnAFeelin.get().addOrChangeRating(tony.get().getId(), 5));
        john.get().addSongToLibrary(hookedOnAFeelin.get().getId());
        blake.get().addSongToLibrary(hookedOnAFeelin.get().getId());
        tony.get().addSongToLibrary(hookedOnAFeelin.get().getId());

        assertDoesNotThrow(() -> mrBlueSky.get().addOrChangeRating(john.get().getId(), 4));
        assertDoesNotThrow(() -> mrBlueSky.get().addOrChangeRating(blake.get().getId(), 4));
        assertDoesNotThrow(() -> mrBlueSky.get().addOrChangeRating(tony.get().getId(), 2));
        john.get().addSongToLibrary(mrBlueSky.get().getId());
        blake.get().addSongToLibrary(mrBlueSky.get().getId());
        tony.get().addSongToLibrary(mrBlueSky.get().getId());

        assertDoesNotThrow(() -> fatherAndSon.get().addOrChangeRating(john.get().getId(), 5));
        assertDoesNotThrow(() -> fatherAndSon.get().addOrChangeRating(blake.get().getId(), 3));
        john.get().addSongToLibrary(mrBlueSky.get().getId());
        blake.get().addSongToLibrary(mrBlueSky.get().getId());

        User[] expectedList = { blake.get(), tony.get(), john.get() };
        var sortedUsers = UserDataHandler.sortUsersBySimilarityToUser(
                john.get(),
                new User[] { tony.get(), blake.get(), john.get() });

        for (int i = 0; i < sortedUsers.length; i++) {
            var expected = expectedList[i];
            var actual = sortedUsers[i];
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testUserRecords() {
        String expectedOutputPath = "./database/testfiles/pa3/expectedUserOutput.csv";
        assertDoesNotThrow(() -> FileManager.loadDataFromCSV("./database/testfiles/file1.csv"));

        ArrayList<String> testRecords = new ArrayList<String>(DataOpperator.getUserRecords());

        ArrayList<String> expectedRecords = null;
        try {
            expectedRecords = readLinesFromFile(expectedOutputPath);
        } catch (IOException e) {
            fail("File Not Found at: " + expectedOutputPath);
        }
        assertEquals(testRecords.size(), expectedRecords.size());

        for (int i = 0; i < testRecords.size(); i++) {
            assertEquals(expectedRecords.get(i), testRecords.get(i));
        }
    }

    @Test
    public void testUserPredictionRecords() {
        String expectedOutputPath = "./database/testfiles/pa3/expectedPredictions.csv";
        assertDoesNotThrow(() -> FileManager.loadDataFromCSV("./database/testfiles/file4.csv"));

        ArrayList<String> testRecords = new ArrayList<String>(DataOpperator.getSongRecordsWithPredictions());

        ArrayList<String> expectedRecords = null;
        try {
            expectedRecords = readLinesFromFile(expectedOutputPath);
        } catch (IOException e) {
            fail("File Not Found at: " + expectedOutputPath);
        }
        assertEquals(testRecords.size(), expectedRecords.size());

        for (int i = 0; i < testRecords.size(); i++) {
            assertEquals(expectedRecords.get(i), testRecords.get(i));
        }
    }

    private ArrayList<String> readLinesFromFile(String path) throws IOException {
        var lines = new ArrayList<String>();
        BufferedReader lineReader = new BufferedReader(new FileReader(path));
        String line = lineReader.readLine();
        while (line != null) {
            lines.add(line);
            line = lineReader.readLine();
        }
        lineReader.close();
        return lines;
    }
}
