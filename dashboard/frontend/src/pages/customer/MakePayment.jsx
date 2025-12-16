import React, { useState } from 'react';
import { Box, Typography, Paper, TextField, Button, CircularProgress, Alert, MenuItem, FormControl, InputLabel, Select } from '@mui/material';
import api from '../../services/api';

const MakePayment = () => {
  const [provider, setProvider] = useState('stripe');
  const [amount, setAmount] = useState('');
  const [currency, setCurrency] = useState('USD');
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
      const paymentRequest = { 
        amount: parseFloat(amount),
        currency,
        description,
        provider
      };

      const response = await api.post('/api/payments/process', paymentRequest);
      setSuccess(`تمت معالجة الدفع بنجاح! معرف العملية: ${response.data.transactionId}`);
      
      // Clear form
      setAmount('');
      setDescription('');

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
            <InputLabel id="provider-label">مزود الخدمة</InputLabel>
            <Select
              labelId="provider-label"
              value={provider}
              label="مزود الخدمة"
              onChange={(e) => setProvider(e.target.value)}
            >
              <MenuItem value="stripe">Stripe</MenuItem>
              <MenuItem value="paypal">PayPal</MenuItem>
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
