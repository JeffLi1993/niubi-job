/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zuoxiaolong.niubi.job.core.scanner;

import com.zuoxiaolong.niubi.job.core.ConfigException;
import com.zuoxiaolong.niubi.job.core.config.Context;
import com.zuoxiaolong.niubi.job.core.helper.LoggerHelper;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 默认的任务扫描器
 *
 * @author Xiaolong Zuo
 * @since 16/1/9 00:45
 */
public class LocalJobScanner extends AbstractJobScanner {

    public LocalJobScanner(Context context) {
        super(context);
    }

    @Override
    public List<MethodTriggerDescriptor> scan() {
        List<MethodTriggerDescriptor> descriptorList = new ArrayList<MethodTriggerDescriptor>();
        URL url = getContext().classLoader().getResource("");
        if (url == null) {
            LoggerHelper.error("classpath can't be find.");
            throw new ConfigException();
        }
        if (url.getProtocol().toLowerCase().equals("file")) {
            LoggerHelper.info("scan classpath [" + url + "]");
            File[] children = new File(url.getFile()).listFiles();
            if (children != null && children.length > 0) {
                for (File child : children) {
                    fill("", child, descriptorList);
                }
            }
            return descriptorList;
        } else {
            LoggerHelper.warn("url [" + url + "] is not a file but a " + url.getProtocol() + ".");
            return descriptorList;
        }
    }

    public void fill(String packageName, File file, List<MethodTriggerDescriptor> descriptorList) {
        String fileName = file.getName();
        if (file.isFile() && fileName.endsWith(".class")) {
            String className = packageName + "." + fileName.substring(0, fileName.lastIndexOf("."));
            super.scanClass(className, descriptorList);
        } else if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null && children.length > 0) {
                for (File child : children) {
                    fill(packageName + "." + fileName, child, descriptorList);
                }
            }
        }
    }

}
