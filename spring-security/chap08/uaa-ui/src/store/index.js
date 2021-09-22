import Vue from "vue";
import Vuex from "vuex";
import OAUTH_API from "../services/oauth.service";
import router from "../router";
import UTIL from "@/core/util";
import { usersModule } from "./modules/users";
import { rolesModule } from "./modules/roles";
import { clientsModule } from "./modules/clients";

Vue.use(Vuex);
const secret = "secret";
const getAuthFromStorage = () => {
  return sessionStorage.getItem("auth")
    ? JSON.parse(UTIL.decryptStr(sessionStorage.getItem("auth"), secret))
    : null;
};

const auth = getAuthFromStorage();
const getDefaultState = () => {
  return {
    login: !!sessionStorage.getItem("auth"),
    secret: secret,
    mfa: null,
    loginErrMsg: null,
    auth: {
      accessToken: auth ? auth["access_token"] : null,
      refreshToken: auth ? auth["refresh_token"] : null,
    },
  };
};

// 初始化状态
let initialState = getDefaultState();

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
    loginFail: (state, payload) => {
      state.login = false;
      state.loginErrMsg = payload;
      state.auth = null;
    },
    reset(state) {
      Object.assign(state, getDefaultState());
    },
  },
  actions: {
    login: ({ commit, state }, { code, oauthState }) => {
      return OAUTH_API.getToken(code, oauthState)
        .then((res) => {
          if (res.data) {
            commit("loginSuccess", {
              accessToken: res.data.access_token,
              refreshToken: res.data.refresh_token,
            });
            const jsonData = JSON.stringify(res.data);
            const encrypted = UTIL.encryptStr(jsonData, state.secret);
            sessionStorage.setItem("auth", encrypted);
            router.push("/");
            return;
          }
          commit("loginFail", "服务器返回结果异常");
          return Promise.resolve();
        })
        .catch((err) => {
          commit(
            "loginFail",
            UTIL.getErrorDetailFromResponse(err) || "用户名或密码错误"
          );
          return Promise.reject(err);
        });
    },
    reset: ({ commit }) => {
      commit("reset");
      sessionStorage.clear();
      document.cookie.split(";").forEach(function(c) {
        document.cookie = c
          .replace(/^ +/, "")
          .replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/");
      });
      router.push("/logout");
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
    username: (state) => {
      const token = state.auth.accessToken;
      if (!token) {
        return "";
      }
      const payload = UTIL.parseJwt(token);
      const username = payload["user_name"];
      return username || "";
    },
  },
  modules: {
    usersModule,
    rolesModule,
    clientsModule,
  },
});
