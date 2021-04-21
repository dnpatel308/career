import axios from 'axios'
import { Base64 } from 'js-base64'

export function brocloak (store, redirect, config) {
  var promise = new Promise(function (resolve, reject) {
    var otp = window.location.hash.substr(1)

    // for testing on local
    // [
    // otp = ''
    // ]

    if (otp !== '') {
      axios.get(config.OTPAPI, { params: { otp: otp }, withCredentials: true })
        .then(function (response) {
          store.dispatch(config.STOREID + '/setToken', response.data)
          store.dispatch(config.STOREID + '/setUser', JSON.parse(Base64.decode(response.data)).payload)
          axios.defaults.headers.common['x-brocloak-token'] = response.data
          setInterval(() => axios.get(config.REFRESHAPI, { withCredentials: true })
            .then(function (response) {
              axios.defaults.headers.common['x-brocloak-token'] = ''
              delete axios.defaults.headers.common['x-brocloak-token']
              store.dispatch(config.STOREID + '/setToken', '')
              if (response.data !== '') {
                store.dispatch(config.STOREID + '/setToken', response.data)
                axios.defaults.headers.common['x-brocloak-token'] = response.data
              }
            })
            .catch(function (response) {
              window.location.href = config.BNETURL
            })
          , 30000)
          resolve()
          redirect(window.location.pathname)
        })
    } else {
      redirect(false)
      window.location.href = config.BNETURL

      // for testing on local
      // [
      // var xBrocloakToken = window.location.hash.substr(1)
      // Window.log.debug(xBrocloakToken)
      // store.dispatch(config.STOREID + '/setToken', xBrocloakToken)
      // store.dispatch(config.STOREID + '/setUser', JSON.parse(Base64.decode(xBrocloakToken)).payload)
      // axios.defaults.headers.common['x-brocloak-token'] = xBrocloakToken
      // setInterval(() => axios.get(config.REFRESHAPI, { withCredentials: true })
      //   .then(function (response) {
      //     let token2 = response.data
      //     Window.log.debug(token2)
      //     Window.log.debug(Base64.decode(token2))
      //     axios.defaults.headers['x-brocloak-token'] = ''
      //     delete axios.defaults.headers['x-brocloak-token']
      //     store.dispatch(config.STOREID + '/setToken', '')
      //     if (token2 !== '') {
      //       store.dispatch(config.STOREID + '/setToken', token2)
      //       axios.defaults.headers['x-brocloak-token'] = token2
      //     }
      //   })
      //   .catch(function (response) {
      //     window.location.href = config.BNETURL
      //   })
      // , 30000)

      // resolve()
      // ]
    }
  })
  return promise
}
