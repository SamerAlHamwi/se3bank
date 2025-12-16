import React, { useState, useEffect } from 'react';
import { useNavigate, Outlet } from 'react-router-dom';
import {
  Drawer,
  AppBar,
  Toolbar,
  List,
  Typography,
  Divider,
  IconButton,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Box,
  Avatar,
  Menu,
  MenuItem,
  CircularProgress,
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import TransferWithinAStationIcon from '@mui/icons-material/TransferWithinAStation';
import PersonIcon from '@mui/icons-material/Person';
import LogoutIcon from '@mui/icons-material/Logout';
import DashboardIcon from '@mui/icons-material/Dashboard';
import GroupsIcon from '@mui/icons-material/Groups';
import ReceiptLongIcon from '@mui/icons-material/ReceiptLong';
import AddCircleIcon from '@mui/icons-material/AddCircle';
import { authApi } from '../services/api';

const drawerWidth = 280;

const Home = () => {
  const [open, setOpen] = useState(true);
  const [user, setUser] = useState(null);
  const [anchorEl, setAnchorEl] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const response = await authApi.get('/me');
        setUser(response.data);
      } catch (error) {
        navigate('/login');
      }
    };
    fetchUser();
  }, [navigate]);

  const handleDrawerOpen = () => {
    setOpen(true);
  };

  const handleDrawerClose = () => {
    setOpen(false);
  };

  const handleMenu = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    navigate('/login');
  };

  const hasRole = (role) => {
    return user?.roles?.includes(role);
  };

  const menuItems = [
    {
      text: 'لوحة التحكم',
      icon: <DashboardIcon />,
      path: '',
      roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_TELLER', 'ROLE_CUSTOMER'],
    },
    {
      text: 'قائمة الحسابات',
      icon: <AccountBalanceIcon />,
      path: 'accounts',
      roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_TELLER', 'ROLE_CUSTOMER'],
    },
    {
      text: 'إنشاء حساب',
      icon: <AccountBalanceWalletIcon />,
      path: 'accounts/create',
      roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_TELLER'],
    },
    {
      text: 'التحويل',
      icon: <TransferWithinAStationIcon />,
      path: 'transfer',
      roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_TELLER', 'ROLE_CUSTOMER'],
    },
    {
      text: 'سجل المعاملات',
      icon: <ReceiptLongIcon />,
      path: 'transactions',
      roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_TELLER', 'ROLE_CUSTOMER'],
    },
    {
      text: 'إنشاء معاملة',
      icon: <AddCircleIcon />,
      path: 'transactions/create',
      roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_TELLER'],
    },
    {
      text: 'مجموعات الحسابات',
      icon: <GroupsIcon />,
      path: `groups/user/${user?.userId}`,
      roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_TELLER', 'ROLE_CUSTOMER'],
    },
  ];

  if (!user) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <CircularProgress size={50} sx={{ color: '#2563EB' }} />
      </Box>
    );
  }

  return (
    <Box sx={{ display: 'flex' }}>
      <AppBar
        position="fixed"
        sx={{
          width: open ? `calc(100% - ${drawerWidth}px)` : '100%',
          ml: open ? `${drawerWidth}px` : 0,
          transition: 'width 0.3s ease, margin-left 0.3s ease',
          background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
          boxShadow: '0 4px 20px rgba(0, 0, 0, 0.1)',
        }}
      >
        <Toolbar sx={{ px: { xs: 2, sm: 3 } }}>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            onClick={handleDrawerOpen}
            edge="start"
            sx={{ 
              mr: 2, 
              ...(open && { display: 'none' }),
              transition: 'all 0.3s ease',
            }}
          >
            <MenuIcon />
          </IconButton>
          <Typography 
            variant="h6" 
            noWrap 
            component="div" 
            sx={{ 
              flexGrow: 1,
              fontWeight: 700,
              fontSize: '1.3rem',
              letterSpacing: '0.5px',
            }}
          >
            🏦 SE3 Bank
          </Typography>
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <Typography 
              sx={{ 
                mr: 2, 
                fontWeight: 600,
                fontSize: '0.95rem',
                display: { xs: 'none', sm: 'block' },
              }}
            >
              {user?.fullName}
            </Typography>
            <IconButton
              size="large"
              aria-label="account of current user"
              aria-controls="menu-appbar"
              aria-haspopup="true"
              onClick={handleMenu}
              color="inherit"
              sx={{
                transition: 'all 0.3s ease',
                '&:hover': {
                  backgroundColor: 'rgba(255, 255, 255, 0.1)',
                },
              }}
            >
              <Avatar 
                sx={{ 
                  width: 40, 
                  height: 40,
                  background: 'rgba(255, 255, 255, 0.2)',
                  border: '2px solid rgba(255, 255, 255, 0.3)',
                }}
              >
                <PersonIcon />
              </Avatar>
            </IconButton>
            <Menu
              id="menu-appbar"
              anchorEl={anchorEl}
              anchorOrigin={{
                vertical: 'bottom',
                horizontal: 'right',
              }}
              keepMounted
              transformOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
              open={Boolean(anchorEl)}
              onClose={handleClose}
              PaperProps={{
                sx: {
                  borderRadius: 2,
                  boxShadow: '0 12px 32px rgba(0, 0, 0, 0.15)',
                  mt: 1.5,
                },
              }}
            >
              <MenuItem 
                onClick={() => {
                  navigate('/profile');
                  handleClose();
                }}
                sx={{
                  py: 1.5,
                  fontSize: '0.95rem',
                  fontWeight: 500,
                  '&:hover': {
                    backgroundColor: '#F0F4FF',
                  },
                }}
              >
                <PersonIcon sx={{ mr: 1.5, color: '#2563EB' }} />
                الملف الشخصي
              </MenuItem>
              <Divider sx={{ my: 0.5 }} />
              <MenuItem 
                onClick={() => {
                  handleLogout();
                  handleClose();
                }}
                sx={{
                  py: 1.5,
                  fontSize: '0.95rem',
                  fontWeight: 500,
                  '&:hover': {
                    backgroundColor: '#FEE2E2',
                  },
                }}
              >
                <LogoutIcon sx={{ mr: 1.5, color: '#EF4444' }} />
                تسجيل الخروج
              </MenuItem>
            </Menu>
          </Box>
        </Toolbar>
      </AppBar>

      <Drawer
        sx={{
          width: drawerWidth,
          flexShrink: 0,
          '& .MuiDrawer-paper': {
            width: drawerWidth,
            boxSizing: 'border-box',
            background: 'linear-gradient(180deg, #FFFFFF 0%, #F8FAFC 100%)',
            borderRight: '1px solid #E5E7EB',
            boxShadow: '4px 0 12px rgba(0, 0, 0, 0.08)',
          },
        }}
        variant="persistent"
        anchor="right"
        open={open}
      >
        <Toolbar
          sx={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            px: 2,
            py: 2,
            borderBottom: '1px solid #E5E7EB',
          }}
        >
          <Typography
            sx={{
              fontWeight: 700,
              fontSize: '1.1rem',
              background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
              backgroundClip: 'text',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
            }}
          >
            القائمة
          </Typography>
          <IconButton 
            onClick={handleDrawerClose}
            sx={{
              transition: 'all 0.3s ease',
              '&:hover': {
                backgroundColor: '#E5E7EB',
              },
            }}
          >
            <ChevronLeftIcon />
          </IconButton>
        </Toolbar>
        <List sx={{ px: 1, py: 2 }}>
          {menuItems
            .filter(item => item.roles.some(role => hasRole(role)))
            .map((item) => (
              <ListItem key={item.text} disablePadding>
                <ListItemButton
                  onClick={() => navigate(item.path)}
                  sx={{
                    borderRadius: 2,
                    mb: 1,
                    transition: 'all 0.3s ease',
                    '&:hover': {
                      backgroundColor: '#F0F4FF',
                      transform: 'translateX(-4px)',
                    },
                    '&:active': {
                      backgroundColor: '#E0E7FF',
                    },
                  }}
                >
                  <ListItemIcon
                    sx={{
                      minWidth: 40,
                      color: '#2563EB',
                      transition: 'all 0.3s ease',
                    }}
                  >
                    {item.icon}
                  </ListItemIcon>
                  <ListItemText 
                    primary={item.text}
                    primaryTypographyProps={{
                      sx: {
                        fontWeight: 600,
                        fontSize: '0.95rem',
                        color: '#333',
                      },
                    }}
                  />
                </ListItemButton>
              </ListItem>
            ))}
        </List>
      </Drawer>

      <Box
        component="main"
        sx={{
          flexGrow: 1,
          bgcolor: '#F8FAFC',
          width: open ? `calc(100% - ${drawerWidth}px)` : '100%',
          transition: 'width 0.3s ease',
          minHeight: '100vh',
        }}
      >
        <Toolbar />
        <Box sx={{ p: { xs: 2, sm: 3, md: 4 } }}>
          <Outlet />
        </Box>
      </Box>
    </Box>
  );
};

export default Home;