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

const fs = require('fs');
const { compileFunction } = require("vm");
const glob = require('glob');
const path = require('path');
const generateThemeFile = require('./theme-generator');

let logger;

class ApplicationThemePlugin {
    constructor(options) {
        this.options = options;
    }

    apply(compiler) {
        logger = compiler.getInfrastructureLogger("application-theme-plugin");

        compiler.hooks.run.tap("FlowApplicationThemePlugin", (compiler) => {
            if (fs.existsSync(this.options.themeJarFolder)) {
                handleThemes(this.options.themeJarFolder, this.options.projectStaticAssetsOutputFolder);
            } else {
                logger.warn('Theme JAR folder not found from ', this.options.themeJarFolder);
            }

            this.options.themeProjectFolders.forEach((themeProjectFolder) => {
                if (fs.existsSync(themeProjectFolder)) {
                    handleThemes(themeProjectFolder, this.options.projectStaticAssetsOutputFolder);
                }
            });
        });
    }
}

module.exports = ApplicationThemePlugin;

function getThemeProperties(themeFolder) {
    const themePropertyFile = path.resolve(themeFolder, 'theme.json');
    if (!fs.existsSync(themePropertyFile)) {
        return {};
    }
    return JSON.parse(fs.readFileSync(themePropertyFile));
};

function handleThemes(themesFolder, projectStaticAssetsOutputFolder) {
    logger.info("handling theme from ", themesFolder);
    const dir = fs.opendirSync(themesFolder);
    while ((dirent = dir.readSync())) {
        if (!dirent.isDirectory()) {
            continue;
        }
        const themeName = dirent.name;
        const themeFolder = path.resolve(themesFolder, themeName);
        const themeProperties = getThemeProperties(themeFolder);
        logger.info("Found theme ", themeName, " in folder ", themeFolder);

        copyStaticAssets(themeProperties, projectStaticAssetsOutputFolder);
        const themeFile = generateThemeFile(
          themeFolder,
          themeName,
          themeProperties
        );
        fs.writeFileSync(path.resolve(themeFolder, themeName + '.js'), themeFile);
    }
};

function copyStaticAssets(themeProperties, projectStaticAssetsOutputFolder) {

    const assets = themeProperties.assets;
    if (!assets) {
        logger.info("no assets to handle no static assets were copied");
        return;
    }

    fs.mkdirSync(projectStaticAssetsOutputFolder, {
        recursive: true
    });
    Object.keys(assets).forEach((module) => {
        const rules = assets[module];
        Object.keys(rules).forEach((srcSpec) => {
            const files = glob.sync('node_modules/' + module + '/' + srcSpec, {
                nodir: true,
            });
            const targetFolder = path.resolve(
              projectStaticAssetsOutputFolder,
              rules[srcSpec]
            );
            fs.mkdirSync(targetFolder, {
                recursive: true
            });
            files.forEach((file) => {
                logger.trace("Copying: ", file, '=>', targetFolder);
                fs.copyFileSync(file, path.resolve(targetFolder, path.basename(file)));
            });
        });
    });
};
