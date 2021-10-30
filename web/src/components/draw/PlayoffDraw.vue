<template>
  <main id="tournament">

    <section id="bracket">
        <div class="d-flex">
          <div class="round current w-100 d-flex align-content-between m-4" v-for="round in playoffRounds"
               :key="round.round">
            <div class="round-details">{{ $t("round." + round.round) }}</div>
            <div class="w-100 h-100 d-flex flex-column justify-content-around">
              <div class="matchup" v-for="match in round.matches" :key="match.id">
                <table class="table table-responsive mb-2">
                  <tbody class="bg-white">
                  <tr>
                    <td>
                      <div v-if="match.firstPlayer[0].firstName !== 'BYE'">
                        <p class="mb-0" :class="isPlayerOneWinner(match) ? 'fw-bold' : ''">
                          {{ getPlayerOne(match) }}
                          <span class="text-uppercase"> {{ getClub(match.firstPlayer) }}</span>
                        </p>
                      </div>
                    </td>
                    <td class="score" v-for="game in match.result.gameList" :key="game.id">
                      {{ game.firstRegistrationResult }}
                    </td>
                  </tr>
                  <tr>
                    <td>
                      <div v-if="match.secondPlayer[0].firstName !== 'BYE'">
                        <p class="mb-0" :class="isPlayerTwoWinner(match) ? 'fw-bold' : ''">
                          {{ getPlayerTwo(match) }} <span
                            class="text-uppercase"> {{ getClub(match.secondPlayer) }}</span>
                        </p>
                      </div>
                    </td>
                    <td class="score" v-for="game in match.result.gameList" :key="game.id">
                      {{ game.secondRegistrationResult }}
                    </td>
                  </tr>
                  </tbody>
                </table>
              </div>

            </div>
          </div>
      </div>
    </section>

  </main>
</template>

<script>
import DrawService from "@/common/api-services/draw.service";
import {getClub, getPlayerOne, getPlayerTwo, isPlayerOneWinner, isPlayerTwoWinner} from "@/common/util";

export default {
  name: "PlayoffDraw",
  data() {
    return {
      playoffRounds: []
    }
  },
  props: {
    playoff: Object
  },
  mounted() {
    DrawService.getPlayoffDraw().then(res => {
      this.playoffRounds = res.data
    })
  },
  methods: {
    getPlayerOne: getPlayerOne,
    getPlayerTwo: getPlayerTwo,
    getClub: getClub,
    isPlayerOneWinner: isPlayerOneWinner,
    isPlayerTwoWinner: isPlayerTwoWinner,
  }
}
</script>

<style scoped>


#bracket {
  background-color: rgba(225, 225, 225, 0.20);
  font-size: 12px;
  padding: 40px 0;
  height: 100%;
}


.round {
  display: block;
  display: -webkit-box;
  display: -moz-box;
  display: -ms-flexbox;
  display: -webkit-flex;
  -webkit-flex-direction: column;
  flex-direction: column;
}

.split-one .round {
  margin: 0 2.5% 0 0;
}


.score {
  font-size: 11px;
  text-transform: uppercase;
  color: #2C7399;
  font-weight: bold;
  right: 5px;
}

.round-details {
  font-size: 13px;
  color: #2C7399;
  text-transform: uppercase;
  text-align: center;
  height: 40px;
}

.current li {
  opacity: 1;
}



</style>