<template>
  <a-modal v-model="visible" title="添加客户端" on-ok="handleOk">
    <div v-if="$store.state.clientsModule.addError">
      <a-alert
        style="width:100%"
        message="添加客户端失败"
        v-bind:description="$store.state.clientsModule.addError"
        type="error"
      />
    </div>
    <a-form-model ref="form" :model="model" :rules="rules" v-bind="layout">
      <a-form-model-item has-feedback prop="clientId" label="客户端 Id">
        <a-input v-model="model.clientId" type="text" autocomplete="off" />
      </a-form-model-item>
      <a-form-model-item has-feedback prop="clientSecret" label="客户端 Secret">
        <a-input v-model="model.clientSecret" type="text" autocomplete="off" />
      </a-form-model-item>
      <a-form-model-item has-feedback prop="scopes" label="授权范围">
        <a-select
          v-model="model.scopes"
          mode="multiple"
          style="width: 100%"
          placeholder="请选择授权范围"
          option-label-prop="label"
        >
          <a-select-option value="user.admin" label="用户管理">
            用户管理
          </a-select-option>
          <a-select-option value="client.admin" label="客户端管理">
            客户端管理
          </a-select-option>
          <a-select-option value="todo.read" label="读取待办事项">
            读取待办事项
          </a-select-option>
          <a-select-option value="todo.write" label="写入待办事项">
            写入待办事项
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item has-feedback prop="grantTypes" label="授权类型">
        <a-select
          v-model="model.grantTypes"
          mode="multiple"
          style="width: 100%"
          placeholder="请选择授权类型"
          option-label-prop="label"
        >
          <a-select-option value="authorization_code" label="授权码">
            授权码
          </a-select-option>
          <a-select-option value="refresh_token" label="刷新">
            刷新令牌
          </a-select-option>
          <a-select-option value="password" label="密码">
            资源所有者密码
          </a-select-option>
          <a-select-option value="client_credentials" label="隐式">
            客户端密钥
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item has-feedback prop="redirectUris" label="重定向 URI">
        <a-input v-model="model.redirectUris" type="text" autocomplete="off" />
      </a-form-model-item>
      <a-form-model-item has-feedback prop="autoApproves" label="自动授权">
        <a-select
          v-model="model.autoApproves"
          mode="multiple"
          style="width: 100%"
          placeholder="请选择授权范围"
          option-label-prop="label"
        >
          <a-select-option value="user.admin" label="用户管理">
            用户管理
          </a-select-option>
          <a-select-option value="client.admin" label="客户端管理">
            客户端管理
          </a-select-option>
          <a-select-option value="todo.read" label="读取待办事项">
            读取待办事项
          </a-select-option>
          <a-select-option value="todo.write" label="写入待办事项">
            写入待办事项
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item
        has-feedback
        prop="accessTokenValidity"
        label="访问令牌有效期"
      >
        <a-input-number
          :min="60"
          :max="2592000"
          v-model="model.accessTokenValidity"
          type="text"
          autocomplete="off"
        />
      </a-form-model-item>
      <a-form-model-item
        has-feedback
        prop="refreshTokenValidity"
        label="刷新令牌有效期"
      >
        <a-input-number
          :min="900"
          :max="31536000"
          v-model="model.refreshTokenValidity"
          type="text"
          autocomplete="off"
        />
      </a-form-model-item>
    </a-form-model>
    <template slot="footer">
      <a-button key="back" @click="handleCancel">返回</a-button>
      <a-button
        key="submit"
        type="primary"
        :loading="$store.state.clientsModule.loading"
        @click="handleOk"
        >确认</a-button
      >
    </template>
  </a-modal>
</template>

<script>
import { addClientRules } from "@/views/form-rules/add-client";
export default {
  props: {
    show: Boolean,
    record: Object,
  },
  data() {
    return {
      layout: {
        labelCol: { span: 6 },
        wrapperCol: { span: 12 },
      },
    };
  },
  computed: {
    rules: function() {
      return addClientRules();
    },
    visible: {
      get() {
        return this.show;
      },
      set(value) {
        this.$emit("closed", value);
      },
    },
    model: {
      get() {
        return this.record;
      },
    },
  },
  methods: {
    handleOk() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.$emit("submitted", this.model);
          const client = {
            ...this.model,
            redirectUris: this.model.redirectUris.split(","),
          };
          this.$store.dispatch("clientsModule/add", client).then((res) => {
            if (res) {
              this.$refs.form.resetFields();
              this.visible = false;
            }
          });
        }
      });
    },
    handleCancel() {
      this.visible = false;
    },
  },
};
</script>

<style></style>
