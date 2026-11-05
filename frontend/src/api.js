import axios from 'axios';

const defaultBaseURL = import.meta.env.DEV ? 'http://localhost:8082/api' : '/api';
const api = axios.create({ baseURL: import.meta.env.VITE_API_URL || defaultBaseURL, timeout: 12000 });
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('nexus_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});
api.interceptors.response.use((response) => response, (error) => {
  if (error.response?.status === 401) {
    localStorage.removeItem('nexus_token');
    window.dispatchEvent(new CustomEvent('nexus:unauthorized'));
  }
  return Promise.reject(error);
});
export default api;
