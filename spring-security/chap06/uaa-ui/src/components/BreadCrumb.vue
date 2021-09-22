<template>
  <a-breadcrumb style="margin: 16px 0">
    <a-breadcrumb-item
      v-for="(item, index) in $route.meta.breadcrumb"
      :key="index"
    >
      <router-link :to="item.to">{{ item.label }}</router-link>
    </a-breadcrumb-item>
  </a-breadcrumb>
</template>

<script>
export default {};
</script>
