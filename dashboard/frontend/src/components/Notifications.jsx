import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  Button,
  List,
  ListItem,
  ListItemText,
  IconButton,
  Paper,
  Chip,
  Alert,
  Snackbar,
  CircularProgress,
  Divider,
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import MarkEmailReadIcon from '@mui/icons-material/MarkEmailRead';
import notificationService from '../services/notificationService';

const Notifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
  const [filter, setFilter] = useState('all'); // 'all' or 'unread'

  // Get user from local storage
  const user = JSON.parse(localStorage.getItem('user'));
  const userId = user?.userId || user?.id;

  const fetchNotifications = async () => {
    try {
      setLoading(true);
      if (userId) {
        let response;
        if (filter === 'unread') {
          response = await notificationService.getUnreadNotifications(userId);
        } else {
          response = await notificationService.getUserNotifications(userId);
        }
        setNotifications(response.data);
      } else {
          setError('User information not found. Please log in again.');
      }
    } catch (err) {
      console.error('Error fetching notifications:', err);
      setError('Failed to load notifications');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNotifications();
  }, [filter]);

  const handleMarkAsRead = async (notificationId) => {
    try {
      await notificationService.markAsRead(notificationId);
      // Update local state to reflect change
      setNotifications(prev => 
        prev.map(n => n.id === notificationId ? { ...n, read: true } : n)
      );
      // If we are in 'unread' filter, we might want to remove it or keep it until refresh
      if (filter === 'unread') {
        setNotifications(prev => prev.filter(n => n.id !== notificationId));
      }
    } catch (err) {
      console.error('Error marking notification as read:', err);
      setSnackbar({
        open: true,
        message: 'Failed to mark as read',
        severity: 'error'
      });
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      if (userId) {
        await notificationService.markAllAsRead(userId);
        setSnackbar({
          open: true,
          message: 'All notifications marked as read',
          severity: 'success'
        });
        fetchNotifications();
      }
    } catch (err) {
      console.error('Error marking all as read:', err);
      setSnackbar({
        open: true,
        message: 'Failed to mark all as read',
        severity: 'error'
      });
    }
  };

  const handleCloseSnackbar = () => {
    setSnackbar({ ...snackbar, open: false });
  };

  if (loading && notifications.length === 0) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="50vh">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h2" sx={{ fontWeight: 'bold', color: 'primary.main' }}>
          Notifications
        </Typography>
        <Box>
          <Button 
            variant={filter === 'all' ? "contained" : "outlined"} 
            onClick={() => setFilter('all')}
            sx={{ mr: 1 }}
          >
            All
          </Button>
          <Button 
            variant={filter === 'unread' ? "contained" : "outlined"} 
            onClick={() => setFilter('unread')}
          >
            Unread
          </Button>
        </Box>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Box sx={{ mb: 2, display: 'flex', justifyContent: 'flex-end' }}>
        <Button 
          startIcon={<MarkEmailReadIcon />} 
          onClick={handleMarkAllAsRead}
          disabled={notifications.length === 0}
        >
          Mark All as Read
        </Button>
      </Box>

      <Paper elevation={2}>
        <List>
          {notifications.map((notification, index) => (
            <React.Fragment key={notification.id}>
              {index > 0 && <Divider />}
              <ListItem
                secondaryAction={
                  !notification.read && (
                    <IconButton edge="end" aria-label="mark as read" onClick={() => handleMarkAsRead(notification.id)}>
                      <CheckCircleIcon color="primary" />
                    </IconButton>
                  )
                }
                sx={{ 
                  bgcolor: notification.read ? 'transparent' : 'action.hover',
                  transition: 'background-color 0.3s'
                }}
              >
                <ListItemText
                  primary={
                    <Box display="flex" alignItems="center" gap={1}>
                      <Typography variant="subtitle1" fontWeight={notification.read ? 'normal' : 'bold'}>
                        {notification.message}
                      </Typography>
                      {!notification.read && (
                        <Chip label="New" color="secondary" size="small" />
                      )}
                    </Box>
                  }
                  secondary={
                    <Typography variant="caption" color="text.secondary">
                      {new Date(notification.timestamp).toLocaleString()}
                    </Typography>
                  }
                />
              </ListItem>
            </React.Fragment>
          ))}
          {notifications.length === 0 && (
            <ListItem>
              <ListItemText primary="No notifications found" sx={{ textAlign: 'center', color: 'text.secondary' }} />
            </ListItem>
          )}
        </List>
      </Paper>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={handleCloseSnackbar}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert onClose={handleCloseSnackbar} severity={snackbar.severity} sx={{ width: '100%' }}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Container>
  );
};

export default Notifications;
