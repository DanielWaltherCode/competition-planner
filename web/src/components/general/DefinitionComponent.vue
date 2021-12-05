<template>
<div class="d-flex text-start">
  <p id="word" class="fw-bolder">{{word}}</p>
  <p id="explanation">{{explanation}}</p>
</div>
</template>

<script>
export default {
  name: "DefinitionComponent",
  props: ["word", "explanation"]
}
</script>

<style scoped>
#word {
  width: 20%;
  margin-right: 30px;
}

#explanation {
  width: 70%;
  margin-left: 10px;
}
</style>