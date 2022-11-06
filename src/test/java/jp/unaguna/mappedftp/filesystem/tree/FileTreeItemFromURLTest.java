package jp.unaguna.mappedftp.filesystem.tree;

import jp.unaguna.mappedftp.TestUtils;
import jp.unaguna.mappedftp.filesystem.tree.date.DateFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class FileTreeItemFromURLTest {
    @Test
    public void testSource(TestInfo testInfo) {
        final URL source = TestUtils.getInputResource("local.txt", testInfo);

        FileTreeItemFromURL fileTreeItem = new FileTreeItemFromURL(source);

        assertEquals(source, fileTreeItem.getSource());
    }

    @Test
    public void testInputStream(TestInfo testInfo) {
        final URL source = TestUtils.getInputResource("local.txt", testInfo);

        FileTreeItemFromURL fileTreeItem = new FileTreeItemFromURL(source);

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
        final URL source = TestUtils.url("http://dummy1.example.com/");

        FileTreeItemFromURL fileTreeItem = new FileTreeItemFromURL(source);

        try (InputStream ignored = fileTreeItem.createInputStream(0)) {
            fail("expected exception has not been thrown");

        } catch (UnknownHostException e) {
            // expected exception
            assertEquals("dummy1.example.com", e.getMessage());

        } catch (IOException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin", "test", ""})
    public void testGetOwnerName(String ownerName) {
        final URL source = TestUtils.url("http://dummy1.example.com/");

        final FileTreeItemFromURL fileTreeItem = new FileTreeItemFromURL(source);
        fileTreeItem.setOwnerName(ownerName);

        assertEquals(ownerName, fileTreeItem.getOwnerName());
    }

    @Test
    public void testGetOwnerName__null_if_not_specified() {
        final URL source = TestUtils.url("http://dummy1.example.com/");

        final FileTreeItemFromURL fileTreeItem = new FileTreeItemFromURL(source);

        assertNull(fileTreeItem.getOwnerName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin", "test", ""})
    public void testGetGroupName(String groupName) {
        final URL source = TestUtils.url("http://dummy1.example.com/");

        final FileTreeItemFromURL fileTreeItem = new FileTreeItemFromURL(source);
        fileTreeItem.setGroupName(groupName);

        assertEquals(groupName, fileTreeItem.getGroupName());
    }

    @Test
    public void testGetGroupName__null_if_not_specified() {
        final URL source = TestUtils.url("http://dummy1.example.com/");

        final FileTreeItemFromURL fileTreeItem = new FileTreeItemFromURL(source);

        assertNull(fileTreeItem.getGroupName());
    }

    private static Stream<Arguments> parameters__testGetLastModified() {
        return Stream.of(
                Arguments.arguments(DateFactory.constance(0L), 0L),
                Arguments.arguments(DateFactory.constance(1666501478_000L), 1666501478_000L)
        );
    }

    @ParameterizedTest
    @MethodSource("parameters__testGetLastModified")
    public void testGetLastModified(DateFactory dateFactory, long expected) {
        final URL source = TestUtils.url("http://dummy1.example.com/");

        final FileTreeItemFromURL fileTreeItem = new FileTreeItemFromURL(source);
        fileTreeItem.setLastModifiedFactory(dateFactory);

        assertEquals(expected, fileTreeItem.getLastModified());
    }

    @Test
    public void testGetLastModified__null_if_not_specified() {
        final URL source = TestUtils.url("http://dummy1.example.com/");

        final FileTreeItemFromURL fileTreeItem = new FileTreeItemFromURL(source);

        assertNull(fileTreeItem.getLastModified());
    }
}
