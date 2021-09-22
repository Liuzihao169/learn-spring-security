import ADMIN_API from "@/services/admin.service";
import UTIL from "@/core/util";
import router from "@/router";
import { union, difference } from "lodash";

export const rolePermissionsModule = {
  namespaced: true,
  state: () => ({
    assignedPermissions: [],
    loading: false,
    error: null,
    availablePermissions: [],
  }),
  mutations: {
    loadAvailableSuccess: (state, payload) => {
      state.availablePermissions = payload;
      state.loading = false;
      state.error = null;
    },
    loadAvailableFail: (state, payload) => {
      state.error = payload;
      state.loading = false;
      state.availablePermissions = [];
    },
    loadSuccess: (state, payload) => {
      state.assignedPermissions = payload.permissions;
      state.loading = false;
      state.error = null;
    },
    loadFail: (state, payload) => {
      state.error = payload;
      state.assignedPermissions = [];
      state.loading = false;
    },
    startLoad: (state) => {
      state.loading = true;
    },
    moveToAssignedSuccess: (state, payload) => {
      const selectedRoles = state.availablePermissions.filter((role) =>
        payload.includes(role.id)
      );
      state.assignedPermissions = union(
        state.assignedPermissions,
        selectedRoles
      );
      state.availablePermissions = difference(
        state.availablePermissions,
        selectedRoles
      );
    },
    moveToAvailableASuccess: (state, payload) => {
      const selectedRoles = state.assignedPermissions.filter((role) =>
        payload.includes(role.id)
      );
      state.availablePermissions = union(
        state.availablePermissions,
        selectedRoles
      );
      state.assignedPermissions = difference(
        state.assignedPermissions,
        selectedRoles
      );
    },
    saveFail: (state, payload) => {
      state.error = payload;
      state.loading = false;
    },
  },
  actions: {
    save: ({ commit, state }, payload) => {
      commit("startLoad");
      ADMIN_API.saveRolePermissions(
        payload,
        state.assignedPermissions.map((permission) => permission.id)
      )
        .then((res) => {
          if (res && res.data) {
            commit("rolesModule/updateSuccess", res.data, { root: true });
            router.push("/roles");
          }
        })
        .catch((err) => {
          commit(
            "saveFail",
            UTIL.getErrorDetailFromResponse(err) || "保存角色权限列表失败"
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
      ADMIN_API.loadByRoleId(payload)
        .then((res) => {
          if (res && res.data) {
            commit("loadSuccess", res.data);
            ADMIN_API.loadAvailablePermissions(payload)
              .then((res) => {
                if (res && res.data) {
                  commit("loadAvailableSuccess", res.data);
                }
              })
              .catch((err) => {
                commit(
                  "loadAvailableFail",
                  UTIL.getErrorDetailFromResponse(err) || "加载可用权限列表失败"
                );
              });
          }
        })
        .catch((err) => {
          commit(
            "loadFail",
            UTIL.getErrorDetailFromResponse(err) || "加载角色失败"
          );
        });
    },
  },
};
