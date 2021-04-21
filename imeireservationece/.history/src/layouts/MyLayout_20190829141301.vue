<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated class="bg-primary text-black text-bold" height-hint="98">
      <q-toolbar>

        <q-toolbar-title class="bg-primary text-black text-bold">
          <q-avatar class="custom-logo">
            <img src="https://brodos.net/omnichannel/wp-content/uploads/2018/07/bnet_logo.png">
          </q-avatar>
          IMEIReservation
        </q-toolbar-title>
        <div>Back to brodosnet</div>
        </q-toolbar>
      <q-tabs align="left">
        <q-route-tab to="/page1" label="Reservation" />
        <q-route-tab to="/page2" label="Manage Reservation" />
        <q-route-tab to="/page3" label="IMEIHistory" />
      </q-tabs>
    </q-header>
    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
import { openURL } from 'quasar'

export default {
  name: 'MyLayout',
  data () {
    return {
      leftDrawerOpen: false
    }
  },
  methods: {
    openURL
  }
}
</script>

<style>

</style>
