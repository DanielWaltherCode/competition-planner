<template>
  <div v-if="category.settings != null" class="p-2 p-md-3">
    <p v-if="isDrawMade" class="fs-4 fw-bolder"> {{$t("categories.cannotUpdate")}}</p>
    <div class="row">
      <fieldset class="col mb-3">
        <label class="h5 form-label">{{ $t("categorySettings.drawType") }}</label>
        <div class="col"></div>
        <div class="form-check form-check-inline" v-for="drawType in possibleDrawOptions.drawTypes" :key="drawType">
          <input v-model="category.settings.drawType" :value="drawType" class="form-check-input" type="radio"
                 name="inlineRadioOptions" id="inputDrawTypePool" :disabled="isDrawMade">
          <label class="form-check-label" for="inputDrawTypePool">
            {{ $t("categorySettings.drawTypes." + drawType) }}</label>
        </div>
      </fieldset>
    </div>
    <div class="row my-5">
      <div class="col-sm-12 col-md-6 mb-3">
        <label class="h5 form-label" for="inputCost">{{ $t("categorySettings.cost") }}</label>
        <input v-model="category.settings.cost" type="text" class="form-control" id="inputCost" :disabled="isDrawMade">
      </div>
      <div class="col-sm-12 col-md-6 mb-3" v-if="category.settings.drawType !== 'CUP_ONLY'">
        <label class="h5 form-label"
               for="inputNumberOfPlayersPerPool">{{ $t("categorySettings.playersPerGroup") }}</label>
        <select v-model="category.settings.playersPerGroup" id="inputNumberOfPlayersPerPool" class="form-select" :disabled="isDrawMade">
          <option v-for="i in 3" :key="i">
            {{ i + 2 }}
          </option>
        </select>
      </div>
    </div>
    <div v-if="category.settings.drawType === 'POOL_AND_CUP'" class="row">
      <div class="col-sm-12 col-md-6 d-flex flex-column justify-content-end">
        <label class="h5 form-label" for="inputPlayersThatAdvance">{{ $t("categorySettings.playersToPlayoff") }}</label>
        <input v-model="category.settings.playersToPlayOff" type="text" class="form-control" id="inputPlayersThatAdvance" :disabled="isDrawMade">
      </div>
      <div class="col-sm-12 col-md-6 d-flex flex-column justify-content-end">
        <label class="h5 form-label" for="inputDrawStrategy">{{ $t("categorySettings.drawStrategy") }}</label>
        <select id="inputDrawStrategy" class="form-select" :disabled="isDrawMade">
          <option v-for="drawStrategy in possibleDrawOptions.drawStrategies" :value="drawStrategy" :key="drawStrategy">
            {{$t("categorySettings.drawStrategies." + drawStrategy)}}
          </option>
        </select>
      </div>
    </div>
  </div>
</template>

<script>
import CategoryService from "@/common/api-services/category.service";

export default {
  name: "CategoryGeneralSettings",
  props: {
    category: Object,
    isDrawMade: Boolean
  },
  data() {
    return {
      possibleDrawOptions: []
    }
  },
  computed: {
    competition: function () {
      return this.$store.getters.competition
    }
  },
  mounted() {
    CategoryService.getPossibleMetaDataValues(this.competition.id).then(res => {
      this.possibleDrawOptions = res.data
    })
  },
  methods: {

  }
}
</script>

<style scoped>
@media (max-width: 767px) {
  .form-check-inline {
    display: block;
  }
}
</style>