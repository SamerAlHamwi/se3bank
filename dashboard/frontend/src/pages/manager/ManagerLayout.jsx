import React from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import { AppBar, Box, Drawer, List, ListItem, ListItemIcon, ListItemText, Toolbar, Typography, Avatar, ListItemButton, Divider } from '@mui/material';
import {
  Home, SwapHoriz, Receipt, Payment, SyncAlt, PersonAdd, People, FactCheck, 
  AccountBalanceWallet, CallMade, CallReceived, PlaylistAddCheck
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
];

const ManagerLayout = () => {
  const user = JSON.parse(localStorage.getItem('user'));
  const location = useLocation();

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

export default ManagerLayout;
