<template>
  <div class="historyPage" style="padding: 40px;">
  <div class="row" style="margin-bottom: 18px;">
    <div class="historyInputRow">
      <label>IMEI:</label>
      <q-input class="searchimeihistory" style="max-height: 32px; min-width: 220px;" filled v-model="imei" v-on:keydown="handleSearchByImei"></q-input>
    </div>
    <q-space style="max-width: 50px; max-height: 50px;"></q-space>
    <div class="oder historyInputRow" style="margin-top: 25px;">
      <b>oder</b>
    </div>
    <q-space style="max-width: 50px; max-height: 50px;"></q-space>
    <div class="dates historyInputRow">
      <label>Von:</label>
      <q-input filled v-model="fromdate">
        <template v-slot:append>
          <q-icon name="event" class="cursor-pointer">
            <q-popup-proxy ref="qFromDateProxy" transition-show="scale" transition-hide="scale">
              <q-date mask="DD.MM.YYYY" :options="optionsFn" color="white" text-color="black" v-model="fromdate" @input="() => $refs.qFromDateProxy.hide()" />
            </q-popup-proxy>
          </q-icon>
        </template>
      </q-input>
    </div>
    <q-space style="max-width: 10px;"></q-space>
    <div class="todate historyInputRow">
    <label>Bis:</label>
     <q-input filled v-model="todate">
      <template v-slot:append>
        <q-icon name="event" class="cursor-pointer">
          <q-popup-proxy ref="qToDateProxy" transition-show="scale" transition-hide="scale">
            <q-date mask="DD.MM.YYYY" :options="optionsFn" color="white" text-color="black" v-model="todate" @input="() => $refs.qToDateProxy.hide()" />
          </q-popup-proxy>
        </q-icon>
      </template>
    </q-input>
    </div>
    <div class="historyOk">
      <q-space style="min-height: 20px; min-width: 10px;" />
      <q-btn style="min-height: auto; margin-left: 10px; max-height: 32px;" color="positive" label="OK" v-on:click="validate()"></q-btn>
    </div>
  </div>

  <div class="tabelsearch row justify-end" style="margin-bottom: -50px; z-index: 1000; position: relative;">
  <q-input debounce="300" color="primary" v-model="filter" style="max-width: 200px; margin-bottom: 10px; margin-right: 10px;">
     <template v-slot:append>
        <q-icon name="search" />
     </template>
  </q-input>
  </div>

  <div class="historyTable">
   <q-table
      class="my-sticky-header-table"
      title="IMEI Historie"
      no-data-label="Keine Daten verfügbar"
      rows-per-page-label="Einträge pro Seite"
      no-results-label="Keine Daten verfügbar"
      :data="data"
      :columns="columns"
      :filter="filter"
      :rows-per-page-options="rowsOptions"
      row-key="name"
      flat
      bordered
    ></q-table>
    </div>
    </div>
</template>

<script>
import { date } from 'quasar'
import { mapGetters, mapActions } from 'vuex'

export default {
  mounted: function () {
    let brocloakTokenData = this.getUser()
    this.customerNo = brocloakTokenData.customerno
    Window.log.debug(this.customerNo)
    Window.log.debug(this.$axios.defaults.headers)
    this.getIMEIHistoryData()
  },
  data () {
    return {
      rowsOptions: [3, 5, 7, 10, 15, 25, 50, 100],
      columns: this.getImeiHistoryColumns(),
      data: this.getIMEIHistoryData(),
      fromdate: date.formatDate(Date.now(), 'DD.MM.YYYY'),
      todate: date.formatDate(Date.now(), 'DD.MM.YYYY'),
      imei: null,
      filter: ''
    }
  },
  methods: {
    ...mapGetters('brocloakStore', [
      'getUser'
    ]),
    ...mapActions('imeireservationeceStore', [
      'fetchIMEIHistoryData'
    ]),
    optionsFn (opdate) {
      let longopdate = new Date(opdate).getTime()
      let now = Date.now()
      let oneYearOldDate = new Date()
      oneYearOldDate.setFullYear(oneYearOldDate.getFullYear() - 1)

      return longopdate >= oneYearOldDate && longopdate <= now
    },
    getIMEIHistoryData () {
      this.$q.loading.show()
      let result = this.fetchIMEIHistoryData({ imei: this.imei, fromdate: this.fromdate, todate: this.todate })
      result.then(data => {
        this.data = data
        this.$q.loading.hide()
      }).catch(err => {
        this.$q.loading.hide()
        this.showGenericRequestFailureMessage(this, err)
      })
    },
    validate () {
      let now = Date.now()
      let oneYearOldDate = new Date()
      oneYearOldDate.setFullYear(oneYearOldDate.getFullYear() - 1)
      let oneYearOldDateLong = oneYearOldDate.getTime()
      let fromdateLong = date.extractDate(this.fromdate, 'DD.MM.YYYY').getTime()
      let todateLong = date.extractDate(this.todate, 'DD.MM.YYYY').getTime()

      let isRangeValid = fromdateLong >= oneYearOldDateLong && fromdateLong <= now &&
        todateLong >= oneYearOldDateLong && todateLong <= now

      if (!(this.imei || (this.fromdate && this.todate)) || !isRangeValid) {
        this.$q.dialog({
          title: 'Error',
          message: 'Deine Suche konnte nicht bearbeitet werden. Bitte gebe ein gültiges Suchkriterium ein.',
          ok: {
            push: true,
            size: 'md',
            color: 'positive'
          }
        }).onOk(() => {
        })
      } else {
        this.getIMEIHistoryData()
      }
    },
    handleSearchByImei (e) {
      if (e.which === 10 || e.which === 13 || e.which === 0 || e.which === 9) {
        this.getIMEIHistoryData()
      }
    }
  }
}
</script>

<style lang="stylus">
.my-sticky-header-table
  /* max height is important */
  .q-table__middle
    max-height auto

  .q-table__top,
  .q-table__bottom,
  thead tr:first-child th /* bg color is important for th; just specify one */
    background-color #f6f7f7

  thead tr:first-child th
    position sticky
    top 0
    opacity 1
    z-index 1
</style>
