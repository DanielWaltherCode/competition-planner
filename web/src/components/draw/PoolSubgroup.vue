<template>
  <div class="modal-content">
    <div class="modal-header">
      <h5 class="modal-title" id="exampleModalLabel"><h3> {{ $t("draw.pool.subgroupStanding.heading") }}</h3></h5>
      <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
    </div>
    <div class="modal-body">
      <p class="fs-6 text-black-50">{{ $t("draw.pool.subgroupStanding.introText") }}</p>
      <div v-for="(standingList, key) in subGroupList" :key="key" class="table-responsive">
        <table class="table table-bordered table-responsive">
          <thead>
          <tr>
            <th scope="col">{{ $t("draw.pool.place") }}</th>
            <th scope="col">{{ $t("draw.pool.subgroupStanding.matchesInSubGroup") }}</th>
            <th scope="col">{{ $t("draw.pool.matches") }}</th>
            <th scope="col">{{ $t("draw.pool.subgroupStanding.GAME_QUOTIENT") }}</th>
            <th scope="col">{{ $t("draw.pool.subgroupStanding.POINT_QUOTIENT") }}</th>
            <th scope="col">{{ $t("draw.pool.groupPoints") }}</th>
            <th scope="col">{{ $t("draw.pool.subgroupStanding.reasonForPosition") }}</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(standing, index) in standingList.groupStandingList" :key="index">
            <td class="text-start ps-3">{{ standing.groupPosition + ". " + getName(standing.player) }} {{
                getClub(standing.player)
              }}
            </td>
            <td v-if="standing.subgroupStanding != null">{{ standing.subgroupStanding.matchesPlayed }}</td>
            <td v-if="standing.subgroupStanding != null">{{ standing.subgroupStanding.matchesWonLost.won }} -
              {{ standing.subgroupStanding.matchesWonLost.lost }}
            </td>
            <td v-if="standing.subgroupStanding != null">{{
                standing.subgroupStanding.gamesWonLost.won
              }}/{{ standing.subgroupStanding.gamesWonLost.lost }}
            </td>
            <td v-if="standing.subgroupStanding != null">{{
                standing.subgroupStanding.pointsWonLost.won
              }}/{{ standing.subgroupStanding.pointsWonLost.lost }}
            </td>
            <td v-if="standing.subgroupStanding != null">{{ standing.subgroupStanding.groupScore }}</td>
            <td v-if="standing.subgroupStanding != null">{{
                $t("draw.pool.subgroupStanding." + standing.sortedBy)
              }}
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">{{ $t("general.close") }}</button>
    </div>
  </div>
</template>

<script>
export default {
  name: "PoolSubgroup",
  props: ["subGroupList"],
  methods: {
    getClub(playerDTOs) {
      if (playerDTOs.length === 1) {
        return playerDTOs[0].club.name
      } else if (playerDTOs.length === 2) {
        return playerDTOs[0].club.name + "/" + playerDTOs[1].club.name
      }
    },
    getName(playerDTOs) {
      if (playerDTOs.length === 1) {
        return playerDTOs[0].firstName + " " + playerDTOs[0].lastName
      } else if (playerDTOs.length === 2) {
        return playerDTOs[0].firstName + " " + playerDTOs[0].lastName + "/" +
            playerDTOs[1].firstName + " " + playerDTOs[1].lastName
      }
    },
  }
}
</script>

<style scoped>
tr td {
  min-width: 150px;
}
</style>