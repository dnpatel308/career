<template>
  <q-page padding>
    <div class="q-pa-md" style="max-width: 400px">
      <q-form
        @submit="onSubmit"
        @reset="onReset"
        class="q-gutter-md"
      >
        <q-input v-model="name"
        filled=""
          lazy-rules
          label="Artikel-Nr: *"
          :rules="[ val => val && val.length > 0 || 'Please enter Artikel-Nr']"
        ></q-input>
        <q-input
          filled
          type="number"
          v-model="quantity"
          label="Anzahl:"
          lazy-rules
          disable="disable"
        ></q-input>
        <q-input
          filled
          type="email"
          v-model="customerEmail"
          label="Mail-Adresse: *"
          lazy-rules
          :rules="[
            val => val !== null && val !== '' || 'Please enter mail-adresse'
          ]"
        ></q-input>
        <q-input
          filled
          type="textarea"
          v-model="reservationComment"
          label="Kommentar:"
          lazy-rules
          :rules="[
            val => val !== null && val !== '' || 'Please enter kommentar'
          ]"
        ></q-input>
        <div>
          <q-btn label="Submit" type="submit" color="blue"></q-btn>
          <q-btn label="Reset" type="reset" color="gray" flat class="q-ml-sm" ></q-btn>
        </div>
      </q-form>
      </div>
  </q-page>
</template>

<script>
export default {
  data () {
    return {
      name: null,
      age: null,
      quantity: 1,
      customerEmail: null,
      reservationComment: null
    }
  },

  methods: {
    onSubmit () {
      this.$q.notify({
        color: 'green-4',
        textColor: 'white',
        icon: 'fas fa-check-circle',
        message: 'Submitted'
      })
    },

    onReset () {
      this.name = null
      this.age = null
      this.customerEmail = null
      this.reservationComment = null
    },

    mounted () {
      axios.get('https://api.coindesk.com/v1/bpi/currentprice.json').then(response => (this.info = response))
      }
  }
}
</script>

<style>
</style>
