const path = require('path');
const webpack = require('webpack');
const merge = require('webpack-merge');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const CleanPlugin = require('clean-webpack-plugin');
const autoprefixer = require('autoprefixer');

const TARGET = process.env.npm_lifecycle_event;
const PATHS = {
    app: path.join(__dirname, 'src/main/resources/public'),
    build: path.join(__dirname, 'src/main/resources/public/build')
};

const commonEntryPointsLoadersAndServers = ['bootstrap-loader'];
const devEntryPointsLoadersAndServers = ['webpack-dev-server/client?http://localhost:3000', 'webpack/hot/only-dev-server'];

const common = {
    entry: [path.join(PATHS.app, 'jsx/App.jsx')].concat(commonEntryPointsLoadersAndServers),
    output: {
        path: PATHS.build,
        filename: 'bundle.js',
        publicPath: '/build/'
    },
    plugins: [
        new webpack.ProvidePlugin({
            $: 'jquery',
            jQuery: 'jquery',
            'Promise': 'es6-promise', // Thanks Aaron (https://gist.github.com/Couto/b29676dd1ab8714a818f#gistcomment-1584602)
            'fetch': 'imports?this=>global!exports?global.fetch!whatwg-fetch'
        }),
        new ExtractTextPlugin('styles.css', { allChunks: true })
    ],
    resolve: {
        extensions: [ '', '.js', '.jsx' ],
        alias: {
            'Autosuggest.scss': path.resolve(__dirname, 'node_modules/react-bootstrap-autosuggest/src/Autosuggest.scss')
        }
    },
    module: {
        loaders: [
            { test: /\.png$/, loader: 'url-loader?limit=10000' },
            /* TODO : loaders for TWBS glyphicons ? */
            { test: /\.woff(2)?(\?v=[0-9]\.[0-9]\.[0-9])?$/, loader: 'url-loader?limit=10000&mimetype=application/font-woff' },
            { test: /\.(ttf|eot|svg)(\?v=[0-9]\.[0-9]\.[0-9])?$/, loader: 'file-loader' },
            /* to ensure jQuery is loaded before Bootstrap */
            { test: /bootstrap-sass\/assets\/javascripts\//, loader: 'imports?jQuery=jquery' },
            /* loader for JSX / ES6 */
            { test: /\.jsx?$/, loaders: ['react-hot', 'babel?cacheDirectory,presets[]=react,presets[]=es2015'], include: path.join(PATHS.app, 'jsx')}
        ]
    },
    postcss: [ autoprefixer ],
    debug: true
};

// Default configuration
if(TARGET === 'start' || !TARGET) {
    module.exports = merge(common, {
        devtool: 'eval-source-map',
        devServer: {
            publicPath: common.output.publicPath,
            contentBase: '/build',
            hot: true,
            inline: true,
            progress: true,
            stats: { colors: true },
            port: 3000,
            proxy : {
                '*': 'http://localhost:8080'
            }
        },
        entry: common.entry.concat(devEntryPointsLoadersAndServers),
        plugins: [
            new webpack.HotModuleReplacementPlugin()
        ],
        module: {
            loaders: [
                {test: /\.css$/, loaders: ['style', 'css', 'postcss']},
                /* loaders for Bootstrap */
                {test: /\.scss$/, loaders: ['style', 'css', 'postcss', 'sass']},
                {
                    test: /\.scss$/,
                    loader: ExtractTextPlugin.extract('style', 'css!sass?includePaths[]=./node_modules/bootstrap-sass/assets/stylesheets')
                }
            ]
        },
        resolve: {
            alias: {
                'Autosuggest.scss': path.resolve(__dirname, 'node_modules/react-bootstrap-autosuggest/src/Autosuggest.scss')
            }
        }
    });
}
if(TARGET === 'build' || TARGET === 'stats') {
    module.exports = merge(common, {
        module: {
            loaders: [
                {test: /\.css$/, loader: ExtractTextPlugin.extract('style', 'css!postcss')},
                /* loaders for Bootstrap */
                {test: /\.scss$/, loader: ExtractTextPlugin.extract('style', 'css!postcss!sass')}
            ]
        },
        plugins: [
            new CleanPlugin([PATHS.build]),
            // Setting DefinePlugin affects React library size!
            // DefinePlugin replaces content "as is" so we need some extra quotes
            // for the generated code to make sense
            new webpack.DefinePlugin({
                'process.env.NODE_ENV': '"production"'
            }),
            new webpack.optimize.UglifyJsPlugin({
                compress: {
                    warnings: false
                }
            }),
            new ExtractTextPlugin('styles.min.css', {
                allChunks: true
            })
        ]
    });
}
