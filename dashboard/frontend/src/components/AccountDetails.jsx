import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Container,
  Paper,
  Typography,
  Box,
  Button,
  Grid,
  Divider,
  Chip,
  Card,
  CardContent,
  CircularProgress,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  TextField,
  Snackbar,
  List,
  ListItem,
  ListItemText
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import CompareArrowsIcon from '@mui/icons-material/CompareArrows';
import TimelineIcon from '@mui/icons-material/Timeline';
import api from '../services/api';
import interestService from '../services/interestService';

const AccountDetails = () => {
  const { accountId } = useParams();
  const navigate = useNavigate();
  
  const [account, setAccount] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [transactions, setTransactions] = useState([]);
  
  // Interest related states
  const [interestDialogOpen, setInterestDialogOpen] = useState(false);
  const [selectedStrategy, setSelectedStrategy] = useState('');
  const [availableStrategies, setAvailableStrategies] = useState({});
  const [futureInterest, setFutureInterest] = useState(null);
  const [futureMonths, setFutureMonths] = useState(12);
  const [comparisonResult, setComparisonResult] = useState(null);
  const [compareStrategy1, setCompareStrategy1] = useState('');
  const [compareStrategy2, setCompareStrategy2] = useState('');
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
  const [interestReport, setInterestReport] = useState(null);
  const [effectiveRate, setEffectiveRate] = useState(null);

  const fetchAccountData = async () => {
    try {
      setLoading(true);
      const accResponse = await api.get(`/accounts/${accountId}`);
      setAccount(accResponse.data);
      
      const transResponse = await api.get(`/transactions/account/${accountId}`);
      setTransactions(transResponse.data);

      // Fetch supported strategies based on account type
      if (accResponse.data.accountType) {
        const stratResponse = await interestService.getSupportedStrategies(accResponse.data.accountType);
        setAvailableStrategies(stratResponse.data);
      }
      
      // Fetch Effective Rate
      try {
        const rateResponse = await interestService.getEffectiveInterestRate(accountId);
        setEffectiveRate(rateResponse.data);
      } catch (e) {
        console.log("Effective rate not available");
      }

    } catch (err) {
      console.error(err);
      setError('Failed to load account details');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (accountId) {
      fetchAccountData();
    }
  }, [accountId]);

  const handleApplyInterest = async () => {
    try {
      const response = await interestService.applyInterest(accountId);
      setSnackbar({
        open: true,
        message: `Interest applied: $${response.data}`,
        severity: 'success'
      });
      fetchAccountData();
    } catch (err) {
      setSnackbar({
        open: true,
        message: 'Failed to apply interest',
        severity: 'error'
      });
    }
  };

  const handleChangeStrategy = async () => {
    if (!selectedStrategy) return;
    try {
      await interestService.changeStrategy(accountId, selectedStrategy);
      setSnackbar({
        open: true,
        message: `Strategy changed to ${selectedStrategy}`,
        severity: 'success'
      });
      fetchAccountData(); // Refresh to update strategy if it's part of account details
    } catch (err) {
      setSnackbar({
        open: true,
        message: 'Failed to change strategy',
        severity: 'error'
      });
    }
  };

  const handleCalculateFuture = async () => {
    try {
      const response = await interestService.calculateFutureInterest(accountId, futureMonths);
      setFutureInterest(response.data);
    } catch (err) {
      console.error(err);
      setSnackbar({ open: true, message: 'Failed to calculate future interest', severity: 'error' });
    }
  };

  const handleCompareStrategies = async () => {
    if (!compareStrategy1 || !compareStrategy2) return;
    try {
      const response = await interestService.compareStrategies(accountId, compareStrategy1, compareStrategy2);
      setComparisonResult(response.data);
    } catch (err) {
      console.error(err);
      setSnackbar({ open: true, message: 'Failed to compare strategies', severity: 'error' });
    }
  };
  
  const handleGetReport = async () => {
     try {
       const response = await interestService.getInterestReport(accountId);
       setInterestReport(response.data);
     } catch (err) {
       console.error(err);
       setSnackbar({ open: true, message: 'Failed to get interest report', severity: 'error' });
     }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'ACTIVE': return 'success';
      case 'FROZEN': return 'warning';
      case 'CLOSED': return 'error';
      default: return 'default';
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="50vh">
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4 }}>
        <Alert severity="error">{error}</Alert>
        <Button startIcon={<ArrowBackIcon />} onClick={() => navigate(-1)} sx={{ mt: 2 }}>
          Back
        </Button>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mb: 4 }}>
      <Button 
        startIcon={<ArrowBackIcon />} 
        onClick={() => navigate('/dashboard/accounts')} 
        sx={{ mb: 3 }}
      >
        Back to Accounts
      </Button>

      <Grid container spacing={3}>
        {/* Main Account Info */}
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 3, borderRadius: 2 }}>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
              <Typography variant="h5" fontWeight="bold" color="primary">
                Account Details
              </Typography>
              <Chip 
                label={account.status} 
                color={getStatusColor(account.status)} 
                sx={{ fontWeight: 'bold' }}
              />
            </Box>
            <Divider sx={{ mb: 2 }} />
            
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <Typography color="text.secondary">Account Number</Typography>
                <Typography variant="h6">{account.accountNumber}</Typography>
              </Grid>
              <Grid item xs={12} sm={6}>
                <Typography color="text.secondary">Account Type</Typography>
                <Typography variant="h6">{account.accountType}</Typography>
              </Grid>
              <Grid item xs={12} sm={6}>
                <Typography color="text.secondary">Current Balance</Typography>
                <Typography variant="h4" color="success.main" fontWeight="bold">
                  ${account.balance?.toFixed(2)}
                </Typography>
              </Grid>
              <Grid item xs={12} sm={6}>
                <Typography color="text.secondary">Available Balance</Typography>
                <Typography variant="h6">
                  ${account.availableBalance?.toFixed(2)}
                </Typography>
              </Grid>
              {effectiveRate !== null && (
                 <Grid item xs={12} sm={6}>
                  <Typography color="text.secondary">Effective Interest Rate</Typography>
                  <Typography variant="h6" color="secondary.main">
                    {effectiveRate}%
                  </Typography>
                </Grid>
              )}
            </Grid>
          </Paper>

          {/* Transactions History */}
          <Paper sx={{ p: 3, mt: 3, borderRadius: 2 }}>
            <Typography variant="h6" gutterBottom fontWeight="bold">
              Recent Transactions
            </Typography>
            <Divider sx={{ mb: 2 }} />
            {transactions.length > 0 ? (
               <List>
                 {transactions.slice(0, 5).map((t, i) => (
                   <React.Fragment key={t.id}>
                     {i > 0 && <Divider component="li" />}
                     <ListItem>
                       <ListItemText 
                         primary={t.type} 
                         secondary={new Date(t.timestamp).toLocaleDateString()}
                       />
                       <Typography 
                          fontWeight="bold" 
                          color={t.type === 'DEPOSIT' || (t.type === 'TRANSFER' && t.toAccountId === account.id) ? 'success.main' : 'error.main'}
                       >
                         {t.amount > 0 ? '+' : ''}{t.amount}
                       </Typography>
                     </ListItem>
                   </React.Fragment>
                 ))}
               </List>
            ) : (
              <Typography color="text.secondary">No transactions found.</Typography>
            )}
            <Box mt={2} textAlign="right">
               <Button onClick={() => navigate('/dashboard/transactions')}>View All</Button>
            </Box>
          </Paper>
        </Grid>

        {/* Interest Management Sidebar */}
        <Grid item xs={12} md={4}>
          <Card elevation={3} sx={{ borderRadius: 2 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom display="flex" alignItems="center" gap={1}>
                <TrendingUpIcon color="secondary" /> Interest Management
              </Typography>
              <Divider sx={{ mb: 2 }} />
              
              <Button 
                variant="contained" 
                color="secondary" 
                fullWidth 
                sx={{ mb: 2 }}
                onClick={() => setInterestDialogOpen(true)}
              >
                Manage Interest Options
              </Button>

              <Button 
                variant="outlined" 
                fullWidth 
                sx={{ mb: 2 }}
                onClick={handleApplyInterest}
              >
                Apply Current Interest Now
              </Button>
              
               <Button 
                variant="outlined" 
                color="info"
                fullWidth 
                onClick={handleGetReport}
              >
                Get Detailed Interest Report
              </Button>
              
              {interestReport && (
                <Box mt={2} p={2} bgcolor="background.default" borderRadius={1}>
                    <Typography variant="subtitle2" gutterBottom>Interest Report:</Typography>
                    <Typography variant="body2">Accrued Interest: ${interestReport.accruedInterest}</Typography>
                    <Typography variant="body2">Last Applied: {interestReport.lastAppliedDate || 'Never'}</Typography>
                </Box>
              )}
            </CardContent>
          </Card>

          {/* Future Calculator */}
          <Card elevation={3} sx={{ mt: 3, borderRadius: 2 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom display="flex" alignItems="center" gap={1}>
                <TimelineIcon color="primary" /> Future Calculator
              </Typography>
              <Box display="flex" gap={1} mb={2}>
                 <TextField 
                    label="Months" 
                    type="number" 
                    size="small" 
                    value={futureMonths} 
                    onChange={(e) => setFutureMonths(e.target.value)}
                    fullWidth
                 />
                 <Button variant="contained" onClick={handleCalculateFuture}>Calc</Button>
              </Box>
              {futureInterest !== null && (
                 <Alert severity="info">
                    Estimated Interest in {futureMonths} months: <strong>${futureInterest.toFixed(2)}</strong>
                 </Alert>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Interest Management Dialog */}
      <Dialog 
        open={interestDialogOpen} 
        onClose={() => setInterestDialogOpen(false)}
        fullWidth
        maxWidth="sm"
      >
        <DialogTitle>Interest Configuration</DialogTitle>
        <DialogContent>
          <Box sx={{ mt: 2 }}>
             <Typography variant="subtitle1" gutterBottom fontWeight="bold">Change Strategy</Typography>
             <Box display="flex" gap={2} mb={3}>
                <FormControl fullWidth size="small">
                  <InputLabel>New Strategy</InputLabel>
                  <Select
                    value={selectedStrategy}
                    label="New Strategy"
                    onChange={(e) => setSelectedStrategy(e.target.value)}
                  >
                    {Object.keys(availableStrategies).map(key => (
                      <MenuItem key={key} value={key}>{key}</MenuItem>
                    ))}
                  </Select>
                </FormControl>
                <Button variant="contained" onClick={handleChangeStrategy} disabled={!selectedStrategy}>Save</Button>
             </Box>
             
             <Divider sx={{ my: 3 }} />
             
             <Typography variant="subtitle1" gutterBottom fontWeight="bold" display="flex" alignItems="center" gap={1}>
                <CompareArrowsIcon fontSize="small" /> Compare Strategies
             </Typography>
             <Grid container spacing={2} alignItems="center">
                <Grid item xs={5}>
                   <FormControl fullWidth size="small">
                      <InputLabel>Strategy 1</InputLabel>
                      <Select
                        value={compareStrategy1}
                        label="Strategy 1"
                        onChange={(e) => setCompareStrategy1(e.target.value)}
                      >
                         {Object.keys(availableStrategies).map(key => (
                          <MenuItem key={key} value={key}>{key}</MenuItem>
                        ))}
                      </Select>
                   </FormControl>
                </Grid>
                <Grid item xs={5}>
                   <FormControl fullWidth size="small">
                      <InputLabel>Strategy 2</InputLabel>
                      <Select
                        value={compareStrategy2}
                        label="Strategy 2"
                        onChange={(e) => setCompareStrategy2(e.target.value)}
                      >
                        {Object.keys(availableStrategies).map(key => (
                          <MenuItem key={key} value={key}>{key}</MenuItem>
                        ))}
                      </Select>
                   </FormControl>
                </Grid>
                <Grid item xs={2}>
                   <Button variant="outlined" onClick={handleCompareStrategies} fullWidth>Go</Button>
                </Grid>
             </Grid>
             
             {comparisonResult && (
                <Box mt={2} p={2} bgcolor="#f5f5f5" borderRadius={2}>
                   <Typography variant="subtitle2" gutterBottom>Comparison Result:</Typography>
                   <Grid container spacing={1}>
                      <Grid item xs={6}>
                         <Typography variant="caption" display="block" color="text.secondary">
                           {comparisonResult.strategy1Name}
                         </Typography>
                         <Typography variant="body1" fontWeight="bold">
                           ${comparisonResult.strategy1Value?.toFixed(2)}
                         </Typography>
                      </Grid>
                      <Grid item xs={6}>
                         <Typography variant="caption" display="block" color="text.secondary">
                           {comparisonResult.strategy2Name}
                         </Typography>
                         <Typography variant="body1" fontWeight="bold">
                           ${comparisonResult.strategy2Value?.toFixed(2)}
                         </Typography>
                      </Grid>
                      <Grid item xs={12}>
                         <Typography variant="body2" color="primary" sx={{ mt: 1 }}>
                            Difference: ${comparisonResult.difference?.toFixed(2)}
                         </Typography>
                      </Grid>
                   </Grid>
                </Box>
             )}
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setInterestDialogOpen(false)}>Close</Button>
        </DialogActions>
      </Dialog>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert onClose={() => setSnackbar({ ...snackbar, open: false })} severity={snackbar.severity} sx={{ width: '100%' }}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Container>
  );
};

export default AccountDetails;
