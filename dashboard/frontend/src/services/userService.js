import api from './api';

const userService = {
  /**
   * Create a new user (Admin)
   * POST /api/users
   */
  createUser: (userData) => {
    return api.post('/users', userData);
  },

  /**
   * Get all users (Admin)
   * GET /api/users
   */
  getAllUsers: () => {
    return api.get('/users');
  },

  /**
   * Get user by ID
   * GET /api/users/{userId}
   */
  getUserById: (userId) => {
    return api.get(`/users/${userId}`);
  },

  /**
   * Get user by Username
   * GET /api/users/username/{username}
   */
  getUserByUsername: (username) => {
    return api.get(`/users/username/${username}`);
  },

  /**
   * Search users by name
   * GET /api/users/search?name={name}
   */
  searchUsers: (name) => {
    return api.get('/users/search', { params: { name } });
  },

  /**
   * Change user status (Admin)
   * PATCH /api/users/{userId}/status?isActive={boolean}
   */
  setUserStatus: (userId, isActive) => {
    return api.patch(`/users/${userId}/status`, null, { params: { isActive } });
  },

  /**
   * Add role to user (Admin)
   * PATCH /api/users/{userId}/role?role={role}
   */
  addRole: (userId, role) => {
    return api.patch(`/users/${userId}/role`, null, { params: { role } });
  }
};

export default userService;
