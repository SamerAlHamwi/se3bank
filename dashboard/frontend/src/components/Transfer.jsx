import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Paper,
  TextField,
  Button,
  Typography,
  Box,
  Alert,
  Grid,
  Card,
  CardContent,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from '@mui/material';
import SendIcon from '@mui/icons-material/Send';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ErrorIcon from '@mui/icons-material/Error';
import api, { authApi } from '../services/api';

const Transfer = () => {
  const [formData, setFormData] = useState({
    fromAccountNumber: '',
    toAccountNumber: '',
    amount: '',
    description: '',
  });
  const [user, setUser] = useState(null);
  const [userAccounts, setUserAccounts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [confirmDialogOpen, setConfirmDialogOpen] = useState(false);
  const [transferResult, setTransferResult] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchUserData();
  }, []);

  const fetchUserData = async () => {
    try {
      const userResponse = await authApi.get('/me');
      setUser(userResponse.data);

      // Get user's accounts for fromAccount selection
      const accountsResponse = await api.get(`/accounts/user/${userResponse.data.userId}`);
      setUserAccounts(accountsResponse.data.filter(account => account.status === 'ACTIVE'));
    } catch (err) {
      setError('فشل في تحميل البيانات');
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (parseFloat(formData.amount) <= 0) {
      setError('يجب أن يكون المبلغ أكبر من صفر');
      return;
    }
    setConfirmDialogOpen(true);
  };

  const handleConfirmTransfer = async () => {
    setConfirmDialogOpen(false);
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const response = await api.post('/accounts/transfer', {
        fromAccountNumber: formData.fromAccountNumber,
        toAccountNumber: formData.toAccountNumber,
        amount: parseFloat(formData.amount),
        description: formData.description,
      });

      setTransferResult(response.data);
      setSuccess('تم إجراء التحويل بنجاح');
      setFormData({
        fromAccountNumber: '',
        toAccountNumber: '',
        amount: '',
        description: '',
      });
    } catch (err) {
      setError(err.response?.data?.message || 'فشل في إجراء التحويل');
    } finally {
      setLoading(false);
    }
  };

  const checkAccountExists = async (accountNumber) => {
    if (!accountNumber) return;
    try {
      const response = await api.get(`/accounts/exists/${accountNumber}`);
      return response.data.exists;
    } catch (err) {
      return false;
    }
  };

  return (
    <Container maxWidth="md">
      <Box sx={{ mb: 4 }}>
        <Typography 
          variant="h4" 
          component="h1" 
          gutterBottom
          sx={{
            fontWeight: 700,
            background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
            backgroundClip: 'text',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent',
            mb: 3,
          }}
        >
          تحويل الأموال
        </Typography>

        <Grid container spacing={3}>
          <Grid item xs={12} md={8}>
            <Paper 
              sx={{ 
                p: { xs: 2.5, sm: 3.5 },
                borderRadius: 3,
                boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)',
                border: '1px solid rgba(0, 0, 0, 0.05)',
              }}
            >
              {error && (
                <Alert 
                  severity="error" 
                  sx={{ 
                    mb: 2,
                    borderRadius: 2,
                    boxShadow: '0 4px 12px rgba(239, 68, 68, 0.2)',
                  }}
                >
                  {error}
                </Alert>
              )}

              {success && (
                <Alert 
                  severity="success" 
                  sx={{ 
                    mb: 2,
                    borderRadius: 2,
                    boxShadow: '0 4px 12px rgba(16, 185, 129, 0.2)',
                  }}
                >
                  {success}
                </Alert>
              )}

              <Box component="form" onSubmit={handleSubmit}>
                <Grid container spacing={2.5}>
                  <Grid item xs={12}>
                    <TextField
                      select
                      fullWidth
                      label="من حساب"
                      name="fromAccountNumber"
                      value={formData.fromAccountNumber}
                      onChange={handleChange}
                      required
                      variant="outlined"
                      SelectProps={{
                        native: true,
                      }}
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
                    >
                      <option value="">اختر الحساب</option>
                      {userAccounts.map((account) => (
                        <option key={account.id} value={account.accountNumber}>
                          {account.accountNumber} - {account.accountType} (${account.balance?.toFixed(2)})
                        </option>
                      ))}
                    </TextField>
                  </Grid>

                  <Grid item xs={12}>
                    <TextField
                      fullWidth
                      label="إلى حساب (رقم الحساب)"
                      name="toAccountNumber"
                      value={formData.toAccountNumber}
                      onChange={handleChange}
                      required
                      placeholder="أدخل رقم الحساب المستلم"
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
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="المبلغ"
                      name="amount"
                      type="number"
                      value={formData.amount}
                      onChange={handleChange}
                      required
                      inputProps={{ min: 0.01, step: 0.01 }}
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
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="الوصف (اختياري)"
                      name="description"
                      value={formData.description}
                      onChange={handleChange}
                      placeholder="سبب التحويل"
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
                  </Grid>

                  <Grid item xs={12}>
                    <Button
                      type="submit"
                      variant="contained"
                      startIcon={loading ? <CircularProgress size={20} /> : <SendIcon />}
                      disabled={loading}
                      fullWidth
                      size="large"
                      sx={{
                        py: 1.3,
                        borderRadius: 2,
                        fontSize: '1rem',
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
                      {loading ? 'جاري التحويل...' : 'إجراء التحويل'}
                    </Button>
                  </Grid>
                </Grid>
              </Box>
            </Paper>
          </Grid>

          <Grid item xs={12} md={4}>
            <Card
              sx={{
                borderRadius: 3,
                boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)',
                border: '1px solid rgba(0, 0, 0, 0.05)',
              }}
            >
              <CardContent>
                <Typography 
                  variant="h6" 
                  gutterBottom
                  sx={{ fontWeight: 700, color: '#333', mb: 2 }}
                >
                  معلومات التحويل
                </Typography>
                <Box sx={{ 
                  p: 2, 
                  backgroundColor: '#F0F4FF', 
                  borderRadius: 2,
                  mb: 2,
                  borderLeft: '4px solid #2563EB',
                }}>
                  <Typography 
                    variant="body2" 
                    sx={{ 
                      color: '#555', 
                      fontWeight: 500,
                      mb: 1,
                    }}
                  >
                    ✓ تأكد من صحة رقم الحساب المستلم قبل إجراء التحويل
                  </Typography>
                  <Typography 
                    variant="body2" 
                    sx={{ 
                      color: '#555', 
                      fontWeight: 500,
                      mb: 1,
                    }}
                  >
                    ✓ سيتم خصم المبلغ من رصيد حسابك فوراً
                  </Typography>
                  <Typography 
                    variant="body2" 
                    sx={{ 
                      color: '#10B981', 
                      fontWeight: 700,
                    }}
                  >
                    ✓ رسوم التحويل: مجاناً
                  </Typography>
                </Box>
              </CardContent>
            </Card>

            {transferResult && (
              <Card 
                sx={{ 
                  mt: 2,
                  borderRadius: 3,
                  boxShadow: transferResult.success 
                    ? '0 8px 24px rgba(16, 185, 129, 0.2)' 
                    : '0 8px 24px rgba(239, 68, 68, 0.2)',
                  border: '1px solid rgba(0, 0, 0, 0.05)',
                }}
              >
                <CardContent>
                  <Box display="flex" alignItems="center" mb={2}>
                    {transferResult.success ? (
                      <CheckCircleIcon 
                        sx={{ 
                          mr: 1, 
                          color: '#10B981',
                          fontSize: '1.8rem',
                        }} 
                      />
                    ) : (
                      <ErrorIcon 
                        sx={{ 
                          mr: 1, 
                          color: '#EF4444',
                          fontSize: '1.8rem',
                        }} 
                      />
                    )}
                    <Typography 
                      variant="h6"
                      sx={{
                        fontWeight: 700,
                        color: transferResult.success ? '#10B981' : '#EF4444',
                      }}
                    >
                      {transferResult.success ? 'تم بنجاح' : 'فشل التحويل'}
                    </Typography>
                  </Box>
                  <Box sx={{ 
                    p: 2, 
                    backgroundColor: '#F8FAFC',
                    borderRadius: 2,
                  }}>
                    <Typography variant="body2" sx={{ mb: 1, fontWeight: 600 }}>
                      من: <span style={{ color: '#2563EB' }}>{transferResult.fromAccount}</span>
                    </Typography>
                    <Typography variant="body2" sx={{ mb: 1, fontWeight: 600 }}>
                      إلى: <span style={{ color: '#2563EB' }}>{transferResult.toAccount}</span>
                    </Typography>
                    <Typography variant="body2" sx={{ mb: 1, fontWeight: 600 }}>
                      المبلغ: <span style={{ color: '#10B981', fontWeight: 700 }}>${transferResult.amount?.toFixed(2)}</span>
                    </Typography>
                    <Typography variant="body2" sx={{ mb: 1, fontWeight: 600 }}>
                      الرصيد الجديد: <span style={{ color: '#7C3AED', fontWeight: 700 }}>${transferResult.newFromBalance?.toFixed(2)}</span>
                    </Typography>
                  </Box>
                  <Typography 
                    variant="caption" 
                    sx={{ 
                      color: '#999', 
                      mt: 1.5,
                      display: 'block',
                    }}
                  >
                    {transferResult.message}
                  </Typography>
                </CardContent>
              </Card>
            )}
          </Grid>
        </Grid>

        {/* Confirmation Dialog */}
        <Dialog
          open={confirmDialogOpen}
          onClose={() => setConfirmDialogOpen(false)}
          PaperProps={{
            sx: {
              borderRadius: 3,
              boxShadow: '0 20px 60px rgba(0, 0, 0, 0.3)',
            },
          }}
        >
          <DialogTitle sx={{ fontWeight: 700, color: '#333', pb: 1 }}>
            تأكيد التحويل
          </DialogTitle>
          <DialogContent sx={{ pt: 2 }}>
            <DialogContentText sx={{ color: '#555', fontWeight: 500, mb: 2 }}>
              هل أنت متأكد من إجراء هذا التحويل؟
            </DialogContentText>
            <Box sx={{ 
              p: 2.5, 
              backgroundColor: '#F8FAFC',
              borderRadius: 2,
              border: '1px solid #E5E7EB',
            }}>
              <Typography variant="body2" sx={{ mb: 1.5, fontWeight: 600 }}>
                من حساب: <span style={{ color: '#2563EB' }}>{formData.fromAccountNumber}</span>
              </Typography>
              <Typography variant="body2" sx={{ mb: 1.5, fontWeight: 600 }}>
                إلى حساب: <span style={{ color: '#2563EB' }}>{formData.toAccountNumber}</span>
              </Typography>
              <Typography variant="body2" sx={{ mb: 1.5, fontWeight: 700, fontSize: '1.05rem' }}>
                المبلغ: <span style={{ color: '#10B981' }}>${parseFloat(formData.amount || 0).toFixed(2)}</span>
              </Typography>
              {formData.description && (
                <Typography variant="body2" sx={{ fontWeight: 600 }}>
                  الوصف: <span style={{ color: '#666' }}>{formData.description}</span>
                </Typography>
              )}
            </Box>
          </DialogContent>
          <DialogActions sx={{ p: 2, gap: 1 }}>
            <Button 
              onClick={() => setConfirmDialogOpen(false)}
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
              onClick={handleConfirmTransfer} 
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
              تأكيد التحويل
            </Button>
          </DialogActions>
        </Dialog>
      </Box>
    </Container>
  );
};

export default Transfer;