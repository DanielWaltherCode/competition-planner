<template>
  <main>
    <h1 class="p-4">
      <i class="fas fa-arrow-left" style="float: left" @click="$router.push('/players')" />
      {{ $t("draw.main.title") }}
      <i class="fas fa-arrow-right" style="float: right" @click="$router.push('/schedule')" />
    </h1>
    <div>
      <div class="row">
        <!-- Sidebar -->
        <div class="sidebar col-md-3">
          <div class="sidebar-header">
            <h4> {{ $t("draw.sidebar.title") }}</h4>
          </div>
          <ul class="list-group list-group-flush">
            <li v-for="category in competitionCategories" :key="category.id" class="list-group-item clickable"
                :class="category.category.name === chosenCategory.category.name ? 'active' : ''"
                @click="makeChoice(category)">
              {{ tryTranslateCategoryName(category.category.name) }}
            </li>
          </ul>
        </div>

        <!-- Main content -->
        <div v-if="chosenCategory !== null" class="col-md-9 ps-0">
          <div class="blue-section row">
            <div class="top-content col-md-10 mx-auto">
              <h3 class="p-4">
                {{ tryTranslateCategoryName(chosenCategory.category.name) }}
              </h3>

              <!-- If class is not drawn yet -->
              <div v-if="!isChosenCategoryDrawn " class="pb-4 ms-3 ms-md-0">
                <div v-if="registeredPlayersDoubles != null && registeredPlayersDoubles.length > 0 ||
                registeredPlayersSingles != null && registeredPlayersSingles.numberOfPlayers > 0">
                  <p class="text-start text-md-center">
                    {{ $t("draw.main.notDrawnTitle") }} {{ $t("draw.main.notDrawnBody") }}
                  </p>
                  <button class="btn btn-primary" @click="createDraw">
                    {{ $t("draw.main.drawNow") }}
                  </button>
                  <button type="button" class="btn btn-outline-primary" @click="showSeedModal = !showSeedModal">
                    Seeda manuellt
                  </button>
                </div>
                <div v-else>
                  <p>{{ $t("draw.main.notDrawnNoPlayers") }}</p>
                </div>
              </div>
            </div>
          </div>
          <!-- List of registered players if there are any. Doubles case first -->
          <div v-if="!isChosenCategoryDrawn && showSeedModal" class="custom-card p-4 col-7 mx-auto">
            <div v-for="seed in currentSeed" :key="seed.registrationSeedDTO.registration.id" class="d-flex">
              <input v-model="seed.registrationSeedDTO.seed" type="text"
                     style="width: 30px" class="me-4 mb-1">
              <div>
                <p v-for="player in seed.playerWithClubDTOs" :key="player.id">
                  {{ getFormattedPlayerName(player) }}
                </p>
              </div>
            </div>
            <div class="d-flex justify-content-end p-3">
              <button class="btn btn-primary" type="button" @click="approveSeeding">
                Spara seedning
              </button>
            </div>
          </div>
          <div v-if="!isChosenCategoryDrawn && registeredPlayersDoubles !== null" id="registered-players" class="pb-5">
<h3>{{ $t("draw.main.registeredPlayers") }}</h3>
            <!-- The innerPlayerList contains two players in case of doubles -->
            <div v-for="(innerPlayerList, index) in registeredPlayersDoubles" :key="index"
                 class="py-2 justify-content-center">
              <div v-for="player in innerPlayerList" :key="player.id">
                {{ player.firstName + " " + player.lastName + " " + player.club.name }}
              </div>
            </div>
          </div>
          <div v-if="!isChosenCategoryDrawn && registeredPlayersSingles !== null" class="pt-4 px-3">
            <h3 v-if="registeredPlayersSingles.numberOfPlayers > 0">
              {{ $t("draw.main.nrPlayers") + registeredPlayersSingles.numberOfPlayers }}
