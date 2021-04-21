<template>
  <q-page padding>
    <!-- content -->
    <div class="q-pa-md">
    <q-table
      class="my-sticky-header-table"
      title="Manage Reservation"
      :data="data"
      :columns="columns"
      row-key="name"
      flat
      bordered
    ></q-table>
  </q-page>
</template>

<script>
export default {
  // name: 'PageName',
}
</script>

<style>
</style>
