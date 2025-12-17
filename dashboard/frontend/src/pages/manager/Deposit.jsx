import React, { useState, useEffect } from 'react';
import { Box, Typography, Paper, TextField, Button, CircularProgress, Alert, Autocomplete } from '@mui/material';
import api from '../../services/api';

const Deposit = () => {
  const [accounts, setAccounts] = useState([]);
  const [selectedAccount, setSelectedAccount] = useState(null);
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    const fetchAccounts = async () => {
      try {
        const response = await api.get('/accounts');
        setAccounts(response.data);
      } catch (err) {
        console.error('فشل في تحميل الحسابات', err);
      }
    };
    fetchAccounts();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedAccount) {
      setError('الرجاء اختيار الحساب');
      return;
    }

    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const request = { 
        toAccountNumber: selectedAccount.accountNumber,
        amount: parseFloat(amount),
        description,
        transactionType: 'DEPOSIT'
      };

      const response = await api.post('/transactions', request);
      setSuccess(`تمت عملية الإيداع بنجاح! معرف المعاملة: ${response.data.transactionId}`);
      
      // Clear form
      setSelectedAccount(null);
      setAmount('');
      setDescription('');

    } catch (err) {
      setError(err.response?.data?.message || 'فشل في عملية الإيداع');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: '#1E293B', mb: 3 }}>
        تنفيذ عملية إيداع
      </Typography>

      <Paper elevation={3} sx={{ p: 4, borderRadius: '16px', maxWidth: '600px', mx: 'auto' }}>
        <Box component="form" onSubmit={handleSubmit}>
          {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
          {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

          <Autocomplete
            options={accounts}
            getOptionLabel={(option) => `${option.accountNumber} (${option.accountType}) - $${option.balance}`}
            value={selectedAccount}
            onChange={(event, newValue) => {
              setSelectedAccount(newValue);
            }}
            renderInput={(params) => (
              <TextField
                {...params}
                label="رقم الحساب للإيداع فيه"
                required
                margin="normal"
                fullWidth
              />
            )}
            renderOption={(props, option) => (
                <li {...props} key={option.id}>
                    <Box>
                        <Typography variant="body1">{option.accountNumber}</Typography>
                        <Typography variant="caption" color="text.secondary">
                             نوع: {option.accountType} | الرصيد: ${option.balance} | مستخدم: {option.userId}
                        </Typography>
                    </Box>
                </li>
            )}
            noOptionsText="لا توجد حسابات"
          />

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
            label="الوصف"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            fullWidth
            required
            margin="normal"
          />

          <Button 
            type="submit" 
            variant="contained" 
            disabled={loading} 
            fullWidth 
            sx={{ mt: 3, py: 1.5, fontSize: '1rem' }}
          >
            {loading ? <CircularProgress size={24} color="inherit" /> : 'تنفيذ الإيداع'}
          </Button>
        </Box>
      </Paper>
    </Box>
  );
};

export default Deposit;
