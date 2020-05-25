const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

module.exports = (env, options) => {
    //context: path.resolve(__dirname, 'src/main/webapp/js'),
    const config = {
        entry: ['@babel/polyfill', './src/main/webapp/js/index.js'],
        devtool: 'source-map',
        cache: true,
        output: {
            //path: path.resolve('./src/main/webapp/dist'),
            path: path.resolve('./src/main/resources/static'),
            filename: 'index.bundle.[hash].js'
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
            new webpack.BannerPlugin({
                banner: () => `Build Date: ${new Date().toLocaleString()}\n`
            }),
            new HtmlWebpackPlugin({
                template: './src/main/webapp/index.html', // ���ø� ��θ� ����
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
        ]
    }

    return config;
}
