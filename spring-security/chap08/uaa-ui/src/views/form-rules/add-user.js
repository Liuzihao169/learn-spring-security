import VALIDATOR from "@/validator";

export const addUserRules = () => {
  return {
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
  };
};
