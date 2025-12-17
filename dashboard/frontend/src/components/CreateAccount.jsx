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
import api from '../services/api';

const CreateAccount = () => {
  const [formData, setFormData] = useState({
    accountType: '',
    userId: '',
    initialBalance: 0,
    interestRate: 0,
    overdraftLimit: 0,
    minimumBalance: 0,
    monthlyWithdrawalLimit: 0,
    riskLevel: '',
    investmentType: '',
    loanAmount: 0,
    loanTermMonths: 0,
    annualInterestRate: 0,
  });
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await api.get('/users');
        setUsers(response.data);
      } catch (err) {
        setError('فشل في تحميل قائمة المستخدمين');
      }
    };
    fetchUsers();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name.includes('Amount') || name.includes('Balance') || name.includes('Rate') || name.includes('Limit')
        ? parseFloat(value) || 0
        : (value === undefined || value === null ? '' : value)
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await api.post('/accounts', formData);
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

  const riskLevels = [
    { value: 'LOW', label: 'منخفض' },
    { value: 'MEDIUM', label: 'متوسط' },
    { value: 'HIGH', label: 'عالي' },
  ];

  const investmentTypes = [
    { value: 'STOCKS', label: 'أسهم' },
    { value: 'BONDS', label: 'سندات' },
    { value: 'MUTUAL_FUNDS', label: 'صناديق استثمار' },
    { value: 'CRYPTO', label: 'عملات رقمية' },
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
          إنشاء حساب جديد
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
                <FormControl fullWidth required>
                  <InputLabel sx={{ fontSize: '0.95rem' }}>المستخدم</InputLabel>
                  <Select
                    name="userId"
                    value={formData.userId}
                    onChange={handleChange}
                    label="المستخدم"
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
                    {users.map((user) => (
                      <MenuItem key={user.id} value={user.id}>
                        {user.username} ({user.id})
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="الرصيد الأولي"
                  name="initialBalance"
                  type="number"
                  value={formData.initialBalance}
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
                  label="معدل الفائدة (%)"
                  name="interestRate"
                  type="number"
                  value={formData.interestRate}
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
                  label="حد السحب على المكشوف"
                  name="overdraftLimit"
                  type="number"
                  value={formData.overdraftLimit}
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
                  label="الحد الأدنى للرصيد"
                  name="minimumBalance"
                  type="number"
                  value={formData.minimumBalance}
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

              {/* Savings Account Fields */}
              {formData.accountType === 'SAVINGS' && (
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="حد السحب الشهري"
                    name="monthlyWithdrawalLimit"
                    type="number"
                    value={formData.monthlyWithdrawalLimit}
                    onChange={handleChange}
                    inputProps={{ min: 0 }}
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
              )}

              {/* Investment Account Fields */}
              {formData.accountType === 'INVESTMENT' && (
                <>
                  <Grid item xs={12} sm={6}>
                    <FormControl fullWidth>
                      <InputLabel sx={{ fontSize: '0.95rem' }}>مستوى المخاطر</InputLabel>
                      <Select
                        name="riskLevel"
                        value={formData.riskLevel}
                        onChange={handleChange}
                        label="مستوى المخاطر"
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
                        {riskLevels.map((level) => (
                          <MenuItem key={level.value} value={level.value}>
                            {level.label}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <FormControl fullWidth>
                      <InputLabel sx={{ fontSize: '0.95rem' }}>نوع الاستثمار</InputLabel>
                      <Select
                        name="investmentType"
                        value={formData.investmentType}
                        onChange={handleChange}
                        label="نوع الاستثمار"
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
                        {investmentTypes.map((type) => (
                          <MenuItem key={type.value} value={type.value}>
                            {type.label}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  </Grid>
                </>
              )}

              {/* Loan Account Fields */}
              {formData.accountType === 'LOAN' && (
                <>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="مبلغ القرض"
                      name="loanAmount"
                      type="number"
                      value={formData.loanAmount}
                      onChange={handleChange}
                      inputProps={{ min: 0, step: 0.01 }}
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
                      label="مدة القرض (أشهر)"
                      name="loanTermMonths"
                      type="number"
                      value={formData.loanTermMonths}
                      onChange={handleChange}
                      inputProps={{ min: 1 }}
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
                      label="معدل الفائدة السنوي (%)"
                      name="annualInterestRate"
                      type="number"
                      value={formData.annualInterestRate}
                      onChange={handleChange}
                      inputProps={{ min: 0, step: 0.01 }}
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
                </>
              )}

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