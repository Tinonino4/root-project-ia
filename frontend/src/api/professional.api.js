import client from './client';

export const professionalApi = {
  getProfile: () => client.get('/v1/professional/profile'),
  updateProfile: (data) => client.put('/v1/professional/profile', data),
  getExperiences: () => client.get('/v1/professional/experiences'),
  createExperience: (data) => client.post('/v1/professional/experiences', data),
  updateExperience: (id, data) => client.put(`/v1/professional/experiences/${id}`, data),
  deleteExperience: (id) => client.delete(`/v1/professional/experiences/${id}`),
};
