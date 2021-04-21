<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated class="bg-primary text-black fa-bold" height-hint="98">
      <q-toolbar>

        <q-toolbar-title>
          <q-avatar class="custom-logo">
            <img src="https://brodos.net/omnichannel/wp-content/uploads/2018/07/bnet_logo.png">
          </q-avatar>
          IMEIReservation
        </q-toolbar-title>
        <div>Back to brodosnet</div>
        </q-toolbar>
      <q-tabs align="left">
        <q-route-tab to="/page1" label="Page One" />
        <q-route-tab to="/page2" label="Page Two" />
        <q-route-tab to="/page3" label="Page Three" />
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
