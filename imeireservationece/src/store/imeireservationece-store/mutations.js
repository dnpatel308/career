export function updatePosition (state, position) {
  state.position = position
}

export function removePosition (state, position) {
  Window.log.debug('position = ' + (position - 1))
  state.reservationsAndOpenCases.splice(position - 1, 1)
  let index = 1
  for (let i in state.reservationsAndOpenCases) {
    state.reservationsAndOpenCases[i].position = index
    index++
  }
}

export function updateImei (state, imei) {
  state.imei = imei
}

export function updateEan (state, ean) {
  state.ean = ean
}

export function updateReservationsAndOpenCases (state, reservationsAndOpenCases) {
  state.reservationsAndOpenCases = reservationsAndOpenCases
}

export function updateStatus (state, position) {
  if (state.reservationsAndOpenCases[position - 1].reservationStatus) {
    state.reservationsAndOpenCases[position - 1].reservationStatus = 'REQUESTFORSENTOUT'
  }
}

export function updateHistoryData (state, historyData) {
  state.historyData = historyData
}

export function updateNeedToLoadLimit (state, needToLoadLimit) {
  state.needToLoadLimit = needToLoadLimit
}

export function updateLimit (state, limit) {
  state.limit = limit
}

export function updateConsumedLimit (state, consumedLimit) {
  state.consumedLimit = consumedLimit
}

export function updateRemainingLimit (state, remainingLimit) {
  state.remainingLimit = remainingLimit
}
