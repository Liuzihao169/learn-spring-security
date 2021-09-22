import ADMIN_API from "@/services/admin.service";
import UTIL from "@/core/util";

export const clientsModule = {
  namespaced: true,
  state: () => ({
    clients: [],
    loading: false,
    error: null,
    addError: null,
    updateError: null,
  }),
  mutations: {
    loadSuccess: (state, payload) => {
      state.clients = payload;
      state.error = null;
      state.loading = false;
    },
    loadFail: (state, payload) => {
      state.error = payload;
      state.loading = false;
    },
    startLoad: (state) => {
      state.loading = true;
    },
    addSuccess: (state, payload) => {
      state.clients.splice(0, 0, payload);
      state.addError = null;
      state.loading = false;
    },
    addFail: (state, payload) => {
      state.addError = payload;
      state.loading = false;
    },
    updateSuccess: (state, payload) => {
      const idx = state.clients.findIndex(
        (old) => old.client_id === payload.client_id
      );
      state.clients.splice(idx, 1, payload);
      state.updateError = null;
      state.loading = false;
    },
    updateFail: (state, payload) => {
      state.updateError = payload;
      state.loading = false;
    },
    deleteSuccess: (state, payload) => {
      const idx = state.clients.findIndex((old) => old.client_id === payload);
      state.clients.splice(idx, 1);
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
      return ADMIN_API.addClient(payload)
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
      return ADMIN_API.updateClient(payload)
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
      ADMIN_API.deleteClient(payload)
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
    load: ({ commit }) => {
      commit("startLoad");
      ADMIN_API.loadClients()
        .then((res) => {
          if (res && res.data) {
            commit("loadSuccess", res.data);
          }
        })
        .catch((err) => {
          commit(
            "loadFail",
            UTIL.getErrorDetailFromResponse(err) || "获取客户端列表失败"
          );
        });
    },
  },
  getters: {
    clientById: (state) => {
      return (clientId) => {
        const filtered = state.clients.filter(
          (client) => `${client.client_id}` === clientId
        );
        return filtered.length > 0 ? filtered[0] : null;
      };
    },
  },
};
