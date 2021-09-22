<template>
  <a-row type="flex" justify="center" align="middle" style="min-height: 100vh">
    <a-col span="16">
      <!-- 错误信息显示 -->
      <div v-if="registerError">
        <a-alert
          message="注册失败"
          v-bind:description="registerError"
          type="error"
        />
      </div>
      <!-- 表单 -->
      <a-form-model ref="regForm" :model="model" :rules="rules" v-bind="layout">
        <!-- 用户名 -->
        <a-form-model-item has-feedback prop="username">
          <!-- 自定义 Label -->
          <span slot="label">
            用户名&nbsp;
            <a-tooltip title="用于登录，只允许英文字母和数字组合">
              <a-icon type="question-circle-o" />
            </a-tooltip>
          </span>
          <a-input v-model="model.username" type="text" autocomplete="off" />
        </a-form-model-item>
        <!-- 密码 -->
        <a-form-model-item prop="password" label="密码">
          <a-input
            v-model="model.password"
            :type="passwordVisible ? 'text' : 'password'"
            autocomplete="off"
          >
            <!-- 显示/隐藏密码图标 -->
            <a-icon
              :type="passwordVisible ? 'eye-invisible' : 'eye'"
              slot="addonAfter"
              @click="passwordVisible = !passwordVisible"
            />
          </a-input>
        </a-form-model-item>
        <!-- 确认密码 -->
        <a-form-model-item prop="matchingPassword" label="确认密码">
          <a-input
            v-model="model.matchingPassword"
            :type="matchingPasswordVisible ? 'text' : 'password'"
            autocomplete="off"
          >
            <a-icon
              :type="matchingPasswordVisible ? 'eye-invisible' : 'eye'"
              slot="addonAfter"
              @click="matchingPasswordVisible = !matchingPasswordVisible"
            />
          </a-input>
        </a-form-model-item>
        <!-- 姓名 -->
        <a-form-model-item has-feedback prop="name" label="姓名">
          <a-input v-model="model.name" type="text" autocomplete="off" />
        </a-form-model-item>
        <!-- 电子邮件 -->
        <a-form-model-item has-feedback prop="email" label="电子邮件">
          <a-input v-model="model.email" type="text" autocomplete="off" />
        </a-form-model-item>
        <!-- 手机号 -->
        <a-form-model-item has-feedback prop="mobile" label="手机">
          <a-input v-model="model.mobile" type="text" autocomplete="off">
            <a-select slot="addonBefore" defaultValue="86" style="width: 70px">
              <a-select-option value="86">+86</a-select-option>
              <a-select-option value="1">+1</a-select-option>
            </a-select>
          </a-input>
        </a-form-model-item>
        <!-- 用户注册协议 -->
        <a-form-model-item prop="agreement" v-bind="tailFormItemLayout">
          <a-checkbox v-model="model.agreement" :checked="model.agreement">
            我已经阅读了
            <a href>用户注册协议</a>
          </a-checkbox>
        </a-form-model-item>
        <!-- 按钮区域 -->
        <a-form-model-item v-bind="tailFormItemLayout">
          <a-button type="primary" @click="submitForm('regForm')"
            >注册</a-button
          >
          <a-button type="link" @click="handleNavLogin"
            >已经有账户，去登录</a-button
          >
        </a-form-model-item>
      </a-form-model>
    </a-col>
  </a-row>
</template>

<script>
import { registerRules } from "../form-rules/register";
export default {
  data() {
    return {
      passwordVisible: false,
      matchingPasswordVisible: false,
      layout: {
        labelCol: { span: 4 },
        wrapperCol: { span: 12 },
      },
      tailFormItemLayout: {
        wrapperCol: {
          xs: {
            span: 24,
            offset: 0,
          },
          sm: {
            span: 16,
            offset: 8,
          },
        },
      },
      model: {
        username: "",
        password: "",
        matchingPassword: "",
        name: "",
        mobile: "",
        email: "",
        agreement: false,
      },
      rules: {},
    };
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$store.dispatch("registerModule/register", this.model);
          return true;
        } else {
          return false;
        }
      });
    },
    handleNavLogin() {
      this.$router.push("/login");
    },
  },
  computed: {
    registerError: function() {
      return this.$store.state.register?.error;
    },
  },
  mounted() {
    // 在加载之后进行验证规则的更新，否则 model 是 undefined
    this.rules = registerRules(this.model, this.$refs.regForm);
  },
};
</script>
