<template>
    <div class="text-start list-group list-group-flush">
      <span :key="category.id" v-for="category in categories"
            :class="{active: category.id === activeCategoryId}">
        <a @click="selectingAClass(category.id)"
           href="#" :class="{active: category.id === activeCategoryId}"
           class="list-group-item list-group-item-action">{{category.name}}</a>
      </span>
    </div>
</template>

<script>
export default {
  name: "CategoryList",
  props: {
    categories: Array,
  },
  data: function(){
    return {
      activeCategoryId: null
    }
  },
  methods: {
    selectingAClass : function(categoryId){
      this.activeCategoryId = categoryId
      this.$emit('selectedClass', categoryId)
    }
  }
}
</script>

<style scoped>

.list-group-item.active {
  background-color: #E0EAFF;
  color: #074EE8;
  border: none;
}

.list-group-item.active:hover {
  background-color: #E0EAFF;
  color: #0842C0;
}

.list-group-item {
  border: none;
  background-color: transparent;
}

.list-group-item:hover{
  background-color: #F4F8FF;
}

span.active {
  border-left: 1rem solid #074EE8;
}

span.active:hover {
  border-left: 1rem solid #0842C0;
}

span:hover {
  border-left: 1rem solid #8AACF4;
}

span {
  border-left: 1rem solid #ffffff;
}
</style>