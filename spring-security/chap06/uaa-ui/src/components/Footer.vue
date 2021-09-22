<template>
  <div>©{{ year }} {{ copyright }}</div>
</template>

<script>
export default {
  computed: {
    year: () => {
      return new Date().getFullYear();
    },
    copyright: () => {
      return process.env.VUE_APP_COPY_RIGHT;
    },
  },
};
</script>
