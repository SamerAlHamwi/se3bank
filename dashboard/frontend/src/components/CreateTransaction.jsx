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
  MenuItem,
  CircularProgress,
} from '@mui/material';
import SendIcon from '@mui/icons-material/Send';
import api, { authApi } from '../services/api';

const CreateTransaction = () => {
  const [formData, setFormData] = useState({
    fromAccount: '',
    toAccount: '',
    amount: '',
    type: 'TRANSFER', // Default type
    description: '',
  });
  const [userAccounts, setUserAccounts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    fetchUserAccounts();
  }, []);

  const fetchUserAccounts = async () => {
    try {
      const userResponse = await authApi.get('/me');
      const accountsResponse = await api.get(`/accounts/user/${userResponse.data.userId}`);
      setUserAccounts(accountsResponse.data.filter(account => account.status === 'ACTIVE'));
    } catch (err) {
      setError('فشل في تحميل حسابات المستخدم');
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      await api.post('/transactions', {
        ...formData,
        amount: parseFloat(formData.amount),
      });
      setSuccess('تم إنشاء المعاملة بنجاح');
      setTimeout(() => {
        navigate('/dashboard/transactions');
      }, 2000);
    } catch (err) {
      setError(err.response?.data?.message || 'فشل في إنشاء المعاملة');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
      <Paper elevation={3} sx={{ p: 4, borderRadius: 3 }}>
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
            textAlign: 'center',
          }}
        >
          إنشاء معاملة جديدة
        </Typography>

        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

        <Box component="form" onSubmit={handleSubmit}>
          <Grid container spacing={3}>
            <Grid item xs={12}>
              <TextField
                select
                fullWidth
                label="من حساب"
                name="fromAccount"
                value={formData.fromAccount}
                onChange={handleChange}
                required
                variant="outlined"
              >
                {userAccounts.map((account) => (
                  <MenuItem key={account.id} value={account.accountNumber}>
                    {account.accountNumber} - {account.accountType} (${account.balance?.toFixed(2)})
                  </MenuItem>
                ))}
              </TextField>
            </Grid>

            <Grid item xs={12}>
              <TextField
                fullWidth
                label="إلى حساب (رقم الحساب)"
                name="toAccount"
                value={formData.toAccount}
                onChange={handleChange}
                required
                variant="outlined"
                placeholder="أدخل رقم الحساب المستلم"
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="المبلغ"
                name="amount"
                type="number"
                value={formData.amount}
                onChange={handleChange}
                required
                inputProps={{ min: 0.01, step: 0.01 }}
                variant="outlined"
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <TextField
                select
                fullWidth
                label="نوع المعاملة"
                name="type"
                value={formData.type}
                onChange={handleChange}
                required
                variant="outlined"
              >
                <MenuItem value="TRANSFER">تحويل</MenuItem>
                <MenuItem value="DEPOSIT">إيداع</MenuItem>
                <MenuItem value="WITHDRAWAL">سحب</MenuItem>
                <MenuItem value="PAYMENT">دفع</MenuItem>
              </TextField>
            </Grid>

            <Grid item xs={12}>
              <TextField
                fullWidth
                label="الوصف (اختياري)"
                name="description"
                value={formData.description}
                onChange={handleChange}
                multiline
                rows={3}
                variant="outlined"
              />
            </Grid>

            <Grid item xs={12}>
              <Button
                type="submit"
                variant="contained"
                fullWidth
                size="large"
                disabled={loading}
                startIcon={loading ? <CircularProgress size={20} color="inherit" /> : <SendIcon />}
                sx={{
                  py: 1.5,
                  fontSize: '1.1rem',
                  fontWeight: 700,
                  background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
                }}
              >
                {loading ? 'جاري المعالجة...' : 'إنشاء المعاملة'}
              </Button>
            </Grid>
          </Grid>
        </Box>
      </Paper>
    </Container>
  );
};

export default CreateTransaction;
