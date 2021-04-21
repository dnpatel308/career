<template>
  <q-page padding>
    <!-- content -->
  <div v-if="needToLoad" class="q-pa-md">
     <div class="tabelsearch row justify-end" style="margin-bottom: -50px; z-index: 1000; position: relative;">
      <q-input debounce="300" color="primary" v-model="filter" style="max-width: 200px; margin-bottom: 10px; margin-right: 10px;">
      <template v-slot:append>
        <q-icon name="search" />
      </template>
      </q-input>
    </div>

    <q-table
      class="my-sticky-header-table"
      title="IMEI Verwaltung"
      :data="data"
      no-data-label="Keine Daten verfügbar"
      rows-per-page-label="Einträge pro Seite"
      no-results-label="Keine Daten verfügbar"
      :columns="columns"
      :filter="filter"
      :pagination.sync="pagination"
      :rows-per-page-options="rowsOptions"
      row-key="position"
      flat
      bordered>
      <q-td slot="body-cell-action" slot-scope="props" :props="props">
        <q-btn no-caps size="sm" v-if="props.row.type === 'RESERVATION' && props.row.reservationStatus !== 'REQUESTFORSENTOUT'" v-on:click="handleRequestForSentOut(props.row)" color="positive">Versenden</q-btn>
        <q-btn no-caps size="sm" v-if="props.row.reservationStatus !== 'REQUESTFORSENTOUT'" class="q-ml-xs" color="negative" v-on:click="handleRequestForcancellation(props.row)">Storno</q-btn>
        <span v-if="props.row.reservationStatus === 'REQUESTFORSENTOUT'" class="label"><b>Versand wird vorbereitet</b></span>
      </q-td>
    </q-table>
    <div v-if="needToLoadLimit" class="row absolute limits" style="margin-top: -35px; margin-left: 10px;">
      <div class="limit_key">
        <label>Summe aller Reservierungen:</label>
      </div>
      <div class="limit_val">
        <div style="width: 75px; text-align: right; margin-right: 10px; background: white;">{{consumedLimit}} Stück&nbsp;&nbsp;</div>
      </div>
      <div class="limit_key">
        <label>Limit:</label>
      </div>
      <div class="limit_val">
        <div style="width: 75px; text-align: right; margin-right: 10px; background: white;">{{limit}} Stück&nbsp;&nbsp;</div>
      </div>
      <div class="limit_key">
        <label>Verfügbar:</label>
      </div>
      <div class="limit_val">
        <div style="width: 75px; text-align: right; margin-right: 10px; background: white;">{{remainingLimit}} Stück&nbsp;&nbsp;</div>
      </div>
    </div>
  </div>
  </q-page>
</template>

<script>

import { Notify } from 'quasar'
import RequestForSentOutPopup from '../components/RequestForSentOutPopup'
import { mapGetters, mapActions } from 'vuex'

