import React, { useState, useEffect } from 'react';
import { IconButton, Badge, Menu, MenuItem, Typography, Box, ListItemText, Divider, CircularProgress } from '@mui/material';
import { Notifications as NotificationsIcon, Markunread as MarkunreadIcon } from '@mui/icons-material';
import api from '../services/api';

const Notifications = () => {
    const [anchorEl, setAnchorEl] = useState(null);
    const [notifications, setNotifications] = useState([]);
    const [loading, setLoading] = useState(false);
    const user = JSON.parse(localStorage.getItem('user'));

    const fetchNotifications = async () => {
        if (!user) return;
        setLoading(true);
        try {
            const response = await api.get(`/api/notifications/user/${user.id}/unread`);
            setNotifications(response.data);
        } catch (error) {
            console.error("Failed to fetch notifications", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchNotifications();
        const interval = setInterval(fetchNotifications, 60000); // Poll every minute
        return () => clearInterval(interval);
    }, [user]);

    const handleOpen = (event) => {
        setAnchorEl(event.currentTarget);
        fetchNotifications(); // Refresh on open
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    const handleMarkAsRead = async (notificationId) => {
        try {
            await api.patch(`/api/notifications/${notificationId}/read`);
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
