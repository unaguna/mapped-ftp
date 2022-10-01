package jp.unaguna.mappedftp.filesystem.tree;

import jp.unaguna.mappedftp.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileTreeItemFromURLTest {
    @Test
    public void testSource(TestInfo testInfo) {
        final Path localPath = TestUtils.getInputResource("local.txt", testInfo);
        final URL source;
        try {
            source = localPath.toUri().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        FileTreeItemFromURL fileTreeItem = new FileTreeItemFromURL(source);

        assertEquals(source, fileTreeItem.getSource());
    }

    @Test
    public void testInputStream(TestInfo testInfo) {
        Path localPath = TestUtils.getInputResource("local.txt", testInfo);
        final URL source;
        try {
            source = localPath.toUri().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        FileTreeItemFromURL fileTreeItem = new FileTreeItemFromURL(source);

        final String line;
        try(InputStream inputStream = fileTreeItem.createInputStream(0);
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
}
