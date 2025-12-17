import React, { useState } from 'react';
import { Box, Typography, Paper, TextField, Button, CircularProgress, Alert } from '@mui/material';
import api from '../../services/api';

const CheckAccount = () => {
  const [accountNumber, setAccountNumber] = useState('');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setResult(null);
    try {
      const response = await api.get(`/accounts/exists/${accountNumber}`);
      setResult(response.data.exists);
    } catch (err) {
      setResult(false); // Assume not found on error
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: '#1E293B', mb: 3 }}>
        التحقق من وجود حساب
      </Typography>

      <Paper elevation={3} sx={{ p: 4, borderRadius: '16px', maxWidth: '600px', mx: 'auto' }}>
        <Box component="form" onSubmit={handleSubmit}>
          <TextField
            label="رقم الحساب للتحقق"
            value={accountNumber}
            onChange={(e) => setAccountNumber(e.target.value)}
            fullWidth
            required
            margin="normal"
          />
          <Button 
            type="submit" 
            variant="contained" 
            disabled={loading} 
            fullWidth 
            sx={{ mt: 2, py: 1.5, fontSize: '1rem' }}
          >
            {loading ? <CircularProgress size={24} color="inherit" /> : 'تحقق الآن'}
          </Button>
        </Box>
        
        {result !== null && (
            <Alert 
                severity={result ? 'success' : 'error'} 
                sx={{ mt: 3, fontWeight: 'bold' }}
            >
                {result ? 'الحساب موجود بالفعل.' : 'الحساب غير موجود.'}
            </Alert>
        )}
      </Paper>
    </Box>
  );
};

export default CheckAccount;
