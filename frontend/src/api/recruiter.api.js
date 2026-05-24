import client from './client';

export const recruiterApi = {
  searchCandidates: (query) => client.get('/recruiter/candidates', { params: { query } }),
};
