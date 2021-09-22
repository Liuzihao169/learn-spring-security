<template>
  <a-menu mode="inline" v-model="selectedKeys" :openKeys="openKeys" style="height: 100%">
    <a-sub-menu
      v-for="(item, index) in sideMenus"
      :key="`${index}`"
      @titleClick="handleParentClick($event)"
    >
      <span slot="title">
        <a-icon :type="item.icon" />
        {{ item.title }}
      </span>
      <a-menu-item
        v-for="(child, childIndex) in item.children"
        :key="`${index}-${childIndex}`"
        @click="handleNav(item.path, child.path)"
      >{{ child.title }}</a-menu-item>
    </a-sub-menu>
  </a-menu>
</template>

<script>
import SIDE_MENU from "@/core/data/side-menu.data.json";
export default {
  data() {
    return {
      sideMenus: SIDE_MENU,
      selectedKeys: ["0-0"],
      openKeys: ["0"]
    };
  },
  methods: {
    handleParentClick({ key }) {
      const idx = this.openKeys.indexOf(key);
      if (idx > -1) {
        this.openKeys.splice(idx, 1);
        return;
      }
      this.openKeys.push(key);
    },
    handleNav(parentPath, childPath) {
      const path = parentPath ? `${parentPath}${childPath}` : childPath;
      if (path === this.$router.currentRoute.path) {
        return;
      }
      this.$router.push(path);
    }
  },
  mounted() {
    // 根据路由处理菜单的打开以及子菜单的选中
    this.sideMenus.forEach((parent, parentIndex) => {
      const parentPath = parent.path;
      parent.children.forEach((child, childIndex) => {
        const childPath = child.path;
        const path = parentPath ? `${parentPath}${childPath}` : childPath;
        if (this.$router.currentRoute.path.includes(path)) {
          this.selectedKeys = [`${parentIndex}-${childIndex}`];
          this.openKeys = [`${parentIndex}`];
          return;
        }
      });
    });
    if (this.selectedKeys.length === 0) {
      this.selectedKeys = ["0-0"];
    }
    if (this.openKeys.length === 0) {
      this.openKeys = ["0"];
    }
  }
};
</script>
