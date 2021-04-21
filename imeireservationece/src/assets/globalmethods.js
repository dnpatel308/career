export const showGenericRequestFailureMessage = (obj, err) => {
  let errCode = null

  try {
    errCode = err.response.data.data.code
  } catch (e) {
    // ignore
  }

  if (!errCode) {
    errCode = 9999
  }

  obj.$q.loading.hide()
  obj.$q.dialog({
    title: 'Error',
    message: 'Es ist ein Fehler aufgetreten: <b>' + errCode + '</b> <br />Weitere Informationen erhälst du auf der <a target="_blank" href="http://support-brodos.com/imei-reservierung-produktsupport/">Supportseite</a>',
    html: true,
    ok: {
      push: true,
      size: 'md',
      color: 'positive'
    }
  }).onOk(() => {
  })
}

export const showInvaildTokenDataMessage = (obj) => {
  obj.$q.dialog({
    title: 'Error',
    message: 'Es ist ein Fehler aufgetreten: <b>' + 8888 + '</b> <br />Weitere Informationen erhälst du auf der <a target="_blank" href="http://support-brodos.com/imei-reservierung-produktsupport/">Supportseite</a>',
    html: true,
    ok: {
      push: true,
      size: 'md',
      color: 'positive'
    }
  }).onOk(() => {
  })
}

export const redirectToBNet = () => {
  let redirectURL = process.config.BNETURL.substr(0, process.config.BNETURL.indexOf('/index.php'))
  Window.log.debug(redirectURL)
  window.location.href = redirectURL
}

export const getImeiHistoryColumns = () => {
  return [
    {
      name: 'position',
      required: true,
      label: 'Position',
      align: 'left',
      style: 'width: 95px',
      field: row => row.position,
      format: val => `${val}`,
      sortable: true
    },
    {
      name: 'articleno',
      align: 'center',
      label: 'Artikel-Nr',
      field: 'articleNo',
      sortable: true
    },
    {
      name: 'actionBy',
      label: 'Bearbeiter',
      field: 'actionBy',
      sortable: true
    },
    {
      name: 'serialNo',
      label: 'IMEI',
      field: 'serialNo',
      sortable: true
    },
    {
      name: 'ticketNo',
      label: 'Ticket',
      field: 'ticketNo',
      sortable: true
    },
    {
      name: 'dateTime',
      label: 'Datum / Uhrzeit',
      field: 'dateTime',
      sortable: true
    },
    {
      name: 'comment',
      label: 'Kommentar',
      field: 'comment',
      sortable: true
    },
    {
      name: 'status',
      label: 'Status',
      field: 'status',
      sortable: true
    }
  ]
}

export const getManageReservationColumns = () => {
  return [
    {
      name: 'position',
      required: true,
      label: 'Position',
      align: 'left',
      style: 'width: 95px',
      field: row => row.position,
      format: val => `${val}`,
      sortable: true
    },
    {
      name: 'articleno',
      align: 'center',
      label: 'Artikel-Nr',
      field: 'articleno',
      sortable: true
    },
    {
      name: 'imei',
      label: 'IMEI',
      field: 'imei',
      sortable: true
    },
    {
      name: 'ticketno',
      label: 'Ticket',
      field: 'ticketno',
      sortable: true
    },
    {
      name: 'datetime',
      label: 'Datum/Uhrzeit',
      field: 'datetime',
      sortable: true
    },
    {
      name: 'sendindays',
      label: 'Automat.Versand in',
      field: 'sendindays',
      sortable: true
    },
    {
      name: 'comment',
      label: 'Kommentar',
      field: 'comment',
      sortable: true
    },
    {
      name: 'action',
      label: 'Nachster Schritt'
    }
  ]
}
