export default [
  {
    path: '/experiences',
    name: 'ExperienceList',
    component: () => import('@/views/experience/ExperienceListView.vue'),
    meta: { layout: 'DefaultLayout', auth: true }
  },
  {
    path: '/experiences/new',
    name: 'ExperienceNew',
    component: () => import('@/views/experience/ExperienceFormView.vue'),
    meta: { layout: 'DefaultLayout', auth: true }
  },
  {
    path: '/experiences/:id/edit',
    name: 'ExperienceEdit',
    component: () => import('@/views/experience/ExperienceFormView.vue'),
    meta: { layout: 'DefaultLayout', auth: true }
  }
];
