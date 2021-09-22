import VALIDATOR from "@/validator";

export const editUserRules = (model) => {
  return {
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
        asyncValidator: VALIDATOR.checkMobileNotSelf(model.username),
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
        asyncValidator: VALIDATOR.checkEmailNotSelf(model.username),
        trigger: "blur",
      },
    ],
  };
};
