<template>
  <div id="app">
    <Header />
    <router-view id="router"></router-view>
  </div>
</template>

<script>
import Header from "@/components/Header";

export default {
  name: 'App',
  components: {
    Header,
  }
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Open+Sans:wght@400;700&display=swap');

#app {
  font-family: 'Open Sans', sans-serif;;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  font-weight: 400;
  text-align: center;
  color: black;
  font-size: 1em;
}

h1, h2, h3 {
  color: #012970;
}

/* Global variables */
:root {
  --emphasis-color: #1190cb;
  --main-blue: #4154f1;
  --background2: #edf4fc;
}

.emphasized {
  color: var(--emphasis-color)
}

.btn-primary {
  background-color: var(--emphasis-color) !important;
}

.btn-outline-primary:hover {
  background-color: var(--emphasis-color) !important;
}

.btn-outline-primary {
  border: 1px solid var(--emphasis-color) !important;
}

/* Sidebar styling */
.sidebar-header {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 10px 0 30px 0;
  color: white;
}

.sidebar {
  min-height: 100vh;
  z-index: 0;
  background: #404040;
  margin-top: -18px;
  box-shadow: 3px 3px 2px 1px #efefef;
}

.sidebar .active {
  background-color: var(--emphasis-color) !important;
}

@media only screen and (max-width: 768px) {
  .sidebar {
    min-height: fit-content;
  }
}

.sidebar li {
  background-color: inherit;
  color: white;
}

.sidebar-header:hover {
  cursor: pointer;
}

.sidebar li:hover {
  opacity: 0.7;
}

.sidebar li:hover {
  cursor: pointer;
}
</style>
