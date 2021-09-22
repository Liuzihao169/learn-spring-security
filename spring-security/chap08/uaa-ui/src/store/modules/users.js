import ADMIN_API from "@/services/admin.service";
import UTIL from "@/core/util";
import { userRolesModule } from "./user-roles";

export const usersModule = {
  namespaced: true,
  state: () => ({
    users: [],
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
    loadSuccess: (state, { users, page, offset, sort, filters, total }) => {
      state.users = users;
      state.filters = filters;
      state.page = page;
      state.offset = offset;
      state.error = null;
      state.loading = false;
      state.sort = sort;
      state.total = total;
      state.selectedId = null;
    },
    loadFail: (state, payload) => {
      state.error = payload;
      state.loading = false;
    },
    startLoad: (state) => {
      state.loading = true;
    },
    addSuccess: (state, payload) => {
      state.users.splice(0, 0, payload);
      state.total += 1;
      state.addError = null;
      state.loading = false;
    },
    addFail: (state, payload) => {
      state.addError = payload;
      state.loading = false;
    },
    updateSuccess: (state, payload) => {
      const idx = state.users.findIndex((old) => old.id === payload.id);
      state.users.splice(idx, 1, payload);
      state.updateError = null;
      state.loading = false;
    },
    updateFail: (state, payload) => {
      state.updateError = payload;
      state.loading = false;
    },
    toggleEnabledSuccess: (state, payload) => {
      const idx = state.users.findIndex((old) => old.id === payload.id);
      state.users.splice(idx, 1, payload);
      state.error = null;
      state.loading = false;
    },
    toggleEnabledFail: (state, payload) => {
      state.error = payload;
      state.loading = false;
    },
  },
  actions: {
    toggleEnabled: ({ commit }, payload) => {
      commit("startLoad");
      ADMIN_API.toggleEnabled(payload)
        .then((res) => {
          if (res && res.data) {
            commit("toggleEnabledSuccess", res.data);
          }
        })
        .catch((err) => {
          commit(
            "toggleEnabledFail",
            UTIL.getErrorDetailFromResponse(err) || "切换用户状态失败"
          );
        });
    },
    add: ({ commit }, payload) => {
      commit("startLoad");
      return ADMIN_API.addUser(payload)
        .then((res) => {
          if (res && res.data) {
            commit("addSuccess", res.data);
            return Promise.resolve(res.data);
          }
        })
        .catch((err) => {
          commit(
            "addFail",
            UTIL.getErrorDetailFromResponse(err) || "添加用户失败"
          );
          return Promise.reject(err);
        });
    },
    update: ({ commit }, payload) => {
      commit("startLoad");
      return ADMIN_API.updateUser(payload)
        .then((res) => {
          if (res && res.data) {
            commit("updateSuccess", res.data);
            return Promise.resolve(res.data);
          }
        })
        .catch((err) => {
          commit(
            "updateFail",
            UTIL.getErrorDetailFromResponse(err) || "更改用户信息失败"
          );
          return Promise.reject(err);
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
      return ADMIN_API.loadUsers(size, page, offset, sortParam, filterParam)
        .then((res) => {
          if (res && res.data) {
            commit("loadSuccess", {
              users: res.data.content,
              page: res.data.pageable.pageNumber,
              offset: res.data.pageable.offset,
              sort,
              filters,
              total: res.data.totalElements,
            });
            return Promise.resolve(res.data);
          }
        })
        .catch((err) => {
          commit(
            "loadFail",
            UTIL.getErrorDetailFromResponse(err) || "获取用户列表失败"
          );
          return Promise.reject(err);
        });
    },
  },
  getters: {
    /**
     * 这里展示了如何向 getter 传入参数，和大家的直觉不同的是，这个参数是在 getter 内部返回的函数中定义的
     */
    userByUsername: (state) => {
      return (username) => {
        const filtered = state.users.filter(
          (user) => user.username === username
        );
        return filtered.length > 0 ? filtered[0] : null;
      };
    },
  },
  modules: {
    userRolesModule,
  },
};
