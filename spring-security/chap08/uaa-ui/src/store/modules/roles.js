import ADMIN_API from "@/services/admin.service";
import UTIL from "@/core/util";
import { rolePermissionsModule } from "./role-permissions";

export const rolesModule = {
  namespaced: true,
  state: () => ({
    roles: [],
    loading: false,
    page: 0,
    offset: 0,
    total: 0,
    sort: null,
    filters: null,
    error: null,
    addError: null,
    updateError: null,
  }),
  mutations: {
    loadSuccess: (state, { roles, page, offset, sort, filters, total }) => {
      state.roles = roles;
      state.filters = filters;
      state.page = page;
      state.offset = offset;
      state.error = null;
      state.loading = false;
      state.sort = sort;
      state.total = total;
    },
    loadFail: (state, payload) => {
      state.error = payload;
      state.loading = false;
    },
    startLoad: (state) => {
      state.loading = true;
    },
    addSuccess: (state, payload) => {
      state.roles.splice(0, 0, payload);
      state.total += 1;
      state.addError = null;
      state.loading = false;
    },
    addFail: (state, payload) => {
      state.addError = payload;
      state.loading = false;
    },
    updateSuccess: (state, payload) => {
      const idx = state.roles.findIndex((old) => old.id === payload.id);
      state.roles.splice(idx, 1, payload);
      state.updateError = null;
      state.loading = false;
    },
    updateFail: (state, payload) => {
      state.updateError = payload;
      state.loading = false;
    },
    deleteSuccess: (state, payload) => {
      const idx = state.roles.findIndex((old) => old.id === payload);
      state.roles.splice(idx, 1);
      state.total -= 1;
      state.error = null;
      state.loading = false;
    },
    deleteFail: (state, payload) => {
      state.error = payload;
      state.loading = false;
    },
  },
  actions: {
    add: ({ commit }, payload) => {
      commit("startLoad");
      return ADMIN_API.addRole(payload)
        .then((res) => {
          if (res && res.data) {
            commit("addSuccess", res.data);
            return Promise.resolve(res.data);
          }
        })
        .catch((err) => {
          commit(
            "addFail",
            UTIL.getErrorDetailFromResponse(err) || "添加角色失败"
          );
          return Promise.reject(err);
        });
    },
    update: ({ commit }, payload) => {
      commit("startLoad");
      return ADMIN_API.updateRole(payload)
        .then((res) => {
          if (res && res.data) {
            commit("updateSuccess", res.data);
            return Promise.resolve(res.data);
          }
        })
        .catch((err) => {
          commit(
            "updateFail",
            UTIL.getErrorDetailFromResponse(err) || "更改角色信息失败"
          );
          return Promise.reject(err);
        });
    },
    delete: ({ commit }, payload) => {
      commit("startLoad");
      ADMIN_API.deleteRole(payload)
        .then(() => {
          commit("deleteSuccess", payload);
        })
        .catch((err) => {
          commit(
            "deleteFail",
            UTIL.getErrorDetailFromResponse(err) || "删除角色信息失败"
          );
        });
    },
    load: ({ commit }, { size, page, offset, sort, filters }) => {
      commit("startLoad");
      const sortParam =
        sort && sort.order
          ? `${sort.field},${sort.order === "ascend" ? "asc" : "desc"}`
          : `id,desc`;
      const filterParam = filters
        ? Object.keys(filters).reduce(
            (acc, curr) => ({ ...acc, [curr]: filters[curr].join(",") }),
            {}
          )
        : null;
      ADMIN_API.loadRoles(size, page, offset, sortParam, filterParam)
        .then((res) => {
          if (res && res.data) {
            commit("loadSuccess", {
              roles: res.data.content,
              page: res.data.pageable.pageNumber,
              offset: res.data.pageable.offset,
              sort,
              filters,
              total: res.data.totalElements,
            });
          }
        })
        .catch((err) => {
          commit(
            "loadFail",
            UTIL.getErrorDetailFromResponse(err) || "获取角色列表失败"
          );
        });
    },
  },
  getters: {
    roleById: (state) => {
      return (roleId) => {
        const filtered = state.roles.filter((role) => `${role.id}` === roleId);
        return filtered.length > 0 ? filtered[0] : null;
      };
    },
  },
  modules: {
    rolePermissionsModule,
  },
};
