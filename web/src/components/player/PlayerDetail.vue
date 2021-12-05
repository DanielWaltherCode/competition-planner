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
    <div class="p-4 custom-card" v-if="player !== null">
      <h2 class="black"> {{ player.firstName + " " + player.lastName }}</h2>
      <div class="p-4">
        <div class="p-4" v-for="registration in registrations" :key="registration.id">
          <div class="fw-bold pb-2">{{ registration.competitionCategory.name }}

          </div>
          <div v-if="registration.competitionCategory.type === 'DOUBLES'">
                <p>{{ $t("player.playingWith") }}
                  <span class="clickable" @click="getRegistrations(competition.id, registration.accompanyingPlayer.id)">{{ registration.accompanyingPlayer.firstName + ' ' + registration.accompanyingPlayer.lastName }}</span>
                </p>
              </div>
            <match-list-component :matches="registration.matches" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import RegistrationService from "@/common/api-services/registration.service";
import Autocomplete from '@trevoreyre/autocomplete-vue'
import '@trevoreyre/autocomplete-vue/dist/style.css'
import PlayerService from "@/common/api-services/player.service";
import MatchListComponent from "@/components/general/MatchListComponent";

export default {
  name: "PlayerDetail",
  data() {
    return {
      player: null,
      playerId: null,
      playerNotFound: false,
      registrations: []
    }
  },
  components: {MatchListComponent, Autocomplete},
  computed: {
    competition: function () {
      return this.$store.getters.competition
    }
  },
  mounted() {
    this.playerId = this.$route.params.id
    if (this.playerId !== '0') {
      this.getRegistrations(this.competition.id, this.playerId)
    }
  },
  methods: {
    getRegistrations(competitionId, playerId) {
      RegistrationService.getRegistrationsForPlayer(competitionId, playerId).then(res => {
        this.player = res.data.player
        this.registrations = res.data.registrations
      })
    },
    searchPlayers(input) {
      if (input.length < 1) {
        return [];
      }
      return new Promise(resolve => {
        PlayerService.findByNameInCompetition(input, this.competition.id).then(res => {
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
      this.getRegistrations(this.competition.id, this.player.id)
    },
  }
}
</script>

<style scoped>

</style>