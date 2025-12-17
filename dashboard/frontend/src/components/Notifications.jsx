import React, { useState, useEffect, useRef } from 'react';
import { IconButton, Badge, Menu, MenuItem, Typography, Box, ListItemText, Divider, CircularProgress } from '@mui/material';
import { Notifications as NotificationsIcon, Markunread as MarkunreadIcon } from '@mui/icons-material';
import api from '../services/api';

const Notifications = () => {
    const [anchorEl, setAnchorEl] = useState(null);
    const [notifications, setNotifications] = useState([]);
    const [loading, setLoading] = useState(false);
    const user = JSON.parse(localStorage.getItem('user'));
    
    // Use a ref to store user ID to avoid dependency loop if user object changes reference
    const userId = user ? user.userId : null;

    const fetchNotifications = async () => {
        if (!userId) return;
        // Don't set loading state for background polling to avoid UI flickering
        // Only set it if it's the first load or manual refresh
        try {
            const response = await api.get(`/notifications/user/${userId}/unread`);
            setNotifications(response.data);
        } catch (error) {
            console.error("Failed to fetch notifications", error);
        }
    };

    useEffect(() => {
        if (!userId) return;

        // Initial fetch
        fetchNotifications();
        
        // Poll every minute
        const interval = setInterval(fetchNotifications, 60000); 
        
        return () => clearInterval(interval);
    }, [userId]); // Only depend on userId, not the whole user object

    const handleOpen = (event) => {
        setAnchorEl(event.currentTarget);
        // Optional: fetch on open if you want immediate fresh data
        // fetchNotifications(); 
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    const handleMarkAsRead = async (notificationId) => {
        try {
            await api.patch(`/notifications/${notificationId}/read`);
            setNotifications(notifications.filter(n => n.id !== notificationId));
        } catch (error) {
            console.error("Failed to mark notification as read", error);
        }
    };

    return (
        <>
            <IconButton color="inherit" onClick={handleOpen}>
                <Badge badgeContent={notifications.length} color="error">
                    <NotificationsIcon />
                </Badge>
            </IconButton>
            <Menu
                anchorEl={anchorEl}
                open={Boolean(anchorEl)}
                onClose={handleClose}
                sx={{ mt: 1, '& .MuiPaper-root': { width: 360 } }}
            >
                <Typography variant="h6" sx={{ px: 2, py: 1 }}>الإشعارات</Typography>
                <Divider />
                {loading ? (
                    <Box sx={{ display: 'flex', justifyContent: 'center', p: 2 }}><CircularProgress size={24} /></Box>
                ) : notifications.length === 0 ? (
                    <MenuItem disabled>لا توجد إشعارات جديدة</MenuItem>
                ) : (
                    notifications.map(n => (
                        <MenuItem key={n.id} onClick={() => handleMarkAsRead(n.id)} sx={{ whiteSpace: 'normal' }}>
                            <ListItemText primary={n.title} secondary={n.message} />
                        </MenuItem>
                    ))
                )}
            </Menu>
        </>
    );
};

export default Notifications;
