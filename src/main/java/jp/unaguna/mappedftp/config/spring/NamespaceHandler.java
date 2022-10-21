/*
 * This file has been modified by K-izumi.
 * The license for the file before modification is as follows.
 */
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package jp.unaguna.mappedftp.config.spring;

import org.apache.ftpserver.config.spring.CommandFactoryBeanDefinitionParser;
import org.apache.ftpserver.config.spring.FileSystemBeanDefinitionParser;
import org.apache.ftpserver.config.spring.ListenerBeanDefinitionParser;
import org.apache.ftpserver.config.spring.UserManagerBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class NamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("server", new ServerBeanDefinitionParser());

        // same as org.apache.ftpserver.config.spring.FtpServerNamespaceHandler
        registerBeanDefinitionParser("nio-listener", new ListenerBeanDefinitionParser());
        registerBeanDefinitionParser("file-user-manager", new UserManagerBeanDefinitionParser());
        registerBeanDefinitionParser("db-user-manager", new UserManagerBeanDefinitionParser());
        registerBeanDefinitionParser("native-filesystem", new FileSystemBeanDefinitionParser());
        registerBeanDefinitionParser("commands", new CommandFactoryBeanDefinitionParser());

        registerBeanDefinitionParser("mapped-filesystem", new MappedFilesystemBeanDefinitionParser());
        registerBeanDefinitionParser("local-file", new LocalFileBeanDefinitionParser());
        registerBeanDefinitionParser("url-file", new UrlFileBeanDefinitionParser());
        registerBeanDefinitionParser("classpath-file", new ClasspathFileBeanDefinitionParser());
    }
}
