import Vue from "vue";
import Vuex from "vuex";
import AUTH_API from "../services/auth.service";
import IMOOC_API from "../services/imooc.service";
import router from "../router";
import UTIL from "@/core/util";
import { usersModule } from "./modules/users.js";
import { registerModule } from "./modules/register.js";

Vue.use(Vuex);

// 初始化状态
let initialState = {
  // 开发模式下，有的时候为了不用每次登录，我们可以根据环境变量的设置去跳过登录步骤
  login:
    process.env.NODE_ENV === "development" &&
    process.env.VUE_APP_SKIP_LOGIN === "true",
  mfa: null,
  loginErrMsg: null,
  auth:
    process.env.NODE_ENV === "development" &&
    process.env.VUE_APP_SKIP_LOGIN === "true"
      ? {
          accessToken: process.env.VUE_APP_AUTH_ACCESS_TOKEN,
          refreshToken: process.env.VUE_APP_AUTH_REFRESH_TOKEN,
        }
      : null,
};

export default new Vuex.Store({
  state: initialState,
  mutations: {
    loginSuccess: (state, payload) => {
      state.login = true;
      state.loginErrMsg = null;
      state.mfa = null;
      state.auth = {
        accessToken: payload.accessToken,
        refreshToken: payload.refreshToken,
      };
    },
    sendMfaSuccess: (state) => {
      state.loginErrMsg = null;
    },
    sendMfaFail: (state, payload) => {
      state.loginErrMsg = payload;
    },
    setMfa: (state, payload) => {
      state.mfa = payload;
      state.loginErrMsg = null;
    },
    loginFail: (state, payload) => {
      state.login = false;
      state.loginErrMsg = payload;
      state.auth = null;
    },
    reset(state) {
      Object.keys(state)
        .filter((key) => !!state[key])
        .forEach((key) => {
          Object.assign(state[key], initialState[key]);
        });
    },
  },
  actions: {
    sendMfa: ({ commit, getters }, payload) => {
      AUTH_API.sendMfa(getters.mfaId, payload)
        .then(() => commit("sendMfaSuccess"))
        .catch((err) =>
          commit(
            "sendMfaFail",
            UTIL.getErrorDetailFromResponse(err.response.data) ||
              "发送验证码错误"
          )
        );
    },
    verifyMfa: ({ commit }, { mfaId, code }) => {
      AUTH_API.verifyMfa(mfaId, code)
        .then((res) => {
          if (res.data) {
            commit("loginSuccess", {
              accessToken: res.data.accessToken,
              refreshToken: res.data.refreshToken,
            });
            router.push("/");
            return;
          }
          commit("loginFail", "服务器返回结果异常");
          router.push("/login");
        })
        .catch((err) => {
          commit(
            "loginFail",
            UTIL.getErrorDetailFromResponse(err.response.data) ||
              "验证码不正确或过期"
          );
          router.push("/login");
        });
    },
    login: ({ commit }, { username, password, icode }) => {
      IMOOC_API.verifyCode(icode).then((res) => {
        if (res.data.code === 1000) {
          AUTH_API.login(username, password)
            .then((res) => {
              if (res.data) {
                commit("loginSuccess", {
                  accessToken: res.data.accessToken,
                  refreshToken: res.data.refreshToken,
                });
                router.push("/");
                return;
              }
              commit("loginFail", "服务器返回结果异常");
            })
            .catch((err) => {
              if (err.response) {
                const headers = err.response.headers;
                // 判断是否需要多因子认证
                const MFA_PREFIX = "realm=";
                for (const key in headers) {
                  if (key === "x-authenticate") {
                    const elements = headers[key].split(", ");
                    if (
                      elements.length === 2 &&
                      elements[0] === "mfa" &&
                      elements[1].startsWith(MFA_PREFIX)
                    ) {
                      commit("setMfa", elements[1].replace(MFA_PREFIX, ""));
                      router.push("/mfa");
                      return;
                    }
                  }
                }
              }

              commit(
                "loginFail",
                UTIL.getErrorDetailFromResponse(err.response.data) ||
                  "用户名或密码错误"
              );
            });
        } else {
          commit("loginFail", res.data.msg);
        }
      });
    },
    reset: ({ commit }) => {
      commit("reset");
      router.push("/login");
    },
  },
  getters: {
    isLoggedIn: (state) => state.login,
    loginError: (state) => state.loginErrMsg,
    mfaId: (state) => state.mfa,
    userPermissions: (state) => {
      const token = state.auth.accessToken;
      if (!token) {
        return [];
      }
      const payload = UTIL.parseJwt(token);
      const permissions = payload["authorities"];
      return permissions || [];
    },
  },
  modules: {
    usersModule,
    registerModule,
  },
});
