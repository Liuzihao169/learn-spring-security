import store from "../store";
import UTIL from "@/core/util";

export default {
  guardMfa: (to, from, next) => {
    // 没有 MfaId 则不能进入该路由，重定向到 /login
    if (!store.getters.mfaId) {
      next({
        path: "/login",
      });
    } else {
      next();
    }
  },
  globalGuard: (to, from, next) => {
    // 我们在路由的配置中使用一个元数据 meta.requiresAuth 来标识是否需要认证
    if (to.matched.some((record) => record.meta.requiresAuth)) {
      // 如果路由需要认证，则检查是否已经登录，如果没有，导航到登录页面
      if (!store.state.login) {
        next({
          path: "/login",
        });
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
