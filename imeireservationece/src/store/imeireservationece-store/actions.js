import { date } from 'quasar'
import { axios } from '../../boot/axios'
import { i18n } from '../../boot/i18n'

export function updateReservationsAndOpenCases ({ commit }, data) {
  commit('updateReservationsAndOpenCases', data)
}

export function updatePosition ({ commit }, position) {
  commit('updatePosition', position)
}

export function updateImei ({ commit }, imei) {
  commit('updateImei', imei)
}

export function updateEan ({ commit }, ean) {
  commit('updateEan', ean)
}

export function removePosition ({ commit }, position) {
  commit('removePosition', position)
}

export function updateStatus ({ commit }, position) {
  commit('updateStatus', position)
}

export function updateNeedToLoadLimit ({ commit }, needToLoadLimit) {
  commit('updateNeedToLoadLimit', needToLoadLimit)
}

export function updateLimit ({ commit }, limit) {
  commit('updateLimit', limit)
}

export function updateConsumedLimit ({ commit }, consumedLimit) {
  commit('updateConsumedLimit', consumedLimit)
}

export function updateRemainingLimit ({ commit }, remainingLimit) {
  commit('updateRemainingLimit', remainingLimit)
}

export function fetchIMEIHistoryData ({ commit }, payload) {
  let url = null
  if (payload.imei) {
    url = '/reports/customerserialnumberreservationreport/' + payload.imei
  } else if (payload.fromdate && payload.todate) {
    url = '/reports/customerserialnumberreservationreport/' + date.formatDate(date.extractDate(payload.fromdate, 'DD.MM.YYYY'), 'YYYY-MM-DD') + '/' + date.formatDate(date.extractDate(payload.todate, 'DD.MM.YYYY'), 'YYYY-MM-DD')
  }

  if (url) {
    Window.log.debug(axios.defaults.headers)
    let promise = axios.get(url)
      .then(responseJSON => {
        let historyData = []
        for (let i in responseJSON.data) {
          let rowData = responseJSON.data[i]
          rowData.datetimeLong = rowData.dateTime
          rowData.dateTime = date.formatDate(rowData.dateTime, 'DD.MM.YYYY HH:mm:ss')
          rowData.status = i18n.t(rowData.status)
          historyData[historyData.length] = rowData
        }

        historyData.sort(function (a, b) {
          return b.datetimeLong - a.datetimeLong
        })

        let index = 1
        for (let i in historyData) {
          historyData[i].position = index
          index++
        }

        Window.log.debug(historyData)
        commit('updateHistoryData', historyData)
        return historyData
      }).catch((err) => {
        throw (err)
      }
      )

    return promise
  }
}

export function fetchReservationsAndOpenCasesData ({ commit }) {
  let sortedData = []
  Window.log.debug(axios.defaults.headers)
  let promise = axios.get('/serialnumberreservation/')
    .then(responseJSON => {
      Window.log.debug(responseJSON)

      let needToLoadLimit = true
      let limit = responseJSON.data.data.totalReservationAllowed
      let consumedLimit = responseJSON.data.data.totalReservation
      let remainingLimit = limit - consumedLimit

      let unsortedData = []

      for (let i in responseJSON.data.data.reservations) {
        let rowData = {}
        rowData.type = 'RESERVATION'
        rowData.articleno = responseJSON.data.data.reservations[i].articleNo
        rowData.imei = responseJSON.data.data.reservations[i].serialNo
        rowData.ticketno = responseJSON.data.data.reservations[i].ticketNo
        rowData.datetimeLong = responseJSON.data.data.reservations[i].reservationTime
        rowData.datetime = date.formatDate(responseJSON.data.data.reservations[i].reservationTime, 'DD.MM.YYYY HH:mm:ss')
        rowData.sendindays = responseJSON.data.data.reservations[i].automaticShippingDays < 0 ? 0 : responseJSON.data.data.reservations[i].automaticShippingDays
        rowData.comment = responseJSON.data.data.reservations[i].reservationComment
        rowData.ean = responseJSON.data.data.reservations[i].ean
        rowData.reservationStatus = responseJSON.data.data.reservations[i].reservationStatus
        unsortedData[unsortedData.length] = rowData
      }

      for (let i in responseJSON.data.data.opencases) {
        let rowData = {}
        rowData.type = 'OPENCASE'
        rowData.articleno = responseJSON.data.data.opencases[i].articleNo
        rowData.imei = 'warten auf IMEI'
        rowData.ticketno = responseJSON.data.data.opencases[i].ticketNo
        rowData.datetimeLong = responseJSON.data.data.opencases[i].opencaseTime
        rowData.datetime = date.formatDate(responseJSON.data.data.opencases[i].opencaseTime, 'DD.MM.YYYY HH:mm:ss')
        rowData.comment = responseJSON.data.data.opencases[i].reservationComment
        unsortedData[unsortedData.length] = rowData
      }

      unsortedData.sort(function (a, b) {
        return b.datetimeLong - a.datetimeLong
      })

      for (let i in unsortedData) {
        let rowData = unsortedData[i]
        rowData.position = sortedData.length + 1
        sortedData[sortedData.length] = rowData
      }

      this.dispatch('imeireservationeceStore/updateReservationsAndOpenCases', sortedData)

      Window.log.debug(sortedData)

      Window.log.debug(needToLoadLimit)
      Window.log.debug(limit)
      Window.log.debug(consumedLimit)
      Window.log.debug(remainingLimit)

      this.dispatch('imeireservationeceStore/updateNeedToLoadLimit', needToLoadLimit)
      this.dispatch('imeireservationeceStore/updateLimit', limit)
      this.dispatch('imeireservationeceStore/updateConsumedLimit', consumedLimit)
      this.dispatch('imeireservationeceStore/updateRemainingLimit', remainingLimit)

      return sortedData
    }
    ).catch((err) => {
      throw (err)
    }
    )

  return promise
}
