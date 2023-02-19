<template>
  <div>

    <!-- If singles category -->
    <div class="row custom-card p-4" v-if="category.category.type === 'SINGLES'">
      <h2 class="p-3">{{ $t("categories.register") }}</h2>
      <search-player-component ref="singles-search" class="justify-content-center"
                               :with-competition="true"
                               v-on:clear-player="singlesPlayer = null"
                               v-on:player-found="singlesPlayer = $event"></search-player-component>
      <div class="d-flex justify-content-end">
        <button type="button" class="btn btn-primary" @click="registerPlayerSingles">
          {{ $t("player.add.buttonText") }}
        </button>
      </div>
      <div class="row">
        <p class="text-start col-sm-8"><i class="fas fa-info-circle me-3"></i> {{ $t("player.add.helperText") }}
          <span class="clickable text-decoration-underline"
                @click="$router.push('/players/create')">{{ $t("player.sidebar.create") }}
        </span>
        </p>
      </div>
    </div>

    <!-- If doubles category -->
    <div class="row custom-card p-4" v-if="category.category.type === 'DOUBLES'">
      <h2 class="p-3">{{ $t("categories.register") }}</h2>
      <div class="d-flex justify-content-start px-4">
        <p class="mb-0">{{ $t("player.doublesPlayer1") }}</p>
      </div>
      <search-player-component class="justify-content-center"
                               :with-competition="true"
                               ref="double1" v-on:clear-player="doublesPlayer1 = null"
                               v-on:player-found="doublesPlayer1 = $event"></search-player-component>
      <div class="d-flex justify-content-start px-4">
        <p class="mb-0">{{ $t("player.doublesPlayer2") }}</p>
      </div>
      <search-player-component class="justify-content-center"
                               :with-competition="true"
                               ref="double2" v-on:clear-player="doublesPlayer2 = null"
                               v-on:player-found="doublesPlayer2 = $event"></search-player-component>
      <div class="d-flex justify-content-end">
        <button type="button" class="btn btn-primary" @click="registerDoublesPlayers">
          {{ $t("player.add.buttonText") }}
        </button>
      </div>
      <div class="row">
        <p class="text-start col-sm-8"><i class="fas fa-info-circle me-3"></i> {{ $t("player.add.helperText") }}
          <span class="clickable text-decoration-underline"
                @click="$router.push('/players/create')">{{ $t("player.sidebar.create") }}
        </span>
        </p>
      </div>
    </div>

  </div>
</template>

<script>
import SearchPlayerComponent from "@/components/player/SearchPlayerComponent";
import RegistrationService from "@/common/api-services/registration.service";
import {generalErrorHandler} from "@/common/util";

export default {
  name: "AddPlayerToCategory",
  components: {SearchPlayerComponent},
  props: {
    category: Object,
  },
  data() {
    return {
      singlesPlayer: null,
      doublesPlayer1: null,
      doublesPlayer2: null
    }
  },
  computed: {
    competition() {
      return this.$store.getters.competition
    }
  },
  methods: {
    registerPlayerSingles() {
      if (this.singlesPlayer === null) {
        this.$toasted.error(this.$tc("toasts.player.couldNotBeAddedToCompetition")).goAway(5000)
        return;
      }

      const registrationSpec = {
        playerId: this.singlesPlayer.id,
        competitionCategoryId: this.category.id
      }
      RegistrationService.registerPlayerSingles(this.competition.id, registrationSpec).then(() => {
        this.$toasted.success(this.$tc("toasts.player.added")).goAway(3000)
        this.singlesPlayer = null
        this.$refs["singles-search"].clearPlayer()
      }).catch(err => {
        this.errorHandler(err.data)
      })
    },
    registerDoublesPlayers() {
      if (this.doublesPlayer1 === null || this.doublesPlayer2 === null) {
        this.$toasted.error(this.$tc("toasts.player.doubleRegistrationError")).goAway(7000)
        return;
      }

      const registrationSpec = {
        playerOneId: this.doublesPlayer1.id,
        playerTwoId: this.doublesPlayer2.id,
        competitionCategoryId: this.category.id
      }
      RegistrationService.registerPlayerDoubles(this.competition.id, registrationSpec).then(() => {
        this.$toasted.success(this.$tc("toasts.player.added")).goAway(3000)
        this.$refs.double1.clearPlayer()
        this.$refs.double2.clearPlayer()
      }).catch(err => {
        this.errorHandler(err.data)
      })
    },
    errorHandler: generalErrorHandler
  }
}
</script>

<style scoped>

</style>