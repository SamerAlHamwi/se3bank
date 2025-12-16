import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Paper,
  Typography,
  Box,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Button,
  Chip,
  Alert,
  CircularProgress,
  IconButton,
  Tooltip,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import api, { authApi } from '../services/api';

const AccountsList = () => {
  const [accounts, setAccounts] = useState([]);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Get current user
        const userResponse = await authApi.get('/me');
        setUser(userResponse.data);

        // Get accounts based on role
        let accountsResponse;
        if (userResponse.data.roles.includes('ROLE_ADMIN') ||
            userResponse.data.roles.includes('ROLE_MANAGER') ||
            userResponse.data.roles.includes('ROLE_TELLER')) {
          accountsResponse = await api.get('/accounts');
        } else {
          accountsResponse = await api.get(`/accounts/user/${userResponse.data.userId}`);
        }
        setAccounts(accountsResponse.data);
      } catch (err) {
        setError('فشل في تحميل الحسابات');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const handleCreateAccount = () => {
    navigate('create');
  };

  const handleViewAccount = (accountId) => {
    navigate(`${accountId}`);
  };

  const handleEditAccount = (accountId) => {
    navigate(`${accountId}`);
  };

  const handleDeleteAccount = async (accountId) => {
    if (window.confirm('هل أنت متأكد من إغلاق هذا الحساب؟')) {
      try {
        await api.delete(`/accounts/${accountId}`);
        setAccounts(accounts.filter(account => account.id !== accountId));
      } catch (err) {
        setError('فشل في إغلاق الحساب');
      }
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

  const getStatusText = (status) => {
    switch (status) {
      case 'ACTIVE': return 'نشط';
      case 'FROZEN': return 'مجمد';
      case 'CLOSED': return 'مغلق';
      default: return status;
    }
  };

  const getAccountTypeText = (type) => {
    switch (type) {
      case 'SAVINGS': return 'توفير';
      case 'CHECKING': return 'جاري';
      case 'LOAN': return 'قرض';
      case 'INVESTMENT': return 'استثمار';
      case 'BUSINESS': return 'تجاري';
      default: return type;
    }
  };

  const canCreateAccount = user?.roles?.some(role =>
    ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_TELLER'].includes(role)
  );

  const canEditAccount = user?.roles?.some(role =>
    ['ROLE_ADMIN', 'ROLE_MANAGER'].includes(role)
  );

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress size={50} sx={{ color: '#2563EB' }} />
      </Box>
    );
  }

  return (
    <Container maxWidth="xl">
      <Box sx={{ mb: 4 }}>
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
          <Typography 
            variant="h4" 
            component="h1"
            sx={{
              fontWeight: 700,
              background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
              backgroundClip: 'text',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
            }}
          >
            قائمة الحسابات
          </Typography>
          {canCreateAccount && (
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={handleCreateAccount}
              sx={{
                py: 1.1,
                px: 3,
                borderRadius: 2,
                fontSize: '0.95rem',
                fontWeight: 700,
                background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
                boxShadow: '0 8px 24px rgba(37, 99, 235, 0.3)',
                textTransform: 'none',
                transition: 'all 0.3s ease',
                '&:hover': {
                  transform: 'translateY(-2px)',
                  boxShadow: '0 12px 32px rgba(37, 99, 235, 0.4)',
                },
              }}
            >
              إنشاء حساب جديد
            </Button>
          )}
        </Box>

        {error && (
          <Alert 
            severity="error" 
            sx={{ 
              mb: 3,
              borderRadius: 2,
              boxShadow: '0 4px 12px rgba(239, 68, 68, 0.2)',
            }}
          >
            {error}
          </Alert>
        )}

        <Paper
          sx={{
            borderRadius: 3,
            boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)',
            border: '1px solid rgba(0, 0, 0, 0.05)',
            overflow: 'hidden',
          }}
        >
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow
                  sx={{
                    background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
                    '& th': {
                      color: 'white',
                      fontWeight: 700,
                      fontSize: '0.95rem',
                      padding: '18px 16px',
                    },
                  }}
                >
                  <TableCell sx={{ color: 'white', fontWeight: 700 }}>رقم الحساب</TableCell>
                  <TableCell sx={{ color: 'white', fontWeight: 700 }}>نوع الحساب</TableCell>
                  <TableCell sx={{ color: 'white', fontWeight: 700 }}>الحالة</TableCell>
                  <TableCell sx={{ color: 'white', fontWeight: 700 }} align="right">الرصيد</TableCell>
                  <TableCell sx={{ color: 'white', fontWeight: 700 }} align="right">الرصيد المتاح</TableCell>
                  <TableCell sx={{ color: 'white', fontWeight: 700 }}>تاريخ الإنشاء</TableCell>
                  <TableCell sx={{ color: 'white', fontWeight: 700 }} align="center">الإجراءات</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {accounts.map((account, index) => (
                  <TableRow 
                    key={account.id} 
                    hover
                    sx={{
                      transition: 'all 0.3s ease',
                      '&:hover': {
                        backgroundColor: '#F8FAFC',
                        boxShadow: '0 4px 12px rgba(0, 0, 0, 0.05)',
                      },
                      '&:nth-of-type(even)': {
                        backgroundColor: '#FAFBFC',
                      },
                    }}
                  >
                    <TableCell sx={{ fontWeight: 600, color: '#2563EB' }}>
                      {account.accountNumber}
                    </TableCell>
                    <TableCell sx={{ fontWeight: 500, color: '#555' }}>
                      {getAccountTypeText(account.accountType)}
                    </TableCell>
                    <TableCell>
                      <Chip
                        label={getStatusText(account.status)}
                        color={getStatusColor(account.status)}
                        size="small"
                        sx={{
                          fontWeight: 700,
                          fontSize: '0.8rem',
                        }}
                      />
                    </TableCell>
                    <TableCell 
                      align="right" 
                      sx={{ fontWeight: 700, color: '#10B981', fontSize: '0.95rem' }}
                    >
                      ${account.balance?.toFixed(2)}
                    </TableCell>
                    <TableCell 
                      align="right" 
                      sx={{ fontWeight: 600, color: '#666', fontSize: '0.9rem' }}
                    >
                      ${account.availableBalance?.toFixed(2)}
                    </TableCell>
                    <TableCell sx={{ color: '#999', fontSize: '0.9rem' }}>
                      {new Date(account.createdAt).toLocaleDateString('ar')}
                    </TableCell>
                    <TableCell align="center">
                      <Tooltip title="عرض التفاصيل">
                        <IconButton 
                          onClick={() => handleViewAccount(account.id)}
                          size="small"
                          sx={{
                            color: '#2563EB',
                            transition: 'all 0.3s ease',
                            '&:hover': {
                              backgroundColor: '#EFF6FF',
                            },
                          }}
                        >
                          <AccountBalanceIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                      {canEditAccount && (
                        <Tooltip title="تعديل">
                          <IconButton 
                            onClick={() => handleEditAccount(account.id)}
                            size="small"
                            sx={{
                              color: '#7C3AED',
                              transition: 'all 0.3s ease',
                              '&:hover': {
                                backgroundColor: '#F3E8FF',
                              },
                            }}
                          >
                            <EditIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      )}
                      {canEditAccount && account.status !== 'CLOSED' && (
                        <Tooltip title="إغلاق">
                          <IconButton 
                            onClick={() => handleDeleteAccount(account.id)} 
                            size="small"
                            sx={{
                              color: '#EF4444',
                              transition: 'all 0.3s ease',
                              '&:hover': {
                                backgroundColor: '#FEE2E2',
                              },
                            }}
                          >
                            <DeleteIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      )}
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Paper>

        {accounts.length === 0 && !loading && (
          <Box 
            textAlign="center" 
            sx={{
              py: 6,
              backgroundColor: '#F8FAFC',
              borderRadius: 3,
              mt: 2,
              border: '1px dashed #E5E7EB',
            }}
          >
            <AccountBalanceIcon 
              sx={{ 
                fontSize: 60, 
                color: '#DDD', 
                mb: 2,
              }} 
            />
            <Typography 
              variant="h6" 
              sx={{ 
                color: '#999', 
                fontWeight: 500,
              }}
            >
              لا توجد حسابات حالياً
            </Typography>
          </Box>
        )}
      </Box>
    </Container>
  );
};

export default AccountsList;