<template>
  <a-modal v-model="visible" title="添加角色" on-ok="handleOk">
    <div v-if="$store.state.rolesModule.addError">
      <a-alert
        style="width:100%"
        message="添加角色失败"
        v-bind:description="$store.state.rolesModule.addError"
        type="error"
      />
    </div>
    <a-form-model ref="form" :model="model" :rules="rules" v-bind="layout">
      <a-form-model-item has-feedback prop="roleName" label="角色标识">
        <a-input v-model="model.roleName" type="text" autocomplete="off" />
      </a-form-model-item>
      <a-form-model-item has-feedback prop="displayName" label="角色名称">
        <a-input v-model="model.displayName" type="text" autocomplete="off" />
      </a-form-model-item>
    </a-form-model>
    <template slot="footer">
      <a-button key="back" @click="handleCancel">返回</a-button>
      <a-button
        key="submit"
        type="primary"
        :loading="$store.state.rolesModule.loading"
        @click="handleOk"
      >确认</a-button>
    </template>
  </a-modal>
</template>

<script>
import { addRoleRules } from "@/views/form-rules/add-role";
export default {
  props: {
    show: Boolean,
    record: Object
  },
  data() {
    return {
      layout: {
        labelCol: { span: 4 },
        wrapperCol: { span: 12 }
      }
    };
  },
  computed: {
    rules: function() {
      return addRoleRules();
    },
    visible: {
      get() {
        return this.show;
      },
      set(value) {
        this.$emit("closed", value);
      }
    },
    model: {
      get() {
        return this.record;
      }
    }
  },
  methods: {
    handleOk() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.$emit("submitted", this.model);
          this.$store.dispatch("rolesModule/add", this.model).then(res => {
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
    }
  }
};
</script>

<style></style>
