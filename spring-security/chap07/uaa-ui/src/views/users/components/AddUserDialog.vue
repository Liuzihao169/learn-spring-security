<template>
  <a-modal v-model="visible" title="添加用户" on-ok="handleOk">
    <div v-if="$store.state.usersModule.addError">
      <a-alert
        style="width:100%"
        message="添加用户失败"
        v-bind:description="$store.state.usersModule.addError"
        type="error"
      />
    </div>
    <a-form-model ref="form" :model="model" :rules="rules" v-bind="layout">
      <a-form-model-item has-feedback prop="username" label="用户名">
        <a-input v-model="model.username" type="text" autocomplete="off" />
      </a-form-model-item>
      <a-form-model-item has-feedback prop="name" label="姓名">
        <a-input v-model="model.name" type="text" autocomplete="off" />
      </a-form-model-item>
      <a-form-model-item has-feedback prop="email" label="电子邮件">
        <a-input v-model="model.email" type="text" autocomplete="off" />
      </a-form-model-item>
      <a-form-model-item has-feedback prop="mobile" label="手机">
        <a-input v-model="model.mobile" type="text" autocomplete="off" />
      </a-form-model-item>
    </a-form-model>
    <template slot="footer">
      <a-button key="back" @click="handleCancel">返回</a-button>
      <a-button
        key="submit"
        type="primary"
        :loading="$store.state.usersModule.loading"
        @click="handleOk"
      >确认</a-button>
    </template>
  </a-modal>
</template>

<script>
import { addUserRules } from "@/views/form-rules/add-user";
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
      return addUserRules();
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
          this.$store.dispatch("usersModule/add", this.model).then(res => {
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
