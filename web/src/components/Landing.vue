<template>
  <div>
    <main>
      <div class="container">
        <!-- Top section -->
        <div class="row" id="top">
          <div class="col-lg-4">
            <div class="text-div">
              <div>
                <p class="emphasized"> {{ $t("landing.heading.part1") }}</p>
              </div>
              <div v-if="isLoggedIn">
                <br>
                <p>{{$t("landing.heading.greeting", {username: user})}}</p>
                <br>
                <div>
                  <div class="form-group ">
                    <label for="competition-selection"> {{ getString("landing.heading.competitionChoice") }} </label>
                    <select name="competition-selection" id="competition-selection" class="form-control"
                            v-on:change="setCompetition" v-model="selectedCompetition">
                      <option value="none"> {{ getString("landing.heading.noCompetitionSelected") }}</option>
                      <option v-for="comp in competitions" v-bind:key="comp.id" :value="comp">
                        {{comp.name}}
                      </option>
                    </select>
                  </div>
                </div>
              </div>
              <div>
              </div>
              <div v-if="!isLoggedIn">
                <p class="second"> {{ $t("landing.heading.part2") }}</p>
                <div class="mb-3">
                  <label for="username" class="form-label">{{ getString("landing.heading.username") }}</label>
                  <input type="text" class="form-control" id="username" v-model="username" placeholder="">
                </div>
                <div class="mb-3">
                  <label for="password" class="form-label">{{ getString("landing.heading.password") }}</label>
                  <input type="password" class="form-control" id="password" v-model="password" placeholder="">
                </div>
                <button class="btn btn-outline-info" @click="login">{{getString("landing.heading.login")}}</button>
                <p v-if="loginFailed" class="text-danger">{{getString("landing.heading.loginFailed")}}</p>
              </div>
            </div>
          </div>
          <div class="col-lg-8">
            <div >
              <img src="@/assets/tennis.svg" class="img-fluid" alt="Tennis player">
            </div>
          </div>
        </div>
      </div>
      <!-- Middle sections -->
      <div id="middle" class="container-fluid">
        <div class="row">
        <div class="card-container col-sm">
          <div class="card">
            <img src="@/assets/organize_resume.svg" class="img-fluid" alt="Planning">
            <div class="card-body">
              <h3 class="card-title">{{ $t("landing.middle.organize.title") }}</h3>
              <p class="card-text">{{ $t("landing.middle.organize.text") }}</p>
            </div>
          </div>
        </div>
        <div class="card-container col-sm">
          <div class="card">
            <img src="@/assets/schedule.svg" class="img-fluid"  alt="Scheduling">
            <div class="card-body">
              <h3 class="card-title">{{ $t("landing.middle.host.title") }}</h3>
              <p class="card-text">{{ $t("landing.middle.host.text") }}</p>
            </div>
          </div>
        </div>
        <div class="card-container col-sm">
          <div class="card">
            <img src="@/assets/data_points.svg" class="img-fluid"  alt="Results">
            <div class="card-body">
              <h3 class="card-title">{{ $t("landing.middle.report.title") }}</h3>
              <p class="card-text">{{ $t("landing.middle.report.text") }}</p>
            </div>
          </div>
        </div>
        </div>
      <div id="bottom" class="row">
        <div class="col-lg-7">
          <img src="@/assets/good_team.svg" class="img-fluid" alt="Team work">
        </div>
        <div id="bottom-text" class="col-lg-5 text-lg-start">
          <p>{{ $t("landing.bottom.section1") }}</p>
          <ul>
            <li>{{ $t("landing.bottom.bullit1") }}</li>
            <li>{{ $t("landing.bottom.bullit2") }}</li>
            <li>{{ $t("landing.bottom.bullit3") }}</li>
          </ul>
        </div>
      </div>
      </div>
    </main>
  </div>
</template>

<script>
import UserService from "@/common/api-services/user.service";
import CompetitionService from "@/common/api-services/competition.service";

export default {
  name: "Landing",
  data() {
    return {
      username: "",
      password: "",
      loginFailed: false,
      selectedCompetition: "none",
      competitions: []
    }
  },
  computed : {
    isLoggedIn : function(){ return this.$store.getters.isLoggedIn},
    user: function(){ return this.$store.getters.user.username},
    competition: function(){ return this.$store.getters.competition},
  },
  mounted() {
    if (this.isLoggedIn) {
      CompetitionService.getCompetitions().then(res => {
        this.competitions = res.data
      })
    }
    if (this.competition === null) {
      this.selectedCompetition = "none"
    }
    else {
      this.selectedCompetition = this.competition
    }
  },
  created() {
    if (this.isLoggedIn) {
      CompetitionService.getCompetitions().then(res => {
        this.competitions = res.data
      })
    }
    if (this.competition === null) {
      this.selectedCompetition = "none"
    }
    else {
      this.selectedCompetition = this.competition
    }
  },
  methods: {
    getString(string) {
      return this.$t(string)
    },
    login() {
      this.username = "abraham"
      this.password = "anders"
        UserService.login(this.username, this.password).then(res => {
          console.log("login successful", res)
          this.loginFailed = false
          this.$store.commit("auth_success", res.data)
          UserService.getUser().then(res => {
            this.$store.commit("set_user", res.data)
          })
          // Fetch available competitions
          // TODO: ensure only competitions for the logged in user are sent back
        CompetitionService.getCompetitions().then(res => {
            this.competitions = res.data
          })
        })
      .catch(err => {
        console.log("Login failed", err)
        this.loginFailed = true
      })
    },
    setCompetition() {
        if (this.selectedCompetition === "none") {
          this.$store.commit("set_competition", null)
        }
        else {
          this.$store.commit("set_competition", this.selectedCompetition)
        }
    }
  }
}
</script>

<style scoped>

.emphasized {
  font-size: 40px;
}

.second {
  font-size: 24px;
}

/* Middle */
#middle {
  margin-top: 80px;
}

.card-container {
  background-color: var(--background2);
  font-size: 24px;
  padding: 50px;
}



.card {
  min-height: 350px;
  background: white !important;
}

.card-body {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.card h3 {
  font-weight: bolder;
}

#middle div img {
  margin: 30px auto 30px auto;
  max-width: 40%;
}

#bottom {
  margin-top: 30px;
}
</style>