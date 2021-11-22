<template>
  <main class="container-fluid">
    <div class="row">
      <h1 class="p-4">
        <i @click="$router.push('/results')" class="fas fa-arrow-left" style="float: left"></i>
        {{ $t("billing.title") }}
      </h1>

      <!-- Sidebar -->
      <div class="sidebar col-md-3">
        <div class="sidebar-header">
          <h4> {{ $t("billing.sidebarTitle") }}</h4>
        </div>
        <ul class="list-group list-group-flush">
          <li class="list-group-item" v-for="club in clubs" :key="club.id" @click="chooseClub(club)"
              :class="chosenClub.id === club.id ? 'active' : ''">
            {{ club.name }}
          </li>
        </ul>
      </div>

      <!-- Main content -->
      <div id="main" class="col-md-9 mx-auto">
        <div class="p-4 col-sm-10">
          <h4 class="text-start">{{ $t("billing.firstHeading") }}</h4>
          <p class="text-start">{{ $t("billing.firstHelperText") }}</p>
        </div>
        <!-- Cost summary table (broken down per category) -->
        <div class="col-sm-10 mx-auto">
          <p class="text-start fs-5">{{ $t("billing.firstTableHeading") }} <span class="fw-bolder text-uppercase">{{this.chosenClub.name}}</span></p>
          <table v-if="costSummary !== null" class="table table-bordered table-striped table-responsive">
            <thead>
            <tr>
              <th>{{ $t("billing.table1.category") }}</th>
              <th>{{ $t("billing.table1.nrStarts") }}</th>
              <th>{{ $t("billing.table1.price") }}</th>
              <th>{{ $t("billing.table1.total") }}</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="costSummary in costSummary.costSummaries" :key="costSummary.category">
              <td>{{ costSummary.category.category.name }}</td>
              <td>{{ costSummary.numberOfStarts }}</td>
              <td>{{ costSummary.price }}</td>
              <td>{{ costSummary.totalPrice }}</td>
            </tr>
            <tr>
              <td>{{$t("billing.table1.total")}}</td>
              <td></td>
              <td></td>
              <td>{{costSummary.totalCostForClub}}</td>
            </tr>
            </tbody>
          </table>
        </div>
        <!-- Cost summary table (broken down per category) -->
        <div class="col-sm-10 mx-auto pt-4">
          <p class="text-start fs-5">{{ $t("billing.secondHeading") }}</p>
          <table v-if="playerCostSummary !== null" class="table table-bordered table-striped table-responsive">
            <thead>
            <tr>
              <th>{{ $t("billing.table2.player") }}</th>
              <th>{{ $t("billing.table2.category") }}</th>
              <th>{{ $t("billing.table2.price") }}</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="costSummary in playerCostSummary.costSummaryList" :key="costSummary.player.firstName + costSummary.category.category.name">
              <td>{{ costSummary.player.firstName + " " + costSummary.player.lastName }}</td>
              <td>{{ costSummary.category.category.name }}</td>
              <td>{{ costSummary.price }}</td>
            </tr>
            <tr>
              <td>{{$t("billing.table1.total")}}</td>
              <td></td>
              <td>{{playerCostSummary.totalPrice}}</td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
import BillingService from "@/common/api-services/billing.service";

export default {
  name: "Billing",
  data() {
    return {
      clubs: [],
      chosenClub: null,
      costSummary: null,
      playerCostSummary: null
    }
  },
  computed: {
    competition: function () {
      return this.$store.getters.competition
    }
  },
  mounted() {
    BillingService.getParticipatingClubs(this.competition.id).then(res => {
      this.clubs = res.data
      if (this.clubs.length > 0) {
        this.chooseClub(this.clubs[0])
      }
    }).catch(() => {
      this.$toasted.error(this.$tc("billing.toasts.club-error")).goAway(5000)
    })
  },
  methods: {
    chooseClub(club) {
      this.chosenClub = club

      BillingService.getCostSummaryForClub(this.competition.id, this.chosenClub.id).then(res => {
        this.costSummary = res.data
      }).catch(() => {
        this.$toasted.error(this.$tc("billing.toasts.summary-error")).goAway(5000)
      })

      BillingService.getCostSummaryForPlayers(this.competition.id, this.chosenClub.id).then(res => {
        this.playerCostSummary = res.data
      }).catch(() => {
        this.$toasted.error(this.$tc("billing.toasts.summary-error")).goAway(5000)
      })
    }
  }
}
</script>

<style scoped>
h1 {
  background-color: var(--clr-primary-100);
  margin-bottom: 0;
}
</style>