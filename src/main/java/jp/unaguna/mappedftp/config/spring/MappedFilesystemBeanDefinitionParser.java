package jp.unaguna.mappedftp.config.spring;

import jp.unaguna.mappedftp.filesystem.MappingFileSystemFactory;
import jp.unaguna.mappedftp.filesystem.tree.FileTreeItem;
import jp.unaguna.mappedftp.utils.ClasspathUtils;
import org.apache.ftpserver.config.spring.SpringUtil;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Objects;

public class MappedFilesystemBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return MappingFileSystemFactory.class;
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

            // assert that item is instance of FileTreeItem
            try {
                Objects.requireNonNull(item);
                ClasspathUtils.getClass(item.getBeanClassName(), FileTreeItem.class);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (ClassCastException | NullPointerException e) {
                throw new BeanCreationException(e.getMessage(), e);
            }

            files.put(path, item);
        }
        builder.addConstructorArgValue(files);

    }

}
