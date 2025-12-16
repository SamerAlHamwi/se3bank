import axios from 'axios';

const API_BASE_URL = 'http://localhost:9090/api';
const AUTH_BASE_URL = 'http://localhost:9090/auth';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Auth API instance for endpoints under /auth
const authApi = axios.create({
  baseURL: AUTH_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add token for api
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Request interceptor to add token for authApi
authApi.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor to handle errors for api
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Check if error response exists
    if (error.response) {
      // If status is 401 Unauthorized
      if (error.response.status === 401) {
        // Only redirect to login if we are not already on the login or register page
        // to avoid loops or interrupting the user if they are trying to log in
        const path = window.location.pathname;
        if (path !== '/login' && path !== '/register') {
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          window.location.href = '/login';
        }
      }
    }
    return Promise.reject(error);
  }
);

// Response interceptor to handle errors for authApi
authApi.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
       const path = window.location.pathname;
        if (path !== '/login' && path !== '/register') {
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          window.location.href = '/login';
        }
    }
    return Promise.reject(error);
  }
);

export const decoratorService = {
  addDecorator: (decoratorData) => api.post('/decorators', decoratorData),
  getDecoratorsByAccount: (accountId) => api.get(`/decorators/account/${accountId}`),
  getActiveDecoratorsByAccount: (accountId) => api.get(`/decorators/account/${accountId}/active`),
  getAccountFeatures: (accountId) => api.get(`/decorators/account/${accountId}/features`),
  deleteDecorator: (decoratorId) => api.delete(`/decorators/${decoratorId}`),
};

export default api;
export { authApi };
