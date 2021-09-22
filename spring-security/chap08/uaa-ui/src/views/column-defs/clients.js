export const columns = [
  {
    title: "客户端 Id",
    dataIndex: "client_id",
    key: "client_id",
  },
  {
    title: "客户端 Secret",
    dataIndex: "client_secret",
    key: "client_secret",
  },
  {
    title: "授权类型",
    dataIndex: "authorized_grant_types",
    key: "authorized_grant_types",
    scopedSlots: { customRender: "authorized_grant_types" },
  },
  {
    title: "重定向 URI",
    dataIndex: "redirect_uri",
    key: "redirect_uri",
    scopedSlots: { customRender: "redirect_uri" },
  },
  {
    title: "访问令牌有效期",
    dataIndex: "access_token_validity",
    key: "access_token_validity",
  },
  {
    title: "刷新令牌有效期",
    dataIndex: "refresh_token_validity",
    key: "refresh_token_validity",
  },
  {
    title: "授权范围",
    dataIndex: "scope",
    key: "scope",
    scopedSlots: { customRender: "scope" },
  },
  {
    title: "操作",
    dataIndex: "operation",
    scopedSlots: { customRender: "operation" },
    align: "center",
  },
];
