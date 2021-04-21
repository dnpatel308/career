
const routes = [
  {
    path: '/',
    component: () => import('layouts/MyLayout.vue'),
    children: [
      { path: '', component: () => import('pages/Index.vue') },
      { path: '/reservation', component: () => import('pages/Reservation.vue') },
      { path: '/managereservation', component: () => import('pages/ManageReservation.vue') },
      { path: '/imeihistory', component: () => import('pages/ImeiHistory.vue') }
    ]
  }
]

// Always leave this as last one
if (process.env.MODE !== 'ssr') {
  routes.push({
    path: '*',
    component: () => import('pages/Error404.vue')
  })
}

export default routes
