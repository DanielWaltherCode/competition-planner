<template>
  <div class="text-start list-group list-group-flush">
    <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenuButton1" data-bs-toggle="dropdown" aria-expanded="false">
      Skapa ny klass
    </button>
    <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton1">
      <li :key="name" v-for="name in categoryNames">
        <a @click="creatingNewClass(name)" class="dropdown-item" href="#">{{ name }}</a>
      </li>
    </ul>
    <span :key="category.id" v-for="category in categories"
      :class="{active: category.id === activeCategory.id}">
        <a @click="selectingAClass(category.id)"
           href="#" :class="{active: category.id === activeCategory.id}"
           class="list-group-item list-group-item-action">{{category.name}}</a>
      </span>
    </div>
</template>

<script>
export default {
  name: "CategoryList",
  props: {
    categories: Array,
    activeCategory: Object,
    categoryNames: Array
  },
  methods: {
    selectingAClass : function(categoryId){
      this.$emit('selectedClass', categoryId)
    },
    creatingNewClass : function(categoryId){
      this.$emit('createdClass', categoryId)
    }
  }
}
</script>

<style scoped>

.btn {
  background-color: #074EE8
}

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