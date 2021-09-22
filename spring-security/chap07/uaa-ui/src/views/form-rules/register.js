import VALIDATOR from "@/validator";

const validatePass = (model, form) => {
  return (rule, value, callback) => {
    if (model.matchingPassword !== "") {
      form.validateField("matchingPassword");
    }
    callback();
  };
};
const validateMatchingPass = (model) => {
  return (rule, value, callback) => {
    if (value !== "" && value !== model.password) {
      callback(new Error("密码和确认密码不一致！"));
    } else {
      callback();
    }
  };
};
export const registerRules = (model, form) => ({
  username: [
    {
      required: true,
      message: "用户名为必填项！",
      trigger: "blur",
    },
    {
      pattern: VALIDATOR.objectNamePattern,
      message: "用户名仅能使用英文字母和数字以及下划线，长度在3-50个字符",
      trigger: "blur",
    },
    {
      asyncValidator: VALIDATOR.checkUsername,
      trigger: "blur",
    },
  ],
  password: [
    {
      required: true,
      message: "密码为必填项！",
      trigger: "blur",
    },
    {
      pattern: VALIDATOR.passwordPattern,
      message:
        "密码最少8位，包括至少1个大写字母，1个小写字母，1个数字，1个特殊字符！",
      trigger: "blur",
    },
    {
      validator: validatePass(model, form),
      trigger: "blur",
    },
  ],
  matchingPassword: [
    {
      required: true,
      message: "确认密码为必填项！",
      trigger: "blur",
    },
    {
      validator: validateMatchingPass(model),
      trigger: "blur",
    },
  ],
  name: [
    {
      required: true,
      message: "姓名为必填项",
      trigger: "blur",
    },
    {
      pattern: VALIDATOR.humanNamePattern,
      message:
        "中文姓名长度为2-20个字符，不能有空格。英文姓名在2-50个字符，可以有空格和小数点",
      trigger: "blur",
    },
  ],
  mobile: [
    {
      required: true,
      message: "手机号为必填项！",
      trigger: "blur",
    },
    {
      pattern: VALIDATOR.mobilePattern,
      message: "手机号不合法",
      trigger: "blur",
    },
    {
      asyncValidator: VALIDATOR.checkMobile,
      trigger: "blur",
    },
  ],
  email: [
    {
      required: true,
      message: "电子邮件为必填项！",
      trigger: "blur",
    },
    {
      pattern: VALIDATOR.emailPattern,
      message: "电子邮件地址不合法！",
      trigger: "blur",
    },
    {
      asyncValidator: VALIDATOR.checkEmail,
      trigger: "blur",
    },
  ],
  agreement: [
    {
      type: "enum",
      enum: [true],
      message: "注册前请阅读用户注册协议，并勾选同意",
      trigger: "change",
    },
  ],
});
