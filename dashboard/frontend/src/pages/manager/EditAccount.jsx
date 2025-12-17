import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Box, Typography, Paper, TextField, Button, CircularProgress, Alert, MenuItem, FormControl, InputLabel, Select } from '@mui/material';
import api from '../../services/api';

const EditAccount = () => {
  const { accountId } = useParams();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    status: '',
    interestRate: '',
    overdraftLimit: '',
    minimumBalance: '',
    balance: ''
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    const fetchAccount = async () => {
      try {
        const response = await api.get(`/accounts/${accountId}`);
        const { status, interestRate, overdraftLimit, minimumBalance, balance } = response.data;
        setFormData({ status, interestRate, overdraftLimit, minimumBalance, balance });
      } catch (err) {
        setError('فشل في تحميل بيانات الحساب');
      } finally {
        setLoading(false);
      }
    };
    fetchAccount();
  }, [accountId]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const requestData = {
        ...formData,
        interestRate: formData.interestRate ? parseFloat(formData.interestRate) : null,
        overdraftLimit: formData.overdraftLimit ? parseFloat(formData.overdraftLimit) : null,
        minimumBalance: formData.minimumBalance ? parseFloat(formData.minimumBalance) : null,
        balance: formData.balance ? parseFloat(formData.balance) : null,
      }
      await api.put(`/accounts/${accountId}`, requestData);
      setSuccess('تم تحديث الحساب بنجاح!');
      setTimeout(() => navigate('/manager/all-accounts'), 1500);
    } catch (err) {
      setError(err.response?.data?.message || 'فشل في تحديث الحساب');
    } finally {
      setLoading(false);
    }
  };

  if (loading && !formData.status) {
    return <Box sx={{ display: 'flex', justifyContent: 'center' }}><CircularProgress /></Box>;
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: '#1E293B', mb: 3 }}>
        تعديل الحساب
      </Typography>

      <Paper elevation={3} sx={{ p: 4, borderRadius: '16px', maxWidth: '600px', mx: 'auto' }}>
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}
        
        <Box component="form" onSubmit={handleSubmit}>
            <FormControl fullWidth margin="normal">
                <InputLabel>الحالة</InputLabel>
                <Select name="status" value={formData.status} onChange={handleChange} label="الحالة">
                    <MenuItem value="ACTIVE">نشط</MenuItem>
                    <MenuItem value="FROZEN">مجمد</MenuItem>
                    <MenuItem value="CLOSED">مغلق</MenuItem>
                </Select>
            </FormControl>
            <TextField name="balance" label="الرصيد" value={formData.balance} onChange={handleChange} type="number" fullWidth margin="normal" />
            <TextField name="interestRate" label="معدل الفائدة (%)" value={formData.interestRate} onChange={handleChange} type="number" fullWidth margin="normal" />
            <TextField name="overdraftLimit" label="حد السحب على المكشوف" value={formData.overdraftLimit} onChange={handleChange} type="number" fullWidth margin="normal" />
            <TextField name="minimumBalance" label="الحد الأدنى للرصيد" value={formData.minimumBalance} onChange={handleChange} type="number" fullWidth margin="normal" />
          
            <Button type="submit" variant="contained" disabled={loading} fullWidth sx={{ mt: 3, py: 1.5 }}>
                {loading ? <CircularProgress size={24} color="inherit" /> : 'حفظ التعديلات'}
            </Button>
        </Box>
      </Paper>
    </Box>
  );
};

export default EditAccount;
