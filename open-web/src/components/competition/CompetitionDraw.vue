<template>
  <div class="container-fluid">
    <div class="row gx-5">
      <div id="main">
        <h1 id="main-title">{{ category.categoryName }}</h1>

        <!-- If class is not drawn yet -->
        <div class="col-sm-4 m-auto text-start"  v-if="!isCategoryDrawn ">
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
      <div v-if="isCategoryDrawn && draw !== null" class="col-sm-10 m-auto">
        <div id="group-section">
          <div id="main-header">
            <h2>{{ $t("draw.pool.groups") }}</h2>
          </div>
          <br>
          <div v-for="group in draw.groupDraw.groups" :key="group.groupName" class="group">
            <h3>{{ $t("draw.main.group") }} {{ group.groupName }}</h3>
            <PoolDraw :group="group"> </PoolDraw>
            <div id="matches" class="row justify-content-center">
              <h5 class="text-center">{{$t("draw.pool.matches")}}</h5>
              <div class="col-lg-8">
                <table class="table table-striped table-sm">
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
                    <td>{{getTime(match)}}</td>
                    <td>{{getPlayerOne(match)}}</td>
                    <td>{{getPlayerTwo(match)}}</td>
                    <td></td>
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
    categoryId: 0,
    isCategoryDrawn: false,
    category: {},
    draw: null,
    // Holds a list containing lists of players. They need to be sent as lists since in the case of doubles there
    // are two people for each registration id
    registeredPlayersLists: []
    }
  },
  components: {PoolDraw},
  mounted() {
    this.competitionId = this.$route.params.competitionId
    this.categoryId = this.$route.params.categoryId
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
  methods: {
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
      if(match.firstPlayer.length === 1) {
        playerOne = match.firstPlayer[0].firstName + " " + match.firstPlayer[0].lastName
      }
      else if (match.firstPlayer.length === 1) {
        playerOne = match.firstPlayer[0].firstName + " " + match.firstPlayer[0].lastName + "/" +
            match.firstPlayer[1].firstName + " " + match.firstPlayer[1].lastName
      }
      return playerOne
    },
    getPlayerTwo(match) {
      let playerTwo = ""
      if(match.secondPlayer.length === 1) {
        playerTwo = match.secondPlayer[0].firstName + " " + match.secondPlayer[0].lastName
      }
      else if (match.firstPlayer.length === 1) {
        playerTwo = match.secondPlayer[0].firstName + " " + match.secondPlayer[0].lastName + "/" +
            match.secondPlayer[1].firstName + " " + match.secondPlayer[1].lastName
      }
      return playerTwo
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