</h3>
            <div v-for="(players, grouping) in registeredPlayersSingles.groupingsAndPlayers" :key="grouping">
              <div class="heading">
                <p class="mb-0">
                  {{ grouping }}
                </p>
              </div>
              <div v-for="player in players" :key="player.id" class="mt-2 d-flex">
                <p class="player-name clickable" @click="$router.push('/players/detail/' + player.id)">
                  {{ player.lastName + ", " + player.firstName }}
                </p>
              </div>
            </div>
          </div>

          <!-- If class is drawn -->
          <div v-if="isChosenCategoryDrawn && draw !== null">
            <div id="group-section">
              <div id="main-header">
                <div class="d-flex justify-content-center p-3">
                  <div class="p-2 custom-card">
                    <button type="button" class="btn btn-warning me-3" @click="reDraw">
                      {{
                        $t("draw.main.redraw")
                      }}
                    </button>
                    <button type="button" class="btn btn-danger" @click="deleteDraw">
                      {{
                        $t("draw.main.deleteDraw")
                      }}
                    </button>
                  </div>
                </div>
                <div v-if="draw.playOff != null"
                     class="clickable px-5 py-3 text-uppercase fs-6 d-flex align-items-center"
                     @click="scrollToPlayoff">
                  <p class="mb-0 me-1">
                    {{ $t("draw.main.goToPlayoff") }}
                  </p>
                  <i class="fas fa-arrow-right" />
                </div>
              </div>
              <!-- If there are groups -->
              <div v-for="group in draw.groups" :key="group.name"
                   class="row col-sm-11 mx-auto mt-3 mb-4 d-flex align-items-start p-3 custom-card">
                <h4 class="text-start mb-3">
                  {{ $t("draw.main.group") }} {{ group.name }}
                </h4>
                <div class="col-sm-12">
                  <PoolDraw :group="group" />
                </div>
                <div class="col-sm-12 pt-4">
                  <div id="matches" class="row justify-content-center ms-0">
                    <h5 class="black text-start fw-bolder">
                      {{ $t("draw.pool.matches") }}
                    </h5>
                    <match-list-component :matches="group.matches" />
                  </div>
                </div>
              </div>
              <!-- If there is a playoff/cup -->
              <playoff-draw v-if="draw != null && draw.playOff != null && shouldShowPlayoff(draw.playOff)" id="playoff"
                            :playoff-rounds="draw.playOff" />
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
import {generalErrorHandler, getFormattedPlayerName, tryTranslateCategoryName} from "@/common/util"
import SearchPlayerComponent from "@/components/player/SearchPlayerComponent";

