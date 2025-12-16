import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Container,
  Paper,
  Typography,
  Box,
  Grid,
  Card,
  CardContent,
  Button,
  Chip,
  Alert,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  TextField,
  IconButton,
  Tooltip,
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import api, { authApi } from '../services/api';

const AccountDetails = () => {
  const { accountId } = useParams();
  const navigate = useNavigate();
  const [account, setAccount] = useState(null);
  const [balance, setBalance] = useState(null);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [editForm, setEditForm] = useState({
    balance: 0,
    interestRate: 0,
    overdraftLimit: 0,
    minimumBalance: 0,
  });

  useEffect(() => {
    fetchAccountDetails();
    fetchCurrentUser();
  }, [accountId]);

  const fetchAccountDetails = async () => {
    try {
      const [accountResponse, balanceResponse] = await Promise.all([
        api.get(`/accounts/${accountId}`),
        api.get(`/accounts/${accountId}/balance`)
      ]);
      setAccount(accountResponse.data);
      setBalance(balanceResponse.data);
      setEditForm({
        balance: accountResponse.data.balance || 0,
        interestRate: accountResponse.data.interestRate || 0,
        overdraftLimit: accountResponse.data.overdraftLimit || 0,
        minimumBalance: accountResponse.data.minimumBalance || 0,
      });
    } catch (err) {
      setError('فشل في تحميل تفاصيل الحساب');
    } finally {
      setLoading(false);
    }
  };

  const fetchCurrentUser = async () => {
    try {
      const response = await authApi.get('/me');
      setUser(response.data);
    } catch (err) {
      setError('فشل في تحميل بيانات المستخدم');
    }
  };

  const handleEdit = () => {
    setEditDialogOpen(true);
  };

  const handleEditSubmit = async () => {
    try {
      await api.put(`/accounts/${accountId}`, editForm);
      setEditDialogOpen(false);
      fetchAccountDetails();
    } catch (err) {
      setError('فشل في تحديث الحساب');
    }
  };

  const handleDelete = async () => {
    if (window.confirm('هل أنت متأكد من إغلاق هذا الحساب؟')) {
      try {
        await api.delete(`/accounts/${accountId}`);
        navigate('..');
      } catch (err) {
        setError('فشل في إغلاق الحساب');
      }
    }
  };

  const handleEditFormChange = (e) => {
    const { name, value } = e.target;
    setEditForm(prev => ({
      ...prev,
      [name]: parseFloat(value) || 0
    }));
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

  const canEdit = user?.roles?.some(role =>
    ['ROLE_ADMIN', 'ROLE_MANAGER'].includes(role)
  );

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress size={50} sx={{ color: '#2563EB' }} />
      </Box>
    );
  }

  if (!account) {
    return (
      <Container maxWidth="md">
        <Alert 
          severity="error"
          sx={{
            borderRadius: 2,
            boxShadow: '0 4px 12px rgba(239, 68, 68, 0.2)',
            mt: 4,
          }}
        >
          الحساب غير موجود
        </Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="md">
      <Box sx={{ mb: 4 }}>
        <Box display="flex" alignItems="center" mb={3}>
          <IconButton 
            onClick={() => navigate('..')} 
            sx={{ 
              mr: 2,
              transition: 'all 0.3s ease',
              '&:hover': {
                backgroundColor: '#EFF6FF',
                color: '#2563EB',
              },
            }}
          >
            <ArrowBackIcon />
          </IconButton>
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
            تفاصيل الحساب
          </Typography>
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

        <Grid container spacing={3}>
          <Grid item xs={12} md={8}>
            <Card
              sx={{
                borderRadius: 3,
                boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)',
                border: '1px solid rgba(0, 0, 0, 0.05)',
              }}
            >
              <CardContent sx={{ p: 3 }}>
                <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                  <Typography 
                    variant="h6" 
                    component="h2"
                    sx={{ fontWeight: 700, color: '#333' }}
                  >
                    معلومات الحساب
                  </Typography>
                  <Box>
                    {canEdit && account.status !== 'CLOSED' && (
                      <>
                        <Tooltip title="تعديل">
                          <IconButton 
                            onClick={handleEdit} 
                            sx={{
                              color: '#7C3AED',
                              transition: 'all 0.3s ease',
                              '&:hover': {
                                backgroundColor: '#F3E8FF',
                              },
                            }}
                          >
                            <EditIcon />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="إغلاق">
                          <IconButton 
                            onClick={handleDelete} 
                            sx={{
                              color: '#EF4444',
                              transition: 'all 0.3s ease',
                              '&:hover': {
                                backgroundColor: '#FEE2E2',
                              },
                            }}
                          >
                            <DeleteIcon />
                          </IconButton>
                        </Tooltip>
                      </>
                    )}
                  </Box>
                </Box>

                <Grid container spacing={2.5}>
                  <Grid item xs={12} sm={6}>
                    <Box
                      sx={{
                        p: 2,
                        backgroundColor: '#F8FAFC',
                        borderRadius: 2,
                        borderLeft: '3px solid #2563EB',
                      }}
                    >
                      <Typography variant="caption" sx={{ color: '#999', fontWeight: 600, fontSize: '0.85rem' }}>
                        رقم الحساب
                      </Typography>
                      <Typography variant="body1" sx={{ fontWeight: 700, color: '#2563EB', mt: 0.5 }}>
                        {account.accountNumber}
                      </Typography>
                    </Box>
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <Box
                      sx={{
                        p: 2,
                        backgroundColor: '#F8FAFC',
                        borderRadius: 2,
                        borderLeft: '3px solid #7C3AED',
                      }}
                    >
                      <Typography variant="caption" sx={{ color: '#999', fontWeight: 600, fontSize: '0.85rem' }}>
                        نوع الحساب
                      </Typography>
                      <Typography variant="body1" sx={{ fontWeight: 600, color: '#555', mt: 0.5 }}>
                        {getAccountTypeText(account.accountType)}
                      </Typography>
                    </Box>
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <Box
                      sx={{
                        p: 2,
                        backgroundColor: '#F8FAFC',
                        borderRadius: 2,
                        borderLeft: '3px solid #10B981',
                      }}
                    >
                      <Typography variant="caption" sx={{ color: '#999', fontWeight: 600, fontSize: '0.85rem' }}>
                        الحالة
                      </Typography>
                      <Box sx={{ mt: 0.5 }}>
                        <Chip
                          label={getStatusText(account.status)}
                          color={getStatusColor(account.status)}
                          size="small"
                          sx={{ fontWeight: 700, fontSize: '0.8rem' }}
                        />
                      </Box>
                    </Box>
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <Box
                      sx={{
                        p: 2,
                        backgroundColor: '#F8FAFC',
                        borderRadius: 2,
                        borderLeft: '3px solid #F59E0B',
                      }}
                    >
                      <Typography variant="caption" sx={{ color: '#999', fontWeight: 600, fontSize: '0.85rem' }}>
                        تاريخ الإنشاء
                      </Typography>
                      <Typography variant="body1" sx={{ fontWeight: 600, color: '#555', mt: 0.5 }}>
                        {new Date(account.createdAt).toLocaleDateString('ar')}
                      </Typography>
                    </Box>
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <Box
                      sx={{
                        p: 2,
                        backgroundColor: '#F8FAFC',
                        borderRadius: 2,
                        borderLeft: '3px solid #06B6D4',
                      }}
                    >
                      <Typography variant="caption" sx={{ color: '#999', fontWeight: 600, fontSize: '0.85rem' }}>
                        معدل الفائدة
                      </Typography>
                      <Typography variant="body1" sx={{ fontWeight: 700, color: '#06B6D4', mt: 0.5 }}>
                        {account.interestRate}%
                      </Typography>
                    </Box>
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <Box
                      sx={{
                        p: 2,
                        backgroundColor: '#F8FAFC',
                        borderRadius: 2,
                        borderLeft: '3px solid #EC4899',
                      }}
                    >
                      <Typography variant="caption" sx={{ color: '#999', fontWeight: 600, fontSize: '0.85rem' }}>
                        حد السحب على المكشوف
                      </Typography>
                      <Typography variant="body1" sx={{ fontWeight: 600, color: '#555', mt: 0.5 }}>
                        ${account.overdraftLimit?.toFixed(2)}
                      </Typography>
                    </Box>
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <Box
                      sx={{
                        p: 2,
                        backgroundColor: '#F8FAFC',
                        borderRadius: 2,
                        borderLeft: '3px solid #8B5CF6',
                      }}
                    >
                      <Typography variant="caption" sx={{ color: '#999', fontWeight: 600, fontSize: '0.85rem' }}>
                        الحد الأدنى للرصيد
                      </Typography>
                      <Typography variant="body1" sx={{ fontWeight: 600, color: '#555', mt: 0.5 }}>
                        ${account.minimumBalance?.toFixed(2)}
                      </Typography>
                    </Box>
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} md={4}>
            <Card
              sx={{
                borderRadius: 3,
                background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
                color: 'white',
                boxShadow: '0 8px 24px rgba(37, 99, 235, 0.3)',
                border: 'none',
              }}
            >
              <CardContent sx={{ textAlign: 'center' }}>
                <AccountBalanceIcon sx={{ fontSize: 50, mb: 2, opacity: 0.3 }} />
                <Typography 
                  variant="body2" 
                  sx={{ 
                    fontWeight: 500, 
                    opacity: 0.9,
                    mb: 1,
                  }}
                >
                  الرصيد الحالي
                </Typography>
                {balance && (
                  <>
                    <Typography 
                      variant="h3" 
                      sx={{ 
                        fontWeight: 700, 
                        mb: 2,
                        fontSize: '2.5rem',
                      }}
                    >
                      ${balance.balance?.toFixed(2)}
                    </Typography>
                    <Box sx={{ 
                      p: 1.5, 
                      backgroundColor: 'rgba(255, 255, 255, 0.1)',
                      borderRadius: 1.5,
                      mb: 1.5,
                    }}>
                      <Typography variant="body2" sx={{ fontWeight: 600 }}>
                        الرصيد المتاح: ${balance.availableBalance?.toFixed(2)}
                      </Typography>
                    </Box>
                    <Typography variant="caption" sx={{ opacity: 0.8 }}>
                      العملة: {balance.currency}
                    </Typography>
                  </>
                )}
              </CardContent>
            </Card>
          </Grid>
        </Grid>

        {/* Edit Dialog */}
        <Dialog 
          open={editDialogOpen} 
          onClose={() => setEditDialogOpen(false)}
          PaperProps={{
            sx: {
              borderRadius: 3,
              boxShadow: '0 20px 60px rgba(0, 0, 0, 0.3)',
            },
          }}
        >
          <DialogTitle sx={{ fontWeight: 700, color: '#333', pb: 1 }}>
            تعديل الحساب
          </DialogTitle>
          <DialogContent sx={{ pt: 2 }}>
            <DialogContentText sx={{ color: '#555', fontWeight: 500, mb: 2 }}>
              قم بتعديل بيانات الحساب حسب الحاجة.
            </DialogContentText>
            <TextField
              autoFocus
              margin="normal"
              name="balance"
              label="الرصيد"
              type="number"
              fullWidth
              value={editForm.balance}
              onChange={handleEditFormChange}
              inputProps={{ step: 0.01 }}
              variant="outlined"
              sx={{
                '& .MuiOutlinedInput-root': {
                  borderRadius: 2,
                  backgroundColor: '#F8FAFC',
                  '&:hover fieldset': {
                    borderColor: '#2563EB',
                  },
                  '&.Mui-focused fieldset': {
                    borderColor: '#2563EB',
                    boxShadow: '0 0 0 3px rgba(37, 99, 235, 0.1)',
                  },
                },
              }}
            />
            <TextField
              margin="normal"
              name="interestRate"
              label="معدل الفائدة (%)"
              type="number"
              fullWidth
              value={editForm.interestRate}
              onChange={handleEditFormChange}
              inputProps={{ step: 0.01 }}
              variant="outlined"
              sx={{
                '& .MuiOutlinedInput-root': {
                  borderRadius: 2,
                  backgroundColor: '#F8FAFC',
                  '&:hover fieldset': {
                    borderColor: '#2563EB',
                  },
                  '&.Mui-focused fieldset': {
                    borderColor: '#2563EB',
                    boxShadow: '0 0 0 3px rgba(37, 99, 235, 0.1)',
                  },
                },
              }}
            />
            <TextField
              margin="normal"
              name="overdraftLimit"
              label="حد السحب على المكشوف"
              type="number"
              fullWidth
              value={editForm.overdraftLimit}
              onChange={handleEditFormChange}
              inputProps={{ step: 0.01 }}
              variant="outlined"
              sx={{
                '& .MuiOutlinedInput-root': {
                  borderRadius: 2,
                  backgroundColor: '#F8FAFC',
                  '&:hover fieldset': {
                    borderColor: '#2563EB',
                  },
                  '&.Mui-focused fieldset': {
                    borderColor: '#2563EB',
                    boxShadow: '0 0 0 3px rgba(37, 99, 235, 0.1)',
                  },
                },
              }}
            />
            <TextField
              margin="normal"
              name="minimumBalance"
              label="الحد الأدنى للرصيد"
              type="number"
              fullWidth
              value={editForm.minimumBalance}
              onChange={handleEditFormChange}
              inputProps={{ step: 0.01 }}
              variant="outlined"
              sx={{
                '& .MuiOutlinedInput-root': {
                  borderRadius: 2,
                  backgroundColor: '#F8FAFC',
                  '&:hover fieldset': {
                    borderColor: '#2563EB',
                  },
                  '&.Mui-focused fieldset': {
                    borderColor: '#2563EB',
                    boxShadow: '0 0 0 3px rgba(37, 99, 235, 0.1)',
                  },
                },
              }}
            />
          </DialogContent>
          <DialogActions sx={{ p: 2, gap: 1 }}>
            <Button 
              onClick={() => setEditDialogOpen(false)}
              sx={{
                py: 1,
                px: 2.5,
                borderRadius: 1.5,
                textTransform: 'none',
                fontWeight: 600,
              }}
            >
              إلغاء
            </Button>
            <Button 
              onClick={handleEditSubmit}
              variant="contained"
              sx={{
                py: 1,
                px: 2.5,
                borderRadius: 1.5,
                background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
                textTransform: 'none',
                fontWeight: 700,
              }}
            >
              حفظ
            </Button>
          </DialogActions>
        </Dialog>
      </Box>
    </Container>
  );
};

export default AccountDetails;