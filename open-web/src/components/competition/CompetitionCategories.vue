<template>
<div>
  <ul class="list-group list-group-flush w-25 m-4">
    <li class="list-group-item text-start" v-for="category in categories" :key="category.competitionCategoryId">
      <h5 class="mb-1 search-result" v-on:click="chooseCategory(category.competitionCategoryId)">
        {{category.categoryName}}
      </h5>
    </li>
  </ul>
</div>
</template>

<script>

import ApiService from "../../api-services/api.service";

export default {
name: "CompetitionCategories",
data() {
  return {
    categories: []
  }
},
  mounted() {
    const competitionId = this.$route.params.competitionId
    ApiService.getCompetitionCategories(competitionId).then(res => {
      this.categories = res.data.categories
    })
  },
  methods: {
    chooseCategory(categoryId) {
      this.$router.push(`/competition/${this.$route.params.competitionId}/draw/${categoryId}`)
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