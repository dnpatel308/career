
const routes = [
  {
    path: '/',
    component: () => import('layouts/MyLayout.vue'),
    children: [
      { path: '/', component: () => import('pages/ManageReservation.vue') },
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
