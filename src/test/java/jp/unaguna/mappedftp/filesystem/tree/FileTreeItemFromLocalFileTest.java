package jp.unaguna.mappedftp.filesystem.tree;

import jp.unaguna.mappedftp.TemporaryFile;
import jp.unaguna.mappedftp.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.*;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class FileTreeItemFromLocalFileTest {
    @Test
    public void testSource(TestInfo testInfo) {
        final TemporaryFile localFile = TestUtils.getInputResourceAsTempFile("local.txt", testInfo);

        try {
            FileTreeItemFromLocalFile fileTreeItem = new FileTreeItemFromLocalFile(localFile.toPath());

            assertEquals(localFile.toPath(), fileTreeItem.getSource());

        } finally {
            TestUtils.deleteTempFile(localFile);
        }
    }

    @Test
    public void testInputStream(TestInfo testInfo) {
        final TemporaryFile localPath = TestUtils.getInputResourceAsTempFile("local.txt", testInfo);

        try {
            FileTreeItemFromLocalFile fileTreeItem = new FileTreeItemFromLocalFile(localPath.toPath());

            final String line;
            try (InputStream inputStream = fileTreeItem.createInputStream(0);
                 Reader reader = new InputStreamReader(inputStream);
                 BufferedReader bufferedReader = new BufferedReader(reader)) {

                line = bufferedReader.readLine();
                assertNull(bufferedReader.readLine());

            } catch (IOException e) {
                fail(e);
                return;
            }

            assertEquals("I am a text file for test", line);

        } finally {
            TestUtils.deleteTempFile(localPath);
        }
    }

    @Test
    public void testInputStream__error_by_missing_resource() {
        final Path path = Paths.get("/dummy/no_exists");
        FileTreeItemFromLocalFile fileTreeItem = new FileTreeItemFromLocalFile(path);

        try (InputStream ignored = fileTreeItem.createInputStream(0)) {
            fail("expected exception has not been thrown");

        } catch (NoSuchFileException e) {
            // expected exception
            assertEquals(path.toString(), e.getMessage());

        } catch (IOException e) {
            fail(e);
        }

    }
}
