// frontend/webpack.config.js
const { merge } = require('webpack-merge');
const TerserPlugin = require('terser-webpack-plugin');

module.exports = (config) => {
  return merge(config, {
    optimization: {
      minimizer: [
        new TerserPlugin({
          parallel: true,
          terserOptions: {
            compress: {
              drop_console: true, // Удаляет console.log в production
            },
            output: {
              comments: false, // Удаляет комментарии
            }
          }
        })
      ],
      splitChunks: {
        chunks: 'all',
        minSize: 10000,
        maxSize: 250000
      }
    }
  });
};
