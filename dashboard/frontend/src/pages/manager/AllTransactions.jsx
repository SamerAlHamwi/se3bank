import React, { useState, useEffect } from 'react';
import { Box, Typography, Paper, CircularProgress, Alert, Grid, Card, CardContent, Chip, Avatar, Container } from '@mui/material';
import api from '../../services/api';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import SwapHorizIcon from '@mui/icons-material/SwapHoriz';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import TrendingDownIcon from '@mui/icons-material/TrendingDown';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';

const AllTransactions = () => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchTransactions = async () => {
      setLoading(true);
      try {
        const response = await api.get('/transactions');
        // Transactions are already sorted by backend (newest first)
        setTransactions(response.data);
        setError('');
      } catch (err) {
        setError(err.response?.data?.message || 'فشل في جلب كل المعاملات');
      } finally {
        setLoading(false);
      }
    };

    fetchTransactions();
  }, []);

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
      <Container maxWidth="lg" sx={{ mt: 4 }}>
        <Alert severity="error" variant="filled" sx={{ borderRadius: 2 }}>{error}</Alert>
      </Container>
    );
  }

  return (
    <Box sx={{ p: 0, width: '100%' }}>
      <Box sx={{ mb: 4, display: 'flex', alignItems: 'center', gap: 2, pt: 1 }}>
        <AccountBalanceWalletIcon sx={{ fontSize: 32, color: 'primary.main' }} />
        <Typography variant="h5" component="h1" sx={{ fontWeight: 'bold', color: '#1E293B' }}>
          كل المعاملات
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
        <Grid container spacing={2}>
          {transactions.map((tx) => {
            const isIncoming = tx.transactionType === 'DEPOSIT'; 
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
                      boxShadow: '0 4px 12px rgba(0, 0, 0, 0.05)',
                      borderColor: '#CBD5E1'
                    },
                    overflow: 'visible'
                  }}
                >
                  <CardContent sx={{ p: { xs: 2, sm: 3 }, '&:last-child': { pb: { xs: 2, sm: 3 } } }}>
                    <Grid container alignItems="center" spacing={2}>
                      {/* Icon Column */}
                      <Grid item xs="auto">
                        <Avatar
                          sx={{
                            bgcolor: statusColor,
                            width: { xs: 40, sm: 56 },
                            height: { xs: 40, sm: 56 },
                            boxShadow: `0 4px 6px -1px ${statusColor}40`
                          }}
                        >
                          {getTransactionIcon(tx.transactionType, isIncoming)}
                        </Avatar>
                      </Grid>

                      {/* Details Column */}
                      <Grid item xs>
                        <Box sx={{ display: 'flex', flexWrap: 'wrap', alignItems: 'center', gap: 1, mb: 0.5 }}>
                          <Typography variant="subtitle1" sx={{ fontWeight: 'bold', color: '#1E293B' }}>
                            {tx.description || tx.transactionType}
                          </Typography>
                          <Chip 
                            label={tx.status} 
                            size="small" 
                            sx={{ 
                              bgcolor: tx.status === 'FAILED' ? '#FEF2F2' : '#F0FDF4',
                              color: tx.status === 'FAILED' ? '#DC2626' : '#166534',
                              fontWeight: 600,
                              fontSize: '0.7rem',
                              height: 20
                            }} 
                          />
                        </Box>
                        
                        <Grid container spacing={1} sx={{ mt: 0 }}>
                          <Grid item xs={12} md="auto">
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, color: 'text.secondary' }}>
                              <Typography variant="body2" sx={{ fontFamily: 'monospace', fontWeight: 500, fontSize: '0.8rem' }}>
                                من: {tx.fromAccount?.accountNumber || 'N/A'}
                              </Typography>
                            </Box>
                          </Grid>
                          <Grid item xs={12} md="auto">
                             <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, color: 'text.secondary' }}>
                              <Typography variant="body2" sx={{ fontFamily: 'monospace', fontWeight: 500, fontSize: '0.8rem' }}>
                                إلى: {tx.toAccount?.accountNumber || 'N/A'}
                              </Typography>
                            </Box>
                          </Grid>
                          <Grid item xs={12} md="auto">
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, color: 'text.secondary' }}>
                              <CalendarTodayIcon sx={{ fontSize: 14 }} />
                              <Typography variant="caption" sx={{ fontSize: '0.8rem' }}>
                                {formatDate(tx.createdAt)}
                              </Typography>
                            </Box>
                          </Grid>
                        </Grid>

                        {tx.failureReason && (
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mt: 1, color: '#EF4444', bgcolor: '#FEF2F2', p: 0.5, borderRadius: 1 }}>
                            <ErrorOutlineIcon fontSize="small" />
                            <Typography variant="caption" fontWeight="medium">
                              {tx.failureReason}
                            </Typography>
                          </Box>
                        )}
                      </Grid>

                      {/* Amount Column */}
                      <Grid item xs={12} sm="auto" sx={{ textAlign: { xs: 'left', sm: 'right' }, mt: { xs: 1, sm: 0 } }}>
                        <Typography 
                          variant="h6" 
                          sx={{ 
                            fontWeight: 'bold', 
                            color: '#1E293B',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: { xs: 'flex-start', sm: 'flex-end' },
                            gap: 0.5
                          }}
                        >
                          {tx.amount.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                        </Typography>
                        <Typography variant="caption" color="text.secondary" sx={{ display: 'block' }}>
                          #{tx.transactionId}
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
    </Box>
  );
};

export default AllTransactions;
