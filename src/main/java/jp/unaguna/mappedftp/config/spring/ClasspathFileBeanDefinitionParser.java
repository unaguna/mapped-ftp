package jp.unaguna.mappedftp.config.spring;

import jp.unaguna.mappedftp.filesystem.tree.FileTreeItemFromClasspath;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

public class ClasspathFileBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return FileTreeItemFromClasspath.class;
    }

    @Override
    protected void doParse(final Element element,
                           final BeanDefinitionBuilder builder) {
        builder.addConstructorArgValue(element.getAttribute("src"));
    }
}
