<template>
  <main>
    <h1 class="p-4">
      <i @click="$router.push('/players')" class="fas fa-arrow-left" style="float: left"></i>
      {{ $t("draw.main.title") }}
      <i @click="$router.push('/schedule')" class="fas fa-arrow-right" style="float: right"></i>
    </h1>
    <div class="container-fluid">
      <div class="row gx-5">

        <!-- Sidebar -->
        <div class="sidebar col-md-3">
          <div class="sidebar-header">
            <h4> {{ $t("draw.sidebar.title") }}</h4>
          </div>
          <ul class="list-group list-group-flush">
            <li v-for="category in competitionCategories" class="list-group-item" :key="category.id"
                @click="makeChoice(category)"
                :class="category.category.name === chosenCategory.category.name ? 'active' : ''">
              {{ category.category.name }}
            </li>
          </ul>
        </div>

        <!-- Main content -->
        <div class="col-md-9 ps-0" v-if="chosenCategory !== null">
          <div class="blue-section row">
            <div class="top-content col-md-10 mx-auto">
              <h3 class="p-4">{{ chosenCategory.category.name }}</h3>
              <!-- If class is not drawn yet -->
              <div v-if="!isChosenCategoryDrawn " class="pb-4">
                <div v-if="registeredPlayersLists.length > 0">
                  <p> {{ $t("draw.main.notDrawnTitle") }} {{ $t("draw.main.notDrawnBody") }}</p>
                  <button class="btn btn-primary" @click="createDraw">{{ $t("draw.main.drawNow") }}</button>
                </div>
                <div v-if="registeredPlayersLists.length === 0">
                  <p>{{ $t("draw.main.notDrawnNoPlayers") }}</p>
                </div>
              </div>
            </div>
          </div>
            <!-- List of registered players if there are any -->
            <div id="registered-players" v-if="!isChosenCategoryDrawn && registeredPlayersLists.length > 0">
              <h3>{{ $t("draw.main.registeredPlayers") }}</h3>
              <!-- The innerPlayerList contains two players in case of doubles -->
              <div v-for="(innerPlayerList, index) in registeredPlayersLists" class="py-2 justify-content-center" :key="index">
                <div v-for="player in innerPlayerList" :key="player.id">
                  {{ player.firstName + " " + player.lastName + " " + player.club.name }}
                </div>
              </div>
            </div>

          <!-- If class is drawn -->
          <div v-if="isChosenCategoryDrawn && draw !== null">
            <div id="group-section">
              <div id="main-header">
                <div class="d-flex justify-content-end p-3">
                  <div class="p-2 border border-1 rounded">
                    <button type="button" class="btn btn-warning me-3" @click="createDraw">{{
                        $t("draw.main.redraw")
                      }}
                    </button>
                  <button type="button" class="btn btn-danger" @click="deleteDraw">{{
                      $t("draw.main.deleteDraw")
                    }}
                  </button>
                  </div>
                </div>
              </div>
              <br>
              <!-- If there are groups -->
              <div v-for="group in draw.groupDraw.groups" :key="group.groupName"
                   class="row mb-4 d-flex align-items-start p-3 border rounded">
                <h4 class="text-start mb-3">{{ $t("draw.main.group") }} {{ group.groupName }}</h4>
                <div class="col-sm-4">
                  <PoolDraw :group="group"/>
                </div>
                <div class="col-sm-8">
                  <div id="matches" class="row justify-content-center">
                    <match-list-component :matches="group.matches" />
                  </div>
                </div>
                <br>
              </div>

              <!-- If there is a playoff/cup -->
            <playoff-draw v-if="draw != null && draw.playOff != null" :playoff-rounds="draw.playOff.rounds"></playoff-draw>
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
import CategoryService from "@/common/api-services/category.service";
import MatchListComponent from "@/components/general/MatchListComponent";
import PlayoffDraw from "@/components/draw/PlayoffDraw";

export default {
  components: {PlayoffDraw, MatchListComponent, PoolDraw},
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
    CategoryService.getCompetitionCategories(this.competition.id).then(res => {
      this.competitionCategories = res.data
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
      DrawService.isClassDrawn(this.competition.id, category.id).then(res => {
        if (res.data === true) {
          this.isChosenCategoryDrawn = true
          this.getDraw(category.id)
        } else {
          this.isChosenCategoryDrawn = false
          this.getRegisteredPlayers()
        }
      })
    },
    createDraw() {
      if(confirm(this.$tc("confirm.redraw"))) {
        DrawService.createDraw(this.competition.id, this.chosenCategory.id).then(res => {
          this.$toasted.success(this.$tc("toasts.categoryDrawn")).goAway(3000)
          this.draw = res.data
          this.isChosenCategoryDrawn = true
        }).catch(() => {
          this.$toasted.error(this.$tc("toasts.error.general")).goAway(5000)
        })
      }
    },
    deleteDraw() {
      if(confirm(this.$tc("confirm.deleteDraw"))) {
        DrawService.deleteDraw(this.competition.id, this.chosenCategory.id).then(() => {
          this.$toasted.success(this.$tc("toasts.categoryDrawDeleted")).goAway(3000)
          this.isChosenCategoryDrawn = false
          this.getRegisteredPlayers()
        }).catch(() => {
          this.$toasted.error(this.$tc("toasts.error.general")).goAway(5000)
        })
      }
    },
    getDraw(categoryId) {
      DrawService.getDraw(this.competition.id, categoryId).then(res => {
        this.draw = res.data
      }).catch(() => {
        this.$toasted.error(this.$tc("toasts.error.general")).goAway(5000)
      })
    },
    getRegisteredPlayers() {
      RegistrationService.getRegistrationsInCategory(this.competition.id, this.chosenCategory.id)
          .then(res => {
            this.registeredPlayersLists = res.data
          })
    },
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