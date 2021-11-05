<template>
  <div class="row">
    <div class="d-flex p-3">
      <autocomplete class="col-sm-8 me-2" id="autocomplete-field"
                    ref="autocomplete"
                    :search="searchPlayers"
                    auto-select
                    :get-result-value="getSearchResult"
                    :placeholder="$t('player.add.search')"
                    @submit="handleSubmit">
      </autocomplete>
      <button class="btn btn-primary" type="button" @click="clearPlayer"> {{
          $t("player.add.clear")
        }}
      </button>
    </div>
    <div class="row">
      <form id="input-form" v-if="player !== null" class="rounded row p-3">
        <div class="col-md-11">
          <div class="row mx-auto">
            <div class="col-sm-6 mb-3">
              <label for="firstName" class="text-start form-label"> {{ $t("player.add.firstName") }}</label>
              <input type="text" disabled class="form-control"
                     id="firstName" v-model="player.firstName">
            </div>

            <div class="col-sm-6 mb-3">
              <label for="lastName" class="text-start form-label"> {{ $t("player.add.lastName") }}</label>
              <input type="text" disabled class="form-control"
                     v-model="player.lastName"
                     id="lastName">
            </div>

            <div class="col-sm-6 mb-3">
              <!-- Standard validation does not work on select menues -->
              <label for="club-select" class="form-label"> {{ $t("player.add.club") }}</label>
              <input type="text" disabled class="form-control" v-model="player.club.name">
            </div>

            <div class="col-sm-6 mb-3">
              <label for="dateOfBirth" class="text-start form-label"> {{ $t("player.add.dateOfBirth") }}</label>
              <input disabled class="form-control"
                     :placeholder="getFormattedDate(player.dateOfBirth)"
                     id="dateOfBirth">
            </div>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import Autocomplete from '@trevoreyre/autocomplete-vue'
import '@trevoreyre/autocomplete-vue/dist/style.css';
import PlayerService from "@/common/api-services/player.service";
import {getFormattedDate} from "@/common/util";
export default {
  name: "RegisterPlayerComponent",
  components: {Autocomplete},
  props: ["clubs"],
  data() {
    return {
      playerNotFound: false,
      player: null
    }
  },
  methods: {
    searchPlayers(input) {
      if (input.length < 1) {
        return [];
      }
      return new Promise(resolve => {
        PlayerService.searchAllPlayers(input).then(res => {
          this.playerNotFound = false
          resolve(res.data)
        }).catch(() => {
          this.playerNotFound = true;
        })
      })
    },
    getSearchResult(searchResult) {
      return searchResult.firstName + " " + searchResult.lastName
    },
    clearPlayer() {
      this.player = null
      this.selectedCategories = []
      this.$refs.autocomplete.value = ""
      this.$emit("clear-player")
    },
    handleSubmit(result) {
      console.log(result)
      if (result === undefined || result === "") {
        return;
      }
      this.player = result
      this.$emit("player-found", this.player)
    },
    getFormattedDate: getFormattedDate
  }
}
</script>

<style scoped>

</style>