export default {
  beforeCreate: function () {
    this.needToLoad = true
  },
  mounted: function () {
    let brocloakTokenData = this.getUser()
    this.customerNo = brocloakTokenData.customerno

    if (this.customerNo) {
      Window.log.debug(this.customerNo)
      Window.log.debug(this.$axios.defaults.headers)
      this.getReservationAndOpenCasesAPI()
    }
  },
  data () {
    return {
      columns: this.getManageReservationColumns(),
      data: this.sortedData,
      filter: '',
      needToLoadLimit: this.needToLoadLimit,
      limit: 0,
      consumedLimit: 0,
      remainingLimit: 0,
      rowsOptions: [3, 5, 7, 10, 15, 25, 50, 100],
      pagination: {
        rowsPerPage: 15
      }
    }
  },
  methods: {
    ...mapGetters('brocloakStore', [
      'getUser'
    ]),
    ...mapGetters('imeireservationeceStore', [
      'getReservationsAndOpenCases',
      'getPosition',
      'getNeedToLoadLimit',
      'getLimit',
      'getConsumedLimit',
      'getRemainingLimit'
    ]),
    ...mapActions('imeireservationeceStore', [
      'fetchReservationsAndOpenCasesData',
      'updateReservationsAndOpenCases',
      'updatePosition',
      'updateImei',
      'updateEan',
      'removePosition'
    ]),

    getReservationAndOpenCasesAPI () {
      this.$q.loading.show()
      let result = this.fetchReservationsAndOpenCasesData()
      result.then(data => {
        this.data = data
        this.needToLoadLimit = this.getNeedToLoadLimit()
        this.limit = this.getLimit()
        this.consumedLimit = this.getConsumedLimit()
        this.remainingLimit = this.getRemainingLimit()
        this.$q.loading.hide()
      }).catch(err => {
        Window.log.debug(err)
        this.$q.loading.hide()
        // this.showGenericRequestFailureMessage(this, err)
      })
    },

    handleRequestForSentOut (obj) {
      this.updatePosition(obj.position)
      this.updateImei(obj.imei)
      this.updateEan(this.data[obj.position - 1].ean)

      this.$q.dialog({
        component: RequestForSentOutPopup,
        parent: this,
        text: 'something'
      }).onOk(() => {
        this.data = this.getReservationsAndOpenCases()
      })
    },

    handleRequestForcancellation (obj) {
      let title = 'Hiermit wird die IMEI storniert und ist nicht mehr für dich reserviert.'
      // let vertriebspartnervertraglink = 'https://www.brodos.net/index.php?id=wiki:service:ablaufe:formulare&s[]=%2Avertriebspartnervertrag%2A'
      let vertriebspartnervertraglink = 'https://www.brodos.net/index.php/mpath/home_default?id=wiki:service:ablaufe:formulare&s[]=%2Avertriebspartnervertrag%2A'
      let agblink = 'http://www.brodos.com/group/agb/'
      let message = 'Mit Klick auf den Button bestätigst du den <a target="_blank" style="word-break: break-all" href=' + vertriebspartnervertraglink + '>Vertriebspartnervertrag</a> und die <a target="_blank" href=' + agblink + '>AGBs</a> gelesen und akzeptiert zu haben.<br /><br />Wenn du erneut eine IMEI für diesen Artikel benötigst, führe eine weitere Reservierung durch.'

      if (obj.type === 'OPENCASE') {
        title = 'Reservierungsanfrage stornieren.'
        message = 'Möchtest du die offene Anfrage wirklich stornieren?'
      }

      this.$q.dialog({
        title: title,
        message: message,
        html: true,
        style: 'width: 800px;',
        ok: {
          push: true,
          size: 'md',
          color: 'negative',
          label: 'Stornieren',
          noCaps: true
        },
        cancel: {
          class: 'abc',
          push: true,
          size: 'md',
          color: 'gray',
          label: 'Abbrechen',
          noCaps: true
        }
      }).onOk(() => {
        this.$q.loading.show()
        let callback = () => {
          this.updatePosition(obj.position)
          this.updateImei(obj.imei)
          this.updateEan(this.data[obj.position - 1].ean)

          if (obj.type === 'RESERVATION') {
            this.callReservationCancellationAPI(obj)
          } else if (obj.type === 'OPENCASE') {
            this.callOpenCaseCancellationAPI(obj)
          }
        }

        setTimeout(callback, 500)
      })
    },

    callReservationCancellationAPI (obj) {
      let reqBody = {}

      reqBody.tenantId = '1'
      reqBody.ticketNumber = obj.ticketno
      reqBody.imei = obj.imei

      Window.log.debug(this.$axios.defaults.headers)
      this.$axios.put('/reservationcancellation', JSON.stringify(reqBody))
        .then(responseJSON => {
          this.$q.loading.hide()
          Notify.create({
            message: 'Die IMEI Reservierung wurde erfolgreich storniert.',
            position: 'center',
            timeout: 5000
          })

          this.removePosition(this.getPosition())
          this.data = this.getReservationsAndOpenCases()

          this.consumedLimit = this.consumedLimit - 1
          this.remainingLimit = this.limit - this.consumedLimit
          Window.log.debug(this.limit)
          Window.log.debug(this.consumedLimit)
          Window.log.debug(this.remainingLimit)
        }).catch((err) => {
          this.showGenericRequestFailureMessage(this, err)
        }
        )
    },

    callOpenCaseCancellationAPI (obj) {
      let reqBody = {}

      reqBody.tenantId = '1'
      reqBody.importStatus = 'CANCELLED'
      reqBody.cancellationComment = 'Canceled by ' + (this.getUser().fullname ? this.getUser().fullname : this.getUser().firstname)

      Window.log.debug(this.$axios.defaults.headers)
      this.$axios.put('/serialnumberimportticketrefs/' + obj.ticketno, JSON.stringify(reqBody))
        .then(responseJSON => {
          this.$q.loading.hide()
          Notify.create({
            message: 'Deine Reservierungsanfrage wurde erfolgreich storniert.',
            position: 'center',
            timeout: 5000
          })

          this.removePosition(this.getPosition())
          this.data = this.getReservationsAndOpenCases()

          this.consumedLimit = this.consumedLimit - 1
          this.remainingLimit = this.limit - this.consumedLimit
          Window.log.debug(this.limit)
          Window.log.debug(this.consumedLimit)
          Window.log.debug(this.remainingLimit)
        })
        .catch((err) => {
          this.showGenericRequestFailureMessage(this, err)
        }
        )
    }
  }
}
</script>

<style>
</style>
