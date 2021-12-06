<template>
  <div>
    <div class="row p-4 d-flex align-items-center">
      <autocomplete class="col-sm-8 mx-auto" id="autocomplete-field"
                    ref="autocomplete"
                    :search="searchPlayers"
                    auto-select
                    :get-result-value="getSearchResult"
                    :placeholder="$t('player.search')"
                    @submit="handleSubmit">
      </autocomplete>
    </div>
    <p class="fs-6 text-danger" v-if="playerNotFound">{{ $t("player.notFound") }}</p>
    <div class="p-4 custom-card col-11 col-md-8 mx-auto" v-if="player !== null">
      <h2 class="black"> {{ player.firstName + " " + player.lastName }}</h2>
      <div class="p-4" v-for="registration in registrations" :key="registration.id">
        <div class="fw-bold pb-2">
          {{ registration.competitionCategory.name }}
        </div>
        <div v-if="registration.competitionCategory.type === 'DOUBLES'">
          {{ $t("player.playingWith") }}
          {{ registration.accompanyingPlayer.firstName + ' ' + registration.accompanyingPlayer.lastName }}
        </div>
        <match-list-component :matches="registration.matches"/>
      </div>
    </div>
  </div>
</template>

<script>
import Autocomplete from '@trevoreyre/autocomplete-vue'
import '@trevoreyre/autocomplete-vue/dist/style.css'
import ApiService from "../../api-services/api.service";
import MatchListComponent from "../MatchListComponent";

export default {
  name: "PlayerDetail",
  data() {
    return {
      player: null,
      playerId: null,
      playerNotFound: false,
      registrations: [],
      competitionId: null,
    }
  },
  components: {MatchListComponent, Autocomplete},
  mounted() {
    this.competitionId = this.$route.params.competitionId
    this.playerId = this.$route.params.playerId
    if (this.playerId !== '0') {
      this.getRegistrations(this.competitionId, this.playerId)
    }
  },
  methods: {
    getRegistrations(competitionId, playerId) {
      ApiService.getRegistrationsForPlayer(competitionId, playerId).then(res => {
        this.player = res.data.player
        this.registrations = res.data.registrations
      })
    },
    searchPlayers(input) {
      if (input.length < 1) {
        return [];
      }
      return new Promise(resolve => {
        ApiService.findByNameInCompetition(input, this.competitionId).then(res => {
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
    handleSubmit(result) {
      if (result === undefined || result === "") {
        return;
      }
      this.player = result
      this.$router.replace({params: {id: this.player.id}}).catch(() => {
      })
      this.getRegistrations(this.competitionId, this.player.id)
    },
  }
}
</script>

<style scoped>

</style>