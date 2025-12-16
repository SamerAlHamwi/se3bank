import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import {
  Container,
  Paper,
  TextField,
  Button,
  Typography,
  Box,
  Alert,
  Avatar,
  Grid,
  InputAdornment,
  IconButton,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
} from '@mui/material';
import PersonAddOutlinedIcon from '@mui/icons-material/PersonAddOutlined';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import AccountCircle from '@mui/icons-material/AccountCircle';
import Email from '@mui/icons-material/Email';
import Lock from '@mui/icons-material/Lock';
import Phone from '@mui/icons-material/Phone';
import Home from '@mui/icons-material/Home';
import Badge from '@mui/icons-material/Badge';
import api, { authApi } from '../services/api';

const Register = () => {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    firstName: '',
    lastName: '',
    phoneNumber: '',
    address: '',
    nationalId: '',
    role: 'ROLE_CUSTOMER',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleClickShowPassword = () => setShowPassword((show) => !show);
  const handleClickShowConfirmPassword = () => setShowConfirmPassword((show) => !show);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (formData.password !== formData.confirmPassword) {
      setError('كلمات المرور غير متطابقة');
      return;
    }
    setLoading(true);
    setError('');
    try {
      await authApi.post('/register', {
        username: formData.username,
        email: formData.email,
        password: formData.password,
        firstName: formData.firstName,
        lastName: formData.lastName,
        phoneNumber: formData.phoneNumber,
        address: formData.address,
        nationalId: formData.nationalId,
        roles: [formData.role],
      });
      navigate('/login');
    } catch (err) {
      setError(err.response?.data?.message || 'فشل في التسجيل');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box
      sx={{
        minHeight: '100vh',
        background: 'linear-gradient(135deg, #7C3AED 0%, #2563EB 100%)',
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
          left: '-100px',
        },
        '&::after': {
          content: '""',
          position: 'absolute',
          width: '300px',
          height: '300px',
          borderRadius: '50%',
          background: 'rgba(255, 255, 255, 0.05)',
          bottom: '-50px',
          right: '-50px',
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
            maxHeight: '90vh',
            overflowY: 'auto',
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
                bgcolor: 'linear-gradient(135deg, #7C3AED 0%, #2563EB 100%)',
                width: 80,
                height: 80,
                boxShadow: '0 8px 24px rgba(124, 58, 237, 0.3)',
              }}
            >
              <PersonAddOutlinedIcon sx={{ fontSize: 40, color: 'white' }} />
            </Avatar>
            <Typography
              component="h1"
              variant="h4"
              sx={{
                fontWeight: 700,
                background: 'linear-gradient(135deg, #7C3AED 0%, #2563EB 100%)',
                backgroundClip: 'text',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                mb: 1,
                textAlign: 'center',
              }}
            >
              إنشاء حساب جديد
            </Typography>
            <Typography 
              variant="body2" 
              sx={{ 
                color: '#666', 
                mb: 3, 
                textAlign: 'center',
                fontSize: '0.95rem',
              }}
            >
              انضم إلى ملايين المستخدمين الآمنين
            </Typography>
            {error && (
              <Alert
                severity="error"
                sx={{
                  width: '100%',
                  mb: 2,
                  borderRadius: 2,
                  fontWeight: 600,
                  boxShadow: '0 4px 12px rgba(239, 68, 68, 0.2)',
                  border: '1px solid #FEE2E2',
                }}
              >
                {error}
              </Alert>
            )}
            <Box component="form" onSubmit={handleSubmit} sx={{ mt: 2, width: '100%' }}>
              <TextField
                margin="normal"
                required
                fullWidth
                id="username"
                label="اسم المستخدم"
                name="username"
                autoComplete="username"
                autoFocus
                value={formData.username}
                onChange={handleChange}
                variant="outlined"
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <AccountCircle sx={{ color: '#7C3AED', mr: 1 }} />
                    </InputAdornment>
                  ),
                }}
                sx={{
                  '& .MuiOutlinedInput-root': {
                    borderRadius: 2,
                    backgroundColor: '#F8FAFC',
                    transition: 'all 0.3s ease',
                    '&:hover fieldset': {
                      borderColor: '#7C3AED',
                    },
                    '&.Mui-focused fieldset': {
                      borderColor: '#7C3AED',
                      boxShadow: '0 0 0 3px rgba(124, 58, 237, 0.1)',
                    },
                  },
                }}
              />
              <TextField
                margin="normal"
                required
                fullWidth
                id="email"
                label="البريد الإلكتروني"
                name="email"
                autoComplete="email"
                type="email"
                value={formData.email}
                onChange={handleChange}
                variant="outlined"
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Email sx={{ color: '#7C3AED', mr: 1 }} />
                    </InputAdornment>
                  ),
                }}
                sx={{
                  '& .MuiOutlinedInput-root': {
                    borderRadius: 2,
                    backgroundColor: '#F8FAFC',
                    transition: 'all 0.3s ease',
                    '&:hover fieldset': {
                      borderColor: '#7C3AED',
                    },
                    '&.Mui-focused fieldset': {
                      borderColor: '#7C3AED',
                      boxShadow: '0 0 0 3px rgba(124, 58, 237, 0.1)',
                    },
                  },
                }}
              />
              <TextField
                margin="normal"
                required
                fullWidth
                name="password"
                label="كلمة المرور"
                type={showPassword ? 'text' : 'password'}
                id="password"
                autoComplete="new-password"
                value={formData.password}
                onChange={handleChange}
                variant="outlined"
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Lock sx={{ color: '#7C3AED', mr: 1 }} />
                    </InputAdornment>
                  ),
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        aria-label="toggle password visibility"
                        onClick={handleClickShowPassword}
                        edge="end"
                        size="small"
                      >
                        {showPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
                sx={{
                  '& .MuiOutlinedInput-root': {
                    borderRadius: 2,
                    backgroundColor: '#F8FAFC',
                    transition: 'all 0.3s ease',
                    '&:hover fieldset': {
                      borderColor: '#7C3AED',
                    },
                    '&.Mui-focused fieldset': {
                      borderColor: '#7C3AED',
                      boxShadow: '0 0 0 3px rgba(124, 58, 237, 0.1)',
                    },
                  },
                }}
              />
              <TextField
                margin="normal"
                required
                fullWidth
                name="confirmPassword"
                label="تأكيد كلمة المرور"
                type={showConfirmPassword ? 'text' : 'password'}
                id="confirmPassword"
                autoComplete="new-password"
                value={formData.confirmPassword}
                onChange={handleChange}
                variant="outlined"
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Lock sx={{ color: '#7C3AED', mr: 1 }} />
                    </InputAdornment>
                  ),
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        aria-label="toggle confirm password visibility"
                        onClick={handleClickShowConfirmPassword}
                        edge="end"
                        size="small"
                      >
                        {showConfirmPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
                sx={{
                  '& .MuiOutlinedInput-root': {
                    borderRadius: 2,
                    backgroundColor: '#F8FAFC',
                    transition: 'all 0.3s ease',
                    '&:hover fieldset': {
                      borderColor: '#7C3AED',
                    },
                    '&.Mui-focused fieldset': {
                      borderColor: '#7C3AED',
                      boxShadow: '0 0 0 3px rgba(124, 58, 237, 0.1)',
                    },
                  },
                }}
              />
              <Grid container spacing={2}>
                <Grid item xs={12} sm={6}>
                  <TextField
                    margin="normal"
                    required
                    fullWidth
                    id="firstName"
                    label="الاسم الأول"
                    name="firstName"
                    autoComplete="given-name"
                    value={formData.firstName}
                    onChange={handleChange}
                    variant="outlined"
                    size="small"
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <AccountCircle sx={{ color: '#7C3AED', mr: 1, fontSize: '1.2rem' }} />
                        </InputAdornment>
                      ),
                    }}
                    sx={{
                      '& .MuiOutlinedInput-root': {
                        borderRadius: 2,
                        backgroundColor: '#F8FAFC',
                        '&:hover fieldset': {
                          borderColor: '#7C3AED',
                        },
                        '&.Mui-focused fieldset': {
                          borderColor: '#7C3AED',
                          boxShadow: '0 0 0 3px rgba(124, 58, 237, 0.1)',
                        },
                      },
                    }}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    margin="normal"
                    required
                    fullWidth
                    id="lastName"
                    label="اسم العائلة"
                    name="lastName"
                    autoComplete="family-name"
                    value={formData.lastName}
                    onChange={handleChange}
                    variant="outlined"
                    size="small"
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <AccountCircle sx={{ color: '#7C3AED', mr: 1, fontSize: '1.2rem' }} />
                        </InputAdornment>
                      ),
                    }}
                    sx={{
                      '& .MuiOutlinedInput-root': {
                        borderRadius: 2,
                        backgroundColor: '#F8FAFC',
                        '&:hover fieldset': {
                          borderColor: '#7C3AED',
                        },
                        '&.Mui-focused fieldset': {
                          borderColor: '#7C3AED',
                          boxShadow: '0 0 0 3px rgba(124, 58, 237, 0.1)',
                        },
                      },
                    }}
                  />
                </Grid>
              </Grid>
              <TextField
                margin="normal"
                fullWidth
                id="phoneNumber"
                label="رقم الهاتف"
                name="phoneNumber"
                autoComplete="tel"
                value={formData.phoneNumber}
                onChange={handleChange}
                variant="outlined"
                size="small"
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Phone sx={{ color: '#7C3AED', mr: 1 }} />
                    </InputAdornment>
                  ),
                }}
                sx={{
                  '& .MuiOutlinedInput-root': {
                    borderRadius: 2,
                    backgroundColor: '#F8FAFC',
                    '&:hover fieldset': {
                      borderColor: '#7C3AED',
                    },
                    '&.Mui-focused fieldset': {
                      borderColor: '#7C3AED',
                      boxShadow: '0 0 0 3px rgba(124, 58, 237, 0.1)',
                    },
                  },
                }}
              />
              <TextField
                margin="normal"
                fullWidth
                id="address"
                label="العنوان"
                name="address"
                autoComplete="address"
                value={formData.address}
                onChange={handleChange}
                variant="outlined"
                size="small"
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Home sx={{ color: '#7C3AED', mr: 1 }} />
                    </InputAdornment>
                  ),
                }}
                sx={{
                  '& .MuiOutlinedInput-root': {
                    borderRadius: 2,
                    backgroundColor: '#F8FAFC',
                    '&:hover fieldset': {
                      borderColor: '#7C3AED',
                    },
                    '&.Mui-focused fieldset': {
                      borderColor: '#7C3AED',
                      boxShadow: '0 0 0 3px rgba(124, 58, 237, 0.1)',
                    },
                  },
                }}
              />
              <TextField
                margin="normal"
                fullWidth
                id="nationalId"
                label="الرقم القومي"
                name="nationalId"
                autoComplete="national-id"
                value={formData.nationalId}
                onChange={handleChange}
                variant="outlined"
                size="small"
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Badge sx={{ color: '#7C3AED', mr: 1 }} />
                    </InputAdornment>
                  ),
                }}
                sx={{
                  '& .MuiOutlinedInput-root': {
                    borderRadius: 2,
                    backgroundColor: '#F8FAFC',
                    '&:hover fieldset': {
                      borderColor: '#7C3AED',
                    },
                    '&.Mui-focused fieldset': {
                      borderColor: '#7C3AED',
                      boxShadow: '0 0 0 3px rgba(124, 58, 237, 0.1)',
                    },
                  },
                }}
              />
              <FormControl fullWidth margin="normal" size="small">
                <InputLabel>نوع الحساب</InputLabel>
                <Select
                  name="role"
                  value={formData.role}
                  onChange={handleChange}
                  label="نوع الحساب"
                  sx={{
                    borderRadius: 2,
                    backgroundColor: '#F8FAFC',
                    '&:hover .MuiOutlinedInput-notchedOutline': {
                      borderColor: '#7C3AED',
                    },
                    '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
                      borderColor: '#7C3AED',
                    },
                  }}
                >
                  <MenuItem value="ROLE_CUSTOMER">عميل</MenuItem>
                </Select>
              </FormControl>
              <Button
                type="submit"
                fullWidth
                variant="contained"
                sx={{
                  mt: 3,
                  mb: 2,
                  py: 1.3,
                  borderRadius: 2,
                  fontSize: '1rem',
                  fontWeight: 700,
                  background: 'linear-gradient(135deg, #7C3AED 0%, #2563EB 100%)',
                  boxShadow: '0 8px 24px rgba(124, 58, 237, 0.3)',
                  textTransform: 'none',
                  transition: 'all 0.3s ease',
                  '&:hover': {
                    transform: 'translateY(-2px)',
                    boxShadow: '0 12px 32px rgba(124, 58, 237, 0.4)',
                  },
                  '&:active': {
                    transform: 'translateY(0px)',
                  },
                }}
                disabled={loading}
              >
                {loading ? 'جاري التسجيل...' : 'إنشاء الحساب'}
              </Button>
              <Box sx={{ textAlign: 'center', mt: 2 }}>
                <Typography variant="body2" sx={{ color: '#666' }}>
                  لديك حساب بالفعل؟{' '}
                  <Link
                    to="/login"
                    style={{
                      textDecoration: 'none',
                      color: '#7C3AED',
                      fontWeight: 700,
                      cursor: 'pointer',
                      transition: 'all 0.3s ease',
                    }}
                    onMouseEnter={(e) => (e.target.style.opacity = '0.8')}
                    onMouseLeave={(e) => (e.target.style.opacity = '1')}
                  >
                    تسجيل الدخول
                  </Link>
                </Typography>
              </Box>
            </Box>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
};

export default Register;