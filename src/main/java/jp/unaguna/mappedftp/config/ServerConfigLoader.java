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

public class ServerConfigLoader {
    private static final String ROOT_TAG_NAME = "MappedFtp";

    public ServerConfig load(Path configPath) throws ConfigException, ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory documentbuilderfactory = DocumentBuilderFactory.newInstance();
        documentbuilderfactory.setIgnoringComments(true);
        DocumentBuilder documentbuilder = documentbuilderfactory.newDocumentBuilder();

        Document document = documentbuilder.parse(configPath.toFile());

        return load(document);
    }

    public ServerConfig load(Document xmlDocument) throws ConfigException {
        ServerConfig config = new ServerConfig();
        Element root = xmlDocument.getDocumentElement();

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
     * file タグを解析して設定オブジェクトに追加する。
     *
     * @param config 更新する設定オブジェクト
     * @param fileElement 解析してその内容を設定オブジェクトへ追加するノード
     */
    private void appendFileElement(ServerConfig config, Node fileElement) {
        AttributeMap attributes = (AttributeMap) fileElement.getAttributes();
        config.putFile(attributeMapToMap(attributes));
    }

    private static AttributeHashMap attributeMapToMap(AttributeMap attributeMap) {
        AttributeHashMap map = new AttributeHashMap(attributeMap.getLength(), 1.0F);

        for(int i=0; i<attributeMap.getLength(); i++) {
            Attr attribute = (Attr) attributeMap.item(i);

            map.put(attribute.getName(), attribute.getValue());
        }

        return map;
    }
}
