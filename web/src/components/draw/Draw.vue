<template>
  <div class="container-fluid">
    <div class="row gx-5">

      <!-- Sidebar -->
      <div class="sidebar col-md-3">
        <div class="sidebar-header">
          <h4> {{ $t("draw.sidebar.title") }}</h4>
        </div>
        <ul class="list-group list-group-flush">
          <li v-for="category in competitionCategories" class="list-group-item" :key="category.competitionCategoryId"
              @click="makeChoice(category)"
              :class="category.categoryName === chosenCategory.categoryName ? 'active' : ''">
            {{ category.categoryName }}
          </li>
        </ul>
      </div>

      <!-- Main content -->
      <div id="main" class="col-md-9" v-if="chosenCategory !== null">
        <h1 id="main-title">{{ chosenCategory.categoryName }}</h1>

        <!-- If class is not drawn yet -->
        <div v-if="!isChosenCategoryDrawn ">
          <div class="main-upper" v-if="registeredPlayersLists.length > 0">
            <p> {{ $t("draw.main.notDrawnTitle") }}</p>
            <br>
            <p> {{ $t("draw.main.notDrawnBody") }}</p>
            <button class="btn btn-primary" @click="createDraw">{{ $t("draw.main.drawNow") }}</button>
          </div>
          <div v-if="registeredPlayersLists.length === 0">
            <p>{{ $t("draw.main.notDrawnNoPlayers") }}</p>
          </div>
          <!-- List of registered players if there are any -->
          <div id="registered-players" v-if="registeredPlayersLists.length > 0">
            <h3>{{ $t("draw.main.registeredPlayers") }}</h3>
            <div v-for="(playerList, index) in registeredPlayersLists" :key="index">
              <div v-for="player in playerList" :key="player.id">
                {{ player.firstName + " " + player.lastName + " (" + player.club.name + ")" }}
              </div>
            </div>
          </div>
        </div>

        <!-- If class is drawn -->
        <div v-if="isChosenCategoryDrawn && draw !== null">
          <div>
            <p>{{$t("draw.main.drawMade")}}</p>
          </div>
          <div id="group-section">
            <div id="main-header">
              <h2>{{ $t("draw.pool.groups") }}</h2>
              <div>
                <button type="button" class="btn btn-outline-danger me-3" @click="deleteDraw">{{ $t("draw.main.deleteDraw") }}</button>
                <button type="button" class="btn btn-outline-primary" @click="createDraw">{{ $t("draw.main.redraw") }}</button>
              </div>
            </div>
            <br>
            <div v-for="group in draw.groupDraw.groups" :key="group.groupName" class="group">
              <h3>{{ $t("draw.main.group") }} {{ group.groupName }}</h3>
              <PoolDraw :group="group"/>
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
  </div>
</template>

<script>
import DrawService from "@/common/api-services/draw.service";
import RegistrationService from "@/common/api-services/registration.service";
import PoolDraw from "@/components/draw/PoolDraw";

export default {
  components: {PoolDraw},
  data() {
    return {
      chosenCategory: null,
      isChosenCategoryDrawn: false,
      competitionCategories: [],
      draw: null,
      // Holds a list containing lists of players. They need to be sent as lists since in the case of doubles there
      // are two people for each registration id
      registeredPlayersLists: []
    }
  },
  name: "Draw",
  computed: {
    competition: function () {
      return this.$store.getters.competition
    }
  },
  mounted() {
    DrawService.getCompetitionCategories(this.competition.id).then(res => {
      this.competitionCategories = res.data.categories
      if (this.competitionCategories.length > 0) {
        this.makeChoice(this.competitionCategories[0])
      }
    })
  },
  methods: {
    isCategoryDrawn(categoryId) {
      return DrawService.isClassDrawn(categoryId)
    },
    makeChoice(category) {
      this.chosenCategory = category
      DrawService.isClassDrawn(this.competition.id, category.competitionCategoryId).then(res => {
        if (res.data === true) {
          this.isChosenCategoryDrawn = true
          this.getDraw(category.competitionCategoryId)
        } else {
          this.isChosenCategoryDrawn = false
          this.getRegisteredPlayers()
        }
      })
    },
    createDraw() {
      DrawService.createDraw(this.competition.id, this.chosenCategory.competitionCategoryId).then(res => {
        this.draw = res.data
        this.isChosenCategoryDrawn = true
      })
    },
    deleteDraw() {
      DrawService.deleteDraw(this.competition.id, this.chosenCategory.competitionCategoryId).then( ()=> {
        this.isChosenCategoryDrawn = false
        this.getRegisteredPlayers()
      })
    },
    getDraw(categoryId) {
      DrawService.getDraw(this.competition.id, categoryId).then(res => {
        this.draw = res.data
      })
    },
    getRegisteredPlayers() {
      RegistrationService.getRegistrationsInCategory(this.competition.id, this.chosenCategory.competitionCategoryId)
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


/**
* Main
 */
#main {
  text-align: left;
}

#main-title {
  margin-bottom: 30px;
}

.main-upper {
  background: #f5f4f4;
  padding: 10px;
  box-shadow: 0 2px #efefef;
}

#main-header {
  display: flex;
  justify-content: space-between;
}

.heading p {
  margin-bottom: 0;
}

#registered-players {
  margin-top: 40px;
}

#matches {
  margin-left: 20px;
}

.group {
  box-shadow: 0 3px #efefef;
  margin-bottom: 20px;
}
</style>