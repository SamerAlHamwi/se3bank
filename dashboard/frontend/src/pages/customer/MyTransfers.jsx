import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Typography, Paper, CircularProgress, Alert, List, ListItem, ListItemText, Divider } from '@mui/material';
import api from '../../services/api';

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
        // Using recent transactions endpoint for the user
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

  if (loading) {
    return <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}><CircularProgress /></Box>;
  }

  if (error) {
    return <Alert severity="error">{error}</Alert>;
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: '#1E293B', mb: 3 }}>
        سجل معاملاتي
      </Typography>

      <Paper elevation={2} sx={{ borderRadius: '12px', overflow: 'hidden' }}>
        <List disablePadding>
          {transactions.length > 0 ? (
            transactions.map((tx, index) => (
              <React.Fragment key={tx.transactionId}>
                <ListItem sx={{ py: 2 }}>
                  <ListItemText 
                    primary={<Typography sx={{ fontWeight: 'bold' }}>{tx.description || tx.transactionType}</Typography>}
                    secondary={`من حساب: ${tx.fromAccountNumber || 'N/A'} | إلى حساب: ${tx.toAccountNumber || 'N/A'} - ${new Date(tx.date).toLocaleString('ar')}`}
                  />
                  <Typography 
                    variant="h6" 
                    sx={{ fontWeight: 'bold', color: tx.amount > 0 && (tx.transactionType === 'DEPOSIT' || (tx.toAccountNumber && tx.fromAccountNumber !== tx.toAccountNumber)) ? '#10B981' : '#EF4444' }}
                  >
                    {tx.amount > 0 && (tx.transactionType === 'DEPOSIT' || (tx.toAccountNumber && tx.fromAccountNumber !== tx.toAccountNumber)) ? '+' : '-'}${tx.amount.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
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

export default MyTransfers;
