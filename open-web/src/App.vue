<template>
  <div id="app">
    <router-view id="router"></router-view>
  </div>
</template>

<script>

export default {
  name: 'App',
  components: {
  }
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Bebas+Neue&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Open+Sans:wght@400;500;600;700&display=swap');

* {
  box-sizing: border-box;
}

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
  color: var(--main-color);
  margin-top: 20px !important;
  margin-bottom: 20px !important;
  text-align: center;
}

/* Global variables */
:root {
  --main-color: #012970;
  --yellow: #FFB657;
  --background: #F6EBFF;
  --sidebar-color: #F1F5FD;
  --grey-color: #F8F8F8;

  --clr-primary-50: hsl(221, 94%, 92%);;
  --clr-primary-100: hsl(221, 94%, 87%);
  --clr-primary-200: hsl(221, 94%, 77%);
  --clr-primary-300: hsl(221, 94%, 67%);
  --clr-primary-400: hsl(221, 94%, 57%);
  --clr-primary-500: hsl(221, 94%, 47%); /* This is Primary in Figma*/
  --clr-primary-600: hsl(221, 94%, 37%);
  --clr-primary-700: hsl(221, 94%, 27%);
  --clr-primary-800: hsl(221, 94%, 17%);
}


.emphasized {
  color: var(--main-color)
}

.btn-primary {
  background-color: var(--main-color) !important;
}

.btn-check:active+.btn-outline-primary, .btn-check:checked+.btn-outline-primary, .btn-outline-primary.active, .btn-outline-primary.dropdown-toggle.show, .btn-outline-primary:active{
  background-color: var(--main-color) !important;
  color: white !important;
}

.btn-outline-primary:hover {
  color: white !important;
  background-color: var(--main-color) !important;
}

.btn-outline-primary {
  color: var(--main-color) !important;
  border: 1px solid var(--main-color) !important;
}


/* Sidebar styling */
.sidebar-header {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 10px 0 30px 0;
  color: var(--main-color);
  font-weight: bold;
}

.sidebar {
  min-height: 100vh;
  z-index: 0;
  background: var(--sidebar-color);
  margin-top: -18px;
  box-shadow: 3px 3px 2px 1px #efefef;
}

.sidebar .active {
  background-color: var(--main-color) !important;
  border-color: 1px var(--main-color) !important;
}

@media only screen and (max-width: 768px) {
  .sidebar {
    min-height: fit-content;
  }
}

.sidebar li {
  background-color: inherit;
  color: black;
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
