<template>
  <div>
    <div class="logo" />
    <a-menu
      theme="dark"
      mode="horizontal"
      :default-selected-keys="['1']"
      :style="{ lineHeight: '64px' }"
    >
      <a-menu-item v-for="(item, index) in nvaMenus" :key="index">
        <router-link :to="item.path">{{ item.title }}</router-link>
      </a-menu-item>
      <a-dropdown style="float: right" :trigger="['click']">
        <a class="ant-dropdown-link" @click="(e) => e.preventDefault()">
          <a-icon type="user" />
          <a-icon type="down" />
        </a>
        <a-menu slot="overlay">
          <a-menu-item v-for="(item, index) in userMenus" :key="index">
            <a
              v-if="item.type === 'link'"
              :target="item.target"
              rel="noopener noreferrer"
              :href="item.path"
              >{{ item.title }}</a
            >
            <router-link v-else :to="item.path">{{ item.title }}</router-link>
          </a-menu-item>

          <a-menu-divider />
          <a-menu-item :key="userMenus.length" @click="logout()"
            >退出登录</a-menu-item
          >
        </a-menu>
      </a-dropdown>
    </a-menu>
  </div>
</template>

<script>
import USER_DROP_DOWN from "@/core/data/user-dropdown.data.json";
import NAV_MENU from "@/core/data/nav-menu.data.json";
export default {
  data() {
    return {
      userMenus: USER_DROP_DOWN,
      nvaMenus: NAV_MENU,
    };
  },
  methods: {
    logout() {
      this.$store.dispatch("reset");
    },
  },
  computed: {
    serverAddr: () => process.env.VUE_APP_API_URL,
  },
};
</script>
