import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Paper,
  Typography,
  Box,
  Avatar,
  Button,
  Alert,
  CircularProgress,
  Card,
  CardContent,
  Divider,
  Chip,
} from '@mui/material';
import PersonIcon from '@mui/icons-material/Person';
import EmailIcon from '@mui/icons-material/Email';
import BadgeIcon from '@mui/icons-material/Badge';
import LogoutIcon from '@mui/icons-material/Logout';
import api, { authApi } from '../services/api';

const Profile = () => {
  const [user, setUser] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const response = await authApi.get('/me');
        setUser(response.data);
      } catch (err) {
        setError('فشل في تحميل الملف الشخصي');
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  if (loading) {
    return (
      <Box
        sx={{
          minHeight: '100vh',
          background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        }}
      >
        <CircularProgress size={80} sx={{ color: 'white' }} />
      </Box>
    );
  }

  if (error) {
    return (
      <Box
        sx={{
          minHeight: '100vh',
          background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          padding: 2,
        }}
      >
        <Container maxWidth="sm">
          <Alert
            severity="error"
            sx={{
              borderRadius: 3,
              fontWeight: 'bold',
              fontSize: '1.1rem',
              boxShadow: '0 4px 12px rgba(239, 68, 68, 0.2)',
            }}
          >
            {error}
          </Alert>
        </Container>
      </Box>
    );
  }

  return (
    <Box
      sx={{
        minHeight: '100vh',
        background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: 2,
        position: 'relative',
        overflow: 'hidden',
        '&::before': {
          content: '""',
          position: 'absolute',
          width: '400px',
          height: '400px',
          borderRadius: '50%',
          background: 'rgba(255, 255, 255, 0.1)',
          top: '-100px',
          right: '-100px',
        },
        '&::after': {
          content: '""',
          position: 'absolute',
          width: '300px',
          height: '300px',
          borderRadius: '50%',
          background: 'rgba(255, 255, 255, 0.05)',
          bottom: '-50px',
          left: '-50px',
        },
      }}
    >
      <Container component="main" maxWidth="sm" sx={{ position: 'relative', zIndex: 1 }}>
        <Paper
          elevation={0}
          sx={{
            padding: { xs: 3, sm: 4 },
            borderRadius: 3,
            background: 'rgba(255, 255, 255, 0.98)',
            backdropFilter: 'blur(10px)',
            boxShadow: '0 20px 60px rgba(0, 0, 0, 0.3)',
            border: '1px solid rgba(255, 255, 255, 0.2)',
          }}
        >
          <Box
            sx={{
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
            }}
          >
            <Avatar
              sx={{
                m: 2,
                bgcolor: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
                width: 100,
                height: 100,
                boxShadow: '0 8px 24px rgba(37, 99, 235, 0.3)',
              }}
            >
              <PersonIcon sx={{ fontSize: 50, color: 'white' }} />
            </Avatar>
            <Typography
              component="h1"
              variant="h4"
              sx={{
                fontWeight: 700,
                background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
                backgroundClip: 'text',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                mb: 1,
                textAlign: 'center',
              }}
            >
              مرحباً بك
            </Typography>
            <Typography
              variant="h5"
              sx={{
                color: '#333',
                fontWeight: 600,
                mb: 3,
                textAlign: 'center',
              }}
            >
              {user.fullName}
            </Typography>
            <Card
              sx={{
                width: '100%',
                mb: 3,
                borderRadius: 2,
                boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)',
                border: '1px solid rgba(0, 0, 0, 0.05)',
                transition: 'all 0.3s ease',
              }}
            >
              <CardContent sx={{ p: 2.5 }}>
                <Box 
                  sx={{ 
                    display: 'flex', 
                    alignItems: 'center', 
                    mb: 2,
                    pb: 2,
                    borderBottom: '1px solid #E5E7EB'
                  }}
                >
                  <PersonIcon sx={{ mr: 2, color: '#2563EB', fontSize: '1.8rem' }} />
                  <Box>
                    <Typography variant="caption" sx={{ color: '#999', fontSize: '0.85rem', fontWeight: 600 }}>
                      الاسم الكامل
                    </Typography>
                    <Typography variant="body1" sx={{ fontWeight: 600, color: '#333' }}>
                      {user.fullName}
                    </Typography>
                  </Box>
                </Box>

                <Box 
                  sx={{ 
                    display: 'flex', 
                    alignItems: 'center', 
                    mb: 2,
                    pb: 2,
                    borderBottom: '1px solid #E5E7EB'
                  }}
                >
                  <BadgeIcon sx={{ mr: 2, color: '#7C3AED', fontSize: '1.8rem' }} />
                  <Box>
                    <Typography variant="caption" sx={{ color: '#999', fontSize: '0.85rem', fontWeight: 600 }}>
                      اسم المستخدم
                    </Typography>
                    <Typography variant="body1" sx={{ fontWeight: 600, color: '#333' }}>
                      {user.username}
                    </Typography>
                  </Box>
                </Box>

                <Box 
                  sx={{ 
                    display: 'flex', 
                    alignItems: 'center', 
                    mb: 2,
                    pb: 2,
                    borderBottom: '1px solid #E5E7EB'
                  }}
                >
                  <EmailIcon sx={{ mr: 2, color: '#10B981', fontSize: '1.8rem' }} />
                  <Box>
                    <Typography variant="caption" sx={{ color: '#999', fontSize: '0.85rem', fontWeight: 600 }}>
                      البريد الإلكتروني
                    </Typography>
                    <Typography variant="body1" sx={{ fontWeight: 600, color: '#333' }}>
                      {user.email}
                    </Typography>
                  </Box>
                </Box>

                {user.roles && user.roles.length > 0 && (
                  <Box>
                    <Typography variant="caption" sx={{ color: '#999', fontSize: '0.85rem', fontWeight: 600 }}>
                      الأدوار والصلاحيات
                    </Typography>
                    <Box sx={{ mt: 1, display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                      {user.roles.map((role, index) => (
                        <Chip
                          key={index}
                          label={role.replace('ROLE_', '')}
                          sx={{
                            fontWeight: 700,
                            fontSize: '0.85rem',
                            background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
                            color: 'white',
                            boxShadow: '0 4px 12px rgba(37, 99, 235, 0.2)',
                          }}
                        />
                      ))}
                    </Box>
                  </Box>
                )}
              </CardContent>
            </Card>

            <Button
              variant="contained"
              startIcon={<LogoutIcon />}
              sx={{
                py: 1.3,
                px: 4,
                borderRadius: 2,
                fontSize: '1rem',
                fontWeight: 700,
                background: 'linear-gradient(135deg, #EF4444 0%, #F97316 100%)',
                boxShadow: '0 8px 24px rgba(239, 68, 68, 0.3)',
                textTransform: 'none',
                transition: 'all 0.3s ease',
                '&:hover': {
                  transform: 'translateY(-2px)',
                  boxShadow: '0 12px 32px rgba(239, 68, 68, 0.4)',
                },
              }}
              onClick={handleLogout}
            >
              تسجيل الخروج
            </Button>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
};

export default Profile;