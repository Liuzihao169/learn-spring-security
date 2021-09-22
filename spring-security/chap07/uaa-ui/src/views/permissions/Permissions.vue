<template>
  <div>
    <div v-if="error">
      <a-alert style="width:100%" message="请求失败" v-bind:description="error" type="error" />
    </div>
    <a-table
      :rowKey="(item) => item.id"
      :columns="columns"
      :data-source="data"
      :scroll="{ y: 460 }"
    />
  </div>
</template>
<script>
import ADMIN_API from "@/services/admin.service";
import UTIL from "@/core/util";

const columns = [
  {
    title: "Id",
    dataIndex: "id",
    key: "id"
  },
  {
    title: "代码",
    dataIndex: "authority",
    key: "authority"
  },
  {
    title: "名称",
    dataIndex: "displayName",
    key: "displayName"
  }
];

export default {
  data() {
    return {
      data: [],
      columns,
      error: null
    };
  },
  mounted() {
    ADMIN_API.loadPermissions()
      .then(res => (this.data = res.data))
      .catch(
        err =>
          (this.error =
            UTIL.getErrorDetailFromResponse(err) || "加载权限列表错误")
      );
  }
};
</script>
