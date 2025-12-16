import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Typography, Paper, TextField, Button, CircularProgress, Alert, MenuItem, FormControl, InputLabel, Select } from '@mui/material';
import api from '../../services/api';

const Transfer = () => {
  const [fromAccount, setFromAccount] = useState('');
  const [toAccountNumber, setToAccountNumber] = useState('');
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
        const response = await api.get(`/api/accounts/user/${user.id}`);
        setUserAccounts(response.data);
      } catch (err) {
        setError('فشل في جلب حسابات المستخدم');
      }
    };

    fetchUserAccounts();
  }, [user, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const request = { 
        fromAccountNumber: userAccounts.find(acc => acc.id === fromAccount)?.accountNumber,
        toAccountNumber,
        amount: parseFloat(amount),
        description
      };

      await api.post('/api/accounts/transfer', request);
      setSuccess('تم طلب التحويل بنجاح! قد يتطلب الأمر موافقة المدير.');
      
      // Clear form
      setFromAccount('');
      setToAccountNumber('');
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
        تحويل الأموال
      </Typography>

      <Paper elevation={3} sx={{ p: 4, borderRadius: '16px' }}>
        <Box component="form" onSubmit={handleSubmit}>
          {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
          {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

          <FormControl fullWidth margin="normal">
            <InputLabel id="from-account-label">من حساب</InputLabel>
            <Select
              labelId="from-account-label"
              id="fromAccount"
              value={fromAccount}
              label="من حساب"
              onChange={(e) => setFromAccount(e.target.value)}
              required
            >
              {userAccounts.map(acc => (
                <MenuItem key={acc.id} value={acc.id}>
                  {acc.accountType} - {acc.accountNumber} (${acc.balance.toLocaleString()})
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <TextField
            label="إلى رقم الحساب"
            value={toAccountNumber}
            onChange={(e) => setToAccountNumber(e.target.value)}
            fullWidth
            required
            margin="normal"
          />

          <TextField
            label="المبلغ"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            type="number"
            fullWidth
            required
            margin="normal"
          />

          <TextField
            label="الوصف"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            fullWidth
            required
            margin="normal"
            multiline
            rows={3}
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

export default Transfer;
