import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  Button,
  Grid,
  Card,
  CardContent,
  Alert,
  Snackbar,
  CircularProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  FormControl,
  InputLabel,
  Select,
  MenuItem
} from '@mui/material';
import interestService from '../services/interestService';
import api from '../services/api';

const InterestManagement = () => {
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
  const [strategyDialog, setStrategyDialog] = useState({ open: false, accountId: null, currentStrategy: '' });
  const [selectedStrategy, setSelectedStrategy] = useState('');

  // Get user from local storage
  const user = JSON.parse(localStorage.getItem('user'));
  // Fallback to userId if id is not present, or vice versa depending on backend response structure
  const userId = user?.userId || user?.id;

  const fetchAccounts = async () => {
    try {
      setLoading(true);
      if (userId) {
        // Assuming we have an endpoint to get accounts for the user, reusing logic from AccountsList
        const response = await api.get(`/accounts/user/${userId}`);
        setAccounts(response.data);
      } else {
          setError('User information not found. Please log in again.');
      }
    } catch (err) {
      console.error('Error fetching accounts:', err);
      setError('Failed to load accounts');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAccounts();
  }, []);

  const handleApplyInterest = async (accountId) => {
    try {
      await interestService.applyInterest(accountId);
      setSnackbar({
        open: true,
        message: 'Interest applied successfully to account ' + accountId,
        severity: 'success'
      });
      // Refresh accounts to show updated balance if necessary
      fetchAccounts();
    } catch (err) {
      console.error('Error applying interest:', err);
      setSnackbar({
        open: true,
        message: 'Failed to apply interest',
        severity: 'error'
      });
    }
  };

  const handleApplyAllInterest = async () => {
    try {
      await interestService.applyAllInterest();
      setSnackbar({
        open: true,
        message: 'Interest applied to all accounts successfully',
        severity: 'success'
      });
      fetchAccounts();
    } catch (err) {
      console.error('Error applying all interest:', err);
      setSnackbar({
        open: true,
        message: 'Failed to apply interest to all accounts',
        severity: 'error'
      });
    }
  };

  const handleOpenStrategyDialog = (accountId) => {
    setStrategyDialog({ open: true, accountId, currentStrategy: '' });
    setSelectedStrategy('SIMPLE'); // Default or fetch current if available
  };

  const handleCloseStrategyDialog = () => {
    setStrategyDialog({ open: false, accountId: null, currentStrategy: '' });
  };

  const handleChangeStrategy = async () => {
    if (!strategyDialog.accountId) return;

    try {
      await interestService.changeStrategy(strategyDialog.accountId, selectedStrategy);
      setSnackbar({
        open: true,
        message: `Strategy changed to ${selectedStrategy} for account ${strategyDialog.accountId}`,
        severity: 'success'
      });
      handleCloseStrategyDialog();
    } catch (err) {
      console.error('Error changing strategy:', err);
      setSnackbar({
        open: true,
        message: 'Failed to change strategy',
        severity: 'error'
      });
    }
  };

  const handleCloseSnackbar = () => {
    setSnackbar({ ...snackbar, open: false });
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="50vh">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom component="h2" sx={{ fontWeight: 'bold', color: 'primary.main' }}>
        Interest Management
      </Typography>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Box sx={{ mb: 3 }}>
        <Button 
          variant="contained" 
          color="secondary" 
          onClick={handleApplyAllInterest}
          size="large"
        >
          Apply Interest to All Accounts
        </Button>
      </Box>

      <Grid container spacing={3}>
        {accounts.map((account) => (
          <Grid item xs={12} md={6} lg={4} key={account.id}>
            <Card elevation={3}>
              <CardContent>
                <Typography variant="h6" component="div" gutterBottom>
                  Account: {account.accountNumber}
                </Typography>
                <Typography color="text.secondary" gutterBottom>
                  Balance: {account.balance}
                </Typography>
                <Typography color="text.secondary" gutterBottom>
                  Type: {account.accountType}
                </Typography>
                
                <Box sx={{ mt: 2, display: 'flex', flexDirection: 'column', gap: 1 }}>
                  <Button 
                    variant="contained" 
                    size="small" 
                    onClick={() => handleApplyInterest(account.id)}
                  >
                    Apply Interest
                  </Button>
                  <Button 
                    variant="outlined" 
                    size="small" 
                    onClick={() => handleOpenStrategyDialog(account.id)}
                  >
                    Change Strategy
                  </Button>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}
        {accounts.length === 0 && (
          <Grid item xs={12}>
            <Alert severity="info">No accounts found.</Alert>
          </Grid>
        )}
      </Grid>

      {/* Change Strategy Dialog */}
      <Dialog open={strategyDialog.open} onClose={handleCloseStrategyDialog}>
        <DialogTitle>Change Interest Strategy</DialogTitle>
        <DialogContent sx={{ minWidth: 300, mt: 1 }}>
          <FormControl fullWidth margin="dense">
            <InputLabel id="strategy-select-label">Strategy</InputLabel>
            <Select
              labelId="strategy-select-label"
              value={selectedStrategy}
              label="Strategy"
              onChange={(e) => setSelectedStrategy(e.target.value)}
            >
              <MenuItem value="SIMPLE">Simple Interest</MenuItem>
              <MenuItem value="COMPOUND">Compound Interest</MenuItem>
              {/* Add other strategies if available */}
            </Select>
          </FormControl>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseStrategyDialog}>Cancel</Button>
          <Button onClick={handleChangeStrategy} variant="contained" color="primary">
            Save
          </Button>
        </DialogActions>
      </Dialog>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={handleCloseSnackbar}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert onClose={handleCloseSnackbar} severity={snackbar.severity} sx={{ width: '100%' }}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Container>
  );
};

export default InterestManagement;
