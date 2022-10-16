package jp.unaguna.mappedftp.filesystem.tree;

import jp.unaguna.mappedftp.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.*;
import java.net.URL;
import java.net.UnknownHostException;

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
}
