export const userRolesColumns = () => [
  {
    title: "角色标识",
    dataIndex: "roleName",
    key: "roleName",
    scopedSlots: {
      filterDropdown: "filterDropdown",
      filterIcon: "filterIcon",
    },
  },
  {
    title: "角色名称",
    dataIndex: "displayName",
    key: "displayName",
    scopedSlots: {
      filterDropdown: "filterDropdown",
      filterIcon: "filterIcon",
    },
  },
];
