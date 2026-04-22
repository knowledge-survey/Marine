module.exports = {
  devServer: {
    port: 8080,
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        onProxyRes: function(proxyRes) {
          proxyRes.headers['Access-Control-Allow-Origin'] = '*';
        }
      }
    },
    historyApiFallback: true,
    client: {
      overlay: {
        runtimeErrors: (error) => {
          if (error.message && error.message.includes('ResizeObserver loop')) {
            return false;
          }
          return true;
        }
      }
    }
  },
  publicPath: '/'
}
