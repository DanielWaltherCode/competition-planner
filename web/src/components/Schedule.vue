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
          <div v-if="sidebarChoice === 'overview'">
            <h2>{{$t("schedule.main.categoryStartTimes")}}</h2>
            <div id="classes" class="row">
              <div id="table-container" class="col-sm-12 m-auto">
                <table class="table table-bordered">
                  <thead>
                  <tr>
                    <th scope="col">{{ $t("schedule.main.category") }}</th>
                    <th scope="col">{{ $t("schedule.main.day") }}</th>
                    <th scope="col">{{ $t("schedule.main.timeSpan") }}</th>
                    <th scope="col">{{ $t("schedule.main.startTime") }}</th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr v-for="category in categoryStartTimeDTO.categoryStartTimeList" :key="category.id">
                    <td>{{category.categoryDTO.categoryName}}</td>
                    <!-- Select date -->
                    <td>
                      <select id="date-selection" class="form-control"
                              v-on:change="setStartTime(category.id)" v-model="category.playingDay">
                        <option value="null"> {{ $t("schedule.main.notSelected") }}</option>
                        <option v-for="(day, counter) in categoryStartTimeDTO.startTimeFormOptions.availableDays" v-bind:key="counter" :value="getPlayingDate(day)">
                          {{ getPlayingDate(day) }}
                        </option>
                      </select>
                    </td>
                    <!-- Select interval during day -->
                    <td>
                      <select id="interval-selection" class="form-control"
                              v-on:change="setStartTime(category.id)" v-model="category.startInterval">
                        <option v-for="(interval, counter) in categoryStartTimeDTO.startTimeFormOptions.startIntervals" v-bind:key="counter" :value="interval">
                          {{ getInterval(interval) }}
                        </option>
                      </select>
                    </td>
                    <!-- Select exact start time -->
                    <td>
                      <vue-timepicker v-model="category.exactStartTime" v-on:change="setStartTime(category.id)"></vue-timepicker>
                    </td>
                  </tr>
                  </tbody>
                </table>
              </div>
              <!--<div class="form-group col-sm-5 m-auto">
                <label for="category-selection"> {{ $t("schedule.main.addCategory") }} </label>
                <select name="category-selection" id="category-selection" class="form-control"
                        v-on:change="addCategory" v-model="selectedCategory">
                  <option value="none"> {{ $t("schedule.main.dropdownPlaceholder") }}</option>
                  <option v-for="category in competitionCategories" v-bind:key="category.id" :value="category">
                    {{ category.categoryName }}
                  </option>
                </select>
              </div>-->
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {getFormattedDate, getHoursMinutes} from "@/common/util";
import VueTimepicker from 'vue2-timepicker/src/vue-timepicker.vue'
import DrawService from "@/common/api-services/draw.service";
import CategoryStartTimeService from "@/common/api-services/schedule/category-start-time.service";

export default {
  name: "Schedule",
  components: { VueTimepicker },
  data() {
    return {
      sidebarChoice: "overview",
      selectedCategory: "",
      competitionCategories: [],
      categoryStartTimeDTO: [],
    }
  },
  computed: {
    competition: function () {
      return this.$store.getters.competition
    }
  },
  mounted() {
    DrawService.getCompetitionCategories(this.competition.id).then(res => {
      this.competitionCategories = res.data.categories
    })
    this.getCategoryStartTimes()
  },
  methods: {
    getPlayingDate(date) {
      if (date === null) {
        return this.$t("schedule.main.notSelected")
      }
      else {
        return this.formattedDate(date)
      }
    },
    getInterval(interval) {
      return this.$t("schedule.main.intervals." + interval)
    },
    getExactStartTime(time) {
      if (time === null || time.HH === "" || time.mm === "") {
        return null
      }
      else if(typeof time === "object") {
        return `${time.HH}:${time.mm}`
      }
      else {
        return time
      }
    },
    getCategoryStartTimes() {
      // Category id is not used in backend for this call so can be set to anything
      CategoryStartTimeService.getCategoryStartTimesInCompetition(this.competition.id,
          5).then(res => {
            this.categoryStartTimeDTO = res.data
      })
    },
    formattedDate: getFormattedDate,
    formatTime: getHoursMinutes,
    setStartTime(startTimeId) {
      const updatedStartTime = this.categoryStartTimeDTO.categoryStartTimeList.find(object => object.id === startTimeId)
      const exactStartTime = this.getExactStartTime(updatedStartTime.exactStartTime)
      const objectToSave = {
        playingDay: updatedStartTime.playingDay === "null" ? null : updatedStartTime.playingDay,
        startInterval: updatedStartTime.startInterval,
        exactStartTime: exactStartTime,
      }
      console.log("Sending object to save: ", objectToSave)
      CategoryStartTimeService.updateCategoryStartTime(startTimeId, this.competition.id, updatedStartTime.categoryDTO.categoryId,  objectToSave)
    }
  }
}
</script>

<style scoped>

#main {
  margin-top: 20px;
}

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