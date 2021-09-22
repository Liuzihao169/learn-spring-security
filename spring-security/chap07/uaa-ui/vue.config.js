module.exports = {
  // 这里面改变这些位置，主要是为了和 maven 保持一致
  // 参考 https://cli.vuejs.org/config/
  outputDir: "target/dist",
  assetsDir: "static",
  css: {
    loaderOptions: {
      less: {
        lessOptions: {
          modifyVars: {
            "primary-color": "#1DA57A",
            "link-color": "#1DA57A",
            "border-radius-base": "2px",
          },
          javascriptEnabled: true,
        },
      },
    },
  },
};
