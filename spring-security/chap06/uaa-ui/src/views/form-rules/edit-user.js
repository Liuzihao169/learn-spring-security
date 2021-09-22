import ADMIN_API from "@/services/admin.service";
const checkEmail = (username) => {
  return (rule, value, callback) => {
    if (value === "") {
      callback();
    }
    ADMIN_API.checkEmail(value, username)
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
};
const checkMobile = (username) => {
  return (rule, value, callback) => {
    if (value === "") {
      callback();
    }
    ADMIN_API.checkMobile(value, username)
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
};
export const editUserRules = (model) => {
  return {
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
        asyncValidator: checkMobile(model.username),
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
        asyncValidator: checkEmail(model.username),
        trigger: "blur",
      },
    ],
  };
};
