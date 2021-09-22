export const addClientRules = () => {
  return {
    clientId: [
      {
        required: true,
        message: "客户端 Id 为必填项！",
        trigger: "blur",
      },
    ],
    clientSecret: [
      {
        required: true,
        message: "客户端 Secret 为必填项",
        trigger: "blur",
      },
    ],
    grantTypes: [
      {
        required: true,
        message: "至少选择一个授权类型",
        trigger: "blur",
      },
    ],
  };
};
