import client from './client';

export const authApi = {
  register: (data) => client.post('/auth/register', data),
  confirm: (data) => client.post('/auth/confirm', data),
  login: (data) => client.post('/auth/login', data),
  forgotPassword: (data) => client.post('/auth/forgot-password', data),
  resetPassword: (data) => client.post('/auth/reset-password', data),
  refreshToken: (data) => client.post('/auth/refresh-token', data),
  getMe: () => client.get('/auth/me'),
};
