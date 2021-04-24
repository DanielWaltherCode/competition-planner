<template>
  <div class="shadow-sm test">
    <div class="text-start list-group list-group-flush dropend">
      <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenuButton1" data-bs-toggle="dropdown"
              aria-expanded="false">
        Skapa ny klass
      </button>
      <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="dropdownMenuButton1">
        <li :key="name" v-for="name in categoryNames">
          <a @click="creatingNewClass(name)" class="dropdown-item" href="#">{{ name }}</a>
        </li>
      </ul>
      <span :key="category.id" v-for="category in categories"
            :class="{active: category.id === activeCategory.id}">
        <a @click="selectingAClass(category.id)"
           href="#" :class="{active: category.id === activeCategory.id}"
           class="list-group-item list-group-item-action">{{ category.name }}</a>
      </span>
    </div>
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
    selectingAClass: function (categoryId) {
      this.$emit('selectedClass', categoryId)
    },
    creatingNewClass: function (categoryId) {
      this.$emit('createdClass', categoryId)
    }
  }
}
</script>

<style scoped>

.btn {
  background-color: var(--clr-primary-500);
}

.test {
  min-height: 100vh;
}

.list-group-item.active {
  background-color: var(--clr-primary-100);
  color: var(--clr-primary-500);
  border: none;
}

.list-group-item.active:hover {
  background-color: var(--clr-primary-200);
  color: var(--clr-primary-600);
}

.list-group-item {
  border: none;
  background-color: white;
}

.list-group-item:hover {
  background-color: #F4F8FF;
}

span.active {
  border-left: 0.5rem solid var(--clr-primary-500);
}

span.active:hover {
  border-left: 0.5rem solid var(--clr-primary-600);
}

span:hover {
  border-left: 0.5rem solid var(--clr-primary-200);
}

span {
  border-left: 0.5rem solid white;
}
</style>