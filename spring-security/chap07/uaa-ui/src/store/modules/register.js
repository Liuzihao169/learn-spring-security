import AUTH_API from "@/services/auth.service";
import router from "@/router";
import UTIL from "@/core/util";

export const registerModule = {
  namespaced: true,
  state: () => ({
    error: null,
  }),
  mutations: {
    registerSuccess: (state) => {
      state.error = null;
    },
    registerFail: (state, payload) => {
      state.error = payload;
    },
  },
  actions: {
    register: ({ commit }, payload) => {
      AUTH_API.register(payload)
        .then(() => {
          commit("registerSuccess");
          router.push("/login");
        })
        .catch((err) => {
          commit(
            "registerFail",
            UTIL.getErrorDetailFromResponse(err) || "获取用户列表失败"
          );
        });
    },
  },
};
