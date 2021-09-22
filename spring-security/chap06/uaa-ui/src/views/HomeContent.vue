<template>
  <a-layout style="padding: 24px 0; background: #fff">
    <a-layout-sider width="200" style="background: #fff">
      <SideMenu />
    </a-layout-sider>
    <a-layout-content :style="{ padding: '0 24px', minHeight: '280px' }">
      <router-view></router-view>
    </a-layout-content>
  </a-layout>
</template>

<script>
import SideMenu from "@/components/SideMenu.vue";
export default {
  name: "HomeContent",
  components: {
    SideMenu,
  },
};
</script>

<style></style>
