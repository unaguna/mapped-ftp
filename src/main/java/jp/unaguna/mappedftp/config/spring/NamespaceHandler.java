package jp.unaguna.mappedftp.config.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class NamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("server", new ServerBeanDefinitionParser());
        registerBeanDefinitionParser("mapped-filesystem", new MappedFilesystemBeanDefinitionParser());
        registerBeanDefinitionParser("local-file", new LocalFileBeanDefinitionParser());
        registerBeanDefinitionParser("url-file", new UrlFileBeanDefinitionParser());
        registerBeanDefinitionParser("classpath-file", new ClasspathFileBeanDefinitionParser());
    }
}