export default {
  name: "Draw",
  components: {PlayoffDraw, MatchListComponent, PoolDraw},
  data() {
    return {
      chosenCategory: null,
      isChosenCategoryDrawn: false,
      competitionCategories: [],
      draw: null,
      // Holds a list containing lists of players. They need to be sent as lists since in the case of doubles there
      // are two people for each registration id
      registeredPlayersSingles: null,
      registeredPlayersDoubles: null,
      showSeedModal: false,
      currentSeed: []
    }
  },
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
        DrawService.getCurrentSeed(this.competition.id, this.chosenCategory.id).then(res => {
          this.currentSeed = res.data
        })
      }
    })

  },
  methods: {
    isCategoryDrawn(categoryId) {
      return DrawService.isDrawMade(categoryId)
    },
    makeChoice(category) {
      this.showSeedModal = false
      this.chosenCategory = category
      this.currentSeed = []
      DrawService.isDrawMade(this.competition.id, category.id).then(res => {
        if (res.data === true) {
          this.isChosenCategoryDrawn = true
          this.getDraw(category.id)
        } else {
          this.isChosenCategoryDrawn = false
          this.getRegisteredPlayers()
          DrawService.getCurrentSeed(this.competition.id, this.chosenCategory.id).then(res => {
            this.currentSeed = res.data
          })
        }
      })
    },
    createDraw() {
      if (confirm(this.$tc("confirm.redraw"))) {
        DrawService.createDraw(this.competition.id, this.chosenCategory.id).then(res => {
          this.$toasted.success(this.$tc("toasts.categoryDrawn")).goAway(3000)
          this.draw = res.data
          this.isChosenCategoryDrawn = true
        }).catch(() => {
          this.$toasted.error(this.$tc("toasts.error.general.update")).goAway(7000)
        })
      }
    },
    reDraw() {
      if (confirm(this.$tc("confirm.redraw"))) {
        DrawService.deleteDraw(this.competition.id, this.chosenCategory.id).then(() => {
          DrawService.createDraw(this.competition.id, this.chosenCategory.id).then(res => {
            this.$toasted.success(this.$tc("toasts.categoryDrawn")).goAway(3000)
            this.draw = res.data
            this.isChosenCategoryDrawn = true
          }).catch(() => {
            this.$toasted.error(this.$tc("toasts.error.general.update")).goAway(7000)
          })
        }).catch(err => {
          this.errorHandler(err.data)
        })
      }
    },
    deleteDraw() {
      if (confirm(this.$tc("confirm.deleteDraw"))) {
        CategoryService.openCompetitionCategoryForRegistration(this.competition.id, this.chosenCategory.id).then(() => {
          this.$toasted.success(this.$tc("toasts.categoryDrawDeleted")).goAway(3000)
          this.isChosenCategoryDrawn = false
          this.getRegisteredPlayers()
        }).catch(err => {
          this.errorHandler(err.data)
        })
      }
    },
    shouldShowPlayoff(playoff) {
      if (playoff === null) {
        return false;
      }
      let nonPlaceholderPlayers = 0
      playoff[0].matches.forEach(match => {
        const firstPlayerId = match.firstPlayer[0].id
        const secondPlayerId = match.secondPlayer[0].id
        if ((firstPlayerId !== -1) && (secondPlayerId !== -1)) {
          nonPlaceholderPlayers += 2
        }
      })
      return nonPlaceholderPlayers === playoff[0].matches.length * 2;

    },
    getDraw(categoryId) {
      DrawService.getDraw(this.competition.id, categoryId).then(res => {
        this.draw = res.data
      }).catch(() => {
        this.$toasted.error(this.$tc("toasts.error.general.get")).goAway(7000)
      })
    },
    getRegisteredPlayers() {
      if (this.chosenCategory.category.type === "SINGLES") {
        RegistrationService.getRegisteredPlayersSingles(this.competition.id, this.chosenCategory.id)
            .then(res => {
              this.registeredPlayersDoubles = null
              this.registeredPlayersSingles = res.data
            })
      } else {
        RegistrationService.getRegisteredPlayersDoubles(this.competition.id, this.chosenCategory.id)
            .then(res => {
              this.registeredPlayersSingles = null
              this.registeredPlayersDoubles = res.data
            })
      }
    },
    scrollToPlayoff() {
      const playoffElement = document.getElementById("playoff")
      playoffElement.scrollIntoView({behavior: "smooth"})
    },
    approveSeeding() {
      const desiredSeeding = {seeding: this.currentSeed.map(it => it.registrationSeedDTO)}
      DrawService.approveSeeding(this.competition.id, this.chosenCategory.id, desiredSeeding)
          .then(() => {
            this.showSeedModal = false
            DrawService.getCurrentSeed(this.competition.id, this.chosenCategory.id).then(res => {
              this.currentSeed = res.data
            })
            this.$toasted.success(this.$tc("toasts.seedingSaved")).goAway(3000)
          })
          .catch(err => {
            this.errorHandler(err.data)
          })
    },
    tryTranslateCategoryName: tryTranslateCategoryName,
    errorHandler: generalErrorHandler,
    getFormattedPlayerName: getFormattedPlayerName
  },

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

.heading {
  color: var(--clr-primary-400);
  border-bottom: 1px solid lightgrey;
  text-align: left;
}

</style>