package jp.unaguna.mappedftp.config.spring;

import jp.unaguna.mappedftp.filesystem.tree.FileTreeItemFromLocalFile;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

import java.nio.file.Paths;

public class LocalFileBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return FileTreeItemFromLocalFile.class;
    }

    @Override
    protected void doParse(final Element element,
                           final BeanDefinitionBuilder builder) {
        builder.addConstructorArgValue(Paths.get(element.getAttribute("src")));

        if (element.hasAttribute("owner")) {
            builder.addPropertyValue("ownerName", element.getAttribute("owner"));
        }
        if (element.hasAttribute("group")) {
            builder.addPropertyValue("groupName", element.getAttribute("group"));
        }
    }
}
