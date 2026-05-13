export default [
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/profile/ProfileView.vue'),
    meta: { layout: 'DefaultLayout', auth: true }
  },
  {
    path: '/profile/edit',
    name: 'ProfileEdit',
    component: () => import('@/views/profile/ProfileEditView.vue'),
    meta: { layout: 'DefaultLayout', auth: true }
  }
];
