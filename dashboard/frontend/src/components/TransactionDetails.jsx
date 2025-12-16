import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Container,
  Paper,
  Typography,
  Box,
  CircularProgress,
  Alert,
  Grid,
  Divider,
  Button,
  Chip,
  Card,
  CardContent,
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import api from '../services/api';

const TransactionDetails = () => {
  const { transactionId } = useParams();
  const navigate = useNavigate();
  const [transaction, setTransaction] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [actionLoading, setActionLoading] = useState(false);
  const [actionMessage, setActionMessage] = useState({ type: '', text: '' });

  useEffect(() => {
    fetchTransactionDetails();
  }, [transactionId]);

  const fetchTransactionDetails = async () => {
    setLoading(true);
    try {
      const response = await api.get(`/transactions/${transactionId}`);
      setTransaction(response.data);
    } catch (err) {
      setError('فشل في تحميل تفاصيل المعاملة');
    } finally {
      setLoading(false);
    }
  };

  const handleAction = async (action) => {
    setActionLoading(true);
    setActionMessage({ type: '', text: '' });
    try {
      let response;
      if (action === 'approve') {
        response = await api.post(`/transactions/${transactionId}/approve`);
      } else if (action === 'reject') {
        response = await api.post(`/transactions/${transactionId}/reject`);
      } else if (action === 'cancel') {
        response = await api.post(`/transactions/${transactionId}/cancel`);
      } else if (action === 'process') {
         response = await api.post('/transactions/process-pending');
      }

      setActionMessage({ type: 'success', text: response.data.message || 'تم تنفيذ العملية بنجاح' });
      fetchTransactionDetails(); // Refresh details
    } catch (err) {
      setActionMessage({ type: 'error', text: err.response?.data?.message || 'فشل في تنفيذ العملية' });
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress size={50} sx={{ color: '#2563EB' }} />
      </Box>
    );
  }

  if (!transaction) {
    return (
      <Container maxWidth="md" sx={{ mt: 4 }}>
        <Alert severity="error">المعاملة غير موجودة</Alert>
      </Container>
    );
  }

  const getStatusColor = (status) => {
    switch (status) {
      case 'COMPLETED': return 'success';
      case 'PENDING': return 'warning';
      case 'REJECTED': return 'error';
      case 'CANCELLED': return 'default';
      default: return 'default';
    }
  };

  return (
    <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
      <Button
        startIcon={<ArrowBackIcon />}
        onClick={() => navigate(-1)}
        sx={{ mb: 2, color: '#666' }}
      >
        رجوع
      </Button>

      <Paper elevation={3} sx={{ p: 4, borderRadius: 3 }}>
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
          <Typography variant="h4" component="h1" fontWeight="700">
            تفاصيل المعاملة #{transaction.id}
          </Typography>
          <Chip
            label={transaction.status}
            color={getStatusColor(transaction.status)}
            sx={{ fontSize: '1rem', fontWeight: 'bold', height: 32 }}
          />
        </Box>

        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        {actionMessage.text && <Alert severity={actionMessage.type} sx={{ mb: 2 }}>{actionMessage.text}</Alert>}

        <Divider sx={{ mb: 3 }} />

        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <Typography variant="subtitle2" color="textSecondary">من حساب</Typography>
            <Typography variant="h6" fontWeight="600" gutterBottom>{transaction.fromAccount}</Typography>
          </Grid>
          <Grid item xs={12} md={6}>
            <Typography variant="subtitle2" color="textSecondary">إلى حساب</Typography>
            <Typography variant="h6" fontWeight="600" gutterBottom>{transaction.toAccount}</Typography>
          </Grid>
          <Grid item xs={12} md={6}>
            <Typography variant="subtitle2" color="textSecondary">المبلغ</Typography>
            <Typography variant="h5" color="primary" fontWeight="700" gutterBottom>
              ${transaction.amount?.toFixed(2)}
            </Typography>
          </Grid>
          <Grid item xs={12} md={6}>
            <Typography variant="subtitle2" color="textSecondary">تاريخ المعاملة</Typography>
            <Typography variant="h6" fontWeight="600" gutterBottom>
              {new Date(transaction.transactionDate).toLocaleString()}
            </Typography>
          </Grid>
          <Grid item xs={12} md={6}>
             <Typography variant="subtitle2" color="textSecondary">النوع</Typography>
             <Typography variant="h6" fontWeight="600" gutterBottom>{transaction.type}</Typography>
          </Grid>
          {transaction.description && (
            <Grid item xs={12}>
              <Typography variant="subtitle2" color="textSecondary">الوصف</Typography>
              <Typography variant="body1" gutterBottom>{transaction.description}</Typography>
            </Grid>
          )}
          {transaction.referenceId && (
            <Grid item xs={12} md={6}>
               <Typography variant="subtitle2" color="textSecondary">رقم المرجع</Typography>
               <Typography variant="body1" gutterBottom>{transaction.referenceId}</Typography>
            </Grid>
          )}
        </Grid>

        <Box mt={4} display="flex" gap={2} justifyContent="flex-end">
          {transaction.status === 'PENDING' && (
            <>
              <Button
                variant="contained"
                color="success"
                startIcon={<CheckCircleIcon />}
                onClick={() => handleAction('approve')}
                disabled={actionLoading}
              >
                موافقة
              </Button>
              <Button
                variant="contained"
                color="error"
                startIcon={<CancelIcon />}
                onClick={() => handleAction('reject')}
                disabled={actionLoading}
              >
                رفض
              </Button>
               <Button
                variant="contained"
                color="warning"
                startIcon={<AccessTimeIcon />}
                onClick={() => handleAction('process')}
                disabled={actionLoading}
              >
                 معالجة المعلقة
              </Button>
            </>
          )}
           {transaction.status !== 'CANCELLED' && transaction.status !== 'REJECTED' && transaction.status !== 'COMPLETED' && (
            <Button
                variant="outlined"
                color="error"
                startIcon={<CancelIcon />}
                onClick={() => handleAction('cancel')}
                disabled={actionLoading}
            >
                إلغاء المعاملة
            </Button>
           )}
        </Box>
      </Paper>
    </Container>
  );
};

export default TransactionDetails;
