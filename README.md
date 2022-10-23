# Mapped FTP Server 0.2.x

This FTP server provides files that are statically defined by an XML file.

# Steps to try this server

1. Verify that JAVA >= 8 is installed by a command `java -version`. (This server has been tested in Java 8, 11, and 17)

2. Create `lib` directory and collect required JARs in the `lib` directory. mappedftp:0.2.x requires the following JARs:
    - mappedftp:0.2.x (download the release from this repository)
    - org.apache.ftpserver:ftpserver-core:1.2.0
    - org.apache.ftpserver:ftplet-api:1.2.0
    - org.apache.mina:mina-core:2.1.6
    - org.slf4j:slf4j-api:1.7.36
    - org.springframework:spring-beans:5.3.23
    - org.springframework:spring-context:5.3.23
    - org.springframework:spring-core:5.3.23
    - org.springframework:spring-expression:5.3.23
    - (If you want logs, some SLF4J implement is also required.)

3. Run the main method of `jp.unaguna.mappedftp.MappedFtpServer`. For example:
    ```shell
    java -cp 'lib/*' jp.unaguna.mappedftp.MappedFtpServer
    ```
   This command starts the mapped-ftp server with the default configurations.

4. access to `ftp://localhost` with anonymous account

   The following files are provided.

    - `/default-config/default_config.xml`
        - The configuration file used now.
    - `/default-config/default_user.properties`
        - The user configuration file used now.
    - `/LICENSE`
        - The license of this project. It is got from this repository's [LICENSE](./LICENSE) by HTTPS.

# Configure

You can apply non-default settings by passing the path to a configuration file as an argument. For example:

```shell
java -cp 'lib/*' jp.unaguna.mappedftp.MappedFtpServer ./config.xml
```

## Configuration by XML

This file is for the most part similar
to [the Apache FTP server configuration file](https://mina.apache.org/ftpserver-project/configuration.html),
with the following changes.

See also [the default configuration](src/main/resources/mapped-ftpd-default.xml).

### XML name space `http://mappedftp.unaguna.jp/mapped-ftpserver`

Use namespace `http://mappedftp.unaguna.jp/mapped-ftpserver` instead of `http://mina.apache.org/ftpserver/spring/v1`.
This allows the use of configuration items specific to MappedFtpServer.

### Tag `<mapped-filesystem>`

Configure files to serve. See child node description for details.

### Tag `<local-file>` in `<mapped-filesystem>`

The specified file in the local filesystem is served by FTP server.

| Attribute     |                                                                                                                                                            |
|---------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| path          | (Required) The path in the FTP filesystem. Must be UNIX style.                                                                                             |
| src           | (Required) The source of the file content. This is the filepath in the local filesystem.                                                                   |
| last-modified | (Optional) The last modified time of the file. If not specified, the default name is used. See also [Attribute `last-modified`](#attribute-last-modified). |
| owner         | (Optional) The owner name of the file. If not specified, the default name is used.                                                                         |
| group         | (Optional) The group name of the file. If not specified, the default name is used.                                                                         |

### Tag `<classpath-file>` in `<mapped-filesystem>`

The specified file in classpath is served by FTP server.

| Attribute     |                                                                                                                                                            |
|---------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| path          | (Required) The path in the FTP filesystem. Must be UNIX style.                                                                                             |
| src           | (Required) The source of the file content. This is the resource path in the class loader.                                                                  |
| last-modified | (Optional) The last modified time of the file. If not specified, the default name is used. See also [Attribute `last-modified`](#attribute-last-modified). |
| owner         | (Optional) The owner name of the file. If not specified, the default name is used.                                                                         |
| group         | (Optional) The group name of the file. If not specified, the default name is used.                                                                         |

### Tag `<url-file>` in `<mapped-filesystem>`

The content specified by URL is served by FTP server.

| Attribute     |                                                                                                                                                            |
|---------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| path          | (Required) The path in the FTP filesystem. Must be UNIX style.                                                                                             |
| src           | (Required) The source of the file content. This is the URL such as `http://...` or `ftp://...`.                                                            |
| last-modified | (Optional) The last modified time of the file. If not specified, the default name is used. See also [Attribute `last-modified`](#attribute-last-modified). |
| owner         | (Optional) The owner name of the file. If not specified, the default name is used.                                                                         |
| group         | (Optional) The group name of the file. If not specified, the default name is used.                                                                         |

### Attribute `last-modified`

The `last-modified` attribute mentioned above can have the values like following:

- `2022-01-01T11:22:33` - ISO8601 extended format without timezone
    - It is interpreted as the time in the system's time zone.
- `2022-01-01T11:22:33Z` - ISO8601 extended format with UTC
- `2022-01-01T11:22:33+09:00` - ISO8601 extended format with time offset
- `1641036153000` - the milliseconds passed from 1970-01-01T00:00:00Z
    - like `Date#getTime` and `Instant#toEpochMilli`

# Embedded Usage

This FTP server can also be used within Java code.
Originally,
[Apache FTP server can be embedded in java code](https://mina.apache.org/ftpserver-project/embedding_ftpserver.html),
but by using `jp.unaguna.mappedftp.filesystem.MappingServer`,
the Mapped-FTP-Server functionality can also be used.

For example, a server can be started as follows.

```java
import jp.unaguna.mappedftp.filesystem.FileSystemDefinitionException;
import jp.unaguna.mappedftp.filesystem.MappingFileSystemFactory;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeItem;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeItemFromClasspath;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeItemFromLocalFile;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeItemFromURL;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FtpException;

import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;

class MyServer {
    public static void main(String[] args) throws java.net.MalformedURLException, FileSystemDefinitionException, FtpException {
        // start with a factory of Apache FTP Server
        FtpServerFactory serverFactory = new FtpServerFactory();

        // settings (e.g. https://mina.apache.org/ftpserver-project/embedding_ftpserver.html)

        // define my filesystem with Mapped FTP Server's class
        HashMap<String, FileTreeItem> fileMapping = new HashMap<>() {{
            // file content will be got from GitHub by HTTPS
            put("/README",
                    new FileTreeItemFromURL(new URL("https://raw.githubusercontent.com/unaguna/mapped-ftp/main/README.md")));

            // file content will be got from classpath
            put("/config/config_example.xml",
                    new FileTreeItemFromClasspath("/mapped-ftpd-default.xml"));

            // file content will be read from local file (if not exists, download will fail)
            put("/my.txt",
                    new FileTreeItemFromLocalFile(Paths.get("./my.txt")));
        }};
        FileSystemFactory myFileSystemFactory = new MappingFileSystemFactory(fileMapping);

        // apply my filesystem
        serverFactory.setFileSystem(myFileSystemFactory);

        // start the server
        FtpServer server = serverFactory.createServer();
        server.start();
    }
}
```
