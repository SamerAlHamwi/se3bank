import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {TextField, Box, Typography, Paper, Button, CircularProgress, Alert, MenuItem, FormControl, InputLabel, Select } from '@mui/material';
import api from '../../services/api';

const InternalTransfer = () => {
  const [fromAccount, setFromAccount] = useState('');
  const [toAccount, setToAccount] = useState('');
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const [userAccounts, setUserAccounts] = useState([]);
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem('user'));

  useEffect(() => {
    if (!user) {
      navigate('/login');
      return;
    }

    const fetchUserAccounts = async () => {
      try {
        const response = await api.get(`/accounts/user/${user.userId}`);
        setUserAccounts(response.data);
      } catch (err) {
        setError('فشل في جلب حسابات المستخدم');
      }
    };

    fetchUserAccounts();
  }, [navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (fromAccount === toAccount) {
        setError('لا يمكن التحويل إلى نفس الحساب.');
        return;
    }
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const fromAccountNumber = userAccounts.find(acc => acc.id === fromAccount)?.accountNumber;
      const toAccountNumber = userAccounts.find(acc => acc.id === toAccount)?.accountNumber;

      const request = { 
        fromAccountNumber,
        toAccountNumber,
        amount: parseFloat(amount),
        description
      };

      await api.post('/accounts/transfer', request);
      setSuccess('تم التحويل بنجاح!');
      
      // Refresh account balances in the dropdown
      const response = await api.get(`/accounts/user/${user.userId}`);
      setUserAccounts(response.data);

      // Clear form
      setFromAccount('');
      setToAccount('');
      setAmount('');
      setDescription('');

    } catch (err) {
      setError(err.response?.data?.message || 'فشل في عملية التحويل');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: '#1E293B', mb: 3 }}>
        التحويل بين حساباتي
      </Typography>

      <Paper elevation={3} sx={{ p: 4, borderRadius: '16px', maxWidth: '600px', mx: 'auto' }}>
        <Box component="form" onSubmit={handleSubmit}>
          {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
          {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

          <FormControl fullWidth margin="normal">
            <InputLabel id="from-account-label">من حساب</InputLabel>
            <Select
              labelId="from-account-label"
              value={fromAccount}
              label="من حساب"
              onChange={(e) => setFromAccount(e.target.value)}
              required
            >
              {userAccounts.map(acc => (
                <MenuItem key={acc.id} value={acc.id}>
                  {`${acc.accountType} - ${acc.accountNumber} ($${acc.balance.toLocaleString()})`}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <FormControl fullWidth margin="normal">
            <InputLabel id="to-account-label">إلى حساب</InputLabel>
            <Select
              labelId="to-account-label"
              value={toAccount}
              label="إلى حساب"
              onChange={(e) => setToAccount(e.target.value)}
              required
            >
              {userAccounts.map(acc => (
                <MenuItem key={acc.id} value={acc.id}>
                  {`${acc.accountType} - ${acc.accountNumber} ($${acc.balance.toLocaleString()})`}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <TextField
            label="المبلغ"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            type="number"
            fullWidth
            required
            margin="normal"
            InputProps={{ inputProps: { min: 0.01 } }}
          />

          <TextField
            label="الوصف (اختياري)"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            fullWidth
            margin="normal"
          />

          <Button 
            type="submit" 
            variant="contained" 
            disabled={loading} 
            fullWidth 
            sx={{ mt: 3, py: 1.5, fontSize: '1rem' }}
          >
            {loading ? <CircularProgress size={24} color="inherit" /> : 'تنفيذ التحويل'}
          </Button>
        </Box>
      </Paper>
    </Box>
  );
};

export default InternalTransfer;
