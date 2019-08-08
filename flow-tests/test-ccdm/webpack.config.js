/**
 * This file has been autogenerated as it didn't exist or was made for an older incompatible version.
 * This file can be used for manual configuration will not be modified if the flowDefaults constant exists.
 */
const merge = require('webpack-merge');
const flowDefaults = require('./webpack.generated.js');

/**
 * These additional configurations should be added automatically to webpack.generated.js
 * based on the flag 'clientSideMode'.
 * TODO: This file should be removed after https://github.com/vaadin/flow/issues/6136
 */
const CopyWebpackPlugin = require('copy-webpack-plugin');
const mavenOutputFolderForFlowBundledFiles = require('path').resolve(__dirname, 'target/classes/META-INF/VAADIN');
const frontendFolder = `${require('path').resolve(__dirname)}/frontend`;

module.exports = merge(flowDefaults, {
    entry: {
      bundle: require('path').resolve(__dirname, 'frontend/index')
    },
    plugins: [
      new CopyWebpackPlugin([{
        from: `${frontendFolder}/index.html`,
        to: `${mavenOutputFolderForFlowBundledFiles}/index.html`
      }]),
    ]
});

/**
 * This file can be used to configure the flow plugin defaults.
 * <code>
 *   // Add a custom plugin
 *   flowDefaults.plugins.push(new MyPlugin());
 *
 *   // Update the rules to also transpile `.mjs` files
 *   if (!flowDefaults.module.rules[0].test) {
 *     throw "Unexpected structure in generated webpack config";
 *   }
 *   flowDefaults.module.rules[0].test = /\.m?js$/
 *
 *   // Include a custom JS in the entry point in addition to generated-flow-imports.js
 *   if (typeof flowDefaults.entry.index != "string") {
 *     throw "Unexpected structure in generated webpack config";
 *   }
 *   flowDefaults.entry.index = [flowDefaults.entry.index, "myCustomFile.js"];
 * </code>
 * or add new configuration in the merge block.
 * <code>
 *   module.exports = merge(flowDefaults, {
 *     mode: 'development',
 *     devtool: 'inline-source-map'
 *   });
 * </code>
 */