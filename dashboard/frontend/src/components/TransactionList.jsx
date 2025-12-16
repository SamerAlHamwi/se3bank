import React, { useState, useEffect } from 'react';
import {
  Container,
  Paper,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  Box,
  CircularProgress,
  Alert,
  Tabs,
  Tab,
  TextField,
  InputAdornment,
  IconButton,
  Button,
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import RefreshIcon from '@mui/icons-material/Refresh';
import VisibilityIcon from '@mui/icons-material/Visibility';
import { useNavigate } from 'react-router-dom';
import api, { authApi } from '../services/api';

const TransactionList = () => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [currentTab, setCurrentTab] = useState(0);
  const [user, setUser] = useState(null);
  const [searchAccountId, setSearchAccountId] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    fetchUserData();
  }, []);

  useEffect(() => {
    if (user) {
      fetchTransactions();
    }
  }, [user, currentTab]);

  const fetchUserData = async () => {
    try {
      const response = await authApi.get('/me');
      setUser(response.data);
    } catch (err) {
      setError('فشل في تحميل بيانات المستخدم');
    }
  };

  const fetchTransactions = async (accountId = null) => {
    setLoading(true);
    setError('');
    try {
      let response;
      if (accountId) {
        response = await api.get(`/transactions/account/${accountId}`);
      } else if (currentTab === 0) {
        // "المعاملات" - Default to user's recent transactions since global list endpoint is restricted
        response = await api.get(`/transactions/user/${user.userId}/recent`);
      } else if (currentTab === 1) {
        // "بانتظار الموافقة" - Use the correct endpoint for pending approval transactions
        // The user pointed out the previous logic might be wrong.
        // The prompt says: "Transactions List Page: ... GET /api/transactions/pending-approval"
        // Let's make sure we use exactly that.
        response = await api.get('/transactions/pending-approval');
      }
      setTransactions(response.data);
    } catch (err) {
      console.error(err);
      if (err.response && err.response.status === 405) {
          setError('عذراً، طريقة الطلب غير مسموح بها (405). يرجى التحقق من الخادم.');
      } else if (err.response && err.response.status === 403) {
          setError('عذراً، ليس لديك صلاحية لعرض هذه البيانات.');
      } else {
          setError('فشل في تحميل المعاملات.');
      }
      setTransactions([]);
    } finally {
      setLoading(false);
    }
  };

  const handleChangeTab = (event, newValue) => {
    setCurrentTab(newValue);
    setSearchAccountId('');
  };

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchAccountId) {
      fetchTransactions(searchAccountId);
    }
  };

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
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h1" sx={{ fontWeight: 700 }}>
          سجل المعاملات
        </Typography>
        <Button 
          startIcon={<RefreshIcon />} 
          onClick={() => fetchTransactions(searchAccountId || null)}
          variant="outlined"
        >
          تحديث
        </Button>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Paper sx={{ width: '100%', mb: 3, p: 2, borderRadius: 2 }}>
        <Box display="flex" flexDirection={{ xs: 'column', md: 'row' }} justifyContent="space-between" alignItems="center" gap={2}>
          <Tabs 
            value={currentTab} 
            onChange={handleChangeTab} 
            indicatorColor="primary" 
            textColor="primary"
            sx={{ flexGrow: 1 }}
          >
            <Tab label="المعاملات" />
            <Tab label="بانتظار الموافقة" />
          </Tabs>

          <Box component="form" onSubmit={handleSearch} sx={{ display: 'flex', gap: 1 }}>
            <TextField
              size="small"
              placeholder="بحث برقم الحساب (ID)"
              value={searchAccountId}
              onChange={(e) => setSearchAccountId(e.target.value)}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon color="action" />
                  </InputAdornment>
                ),
              }}
              sx={{ width: 250 }}
            />
            <Button type="submit" variant="contained" disabled={!searchAccountId}>
              بحث
            </Button>
          </Box>
        </Box>
        {currentTab === 0 && !searchAccountId && (
            <Typography variant="caption" sx={{ display: 'block', mt: 1, color: 'text.secondary' }}>
                * يتم عرض أحدث المعاملات الخاصة بك افتراضياً. للبحث عن معاملات حساب معين، أدخل رقم الحساب.
            </Typography>
        )}
      </Paper>

      {loading ? (
        <Box display="flex" justifyContent="center" p={5}>
          <CircularProgress />
        </Box>
      ) : (
        <TableContainer component={Paper} sx={{ borderRadius: 2, boxShadow: '0 4px 20px rgba(0,0,0,0.05)' }}>
          <Table>
            <TableHead sx={{ backgroundColor: '#F8FAFC' }}>
              <TableRow>
                <TableCell align="center" sx={{ fontWeight: 'bold' }}>#</TableCell>
                <TableCell align="left" sx={{ fontWeight: 'bold' }}>من</TableCell>
                <TableCell align="left" sx={{ fontWeight: 'bold' }}>إلى</TableCell>
                <TableCell align="center" sx={{ fontWeight: 'bold' }}>المبلغ</TableCell>
                <TableCell align="center" sx={{ fontWeight: 'bold' }}>التاريخ</TableCell>
                <TableCell align="center" sx={{ fontWeight: 'bold' }}>الحالة</TableCell>
                <TableCell align="center" sx={{ fontWeight: 'bold' }}>النوع</TableCell>
                <TableCell align="center" sx={{ fontWeight: 'bold' }}>إجراءات</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {transactions.length > 0 ? (
                transactions.map((transaction) => (
                  <TableRow key={transaction.id} hover sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                    <TableCell align="center">{transaction.id}</TableCell>
                    <TableCell align="left">{transaction.fromAccount}</TableCell>
                    <TableCell align="left">{transaction.toAccount}</TableCell>
                    <TableCell align="center" sx={{ color: '#2563EB', fontWeight: 700 }}>
                      ${transaction.amount?.toFixed(2)}
                    </TableCell>
                    <TableCell align="center">{new Date(transaction.transactionDate).toLocaleDateString()}</TableCell>
                    <TableCell align="center">
                      <Chip 
                        label={transaction.status} 
                        color={getStatusColor(transaction.status)} 
                        size="small" 
                        variant="outlined"
                        sx={{ fontWeight: 600 }}
                      />
                    </TableCell>
                    <TableCell align="center">{transaction.type}</TableCell>
                    <TableCell align="center">
                      <IconButton 
                        color="primary" 
                        onClick={() => navigate(`/dashboard/transactions/${transaction.id}`)}
                        size="small"
                      >
                        <VisibilityIcon />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={8} align="center" sx={{ py: 6 }}>
                    <Typography color="textSecondary" variant="h6">
                      لا توجد معاملات لعرضها
                    </Typography>
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </Container>
  );
};

export default TransactionList;
