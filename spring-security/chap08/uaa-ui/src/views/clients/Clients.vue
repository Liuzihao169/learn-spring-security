<template>
  <div>
    <div v-if="error">
      <a-alert
        style="width:100%"
        message="请求失败"
        v-bind:description="error"
        type="error"
      />
    </div>
    <div style="margin-bottom: 16px">
      <a-button type="primary" @click="showAdd" v-if="hasAddClientPermission"
        >添加客户端</a-button
      >
    </div>
    <a-table
      :rowKey="(item) => item.client_id"
      :columns="columns"
      :data-source="$store.state.clientsModule.clients"
      :scroll="{ y: 460 }"
    >
      <template slot="authorized_grant_types" slot-scope="types">
        <a-tag v-for="type in types" :key="type">{{ type }}</a-tag>
      </template>
      <template slot="redirect_uri" slot-scope="uris">
        <a-tag v-for="uri in uris" :key="uri">{{ uri }}</a-tag>
      </template>
      <template slot="scope" slot-scope="scopes, record">
        <a-tag
          v-for="scope in scopes"
          :key="scope"
          :color="getScopeColor(record, scope)"
          >{{ scope }}</a-tag
        >
      </template>
      <!-- 操作列模版 -->
      <template slot="operation" slot-scope="text, record">
        <!-- 操作列的几个按钮 -->
        <a-button
          type="link"
          @click="showEdit(record)"
          :disabled="!hasUpdateClientPermission"
          >编辑</a-button
        >
        <a-popconfirm
          title="确认删除客户端？"
          ok-text="确认"
          cancel-text="取消"
          :disabled="!hasUpdateClientPermission"
          @confirm="() => deleteById(record.client_id)"
        >
          <a-button type="link" :disabled="!hasUpdateClientPermission"
            >删除</a-button
          >
        </a-popconfirm>
      </template>
    </a-table>
    <!-- 添加客户端对话框 -->
    <AddClientDialog
      v-bind:show="showAddDialog"
      v-bind:record="addModel"
      v-on:closed="showAddDialog = false"
    />
    <!-- 修改客户端对话框 -->
    <EditClientDialog
      v-bind:show="showEditDialog"
      v-bind:record="editModel"
      v-on:closed="showEditDialog = false"
    />
  </div>
</template>
<script>
import { columns } from "../column-defs/clients";
import UTIL from "@/core/util";
import EditClientDialog from "./components/EditClientDialog";
import AddClientDialog from "./components/AddClientDialog";

export default {
  components: {
    EditClientDialog,
    AddClientDialog,
  },
  data() {
    return {
      columns,
      error: null,
      showEditDialog: false,
      editModel: {
        clientId: "",
        clientSecret: "",
        scopes: [],
        grantTypes: [],
        redirectUris: [],
        autoApproves: [],
        accessTokenValidity: "",
        refreshTokenValidity: "",
      },
      showAddDialog: false,
      addModel: {
        clientId: "",
        clientSecret: "",
        scopes: [],
        grantTypes: [],
        redirectUris: "",
        autoApproves: [],
        accessTokenValidity: "",
        refreshTokenValidity: "",
      },
    };
  },
  computed: {
    hasAddClientPermission: function() {
      return UTIL.hasPermissionIn(["CLIENT_ADD"]);
    },
    hasUpdateClientPermission: function() {
      return UTIL.hasPermissionIn(["CLIENT_UPDATE"]);
    },
  },
  methods: {
    showAdd() {
      this.showAddDialog = true;
    },
    showEdit(record) {
      this.showEditDialog = true;
      // 更新表单 model
      this.editModel = {
        clientId: record.client_id,
        clientSecret: record.client_secret.replace(/{\w+}/, ""),
        scopes: record.scope,
        grantTypes: record.authorized_grant_types,
        redirectUris: record.redirect_uri,
        autoApproves: record.autoapprove,
        accessTokenValidity: record.access_token_validity,
        refreshTokenValidity: record.refresh_token_validity,
      };
    },
    deleteById(id) {
      this.$store.dispatch("clientsModule/delete", id);
    },
    getScopeColor(record, scope) {
      if (!record.autoapprove) {
        return "volcano";
      }
      if (record.autoapprove[0] === "TRUE") {
        return "green";
      }
      return record.autoapprove.some((a) => a === scope) ? "green" : "volcano";
    },
  },
  mounted() {
    this.$store.dispatch("clientsModule/load");
  },
};
</script>
