<template>
    <div class="list-group list-group-flush">
      <a :key="category.id" v-for="category in categories"
         @click="something(category)"
         href="#" class="list-group-item list-group-item-action"
         data-bs-toggle="listSomething">{{category.name}}</a>
    </div>
</template>

<script>
export default {
  name: "CategoryList",
  props: {
    categories: Array,
  },
  methods: {
    something : function(category) {
      console.log(category.name)
    }
  }
}
</script>

<style scoped>
.list-group {
  padding-top: 120px;
  text-align: left;
}
</style>