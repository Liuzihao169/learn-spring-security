import store from "@/store";

export default {
  // 从 Response 中提取详细错误信息
  getErrorDetailFromResponse: (err) => {
    if (err.response && err.response.data) {
      const data = err.response.data;
      if (data) {
        for (const key in data) {
          if (key === "detail") {
            return data[key];
          }
        }
      }
    }
    return null;
  },
  parseJwt: (token) => {
    try {
      // jwt 使用 . 分隔为三段
      // payload 位于第二段，所以仅解析数组中第二个即可
      return JSON.parse(atob(token.split(".")[1]));
    } catch (e) {
      return null;
    }
  },
  // 检查用户的权限是否含有传入的权限列表中的任何一个元素
  // 或者如果用户为超级管理则直接返回 true
  hasPermissionIn: (permissions) =>
    permissions.some((perm) => store.getters.userPermissions.includes(perm)) ||
    store.getters.userPermissions.includes("ROLE_ADMIN"),
};
