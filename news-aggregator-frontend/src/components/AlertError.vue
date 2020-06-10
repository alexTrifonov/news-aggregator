<template>
  <div>
    <b-alert
      :show="showAlert"
      variant="danger"
      dismissible
      fade
      @dismissed="showAlert=false"
    >
      {{ message }}
    </b-alert>
    
  </div>
</template>

<script>
export default {
  name: "Alert",
  props: {
      message: String,
      showAlert: Boolean
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
