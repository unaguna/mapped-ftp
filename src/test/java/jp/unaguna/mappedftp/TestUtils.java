package jp.unaguna.mappedftp;

import org.junit.jupiter.api.TestInfo;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
                testClassName + "_" + testMethodName,
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
}
