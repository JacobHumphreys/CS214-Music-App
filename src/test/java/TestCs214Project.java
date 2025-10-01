import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TestCs214Project {

    @Test
    public void testInteractiveMode() {
        String input = "1\ndatabase/files\n3\n2\n";

        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            System.setOut(new PrintStream(out));

            Cs214Project.main(new String[] { "-i" });

            String console = out.toString();
            assertTrue(console.contains("Music Analyzer"));
            assertTrue(console.contains("Load Folder"));
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}
