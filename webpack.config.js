const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const banner = require('./banner');
const Dotenv = require('dotenv-webpack');

module.exports = (env, options) => {
    //context: path.resolve(__dirname, 'src/main/webapp/js'),
    const config = {
        entry: ['@babel/polyfill', './src/main/webapp/js/index.js'],
        devtool: options.mode === 'production' ? 'hidden-source-map' : 'inline-source-map',
        // devtool: 'source-map',
        cache: true,
        output: {
            path: path.resolve('./src/main/resources/static/rss'),
            filename: 'index.bundle.[hash].js',
        },
        mode: options.mode === 'production' ? 'production' : 'development',
        module: {
            rules: [{
                test: /\.(js|jsx)$/,
                exclude: /(node_modules)/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['@babel/preset-env', '@babel/preset-react'],
                        plugins: [
                            ["emotion"],
                            ["@babel/plugin-proposal-class-properties"],
                            ["@babel/plugin-transform-runtime",
                                {
                                    "regenerator": true
                                }
                            ],
                            ["import", {"libraryName": "antd", "libraryDirectory": "es"}, "antd"]
                        ]
                    }
                }
            }, {
                test: /\.(sa|sc|c)ss$/,
                use: [
                    options.mode === 'production'
                    ? MiniCssExtractPlugin.loader
                    : 'style-loader',
                    'css-loader',
                    'sass-loader'
                ]
            }]
        },
        plugins: [
            new webpack.BannerPlugin(banner),
            new HtmlWebpackPlugin({
                template: './src/main/webapp/index.html', // ���ø� ��θ� ����
                filename: '../index.html'
                /*
                minify: process.env.NODE_ENV === 'production' ? {
                    collapseWhitespace: true, // ��ĭ ����
                    removeComments: true, // �ּ� ����
                } : false,
                */
            }),
            new CleanWebpackPlugin(),
            ...(
                options.mode === 'production'
                    ? [ new MiniCssExtractPlugin({filename: `[name].[hash].css?`}) ]
                    : []
            ),
            new Dotenv(),
        ]
    }

    return config;
}
