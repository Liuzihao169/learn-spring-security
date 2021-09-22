export const rolesColumns = () => [
  {
    title: "Id",
    dataIndex: "id",
  },
  {
    title: "角色标识",
    dataIndex: "roleName",
    scopedSlots: {
      filterDropdown: "filterDropdown",
      filterIcon: "filterIcon",
    },
    sorter: true,
  },
  {
    title: "角色名称",
    dataIndex: "displayName",
    scopedSlots: {
      filterDropdown: "filterDropdown",
      filterIcon: "filterIcon",
    },
    sorter: true,
  },
  {
    title: "内建",
    dataIndex: "builtIn",
    scopedSlots: { customRender: "builtIn" },
    align: "center",
    filters: [
      { text: "是", value: "true" },
      { text: "否", value: "false" },
    ],
  },
  {
    title: "权限",
    dataIndex: "permissions",
    scopedSlots: { customRender: "permissions" },
  },
  {
    title: "操作",
    dataIndex: "operation",
    scopedSlots: { customRender: "operation" },
    align: "center",
  },
];
