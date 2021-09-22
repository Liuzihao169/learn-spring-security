import axios from "axios";
import store from "@/store";

// 使用特定实例，避免污染全局
const ADMIN_AXIOS = axios.create({
  baseURL: `${process.env.VUE_APP_API_URL}/admin`,
  timeout: 10000,
});

// 请求拦截器，给每个请求加上认证头
ADMIN_AXIOS.interceptors.request.use(
  (config) => {
    const token = store.state.auth.accessToken;
    if (token) {
      config.headers["Authorization"] = "Bearer " + token;
    }
    return config;
  },
  (error) => Promise.reject(error)
);
// 响应拦截器，在遇到认证错误的时候，使用 refreshToken 进行刷新
ADMIN_AXIOS.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // 存储失败的请求
    const originalRequest = error.config;
    // 由于目前 problem-spring-web 的 bug，导致有时会以 500 形式返回认证异常
    // 未来版本更新应该可以解决。
    if (
      (error.response.status === 401 ||
        (error.response.status === 500 &&
          (error.response.data.detail ===
            "Full authentication is required to access this resource" ||
            error.response.data.detail === "Access is denied"))) &&
      !originalRequest._retry
    ) {
      originalRequest._retry = true;
      const refreshToken = store.state.auth.refreshToken;
      const accessToken = store.state.auth.accessToken;
      // 刷新 token
      return axios
        .post(`${process.env.VUE_APP_API_URL}/authorize/token/refresh`, null, {
          headers: {
            Authorization: "Bearer " + accessToken,
          },
          params: {
            refreshToken: refreshToken,
          },
        })
        .then((res) => {
          // 在 store 中存储
          store.commit("loginSuccess", res.data);
          // 使用新的 token 发送原有的请求
          ADMIN_AXIOS.defaults.headers.common["Authorization"] =
            "Bearer " + store.state.auth.accessToken;
          return ADMIN_AXIOS(originalRequest);
        })
        .catch((error) => Promise.reject(error));
    }
    return Promise.reject(error);
  }
);

export default ADMIN_AXIOS;
