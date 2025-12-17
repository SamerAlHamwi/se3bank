import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Typography, Paper, CircularProgress, Alert, Grid, Card, CardContent, Chip, Avatar, Divider, Container } from '@mui/material';
import api from '../../services/api';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import SwapHorizIcon from '@mui/icons-material/SwapHoriz';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import TrendingDownIcon from '@mui/icons-material/TrendingDown';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';

const MyTransfers = () => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem('user'));

  useEffect(() => {
    if (!user) {
      navigate('/login');
      return;
    }

    const fetchTransactions = async () => {
      setLoading(true);
      try {
        const response = await api.get(`/transactions/user/${user.userId}/recent?limit=50`);
        setTransactions(response.data);
        setError('');
      } catch (err) {
        setError(err.response?.data?.message || 'فشل في جلب سجل المعاملات');
      } finally {
        setLoading(false);
      }
    };

    fetchTransactions();
  }, [navigate]);

  const getTransactionIcon = (type, isIncoming) => {
    if (type === 'TRANSFER') return <SwapHorizIcon sx={{ color: 'white' }} />;
    if (isIncoming) return <ArrowDownwardIcon sx={{ color: 'white' }} />;
    return <ArrowUpwardIcon sx={{ color: 'white' }} />;
  };

  const getTransactionColor = (type, isIncoming, status) => {
    if (status === 'FAILED') return '#EF4444'; // Red for failed
    if (type === 'TRANSFER') return '#3B82F6'; // Blue
    if (isIncoming) return '#10B981'; // Green
    return '#F59E0B'; // Amber for outgoing/withdrawal
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('ar-EG', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    }).format(date);
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
        <CircularProgress size={60} thickness={4} />
      </Box>
    );
  }

  if (error) {
    return (
      <Container maxWidth="md" sx={{ mt: 4 }}>
        <Alert severity="error" variant="filled" sx={{ borderRadius: 2 }}>{error}</Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ mb: 4, display: 'flex', alignItems: 'center', gap: 2 }}>
        <AccountBalanceWalletIcon sx={{ fontSize: 40, color: 'primary.main' }} />
        <Typography variant="h4" component="h1" sx={{ fontWeight: 'bold', color: '#1E293B' }}>
          سجل المعاملات
        </Typography>
      </Box>

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
          <Typography variant="h6" color="text.secondary">لا يوجد معاملات لعرضها حالياً</Typography>
        </Paper>
      ) : (
        <Grid container spacing={3}>
          {transactions.map((tx) => {
            const isIncoming = tx.amount > 0 && (tx.transactionType === 'DEPOSIT' || (tx.toAccount && tx.fromAccount !== tx.toAccount));
            const statusColor = getTransactionColor(tx.transactionType, isIncoming, tx.status);
            
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
                          {getTransactionIcon(tx.transactionType, isIncoming)}
                        </Avatar>
                      </Grid>

                      {/* Details Column */}
                      <Grid item xs>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 0.5 }}>
                          <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#1E293B' }}>
                            {tx.description || tx.transactionType}
                          </Typography>
                          <Chip 
                            label={tx.status} 
                            size="small" 
                            sx={{ 
                              bgcolor: tx.status === 'FAILED' ? '#FEF2F2' : '#F0FDF4',
                              color: tx.status === 'FAILED' ? '#DC2626' : '#166534',
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

                        {tx.failureReason && (
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mt: 1.5, color: '#EF4444', bgcolor: '#FEF2F2', p: 1, borderRadius: 1 }}>
                            <ErrorOutlineIcon fontSize="small" />
                            <Typography variant="body2" fontWeight="medium">
                              سبب الفشل: {tx.failureReason}
                            </Typography>
                          </Box>
                        )}
                      </Grid>

                      {/* Amount Column */}
                      <Grid item xs={12} sm="auto" sx={{ textAlign: { xs: 'left', sm: 'right' } }}>
                        <Typography 
                          variant="h5" 
                          sx={{ 
                            fontWeight: 'bold', 
                            color: isIncoming ? '#059669' : '#D97706',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: { xs: 'flex-start', sm: 'flex-end' },
                            gap: 0.5
                          }}
                        >
                          {isIncoming ? <TrendingUpIcon /> : <TrendingDownIcon />}
                          {isIncoming ? '+' : '-'}{tx.amount.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                        </Typography>
                        <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 0.5 }}>
                          رقم المعاملة: {tx.transactionId}
                        </Typography>
                      </Grid>
                    </Grid>
                  </CardContent>
                </Card>
              </Grid>
            );
          })}
        </Grid>
      )}
    </Container>
  );
};

export default MyTransfers;