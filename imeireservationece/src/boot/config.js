export default async ({ Vue }) => {
  if (!process.config) {
    let config = {}
    let xhr = new XMLHttpRequest()
    xhr.open('GET', '/statics/env.json', false)
    xhr.onload = () => {
      let responseJSON = JSON.parse(xhr.response)
      for (let i in responseJSON) {
        config[i] = responseJSON[i]
      }
    }

    xhr.send()
    process.config = config
    Window.log.debug(process.config)
  }
}
