<template>
  <div class="container-fluid">
    <div class="row">
      <div id="sidebar" class="col-md-3">
        <div id="sidebar-header">
          <h4> {{ $t("draw.sidebar.title") }}</h4>
        </div>
        <ul class="list-group list-group-flush">
          <li v-for="category in competitionCategories" class="list-group-item" :key="category.competitionCategoryId"
              @click="makeChoice(category)" :class="category.categoryName === chosenCategory.categoryName ? 'active' : ''">
            {{ category.categoryName }}
          </li>
        </ul>
      </div>
      <div id="main" class="col-md-9" v-if="chosenCategory !== null">
        <h2 id="main-title">{{chosenCategory.categoryName}}</h2>
        <div v-if="!isChosenCategoryDrawn ">
          <div class="main-upper" v-if="registeredPlayersLists.length > 0">
            <p> {{ $t("draw.main.notDrawnTitle") }}</p>
            <br>
            <p> {{ $t("draw.main.notDrawnBody") }}</p>
            <button class="btn btn-primary" @click="createDraw">{{ $t("draw.main.drawNow") }}</button>
          </div>
          <div v-if="registeredPlayersLists.length === 0">
            <p>{{$t("draw.main.notDrawnNoPlayers")}}</p>
          </div>
          <!-- List of registered players -->
          <div id="registered-players" v-if="registeredPlayersLists.length > 0">
            <h3>{{ $t("draw.main.registeredPlayers") }}</h3>
            <div v-for="(playerList, index) in registeredPlayersLists" :key="index">
              <div v-for="player in playerList" :key="player.id">
                  {{player.firstName + " " + player.lastName + " (" + player.club.name + ")"}}
              </div>
            </div>
          </div>
        </div>
        <div v-if="isChosenCategoryDrawn && draw !== null">
          <div id="group-section">
            <h3>{{$t("draw.pool.groups")}}</h3>
          </div>
          <div v-for="group in draw.groupDraw.groups" :key="group.groupName">
            <PoolDraw :group="group" />
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
    }
  }
}
</script>

<style scoped>
#sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
}

#sidebar {
  min-height: 100vh;
  z-index: 0;
  box-shadow: 3px 3px 2px 1px #efefef;
}

@media only screen and (max-width: 768px) {
  #sidebar {
    min-height: fit-content;
  }
}

#sidebar-header:hover {
  cursor: pointer;
}

#sidebar li:hover {
  opacity: 0.7;
}

#sidebar li:hover {
  cursor: pointer;
}
/**
* Main
 */
#main {
  margin: 10px 0;
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

.heading p {
  margin-bottom: 0;
}

#registered-players {
  margin-top: 40px;
}
</style>