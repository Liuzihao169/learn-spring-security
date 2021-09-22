<template>
  <a-modal v-model="visible" title="修改用户" on-ok="handleOk">
    <a-form-model ref="form" :model="model" :rules="rules" v-bind="layout">
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
        >确认</a-button
      >
    </template>
  </a-modal>
</template>

<script>
import { editUserRules } from "@/views/form-rules/edit-user";
export default {
  props: {
    show: Boolean,
    record: Object,
  },
  data() {
    return {
      layout: {
        labelCol: { span: 4 },
        wrapperCol: { span: 12 },
      },
    };
  },
  computed: {
    rules: function() {
      return editUserRules(this.model);
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
      this.$emit("submitted", this.model);
      this.visible = false;
    },
    handleCancel() {
      this.visible = false;
    },
  },
};
</script>

<style></style>
