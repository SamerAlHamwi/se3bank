import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Typography, Grid, Paper, CircularProgress, Alert } from '@mui/material';
import api from '../../services/api';
import AccountCard from '../../components/customer/AccountCard';

const CustomerDashboard = () => {
  const [accounts, setAccounts] = useState([]);
  const [totalBalance, setTotalBalance] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem('user'));

  useEffect(() => {
    if (!user) {
      navigate('/login');
      return;
    }

    const fetchDashboardData = async () => {
      setLoading(true);
      try {
        // Fetch user accounts
        const accountsResponse = await api.get(`/api/accounts/user/${user.id}`);
        setAccounts(accountsResponse.data);

        // Fetch total balance
        const totalBalanceResponse = await api.get(`/api/accounts/user/${user.id}/total-balance`);
        setTotalBalance(totalBalanceResponse.data.totalBalance);
        
        setError('');
      } catch (err) {
        setError(err.response?.data?.message || 'حدث خطأ أثناء جلب البيانات');
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, [user, navigate]);

  if (loading) {
    return <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}><CircularProgress /></Box>;
  }

  if (error) {
    return <Alert severity="error">{error}</Alert>;
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: '#1E293B', mb: 3 }}>
        لوحة التحكم
      </Typography>

      <Paper 
        elevation={3}
        sx={{ 
          p: 4, 
          mb: 4, 
          borderRadius: '16px',
          background: 'linear-gradient(135deg, #4F46E5 0%, #7C3AED 100%)',
          color: '#fff'
        }}
      >
        <Typography variant="h6" sx={{ opacity: 0.8, mb: 1 }}>الرصيد الكلي</Typography>
        <Typography variant="h3" sx={{ fontWeight: 'bold' }}>
          ${totalBalance.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
        </Typography>
      </Paper>

      <Typography variant="h5" gutterBottom sx={{ fontWeight: 'bold', color: '#334155', mb: 2 }}>
        حساباتي
      </Typography>
      
      <Grid container spacing={3}>
        {accounts.map(account => (
          <Grid item xs={12} md={6} lg={4} key={account.id}>
            <AccountCard account={account} />
          </Grid>
        ))}
      </Grid>
    </Box>
  );
};

export default CustomerDashboard;
