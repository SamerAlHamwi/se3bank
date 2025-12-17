import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Typography, Paper, TextField, Button, CircularProgress, Alert, MenuItem, FormControl, InputLabel, Select } from '@mui/material';
import api from '../../services/api';

const MakePayment = () => {
  const [selectedAccountId, setSelectedAccountId] = useState('');
  const [recipient, setRecipient] = useState('');
  const [amount, setAmount] = useState('');
  const [currency, setCurrency] = useState('USD');
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
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const selectedAccount = userAccounts.find(acc => acc.id === selectedAccountId);
      
      const paymentRequest = { 
        accountNumber: selectedAccount ? selectedAccount.accountNumber : '',
        recipient,
        amount: parseFloat(amount),
        currency,
        description,
      };

      const response = await api.post('/api/payments/process', paymentRequest);
      setSuccess(`تمت معالجة الدفع بنجاح! معرف العملية: ${response.data.transactionId}`);
      
      // Clear form
      setSelectedAccountId('');
      setRecipient('');
      setAmount('');
      setCurrency('USD');
      setDescription('');
      
      // Refresh account balances
      const accountResponse = await api.get(`/accounts/user/${user.userId}`);
      setUserAccounts(accountResponse.data);

    } catch (err) {
      setError(err.response?.data?.message || 'فشل في معالجة الدفع');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: '#1E293B', mb: 3 }}>
        تنفيذ دفع
      </Typography>

      <Paper elevation={3} sx={{ p: 4, borderRadius: '16px', maxWidth: '600px', mx: 'auto' }}>
        <Box component="form" onSubmit={handleSubmit}>
          {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
          {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

          <FormControl fullWidth margin="normal">
            <InputLabel id="account-label">من حساب</InputLabel>
            <Select
              labelId="account-label"
              value={selectedAccountId}
              label="من حساب"
              onChange={(e) => setSelectedAccountId(e.target.value)}
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
            label="المستلم"
            value={recipient}
            onChange={(e) => setRecipient(e.target.value)}
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
            label="العملة"
            value={currency}
            onChange={(e) => setCurrency(e.target.value)}
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
          />

          <Button 
            type="submit" 
            variant="contained" 
            disabled={loading} 
            fullWidth 
            sx={{ mt: 3, py: 1.5, fontSize: '1rem' }}
          >
            {loading ? <CircularProgress size={24} color="inherit" /> : 'ادفع الآن'}
          </Button>
        </Box>
      </Paper>
    </Box>
  );
};

export default MakePayment;
