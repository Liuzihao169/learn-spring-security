import ADMIN_API from "@/services/admin.service";
import UTIL from "@/core/util";

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
    },
    loadFail: (state, payload) => {
      state.error = payload;
      state.loading = false;
    },
    startLoad: (state) => {
      state.loading = true;
    },
  },
  actions: {
    load: ({ commit }, { page, offset, sort, filters }) => {
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
      ADMIN_API.loadUsers(page, offset, sortParam, filterParam)
        .then((res) => {
          commit("loadSuccess", {
            users: res.data.content,
            page: res.data.pageable.pageNumber,
            offset: res.data.pageable.offset,
            sort,
            filters,
            total: res.data.totalElements,
          });
        })
        .catch((err) => {
          commit(
            "loadFail",
            UTIL.getErrorDetailFromResponse(err.response.data) ||
              "获取用户列表失败"
          );
        });
    },
  },
};
