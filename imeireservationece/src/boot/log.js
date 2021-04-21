const consoleLogLevel = require('console-log-level')

export default async ({ Vue }) => {
  Window.getQueryVariable = (variable) => {
    let query = window.location.search.substring(1)
    let vars = query.split('&')
    for (let i = 0; i < vars.length; i++) {
      let pair = vars[i].split('=')
      if (decodeURIComponent(pair[0]) === variable) {
        return decodeURIComponent(pair[1])
      }
    }

    return 'info'
  }

  Window.log = consoleLogLevel({ level: Window.getQueryVariable('loglevel').replace('/', '') })
  Window.log.debug(Window.consoleLogLevel)
}
