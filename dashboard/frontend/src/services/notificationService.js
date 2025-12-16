import api from './api';

const notificationService = {
  /**
   * Get all notifications for a user
   * GET /api/notifications/user/{userId}
   */
  getUserNotifications: (userId) => {
    return api.get(`/notifications/user/${userId}`);
  },

  /**
   * Get unread notifications for a user
   * GET /api/notifications/user/{userId}/unread
   */
  getUnreadNotifications: (userId) => {
    return api.get(`/notifications/user/${userId}/unread`);
  },

  /**
   * Mark a notification as read
   * PATCH /api/notifications/{notificationId}/read
   */
  markAsRead: (notificationId) => {
    return api.patch(`/notifications/${notificationId}/read`);
  },

  /**
   * Mark all notifications as read for a user
   * PATCH /api/notifications/user/{userId}/read-all
   */
  markAllAsRead: (userId) => {
    return api.patch(`/notifications/user/${userId}/read-all`);
  }
};

export default notificationService;
