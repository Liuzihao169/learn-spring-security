import store from "../store";
import UTIL from "@/core/util";
import OAUTH_API from "@/services/oauth.service";

export default {
  globalGuard: (to, from, next) => {
    // 我们在路由的配置中使用一个元数据 meta.requiresAuth 来标识是否需要认证
    if (to.matched.some((record) => record.meta.requiresAuth)) {
      // 如果路由需要认证，则检查是否已经登录，如果没有，导航到登录页面
      if (!store.state.login) {
        // 判断浏览器地址栏是否有 code 和 state，如果有说明是授权服务器重定向回来的
        const codePattern = /code=([\w-.]+)/;
        const codeMatched = codePattern.exec(window.location.href);
        const statePattern = /state=([\w-.]+)/;
        const stateMatched = statePattern.exec(window.location.href);
        if (
          !store.getters.isLoggedIn &&
          codeMatched &&
          codeMatched.length > 1 &&
          statePattern &&
          stateMatched.length > 1
        ) {
          // 有 code 就触发登录 Action，使用 code 换取 token
          store
            .dispatch("login", {
              code: codeMatched[1],
              oauthState: stateMatched[1],
            })
            .then(() => {
              // 成功获取 token 后重定向到首页，否则 code 一直会存在
              window.location.href = "/";
              next();
            })
            .catch((err) => next(err));
        } else {
          // 没有 code 就导航到授权服务器开始授权码流程
          OAUTH_API.getUrl();
          next(new Error("尚未登录"));
        }
      } else if (
        // 这里的约定是
        // 首先检查路由定义的元数据中是否包含 requiredPermissions
        // 如果不包含则放行
        // 如果包含，则检查是否用户的权限名称可以匹配到该路由所需的权限名称
        // 是则放行
        // 否则导航到无权查看页面
        to.matched.some(
          (record) =>
            record.meta.requiredPermissions &&
            !UTIL.hasPermissionIn(record.meta.requiredPermissions)
        )
      ) {
        next({
          path: "/forbidden",
        });
      } else {
        next();
      }
    } else {
      next(); // 确保在其他情况下调用 next()!
    }
  },
};
