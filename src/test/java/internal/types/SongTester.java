package internal.types;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import internal.data.DataBase;
import internal.files.FileManager;

public class SongTester {

    @AfterEach
    public void cleanDataBase() {
        DataBase.clearDataBase();
    }

    @Test
    public void testSongMean() {
        assertDoesNotThrow(() -> FileManager.loadDataFromCSV("./database/testfiles/file1.csv"));
        var id = DataBase.getSongId("song1");
        assertTrue(id.isPresent());
        var testSong1 = DataBase.getSong(id.get());
        assertTrue(testSong1.isPresent());
        assertEquals(String.format("%.6f", 3.25), String.format("%.6f", testSong1.get().getMeanRating()));
    }

    @Test
    public void testSongStdDev() {
        assertDoesNotThrow(() -> FileManager.loadDataFromCSV("./database/testfiles/file1.csv"));
        var id = DataBase.getSongId("song1");
        assertTrue(id.isPresent());
        var testSong1 = DataBase.getSong(id.get());
        assertTrue(testSong1.isPresent());
        assertEquals(String.format("%.6f", 1.0897247358851685), String.format("%.6f", testSong1.get().getStdDev()));
    }

    @Test
    public void testNumOfReviews() {
        assertDoesNotThrow(() -> FileManager.loadDataFromCSV("./database/testfiles/file1.csv"));
        var id = DataBase.getSongId("song1");
        assertTrue(id.isPresent());
        var testSong1 = DataBase.getSong(id.get());
        assertTrue(testSong1.isPresent());
        assertEquals(4, testSong1.get().getNumberOfRatings());
    }

    @Test
    public void testToString() {
        assertDoesNotThrow(() -> FileManager.loadDataFromCSV("./database/testfiles/file1.csv"));
        var id = DataBase.getSongId("song1");
        assertTrue(id.isPresent());
        var testSong1 = DataBase.getSong(id.get());
        assertTrue(testSong1.isPresent());
        assertEquals("song1,4,3.25,1.0897247", testSong1.get().toString());
    }
}
