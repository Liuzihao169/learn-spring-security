<template>
  <div>
    <div v-if="$store.state.rolesModule.rolePermissionsModule.error">
      <a-alert
        style="width:100%"
        message="请求失败"
        v-bind:description="
          $store.state.rolesModule.rolePermissionsModule.error
        "
        type="error"
      />
    </div>
    <a-row type="flex" justify="space-between" align="stretch">
      <a-table
        style="width: 45%"
        :row-key="(record) => record.id"
        :row-selection="availableRowSelection"
        :columns="columns"
        :data-source="availablePermissions"
        :scroll="{ y: 300 }"
      >
        <template slot="title">
          未分配权限
        </template>
      </a-table>
      <div
        style="display:flex; flex-direction: column; justify-content: center"
      >
        <a-button @click="moveToAssigned"><a-icon type="right" /> </a-button>
        <a-button @click="moveToAvailable"><a-icon type="left" /> </a-button>
      </div>
      <a-table
        style="width: 45%"
        :row-key="(record) => record.id"
        :row-selection="assignedRowSelection"
        :columns="columns"
        :data-source="assignedPermissions"
        :scroll="{ y: 300 }"
      >
        <template slot="title">
          已分配权限
        </template>
      </a-table>
    </a-row>
  </div>
</template>

<script>
import { rolePermissionsColumns } from "../column-defs/role-permissions";

export default {
  data() {
    return {
      columns: rolePermissionsColumns(),
      selectedAvailableRowKeys: [],
      selectedAssignedRowKeys: [],
    };
  },
  computed: {
    availableRowSelection() {
      const { selectedAvailableRowKeys } = this;
      return {
        selectedRowKeys: selectedAvailableRowKeys,
        onChange: this.onAvailableSelectChange,
        hideDefaultSelections: true,
        selections: [
          {
            key: "all-data",
            text: "选择全部",
            onSelect: () => {
              this.selectedAvailableRowKeys = this.availablePermissions.map(
                (role) => role.id
              );
            },
          },
        ],
        onSelection: this.onSelection,
      };
    },
    assignedRowSelection() {
      const { selectedAssignedRowKeys } = this;
      return {
        selectedRowKeys: selectedAssignedRowKeys,
        onChange: this.onAssignedSelectChange,
        hideDefaultSelections: true,
        selections: [
          {
            key: "all-data",
            text: "选择全部",
            onSelect: () => {
              this.selectedAssignedRowKeys = this.assignedPermissions.map(
                (role) => role.id
              );
            },
          },
        ],
        onSelection: this.onSelection,
      };
    },
    availablePermissions: function() {
      return this.$store.state.rolesModule.rolePermissionsModule
        .availablePermissions;
    },
    assignedPermissions: function() {
      return this.$store.state.rolesModule.rolePermissionsModule
        .assignedPermissions;
    },
  },
  methods: {
    onAvailableSelectChange(selectedRowKeys) {
      this.selectedAvailableRowKeys = selectedRowKeys;
    },
    onAssignedSelectChange(selectedRowKeys) {
      this.selectedAssignedRowKeys = selectedRowKeys;
    },
    moveToAssigned() {
      if (this.selectedAvailableRowKeys.length === 0) {
        return;
      }
      this.$store.dispatch(
        "rolesModule/rolePermissionsModule/moveToAssigned",
        this.selectedAvailableRowKeys
      );
      this.selectedAvailableRowKeys = [];
    },
    moveToAvailable() {
      if (this.selectedAssignedRowKeys.length === 0) {
        return;
      }
      this.$store.dispatch(
        "rolesModule/rolePermissionsModule/moveToAvailable",
        this.selectedAssignedRowKeys
      );
      this.selectedAssignedRowKeys = [];
    },
  },
  mounted() {
    this.$store.dispatch(
      "rolesModule/rolePermissionsModule/load",
      this.$route.params.roleId
    );
  },
};
</script>

<style></style>
