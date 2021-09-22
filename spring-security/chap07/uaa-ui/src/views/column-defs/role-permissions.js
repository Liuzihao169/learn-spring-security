export const rolePermissionsColumns = () => [
  {
    title: "权限标识",
    dataIndex: "authority",
    key: "authority",
    scopedSlots: {
      filterDropdown: "filterDropdown",
      filterIcon: "filterIcon",
    },
  },
  {
    title: "权限名称",
    dataIndex: "displayName",
    key: "displayName",
    scopedSlots: {
      filterDropdown: "filterDropdown",
      filterIcon: "filterIcon",
    },
  },
];
