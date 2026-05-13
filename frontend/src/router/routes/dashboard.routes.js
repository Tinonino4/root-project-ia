export default [
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('@/views/dashboard/DashboardView.vue'),
    meta: { layout: 'DefaultLayout', auth: true }
  }
];
