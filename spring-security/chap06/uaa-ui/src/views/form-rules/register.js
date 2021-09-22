import AUTH_API from "@/services/auth.service";
const checkUsername = (rule, value, callback) => {
  if (value === "") {
    callback();
  }
  AUTH_API.checkUsername(value)
    .then((res) => {
      if (res.data) {
        callback(new Error("用户名已存在！"));
      }
      callback();
    })
    .catch(() => {
      callback(new Error("服务器异常"));
    });
};
const checkEmail = (rule, value, callback) => {
  if (value === "") {
    callback();
  }
  AUTH_API.checkEmail(value)
    .then((res) => {
      if (res.data) {
        callback(new Error("电子邮件已存在！"));
      }
      callback();
    })
    .catch(() => {
      callback(new Error("服务器异常"));
    });
};
const checkMobile = (rule, value, callback) => {
  if (value === "") {
    callback();
  }
  AUTH_API.checkMobile(value)
    .then((res) => {
      if (res.data) {
        callback(new Error("手机号码已存在！"));
      }
      callback();
    })
    .catch(() => {
      callback(new Error("服务器异常"));
    });
};
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
      pattern: /^[a-zA-Z0-9_]{3,50}$/,
      message: "用户名仅能使用英文字母和数字以及下划线，长度在3-50个字符",
      trigger: "blur",
    },
    {
      asyncValidator: checkUsername,
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
      pattern: /^.*(?=.{8,})(?=.*\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*? ]).*$/,
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
      pattern: /^([\u4e00-\u9fa5]{2,20}|[a-zA-Z.\s]{2,50})$/,
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
      pattern: /^1[3-9]\d{9}$/,
      message: "手机号不合法",
      trigger: "blur",
    },
    {
      asyncValidator: checkMobile,
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
      pattern: /^\S+@\S+\.\S+$/,
      message: "电子邮件地址不合法！",
      trigger: "blur",
    },
    {
      asyncValidator: checkEmail,
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
