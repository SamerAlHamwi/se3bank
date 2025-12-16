import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Box, Typography, Paper, CircularProgress, Alert, Grid, List, ListItem, ListItemText, Divider, Button } from '@mui/material';
import { ArrowBack } from '@mui/icons-material';
import api from '../../services/api';

const AccountDetails = () => {
  const { accountId } = useParams();
  const [account, setAccount] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchDetails = async () => {
      setLoading(true);
      try {
        const accountResponse = await api.get(`/api/accounts/${accountId}`);
        setAccount(accountResponse.data);

        const transactionsResponse = await api.get(`/api/transactions/account/${accountId}`);
        setTransactions(transactionsResponse.data);

        setError('');
      } catch (err) {
        setError(err.response?.data?.message || 'حدث خطأ أثناء جلب تفاصيل الحساب');
      } finally {
        setLoading(false);
      }
    };

    fetchDetails();
  }, [accountId]);

  if (loading) {
    return <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}><CircularProgress /></Box>;
  }

  if (error) {
    return <Alert severity="error">{error}</Alert>;
  }

  if (!account) {
    return <Alert severity="warning">لم يتم العثور على الحساب.</Alert>;
  }

  return (
    <Box>
      <Button component={Link} to="/customer/dashboard" startIcon={<ArrowBack />} sx={{ mb: 3 }}>
        العودة إلى الحسابات
      </Button>

      {/* Account Header */}
      <Paper elevation={3} sx={{ p: 4, mb: 4, borderRadius: '16px', background: '#fff' }}>
        <Grid container spacing={3} alignItems="center">
          <Grid item xs={12} md={6}>
            <Typography variant="h6" sx={{ color: '#64748B' }}>{account.accountNumber}</Typography>
            <Typography variant="h4" sx={{ fontWeight: 'bold', color: '#1E293B' }}>{account.accountType} Account</Typography>
          </Grid>
          <Grid item xs={12} md={6} sx={{ textAlign: { md: 'right' } }}>
            <Typography variant="h6" sx={{ color: '#64748B' }}>الرصيد الحالي</Typography>
            <Typography variant="h3" sx={{ fontWeight: 'bold', color: '#10B981' }}>
              ${account.balance.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
            </Typography>
          </Grid>
        </Grid>
      </Paper>

      {/* Transactions List */}
      <Typography variant="h5" gutterBottom sx={{ fontWeight: 'bold', color: '#334155', mb: 2 }}>
        سجل المعاملات
      </Typography>
      <Paper elevation={2} sx={{ borderRadius: '12px', overflow: 'hidden' }}>
        <List disablePadding>
          {transactions.length > 0 ? (
            transactions.map((tx, index) => (
              <React.Fragment key={tx.transactionId}>
                <ListItem sx={{ py: 2 }}>
                  <ListItemText 
                    primary={<Typography sx={{ fontWeight: 'bold' }}>{tx.description || tx.transactionType}</Typography>}
                    secondary={`#${tx.transactionId} - ${new Date(tx.date).toLocaleString('ar')}`}
                  />
                  <Typography 
                    variant="h6" 
                    sx={{ fontWeight: 'bold', color: tx.transactionType === 'DEPOSIT' ? '#10B981' : '#EF4444' }}
                  >
                    {tx.transactionType === 'DEPOSIT' ? '+' : '-'}${tx.amount.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                  </Typography>
                </ListItem>
                {index < transactions.length - 1 && <Divider component="li" />}
              </React.Fragment>
            ))
          ) : (
            <ListItem sx={{ py: 3 }}>
              <ListItemText primary="لا يوجد معاملات لعرضها." />
            </ListItem>
          )}
        </List>
      </Paper>
    </Box>
  );
};

export default AccountDetails;
