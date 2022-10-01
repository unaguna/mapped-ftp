package jp.unaguna.mappedftp.filesystem.tree;

import jp.unaguna.mappedftp.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.*;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileTreeItemFromLocalFileTest {
    @Test
    public void testSource(TestInfo testInfo) {
        Path localPath = TestUtils.getInputResource("local.txt", testInfo);

        FileTreeItemFromLocalFile fileTreeItem = new FileTreeItemFromLocalFile(localPath);

        assertEquals(localPath, fileTreeItem.getSource());
    }

    @Test
    public void testInputStream(TestInfo testInfo) {
        Path localPath = TestUtils.getInputResource("local.txt", testInfo);

        FileTreeItemFromLocalFile fileTreeItem = new FileTreeItemFromLocalFile(localPath);

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
