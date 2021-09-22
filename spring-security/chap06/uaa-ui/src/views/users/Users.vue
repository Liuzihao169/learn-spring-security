<template>
  <div>
    <div style="margin-bottom: 16px">
      <a-button type="primary" @click="showAdd">添加用户</a-button>
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
        <a-button type="link" @click="showEdit(record)">编辑</a-button>
        <a-popconfirm
          title="确认切换用户状态？"
          ok-text="确认"
          cancel-text="取消"
          @confirm="() => toggleUserEnabled(record.username)"
        >
          <a-button type="link">
            {{
            record.enabled ? "禁用" : "启用"
            }}
          </a-button>
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
</template>

<script>
import { usersColumns } from "../column-defs/users";
import EditUserDialog from "./components/EditUserDialog";
import AddUserDialog from "./components/AddUserDialog";
import TableFilter from "@/components/TableFilter";

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
    }
  },
  methods: {
    handleAdd(value) {
      console.log(value);
    },
    showAdd() {
      this.showAddDialog = true;
    },
    handleUpdate(value) {
      console.log(value);
    },
    showEdit(record) {
      this.showEditDialog = true;
      // 更新表单 model
      this.editModel = { ...record };
    },
    toggleUserEnabled(username) {
      console.log(username);
    },
    navToUserRoles(username) {
      this.$router.push({ path: `/users/${username}` });
    },
    handleTableChange(pagination, filters, sorter) {
      this.pagination = pagination;
      this.$store.dispatch("usersModule/load", {
        page: pagination.current - 1,
        offset: (pagination.current - 1) * pagination.pageSize,
        sort: { field: sorter.field, order: sorter.order },
        filters: { ...filters }
      });
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
