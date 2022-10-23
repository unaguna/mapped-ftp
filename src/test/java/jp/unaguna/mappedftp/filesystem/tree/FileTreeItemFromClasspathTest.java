package jp.unaguna.mappedftp.filesystem.tree;

import jp.unaguna.mappedftp.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;

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

        } catch (FileNotFoundException e) {
            // expected exception
            assertEquals("no such resource: " + source, e.getMessage());

        } catch (IOException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin", "test", ""})
    public void testGetOwnerName(String ownerName, TestInfo testInfo) {
        final String source = TestUtils.getInputResourceClasspath("local.txt", testInfo);

        final FileTreeItemFromClasspath fileTreeItem = new FileTreeItemFromClasspath(source);
        fileTreeItem.setOwnerName(ownerName);

        assertEquals(ownerName, fileTreeItem.getOwnerName());
    }

    @Test
    public void testGetOwnerName__null_if_not_specified(TestInfo testInfo) {
        final String source = TestUtils.getInputResourceClasspath("local.txt", testInfo);

        final FileTreeItemFromClasspath fileTreeItem = new FileTreeItemFromClasspath(source);

        assertNull(fileTreeItem.getOwnerName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin", "test", ""})
    public void testGetGroupName(String groupName, TestInfo testInfo) {
        final String source = TestUtils.getInputResourceClasspath("local.txt", testInfo);

        final FileTreeItemFromClasspath fileTreeItem = new FileTreeItemFromClasspath(source);
        fileTreeItem.setGroupName(groupName);

        assertEquals(groupName, fileTreeItem.getGroupName());
    }

    @Test
    public void testGetGroupName__null_if_not_specified(TestInfo testInfo) {
        final String source = TestUtils.getInputResourceClasspath("local.txt", testInfo);

        final FileTreeItemFromClasspath fileTreeItem = new FileTreeItemFromClasspath(source);

        assertNull(fileTreeItem.getGroupName());
    }
}
