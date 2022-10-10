package jp.unaguna.mappedftp.config;

import jp.unaguna.mappedftp.encrypt.PasswordEncryptorType;
import jp.unaguna.mappedftp.map.AttributeHashMap;
import jp.unaguna.mappedftp.user.ConfigurablePropertiesUserManagerFactory;
import jp.unaguna.mappedftp.utils.ClasspathUtils;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * A loader of {@link ServerConfig}.
 */
public class ServerConfigLoader {
    private static final String ROOT_TAG_NAME = "MappedFtp";

    /**
     * Load a XML configuration file.
     *
     * @param configPath the path of the configuration file to read
     * @return a configuration object
     * @throws ConfigException when the configuration file specifies illegal configuration
     * @throws IOException     when IO error is occurred within reading config file
     * @throws SAXException    when the configuration file is not legal XML file
     */
    public ServerConfig load(Path configPath) throws ConfigException, IOException, SAXException {
        try (InputStream configAsStream = Files.newInputStream(configPath)) {
            return load(configAsStream);
        }
    }

    /**
     * Load a XML configuration file.
     *
     * @param configUrl the url of the configuration file to read
     * @return a configuration object
     * @throws ConfigException when the configuration file specifies illegal configuration
     * @throws IOException     when IO error is occurred within reading config file
     * @throws SAXException    when the configuration file is not legal XML file
     */
    public ServerConfig load(URL configUrl) throws ConfigException, IOException, SAXException {
        try (InputStream configAsStream = configUrl.openStream()) {
            return load(configAsStream);
        }
    }

