package jp.unaguna.mappedftp;

import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * It provides functionality specific to tests of this project.
 */
public class TestUtils {
    private static final Logger LOG = LoggerFactory.getLogger(TestUtils.class.getName());

    /**
     * Return the path of input file prepared for testing.
     *
     * <p>
     *     The file is searched in the following paths.
     * </p>
     * <ol>
     *     <li><code>/unittest/cases/${simpleNameOfTestClass}_${nameOfTestMethod}/input/</code></li>
     *     <li><code>/unittest/cases/${simpleNameOfTestClass}/input/</code></li>
     *     <li><code>/unittest/input/</code></li>
     * </ol>
     * <p>
     *     If you want the path to a temporary file whose contents are copied, rather than a URL,
     *     use {@link #getInputResourceAsTempFile(String, TestInfo)}.
     * </p>
     *
     * @param relativePath the name of file to get
     * @param testInfo the JUnit test information object
     * @return the path of the specified input file
     * @throws NoSuchTestResourceException when specified resource is not found
     * @see #getInputResourceAsTempFile(String, TestInfo) 
     */
    public static URL getInputResource(String relativePath, TestInfo testInfo) {
        URL url;
        final String testClassName = testInfo.getTestClass().orElseThrow(NullPointerException::new).getSimpleName();
        final String testMethodName = testInfo.getTestMethod().orElseThrow(NullPointerException::new).getName();

        url = getResource(
                "unittest/cases",
                testClassName + "#" + testMethodName,
                "input",
                relativePath
        );
        if (url != null) {
            return url;
        }

        url = getResource(
                "unittest/cases",
                testClassName,
                "input",
                relativePath
        );
        if (url != null) {
            return url;
        }

        url = getResource(
                "unittest",
                "input",
                relativePath
        );
        if (url != null) {
            return url;
        }

        throw new NoSuchTestResourceException(relativePath);
    }

    /**
     * Return the path of the clone of input file prepared for testing.
     *
     * <p>
     *     The file is searched in the following paths.
     * </p>
     * <ol>
     *     <li><code>/unittest/cases/${simpleNameOfTestClass}_${nameOfTestMethod}/input/</code></li>
     *     <li><code>/unittest/cases/${simpleNameOfTestClass}/input/</code></li>
     *     <li><code>/unittest/input/</code></li>
     * </ol>
     *
     * @param relativePath the name of file to get
     * @param testInfo the JUnit test information object
     * @return the path of a temporary file whose contents are same as the specified input file
     * @see #getInputResource(String, TestInfo)
     */
    public static TemporaryFile getInputResourceAsTempFile(String relativePath, TestInfo testInfo) {
        final URL resourceUrl = getInputResource(relativePath, testInfo);

        TemporaryFile tempFile = null;
        try {
            // TODO: 一括削除できるよう、特定のディレクトリ下に作る
            tempFile = new TemporaryFile(Files.createTempFile(null, getExtensionWithDot(resourceUrl.getFile())));
            LOG.debug("A temporary file has been created: " + tempFile);

            URLConnection url = resourceUrl.openConnection();
            try (InputStream is = url.getInputStream()) {
                Files.copy(is, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (Exception e) {
            // remove the temporary file before throw exception
            deleteTempFile(tempFile);
            throw new RuntimeException(e);
        }

        return tempFile;
    }

    public static boolean deleteTempFile(TemporaryFile tempFile) {
        try {
            if (tempFile != null) {
                final boolean deleted = Files.deleteIfExists(tempFile.toPath());
                if (deleted) {
                    LOG.debug("A temporary file has been deleted: " + tempFile);
                }
                return deleted;
            } else {
                return false;
            }
        } catch (Exception ex) {
            LOG.warn("failed to delete a temporary file: " + tempFile, ex);
            return false;
        }
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

    private static String buildString(String separator, String ...parts) {
        final StringBuilder pathBuilder = new StringBuilder(128);
        pathBuilder.append(parts[0]);
        for (int i=1; i < parts.length; i++) {
            pathBuilder.append(separator);
            pathBuilder.append(parts[i]);
        }
        return pathBuilder.toString();
    }

    private static URL getResource(String... pathParts) {
        if (pathParts.length == 0) {
            throw new IllegalArgumentException("path must not be empty");
        }

        final String path = buildString("/", pathParts);

        return TestUtils.class.getClassLoader().getResource(path);
    }

    private static String getExtensionWithDot(String filename) {
        final int dotIndex = filename.lastIndexOf('.');

        if (dotIndex < 0) {
            return "";
        } else {
            return filename.substring(dotIndex);
        }
    }
}
