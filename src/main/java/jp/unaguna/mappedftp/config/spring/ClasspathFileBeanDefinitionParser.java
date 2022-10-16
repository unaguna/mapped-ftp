package jp.unaguna.mappedftp.config.spring;

import jp.unaguna.mappedftp.filesystem.tree.FileTreeItemFromURL;
import jp.unaguna.mappedftp.utils.ClasspathUtils;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

import java.net.URL;
import java.util.Objects;

public class ClasspathFileBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return FileTreeItemFromURL.class;
    }

    @Override
    protected void doParse(final Element element,
                           final BeanDefinitionBuilder builder) {
        final String src = element.getAttribute("src");
        final URL url = ClasspathUtils.getResource(src);
        if (url == null) {
            throw new InvalidPropertyException(Objects.requireNonNull(this.getBeanClass(element)), "src", "no such resource: " + src);
        }

        builder.addConstructorArgValue(url);
    }
}
