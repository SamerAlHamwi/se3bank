import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Paper,
  TextField,
  Button,
  Typography,
  Box,
  Alert,
  Grid,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  CircularProgress,
} from '@mui/material';
import SaveIcon from '@mui/icons-material/Save';
import CancelIcon from '@mui/icons-material/Cancel';
import api, { authApi } from '../services/api';
import bankingService from '../services/bankingService';

const CreateAccount = () => {
  const [formData, setFormData] = useState({
    accountType: '',
    userId: '',
    initialDeposit: 0, // Using initialDeposit to match Facade API
    currency: 'USD'
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    // Fetch current user ID to pre-fill
    const fetchCurrentUser = async () => {
      try {
        const response = await authApi.get('/me');
        setFormData(prev => ({ ...prev, userId: response.data.userId }));
      } catch (err) {
        setError('فشل في تحميل بيانات المستخدم');
      }
    };
    fetchCurrentUser();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'initialDeposit' || name === 'userId' ? parseFloat(value) || 0 : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      // Use Facade Service to open account
      await bankingService.openAccount(formData);
      navigate('..');
    } catch (err) {
      setError(err.response?.data?.message || 'فشل في إنشاء الحساب');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    navigate('..');
  };

  const accountTypes = [
    { value: 'SAVINGS', label: 'حساب توفير' },
    { value: 'CHECKING', label: 'حساب جاري' },
    { value: 'LOAN', label: 'حساب قرض' },
    { value: 'INVESTMENT', label: 'حساب استثمار' },
    { value: 'BUSINESS', label: 'حساب تجاري' },
  ];

  return (
    <Container maxWidth="md">
      <Box sx={{ mb: 4 }}>
        <Typography 
          variant="h4" 
          component="h1" 
          gutterBottom
          sx={{
            fontWeight: 700,
            background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
            backgroundClip: 'text',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent',
            mb: 3,
          }}
        >
          إنشاء حساب جديد (واجهة مبسطة)
        </Typography>

        <Paper 
          sx={{ 
            p: { xs: 2.5, sm: 3.5 },
            borderRadius: 3,
            boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)',
            border: '1px solid rgba(0, 0, 0, 0.05)',
          }}
        >
          {error && (
            <Alert 
              severity="error" 
              sx={{ 
                mb: 3,
                borderRadius: 2,
                boxShadow: '0 4px 12px rgba(239, 68, 68, 0.2)',
              }}
            >
              {error}
            </Alert>
          )}

          <Box component="form" onSubmit={handleSubmit}>
            <Grid container spacing={2.5}>
              <Grid item xs={12} sm={6}>
                <FormControl fullWidth required>
                  <InputLabel sx={{ fontSize: '0.95rem' }}>نوع الحساب</InputLabel>
                  <Select
                    name="accountType"
                    value={formData.accountType}
                    onChange={handleChange}
                    label="نوع الحساب"
                    sx={{
                      borderRadius: 2,
                      backgroundColor: '#F8FAFC',
                      '&:hover .MuiOutlinedInput-notchedOutline': {
                        borderColor: '#2563EB',
                      },
                      '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
                        borderColor: '#2563EB',
                      },
                    }}
                  >
                    {accountTypes.map((type) => (
                      <MenuItem key={type.value} value={type.value}>
                        {type.label}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="معرف المستخدم"
                  name="userId"
                  type="number"
                  value={formData.userId}
                  onChange={handleChange}
                  required
                  variant="outlined"
                  sx={{
                    '& .MuiOutlinedInput-root': {
                      borderRadius: 2,
                      backgroundColor: '#F8FAFC',
                      '&:hover fieldset': {
                        borderColor: '#2563EB',
                      },
                      '&.Mui-focused fieldset': {
                        borderColor: '#2563EB',
                        boxShadow: '0 0 0 3px rgba(37, 99, 235, 0.1)',
                      },
                    },
                  }}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="الإيداع الأولي"
                  name="initialDeposit"
                  type="number"
                  value={formData.initialDeposit}
                  onChange={handleChange}
                  inputProps={{ min: 0, step: 0.01 }}
                  variant="outlined"
                  sx={{
                    '& .MuiOutlinedInput-root': {
                      borderRadius: 2,
                      backgroundColor: '#F8FAFC',
                      '&:hover fieldset': {
                        borderColor: '#2563EB',
                      },
                      '&.Mui-focused fieldset': {
                        borderColor: '#2563EB',
                        boxShadow: '0 0 0 3px rgba(37, 99, 235, 0.1)',
                      },
                    },
                  }}
                />
              </Grid>
               
               <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="العملة"
                  name="currency"
                  value={formData.currency}
                  onChange={handleChange}
                  variant="outlined"
                  disabled
                  sx={{
                    '& .MuiOutlinedInput-root': {
                      borderRadius: 2,
                      backgroundColor: '#F0F0F0',
                    },
                  }}
                />
              </Grid>

              <Grid item xs={12}>
                <Box sx={{ display: 'flex', gap: 2, mt: 2 }}>
                  <Button
                    type="submit"
                    variant="contained"
                    startIcon={loading ? <CircularProgress size={20} /> : <SaveIcon />}
                    disabled={loading}
                    sx={{
                      py: 1.3,
                      px: 3,
                      borderRadius: 2,
                      fontSize: '1rem',
                      fontWeight: 700,
                      background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
                      boxShadow: '0 8px 24px rgba(37, 99, 235, 0.3)',
                      textTransform: 'none',
                      transition: 'all 0.3s ease',
                      '&:hover': {
                        transform: 'translateY(-2px)',
                        boxShadow: '0 12px 32px rgba(37, 99, 235, 0.4)',
                      },
                    }}
                  >
                    {loading ? 'جاري الإنشاء...' : 'إنشاء الحساب'}
                  </Button>
                  <Button
                    variant="outlined"
                    startIcon={<CancelIcon />}
                    onClick={handleCancel}
                    disabled={loading}
                    sx={{
                      py: 1.3,
                      px: 3,
                      borderRadius: 2,
                      fontSize: '1rem',
                      fontWeight: 700,
                      borderColor: '#E5E7EB',
                      color: '#666',
                      transition: 'all 0.3s ease',
                      '&:hover': {
                        backgroundColor: '#F8FAFC',
                        borderColor: '#2563EB',
                        color: '#2563EB',
                      },
                    }}
                  >
                    إلغاء
                  </Button>
                </Box>
              </Grid>
            </Grid>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
};

export default CreateAccount;
