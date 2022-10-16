# Mapped FTP Server 0.1.x

This FTP server provides files that are statically defined by an XML file.

# Steps to try this server

1. Verify that JAVA >= 8 is installed by a command `java -version`. (This server has been tested in Java 8, 11, and 17)

2. Create `lib` directory and collect required JARs in the `lib` directory. mappedftp:0.1.x requires the following JARs:
    - mappedftp:0.1.x (download the release from this repository)
    - org.apache.ftpserver:ftpserver-core:1.2.0
    - org.apache.ftpserver:ftplet-api:1.2.0
    - org.apache.mina:mina-core:2.1.6
    - org.slf4j:slf4j-api:1.7.36
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

From here, the contents of the two types of files required for configuration are described.

## Base Configuration

This file is specified as an argument at runtime and is the configuration file for the entire FTP server.

This file is written by XML. The root tag is `<MappedFtp>`.

See also [the default configuration](src/main/resources/mapped-ftpd-default.xml).

### Tag `<file-user-manager>`

Configure settings related to users.

| Attribute         |                                                                                                                                                                                                                                                 |
|-------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| file              | (Required) The filepath or the resource path of the user configuration file.                                                                                                                                                                    |
| encrypt-passwords | (Optional) An encryption of password written in the user configuration file. "clear", "md5", "salted", or a classpath of class which implements PasswordEncryptor (it must be able to construct without any arguments). Default value is "md5". |

### Tag `<files>`

Configure files to serve. See child node description for details.

### Tag `<file>` in `<files>`

It defines a file to serve.

| Attribute |                                                                                                                                                                                                                                                         |
|-----------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| type      | (Required) The type of source. "local", "url", and "classpath".                                                                                                                                                                                         |
| path      | (Required) The path in the FTP filesystem. Must be UNIX style.                                                                                                                                                                                          |
| src       | (Required) The source of the file content. If type is "local", src is the filepath in the local filesystem. If type is "url", src is the URL such as `http://...` or `ftp://...`. If type is "classpath", src is the resource path in the class loader. |

## User Configuration

This file is specified in base configuration files and is the configuration of users.

The format of this file is same as the one of Apache FTP server.

See also [the default configuration](src/main/resources/mapped-ftpd-default-users.properties).
