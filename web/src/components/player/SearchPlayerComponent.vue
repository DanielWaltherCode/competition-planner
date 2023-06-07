<template>
  <div class="row">
    <div class="col-sm-8">
      <autocomplete id="autocomplete-field" ref="autocomplete"
                    class="me-2"
                    :search="searchPlayers"
                    auto-select
                    :get-result-value="getSearchResult"
                    :placeholder="$t('player.add.search')"
                    @submit="handleSubmit">
      </autocomplete>
    </div>
    <div class="col-sm-2">
      <button class="btn btn-primary p-3" type="button" @click="clearPlayer"> {{
          $t("player.add.clear")
        }}
      </button>
    </div>
    <div class="row">
      <form id="input-form" class="rounded row p-3">
        <div class="col-md-11">
          <div class="row mx-auto">
            <div class="col-sm-6 mb-3">
              <label for="firstName" class="text-start form-label"> {{ $t("player.add.firstName") }}</label>
              <input v-if="player !== null" type="text" disabled class="form-control"
                     id="firstName" v-model="player.firstName">
              <input v-else type="text" disabled class="form-control">
            </div>

            <div class="col-sm-6 mb-3">
              <label for="lastName" class="text-start form-label"> {{ $t("player.add.lastName") }}</label>
              <input v-if="player !== null" type="text" disabled class="form-control"
                     v-model="player.lastName"
                     id="lastName">
              <input v-else type="text" disabled class="form-control">
            </div>

            <div class="col-sm-6 mb-3">
              <!-- Standard validation does not work on select menues -->
              <label for="club-select" class="form-label"> {{ $t("player.add.club") }}</label>
              <input v-if="player !== null" type="text" id="club-select" disabled class="form-control" v-model="player.club.name">
              <input v-else type="text" disabled class="form-control">
            </div>

            <div class="col-sm-6 mb-3">
              <label for="dateOfBirth" class="text-start form-label"> {{ $t("player.add.dateOfBirth") }}</label>
              <input v-if="player !== null" disabled class="form-control"
                     :placeholder="getFormattedDate(player.dateOfBirth)"
                     id="dateOfBirth">
              <input v-else type="text" disabled class="form-control">
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
  props: ["withCompetition"],
  data() {
    return {
      playerNotFound: false,
      player: null
    }
  },
  computed: {
    competition: function () {
      return this.$store.getters.competition
    }
  },
  methods: {
    searchPlayers(input) {
      if (input.length < 1) {
        return [];
      }
      if (this.withCompetition) {
        return new Promise(resolve => {
          PlayerService.searchAllPlayersWithCompetition(this.competition.id, input).then(res => {
            this.playerNotFound = false
            resolve(res.data)
          }).catch(() => {
            this.playerNotFound = true;
          })
        })
      }
      else {
        return new Promise(resolve => {
          PlayerService.searchPlayers(input).then(res => {
            this.playerNotFound = false
            resolve(res.data)
          }).catch(() => {
            this.playerNotFound = true;
          })
        })
      }

    },
    getSearchResult(searchResult) {
      return searchResult.firstName + " " + searchResult.lastName + " " + searchResult.club.name
    },
    clearPlayer() {
      this.player = null
      this.$refs.autocomplete.value = ""
      this.$emit("clear-player")
    },
    handleSubmit(result) {
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