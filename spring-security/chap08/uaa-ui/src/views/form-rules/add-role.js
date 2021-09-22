import VALIDATOR from "@/validator";

export const addRoleRules = () => {
  return {
    roleName: [
      {
        required: true,
        message: "角色标识为必填项！",
        trigger: "blur",
      },
      {
        pattern: VALIDATOR.capitalObjectNamePattern,
        message: "角色标识仅能使用大写英文字母以及下划线，长度在3-50个字符",
        trigger: "blur",
      },
      {
        asyncValidator: VALIDATOR.checkRoleName,
        trigger: "blur",
      },
    ],
    name: [
      {
        required: true,
        message: "角色名称为必填项",
        trigger: "blur",
      },
      {
        pattern: VALIDATOR.humanNamePattern,
        message:
          "中文角色名称长度为2-20个字符，不能有空格。英文角色名称在2-50个字符，可以有空格和小数点",
        trigger: "blur",
      },
    ],
  };
};
