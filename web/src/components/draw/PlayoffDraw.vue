<template>
  <main id="tournament">
    <ul class="round">

      <li class="spacer">&nbsp;</li>

      <li class="game game-top"></li>
      <li class="game game-spacer">&nbsp;</li>
      <li class="game game-bottom "></li>

      <li class="spacer">&nbsp;</li>
    </ul>
    <ul class="round round-2">
      <li class="spacer">&nbsp;</li>

      <li class="game game-top winner">Lousville <span>82</span></li>
      <li class="game game-spacer">&nbsp;</li>
      <li class="game game-bottom ">Colo St <span>56</span></li>

      <li class="spacer">&nbsp;</li>

      <li class="game game-top winner">Oregon <span>74</span></li>
      <li class="game game-spacer">&nbsp;</li>
      <li class="game game-bottom ">Saint Louis <span>57</span></li>

      <li class="spacer">&nbsp;</li>

      <li class="game game-top ">Memphis <span>48</span></li>
      <li class="game game-spacer">&nbsp;</li>
      <li class="game game-bottom winner">Mich St <span>70</span></li>

      <li class="spacer">&nbsp;</li>

      <li class="game game-top ">Creighton <span>50</span></li>
      <li class="game game-spacer">&nbsp;</li>
      <li class="game game-bottom winner">Duke <span>66</span></li>

      <li class="spacer">&nbsp;</li>
    </ul>

  </main>
</template>

<script>
export default {
  name: "PlayoffDraw",
  props: {
    playoff: Object
  }
}
</script>

<style scoped>
/*
 *  Flex Layout Specifics
*/
main{
  display:flex;
  flex-direction:row;
}
.round{
  display:flex;
  flex-direction:column;
  justify-content:center;
  width:200px;
  list-style:none;
  padding:0;
}
.round .spacer{ flex-grow:1; }
.round .spacer:first-child,
.round .spacer:last-child{ flex-grow:.5; }

.round .game-spacer{
  flex-grow:1;
}

/*
 *  General Styles
*/
body{
  padding:10px;
  line-height:1.4em;
}

li.game{
  padding-left:20px;
}

li.game.winner{
  font-weight:bold;
}
li.game span{
  float:right;
  margin-right:5px;
}

li.game-top{ border-bottom:1px solid #aaa; }

li.game-spacer{
  border-right:1px solid #aaa;
  min-height:40px;
}

li.game-bottom{
  border-top:1px solid #aaa;
}

</style>