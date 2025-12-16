import React, { useState, useEffect } from 'react';
import {
  Container,
  Box,
  Button,
  Card,
  CardContent,
  Typography,
  Grid,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Chip,
  Alert,
  CircularProgress,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Tooltip,
} from '@mui/material';
import {
  Delete,
  Add,
  ArrowBack,
  AccountBalance,
  Settings,
  Remove,
} from '@mui/icons-material';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';

const API_BASE_URL = 'http://localhost:9090/api';

export default function GroupDetails() {
  const navigate = useNavigate();
  const { groupId } = useParams();
  
  const [group, setGroup] = useState(null);
  const [accounts, setAccounts] = useState([]);
  const [balance, setBalance] = useState(0);
  const [statistics, setStatistics] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  
  const [addAccountDialog, setAddAccountDialog] = useState(false);
  const [statusDialog, setStatusDialog] = useState(false);
  const [removeAccountDialog, setRemoveAccountDialog] = useState(false);
  
  const [availableAccounts, setAvailableAccounts] = useState([]);
  const [selectedAccount, setSelectedAccount] = useState('');
  const [selectedStatus, setSelectedStatus] = useState('ACTIVE');
  const [selectedRemoveAccount, setSelectedRemoveAccount] = useState(null);

  const token = localStorage.getItem('token');
  const currentUser = JSON.parse(localStorage.getItem('user') || '{}');

  useEffect(() => {
    fetchGroupDetails();
  }, [groupId]);

  const fetchGroupDetails = async () => {
    setLoading(true);
    setError('');
    try {
      const [groupRes, accountsRes, balanceRes, statsRes, allAccountsRes] =
        await Promise.all([
          axios.get(`${API_BASE_URL}/groups/${groupId}`, {
            headers: { Authorization: `Bearer ${token}` },
          }),
          axios.get(`${API_BASE_URL}/groups/${groupId}/accounts`, {
            headers: { Authorization: `Bearer ${token}` },
          }),
          axios.get(`${API_BASE_URL}/groups/${groupId}/balance`, {
            headers: { Authorization: `Bearer ${token}` },
          }),
          axios.get(`${API_BASE_URL}/groups/${groupId}/statistics`, {
            headers: { Authorization: `Bearer ${token}` },
          }),
          axios.get(`${API_BASE_URL}/accounts/user/${currentUser.id}`, {
            headers: { Authorization: `Bearer ${token}` },
          }),
        ]);

      setGroup(groupRes.data);
      setAccounts(accountsRes.data);
      setBalance(balanceRes.data);
      setStatistics(statsRes.data);

      // Filter available accounts (not in group)
      const groupAccountIds = accountsRes.data.map(a => a.id);
      setAvailableAccounts(
        allAccountsRes.data.filter(a => !groupAccountIds.includes(a.id))
      );
    } catch (err) {
      console.error('خطأ في تحميل تفاصيل المجموعة:', err);
      setError('فشل في تحميل تفاصيل المجموعة');
    } finally {
      setLoading(false);
    }
  };

  const handleAddAccount = async () => {
    if (!selectedAccount) return;
    try {
      await axios.post(
        `${API_BASE_URL}/groups/${groupId}/accounts/${selectedAccount}`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setAddAccountDialog(false);
      setSelectedAccount('');
      fetchGroupDetails();
    } catch (err) {
      console.error('خطأ في إضافة الحساب:', err);
      setError('فشل في إضافة الحساب');
    }
  };

  const handleRemoveAccount = async () => {
    if (!selectedRemoveAccount) return;
    try {
      await axios.delete(
        `${API_BASE_URL}/groups/${groupId}/accounts/${selectedRemoveAccount.id}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setRemoveAccountDialog(false);
      setSelectedRemoveAccount(null);
      fetchGroupDetails();
    } catch (err) {
      console.error('خطأ في إزالة الحساب:', err);
      setError('فشل في إزالة الحساب');
    }
  };

  const handleChangeStatus = async () => {
    try {
      await axios.patch(
        `${API_BASE_URL}/groups/${groupId}/status`,
        {},
        {
          params: { status: selectedStatus },
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setStatusDialog(false);
      fetchGroupDetails();
    } catch (err) {
      console.error('خطأ في تغيير الحالة:', err);
      setError('فشل في تغيير حالة الحسابات');
    }
  };

  const getStatusColor = (status) => {
    const colors = {
      ACTIVE: 'success',
      FROZEN: 'warning',
      CLOSED: 'error',
    };
    return colors[status] || 'default';
  };

  const getStatusText = (status) => {
    const texts = {
      ACTIVE: 'نشط',
      FROZEN: 'مجمد',
      CLOSED: 'مغلق',
    };
    return texts[status] || status;
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress size={50} sx={{ color: '#2563EB' }} />
      </Box>
    );
  }

  if (!group) {
    return (
      <Container maxWidth="md">
        <Alert severity="error" sx={{ mt: 4, borderRadius: 2 }}>
          المجموعة غير موجودة
        </Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg">
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
            {group.groupName}
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

        <Grid container spacing={3} mb={3}>
          <Grid item xs={12} md={3}>
            <Card
              sx={{
                borderRadius: 3,
                background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
                color: 'white',
                boxShadow: '0 8px 24px rgba(37, 99, 235, 0.3)',
              }}
            >
              <CardContent sx={{ textAlign: 'center' }}>
                <AccountBalance sx={{ fontSize: 50, mb: 1, opacity: 0.3 }} />
                <Typography variant="body2" sx={{ opacity: 0.9, mb: 1 }}>
                  الرصيد الإجمالي
                </Typography>
                <Typography variant="h4" sx={{ fontWeight: 700 }}>
                  ${balance.toFixed(2)}
                </Typography>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} md={3}>
            <Card sx={{ borderRadius: 3, boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)' }}>
              <CardContent sx={{ textAlign: 'center' }}>
                <Typography variant="body2" sx={{ color: '#999', fontWeight: 600, mb: 1 }}>
                  عدد الحسابات
                </Typography>
                <Typography variant="h4" sx={{ fontWeight: 700, color: '#2563EB' }}>
                  {statistics?.totalAccounts || 0}
                </Typography>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} md={3}>
            <Card sx={{ borderRadius: 3, boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)' }}>
              <CardContent sx={{ textAlign: 'center' }}>
                <Typography variant="body2" sx={{ color: '#999', fontWeight: 600, mb: 1 }}>
                  الحسابات النشطة
                </Typography>
                <Typography variant="h4" sx={{ fontWeight: 700, color: '#10B981' }}>
                  {statistics?.activeAccounts || 0}
                </Typography>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} md={3}>
            <Card sx={{ borderRadius: 3, boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)' }}>
              <CardContent sx={{ textAlign: 'center' }}>
                <Typography variant="body2" sx={{ color: '#999', fontWeight: 600, mb: 1 }}>
                  متوسط الرصيد
                </Typography>
                <Typography variant="h6" sx={{ fontWeight: 700, color: '#7C3AED' }}>
                  ${(statistics?.averageBalance || 0).toFixed(2)}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>

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
                الحسابات في المجموعة
              </Typography>
              <Box display="flex" gap={1}>
                <Button
                  startIcon={<Settings />}
                  onClick={() => setStatusDialog(true)}
                  sx={{
                    textTransform: 'none',
                    fontWeight: 600,
                    borderRadius: 1.5,
                  }}
                  variant="outlined"
                >
                  إدارة الحالة
                </Button>
                <Button
                  startIcon={<Add />}
                  onClick={() => setAddAccountDialog(true)}
                  variant="contained"
                  sx={{
                    background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
                    textTransform: 'none',
                    fontWeight: 600,
                    borderRadius: 1.5,
                  }}
                >
                  إضافة حساب
                </Button>
              </Box>
            </Box>

            {accounts.length === 0 ? (
              <Typography sx={{ color: '#999', textAlign: 'center', py: 3 }}>
                لا توجد حسابات في هذه المجموعة
              </Typography>
            ) : (
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow
                      sx={{
                        background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
                        '& th': {
                          color: 'white',
                          fontWeight: 700,
                        },
                      }}
                    >
                      <TableCell sx={{ color: 'white', fontWeight: 700 }}>
                        رقم الحساب
                      </TableCell>
                      <TableCell sx={{ color: 'white', fontWeight: 700 }}>
                        نوع الحساب
                      </TableCell>
                      <TableCell sx={{ color: 'white', fontWeight: 700 }} align="right">
                        الرصيد
                      </TableCell>
                      <TableCell sx={{ color: 'white', fontWeight: 700 }}>الحالة</TableCell>
                      <TableCell sx={{ color: 'white', fontWeight: 700 }} align="center">
                        الإجراءات
                      </TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {accounts.map((account, index) => (
                      <TableRow
                        key={account.id}
                        sx={{
                          backgroundColor: index % 2 === 0 ? '#F8FAFC' : 'white',
                          '&:hover': {
                            backgroundColor: '#F0F4F8',
                          },
                        }}
                      >
                        <TableCell sx={{ fontWeight: 700, color: '#2563EB' }}>
                          {account.accountNumber}
                        </TableCell>
                        <TableCell>{account.accountType}</TableCell>
                        <TableCell align="right" sx={{ fontWeight: 600, color: '#10B981' }}>
                          ${account.balance?.toFixed(2)}
                        </TableCell>
                        <TableCell>
                          <Chip
                            label={getStatusText(account.status)}
                            color={getStatusColor(account.status)}
                            size="small"
                            sx={{ fontWeight: 700 }}
                          />
                        </TableCell>
                        <TableCell align="center">
                          <Tooltip title="إزالة من المجموعة">
                            <IconButton
                              size="small"
                              onClick={() => {
                                setSelectedRemoveAccount(account);
                                setRemoveAccountDialog(true);
                              }}
                              sx={{
                                color: '#EF4444',
                                backgroundColor: '#FEE2E2',
                                transition: 'all 0.3s ease',
                                '&:hover': {
                                  backgroundColor: '#FECACA',
                                },
                              }}
                            >
                              <Delete fontSize="small" />
                            </IconButton>
                          </Tooltip>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            )}
          </CardContent>
        </Card>

        <Box mt={2} display="flex" gap={2}>
          <Button
            variant="contained"
            onClick={() => navigate(`/groups/${groupId}/transfer`)}
            sx={{
              background: 'linear-gradient(135deg, #7C3AED 0%, #2563EB 100%)',
              textTransform: 'none',
              fontWeight: 700,
              borderRadius: 2,
              py: 1.2,
              px: 3,
            }}
          >
            تحويل أموال
          </Button>
        </Box>
      </Box>

      {/* Add Account Dialog */}
      <Dialog
        open={addAccountDialog}
        onClose={() => setAddAccountDialog(false)}
        PaperProps={{
          sx: {
            borderRadius: 3,
            boxShadow: '0 20px 60px rgba(0, 0, 0, 0.3)',
          },
        }}
      >
        <DialogTitle sx={{ fontWeight: 700, color: '#333' }}>
          إضافة حساب للمجموعة
        </DialogTitle>
        <DialogContent sx={{ pt: 2 }}>
          <FormControl fullWidth>
            <InputLabel>اختر حساباً</InputLabel>
            <Select
              value={selectedAccount}
              onChange={(e) => setSelectedAccount(e.target.value)}
              label="اختر حساباً"
            >
              {availableAccounts.map((acc) => (
                <MenuItem key={acc.id} value={acc.id}>
                  {acc.accountNumber} - {acc.accountType}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </DialogContent>
        <DialogActions sx={{ p: 2, gap: 1 }}>
          <Button onClick={() => setAddAccountDialog(false)}>إلغاء</Button>
          <Button
            onClick={handleAddAccount}
            variant="contained"
            sx={{
              background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
              textTransform: 'none',
              fontWeight: 700,
            }}
          >
            إضافة
          </Button>
        </DialogActions>
      </Dialog>

      {/* Remove Account Dialog */}
      <Dialog
        open={removeAccountDialog}
        onClose={() => setRemoveAccountDialog(false)}
        PaperProps={{
          sx: {
            borderRadius: 3,
            boxShadow: '0 20px 60px rgba(0, 0, 0, 0.3)',
          },
        }}
      >
        <DialogTitle sx={{ fontWeight: 700, color: '#333' }}>
          إزالة حساب من المجموعة
        </DialogTitle>
        <DialogContent>
          <Typography sx={{ color: '#555', fontWeight: 500 }}>
            هل تريد إزالة الحساب {selectedRemoveAccount?.accountNumber} من المجموعة؟
          </Typography>
        </DialogContent>
        <DialogActions sx={{ p: 2, gap: 1 }}>
          <Button onClick={() => setRemoveAccountDialog(false)}>إلغاء</Button>
          <Button
            onClick={handleRemoveAccount}
            variant="contained"
            sx={{
              background: 'linear-gradient(135deg, #EF4444 0%, #DC2626 100%)',
              textTransform: 'none',
              fontWeight: 700,
            }}
          >
            إزالة
          </Button>
        </DialogActions>
      </Dialog>

      {/* Status Dialog */}
      <Dialog
        open={statusDialog}
        onClose={() => setStatusDialog(false)}
        PaperProps={{
          sx: {
            borderRadius: 3,
            boxShadow: '0 20px 60px rgba(0, 0, 0, 0.3)',
          },
        }}
      >
        <DialogTitle sx={{ fontWeight: 700, color: '#333' }}>
          تغيير حالة جميع الحسابات
        </DialogTitle>
        <DialogContent sx={{ pt: 2 }}>
          <FormControl fullWidth>
            <InputLabel>اختر الحالة الجديدة</InputLabel>
            <Select
              value={selectedStatus}
              onChange={(e) => setSelectedStatus(e.target.value)}
              label="اختر الحالة الجديدة"
            >
              <MenuItem value="ACTIVE">نشط</MenuItem>
              <MenuItem value="FROZEN">مجمد</MenuItem>
              <MenuItem value="CLOSED">مغلق</MenuItem>
            </Select>
          </FormControl>
        </DialogContent>
        <DialogActions sx={{ p: 2, gap: 1 }}>
          <Button onClick={() => setStatusDialog(false)}>إلغاء</Button>
          <Button
            onClick={handleChangeStatus}
            variant="contained"
            sx={{
              background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
              textTransform: 'none',
              fontWeight: 700,
            }}
          >
            حفظ
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
}
