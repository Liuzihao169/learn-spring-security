<template>
  <div>
    <div v-if="$store.state.rolesModule.error">
      <a-alert
        style="width:100%"
        message="请求失败"
        v-bind:description="$store.state.rolesModule.error"
        type="error"
      />
    </div>
    <div v-if="selectedRole">
      <a-page-header
        :title="selectedRole.roleName"
        :subTitle="selectedRole.displayName"
        @back="() => $router.go(-1)"
      >
        <template slot="extra">
          <a-button key="1" type="primary" @click="saveRolePermissions">保存</a-button>
        </template>
      </a-page-header>
    </div>
    <div v-else>
      <div style="margin-bottom: 16px">
        <a-button type="primary" @click="showAdd" v-if="hasAddRolePermission">添加角色</a-button>
      </div>
      <a-table
        :row-key="(item) => item.id"
        :columns="columns"
        :data-source="$store.state.rolesModule.roles"
        :pagination="pagination"
        :loading="$store.state.rolesModule.loading"
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
        <!-- 自定义是否内置角色列 -->
        <template slot="builtIn" slot-scope="text">
          <a-icon
            type="lock"
            v-if="text"
            theme="twoTone"
            two-tone-color="#eb2f96"
            style="font-size: 24px"
          />
          <a-icon
            type="unlock"
            v-else
            theme="twoTone"
            two-tone-color="#52c41a"
            style="font-size: 24px"
          />
        </template>
        <span slot="permissions" slot-scope="permissions">
          <a-tag
            v-for="permission in permissions"
            :key="permission.id"
            :color="
              permission.authority.endsWith('CREATE')
                ? 'volcano'
                : permission.authority.endsWith('UPDATE')
                ? 'geekblue'
                : 'green'
            "
          >{{ permission.displayName.toUpperCase() }}</a-tag>
        </span>
        <!-- 操作列模版 -->
        <template slot="operation" slot-scope="text, record">
          <!-- 操作列的几个按钮 -->
          <a-button
            type="link"
            @click="showEdit(record)"
            :disabled="!hasUpdateRolePermission || record.builtIn"
          >编辑</a-button>
          <a-popconfirm
            title="确认删除角色？"
            ok-text="确认"
            cancel-text="取消"
            @confirm="() => deleteById(record.id)"
          >
            <a-button type="link" :disabled="!hasUpdateRolePermission || record.builtIn">删除</a-button>
          </a-popconfirm>
          <a-button
            type="link"
            @click="navToRolePermissions(record.id)"
            :disabled="!hasUpdateRolePermission"
          >分配权限</a-button>
        </template>
      </a-table>
      <!-- 添加角色对话框 -->
      <AddRoleDialog
        v-bind:show="showAddDialog"
        v-bind:record="addModel"
        v-on:closed="showAddDialog = false"
      />
      <!-- 修改角色对话框 -->
      <EditRoleDialog
        v-bind:show="showEditDialog"
        v-bind:record="editModel"
        v-on:closed="showEditDialog = false"
      />
    </div>
    <router-view></router-view>
  </div>
</template>

<script>
import { rolesColumns } from "../column-defs/roles";
import EditRoleDialog from "./components/EditRoleDialog";
import AddRoleDialog from "./components/AddRoleDialog";
import TableFilter from "@/components/TableFilter";
import UTIL from "@/core/util";

export default {
  components: {
    EditRoleDialog,
    AddRoleDialog,
    TableFilter
  },
  data() {
    return {
      showEditDialog: false,
      editModel: {
        roleName: "",
        displayName: ""
      },
      showAddDialog: false,
      addModel: {
        roleName: "",
        displayName: ""
      },
      pagination: {
        defaultPageSize: 20,
        showTotal: () => `共 ${this.$store.state.rolesModule.total} 条数据`
      }
    };
  },
  computed: {
    columns: function() {
      return rolesColumns();
    },
    hasAddRolePermission: function() {
      return UTIL.hasPermissionIn(["RL_ADD"]);
    },
    hasUpdateRolePermission: function() {
      return UTIL.hasPermissionIn(["RL_UPDATE"]);
    },
    selectedRole: function() {
      return this.$store.getters["rolesModule/roleById"](
        this.$route.params.roleId
      );
    }
  },
  methods: {
    showAdd() {
      this.showAddDialog = true;
    },
    showEdit(record) {
      this.showEditDialog = true;
      // 更新表单 model
      this.editModel = { ...record };
    },
    handleTableChange(pagination, filters, sorter) {
      this.pagination = pagination;
      this.$store.dispatch("rolesModule/load", {
        page: pagination.current - 1,
        offset: (pagination.current - 1) * pagination.pageSize,
        sort: { field: sorter.field, order: sorter.order },
        filters: { ...filters }
      });
    },
    deleteById(id) {
      this.$store.dispatch("rolesModule/delete", id);
    },
    navToRolePermissions(roleId) {
      this.$router.push({ path: `/roles/${roleId}` });
    },
    saveRolePermissions() {
      if (!this.selectedRole) {
        return;
      }
      this.$store.dispatch(
        "rolesModule/rolePermissionsModule/save",
        this.selectedRole.id
      );
    }
  },
  mounted() {
    this.$store.dispatch("rolesModule/load", {
      page: 0,
      offset: 0
    });
  }
};
</script>

<style></style>
