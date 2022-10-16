package jp.unaguna.mappedftp.config.spring;

import jp.unaguna.mappedftp.filesystem.ReadOnlyFileSystemFactory;
import org.apache.ftpserver.config.spring.SpringUtil;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.util.List;

public class MappedFilesystemBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return ReadOnlyFileSystemFactory.class;
    }

    @Override
    protected void doParse(final Element element,
                           final ParserContext parserContext,
                           final BeanDefinitionBuilder builder) {
        final ManagedMap<String, Object> files = new ManagedMap<>();
        final List<Element> children = SpringUtil.getChildElements(element);

        for (Element childElm : children) {
            final String path = childElm.getAttribute("path");
            final BeanDefinition item = parserContext.getDelegate()
                    .parseCustomElement(childElm, builder.getBeanDefinition());

            // TODO: item の class のバリデーション

            files.put(path, item);
        }
        builder.addConstructorArgValue(files);

    }

}
