<template>
  <main>
    <h1 class="p-4">{{ $t("header.draws") }}</h1>
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
        <div id="main" class="col-md-8 mx-auto" v-if="chosenCategory !== null">
          <h3 class="p-4">{{ chosenCategory.categoryName }}</h3>
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
            <div id="group-section">
              <div id="main-header">
                <div class="d-flex justify-content-end">
                  <button type="button" class="btn btn-danger me-3" @click="deleteDraw">{{
                      $t("draw.main.deleteDraw")
                    }}
                  </button>
                  <button type="button" class="btn btn-warning" @click="createDraw">{{
                      $t("draw.main.redraw")
                    }}
                  </button>
                </div>
              </div>
              <br>
              <div v-for="group in draw.groupDraw.groups" :key="group.groupName" class="row mb-4 d-flex align-items-start p-3 border rounded">
                <h4 class="text-start mb-3">{{ $t("draw.main.group") }} {{ group.groupName }}</h4>
                <div class="col-sm-4">
                  <p class="text-start">{{$t("player.heading")}}</p>
                <PoolDraw :group="group"/>
                </div>
                <div class="col-sm-8">
                  <div id="matches" class="row justify-content-center">
                    <p class="text-start">{{$t("draw.pool.matches")}}</p>
                      <table class="table table-bordered table-striped table-sm">
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
                          <td>{{ getPlayerOne(match) }}</td>
                          <td>{{ getPlayerTwo(match) }}</td>
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
  </main>
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
      DrawService.deleteDraw(this.competition.id, this.chosenCategory.competitionCategoryId).then(() => {
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
      if (match.firstPlayer.length === 1) {
        playerOne = match.firstPlayer[0].firstName + " " + match.firstPlayer[0].lastName
      } else if (match.firstPlayer.length === 2) {
        playerOne = match.firstPlayer[0].firstName + " " + match.firstPlayer[0].lastName + "/" +
            match.firstPlayer[1].firstName + " " + match.firstPlayer[1].lastName
      }
      return playerOne
    },
    getPlayerTwo(match) {
      let playerTwo = ""
      if (match.secondPlayer.length === 1) {
        playerTwo = match.secondPlayer[0].firstName + " " + match.secondPlayer[0].lastName
      } else if (match.firstPlayer.length === 2) {
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
h1 {
  background-color: var(--clr-primary-100);
  margin-bottom: 0;
}

.main-upper {
  background: #f5f4f4;
  padding: 10px;
  box-shadow: 0 2px #efefef;
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

</style>