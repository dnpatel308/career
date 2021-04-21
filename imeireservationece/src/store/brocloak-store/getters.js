const validateAndPopulateToken = (brocloakTokenData) => {
  Window.log.debug(brocloakTokenData)
  if (!brocloakTokenData.customerno) {
    if (brocloakTokenData.customerrelations &&
      brocloakTokenData.userlevel === 2) {
      for (let i in brocloakTokenData.customerrelations) {
        if (brocloakTokenData.customerrelations[i].tenantid === '1') {
          brocloakTokenData.customerno = brocloakTokenData.customerrelations[i].customerno
          Window.log.debug(brocloakTokenData.customerrelations[i])
          break
        }
      }
    }
  }

  Window.log.debug(brocloakTokenData)

  if ((!brocloakTokenData.fullname && !brocloakTokenData.firstname) || !brocloakTokenData.customerno) {
    return null
  }

  return brocloakTokenData
}

export const getUser = (state) => {
  return validateAndPopulateToken(state.user)
}

export const getToken = (state) => {
  return state.token
}
