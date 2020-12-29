/*
 * Copyright 2000-2020 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.server.frontend;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.flow.server.frontend.scanner.FrontendDependenciesScanner;
import com.vaadin.flow.theme.ThemeDefinition;

import static com.vaadin.flow.server.frontend.FrontendUtils.GENERATED;
import static com.vaadin.flow.server.frontend.FrontendUtils.INDEX_JS;
import static com.vaadin.flow.server.frontend.FrontendUtils.INDEX_TS;
import static com.vaadin.flow.server.frontend.FrontendUtils.TARGET;

/**
 * A task for generate a TS file which is always executed in a Vaadin app
 *
 * @author Vaadin Ltd
 */
public class TaskGenerateBootstrap extends AbstractTaskClientGenerator {

    private final FrontendDependenciesScanner frontDeps;
    private final File connectClientTsApiFolder;
    private final File frontendDirectory;

    private static final String CUSTOM_BOOTSTRAP_FILE_NAME = "vaadin.ts";

    TaskGenerateBootstrap(FrontendDependenciesScanner frontDeps,
            File frontendDirectory) {
        this.frontDeps = frontDeps;
        this.frontendDirectory = frontendDirectory;
        this.connectClientTsApiFolder = new File(Paths
                .get(frontendDirectory.getPath(), GENERATED).toString());
    }

    @Override
    protected String getFileContent() {
        List<String> lines = new ArrayList<>();
        lines.add(String.format("import '%s';\n", getIndexTsEntryPath()));
        lines.addAll(getThemeLines());

        return String.join("\n", lines);
    }

    @Override
    protected File getGeneratedFile() {
        return new File(connectClientTsApiFolder, CUSTOM_BOOTSTRAP_FILE_NAME);
    }

    @Override
    protected boolean shouldGenerate() {
        return frontDeps != null && connectClientTsApiFolder != null;
    }

    private String getIndexTsEntryPath() {
        boolean exists = new File(frontendDirectory, INDEX_TS).exists()
                || new File(frontendDirectory, INDEX_JS).exists();
        Path path = exists ? Paths.get(frontendDirectory.getPath(), INDEX_TS)
                : Paths.get(frontendDirectory.getParentFile().getPath(), TARGET,
                        INDEX_TS);

        String relativePath = FrontendUtils
                .getUnixRelativePath(connectClientTsApiFolder.toPath(), path);
        return relativePath.replaceFirst("\\.[tj]s$", "");
    }

    private Collection<String> getThemeLines() {
        Collection<String> lines = new ArrayList<>();
        if (shouldApplyAppTheme()) {
            lines.add("//@ts-ignore");
            lines.add(
                    "import {applyTheme} from '../../target/flow-frontend/themes/theme-generated.js';");
            lines.add("applyTheme(document);");
            lines.add("");
        }
        return lines;
    }

    private boolean shouldApplyAppTheme() {
        ThemeDefinition themeDef = frontDeps.getThemeDefinition();
        return themeDef != null && !"".equals(themeDef.getName());
    }
}
