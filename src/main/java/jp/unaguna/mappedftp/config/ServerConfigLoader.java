package jp.unaguna.mappedftp.config;

import com.sun.org.apache.xerces.internal.dom.AttributeMap;
import jp.unaguna.mappedftp.map.AttributeHashMap;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;

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
     * @throws IOException when IO error is occurred within reading config file
     * @throws SAXException when the configuration file is not legal XML file
     */
    public ServerConfig load(Path configPath) throws ConfigException, IOException, SAXException {
        DocumentBuilderFactory documentbuilderfactory = DocumentBuilderFactory.newInstance();
        documentbuilderfactory.setIgnoringComments(true);
        final DocumentBuilder documentbuilder;
        try {
            documentbuilder = documentbuilderfactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        Document document = documentbuilder.parse(configPath.toFile());

        return load(document, configPath);
    }

    /**
     * Load a XML configuration.
     *
     * @param xmlDocument the configuration expressed by XML
     * @param configPath path to register as the source of the configuration file
     * @return a configuration object
     * @throws ConfigException when the configuration specifies illegal configuration
     */
    public ServerConfig load(Document xmlDocument, Path configPath) throws ConfigException {
        ServerConfig config = new ServerConfig();
        Element root = xmlDocument.getDocumentElement();

        config.setConfigFilepath(configPath);

        // ルート要素が期待と違えば例外
        if (!ROOT_TAG_NAME.equals(root.getTagName())) {
            throw new ConfigException("The root element of the configuration file must be " + ROOT_TAG_NAME);
        }

        NodeList elements = root.getChildNodes();
        for(int i=0; i<elements.getLength(); i++){
            Node element = elements.item(i);
            String tagName = element.getNodeName();

            switch (tagName) {
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

        return config;
    }

    /**
     * Load values from &lt;files&gt; element and put them into {@link ServerConfig} object.
     *
     * @param config the configuration object to edit
     * @param filesElement the xml element
     * @throws ConfigException when the xml element specifies illegal configuration
     */
    private void appendFilesElement(ServerConfig config, Node filesElement) throws ConfigException {
        final String TAG_NAME = "files";
        NodeList fileElements = filesElement.getChildNodes();

        for(int i=0; i<fileElements.getLength(); i++){
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
     * @param config the configuration object to edit
     * @param fileElement the xml element
     */
    private void appendFileElement(ServerConfig config, Node fileElement) {
        AttributeMap attributes = (AttributeMap) fileElement.getAttributes();
        config.putFile(attributeMapToMap(attributes));
    }

    /**
     * Convert an {@link AttributeMap} to an {@link AttributeHashMap}.
     *
     * @param attributeMap the instance to convert
     */
    private static AttributeHashMap attributeMapToMap(AttributeMap attributeMap) {
        AttributeHashMap map = new AttributeHashMap(attributeMap.getLength(), 1.0F);

        for(int i=0; i<attributeMap.getLength(); i++) {
            Attr attribute = (Attr) attributeMap.item(i);

            map.put(attribute.getName(), attribute.getValue());
        }

        return map;
    }
}
