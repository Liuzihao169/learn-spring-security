<template>
  <div style="padding: 8px">
    <a-input
      auto-focus
      v-ant-ref="(c) => (searchInput = c)"
      :placeholder="`搜索 ${column.title}`"
      :value="selectedKeys[0]"
      @change="(e) => setSelectedKeys(e.target.value ? [e.target.value] : [])"
      style="width: 188px; margin-bottom: 8px; display: block;"
    />
    <a-button
      type="primary"
      icon="search"
      size="small"
      style="width: 90px; margin-right: 8px"
      @click="() => confirm()"
    >
      搜索
    </a-button>
    <a-button size="small" style="width: 90px" @click="() => clearFilters()">
      清除
    </a-button>
  </div>
</template>

<script>
export const makeFocus = (searchInput) => {
  return (visible) => {
    if (visible) {
      setTimeout(() => {
        searchInput.focus();
      }, 0);
    }
  };
};
export default {
  props: {
    setSelectedKeys: Function,
    selectedKeys: Array,
    confirm: Function,
    clearFilters: Function,
    column: Object,
  },
  data() {
    return {
      searchInput: null,
    };
  },
  methods: {},
  mounted() {
    this.$nextTick(function() {
      this.column.onFilterDropdownVisibleChange = makeFocus(this.searchInput);
    });
  },
};
</script>
