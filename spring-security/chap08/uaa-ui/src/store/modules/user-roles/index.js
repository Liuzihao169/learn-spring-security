import ADMIN_API from "@/services/admin.service";
import UTIL from "@/core/util";
import router from "@/router";
import { union, difference } from "lodash";

export const userRolesModule = {
  namespaced: true,
  state: () => ({
    assignedRoles: [],
    loading: false,
    error: null,
    availableRoles: [],
  }),
  mutations: {
    loadAvailableSuccess: (state, payload) => {
      state.availableRoles = payload;
      state.loading = false;
      state.error = null;
    },
    loadAvailableFail: (state, payload) => {
      state.error = payload;
      state.loading = false;
      state.availableRoles = [];
    },
    loadSuccess: (state, payload) => {
      state.assignedRoles = payload.roles;
      state.error = null;
      state.loading = false;
    },
    loadFail: (state, payload) => {
      state.error = payload;
      state.assignedRoles = [];
      state.loading = false;
    },
    startLoad: (state) => {
      state.loading = true;
    },
    moveToAssignedSuccess: (state, payload) => {
      const selectedRoles = state.availableRoles.filter((role) =>
        payload.includes(role.id)
      );
      state.assignedRoles = union(state.assignedRoles, selectedRoles);
      state.availableRoles = difference(state.availableRoles, selectedRoles);
    },
    moveToAvailableASuccess: (state, payload) => {
      const selectedRoles = state.assignedRoles.filter((role) =>
        payload.includes(role.id)
      );
      state.availableRoles = union(state.availableRoles, selectedRoles);
      state.assignedRoles = difference(state.assignedRoles, selectedRoles);
    },
    saveFail: (state, payload) => {
      state.error = payload;
      state.loading = false;
    },
  },
  actions: {
    save: ({ commit, state }, payload) => {
      commit("startLoad");
      ADMIN_API.saveUserRoles(
        payload,
        state.assignedRoles.map((role) => role.id)
      )
        .then((res) => {
          if (res && res.data) {
            commit("usersModule/updateSuccess", res.data, { root: true });
            router.push("/users");
          }
        })
        .catch((err) => {
          commit(
            "saveFail",
            UTIL.getErrorDetailFromResponse(err) || "保存用户角色列表失败"
          );
        });
    },
    moveToAssigned: ({ commit }, payload) => {
      commit("moveToAssignedSuccess", payload);
    },
    moveToAvailable: ({ commit }, payload) => {
      commit("moveToAvailableASuccess", payload);
    },
    load: ({ commit }, payload) => {
      commit("startLoad");
      ADMIN_API.loadByUsername(payload)
        .then((res) => {
          if (res && res.data) {
            commit("loadSuccess", res.data);
            ADMIN_API.loadAvailableRoles(payload)
              .then((res) => {
                if (res && res.data) {
                  commit("loadAvailableSuccess", res.data);
                }
              })
              .catch((err) => {
                commit(
                  "loadAvailableFail",
                  UTIL.getErrorDetailFromResponse(err) || "加载可用角色列表失败"
                );
              });
          }
        })
        .catch((err) => {
          commit(
            "loadFail",
            UTIL.getErrorDetailFromResponse(err) || "加载用户失败"
          );
        });
    },
  },
};
