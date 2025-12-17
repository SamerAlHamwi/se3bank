import React, { useState, useEffect } from 'react';
import { Box, Typography, Paper, CircularProgress, Alert, List, ListItem, ListItemText, Divider, Button, Dialog, DialogTitle, DialogContent, TextField, DialogActions } from '@mui/material';
import api from '../../services/api';

const PendingTransactions = () => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedTx, setSelectedTx] = useState(null);
  const [rejectionReason, setRejectionReason] = useState('');
  const [open, setOpen] = useState(false);
  const user = JSON.parse(localStorage.getItem('user'));

  const fetchPendingTransactions = async () => {
    setLoading(true);
    try {
      const response = await api.get('/transactions/pending-approval');
      setTransactions(response.data);
      setError('');
    } catch (err) {
      setError(err.response?.data?.message || 'فشل في جلب المعاملات المعلقة');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPendingTransactions();
  }, []);

  const handleApprove = async (transactionId) => {
    try {
      await api.post(`/transactions/${transactionId}/approve`, { managerId: user.id, comments: 'Approved' });
      fetchPendingTransactions(); // Re-fetch the list
    } catch (err) {
      setError('فشل في الموافقة على المعاملة');
    }
  };

  const handleOpenRejectDialog = (tx) => {
    setSelectedTx(tx);
    setOpen(true);
  };

  const handleCloseRejectDialog = () => {
    setOpen(false);
    setSelectedTx(null);
    setRejectionReason('');
  };

  const handleReject = async () => {
    if (!selectedTx || !rejectionReason) return;
    try {
      await api.post(`/transactions/${selectedTx.transactionId}/reject`, {
        managerId: user.id,
        reason: rejectionReason,
        comments: 'Rejected by manager'
      });
      handleCloseRejectDialog();
      fetchPendingTransactions(); // Re-fetch the list
    } catch (err) {
      setError('فشل في رفض المعاملة');
    }
  };

  const handleProcessAll = async () => {
      try {
          await api.post('/transactions/process-pending');
          fetchPendingTransactions();
      } catch (err) {
          setError('فشل في معالجة المعاملات المعلقة');
      }
  }

  if (loading) {
    return <Box sx={{ display: 'flex', justifyContent: 'center' }}><CircularProgress /></Box>;
  }

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: '#1E293B' }}>
          المعاملات المعلقة للموافقة
        </Typography>
        <Button variant='contained' onClick={handleProcessAll} color='secondary'>
            معالجة الكل
        </Button>
      </Box>
      
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Paper elevation={2} sx={{ borderRadius: '12px', overflow: 'hidden' }}>
        <List disablePadding>
          {transactions.length > 0 ? (
            transactions.map((tx, index) => (
              <React.Fragment key={tx.transactionId}>
                <ListItem sx={{ py: 2 }}>
                  <ListItemText 
                    primary={<Typography sx={{ fontWeight: 'bold' }}>{tx.transactionType} - ${tx.amount.toLocaleString()}</Typography>}
                    secondary={`من: ${tx.fromAccountNumber || 'N/A'} | إلى: ${tx.toAccountNumber || 'N/A'} | ${new Date(tx.date).toLocaleDateString()}`}
                  />
                  <Box>
                    <Button variant='contained' color='success' sx={{ ml: 2 }} onClick={() => handleApprove(tx.transactionId)}>موافقة</Button>
                    <Button variant='contained' color='error' onClick={() => handleOpenRejectDialog(tx)}>رفض</Button>
                  </Box>
                </ListItem>
                {index < transactions.length - 1 && <Divider component="li" />}
              </React.Fragment>
            ))
          ) : (
            <ListItem sx={{ py: 3 }}>
              <ListItemText primary="لا يوجد معاملات معلقة حالياً." align='center' />
            </ListItem>
          )}
        </List>
      </Paper>

      {/* Rejection Dialog */}
      <Dialog open={open} onClose={handleCloseRejectDialog}>
        <DialogTitle>رفض المعاملة</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="سبب الرفض"
            type="text"
            fullWidth
            variant="standard"
            value={rejectionReason}
            onChange={(e) => setRejectionReason(e.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseRejectDialog}>إلغاء</Button>
          <Button onClick={handleReject} color="error">رفض</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default PendingTransactions;
