<template>
  <div class="container-fluid">
    <div class="row gx-5">
      <div id="main">
        <h1 id="main-title">{{ category.categoryName }}</h1>

        <!-- If class is not drawn yet -->
        <div class="col-sm-4 m-auto text-start" v-if="!isCategoryDrawn ">
          <div class="main-upper" v-if="registeredPlayersLists.length > 0">
            <p> {{ $t("draw.main.notDrawnTitle") }}</p>
          </div>
          <div v-if="registeredPlayersLists.length === 0">
            <p>{{ $t("draw.main.notDrawnNoPlayers") }}</p>
          </div>
          <!-- List of registered players if there are any -->
          <div id="registered-players" v-if="registeredPlayersLists.length > 0">
            <h3 class="text-start">{{ $t("draw.main.registeredPlayers") }}</h3>
            <div v-for="(playerList, index) in registeredPlayersLists" :key="index">
              <div v-for="player in playerList" :key="player.id">
                {{ player.firstName + " " + player.lastName + " (" + player.club.name + ")" }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- If class is drawn -->
      <div v-if="isCategoryDrawn && draw !== null">
        <div id="group-section">
          <div id="main-header">
            <h2>{{ $t("draw.pool.groups") }}</h2>
          </div>
          <br>
          <div v-for="group in draw.groupDraw.groups" :key="group.groupName" class="row">
            <h3>{{ $t("draw.main.group") }} {{ group.groupName }}</h3>
            <div class="col-sm-4">
              <PoolDraw :group="group"></PoolDraw>
            </div>
            <div id="matches" class="justify-content-center col-sm-8">
              <table class="table table-striped table-sm table-bordered">
                <thead class="thead-dark">
                <tr>
                  <th>{{ $t("draw.pool.time") }}</th>
                  <th></th>
                  <th></th>
                  <th>{{ $t("draw.pool.result") }}</th>
                </tr>
                </thead>
                <tbody>
                <tr class="group-matches" v-for="match in group.matches" :key="match.id">
                  <td>{{ getTime(match) }}</td>
                  <td :class="isPlayerOneWinner(match) ? 'fw-bold': ''">{{ getPlayerOne(match) }}</td>
                  <td :class="isPlayerTwoWinner(match) ? 'fw-bold': ''">{{ getPlayerTwo(match) }}</td>
                  <td v-if="match !== null">
                    <p class="pe-2 pb-0" v-for="game in match.result.gameList" :key="game.id">
                      {{ game.firstRegistrationResult }} - {{ game.secondRegistrationResult }}
                    </p>
                  </td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
          <br>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ApiService from "../../api-services/api.service";
import PoolDraw from "../PoolDraw";

export default {
  name: "CompetitionDraw",
  data() {
    return {
      competitionId: 0,
      isCategoryDrawn: false,
      category: {},
      draw: null,
      // Holds a list containing lists of players. They need to be sent as lists since in the case of doubles there
      // are two people for each registration id
      registeredPlayersLists: []
    }
  },
  props: {
    categoryId: Number
  },
  components: {PoolDraw},
  mounted() {
    this.competitionId = this.$route.params.competitionId
    this.getCategoryData()
  },
  methods: {
    getCategoryData() {
      ApiService.getCategory(this.competitionId, this.categoryId).then(res => {
        this.category = res.data
      })
      ApiService.isClassDrawn(this.competitionId, this.categoryId).then(res => {
        if (res.data === true) {
          this.isCategoryDrawn = true
          this.getDraw(this.competitionId, this.categoryId)
        } else {
          this.isCategoryDrawn = false
          this.getRegisteredPlayers()
        }
      })
    },
    getDraw() {
      ApiService.getDraw(this.competitionId, this.categoryId).then(res => {
        this.draw = res.data
      })
    },
    getRegisteredPlayers() {
      ApiService.getRegistrationsInCategory(this.competitionId, this.categoryId)
          .then(res => {
            this.registeredPlayersLists = res.data
          })
    },
    getPlayerOne(match) {
      let playerOne = ""
      if (match.firstPlayer.length === 1) {
        playerOne = match.firstPlayer[0].firstName + " " + match.firstPlayer[0].lastName + " " + match.firstPlayer[0].club.name
      } else if (match.firstPlayer.length === 2) {
        playerOne = match.firstPlayer[0].firstName + " " + match.firstPlayer[0].lastName + " " + match.firstPlayer[0].club.name + "/" +
            match.firstPlayer[1].firstName + " " + match.firstPlayer[1].lastName + " " + match.firstPlayer[1].club.name
      }
      return playerOne
    },
    getPlayerTwo(match) {
      let playerTwo = ""
      if (match.secondPlayer.length === 1) {
        playerTwo = match.secondPlayer[0].firstName + " " + match.secondPlayer[0].lastName + " " + match.secondPlayer[0].club.name
      } else if (match.firstPlayer.length === 2) {
        playerTwo = match.secondPlayer[0].firstName + " " + match.secondPlayer[0].lastName + " " + match.secondPlayer[0].club.name + "/" +
            match.secondPlayer[1].firstName + " " + match.secondPlayer[1].lastName + " " + match.secondPlayer[1].club.name
      }
      return playerTwo
    },
    isPlayerOneWinner(match) {
      if (match.winner.length > 0) {
        const winnerIds = match.winner.map(winner => winner.id)
        if (winnerIds.includes(match.firstPlayer[0].id)) {
          return true
        }
      }
      return false
    },
    isPlayerTwoWinner(match) {
      if (match.winner.length > 0) {
        const winnerIds = match.winner.map(winner => winner.id)
        if (winnerIds.includes(match.secondPlayer[0].id)) {
          return true
        }
      }
      return false
    },
    getTime(match) {
      if (match.startTime === null) {
        return this.$t("draw.pool.noTime")
      }
    }
  }
}
</script>

<style scoped>

</style>