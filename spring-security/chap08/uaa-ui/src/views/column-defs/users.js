export const usersColumns = () => [
  {
    title: "Id",
    dataIndex: "id",
    width: 80,
  },
  {
    title: "用户名",
    dataIndex: "username",
    scopedSlots: {
      filterDropdown: "filterDropdown",
      filterIcon: "filterIcon",
    },
    sorter: true,
  },
  {
    title: "姓名",
    dataIndex: "name",
    scopedSlots: {
      filterDropdown: "filterDropdown",
      filterIcon: "filterIcon",
    },
    sorter: true,
  },
  {
    title: "手机号",
    dataIndex: "mobile",
    scopedSlots: {
      filterDropdown: "filterDropdown",
      filterIcon: "filterIcon",
    },
    sorter: true,
  },
  {
    title: "电子邮件",
    dataIndex: "email",
    scopedSlots: {
      filterDropdown: "filterDropdown",
      filterIcon: "filterIcon",
    },
    sorter: true,
  },
  {
    title: "角色",
    dataIndex: "roles",
    scopedSlots: { customRender: "roles" },
  },
  {
    title: "激活",
    dataIndex: "enabled",
    scopedSlots: { customRender: "enabled" },
    align: "center",
    filters: [
      { text: "启用", value: "true" },
      { text: "禁用", value: "false" },
    ],
    width: 80,
  },
  {
    title: "操作",
    dataIndex: "operation",
    scopedSlots: { customRender: "operation" },
    align: "center",
  },
];
