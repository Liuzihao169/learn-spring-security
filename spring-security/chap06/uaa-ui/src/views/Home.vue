<template>
  <div id="home">
    <a-layout id="layout-top-side">
      <a-layout-header :style="{ width: '100%' }">
        <AppHeader />
      </a-layout-header>
      <a-layout-content style="padding: 0 50px">
        <BreadCrumb />
        <router-view></router-view>
      </a-layout-content>
      <a-layout-footer style="text-align: center">
        <AppFooter />
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script>
// @ 是一个指向 /src 的别名
import AppFooter from "@/components/Footer.vue";
import BreadCrumb from "@/components/BreadCrumb.vue";
import AppHeader from "@/components/Header.vue";

export default {
  name: "Home",
  components: {
    AppFooter,
    BreadCrumb,
    AppHeader,
  },
};
</script>

<style>
#home {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}
#layout-top-side .logo {
  width: 120px;
  height: 31px;
  background: rgba(255, 255, 255, 0.2);
  margin: 16px 28px 16px 0;
  float: left;
}
</style>
