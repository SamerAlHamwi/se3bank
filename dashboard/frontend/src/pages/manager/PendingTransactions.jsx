import React, { useState, useEffect } from 'react';
import { 
  Box, 
  Typography, 
  Paper, 
  CircularProgress, 
  Alert, 
  Button, 
  Dialog, 
  DialogTitle, 
  DialogContent, 
  TextField, 
  DialogActions,
  Grid,
  Card,
  CardContent,
  Chip,
  Avatar,
  Divider
} from '@mui/material';
import api from '../../services/api';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import SwapHorizIcon from '@mui/icons-material/SwapHoriz';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import TrendingDownIcon from '@mui/icons-material/TrendingDown';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';

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

  const handleApprove = async (id) => {
    try {
      await api.post(`/transactions/${id}/approve`, { managerId: user.userId, comments: 'Approved' });
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
      await api.post(`/transactions/${selectedTx.id}/reject`, {
        managerId: user.userId,
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

  const getTransactionIcon = (type) => {
    if (type === 'TRANSFER') return <SwapHorizIcon sx={{ color: 'white' }} />;
    if (type === 'DEPOSIT') return <ArrowDownwardIcon sx={{ color: 'white' }} />;
    return <ArrowUpwardIcon sx={{ color: 'white' }} />;
  };

  const getTransactionColor = (type) => {
    if (type === 'TRANSFER') return '#3B82F6'; // Blue
    if (type === 'DEPOSIT') return '#10B981'; // Green
    return '#F59E0B'; // Amber
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    try {
        const date = new Date(dateString);
        return new Intl.DateTimeFormat('ar-EG', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        }).format(date);
    } catch (e) {
        return dateString;
    }
  };

  if (loading) {
    return <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}><CircularProgress /></Box>;
  }

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Typography variant="h4" sx={{ fontWeight: 'bold', color: '#1E293B' }}>
          المعاملات المعلقة للموافقة
        </Typography>
        <Button 
          variant='contained' 
          onClick={handleProcessAll} 
          color='secondary'
          disabled={transactions.length === 0}
          sx={{ borderRadius: 2, px: 3 }}
        >
            معالجة الكل
        </Button>
      </Box>
      
      {error && <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>{error}</Alert>}

      {transactions.length === 0 ? (
        <Paper 
          elevation={0} 
          sx={{ 
            p: 6, 
            textAlign: 'center', 
            borderRadius: 4, 
            bgcolor: '#F8FAFC', 
            border: '2px dashed #E2E8F0' 
          }}
        >
          <Typography variant="h6" color="text.secondary">لا يوجد معاملات معلقة حالياً</Typography>
        </Paper>
      ) : (
        <Grid container spacing={3}>
          {transactions.map((tx) => {
            const statusColor = getTransactionColor(tx.transactionType);
            
            return (
              <Grid item xs={12} key={tx.transactionId}>
                <Card 
                  elevation={0}
                  sx={{ 
                    borderRadius: 3,
                    border: '1px solid #E2E8F0',
                    transition: 'all 0.2s ease-in-out',
                    '&:hover': {
                      transform: 'translateY(-2px)',
                      boxShadow: '0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1)',
                      borderColor: 'transparent'
                    },
                    overflow: 'visible'
                  }}
                >
                  <CardContent sx={{ p: 3, '&:last-child': { pb: 3 } }}>
                    <Grid container alignItems="center" spacing={3}>
                      {/* Icon Column */}
                      <Grid item xs="auto">
                        <Avatar
                          sx={{
                            bgcolor: statusColor,
                            width: 56,
                            height: 56,
                            boxShadow: `0 4px 6px -1px ${statusColor}40`
                          }}
                        >
                          {getTransactionIcon(tx.transactionType)}
                        </Avatar>
                      </Grid>

                      {/* Details Column */}
                      <Grid item xs>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 0.5 }}>
                          <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#1E293B' }}>
                            {tx.transactionType}
                          </Typography>
                          <Chip 
                            label="معلق" 
                            size="small" 
                            sx={{ 
                              bgcolor: '#FEF3C7',
                              color: '#D97706',
                              fontWeight: 600,
                              fontSize: '0.75rem',
                              height: 24
                            }} 
                          />
                        </Box>
                        
                        <Grid container spacing={2} sx={{ mt: 0.5 }}>
                          <Grid item xs={12} sm="auto">
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, color: 'text.secondary' }}>
                              <Typography variant="body2" sx={{ fontFamily: 'monospace', fontWeight: 500 }}>
                                من: {tx.fromAccount || 'N/A'}
                              </Typography>
                            </Box>
                          </Grid>
                          <Grid item xs={12} sm="auto">
                             <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, color: 'text.secondary' }}>
                              <Typography variant="body2" sx={{ fontFamily: 'monospace', fontWeight: 500 }}>
                                إلى: {tx.toAccount || 'N/A'}
                              </Typography>
                            </Box>
                          </Grid>
                          <Grid item xs={12} sm="auto">
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, color: 'text.secondary' }}>
                              <CalendarTodayIcon sx={{ fontSize: 16 }} />
                              <Typography variant="body2">
                                {formatDate(tx.createdAt)}
                              </Typography>
                            </Box>
                          </Grid>
                        </Grid>
                      </Grid>

                      {/* Amount and Actions Column */}
                      <Grid item xs={12} md="auto">
                        <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: { xs: 'flex-start', md: 'flex-end' }, gap: 2 }}>
                          <Typography 
                            variant="h5" 
                            sx={{ 
                              fontWeight: 'bold', 
                              color: statusColor,
                              display: 'flex',
                              alignItems: 'center',
                              gap: 0.5
                            }}
                          >
                             {tx.transactionType === 'DEPOSIT' ? <TrendingUpIcon /> : <TrendingDownIcon />}
                             ${tx.amount.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                          </Typography>

                          <Box sx={{ display: 'flex', gap: 1 }}>
                            <Button 
                              variant="contained" 
                              color="success" 
                              size="small"
                              startIcon={<CheckCircleIcon />}
                              onClick={() => handleApprove(tx.id)}
                              sx={{ borderRadius: 2, textTransform: 'none' }}
                            >
                              موافقة
                            </Button>
                            <Button 
                              variant="outlined" 
                              color="error" 
                              size="small"
                              startIcon={<CancelIcon />}
                              onClick={() => handleOpenRejectDialog(tx)}
                              sx={{ borderRadius: 2, textTransform: 'none' }}
                            >
                              رفض
                            </Button>
                          </Box>
                        </Box>
                      </Grid>
                    </Grid>
                  </CardContent>
                </Card>
              </Grid>
            );
          })}
        </Grid>
      )}

      {/* Rejection Dialog */}
      <Dialog 
        open={open} 
        onClose={handleCloseRejectDialog}
        PaperProps={{ sx: { borderRadius: 3, width: '100%', maxWidth: 400 } }}
      >
        <DialogTitle sx={{ fontWeight: 'bold' }}>رفض المعاملة</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="سبب الرفض"
            type="text"
            fullWidth
            variant="outlined"
            multiline
            rows={3}
            value={rejectionReason}
            onChange={(e) => setRejectionReason(e.target.value)}
            sx={{ mt: 1 }}
          />
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button onClick={handleCloseRejectDialog} sx={{ color: 'text.secondary' }}>إلغاء</Button>
          <Button onClick={handleReject} variant="contained" color="error" disableElevation>
            تأكيد الرفض
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default PendingTransactions;