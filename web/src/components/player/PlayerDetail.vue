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
        <div class="p-4 m-5 custom-card" v-for="registration in registrations" :key="registration.id">
          <div class="pb-2">
            <h4>{{ registration.competitionCategory.name }}
            </h4>
          </div>
          <div v-if="registration.competitionCategory.type === 'DOUBLES'">
            <p>{{ $t("player.playingWith") }}
              <span class="clickable" @click="getRegistrations(competition.id, registration.accompanyingPlayer.id)">{{
                  registration.accompanyingPlayer.firstName + ' ' + registration.accompanyingPlayer.lastName
                }}</span>
            </p>
          </div>
          <p class="py-2" v-if="!isCategoryDrawnMap[registration.competitionCategory.id]">{{ $t("draw.main.notDrawnTitle") }}</p>
          <match-list-component :matches="registration.matches"/>
          <div class="d-flex justify-content-end">
            <!-- If category is already drawn, give WO -->
            <button class="btn btn-danger"
                    type="button"
                    v-if="isCategoryDrawnMap[registration.competitionCategory.id] && registration.registrationStatus === 'PLAYING'"
                    @click="giveWalkover(registration.competitionCategory.id, registration.id)">
              {{ $t("player.giveWalkover") }}
            </button>

            <!-- If category is already drawn, withdraw -->
            <button class="btn btn-danger"
                    type="button"
                    v-if="!isCategoryDrawnMap[registration.competitionCategory.id] && registration.registrationStatus === 'PLAYING'"
                    @click="withdraw(registration.competitionCategory.id, registration.id)">
              {{ $t("player.withdraw") }}
            </button>
          </div>
          <div class="pt-4">
            <div v-if="registration.registrationStatus === 'WALK_OVER'">
              <p class="fw-bold fs-4"> {{ $t("player.hasGivenWalkover") }}</p>
            </div>
            <div v-if="registration.registrationStatus === 'WITHDRAWN'">
              <p class="fw-bold fs-4"> {{ $t("player.hasWithdrawn") }}</p>
            </div>
          </div>
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
import DrawService from "@/common/api-services/draw.service";
import Vue from "vue";

export default {
  name: "PlayerDetail",
  data() {
    return {
      player: null,
      playerId: null,
      playerNotFound: false,
      registrations: [],
      isCategoryDrawnMap: {}
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
    this.isCategoryDrawnMap = {}
    if (this.playerId !== '0') {
      this.getRegistrations(this.competition.id, this.playerId)
    }
  },
  methods: {
    getRegistrations(competitionId, playerId) {
      RegistrationService.getRegistrationsForPlayer(competitionId, playerId).then(res => {
        this.player = res.data.player
        this.registrations = res.data.registrations

        this.registrations.forEach(reg => {
          DrawService.isDrawMade(this.competition.id, reg.competitionCategory.id).then(res => {
            Vue.set(this.isCategoryDrawnMap, reg.competitionCategory.id, res.data)
          })
        })
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
    isCategoryDrawn(categoryId) {
      return DrawService.isDrawMade(this.competition.id, categoryId)
    },
    getSearchResult(searchResult) {
      return searchResult.firstName + " " + searchResult.lastName
    },
    giveWalkover(categoryId, registrationId) {
      if (confirm(this.$t("player.walkoverWarningText", {player: this.player.firstName + this.player.lastName}))) {
        RegistrationService.giveWalkover(this.competition.id, categoryId, registrationId).then(() => {
          this.getRegistrations(this.competition.id, this.playerId)
        })
      }
    },
    withdraw(categoryId, registrationId) {
      if (confirm(this.$t("player.withdrawWarningText", {player: this.player.firstName + this.player.lastName}))) {
        RegistrationService.withdraw(this.competition.id, categoryId, registrationId).then(() => {
          this.getRegistrations(this.competition.id, this.playerId)
        })
      }
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