package jp.unaguna.mappedftp.filesystem.tree;

import jp.unaguna.mappedftp.TemporaryFile;
import jp.unaguna.mappedftp.TestUtils;
import jp.unaguna.mappedftp.filesystem.tree.date.DateFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

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

    @ParameterizedTest
    @ValueSource(strings = {"admin", "test", ""})
    public void testGetOwnerName(String ownerName, TestInfo testInfo) {
        final TemporaryFile localPath = TestUtils.getInputResourceAsTempFile("local.txt", testInfo);

        try {
            FileTreeItemFromLocalFile fileTreeItem = new FileTreeItemFromLocalFile(localPath.toPath());
            fileTreeItem.setOwnerName(ownerName);

            assertEquals(ownerName, fileTreeItem.getOwnerName());

        } finally {
            TestUtils.deleteTempFile(localPath);
        }
    }

    @Test
    public void testGetOwnerName__null_if_not_specified(TestInfo testInfo) {
        final TemporaryFile localPath = TestUtils.getInputResourceAsTempFile("local.txt", testInfo);

        try {
            FileTreeItemFromLocalFile fileTreeItem = new FileTreeItemFromLocalFile(localPath.toPath());

            assertNull(fileTreeItem.getOwnerName());

        } finally {
            TestUtils.deleteTempFile(localPath);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin", "test", ""})
    public void testGetGroupName(String groupName, TestInfo testInfo) {
        final TemporaryFile localPath = TestUtils.getInputResourceAsTempFile("local.txt", testInfo);

        try {
            FileTreeItemFromLocalFile fileTreeItem = new FileTreeItemFromLocalFile(localPath.toPath());
            fileTreeItem.setGroupName(groupName);

            assertEquals(groupName, fileTreeItem.getGroupName());

        } finally {
            TestUtils.deleteTempFile(localPath);
        }
    }

    @Test
    public void testGetGroupName__null_if_not_specified(TestInfo testInfo) {
        final TemporaryFile localPath = TestUtils.getInputResourceAsTempFile("local.txt", testInfo);

        try {
            FileTreeItemFromLocalFile fileTreeItem = new FileTreeItemFromLocalFile(localPath.toPath());

            assertNull(fileTreeItem.getGroupName());

        } finally {
            TestUtils.deleteTempFile(localPath);
        }
    }

    private static Stream<Arguments> parameters__testGetLastModified() {
        return Stream.of(
                Arguments.arguments(DateFactory.constance(0L), 0L),
                Arguments.arguments(DateFactory.constance(1666501478_000L), 1666501478_000L)
        );
    }

    @ParameterizedTest
    @MethodSource("parameters__testGetLastModified")
    public void testGetLastModified(DateFactory dateFactory, long expected, TestInfo testInfo) {
        final TemporaryFile localPath = TestUtils.getInputResourceAsTempFile("local.txt", testInfo);

        try {
            FileTreeItemFromLocalFile fileTreeItem = new FileTreeItemFromLocalFile(localPath.toPath());
            fileTreeItem.setLastModifiedFactory(dateFactory);

            assertEquals(expected, fileTreeItem.getLastModified());

        } finally {
            TestUtils.deleteTempFile(localPath);
        }
    }

    @Test
    public void testGetLastModified__null_if_not_specified(TestInfo testInfo) {
        final TemporaryFile localPath = TestUtils.getInputResourceAsTempFile("local.txt", testInfo);

        try {
            FileTreeItemFromLocalFile fileTreeItem = new FileTreeItemFromLocalFile(localPath.toPath());

            assertNull(fileTreeItem.getLastModified());

        } finally {
            TestUtils.deleteTempFile(localPath);
        }
    }
}
