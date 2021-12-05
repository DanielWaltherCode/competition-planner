<template>
  <div>
    <main>
      <!-- ======= Top section (called "hero" for some reason) ======= -->
      <section id="hero" class="hero d-flex align-items-center">
        <div class="container">
          <div class="row">
            <div class="col-lg-6 d-flex flex-column justify-content-center">
              <h1 v-if="!isLoggedIn" data-aos="fade-up" >{{ $t("landing.heading.part1") }}</h1>
              <h2 v-if="isLoggedIn"> {{ getString("landing.heading.loggedInHelper") }}</h2>
              <div v-if="!isLoggedIn">
                <p data-aos="fade-up" data-aos-delay="400">{{ $t("landing.heading.part2") }}</p>
                <br/>
                <h2 class="mt-2 fw-bolder"> {{getString("landing.heading.login")}}</h2>
                <div class="mb-3">
                  <label for="username" class="form-label">{{ getString("landing.heading.username") }}</label>
                  <input type="text" class="form-control" id="username" v-model="username" placeholder="">
                </div>
                <div class="mb-3">
                  <label for="password" class="form-label">{{ getString("landing.heading.password") }}</label>
                  <input type="password" class="form-control" id="password" v-model="password" placeholder="">
                </div>
                <button
                    class="btn-get-started d-inline-flex align-items-center justify-content-center align-self-center"
                    @click="login">
                  <span>{{ getString("landing.heading.login") }} </span>
                  <i class="bi bi-arrow-right"></i>
                </button>
                <p v-if="loginFailed" class="text-danger">{{ getString("landing.heading.loginFailed") }}</p>
              </div>
              <div v-if="isLoggedIn">
                <!-- If logged in, either choose an existing competition or set up a new one -->
                <br>
                <div>
                  <div class="form-group">
                    <label class="form-label d-flex justify-content-start ms-1" for="competition-selection"> {{ getString("landing.heading.competitionChoice") }} </label>
                    <select id="competition-selection" class="form-control"
                            v-on:change="setCompetition" v-model="selectedCompetition">
                      <option value="none"> {{ getString("landing.heading.noCompetitionSelected") }}</option>
                      <option v-for="comp in competitions" v-bind:key="comp.id" :value="comp">
                        {{ comp.name }}
                      </option>
                    </select>
                  </div>
                </div>
                <div class="d-flex justify-content-end">
                  <button
                      class="btn btn-info mt-5"
                      @click="$router.push('/new-competition')">
                    <span>{{ getString("header.handle.newCompetition") }} </span>
                    <i class="bi bi-arrow-right"></i>
                  </button>
                    <button
                        v-if="selectedCompetition !== 'none'"
                        class="btn btn-light ms-2 mt-5"
                        @click="$router.push('/overview')">
                      <span>{{ getString("landing.heading.handleSelected") }} </span>
                      <i class="bi bi-arrow-right"></i>
                    </button>
                </div>
              </div>
            </div>
            <div class="col-lg-6 hero-img" data-aos="zoom-out" data-aos-delay="200">
              <img src="@/assets/tennis.svg" class="img-fluid" alt="">
            </div>
          </div>
          <div class="row">


          </div>
        </div>

      </section><!-- End Hero -->
      <section id="values" class="values">

        <div class="container" data-aos="fade-up">

          <header class="section-header">
            <h2>{{$t("landing.middle.title")}}</h2>
            <p>{{$t("landing.middle.subtitle")}}</p>
          </header>

          <div class="row">

            <div class="col-lg-4">
              <div class="box" data-aos="fade-up" data-aos-delay="200">
                <img src="@/assets/organize_resume.svg" class="img-fluid" alt="">
                <h3>{{ $t("landing.middle.organize.title") }}</h3>
                <p>{{ $t("landing.middle.organize.text") }}</p>
              </div>
            </div>

            <div class="col-lg-4 mt-4 mt-lg-0">
              <div class="box" data-aos="fade-up" data-aos-delay="400">
                <img src="@/assets/schedule.svg" class="img-fluid" alt="">
                <h3>{{ $t("landing.middle.host.title") }}</h3>
                <p>{{ $t("landing.middle.host.text") }}</p>
              </div>
            </div>

            <div class="col-lg-4 mt-4 mt-lg-0">
              <div class="box" data-aos="fade-up" data-aos-delay="600">
                <img src="@/assets/data_points.svg" class="img-fluid" alt="">
                <h3>{{ $t("landing.middle.report.title") }}</h3>
                <p>{{ $t("landing.middle.report.text") }}</p>
              </div>
            </div>
          </div>
        </div>

      </section>
      <section id="features" class="features">

        <div class="container" data-aos="fade-up">

          <header class="section-header">
            <h2>Features</h2>
            <p>{{ $t("landing.bottom.section1") }}</p>
          </header>

          <div class="row">

            <div class="col-lg-6">
              <img src="@/assets/features.png" class="img-fluid" alt="">
            </div>

            <div class="col-lg-6 mt-5 mt-lg-0 d-flex">
              <div class="row align-self-center gy-4">

                <div class="col-md-6" data-aos="zoom-out" data-aos-delay="200">
                  <div class="feature-box d-flex align-items-center">
                    <i class="bi bi-check"></i>
                    <h3>{{ $t("landing.bottom.bullit1") }}</h3>
                  </div>
                </div>

                <div class="col-md-6" data-aos="zoom-out" data-aos-delay="300">
                  <div class="feature-box d-flex align-items-center">
                    <i class="bi bi-check"></i>
                    <h3>{{ $t("landing.bottom.bullit2") }}</h3>
                  </div>
                </div>

                <div class="col-md-6" data-aos="zoom-out" data-aos-delay="400">
                  <div class="feature-box d-flex align-items-center">
                    <i class="bi bi-check"></i>
                    <h3>{{ $t("landing.bottom.bullit3") }}</h3>
                  </div>
                </div>

                <div class="col-md-6" data-aos="zoom-out" data-aos-delay="500">
                  <div class="feature-box d-flex align-items-center">
                    <i class="bi bi-check"></i>
                    <h3>{{ $t("landing.bottom.bullit1") }}</h3>
                  </div>
                </div>

              </div>
            </div>
          </div>
        </div>
      </section>
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
  computed: {
    isLoggedIn: function () {
      return this.$store.getters.isLoggedIn
    },
    user: function () {
      return this.$store.getters.user.username
    },
    competition: function () {
      return this.$store.getters.competition
    },
  },
  mounted() {
    if (this.isLoggedIn) {
      CompetitionService.getCompetitions().then(res => {
        this.competitions = res.data
      })
    }
    if (this.competition === null) {
      this.selectedCompetition = "none"
    } else {
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
    } else {
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
      } else {
        this.$store.commit("set_competition", this.selectedCompetition)
      }
    }
  }
}
</script>

<style scoped>

.new-competition {
  margin-top: 20px;
  margin-bottom: 0;
}

.card h3 {
  font-weight: bolder;
}

#middle div img {
  margin: 30px auto 30px auto;
  max-width: 40%;
}

</style>