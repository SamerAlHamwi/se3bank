import React, { useState } from 'react';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import { AppBar, Box, Drawer, List, ListItem, ListItemIcon, ListItemText, Toolbar, Typography, Avatar, ListItemButton, Divider, IconButton } from '@mui/material';
import {
  Home, SwapHoriz, Receipt, Payment, SyncAlt, PersonAdd, People, FactCheck, 
  AccountBalanceWallet, CallMade, CallReceived, PlaylistAddCheck, Logout, Menu as MenuIcon, Group, GroupWork, Star
} from '@mui/icons-material';
import { Link } from 'react-router-dom';
import Notifications from '../../components/Notifications';

const drawerWidth = 280;

const personalItems = [
  { text: 'الرئيسية', icon: <Home />, path: '/manager/dashboard' },
  { text: 'تحويل بين حساباتي', icon: <SyncAlt />, path: '/manager/internal-transfer' },
  { text: 'معاملاتي', icon: <Receipt />, path: '/manager/my-transfers' },
  { text: 'تنفيذ دفع', icon: <Payment />, path: '/manager/make-payment' },
];

const managerActions = [
  { text: 'المعاملات المعلقة', icon: <PlaylistAddCheck />, path: '/manager/pending-transactions' },
  { text: 'سحب', icon: <CallMade />, path: '/manager/withdraw' },
  { text: 'إيداع', icon: <CallReceived />, path: '/manager/deposit' },
  { text: 'إنشاء حساب جديد', icon: <PersonAdd />, path: '/manager/create-account' },
  { text: 'كل الحسابات', icon: <People />, path: '/manager/all-accounts' },
  { text: 'التحقق من حساب', icon: <FactCheck />, path: '/manager/check-account' },
  { text: 'كل المستخدمين', icon: <Group />, path: '/manager/all-users' },
  { text: 'كل المجموعات', icon: <GroupWork />, path: '/manager/all-groups' },
  { text: 'الميزات الإضافية', icon: <Star />, path: '/manager/features' },
  { text: 'كل المعاملات', icon: <AccountBalanceWallet />, path: '/manager/all-transactions' },
];

const ManagerLayout = () => {
  const user = JSON.parse(localStorage.getItem('user'));
  const location = useLocation();
  const navigate = useNavigate();
  const [mobileOpen, setMobileOpen] = useState(false);

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const handleLogout = () => {
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    navigate('/login');
  };

  const renderList = (items) => items.map((item) => (
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
  ));

  const drawer = (
    <div>
        <Toolbar sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '24px', flexDirection: 'column' }}>
            <Avatar sx={{ width: 64, height: 64, mb: 2, bgcolor: '#4F46E5' }}>
              {user?.username?.charAt(0).toUpperCase()}
            </Avatar>
            <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#fff' }}>{user?.firstName} {user?.lastName}</Typography>
            <Typography variant="body2" sx={{ color: '#9CA3AF' }}>{`(${user?.roles?.join(', ').replace('ROLE_', '')})`}</Typography>
        </Toolbar>

        <List sx={{ px: 2 }}>
            <Typography variant="overline" sx={{ color: '#6B7280', pl: 2, fontWeight: 'bold' }}>حسابي الشخصي</Typography>
            {renderList(personalItems)}
        </List>

        <Divider sx={{ my: 2, borderColor: '#374151' }} />

        <List sx={{ px: 2 }}>
            <Typography variant="overline" sx={{ color: '#6B7280', pl: 2, fontWeight: 'bold' }}>عمليات المدير</Typography>
            {renderList(managerActions)}
        </List>

        <Divider sx={{ my: 2, borderColor: '#374151' }} />
          
        <List sx={{ px: 2 }}>
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
    </div>
  );

  return (
    <Box sx={{ display: 'flex' }}>
      <AppBar
        position="fixed"
        sx={{
          width: { sm: `calc(100% - ${drawerWidth}px)` },
          mr: { sm: `${drawerWidth}px` }, 
          ml: { sm: 0 },
          backgroundColor: '#fff', 
          color: '#000', 
          boxShadow: '0 1px 4px rgba(0,0,0,0.1)'
        }}
      >
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{ mr: 2, display: { sm: 'none' } }}
          >
            <MenuIcon />
          </IconButton>
          <Box sx={{ flexGrow: 1 }} />
          <Notifications />
        </Toolbar>
      </AppBar>
      <Box
        component="nav"
        sx={{ width: { sm: drawerWidth }, flexShrink: { sm: 0 } }}
        aria-label="mailbox folders"
      >
        {/* Mobile Drawer */}
        <Drawer
          variant="temporary"
          anchor="right" 
          open={mobileOpen}
          onClose={handleDrawerToggle}
          ModalProps={{
            keepMounted: true, 
          }}
          sx={{
            display: { xs: 'block', sm: 'none' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth, backgroundColor: '#111827', color: '#E5E7EB' },
          }}
        >
          {drawer}
        </Drawer>
        
        {/* Desktop Permanent Drawer */}
        <Drawer
          variant="permanent"
          anchor="right"
          sx={{
            display: { xs: 'none', sm: 'block' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth, backgroundColor: '#111827', color: '#E5E7EB' },
          }}
          open
        >
          {drawer}
        </Drawer>
      </Box>
      <Box
        component="main"
        sx={{ 
          flexGrow: 1, 
          p: 3, 
          width: { sm: `calc(100% - ${drawerWidth}px)` }, 
          mr: { sm: `${drawerWidth}px` }, 
          ml: { sm: 0 },
          bgcolor: '#F0F2F5', 
          height: '100vh', 
          overflow: 'auto' 
        }}
      >
        <Toolbar />
        <Outlet />
      </Box>
    </Box>
  );
};

export default ManagerLayout;
