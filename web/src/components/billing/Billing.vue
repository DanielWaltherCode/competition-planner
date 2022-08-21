<template>
  <main class="container-fluid">
    <div class="row">
      <h1 class="p-4">
        <i @click="$router.push('/results')" class="fas fa-arrow-left" style="float: left"></i>
        {{ $t("billing.title") }}
      </h1>

      <!-- Sidebar -->
      <div class="sidebar col-md-3" v-if="this.chosenClub !== null">
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
      <div id="main" class="col-md-9 mx-auto" ref="content">
        <div class="p-4 col-sm-10">
          <h4 class="text-start">{{ $t("billing.firstHeading") }}</h4>
          <p class="text-start">{{ $t("billing.firstHelperText") }}</p>
          <div class="d-flex justify-content-end">
            <button class="btn btn-primary me-2" @click="getPdf"> {{$t("billing.createPdf")}} </button>
            <button class="btn btn-light" @click="$router.push('/payment-info')"> {{$t("billing.updatePaymentInfo")}} </button>
          </div>
        </div>
        <!-- Cost summary table (broken down per category) -->
        <div class="col-sm-10 mx-auto" v-if="chosenClub !== null">
          <p class="text-start fs-5">{{ $t("billing.firstTableHeading") }} <span class="fw-bolder text-uppercase">{{this.chosenClub.name}}</span></p>
          <table id="table1" v-if="costSummary !== null" class="table table-bordered table-striped table-responsive">
            <thead>
            <tr>
              <th>{{ $t("billing.table1.category") }}</th>
              <th>{{ $t("billing.table1.nrStarts") }}</th>
              <th>{{ $t("billing.table1.price") }}</th>
              <th>{{ $t("billing.table1.total") }}</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="costSummary in costSummary.costSummaries" :key="costSummary.category.id">
              <td>{{ tryTranslateCategoryName(costSummary.category.category.name) }}</td>
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
          <table id="table2" v-if="playerCostSummary !== null" class="table table-bordered table-striped table-responsive">
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
              <td>{{ tryTranslateCategoryName(costSummary.category.category.name) }}</td>
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
import jsPDF from "jspdf";
import 'jspdf-autotable'
import {getFormattedDate} from "@/common/util";
import ClubService from "@/common/api-services/club.service";
import { tryTranslateCategoryName } from "@/common/util"

export default {
  name: "Billing",
  data() {
    return {
      clubs: [],
      chosenClub: null,
      costSummary: null,
      playerCostSummary: null,
      paymentInfo: null,
      organizingClub: null
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

    ClubService.getClub(this.competition.organizingClubId).then(res => {
      this.organizingClub = res.data
    }).catch(err => {
      console.log("Couldn't fetch club name", err)
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
    },
    getPdf() {
      ClubService.getPaymentInfo(this.competition.organizerId).then(res => {
        this.paymentInfo = res.data
        this.generalPdfSetup(true)
      }).catch(() => {
        this.$toasted.error(this.$tc("billing.paymentInfo.toastErrorFetch")).goAway(5000)
        this.generalPdfSetup(false)
      })
    },
    getFormattedDate: getFormattedDate,
    tryTranslateCategoryName: tryTranslateCategoryName,
    generalPdfSetup(includePaymentInfo) {
      const pdf = new jsPDF('p', 'pt', 'a4');
      pdf.setFont("times", "normal")
      pdf.setFontSize(16)
      pdf.text(this.$tc("billing.pdf.title"), 450, 50)

      pdf.setFontSize(14)

      pdf.text(this.$tc("billing.pdf.to") + this.chosenClub.name, 100, 90)
      pdf.text(this.$tc("billing.pdf.helperText") + this.competition.name, 100, 130)

      pdf.text(this.$tc("billing.pdf.billDate"), 100, 170)
      pdf.text(this.$tc("billing.pdf.paymentDate"), 250, 170)

      pdf.setFontSize(12)
      const date = new Date()
      pdf.text(getFormattedDate(date), 100, 190)
      pdf.text(getFormattedDate(date.setDate(date.getDate() + 28)), 250, 190)

      pdf.autoTable(
          {html: document.querySelector("#table1"),
            margin: {top: 230, bottom: 200}}
      )

      if (includePaymentInfo) {
        pdf.line(40, 750, 550, 750)

        // Headings
        pdf.setFontSize(10)
        pdf.setFont("times", "italic")
        pdf.text(this.$t("billing.paymentInfo.recipient"), 45, 770)
        pdf.text(this.$t("billing.pdf.address"), 180, 770)
        pdf.text(this.$t("billing.paymentInfo.plusgiro"), 270, 770)
        pdf.text(this.$t("billing.paymentInfo.bankgiro"), 370, 770)
        pdf.text(this.$t("billing.paymentInfo.bankAccountNr"), 470, 770)

        pdf.setFont("times", "normal")
        // Values
        pdf.text(this.paymentInfo.recipient, 45, 790)

        pdf.text(this.paymentInfo.street, 180, 790)
        pdf.text(this.paymentInfo.postcode, 180, 810)
        pdf.text(this.paymentInfo.city, 220, 810)

        pdf.text(this.paymentInfo.plusgiro, 270, 790)
        pdf.text(this.paymentInfo.bankgiro, 370, 790)
        pdf.text(this.paymentInfo.bankAccountNr, 470, 790)
      }
      pdf.setFontSize(12)
      const page2 = pdf.addPage()
      page2.text(
          this.$tc("billing.pdf.addendum"), 50, 90
      )

      page2.text(
          this.$tc("billing.pdf.addendumHelper") + this.chosenClub.name, 50, 120
      )

      page2.autoTable(
          {html: document.querySelector("#table2"),
            margin: {top: 130}}
      )
      pdf.save()
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