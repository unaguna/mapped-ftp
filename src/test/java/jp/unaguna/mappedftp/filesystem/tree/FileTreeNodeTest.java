package jp.unaguna.mappedftp.filesystem.tree;

import jp.unaguna.mappedftp.filesystem.TreePath;
import org.apache.ftpserver.ftplet.FtpFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class FileTreeNodeTest {

    @Test
    public void testAddChild() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeNode child1 = new FileTreeNode(new FileTreeItemDirectory(), "child1");
        final FileTreeNode child2 = new FileTreeNode(new FileTreeItemEmptyFile(), "child2");

        baseNode.addChild(child1, child1.getName());
        baseNode.addChild(child2, child2.getName());

        final List<? extends FtpFile> actualChildren = baseNode.listFiles();
        actualChildren.sort(Comparator.comparing(FtpFile::getName));

        assertEquals(2, actualChildren.size());
        assertEquals(child1, actualChildren.get(0));
        assertEquals(child2, actualChildren.get(1));
        try {
            assertEquals(child1, baseNode.getNodeByRelativePath(TreePath.get("child1")));
            assertEquals(child2, baseNode.getNodeByRelativePath(TreePath.get("child2")));
        } catch (NotDirectoryException | NoSuchFileException e) {
            fail(e);
        }
    }

    private static Stream<Arguments> parameters__testAppendSubFile__auto_create_inter_directory() {
        return Stream.of(
                Arguments.arguments(new FileTreeItemEmptyFile(), false),
                Arguments.arguments(new FileTreeItemDirectory(), true)
        );
    }

    @ParameterizedTest
    @MethodSource("parameters__testAppendSubFile__auto_create_inter_directory")
    public void testAppendSubFile__auto_create_inter_directory(
            FileTreeItem child1,
            boolean expectedIsDirectory
    ) {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);

        // create the file "dir1/dir1-1/child1"
        // "dir1" and "dir1-1" should be created automatically
        baseNode.appendSubFile(child1, TreePath.get("dir1", "dir1-1", "child1"));

        // assert that the directory "dir1" was created
        final List<? extends FtpFile> actualChildrenOfBase = baseNode.listFiles();
        assertEquals(1, actualChildrenOfBase.size());
        final FileTreeNode actualDir1 = (FileTreeNode) actualChildrenOfBase.get(0);
        assertEquals("dir1", actualDir1.getName());
        assertTrue(actualDir1.isDirectory());
        assertFalse(actualDir1.isFile());

        // assert that the directory "dir1/dir1-1" was created
        final List<? extends FtpFile> actualChildrenOfDir1 = actualDir1.listFiles();
        assertEquals(1, actualChildrenOfDir1.size());
        final FileTreeNode actualDir1_1 = (FileTreeNode) actualChildrenOfDir1.get(0);
        assertEquals("dir1-1", actualDir1_1.getName());
        assertTrue(actualDir1_1.isDirectory());
        assertFalse(actualDir1_1.isFile());

        // assert that the file "dir1/dir1-1/child1" was created
        final List<? extends FtpFile> actualChildrenOfDir1_1 = actualDir1_1.listFiles();
        assertEquals(1, actualChildrenOfDir1_1.size());
        final FileTreeNode actualChild1 = (FileTreeNode) actualChildrenOfDir1_1.get(0);
        assertEquals(child1, actualChild1.getFile());
        assertEquals(expectedIsDirectory, actualChild1.isDirectory());
        assertEquals(!expectedIsDirectory, actualChild1.isFile());
    }

    @Test
    public void testAppendSubFile__auto_create_inter_directory_in_existing_directory() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem dir1 = new FileTreeItemDirectory();
        final FileTreeItem child1 = new FileTreeItemEmptyFile();

        // create the directory "dir1" and the file "dir1/dir1-1/child1"
        // "dir1-1" should be created automatically
        baseNode.appendSubFile(dir1, TreePath.get("dir1"));
        baseNode.appendSubFile(child1, TreePath.get("dir1", "dir1-1", "child1"));

        // assert that the directory "dir1" was created
        final List<? extends FtpFile> actualChildrenOfBase = baseNode.listFiles();
        assertEquals(1, actualChildrenOfBase.size());
        final FileTreeNode actualDir1 = (FileTreeNode) actualChildrenOfBase.get(0);
        assertEquals(dir1, actualDir1.getFile());

        // assert that the directory "dir1/dir1-1" was created
        final List<? extends FtpFile> actualChildrenOfDir1 = actualDir1.listFiles();
        assertEquals(1, actualChildrenOfDir1.size());
        final FileTreeNode actualDir1_1 = (FileTreeNode) actualChildrenOfDir1.get(0);
        assertEquals("dir1-1", actualDir1_1.getName());
        assertTrue(actualDir1_1.isDirectory());
        assertFalse(actualDir1_1.isFile());

        // assert that the file "dir1/dir1-1/child1" was created
        final List<? extends FtpFile> actualChildrenOfDir1_1 = actualDir1_1.listFiles();
        assertEquals(1, actualChildrenOfDir1_1.size());
        final FileTreeNode actualChild1 = (FileTreeNode) actualChildrenOfDir1_1.get(0);
        assertEquals(child1, actualChild1.getFile());
    }

    @Test
    public void testAppendSubFile__with_current_dot() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem child1 = new FileTreeItemEmptyFile();

        baseNode.appendSubFile(child1, TreePath.get(".", "dir1", "dir1-1", "child1"));

        // assert that the directory "dir1" was created
        final List<? extends FtpFile> actualChildrenOfBase = baseNode.listFiles();
        assertEquals(1, actualChildrenOfBase.size());
        final FileTreeNode actualDir1 = (FileTreeNode) actualChildrenOfBase.get(0);
        assertEquals("dir1", actualDir1.getName());
        assertTrue(actualDir1.isDirectory());
        assertFalse(actualDir1.isFile());

        // assert that the directory "dir1/dir1-1" was created
        final List<? extends FtpFile> actualChildrenOfDir1 = actualDir1.listFiles();
        assertEquals(1, actualChildrenOfDir1.size());
        final FileTreeNode actualDir1_1 = (FileTreeNode) actualChildrenOfDir1.get(0);
        assertEquals("dir1-1", actualDir1_1.getName());
        assertTrue(actualDir1.isDirectory());
        assertFalse(actualDir1.isFile());

        // assert that the file "dir1/dir1-1/child1" was created
        final List<? extends FtpFile> actualChildrenOfDir1_1 = actualDir1_1.listFiles();
        assertEquals(1, actualChildrenOfDir1_1.size());
        final FileTreeNode actualChild1 = (FileTreeNode) actualChildrenOfDir1_1.get(0);
        assertEquals(child1, actualChild1.getFile());
    }

    @Test
    public void testAppendSubFile__with_parent_double_dot() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem child1 = new FileTreeItemEmptyFile();

        baseNode.appendSubFile(child1, TreePath.get("dir1", "..", "dir1", "dir1-1", "child1"));

        // assert that the directory "dir1" was created
        final List<? extends FtpFile> actualChildrenOfBase = baseNode.listFiles();
        assertEquals(1, actualChildrenOfBase.size());
        final FileTreeNode actualDir1 = (FileTreeNode) actualChildrenOfBase.get(0);
        assertEquals("dir1", actualDir1.getName());
        assertTrue(actualDir1.isDirectory());
        assertFalse(actualDir1.isFile());

        // assert that the directory "dir1/dir1-1" was created
        final List<? extends FtpFile> actualChildrenOfDir1 = actualDir1.listFiles();
        assertEquals(1, actualChildrenOfDir1.size());
        final FileTreeNode actualDir1_1 = (FileTreeNode) actualChildrenOfDir1.get(0);
        assertEquals("dir1-1", actualDir1_1.getName());
        assertTrue(actualDir1.isDirectory());
        assertFalse(actualDir1.isFile());

        // assert that the file "dir1/dir1-1/child1" was created
        final List<? extends FtpFile> actualChildrenOfDir1_1 = actualDir1_1.listFiles();
        assertEquals(1, actualChildrenOfDir1_1.size());
        final FileTreeNode actualChild1 = (FileTreeNode) actualChildrenOfDir1_1.get(0);
        assertEquals(child1, actualChild1.getFile());
    }

    private static Stream<Arguments> parameters__testAppendSubFile__error_when_absolute_path_is_specified() {
        return Stream.of(
                Arguments.arguments(new FileTreeItemEmptyFile()),
                Arguments.arguments(new FileTreeItemDirectory())
        );
    }

    @ParameterizedTest
    @MethodSource("parameters__testAppendSubFile__error_when_absolute_path_is_specified")
    public void testAppendSubFile__error_when_absolute_path_is_specified(
            FileTreeItem child1
    ) {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);

        try {
            baseNode.appendSubFile(child1, TreePath.get("/dir1", "dir1-1", "child1"));
            fail("expected exception has not been thrown");

        } catch (IllegalArgumentException e) {
            // expected exception
            assertEquals("relativePath must not be absolute: /dir1/dir1-1/child1", e.getMessage());
        }
    }

    private static Stream<Arguments> parameters__testAppendSubFile__error_when_empty_path_is_specified() {
        return Stream.of(
                Arguments.arguments(new FileTreeItemEmptyFile()),
                Arguments.arguments(new FileTreeItemDirectory())
        );
    }

    @ParameterizedTest
    @MethodSource("parameters__testAppendSubFile__error_when_empty_path_is_specified")
    public void testAppendSubFile__error_when_empty_path_is_specified(
            FileTreeItem child1
    ) {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);

        try {
            baseNode.appendSubFile(child1, TreePath.get(""));
            fail("expected exception has not been thrown");

        } catch (IllegalArgumentException e) {
            // expected exception
            assertEquals("illegal child path: ", e.getMessage());
        }
    }

    @Test
    public void testGetNodeByRelativePath() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem child1 = new FileTreeItemDirectory();
        final FileTreeItem child2 = new FileTreeItemEmptyFile();
        final TreePath child1Path = TreePath.get("dir1", "dir1-1", "child1");
        final TreePath child2Path = TreePath.get("dir1", "dir1-1", "child2");

        baseNode.appendSubFile(child1, child1Path);
        baseNode.appendSubFile(child2, child2Path);

        try {
            assertEquals(child1, baseNode.getNodeByRelativePath(child1Path).getFile());
            assertEquals(child2, baseNode.getNodeByRelativePath(child2Path).getFile());
            assertEquals("dir1", baseNode.getNodeByRelativePath(TreePath.get("dir1")).getName());
        } catch (NotDirectoryException | NoSuchFileException e) {
            fail(e);
        }
    }

    @Test
    public void testGetNodeByRelativePath__return_itself() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem child1 = new FileTreeItemDirectory();
        final FileTreeItem child2 = new FileTreeItemEmptyFile();
        final TreePath child1Path = TreePath.get("dir1", "dir1-1", "child1");
        final TreePath child2Path = TreePath.get("dir1", "dir1-1", "child2");

        baseNode.appendSubFile(child1, child1Path);
        baseNode.appendSubFile(child2, child2Path);

        try {
            assertEquals(baseNode, baseNode.getNodeByRelativePath(TreePath.get("")));
            assertEquals(baseNode, baseNode.getNodeByRelativePath(TreePath.get(".")));
            assertEquals(baseNode, baseNode.getNodeByRelativePath(TreePath.get(".", ".")));

            final FileTreeNode dir1Node = baseNode.getNodeByRelativePath(TreePath.get("./dir1"));
            final FileTreeNode dir1_1Node = baseNode.getNodeByRelativePath(TreePath.get("./dir1/./dir1-1"));
            final FileTreeNode child2Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/./dir1-1/child2"));

            assertEquals("dir1", dir1Node.getName());
            assertEquals(dir1Node, dir1Node.getNodeByRelativePath(TreePath.get(".")));
            assertEquals("dir1-1", dir1_1Node.getName());
            assertEquals(dir1_1Node, dir1_1Node.getNodeByRelativePath(TreePath.get(".")));

            // assert that even if child2 is a regular file, it can resolve "."
            assertEquals("child2", child2Node.getName());
            assertTrue(child2Node.isFile());
            assertEquals(child2Node, child2Node.getNodeByRelativePath(TreePath.get(".")));

        } catch (NotDirectoryException | NoSuchFileException e) {
            fail(e);
        }
    }

    @Test
    public void testGetNodeByRelativePath__return_parent() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem child1 = new FileTreeItemDirectory();
        final FileTreeItem child2 = new FileTreeItemEmptyFile();
        final TreePath child1Path = TreePath.get("dir1", "dir1-1", "child1");
        final TreePath child2Path = TreePath.get("dir1", "dir1-1", "child2");

        baseNode.appendSubFile(child1, child1Path);
        baseNode.appendSubFile(child2, child2Path);

        try {
            // the parent of the root is the root itself
            assertEquals(baseNode, baseNode.getNodeByRelativePath(TreePath.get("..")));

            final FileTreeNode dir1Node = baseNode.getNodeByRelativePath(TreePath.get("../dir1"));
            final FileTreeNode dir1_1Node = baseNode.getNodeByRelativePath(TreePath.get("../dir1/../dir1/dir1-1"));
            final FileTreeNode child2Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/../dir1/dir1-1/child2"));

            assertEquals("dir1", dir1Node.getName());
            assertEquals(baseNode, dir1Node.getNodeByRelativePath(TreePath.get("..")));
            assertEquals("dir1-1", dir1_1Node.getName());
            assertEquals(dir1Node, dir1_1Node.getNodeByRelativePath(TreePath.get("..")));
            assertEquals(baseNode, dir1_1Node.getNodeByRelativePath(TreePath.get("../..")));

            // assert that even if child2 is a regular file, it can resolve ".."
            assertEquals("child2", child2Node.getName());
            assertTrue(child2Node.isFile());
            assertEquals(dir1_1Node, child2Node.getNodeByRelativePath(TreePath.get("..")));

        } catch (NotDirectoryException | NoSuchFileException e) {
            fail(e);
        }
    }

    @Test
    public void testGetNodeByRelativePath__error_when_absolute_path_is_specified() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem child1 = new FileTreeItemDirectory();
        final TreePath child1Path = TreePath.get("dir1", "dir1-1", "child1");

        baseNode.appendSubFile(child1, child1Path);

        try {
            baseNode.getNodeByRelativePath(TreePath.get("/dir1/dir1-1/child1"));
            fail("expected exception has not been thrown");

        } catch (IllegalArgumentException e) {
            // expected exception
            assertEquals("relativePath must not be absolute: /dir1/dir1-1/child1", e.getMessage());

        } catch (NotDirectoryException | NoSuchFileException e) {
            fail(e);
        }
    }

    @Test
    public void testGetNodeByRelativePath__error_when_item_in_a_regular_file_is_specified() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem child1 = new FileTreeItemEmptyFile();
        final TreePath child1Path = TreePath.get("dir1", "dir1-1", "child1");

        baseNode.appendSubFile(child1, child1Path);

        try {
            baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1/child1/dummy"));
            fail("expected exception has not been thrown");

        } catch (NotDirectoryException e) {
            // expected exception
            // TODO: "dir1/dir1-1/child1" であるべきでは。
            assertEquals("/dir1/dir1-1/child1", e.getMessage());

        } catch (NoSuchFileException e) {
            fail(e);
        }
    }

    @Test
    public void testGetNodeByRelativePath__error_when_the_specified_file_is_not_found() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem child1 = new FileTreeItemDirectory();
        final TreePath child1Path = TreePath.get("dir1", "dir1-1", "child1");

        baseNode.appendSubFile(child1, child1Path);

        try {
            baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1/child2"));
            fail("expected exception has not been thrown");

        } catch (NoSuchFileException e) {
            // expected exception
            assertEquals("dir1/dir1-1/child2", e.getMessage());

        } catch (NotDirectoryException e) {
            fail(e);
        }
    }

    @Test
    public void testGetAbsolutePath() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem child1 = new FileTreeItemDirectory();
        final FileTreeItem child2 = new FileTreeItemEmptyFile();
        final TreePath child1Path = TreePath.get("dir1", "dir1-1", "child1");
        final TreePath child2Path = TreePath.get("dir1", "dir1-1", "child2");

        baseNode.appendSubFile(child1, child1Path);
        baseNode.appendSubFile(child2, child2Path);

        try {
            final FileTreeNode dir1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1"));
            final FileTreeNode dir1_1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1"));
            final FileTreeNode child2Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1/child2"));

            assertEquals("/", baseNode.getAbsolutePath());
            assertEquals("/dir1", dir1Node.getAbsolutePath());
            assertEquals("/dir1/dir1-1", dir1_1Node.getAbsolutePath());
            assertEquals("/dir1/dir1-1/child2", child2Node.getAbsolutePath());

        } catch (NotDirectoryException | NoSuchFileException e) {
            fail(e);
        }
    }

    @Test
    public void testIsHidden() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem child1 = new FileTreeItemDirectory();
        final FileTreeItem child2 = new FileTreeItemEmptyFile();
        final TreePath child1Path = TreePath.get("dir1", "dir1-1", "child1");
        final TreePath child2Path = TreePath.get("dir1", "dir1-1", "child2");

        baseNode.appendSubFile(child1, child1Path);
        baseNode.appendSubFile(child2, child2Path);

        try {
            final FileTreeNode dir1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1"));
            final FileTreeNode dir1_1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1"));
            final FileTreeNode child2Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1/child2"));

            assertFalse(baseNode.isHidden());
            assertFalse(dir1Node.isHidden());
            assertFalse(dir1_1Node.isHidden());
            assertFalse(child2Node.isHidden());

        } catch (NotDirectoryException | NoSuchFileException e) {
            fail(e);
        }
    }

    @Test
    public void testDoesExists() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem child1 = new FileTreeItemDirectory();
        final FileTreeItem child2 = new FileTreeItemEmptyFile();
        final TreePath child1Path = TreePath.get("dir1", "dir1-1", "child1");
        final TreePath child2Path = TreePath.get("dir1", "dir1-1", "child2");

        baseNode.appendSubFile(child1, child1Path);
        baseNode.appendSubFile(child2, child2Path);

        try {
            final FileTreeNode dir1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1"));
            final FileTreeNode dir1_1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1"));
            final FileTreeNode child2Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1/child2"));

            assertTrue(baseNode.doesExist());
            assertTrue(dir1Node.doesExist());
            assertTrue(dir1_1Node.doesExist());
            assertTrue(child2Node.doesExist());

        } catch (NotDirectoryException | NoSuchFileException e) {
            fail(e);
        }
    }

    @Test
    public void testIsDirectory() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem child1 = new FileTreeItemDirectory();
        final FileTreeItem child2 = new FileTreeItemEmptyFile();
        final TreePath child1Path = TreePath.get("dir1", "dir1-1", "child1");
        final TreePath child2Path = TreePath.get("dir1", "dir1-1", "child2");

        baseNode.appendSubFile(child1, child1Path);
        baseNode.appendSubFile(child2, child2Path);

        try {
            final FileTreeNode dir1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1"));
            final FileTreeNode dir1_1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1"));
            final FileTreeNode child2Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1/child2"));

            assertTrue(baseNode.isDirectory());
            assertTrue(dir1Node.isDirectory());
            assertTrue(dir1_1Node.isDirectory());
            assertFalse(child2Node.isDirectory());

        } catch (NotDirectoryException | NoSuchFileException e) {
            fail(e);
        }
    }

    @Test
    public void testIsFile() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem child1 = new FileTreeItemDirectory();
        final FileTreeItem child2 = new FileTreeItemEmptyFile();
        final TreePath child1Path = TreePath.get("dir1", "dir1-1", "child1");
        final TreePath child2Path = TreePath.get("dir1", "dir1-1", "child2");

        baseNode.appendSubFile(child1, child1Path);
        baseNode.appendSubFile(child2, child2Path);

        try {
            final FileTreeNode dir1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1"));
            final FileTreeNode dir1_1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1"));
            final FileTreeNode child2Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1/child2"));

            assertFalse(baseNode.isFile());
            assertFalse(dir1Node.isFile());
            assertFalse(dir1_1Node.isFile());
            assertTrue(child2Node.isFile());

        } catch (NotDirectoryException | NoSuchFileException e) {
            fail(e);
        }
    }

    @Test
    public void testIsReadable() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem child1 = new FileTreeItemDirectory();
        final FileTreeItem child2 = new FileTreeItemEmptyFile();
        final TreePath child1Path = TreePath.get("dir1", "dir1-1", "child1");
        final TreePath child2Path = TreePath.get("dir1", "dir1-1", "child2");

        baseNode.appendSubFile(child1, child1Path);
        baseNode.appendSubFile(child2, child2Path);

        try {
            final FileTreeNode dir1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1"));
            final FileTreeNode dir1_1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1"));
            final FileTreeNode child2Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1/child2"));

            assertTrue(baseNode.isReadable());
            assertTrue(dir1Node.isReadable());
            assertTrue(dir1_1Node.isReadable());
            assertTrue(child2Node.isReadable());

        } catch (NotDirectoryException | NoSuchFileException e) {
            fail(e);
        }
    }

    @Test
    public void testIsWritable() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem child1 = new FileTreeItemDirectory();
        final FileTreeItem child2 = new FileTreeItemEmptyFile();
        final TreePath child1Path = TreePath.get("dir1", "dir1-1", "child1");
        final TreePath child2Path = TreePath.get("dir1", "dir1-1", "child2");

        baseNode.appendSubFile(child1, child1Path);
        baseNode.appendSubFile(child2, child2Path);

        try {
            final FileTreeNode dir1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1"));
            final FileTreeNode dir1_1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1"));
            final FileTreeNode child2Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1/child2"));

            assertFalse(baseNode.isWritable());
            assertFalse(dir1Node.isWritable());
            assertFalse(dir1_1Node.isWritable());
            assertFalse(child2Node.isWritable());

        } catch (NotDirectoryException | NoSuchFileException e) {
            fail(e);
        }
    }

    @Test
    public void testIsRemovable() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem child1 = new FileTreeItemDirectory();
        final FileTreeItem child2 = new FileTreeItemEmptyFile();
        final TreePath child1Path = TreePath.get("dir1", "dir1-1", "child1");
        final TreePath child2Path = TreePath.get("dir1", "dir1-1", "child2");

        baseNode.appendSubFile(child1, child1Path);
        baseNode.appendSubFile(child2, child2Path);

        try {
            final FileTreeNode dir1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1"));
            final FileTreeNode dir1_1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1"));
            final FileTreeNode child2Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1/child2"));

            assertFalse(baseNode.isRemovable());
            assertFalse(dir1Node.isRemovable());
            assertFalse(dir1_1Node.isRemovable());
            assertFalse(child2Node.isRemovable());

        } catch (NotDirectoryException | NoSuchFileException e) {
            fail(e);
        }
    }

    @Test
    public void testGetLastModified() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem child1 = new FileTreeItemDirectory();
        final FileTreeItem child2 = new FileTreeItemEmptyFile();
        final TreePath child1Path = TreePath.get("dir1", "dir1-1", "child1");
        final TreePath child2Path = TreePath.get("dir1", "dir1-1", "child2");

        baseNode.appendSubFile(child1, child1Path);
        baseNode.appendSubFile(child2, child2Path);

        try {
            final FileTreeNode dir1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1"));
            final FileTreeNode dir1_1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1"));
            final FileTreeNode child2Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1/child2"));

            assertInstanceOf(Long.class, baseNode.getLastModified());
            assertInstanceOf(Long.class, dir1Node.getLastModified());
            assertInstanceOf(Long.class, dir1_1Node.getLastModified());
            assertInstanceOf(Long.class, child2Node.getLastModified());

        } catch (NotDirectoryException | NoSuchFileException e) {
            fail(e);
        }
    }

    @Test
    public void testSetLastModified() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem child1 = new FileTreeItemDirectory();
        final FileTreeItem child2 = new FileTreeItemEmptyFile();
        final TreePath child1Path = TreePath.get("dir1", "dir1-1", "child1");
        final TreePath child2Path = TreePath.get("dir1", "dir1-1", "child2");

        baseNode.appendSubFile(child1, child1Path);
        baseNode.appendSubFile(child2, child2Path);

        try {
            final FileTreeNode dir1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1"));
            final FileTreeNode dir1_1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1"));
            final FileTreeNode child2Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1/child2"));

            assertFalse(baseNode.setLastModified(1));
            assertFalse(dir1Node.setLastModified(2));
            assertFalse(dir1_1Node.setLastModified(3));
            assertFalse(child2Node.setLastModified(4));

        } catch (NotDirectoryException | NoSuchFileException e) {
            fail(e);
        }
    }

    @Test
    public void testMkdir() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem child1 = new FileTreeItemDirectory();
        final FileTreeItem child2 = new FileTreeItemEmptyFile();
        final TreePath child1Path = TreePath.get("dir1", "dir1-1", "child1");
        final TreePath child2Path = TreePath.get("dir1", "dir1-1", "child2");

        baseNode.appendSubFile(child1, child1Path);
        baseNode.appendSubFile(child2, child2Path);

        try {
            final FileTreeNode dir1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1"));
            final FileTreeNode dir1_1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1"));
            final FileTreeNode child2Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1/child2"));

            assertFalse(baseNode.mkdir());
            assertFalse(dir1Node.mkdir());
            assertFalse(dir1_1Node.mkdir());
            assertFalse(child2Node.mkdir());

        } catch (NotDirectoryException | NoSuchFileException e) {
            fail(e);
        }
    }

    @Test
    public void testDelete() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem child1 = new FileTreeItemDirectory();
        final FileTreeItem child2 = new FileTreeItemEmptyFile();
        final TreePath child1Path = TreePath.get("dir1", "dir1-1", "child1");
        final TreePath child2Path = TreePath.get("dir1", "dir1-1", "child2");

        baseNode.appendSubFile(child1, child1Path);
        baseNode.appendSubFile(child2, child2Path);

        try {
            final FileTreeNode dir1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1"));
            final FileTreeNode dir1_1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1"));
            final FileTreeNode child2Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1/child2"));

            assertFalse(baseNode.delete());
            assertFalse(dir1Node.delete());
            assertFalse(dir1_1Node.delete());
            assertFalse(child2Node.delete());

        } catch (NotDirectoryException | NoSuchFileException e) {
            fail(e);
        }
    }

    @Test
    public void testMove() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem child1 = new FileTreeItemDirectory();
        final FileTreeItem child2 = new FileTreeItemEmptyFile();
        final TreePath child1Path = TreePath.get("dir1", "dir1-1", "child1");
        final TreePath child2Path = TreePath.get("dir1", "dir1-1", "child2");

        baseNode.appendSubFile(child1, child1Path);
        baseNode.appendSubFile(child2, child2Path);

        try {
            final FileTreeNode dir1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1"));
            final FileTreeNode dir1_1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1"));
            final FileTreeNode child2Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1/child2"));

            assertFalse(baseNode.move(baseNode));
            assertFalse(dir1Node.move(baseNode));
            assertFalse(dir1_1Node.move(baseNode));
            assertFalse(child2Node.move(baseNode));

        } catch (NotDirectoryException | NoSuchFileException e) {
            fail(e);
        }
    }

    @Test
    public void testGetPhysicalFile() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem child1 = new FileTreeItemDirectory();
        final FileTreeItem child2 = new FileTreeItemEmptyFile();
        final TreePath child1Path = TreePath.get("dir1", "dir1-1", "child1");
        final TreePath child2Path = TreePath.get("dir1", "dir1-1", "child2");

        baseNode.appendSubFile(child1, child1Path);
        baseNode.appendSubFile(child2, child2Path);

        try {
            final FileTreeNode dir1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1"));
            final FileTreeNode dir1_1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1"));
            final FileTreeNode child2Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1/child2"));

            assertNull(baseNode.getPhysicalFile());
            assertNull(dir1Node.getPhysicalFile());
            assertNull(dir1_1Node.getPhysicalFile());
            assertNull(child2Node.getPhysicalFile());

        } catch (NotDirectoryException | NoSuchFileException e) {
            fail(e);
        }
    }

    @Test
    public void testGetLinkCount() {
        final FileTreeNode baseNode = new FileTreeNode(new FileTreeItemDirectory(), null);
        final FileTreeItem child1 = new FileTreeItemDirectory();
        final FileTreeItem child2 = new FileTreeItemEmptyFile();
        final TreePath child1Path = TreePath.get("dir1", "dir1-1", "child1");
        final TreePath child2Path = TreePath.get("dir1", "dir1-1", "child2");

        baseNode.appendSubFile(child1, child1Path);
        baseNode.appendSubFile(child2, child2Path);

        try {
            final FileTreeNode dir1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1"));
            final FileTreeNode dir1_1Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1"));
            final FileTreeNode child2Node = baseNode.getNodeByRelativePath(TreePath.get("dir1/dir1-1/child2"));

            assertEquals(0, baseNode.getLinkCount());
            assertEquals(0, dir1Node.getLinkCount());
            assertEquals(0, dir1_1Node.getLinkCount());
            assertEquals(0, child2Node.getLinkCount());

        } catch (NotDirectoryException | NoSuchFileException e) {
            fail(e);
        }
    }

    @Test
    public void testCreateInputStream() {
        try (final InputStream inputStreamStub = new InputStreamStub()) {
            final FileTreeItem fileTreeItem = new FileTreeItemStub(inputStreamStub, null);
            final FileTreeNode fileTreeNode = new FileTreeNode(fileTreeItem, "file");

            // FileTreeNode#createInputStream must return the result of FileTreeItem#createInputStream
            final InputStream actualInputStream = fileTreeNode.createInputStream(0);
            assertEquals(inputStreamStub, actualInputStream);

        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    public void testCreateOutputStream() {
        try (final OutputStream outputStreamStub = new OutputStreamStub()) {
            final FileTreeItem fileTreeItem = new FileTreeItemStub(null, outputStreamStub);
            final FileTreeNode fileTreeNode = new FileTreeNode(fileTreeItem, "file");

            // FileTreeNode#createInputStream must return the result of FileTreeItem#createInputStream
            final OutputStream actualOutputStream = fileTreeNode.createOutputStream(0);
            assertEquals(outputStreamStub, actualOutputStream);

        } catch (IOException e) {
            fail(e);
        }
    }

    private static class FileTreeItemStub implements FileTreeItem {
        private final InputStream inputStreamStub;
        private final OutputStream outputStreamStub;

        public FileTreeItemStub(InputStream inputStreamStub, OutputStream outputStreamStub) {
            this.inputStreamStub = inputStreamStub;
            this.outputStreamStub = outputStreamStub;
        }

        @Override
        public boolean isDirectory() {
            return false;
        }

        @Override
        public OutputStream createOutputStream(long offset) {
            return outputStreamStub;
        }

        @Override
        public InputStream createInputStream(long offset) {
            return inputStreamStub;
        }
    }

    private static class OutputStreamStub extends OutputStream {
        @Override
        public void write(int b) {
            throw new UnsupportedOperationException("stub");
        }
    }

    private static class InputStreamStub extends InputStream {
        @Override
        public int read() {
            throw new UnsupportedOperationException("stub");
        }
    }
}
