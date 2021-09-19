<template>
  <div v-if="category.settings != null">
    <div class="row">
      <div class="col-sm-12 col-md-6 mb-3">
        <label class="h5 form-label" for="inputCost">{{ $t("categorySettings.cost") }}</label>
        <input v-model="category.settings.cost" type="text" class="form-control" id="inputCost">
      </div>
      <div class="col-sm-12 col-md-6 mb-3">
        <label class="h5 form-label"
               for="inputNumberOfPlayersPerPool">{{ $t("categorySettings.playersPerGroup") }}</label>
        <select v-model="category.settings.playersPerGroup" id="inputNumberOfPlayersPerPool" class="form-select" >
          <option v-for="i in 3" :key="i">
            {{ i + 2 }}
          </option>
        </select>
      </div>
    </div>
    <div class="row my-4">
      <fieldset class="col mb-3">
        <label class="h5 form-label">{{ $t("categorySettings.drawType") }}</label>
        <div class="col"></div>
        <div class="form-check form-check-inline" v-for="drawType in possibleDrawOptions.drawTypes" :key="drawType">
          <input v-model="category.settings.drawType" :value="drawType" class="form-check-input" type="radio"
                 name="inlineRadioOptions" id="inputDrawTypePool">
          <label class="form-check-label" for="inputDrawTypePool">
            {{ $t("categorySettings.drawTypes." + drawType) }}</label>
        </div>
      </fieldset>
    </div>
    <div v-if="category.settings.drawType === 'POOL_AND_CUP'" class="row">
      <div class="col-sm-12 col-md-6 d-flex flex-column justify-content-end">
        <label class="h5 form-label" for="inputPlayersThatAdvance">{{ $t("categorySettings.playersToPlayoff") }}</label>
        <input v-model="category.settings.playersToPlayOff" type="text" class="form-control" id="inputPlayersThatAdvance">
      </div>
      <div class="col-sm-12 col-md-6 d-flex flex-column justify-content-end">
        <label class="h5 form-label" for="inputDrawStrategy">{{ $t("categorySettings.drawStrategy") }}</label>
        <select id="inputDrawStrategy" class="form-select">
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