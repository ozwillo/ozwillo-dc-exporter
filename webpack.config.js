const path = require('path');
const webpack = require('webpack');
const merge = require('webpack-merge');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CleanPlugin = require('clean-webpack-plugin');
const autoprefixer = require('autoprefixer');
const UglifyJsPlugin = require("uglifyjs-webpack-plugin");
const OptimizeCSSAssetsPlugin = require("optimize-css-assets-webpack-plugin");

const TARGET = process.env.npm_lifecycle_event;
const PATHS = {
    app: path.join(__dirname, 'src/main/resources/public'),
    style: path.join(__dirname, 'src/main/resources/public/styles', 'index.scss'),
    build: path.join(__dirname, 'src/main/resources/public/build')
};

const commonEntryPointsLoadersAndServers = ['bootstrap-loader'];
const devEntryPointsLoadersAndServers = ['webpack-dev-server/client?http://localhost:3000', 'webpack/hot/only-dev-server'];

const common = {
    entry: path.join(PATHS.app, 'jsx/App.jsx'),
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
            'fetch': 'imports-loader?this=>global!exports-loader?global.fetch!whatwg-fetch'
        }),
        new webpack.LoaderOptionsPlugin({
            // test: /\.xxx$/, // may apply this only for some modules
            options: {
              postcss: [ autoprefixer ]
            }
        })
    ],
    resolve: {
        extensions: [ '.js', '.jsx' ]
    },
    module: {
    	rules: [
            { test: /\.png$/, loader: 'url-loader?limit=10000' },
            /* TODO : loaders for TWBS glyphicons ? */
            { test: /\.woff(2)?(\?v=[0-9]\.[0-9]\.[0-9])?$/, loader: 'url-loader?limit=10000&mimetype=application/font-woff' },
            { test: /\.(ttf|eot|svg)(\?v=[0-9]\.[0-9]\.[0-9])?$/, loader: 'file-loader' },
            /* loader for JSX / ES6 */
            { test: /\.jsx?$/, loaders: ['babel-loader?cacheDirectory,presets[]=react,presets[]=es2015,presets[]=stage-0'], include: path.join(PATHS.app, 'jsx')}
        ]
    }
};

// Default configuration
if(TARGET === 'start' || !TARGET) {
    module.exports = merge(common, {
    	mode: "development",
        devtool: 'eval',
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
        plugins: [
            new webpack.HotModuleReplacementPlugin(),
            new MiniCssExtractPlugin({
            	filename: "styles.min.css"
            })
        ],
        module: {
            rules: [
            	{
                	test: /\.(css|scss)$/, 
                	include: path.join(PATHS.app, 'styles'),
                	use: [
                		MiniCssExtractPlugin.loader,
                		"css-loader",
                		{
                			loader: "postcss-loader",
                			options: {
                				plugins: [
                					autoprefixer
	                			]
                			}
                		},
                		"sass-loader"
                	]	
                }
            ]
        },
        optimization: {
            minimize: false
        }
    });
}
if(TARGET === 'build' || TARGET === 'stats') {
    module.exports = merge(common, {
    	mode: "production",
        module: {
        	rules: [
        		{
                	test: /\.(css|scss)$/, 
                	use: [
                		MiniCssExtractPlugin.loader,
                		"css-loader",
                		{
                			loader: "postcss-loader",
                			options: {
                				plugins: [
                					autoprefixer
	                			]
                			}
                		},
                		"sass-loader"
                	]	
                }
            ]
        },
        optimization: {
            minimize: true,
            minimizer: [
            	new UglifyJsPlugin({
                    cache: true,
                    parallel: true,
                    sourceMap: true // set to true if you want JS source maps
                }),
                new OptimizeCSSAssetsPlugin({})
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
            new MiniCssExtractPlugin({
            	filename: "styles.min.css"
            })
        ]
    });
}
