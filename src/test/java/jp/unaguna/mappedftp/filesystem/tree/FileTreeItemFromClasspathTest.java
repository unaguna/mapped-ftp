package jp.unaguna.mappedftp.filesystem.tree;

import jp.unaguna.mappedftp.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.*;
import java.util.MissingResourceException;

import static org.junit.jupiter.api.Assertions.*;

public class FileTreeItemFromClasspathTest {
    @Test
    public void testSource(TestInfo testInfo) {
        final String source = TestUtils.getInputResourceClasspath("local.txt", testInfo);

        FileTreeItemFromClasspath fileTreeItem = new FileTreeItemFromClasspath(source);

        assertEquals(source, fileTreeItem.getSource());
    }

    @Test
    public void testInputStream(TestInfo testInfo) {
        final String source = TestUtils.getInputResourceClasspath("local.txt", testInfo);

        FileTreeItemFromClasspath fileTreeItem = new FileTreeItemFromClasspath(source);

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
    }

    @Test
    public void testInputStream__error_by_missing_resource() {
        final String source = "dummy/no_exists";

        FileTreeItemFromClasspath fileTreeItem = new FileTreeItemFromClasspath(source);

        try (InputStream ignored = fileTreeItem.createInputStream(0)) {
            fail("expected exception has not been thrown");

        } catch (MissingResourceException e) {
            // expected exception
            assertEquals("no such resource: " + source, e.getMessage());

        } catch (IOException e) {
            fail(e);
        }
    }
}
