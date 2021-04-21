<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated class="bg-primary text-black text-bold" height-hint="98">
      <q-toolbar>
        <q-toolbar-title class="bg-primary text-black text-bold">
          <div class="row">
          <div class="bnetlogo" style="cursor: pointer;" @click="redirectToBNet()">
            <img class="logo_qmenu" src="statics/img_sprite.png" />
          </div>
          <label class="logolbl">
            IMEI Reservierung
          </label>
          </div>
        </q-toolbar-title>
        <div class="column" style="margin-top: 0px;">
          <div class="row justify-end" style="max-width: 200px; word-break: break-all;">
          <div>
            <label>{{customerName}}</label>
          </div>
          </div>
        </div>
      </q-toolbar>
      <q-tabs id="tab-view" v-model="selectedTab" inline-label class="text-secondary no-shadow" align="left">
        <q-route-tab to="/managereservation" label="Verwalten" name="manageReservation" />
        <q-route-tab to="/imeihistory" label="IMEI Historie" name="imeiHistory" />
      </q-tabs>
    </q-header>
    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
import { openURL } from 'quasar'
import { mapGetters } from 'vuex'

export default {
  name: 'MyLayout',
  data () {
    return {
      selectedTab: 'reservation'
    }
  },
  computed: {
    customerName: function () {
      return this.getUser().fullname ? this.getUser().fullname : this.getUser().firstname
    }
  },
  mounted () {
    let brocloakTokenData = this.getUser()
    Window.log.debug(brocloakTokenData)

    if (!brocloakTokenData) {
      this.showInvaildTokenDataMessage(this)
    }
  },
  methods: {
    ...mapGetters('brocloakStore', [
      'getUser'
    ]),
    openURL
  }
}
</script>

<style>
</style>
