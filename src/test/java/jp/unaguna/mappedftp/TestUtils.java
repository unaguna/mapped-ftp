package jp.unaguna.mappedftp;

import org.junit.jupiter.api.TestInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * It provides functionality specific to tests of this project.
 */
public class TestUtils {

    /**
     * Return the path of input file prepared for testing.
     *
     * <p>
     *     The file is searched in the following paths.
     * </p>
     * <ol>
     *     <li><code>./src/test/resources/cases/${simpleNameOfTestClass}_${nameOfTestMethod}/input/</code></li>
     *     <li><code>./src/test/resources/cases/${simpleNameOfTestClass}/input/</code></li>
     *     <li><code>./src/test/resources/input/</code></li>
     * </ol>
     *
     * @param relativePath the name of file to get
     * @param testInfo the JUnit test information object
     * @return the path of the specified input file
     */
    public static Path getInputResource(String relativePath, TestInfo testInfo) {
        Path path;
        final String testClassName = testInfo.getTestClass().orElseThrow(NullPointerException::new).getSimpleName();
        final String testMethodName = testInfo.getTestMethod().orElseThrow(NullPointerException::new).getName();

        path =  Paths.get(
                "src/test/resources/cases",
                testClassName + "#" + testMethodName,
                "input",
                relativePath
        );
        if (Files.exists(path)) {
            return path;
        }

        path = Paths.get(
                "src/test/resources/cases",
                testClassName,
                "input",
                relativePath
        );
        if (Files.exists(path)) {
            return path;
        }

        path = Paths.get(
                "src/test/resources",
                "input",
                relativePath
        );
        if (Files.exists(path)) {
            return path;
        }

        throw new RuntimeException(new NoSuchFileException(relativePath));
    }

    /**
     * Assert that expected lines equal to actual content of the text file.
     *
     * @param expectedLines expected lines
     * @param actual the actual text file
     * @throws IOException some error occurred when read the actual file
     */
    public static void assertTextFile(String[] expectedLines, InputStream actual) throws IOException {
        try (
                final InputStreamReader r = new InputStreamReader(actual);
                final BufferedReader reader = new BufferedReader(r)
        ) {
            int i;
            for (i=0; i < expectedLines.length; i++) {
                final int lineNum = i;
                final String expectedLine = expectedLines[i];
                final String actualLine = reader.readLine();

                if (actualLine != null) {
                    assertEquals(expectedLine, actualLine,
                            () -> "expected line[" + lineNum + "] was \"" + expectedLine + "\" but actual was \"" + actualLine + "\"");

                } else {
                    if (!expectedLine.equals("")) {
                        fail("expected line[" + lineNum + "] was \"" + expectedLine + "\" but actual has lesser lines");
                    }
                }
            }

            // non-expected lines
            for(;; i++) {
                final String actualLine = reader.readLine();
                if (actualLine == null) {
                    break;
                }

                if (!actualLine.equals("")) {
                    fail("expected text doesn't have line[" + i + "] but actual was \"" + actualLine + "\"");
                }
            }

            // asserted
        } finally {
            actual.close();
        }
    }
}
