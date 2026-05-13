export default [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/LoginView.vue'),
    meta: { layout: 'AuthLayout', guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/RegisterView.vue'),
    meta: { layout: 'AuthLayout', guest: true }
  },
  {
    path: '/confirm',
    name: 'ConfirmAccount',
    component: () => import('@/views/auth/ConfirmAccountView.vue'),
    meta: { layout: 'AuthLayout', guest: true }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/views/auth/ForgotPasswordView.vue'),
    meta: { layout: 'AuthLayout', guest: true }
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: () => import('@/views/auth/ResetPasswordView.vue'),
    meta: { layout: 'AuthLayout', guest: true }
  }
];
