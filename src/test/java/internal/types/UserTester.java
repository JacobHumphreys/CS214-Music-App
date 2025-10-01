package internal.types;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import internal.data.DataBase;
import internal.files.FileManager;

public class UserTester {

    @AfterEach
    public void cleanDataBase(){
        DataBase.clearDataBase();
    }

    @Test
    public void testUserStandardDeviation() {
        assertDoesNotThrow(() -> FileManager.loadDataFromCSV("./database/testfiles/userMeanAndStdDev.csv"));
        var userId  = assertDoesNotThrow(() -> DataBase.getUserId("sifat"));
        assertTrue(userId.isPresent());
        var userSifat = DataBase.getUser(userId.get());
        assertTrue(userSifat.isPresent());

        var maxDifference = 1 * Math.pow(10,-6);
        assertEquals(Math.abs(userSifat.get().getStdDev() -  0.81649658092773) < maxDifference, true);
    }

    @Test
    public void testUserRatingsMean() {
        assertDoesNotThrow(() -> FileManager.loadDataFromCSV("./database/testfiles/userMeanAndStdDev.csv"));
        var userId  = assertDoesNotThrow(() -> DataBase.getUserId("sifat"));
        assertTrue(userId.isPresent());
        var userSifat = DataBase.getUser(userId.get());
        assertNotNull(userSifat);

        var maxDifference = 1 * Math.pow(10,-6);
        assertEquals(Math.abs(userSifat.get().getMeanRating() -  3.0) < maxDifference, true);
    }
}
