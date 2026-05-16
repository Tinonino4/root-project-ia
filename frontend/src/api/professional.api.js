import client from './client';

export const professionalApi = {
  getProfile: () => client.get('/professional/profile'),
  updateProfile: (data) => client.put('/professional/profile', data),
  getExperiences: () => client.get('/professional/experiences'),
  createExperience: (data) => client.post('/professional/experiences', data),
  updateExperience: (id, data) => client.put(`/professional/experiences/${id}`, data),
  deleteExperience: (id) => client.delete(`/professional/experiences/${id}`),
};
