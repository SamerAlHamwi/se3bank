import React, { useState } from 'react';
import { Box, Typography, Paper, TextField, Button, CircularProgress, Alert } from '@mui/material';
import api from '../../services/api';

const Withdraw = () => {
  const [accountNumber, setAccountNumber] = useState('');
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const request = { 
        fromAccountNumber: accountNumber,
        amount: parseFloat(amount),
        description,
        transactionType: 'WITHDRAWAL'
      };

      // The backend endpoint for creating a transaction can handle this
      const response = await api.post('/transactions', request);
      setSuccess(`تمت عملية السحب بنجاح! معرف المعاملة: ${response.data.transactionId}`);
      
      // Clear form
      setAccountNumber('');
      setAmount('');
      setDescription('');

    } catch (err) {
      setError(err.response?.data?.message || 'فشل في عملية السحب');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: '#1E293B', mb: 3 }}>
        تنفيذ عملية سحب
      </Typography>

      <Paper elevation={3} sx={{ p: 4, borderRadius: '16px', maxWidth: '600px', mx: 'auto' }}>
        <Box component="form" onSubmit={handleSubmit}>
          {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
          {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

          <TextField
            label="رقم الحساب للسحب منه"
            value={accountNumber}
            onChange={(e) => setAccountNumber(e.target.value)}
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
            {loading ? <CircularProgress size={24} color="inherit" /> : 'تنفيذ السحب'}
          </Button>
        </Box>
      </Paper>
    </Box>
  );
};

export default Withdraw;
