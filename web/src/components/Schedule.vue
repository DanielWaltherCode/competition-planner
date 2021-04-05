<template>
  <div>
    <div class="container-fluid">
      <div class="row gx-5">

        <!-- Sidebar -->
        <div class="sidebar col-md-3">
          <br>
          <ul class="list-group list-group-flush">
            <li class="list-group-item"
                :class="sidebarChoice === 'overview' ? 'active' : ''"
                @click="sidebarChoice = 'overview'"> {{ $t("schedule.sidebar.overview") }}
            </li>
            <li class="list-group-item"
                :class="sidebarChoice === 'advanced' ? 'active' : ''"
                @click="sidebarChoice = 'advanced'"> {{ $t("schedule.sidebar.advanced") }}
            </li>
          </ul>
        </div>

        <!-- Main content -->
        <div id="main" class="col-md-9">
          <h1> {{ $t("schedule.main.heading") }}</h1>
          <div id="date-selector" class="row">
            <h4> {{ $t("schedule.main.competitionDays") }}</h4>
            <div class="d-flex col-md-8 m-auto">
              <p v-for="day in days" :key="day" class="day"
                 @click="setDay(day)" :class="selectedDay.getDate() === day.getDate() ? 'date-chosen' : ''">
                {{ formattedDate(day) }}</p>
            </div>
          </div>
          <div v-if="sidebarChoice === 'overview'">
            <div id="classes" class="row">
              <div id="table-container" class="col-sm-6">
                <table class="table table-bordered">
                  <thead>
                  <tr>
                    <th scope="col">{{ $t("schedule.main.category") }}</th>
                    <th scope="col">{{ $t("schedule.main.startTime") }}</th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr>
                    <td></td>
                    <td></td>
                  </tr>
                  </tbody>
                </table>
              </div>
                <div class="form-group col-sm-5 m-auto">
                  <label for="competition-selection"> {{ $t("schedule.main.addCategory")}} </label>
                  <select name="competition-selection" id="competition-selection" class="form-control"
                          v-on:change="setCompetition" v-model="selectedCompetition">
                    <option value="none"> {{ $t("schedule.main.dropdownPlaceholder") }}</option>
                    <option v-for="category in competitionCategories" v-bind:key="category.id" :value="category">
                      {{ category.categoryName }}
                    </option>
                  </select>
                </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {getFormattedDate} from "@/common/util";
import DrawService from "@/common/api-services/draw.service";

export default {
  name: "Schedule",
  data() {
    return {
      sidebarChoice: "overview",
      days: [],
      selectedDay: "",
      competitionCategories: [],
    }
  },
  computed: {
    competition: function () {
      return this.$store.getters.competition
    }
  },
  mounted() {
    this.days = this.getDays()
    this.selectedDay = this.days[0]
    DrawService.getCompetitionCategories(this.competition.id).then(res => {
      this.competitionCategories = res.data.categories
    })
  },
  methods: {
    // Returns all days of competition
    getDays() {
      let currentDate = new Date(this.competition.startDate)
      const endDate = new Date(this.competition.endDate)
      const days = []

      console.log(currentDate)
      console.log(endDate)
      while (currentDate <= endDate) {
        days.push(new Date(currentDate));
        currentDate.setDate(currentDate.getDate() + 1)
        console.log(days)
      }
      return days;
    },
    formattedDate: getFormattedDate,
    setDay(day) {
      console.log("Setting day to " + day)
      this.selectedDay = day;
    }
  }
}
</script>

<style scoped>

#main h1 {
  text-align: left;
  margin-bottom: 30px;
}

#main h4 p {
  margin-bottom: 30px;
}

.day {
  margin-right: 15px;
}

.date-chosen {
  color: #0245bc;
  text-decoration: underline;
}

.day:hover {
  cursor: pointer;
}
</style>