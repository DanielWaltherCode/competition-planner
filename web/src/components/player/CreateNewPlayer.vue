<template>
  <div>
    <!-- Page heading -->
    <div class="row p-4 blue-section">
      <h4 class="text-start">{{ $t("player.create.heading") }}</h4>
      <p class="text-start col-sm-8">{{ $t("player.create.text") }}</p>
    </div>
    <div class="row custom-card m-sm-1">
      <form id="input-form" class="rounded row p-3">
        <div class="col-md-11">
          <div class="row mx-auto">
            <div class="col-sm-6 mb-3">
              <label for="firstName" class="text-start form-label"> {{ $t("player.add.firstName") }}</label>
              <input type="text" @keyup="noFirstName = false" class="form-control"
                     v-model="firstName"
                     id="firstName">
              <p class="fs-6 text-danger" v-if="noFirstName">{{ $t("validations.required") }}</p>
            </div>

            <div class="col-sm-6 mb-3">
              <label for="lastName" class="text-start form-label"> {{ $t("player.add.lastName") }}</label>
              <input type="text" @keyup="noLastName = false" class="form-control"
                     v-model="lastName"
                     id="lastName">
              <p class="fs-6 text-danger" v-if="noLastName">{{ $t("validations.required") }}</p>
            </div>

            <div class="col-sm-6 mb-3">
              <!-- Standard validation does not work on select menues -->
              <label for="club-select" class="form-label"> {{ $t("player.add.club") }}</label>
              <select class="form-select" id="club-select"
                      @change="noClub = false" v-model="club">
                <option v-for="club in clubs" :key="club.id" :value="club">
                  {{ club.name }}
                </option>
              </select>
              <p class="fs-6 text-danger" v-if="noClub">{{ $t("validations.required") }}</p>
            </div>

            <div class="col-sm-6 mb-3">
              <label for="dateOfBirth" class="text-start form-label"> {{ $t("player.add.dateOfBirth") }}</label>
              <input type="date" @change="noDateOfBirth = false" class="form-control"
                     v-model="dateOfBirth"
                     id="dateOfBirth">
              <p class="fs-6 text-danger" v-if="noDateOfBirth">{{ $t("validations.required") }}</p>
            </div>
            <div class="d-flex justify-content-end">
              <button type="button" class="btn btn-primary" @click="addPlayer">
                {{ $t("player.add.buttonText") }}
              </button>
            </div>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import PlayerService from "@/common/api-services/player.service";
import ClubService from "@/common/api-services/club.service";

export default {
  name: "CreateNewPlayer",
  data() {
    return {
      noClub: false,
      noFirstName: false,
      noLastName: false,
      noDateOfBirth: false,
      failedToAddPlayer: false,
      player: null,
      firstName: null,
      lastName: null,
      club: null,
      clubs: [],
      dateOfBirth: null,
    }
  },
  mounted() {
    ClubService.getClubs().then(res => {
      this.clubs = res.data
    })
  },
  methods: {

    clearPlayer() {
      this.firstName = null
      this.lastName = null
      this.club = null
      this.dateOfBirth = null
      this.failedToAddPlayer = false
    },
    addPlayer() {
      if (!this.validateSubmission()) {
        return
      }
      const playerSpec = {
        firstName: this.firstName,
        lastName: this.lastName,
        clubId: this.club.id,
        dateOfBirth: this.dateOfBirth
      }
      PlayerService.addPlayer(playerSpec).then(() => {
        this.$toasted.success(this.$tc("toasts.player.added")).goAway(3000)
        this.clearPlayer()
      })
          .catch(() => {
            this.$toasted.error(this.$tc("toasts.error.general.update")).goAway(3000)
          })
    },
    validateSubmission() {
      if (!this.firstName) {
        this.noFirstName = true
        return false
      }
      if (!this.lastName) {
        this.noLastName = true
        return false
      }
      if (!this.club) {
        this.noClub = true
        return false
      }
      if (!this.dateOfBirth) {
        this.noDateOfBirth = true
        return false
      }
      return true
    }
  }
}
</script>

<style scoped>

</style>