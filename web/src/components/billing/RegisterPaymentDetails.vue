<template>
  <main class="container-fluid">
    <h1 class="p-4">{{ $t("billing.paymentInfo.title") }} </h1>
    <p class="p-4 w-50 m-auto">{{ $t("billing.paymentInfo.helperText") }}</p>
    <div v-if="paymentInfo !== null" class="bg-grey p-5">
      <div class="row justify-content-center">
        <div class="col-sm-7">
          <div class="form-group mb-4">
            <div class="d-flex align-items-center mb-2">
              <label for="recipient" class="form-label mb-0">{{ $t("billing.paymentInfo.recipient") }}</label>
            </div>
            <input type="text" class="form-control" id="recipient" v-model="paymentInfo.recipient">
          </div>
        </div>
        <div class="col-sm-7">
          <div class="form-group mb-4">
            <div class="d-flex align-items-center mb-2">
              <label for="street" class="form-label mb-0">{{ $t("billing.paymentInfo.street") }}</label>
            </div>
            <input type="text" class="form-control" id="street" v-model="paymentInfo.street">
          </div>
        </div>
      </div>
      <div class="row justify-content-center py-2 ">
        <div class="col-sm-2">
          <div class="form-group mb-4">
            <div class="d-flex align-items-center mb-2">
              <label for="postcode" class="form-label mb-0">{{ $t("billing.paymentInfo.postcode") }}</label>
            </div>
            <input type="text" class="form-control" id="postcode" v-model="paymentInfo.postcode">
          </div>
        </div>
        <div class="col-sm-5">
          <div class="form-group mb-4">
            <div class="d-flex align-items-center mb-2">
              <label for="city" class="form-label mb-0">{{ $t("billing.paymentInfo.city") }}</label>
            </div>
            <input type="text" class="form-control" id="city" v-model="paymentInfo.city">
          </div>
        </div>
      </div>
      <div class="row justify-content-center">
        <div class="col-sm-3">
          <div class="form-group mb-4">
            <div class="d-flex align-items-center mb-2">
              <label for="plusgiro" class="form-label mb-0">{{ $t("billing.paymentInfo.plusgiro") }}</label>
            </div>
            <input type="text" class="form-control" id="plusgiro" v-model="paymentInfo.plusgiro">
          </div>
        </div>
        <div class="col-sm-3">
          <div class="form-group mb-4">
            <div class="d-flex align-items-center mb-2">
              <label for="bankgiro" class="form-label mb-0">{{ $t("billing.paymentInfo.bankgiro") }}</label>
            </div>
            <input type="text" class="form-control" id="bankgiro" v-model="paymentInfo.bankgiro">
          </div>
        </div>
        <div class="col-sm-3">
          <div class="form-group mb-4">
            <div class="d-flex align-items-center mb-2">
              <label for="bank-account" class="form-label mb-0">{{ $t("billing.paymentInfo.bankAccountNr") }}</label>
            </div>
            <input type="text" class="form-control" id="bank-account" v-model="paymentInfo.bankAccountNr">
          </div>
        </div>
        <div class="d-flex justify-content-end px-1">
          <button class="btn btn-primary" type="button" @click="updatePaymentInfo">{{$t("general.save")}}</button>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
import ClubService from "@/common/api-services/club.service";

export default {
  name: "RegisterPaymentDetails",
  data() {
    return {
      paymentInfo: null,
    }
  },
  computed: {
    competition: function () {
      return this.$store.getters.competition
    }
  },
  mounted() {
    ClubService.getPaymentInfo(this.competition.organizerId).then(res => {
      this.paymentInfo = res.data
    }).catch(() => {
      this.$toasted.error(this.$tc("billing.paymentInfo.toastErrorFetch")).goAway(7000)
    })
  },
  methods: {
    updatePaymentInfo() {
      ClubService.updatePaymentInfo(this.competition.organizerId, this.paymentInfo.id, this.paymentInfo).then(() => {
        this.$toasted.success(this.$tc("billing.paymentInfo.toastSaveSuccess")).goAway(3000)
      }).catch(() => {
        this.$toasted.error(this.$tc("billing.paymentInfo.toastErrorUpdate")).goAway(7000)
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