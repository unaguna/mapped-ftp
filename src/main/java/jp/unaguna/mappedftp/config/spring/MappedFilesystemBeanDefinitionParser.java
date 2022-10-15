package jp.unaguna.mappedftp.config.spring;

import jp.unaguna.mappedftp.filesystem.ReadOnlyFileSystemFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

public class MappedFilesystemBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return ReadOnlyFileSystemFactory.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder bean) {
        // TODO: 実装
    }
}
