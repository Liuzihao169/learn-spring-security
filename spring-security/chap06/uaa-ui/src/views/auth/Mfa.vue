<template>
  <a-row type="flex" justify="center" align="middle" style="min-height: 100vh">
    <a-col span="12">
      <div v-if="loginError">
        <a-alert
          style="width:100%"
          message="二次认证失败"
          v-bind:description="loginError"
          type="error"
        />
      </div>
      <a-form-model ref="mfaForm" :model="model" :rules="rules" v-bind="layout">
        <a-form-model-item label="发送方式" prop="mfaType">
          <a-radio-group v-model="model.mfaType">
            <a-radio value="0">短信</a-radio>
            <a-radio value="1">电子邮件</a-radio>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="验证码" has-feedback prop="code">
          <a-input v-model="model.code" type="text" autocomplete="off">
            <a-button
              type="link"
              slot="addonAfter"
              :disabled="!canCountDownClick"
              @click="resend"
              >{{ countDownLabel }}</a-button
            >
          </a-input>
        </a-form-model-item>
        <a-form-model-item v-bind="tailFormItemLayout">
          <a-button type="primary" @click="submitForm('mfaForm')"
            >验证并登录</a-button
          >
        </a-form-model-item>
      </a-form-model>
    </a-col>
  </a-row>
</template>

<script>
export default {
  data() {
    return {
      countDownLabel: "发送验证码",
      countDownTime: 60,
      canCountDownClick: true,
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
            offset: 4,
          },
        },
      },
      model: {
        mfaType: "0",
        code: "",
      },
      rules: {
        code: [
          {
            required: true,
            message: "请填写收到的验证码",
            trigger: "blur",
          },
        ],
      },
    };
  },
  computed: {
    loginError: function() {
      return this.$store.state.loginErrMsg;
    },
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (!valid) {
          return false;
        }
        this.$store.dispatch("verifyMfa", {
          mfaId: this.$store.getters.mfaId,
          code: this.model.code,
        });
      });
    },
    resend() {
      if (!this.canCountDownClick) return;
      this.$store.dispatch("sendMfa", this.model.mfaType);
      this.canCountDownClick = false;
      this.countDownLabel = this.countDownTime + "秒后重新发送";
      let clock = window.setInterval(() => {
        this.countDownTime--;
        this.countDownLabel = this.countDownTime + "秒后重新发送";
        if (this.countDownTime < 0) {
          window.clearInterval(clock);
          this.countDownLabel = "重新发送验证码";
          this.countDownTime = 10;
          this.canCountDownClick = true;
        }
      }, 1000);
    },
  },
};
</script>

<style></style>
