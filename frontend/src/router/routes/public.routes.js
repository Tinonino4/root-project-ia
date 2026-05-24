export default [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/HomeView.vue'),
    meta: { layout: 'PublicLayout', guest: true }
  },
  {
    path: '/u/:userId',
    name: 'PublicProfile',
    component: () => import('@/views/profile/PublicProfileView.vue'),
    meta: { layout: 'PublicLayout', guest: true }
  },
  {
    path: '/recruiter/search',
    name: 'RecruiterSearch',
    component: () => import('@/views/recruiter/RecruiterSearchView.vue'),
    meta: { layout: 'DefaultLayout', auth: true }
  }
];
