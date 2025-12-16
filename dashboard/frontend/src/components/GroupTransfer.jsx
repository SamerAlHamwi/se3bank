import React, { useState, useEffect } from 'react';
import {
  Container,
  Box,
  Button,
  Card,
  CardContent,
  TextField,
  Typography,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  CircularProgress,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
} from '@mui/material';
import {
  ArrowBack,
  SwapHoriz,
  CheckCircle,
  ErrorOutline,
} from '@mui/icons-material';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';

const API_BASE_URL = 'http://localhost:9090/api';

export default function GroupTransfer() {
  const navigate = useNavigate();
  const { groupId } = useParams();

  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [formData, setFormData] = useState({
    fromAccount: '',
    toAccount: '',
    amount: '',
  });

  const [confirmDialog, setConfirmDialog] = useState(false);
  const [transferResult, setTransferResult] = useState(null);
  const [resultDialog, setResultDialog] = useState(false);

  const token = localStorage.getItem('token');

  useEffect(() => {
    fetchGroupAccounts();
  }, [groupId]);

  const fetchGroupAccounts = async () => {
    setLoading(true);
    try {
      const response = await axios.get(
        `${API_BASE_URL}/groups/${groupId}/accounts`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setAccounts(response.data);
    } catch (err) {
      console.error('خطأ في تحميل الحسابات:', err);
      setError('فشل في تحميل حسابات المجموعة');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
    setError('');
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    // Validation
    if (!formData.fromAccount || !formData.toAccount || !formData.amount) {
      setError('يرجى ملء جميع الحقول المطلوبة');
      return;
    }

    if (formData.fromAccount === formData.toAccount) {
      setError('لا يمكن التحويل من والى نفس الحساب');
      return;
    }

    if (parseFloat(formData.amount) <= 0) {
      setError('المبلغ يجب أن يكون أكبر من صفر');
      return;
    }

    setConfirmDialog(true);
  };

  const handleTransfer = async () => {
    setConfirmDialog(false);
    setLoading(true);

    try {
      const fromAccount = accounts.find(a => a.id == formData.fromAccount);
      const toAccount = accounts.find(a => a.id == formData.toAccount);

      await axios.post(
        `${API_BASE_URL}/groups/${groupId}/transfer`,
        {},
        {
          params: {
            fromAccount: fromAccount.accountNumber,
            toAccount: toAccount.accountNumber,
            amount: parseFloat(formData.amount),
          },
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      setTransferResult({
        success: true,
        message: 'تم التحويل بنجاح',
        from: fromAccount.accountNumber,
        to: toAccount.accountNumber,
        amount: parseFloat(formData.amount),
      });
      setResultDialog(true);
      setFormData({ fromAccount: '', toAccount: '', amount: '' });
    } catch (err) {
      console.error('خطأ في التحويل:', err);
      setTransferResult({
        success: false,
        message:
          err.response?.data?.message || 'فشل في إجراء التحويل. تحقق من الرصيد المتاح.',
      });
      setResultDialog(true);
    } finally {
      setLoading(false);
    }
  };

  if (loading && accounts.length === 0) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress size={50} sx={{ color: '#2563EB' }} />
      </Box>
    );
  }

  return (
    <Container maxWidth="md">
      <Box sx={{ mb: 4 }}>
        <Box display="flex" alignItems="center" mb={3}>
          <IconButton
            onClick={() => navigate(-1)}
            sx={{
              mr: 2,
              transition: 'all 0.3s ease',
              '&:hover': {
                backgroundColor: '#EFF6FF',
                color: '#2563EB',
              },
            }}
          >
            <ArrowBack />
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
            تحويل داخل المجموعة
          </Typography>
        </Box>

        <Card
          sx={{
            borderRadius: 3,
            boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)',
            border: '1px solid rgba(0, 0, 0, 0.05)',
          }}
        >
          <CardContent sx={{ p: 3 }}>
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

            <form onSubmit={handleSubmit}>
              {/* From Account */}
              <FormControl
                fullWidth
                margin="normal"
                required
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
                <InputLabel>من (الحساب المرسل)</InputLabel>
                <Select
                  name="fromAccount"
                  value={formData.fromAccount}
                  onChange={handleChange}
                  label="من (الحساب المرسل)"
                >
                  {accounts.map((account) => (
                    <MenuItem key={account.id} value={account.id}>
                      {account.accountNumber} - الرصيد: ${account.balance?.toFixed(2)}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>

              {/* Swap Button */}
              <Box display="flex" justifyContent="center" my={2}>
                <Button
                  type="button"
                  sx={{
                    p: 1,
                    borderRadius: '50%',
                    minWidth: '44px',
                    backgroundColor: '#F3E8FF',
                    color: '#7C3AED',
                    transition: 'all 0.3s ease',
                    '&:hover': {
                      backgroundColor: '#E9D5FF',
                      transform: 'rotate(180deg)',
                    },
                  }}
                  onClick={() => {
                    setFormData({
                      ...formData,
                      fromAccount: formData.toAccount,
                      toAccount: formData.fromAccount,
                    });
                  }}
                >
                  <SwapHoriz />
                </Button>
              </Box>

              {/* To Account */}
              <FormControl
                fullWidth
                margin="normal"
                required
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
                <InputLabel>إلى (الحساب المستقبل)</InputLabel>
                <Select
                  name="toAccount"
                  value={formData.toAccount}
                  onChange={handleChange}
                  label="إلى (الحساب المستقبل)"
                >
                  {accounts.map((account) => (
                    <MenuItem key={account.id} value={account.id}>
                      {account.accountNumber} - الرصيد: ${account.balance?.toFixed(2)}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>

              {/* Amount */}
              <TextField
                fullWidth
                label="المبلغ"
                name="amount"
                type="number"
                value={formData.amount}
                onChange={handleChange}
                required
                margin="normal"
                placeholder="0.00"
                inputProps={{ step: 0.01, min: 0 }}
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

              {/* Summary Card */}
              {formData.fromAccount && formData.toAccount && formData.amount && (
                <Card
                  sx={{
                    mt: 3,
                    borderRadius: 2,
                    backgroundColor: '#F0F4F8',
                    border: '1px solid #DBEAFE',
                  }}
                >
                  <CardContent>
                    <Typography variant="body2" sx={{ color: '#666', fontWeight: 600, mb: 1 }}>
                      ملخص التحويل
                    </Typography>
                    <Box display="flex" justifyContent="space-between">
                      <Typography sx={{ color: '#555' }}>
                        من:{' '}
                        {accounts.find(a => a.id == formData.fromAccount)?.accountNumber}
                      </Typography>
                      <Typography sx={{ color: '#555' }}>
                        إلى:{' '}
                        {accounts.find(a => a.id == formData.toAccount)?.accountNumber}
                      </Typography>
                      <Typography sx={{ color: '#2563EB', fontWeight: 700 }}>
                        ${parseFloat(formData.amount || 0).toFixed(2)}
                      </Typography>
                    </Box>
                  </CardContent>
                </Card>
              )}

              <Box display="flex" gap={2} mt={3}>
                <Button
                  variant="outlined"
                  onClick={() => navigate(-1)}
                  sx={{
                    py: 1.2,
                    px: 3,
                    borderRadius: 2,
                    textTransform: 'none',
                    fontWeight: 600,
                  }}
                >
                  إلغاء
                </Button>
                <Button
                  type="submit"
                  variant="contained"
                  disabled={loading}
                  sx={{
                    flex: 1,
                    py: 1.2,
                    borderRadius: 2,
                    background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
                    textTransform: 'none',
                    fontWeight: 700,
                  }}
                >
                  {loading ? 'جاري المعالجة...' : 'متابعة'}
                </Button>
              </Box>
            </form>
          </CardContent>
        </Card>
      </Box>

      {/* Confirm Dialog */}
      <Dialog
        open={confirmDialog}
        onClose={() => setConfirmDialog(false)}
        PaperProps={{
          sx: {
            borderRadius: 3,
            boxShadow: '0 20px 60px rgba(0, 0, 0, 0.3)',
          },
        }}
      >
        <DialogTitle sx={{ fontWeight: 700, color: '#333' }}>
          تأكيد التحويل
        </DialogTitle>
        <DialogContent>
          <Box sx={{ mt: 2 }}>
            <Typography variant="body2" sx={{ color: '#666', mb: 2 }}>
              تأكد من البيانات قبل إتمام التحويل:
            </Typography>
            <Box sx={{ p: 2, backgroundColor: '#F8FAFC', borderRadius: 2 }}>
              <Typography sx={{ mb: 1 }}>
                <strong>من:</strong>{' '}
                {accounts.find(a => a.id == formData.fromAccount)?.accountNumber}
              </Typography>
              <Typography sx={{ mb: 1 }}>
                <strong>إلى:</strong>{' '}
                {accounts.find(a => a.id == formData.toAccount)?.accountNumber}
              </Typography>
              <Typography sx={{ color: '#2563EB', fontWeight: 700 }}>
                <strong>المبلغ:</strong> ${parseFloat(formData.amount).toFixed(2)}
              </Typography>
            </Box>
          </Box>
        </DialogContent>
        <DialogActions sx={{ p: 2, gap: 1 }}>
          <Button
            onClick={() => setConfirmDialog(false)}
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
            onClick={handleTransfer}
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

      {/* Result Dialog */}
      <Dialog
        open={resultDialog}
        onClose={() => {
          setResultDialog(false);
          if (transferResult?.success) {
            navigate(-1);
          }
        }}
        PaperProps={{
          sx: {
            borderRadius: 3,
            boxShadow: '0 20px 60px rgba(0, 0, 0, 0.3)',
          },
        }}
      >
        <DialogContent sx={{ textAlign: 'center', pt: 4, pb: 2 }}>
          {transferResult?.success ? (
            <>
              <CheckCircle
                sx={{
                  fontSize: 80,
                  color: '#10B981',
                  mb: 2,
                }}
              />
              <Typography variant="h5" sx={{ fontWeight: 700, mb: 2, color: '#333' }}>
                {transferResult.message}
              </Typography>
              <Box sx={{ p: 2, backgroundColor: '#F0FDF4', borderRadius: 2 }}>
                <Typography sx={{ mb: 1 }}>
                  <strong>من:</strong> {transferResult.from}
                </Typography>
                <Typography sx={{ mb: 1 }}>
                  <strong>إلى:</strong> {transferResult.to}
                </Typography>
                <Typography sx={{ color: '#10B981', fontWeight: 700 }}>
                  <strong>المبلغ:</strong> ${transferResult.amount.toFixed(2)}
                </Typography>
              </Box>
            </>
          ) : (
            <>
              <ErrorOutline
                sx={{
                  fontSize: 80,
                  color: '#EF4444',
                  mb: 2,
                }}
              />
              <Typography variant="h5" sx={{ fontWeight: 700, mb: 2, color: '#333' }}>
                فشل التحويل
              </Typography>
              <Typography sx={{ color: '#666' }}>
                {transferResult?.message}
              </Typography>
            </>
          )}
        </DialogContent>
        <DialogActions sx={{ p: 2, justifyContent: 'center' }}>
          <Button
            onClick={() => {
              setResultDialog(false);
              if (transferResult?.success) {
                navigate(-1);
              }
            }}
            variant="contained"
            sx={{
              px: 3,
              borderRadius: 1.5,
              background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
              textTransform: 'none',
              fontWeight: 700,
            }}
          >
            حسناً
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
}
