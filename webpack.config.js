var path = require('path');

module.exports = {
    context: path.resolve(__dirname, 'src/main/webapp/js'),
    entry: {
        index: './index.js'
    },
    devtool: 'sourcemaps',
    cache: true,
    output: {
        path: __dirname,
        filename: './src/main/webapp/build/react/[name].bundle.js'
    },
    mode: 'none',
    module: {
        rules: [ {
            test: /\.(js|jsx)$/,
            exclude: /(node_modules)/,
            use: {
                loader: 'babel-loader',
                options: {
                    presets: [ '@babel/preset-env', '@babel/preset-react' ],
                    plugins: [
                        ["@babel/plugin-proposal-class-properties"],
                        ["@babel/plugin-transform-runtime",
                            {
                                "regenerator": true
                            }
                        ]
                    ]
                }
            }
        }, {
            test: /\.(sa|sc|c)ss$/,
            use: [ 'style-loader', 'css-loader', 'sass-loader' ]
        } ]
    }
};