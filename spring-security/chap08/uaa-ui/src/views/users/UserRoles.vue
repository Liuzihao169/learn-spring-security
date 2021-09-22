<template>
  <div>
    <div v-if="$store.state.usersModule.userRolesModule.error">
      <a-alert
        style="width:100%"
        message="请求失败"
        v-bind:description="$store.state.usersModule.userRolesModule.error"
        type="error"
      />
    </div>
    <a-row type="flex" justify="space-between" align="stretch">
      <a-table
        style="width: 45%"
        :row-key="(record) => record.id"
        :row-selection="availableRowSelection"
        :columns="columns"
        :data-source="availableRoles"
        :scroll="{ y: 300 }"
      >
        <template slot="title">
          未分配角色
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
        :data-source="assignedRoles"
        :scroll="{ y: 300 }"
      >
        <template slot="title">
          已分配角色
        </template>
      </a-table>
    </a-row>
  </div>
</template>

<script>
import { userRolesColumns } from "../column-defs/user-roles";

export default {
  data() {
    return {
      columns: userRolesColumns(),
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
              this.selectedAvailableRowKeys = this.availableRoles.map(
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
              this.selectedAssignedRowKeys = this.assignedRoles.map(
                (role) => role.id
              );
            },
          },
        ],
        onSelection: this.onSelection,
      };
    },
    availableRoles: function() {
      return this.$store.state.usersModule.userRolesModule.availableRoles;
    },
    assignedRoles: function() {
      return this.$store.state.usersModule.userRolesModule.assignedRoles;
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
        "usersModule/userRolesModule/moveToAssigned",
        this.selectedAvailableRowKeys
      );
      this.selectedAvailableRowKeys = [];
    },
    moveToAvailable() {
      if (this.selectedAssignedRowKeys.length === 0) {
        return;
      }
      this.$store.dispatch(
        "usersModule/userRolesModule/moveToAvailable",
        this.selectedAssignedRowKeys
      );
      this.selectedAssignedRowKeys = [];
    },
  },
  mounted() {
    this.$store.dispatch(
      "usersModule/userRolesModule/load",
      this.$route.params.username
    );
  },
};
</script>

<style></style>
