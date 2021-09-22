<template>
  <div>
    <div v-if="$store.state.usersModule.error">
      <a-alert
        style="width:100%"
        message="请求失败"
        v-bind:description="$store.state.usersModule.error"
        type="error"
      />
    </div>
    <div v-if="selectedUser">
      <a-page-header
        :title="selectedUser.name"
        :subTitle="selectedUser.username"
        @back="() => $router.go(-1)"
      >
        <template slot="extra">
          <a-button key="1" type="primary" @click="saveUserRoles">保存</a-button>
        </template>
        <template slot="tags">
          <a-tag color="blue">
            {{
            selectedUser.enabled ? "激活" : "禁用"
            }}
          </a-tag>
        </template>
        <a-descriptions size="small" :column="3">
          <a-descriptions-item label="电子邮件">
            {{
            selectedUser.email
            }}
          </a-descriptions-item>
          <a-descriptions-item label="手机">
            <a>{{ selectedUser.mobile }}</a>
          </a-descriptions-item>
        </a-descriptions>
      </a-page-header>
    </div>
    <div v-else>
      <div style="margin-bottom: 16px">
        <a-button type="primary" @click="showAdd" v-if="hasAddUserPermission">添加用户</a-button>
      </div>
      <a-table
        :row-key="(item) => item.id"
        :columns="columns"
        :data-source="$store.state.usersModule.users"
        :pagination="pagination"
        :loading="$store.state.usersModule.loading"
        :locale="{
          filterConfirm: '确定',
          filterReset: '重置',
          emptyText: '暂无数据',
        }"
        @change="handleTableChange"
        bordered
        :scroll="{ y: 460 }"
      >
        <!-- 自定义过滤器模版 -->
        <TableFilter
          slot="filterDropdown"
          slot-scope="{
            setSelectedKeys,
            selectedKeys,
            confirm,
            clearFilters,
            column,
          }"
          v-bind:setSelectedKeys="setSelectedKeys"
          v-bind:selectedKeys="selectedKeys"
          v-bind:confirm="confirm"
          v-bind:clearFilters="clearFilters"
          v-bind:column="column"
        />
        <!-- 自定义过滤器图标 -->
        <a-icon
          slot="filterIcon"
          slot-scope="filtered"
          type="search"
          :style="{ color: filtered ? '#108ee9' : undefined }"
        />
        <span slot="roles" slot-scope="roles">
          <a-tag
            v-for="role in roles"
            :key="role.id"
            :color="
              role.roleName === 'ROLE_ADMIN'
                ? 'volcano'
                : role.roleName === 'ROLE_STAFF'
                ? 'geekblue'
                : 'green'
            "
          >{{ role.displayName.toUpperCase() }}</a-tag>
        </span>
        <!-- 自定义用户状态列 -->
        <template slot="enabled" slot-scope="text">
          <a-icon
            type="check-circle"
            v-if="text"
            theme="twoTone"
            two-tone-color="#52c41a"
            style="font-size: 24px"
          />
          <a-icon
            type="close-circle"
            v-else
            theme="twoTone"
            two-tone-color="#eb2f96"
            style="font-size: 24px"
          />
        </template>
        <!-- 操作列模版 -->
        <template slot="operation" slot-scope="text, record">
          <!-- 操作列的几个按钮 -->
          <a-button type="link" @click="showEdit(record)" :disabled="!hasUpdateUserPermission">编辑</a-button>
          <a-popconfirm
            title="确认切换用户状态？"
            ok-text="确认"
            cancel-text="取消"
            :disabled="!hasUpdateUserPermission || isSelf(record.username)"
            @confirm="toggleUserEnabled(record.username)"
          >
            <a-button
              type="link"
              :disabled="!hasUpdateUserPermission || isSelf(record.username)"
            >{{ record.enabled ? "禁用" : "启用" }}</a-button>
          </a-popconfirm>
          <a-button
            type="link"
            @click="navToUserRoles(record.username)"
            :disabled="!hasUpdateUserPermission || isSelf(record.username)"
          >分配角色</a-button>
        </template>
      </a-table>
      <!-- 添加用户对话框 -->
      <AddUserDialog
        v-bind:show="showAddDialog"
        v-bind:record="addModel"
        v-on:closed="showAddDialog = false"
      />
      <!-- 修改用户对话框 -->
      <EditUserDialog
        v-bind:show="showEditDialog"
        v-bind:record="editModel"
        v-on:closed="showEditDialog = false"
      />
    </div>
    <router-view></router-view>
  </div>
</template>

<script>
import { usersColumns } from "../column-defs/users";
import EditUserDialog from "./components/EditUserDialog";
import AddUserDialog from "./components/AddUserDialog";
import TableFilter from "@/components/TableFilter";
import UTIL from "@/core/util";

export default {
  components: {
    EditUserDialog,
    AddUserDialog,
    TableFilter
  },
  data() {
    return {
      showEditDialog: false,
      editModel: {
        name: "",
        email: "",
        mobile: ""
      },
      showAddDialog: false,
      addModel: {
        username: "",
        name: "",
        email: "",
        mobile: ""
      },
      pagination: {
        defaultPageSize: 20,
        showTotal: () => `共 ${this.$store.state.usersModule.total} 条数据`
      }
    };
  },
  computed: {
    columns: function() {
      return usersColumns();
    },
    hasAddUserPermission: function() {
      return UTIL.hasPermissionIn(["USER_ADD"]);
    },
    hasUpdateUserPermission: function() {
      return UTIL.hasPermissionIn(["USER_UPDATE"]);
    },
    /**
     * 如何使用模块中的 getter，并传递参数
     */
    selectedUser: function() {
      return this.$store.getters["usersModule/userByUsername"](
        this.$route.params.username
      );
    }
  },
  methods: {
    showAdd() {
      this.addModel = {
        username: "",
        name: "",
        email: "",
        mobile: ""
      };
      this.showAddDialog = true;
    },
    showEdit(record) {
      this.showEditDialog = true;
      // 更新表单 model
      this.editModel = { ...record };
    },
    toggleUserEnabled(username) {
      this.$store.dispatch("usersModule/toggleEnabled", username);
    },
    handleTableChange(pagination, filters, sorter) {
      this.pagination = pagination;
      this.$store.dispatch("usersModule/load", {
        page: pagination.current - 1,
        offset: (pagination.current - 1) * pagination.pageSize,
        sort: { field: sorter.field, order: sorter.order },
        filters: { ...filters }
      });
    },
    navToUserRoles(username) {
      this.$router.push({ path: `/users/${username}` });
    },
    isSelf(username) {
      return this.$store.getters.username === username;
    },
    saveUserRoles() {
      if (!this.selectedUser) {
        return;
      }
      this.$store.dispatch(
        "usersModule/userRolesModule/save",
        this.selectedUser.username
      );
    }
  },
  mounted() {
    this.$store.dispatch("usersModule/load", {
      page: 0,
      offset: 0
    });
  }
};
</script>
