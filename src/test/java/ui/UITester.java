package ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import internal.data.DataBase;

public class UITester {
    @AfterEach
    public void clearDB(){
        DataBase.clearDataBase();
    }

    @Test
    public void testUIApp() {
        File tmpIn = assertDoesNotThrow(() -> File.createTempFile("stdin", null));
        var tmpOut = assertDoesNotThrow(() -> File.createTempFile("stdout", null));
        FileWriter writer = assertDoesNotThrow(() -> new FileWriter(tmpIn));

        FileInputStream tmpInStream = assertDoesNotThrow(() -> new FileInputStream(tmpIn));

        PrintStream tmpOutStream = assertDoesNotThrow(() -> new PrintStream(tmpOut));
        System.setIn(tmpInStream);
        System.setOut(tmpOutStream);
        InteractiveApp.init();

        assertDoesNotThrow(() -> {
            writer.write("1\n");
            writer.flush();
        });
        assertDoesNotThrow(() -> InteractiveApp.appLoop());
        assertDoesNotThrow(() -> InteractiveApp.appLoop());
        assertDoesNotThrow(() -> {
            writer.write("database/testfiles/pa5\n");
            writer.flush();
        });
        assertDoesNotThrow(()->InteractiveApp.appLoop());
        assertDoesNotThrow(()->InteractiveApp.appLoop());
        assertDoesNotThrow(() -> {
            writer.write("1\n");
            writer.flush();
        });
        assertDoesNotThrow(()->InteractiveApp.appLoop());
        assertDoesNotThrow(()->InteractiveApp.appLoop());
        assertDoesNotThrow(() -> {
            writer.write("1\n");
            writer.flush();
        });
        assertDoesNotThrow(()->InteractiveApp.appLoop()); // render
        assertDoesNotThrow(()->InteractiveApp.appLoop()); // input
        assertDoesNotThrow(() -> {
            writer.write("1\n");
            writer.flush();
        });

        assertDoesNotThrow(()->InteractiveApp.appLoop()); // render
        assertDoesNotThrow(()->InteractiveApp.appLoop());

        assertDoesNotThrow(() -> {
            writer.write("output/tempout.csv\n");
            writer.flush();
        });
        assertDoesNotThrow(()->InteractiveApp.appLoop()); // render
        assertDoesNotThrow(()->InteractiveApp.appLoop());

        assertDoesNotThrow(() -> {
            writer.write("6\n");
            writer.flush();
        });
        assertDoesNotThrow(()->InteractiveApp.appLoop()); // render
        assertDoesNotThrow(()->InteractiveApp.appLoop());

        assertDoesNotThrow(() -> {
            writer.write("1\n");
            writer.flush();
        });
        assertDoesNotThrow(()->InteractiveApp.appLoop()); // render
        assertDoesNotThrow(()->InteractiveApp.appLoop());

        assertDoesNotThrow(() -> {
            writer.write("4\n");
            writer.flush();
        });
        assertDoesNotThrow(()->InteractiveApp.appLoop()); // render
        assertDoesNotThrow(()->InteractiveApp.appLoop());

        assertDoesNotThrow(() -> {
            writer.write("4\n");
            writer.flush();
        });
        assertDoesNotThrow(()->InteractiveApp.appLoop()); // render
        assertDoesNotThrow(()->InteractiveApp.appLoop());

        assertDoesNotThrow(() -> {
            writer.write("output/tempout.csv\n");
            writer.flush();
        });
        assertDoesNotThrow(()->InteractiveApp.appLoop()); // render
        assertDoesNotThrow(()->InteractiveApp.appLoop());
        assertDoesNotThrow(() -> {
            writer.write("1,2,4\n");
            writer.flush();
        });
        assertDoesNotThrow(()->InteractiveApp.appLoop()); // render
        assertDoesNotThrow(()->InteractiveApp.appLoop());

        assertDoesNotThrow(() -> {
            writer.write("6\n");
            writer.flush();
        });
        assertDoesNotThrow(()->InteractiveApp.appLoop()); // render
        assertDoesNotThrow(()->InteractiveApp.appLoop());

        assertDoesNotThrow(() -> {
            writer.write("3\n");
            writer.flush();
        });
        assertDoesNotThrow(()->InteractiveApp.appLoop()); // render
        assertDoesNotThrow(()->InteractiveApp.appLoop());

        assertDoesNotThrow(() -> {
            writer.write("2\n");
            writer.flush();
        });
        assertDoesNotThrow(()->InteractiveApp.appLoop()); // render
        assertDoesNotThrow(()->InteractiveApp.appLoop());

        File output = new File("output/tempout.csv");
        output.delete();
        assertDoesNotThrow(() -> writer.close());
        assertDoesNotThrow(() -> tmpInStream.close());
        assertDoesNotThrow(() -> tmpOutStream.close());
    }

}
