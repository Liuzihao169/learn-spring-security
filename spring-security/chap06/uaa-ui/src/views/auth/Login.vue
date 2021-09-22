<template>
  <a-row type="flex" justify="center" align="middle" style="min-height: 100vh">
    <a-col span="8">
      <div v-if="loginError">
        <a-alert style="width:100%" message="登录失败" v-bind:description="loginError" type="error" />
      </div>
      <a-form-model ref="form" :model="model" :rules="rules" v-bind="layout">
        <a-form-model-item has-feedback prop="username">
          <a-input v-model="model.username" placeholder="用户名">
            <a-icon slot="prefix" type="user" style="color:rgba(0,0,0,.25)" />
          </a-input>
        </a-form-model-item>
        <a-form-model-item has-feedback prop="password">
          <a-input v-model="model.password" type="password" placeholder="密码">
            <a-icon slot="prefix" type="lock" style="color:rgba(0,0,0,.25)" />
          </a-input>
        </a-form-model-item>
        <a-form-model-item has-feedback prop="icode">
          <a-input v-model="model.icode" placeholder="在慕课网课程首页可以找到 icode">
            <a-icon slot="prefix" type="lock" style="color:rgba(0,0,0,.25)" />
          </a-input>
        </a-form-model-item>
        <a-form-model-item v-bind="tailFormItemLayout">
          <a-button type="primary" @click="submitForm('form')">登录</a-button>
          <a-button type="link" htmlType="button" @click="handleNavRegister()">还没有注册？</a-button>
        </a-form-model-item>
      </a-form-model>
    </a-col>
  </a-row>
</template>

<script>
import { loginRules } from "../form-rules/login";
export default {
  data() {
    return {
      layout: {
        labelCol: { span: 0 },
        wrapperCol: { span: 24 },
      },
      tailFormItemLayout: {
        wrapperCol: {
          xs: {
            span: 24,
            offset: 0,
          },
          sm: {
            span: 24,
            offset: 8,
          },
        },
      },
      model: {
        username: "",
        password: "",
      },
      rules: loginRules,
    };
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$store.dispatch("login", {
            username: this.model.username,
            password: this.model.password,
            icode: this.model.icode,
          });
        }
      });
    },
    handleNavRegister() {
      this.$router.push("/register");
    },
  },
  // 计算型属性
  computed: {
    loginError: function () {
      return this.$store.state.loginErrMsg;
    },
  },
};
</script>
