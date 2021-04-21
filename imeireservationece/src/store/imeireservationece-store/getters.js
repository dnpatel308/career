export const getPosition = (state) => {
  return state.position
}

export const getImei = (state) => {
  return state.imei
}

export const getEan = (state) => {
  return state.ean
}

export function getNeedToLoadLimit (state) {
  return state.needToLoadLimit
}

export function getLimit (state) {
  return state.limit
}

export function getConsumedLimit (state) {
  return state.consumedLimit
}

export function getRemainingLimit (state) {
  return state.remainingLimit
}

export function getHistoryData (state) {
  return state.historyData
}

export const getReservationsAndOpenCases = (state) => {
  return state.reservationsAndOpenCases
}
