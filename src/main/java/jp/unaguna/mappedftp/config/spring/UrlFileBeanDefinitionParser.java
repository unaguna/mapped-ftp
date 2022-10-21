package jp.unaguna.mappedftp.config.spring;

import jp.unaguna.mappedftp.filesystem.tree.FileTreeItemFromURL;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

public class UrlFileBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return FileTreeItemFromURL.class;
    }

    @Override
    protected void doParse(final Element element,
                           final BeanDefinitionBuilder builder) {
        builder.addConstructorArgValue(element.getAttribute("src"));
    }
}
