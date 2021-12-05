<template>
  <div class="container-fluid">
    <div class="row">
      <div class="sidebar col-md-3">
        <div class="sidebar-header">
          <h4> {{ $t("competition.categories.header") }}</h4>
        </div>
        <ul class="list-group">
          <li class="list-group-item" v-for="competitionCategory in categories"
              @click="chosenCategoryId = competitionCategory.id" :key="competitionCategory.id"
              :class="chosenCategoryId === competitionCategory.id ? 'active' : ''">
            {{ competitionCategory.category.name }}
          </li>
        </ul>
      </div>
      <competition-draw v-if="chosenCategoryId !== 0" :category-id="chosenCategoryId" class="col-md-9"></competition-draw>
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
      console.log(res.data)
      this.categories = res.data
      if(this.categories.length > 0) {
        this.chosenCategoryId = this.categories[0].id
      }
    })
  },
}
</script>

<style scoped>
.search-result:hover {
  cursor: pointer;
  opacity: 0.7;
}
</style>