<template>
  <div class="container-fluid">
    <div class="row">
      <div class="sidebar col-md-3">
        <div class="sidebar-header">
          <h4> {{ $t("competition.categories.header") }}</h4>
        </div>
        <ul class="list-group list-group-flush">
          <li class="list-group-item" v-for="category in categories"
              @click="chooseCategory(category.id)" :key="category.id"
              :class="chosenCategoryId === category.id ? 'active' : ''">
            {{ category.name }}
          </li>
        </ul>
      </div>
      <competition-draw v-if="chosenCategoryId !== 0" :category-id="chosenCategoryId" :key="chosenCategoryId" class="col-md-9"></competition-draw>
    </div>
  </div>
</template>

<script>

import ApiService from "../../api-services/api.service";
import CompetitionDraw from "./CompetitionDraw";

export default {
  name: "CompetitionCategories",
  components: {CompetitionDraw},
  data() {
    return {
      chosenCategoryId: 0,
      categories: []
    }
  },
  mounted() {
    const competitionId = this.$route.params.competitionId
    ApiService.getCompetitionCategories(competitionId).then(res => {
      this.categories = res.data.categories
      if(this.categories.length > 0) {
        this.chooseCategory(this.categories[0].id)
      }
    })
  },
  methods: {
    chooseCategory(categoryId) {
      this.chosenCategoryId = categoryId
    }
  }
}
</script>

<style scoped>
.search-result:hover {
  cursor: pointer;
  opacity: 0.7;
}
</style>