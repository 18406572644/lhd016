module.exports = {
  transpileDependencies: true,
  lintOnSave: false,
  devServer: {
    port: 2016,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:6016',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/api'
        }
      }
    }
  },
  css: {
    loaderOptions: {
      sass: {
        additionalData: `@import "@/styles/variables.scss";`
      }
    }
  }
}