    /**
     * Load a XML configuration file.
     *
     * @param configFile configuration file to read
     * @return a configuration object
     * @throws ConfigException when the configuration file specifies illegal configuration
     * @throws IOException     when IO error is occurred within reading config file
     * @throws SAXException    when the configuration file is not legal XML file
     */
    public ServerConfig load(InputStream configFile) throws ConfigException, IOException, SAXException {
        DocumentBuilderFactory documentbuilderfactory = DocumentBuilderFactory.newInstance();
        documentbuilderfactory.setIgnoringComments(true);
        final DocumentBuilder documentbuilder;
        try {
            documentbuilder = documentbuilderfactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        Document document = documentbuilder.parse(configFile);

        return load(document);
    }

    /**
     * Load a XML configuration.
     *
     * @param xmlDocument the configuration expressed by XML
     * @return a configuration object
     * @throws ConfigException when the configuration specifies illegal configuration
     */
    public ServerConfig load(Document xmlDocument) throws ConfigException {
        LoadContext context = new LoadContext();
        ServerConfig config = new ServerConfig();
        Element root = xmlDocument.getDocumentElement();

        // ルート要素が期待と違えば例外
        if (!ROOT_TAG_NAME.equals(root.getTagName())) {
            throw new ConfigException("The root element of the configuration file must be " + ROOT_TAG_NAME);
        }

        NodeList elements = root.getChildNodes();
        for (int i = 0; i < elements.getLength(); i++) {
            Node element = elements.item(i);
            String tagName = element.getNodeName();

            switch (tagName) {
                case "file-user-manager":
                    // Reflection in config is done after loading the xml file.
                    context.fileManagerNode.add(element);
                    break;
                case "files":
                    appendFilesElement(config, element);
                    break;
                case "#text":
                    // インデントなどは無視する
                    break;
                default:
                    throw new ConfigException("Unexpected tag found: " + tagName);
            }
        }

        appendUserManagerElement(config, context);

        return config;
    }

    /**
     * Load values from &lt;*-user-manager&gt; element and put them into {@link ServerConfig} object.
     *
     * @param config  the configuration object to edit
     * @param context the context of loading
     * @throws ConfigException when the xml element specifies illegal configuration
     */
    private void appendUserManagerElement(ServerConfig config, LoadContext context) throws ConfigException {
        if (context.fileManagerNode.size() > 1) {
            throw new ConfigException("Multiple user managers cannot be specified.");

        } else if (context.fileManagerNode.size() == 1) {
            appendUserManagerElement(config, context.fileManagerNode.get(0));

        }
    }

    /**
     * Load values from &lt;*-user-manager&gt; element and put them into {@link ServerConfig} object.
     *
     * @param config             the configuration object to edit
     * @param userManagerElement the xml element
     * @throws ConfigException when the xml element specifies illegal configuration
     */
    private void appendUserManagerElement(ServerConfig config, Node userManagerElement) throws ConfigException {
        final String tagName = userManagerElement.getNodeName();
        switch (tagName) {
            case "file-user-manager":
                appendFileUserManagerElement(config, userManagerElement);
                break;
            default:
                throw new ConfigException("Unexpected tag found: " + tagName);
        }
    }

    /**
     * Load values from &lt;file-user-manager&gt; element and put them into {@link ServerConfig} object.
     *
     * @param config             the configuration object to edit
     * @param userManagerElement the xml element
     * @throws ConfigException when the xml element specifies illegal configuration
     */
    private void appendFileUserManagerElement(ServerConfig config, Node userManagerElement) throws ConfigException {
        final String TAG_NAME = "file-user-manager";
        final NodeList childElements = userManagerElement.getChildNodes();
        final NamedNodeMap attributes = userManagerElement.getAttributes();

        config.setUserManagerFactoryClass(ConfigurablePropertiesUserManagerFactory.class);

        for (int i = 0; i < childElements.getLength(); i++) {
            final Node element = childElements.item(i);
            final String tagName = element.getNodeName();

            switch (tagName) {
                case "#text":
                    // インデントなどは無視する
                    break;
                default:
                    throw new ConfigException("Unexpected tag found in <" + TAG_NAME + ">: " + tagName);
            }
        }

        for (int i = 0; i < attributes.getLength(); i++) {
            final Attr attribute = (Attr) attributes.item(i);
            final String attributeName = attribute.getName();
            final String attributeValue = attribute.getValue();

            switch (attributeName) {
                case "file":
                    appendFileUserManagerElementFileAttribute(config, attribute);
                    break;
                case "encrypt-passwords":
                    appendFileUserManagerElementEncryptPasswordAttribute(config, attribute);
                    break;
                default:
                    throw new ConfigException("Unexpected attribute found in <" + TAG_NAME + ">: " +
                            attributeName + "=\"" + attributeValue + "\"");
            }
        }
    }

    /**
     * Load values from file attribute of &lt;file-user-manager&gt; element and put it into {@link ServerConfig} object.
     *
     * @param config        the configuration object to edit
     * @param fileAttribute the xml element
     * @throws ConfigException when the xml element specifies illegal configuration
     */
    private void appendFileUserManagerElementFileAttribute(ServerConfig config, Attr fileAttribute)
            throws ConfigException {
        final String attributeName = fileAttribute.getName();
        final String attributeValue = fileAttribute.getValue();

        try {
            config.setUserPropertiesPath(attributeValue);
        } catch (InvalidPathException e) {
            throw new ConfigException("Unexpected value is appended to the attribute \"" +
                    attributeName + "\": " + attributeValue, e);
        }
    }

    /**
     * Load values from encrypt-passwords attribute of &lt;file-user-manager&gt; element and put it into {@link ServerConfig} object.
     *
     * @param config                   the configuration object to edit
     * @param encryptPasswordAttribute the xml element
     * @throws ConfigException when the xml element specifies illegal configuration
     */
    private void appendFileUserManagerElementEncryptPasswordAttribute(ServerConfig config, Attr encryptPasswordAttribute)
            throws ConfigException {
        final String attributeName = encryptPasswordAttribute.getName();
        final String attributeValue = encryptPasswordAttribute.getValue();

        final PasswordEncryptorType passwordEncryptorType = PasswordEncryptorType.orNull(attributeValue);
        final Class<? extends PasswordEncryptor> passwordEncryptor;
        if (passwordEncryptorType != null) {
            passwordEncryptor = passwordEncryptorType.getPasswordEncryptorClass();
        } else {
            try {
                passwordEncryptor = ClasspathUtils.getClass(attributeValue, PasswordEncryptor.class);
            } catch (ClassNotFoundException | ClassCastException e) {
                throw new ConfigException("Unexpected value is appended to the attribute \"" +
                        attributeName + "\": " + attributeValue);
            }
        }
        config.setPasswordEncryptorClass(passwordEncryptor);
    }

    /**
     * Load values from &lt;files&gt; element and put them into {@link ServerConfig} object.
     *
     * @param config       the configuration object to edit
     * @param filesElement the xml element
     * @throws ConfigException when the xml element specifies illegal configuration
     */
    private void appendFilesElement(ServerConfig config, Node filesElement) throws ConfigException {
        final String TAG_NAME = "files";
        NodeList fileElements = filesElement.getChildNodes();

        for (int i = 0; i < fileElements.getLength(); i++) {
            Node element = fileElements.item(i);
            String tagName = element.getNodeName();

            switch (tagName) {
                case "file":
                    appendFileElement(config, element);
                    break;
                case "#text":
                    // インデントなどは無視する
                    break;
                default:
                    throw new ConfigException("Unexpected tag found in <" + TAG_NAME + ">: " + tagName);
            }

        }
    }

    /**
     * Load values from &lt;file&gt; element and put them into {@link ServerConfig} object.
     *
     * @param config      the configuration object to edit
     * @param fileElement the xml element
     */
    private void appendFileElement(ServerConfig config, Node fileElement) {
        NamedNodeMap attributes = fileElement.getAttributes();
        config.putFile(namedNodeMapToAttributeMap(attributes));
    }

    /**
     * Convert an {@link NamedNodeMap} to an {@link AttributeHashMap}.
     *
     * @param attributeMap the instance to convert
     */
    private static AttributeHashMap namedNodeMapToAttributeMap(NamedNodeMap attributeMap) {
        AttributeHashMap map = new AttributeHashMap(attributeMap.getLength(), 1.0F);

        for (int i = 0; i < attributeMap.getLength(); i++) {
            Attr attribute = (Attr) attributeMap.item(i);

            map.put(attribute.getName(), attribute.getValue());
        }

        return map;
    }

    private static class LoadContext {
        public final List<Node> fileManagerNode = new ArrayList<>();
    }
}
