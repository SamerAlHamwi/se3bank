import React from 'react';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import { AppBar, Box, Drawer, List, ListItem, ListItemIcon, ListItemText, Toolbar, Typography, Avatar, ListItemButton, Divider } from '@mui/material';
import { Home, SwapHoriz, Receipt, Payment, SyncAlt, Logout } from '@mui/icons-material';
import { Link } from 'react-router-dom';
import Notifications from '../../components/Notifications';

const drawerWidth = 280;

const SidebarItems = [
  { text: 'الرئيسية', icon: <Home />, path: '/customer/dashboard' },
  { text: 'تحويل بين حساباتي', icon: <SyncAlt />, path: '/customer/internal-transfer' },
  { text: 'تحويل خارجي', icon: <SwapHoriz />, path: '/customer/external-transfer' },
  { text: 'معاملاتي', icon: <Receipt />, path: '/customer/my-transfers' },
  { text: 'تنفيذ دفع', icon: <Payment />, path: '/customer/make-payment' },
];

const CustomerLayout = () => {
  const user = JSON.parse(localStorage.getItem('user'));
  const location = useLocation();
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    navigate('/login');
  };

  return (
    <Box sx={{ display: 'flex' }}>
      <Drawer
        variant="permanent"
        sx={{ width: drawerWidth, flexShrink: 0, [`& .MuiDrawer-paper`]: { width: drawerWidth, boxSizing: 'border-box', backgroundColor: '#111827', color: '#E5E7EB'} }}
      >
        <Toolbar sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '24px', flexDirection: 'column' }}>
            <Avatar sx={{ width: 64, height: 64, mb: 2, bgcolor: '#4F46E5' }}>
              {user?.username?.charAt(0).toUpperCase()}
            </Avatar>
            <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#fff' }}>{user?.firstName} {user?.lastName}</Typography>
            <Typography variant="body2" sx={{ color: '#9CA3AF' }}>{user?.email}</Typography>
        </Toolbar>
        <List sx={{ padding: '16px' }}>
          {SidebarItems.map((item) => (
            <ListItem key={item.text} disablePadding sx={{ mb: 1 }}>
              <ListItemButton 
                component={Link} 
                to={item.path}
                selected={location.pathname === item.path}
                sx={{
                  borderRadius: '8px',
                  '&.Mui-selected': {
                    backgroundColor: '#4F46E5',
                    color: '#fff',
                    '& .MuiListItemIcon-root': { color: '#fff' },
                    '&:hover': { backgroundColor: '#4338CA' }
                  },
                  '&:hover': { backgroundColor: '#1F2937' }
                }}
              >
                <ListItemIcon sx={{ color: '#9CA3AF', minWidth: '40px' }}>
                  {item.icon}
                </ListItemIcon>
                <ListItemText primary={item.text} />
              </ListItemButton>
            </ListItem>
          ))}
          
          <Divider sx={{ my: 2, borderColor: '#374151' }} />
          
          <ListItem disablePadding>
            <ListItemButton 
              onClick={handleLogout}
              sx={{
                borderRadius: '8px',
                color: '#EF4444',
                '&:hover': { backgroundColor: 'rgba(239, 68, 68, 0.1)' }
              }}
            >
              <ListItemIcon sx={{ color: '#EF4444', minWidth: '40px' }}>
                <Logout />
              </ListItemIcon>
              <ListItemText primary="تسجيل الخروج" />
            </ListItemButton>
          </ListItem>
        </List>
      </Drawer>
      <Box
        component="main"
        sx={{ flexGrow: 1, bgcolor: '#F0F2F5', height: '100vh', overflow: 'auto' }}
      >
        <AppBar position="fixed" sx={{ width: `calc(100% - ${drawerWidth}px)`, ml: `${drawerWidth}px`, backgroundColor: '#fff', color: '#000', boxShadow: '0 1px 4px rgba(0,0,0,0.1)' }}>
            <Toolbar>
                <Box sx={{ flexGrow: 1 }} />
                <Notifications />
            </Toolbar>
        </AppBar>
        <Box sx={{ p: 3, mt: 8 }}>
            <Outlet />
        </Box>
      </Box>
    </Box>
  );
};

export default CustomerLayout;
