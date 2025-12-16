import React, { useState, useEffect } from 'react';
import {
  Container,
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  CircularProgress,
  Alert,
} from '@mui/material';
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import TransferWithinAStationIcon from '@mui/icons-material/TransferWithinAStation';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import api, { authApi } from '../services/api';

const Dashboard = () => {
  const [user, setUser] = useState(null);
  const [accounts, setAccounts] = useState([]);
  const [totalBalance, setTotalBalance] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const userResponse = await authApi.get('/me');
      setUser(userResponse.data);

      // Get user's accounts
      const accountsResponse = await api.get(`/accounts/user/${userResponse.data.userId}`);
      setAccounts(accountsResponse.data);

      // Get total balance
      const balanceResponse = await api.get(`/accounts/user/${userResponse.data.userId}/total-balance`);
      setTotalBalance(balanceResponse.data.totalBalance || 0);
    } catch (err) {
      setError('فشل في تحميل بيانات لوحة التحكم');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress size={50} sx={{ color: '#2563EB' }} />
      </Box>
    );
  }

  const activeAccounts = accounts.filter(account => account.status === 'ACTIVE');
  const totalAccounts = accounts.length;

  const StatCard = ({ icon: Icon, label, value, color, gradient }) => (
    <Card
      sx={{
        height: '100%',
        borderRadius: 3,
        background: `linear-gradient(135deg, ${gradient[0]} 0%, ${gradient[1]} 100%)`,
        color: 'white',
        boxShadow: `0 8px 24px ${color}33`,
        border: 'none',
        overflow: 'hidden',
        position: 'relative',
        transition: 'all 0.3s ease',
        '&:hover': {
          transform: 'translateY(-8px)',
          boxShadow: `0 12px 40px ${color}44`,
        },
        '&::before': {
          content: '""',
          position: 'absolute',
          top: '-50px',
          right: '-50px',
          width: '150px',
          height: '150px',
          borderRadius: '50%',
          background: 'rgba(255, 255, 255, 0.1)',
        },
      }}
    >
      <CardContent sx={{ position: 'relative', zIndex: 1 }}>
        <Box display="flex" alignItems="center" justifyContent="space-between">
          <Box>
            <Typography 
              sx={{ 
                fontSize: '0.95rem', 
                fontWeight: 500, 
                opacity: 0.9,
                mb: 1,
              }}
            >
              {label}
            </Typography>
            <Typography 
              variant="h5" 
              component="div"
              sx={{ 
                fontWeight: 700,
                fontSize: '1.8rem',
              }}
            >
              {value}
            </Typography>
          </Box>
          <Icon sx={{ fontSize: 50, opacity: 0.2 }} />
        </Box>
      </CardContent>
    </Card>
  );

  return (
    <Container maxWidth="lg">
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
          مرحباً بك، {user?.fullName}
        </Typography>

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

        <Grid container spacing={2.5}>
          {/* Total Balance */}
          <Grid item xs={12} sm={6} md={3}>
            <StatCard
              icon={AccountBalanceIcon}
              label="إجمالي الرصيد"
              value={`$${totalBalance.toFixed(2)}`}
              color="#2563EB"
              gradient={['#2563EB', '#3B82F6']}
            />
          </Grid>

          {/* Total Accounts */}
          <Grid item xs={12} sm={6} md={3}>
            <StatCard
              icon={AccountBalanceWalletIcon}
              label="عدد الحسابات"
              value={totalAccounts}
              color="#7C3AED"
              gradient={['#7C3AED', '#A78BFA']}
            />
          </Grid>

          {/* Active Accounts */}
          <Grid item xs={12} sm={6} md={3}>
            <StatCard
              icon={TrendingUpIcon}
              label="الحسابات النشطة"
              value={activeAccounts.length}
              color="#10B981"
              gradient={['#10B981', '#34D399']}
            />
          </Grid>

          {/* Recent Activity */}
          <Grid item xs={12} sm={6} md={3}>
            <StatCard
              icon={TransferWithinAStationIcon}
              label="آخر تحديث"
              value="نشط"
              color="#F59E0B"
              gradient={['#F59E0B', '#FBBF24']}
            />
          </Grid>

          {/* Recent Accounts Section */}
          <Grid item xs={12}>
            <Card
              sx={{
                borderRadius: 3,
                boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)',
                border: '1px solid rgba(0, 0, 0, 0.05)',
              }}
            >
              <CardContent sx={{ pb: 3 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                  <AccountBalanceIcon 
                    sx={{ 
                      mr: 2, 
                      color: '#2563EB',
                      fontSize: '1.8rem',
                    }} 
                  />
                  <Typography 
                    variant="h6" 
                    component="h2"
                    sx={{
                      fontWeight: 700,
                      color: '#333',
                    }}
                  >
                    حساباتك الأخيرة
                  </Typography>
                </Box>
                
                {accounts.length > 0 ? (
                  <Grid container spacing={2}>
                    {accounts.slice(0, 6).map((account) => (
                      <Grid item xs={12} sm={6} md={4} key={account.id}>
                        <Card
                          sx={{
                            borderRadius: 2,
                            border: '1px solid #E5E7EB',
                            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.05)',
                            transition: 'all 0.3s ease',
                            cursor: 'pointer',
                            '&:hover': {
                              transform: 'translateY(-4px)',
                              boxShadow: '0 8px 20px rgba(0, 0, 0, 0.1)',
                              borderColor: '#2563EB',
                            },
                          }}
                        >
                          <CardContent sx={{ p: 2 }}>
                            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', mb: 2 }}>
                              <Box>
                                <Typography 
                                  variant="body2" 
                                  sx={{ 
                                    color: '#999', 
                                    fontWeight: 600,
                                    fontSize: '0.85rem',
                                    mb: 0.5,
                                  }}
                                >
                                  رقم الحساب
                                </Typography>
                                <Typography 
                                  variant="h6" 
                                  component="div"
                                  sx={{
                                    fontWeight: 700,
                                    color: '#333',
                                    fontSize: '1.1rem',
                                  }}
                                >
                                  {account.accountNumber}
                                </Typography>
                              </Box>
                              <Box
                                sx={{
                                  px: 2,
                                  py: 1,
                                  borderRadius: 1.5,
                                  backgroundColor: 
                                    account.status === 'ACTIVE' ? '#D1FAE5' :
                                    account.status === 'FROZEN' ? '#FEF3C7' :
                                    '#FEE2E2',
                                }}
                              >
                                <Typography
                                  sx={{
                                    fontSize: '0.75rem',
                                    fontWeight: 700,
                                    color:
                                      account.status === 'ACTIVE' ? '#059669' :
                                      account.status === 'FROZEN' ? '#D97706' :
                                      '#DC2626',
                                  }}
                                >
                                  {account.status === 'ACTIVE' ? 'نشط' :
                                   account.status === 'FROZEN' ? 'مجمد' :
                                   'مغلق'}
                                </Typography>
                              </Box>
                            </Box>
                            
                            <Typography 
                              sx={{ 
                                color: '#666', 
                                fontSize: '0.9rem',
                                mb: 1.5,
                                fontWeight: 500,
                              }}
                            >
                              {account.accountType === 'SAVINGS' ? '🏦 حساب توفير' :
                               account.accountType === 'CHECKING' ? '💳 حساب جاري' :
                               account.accountType === 'LOAN' ? '📊 قرض' :
                               account.accountType === 'INVESTMENT' ? '📈 استثمار' :
                               account.accountType === 'BUSINESS' ? '🏢 تجاري' : account.accountType}
                            </Typography>

                            <Box sx={{ 
                              pt: 1.5, 
                              borderTop: '1px solid #E5E7EB',
                              display: 'flex',
                              justifyContent: 'space-between',
                              alignItems: 'center',
                            }}>
                              <Typography 
                                variant="body2" 
                                sx={{ 
                                  color: '#999', 
                                  fontSize: '0.85rem',
                                  fontWeight: 600,
                                }}
                              >
                                الرصيد
                              </Typography>
                              <Typography 
                                variant="h6"
                                sx={{
                                  color: '#2563EB',
                                  fontWeight: 700,
                                  fontSize: '1.1rem',
                                }}
                              >
                                ${account.balance?.toFixed(2)}
                              </Typography>
                            </Box>
                          </CardContent>
                        </Card>
                      </Grid>
                    ))}
                  </Grid>
                ) : (
                  <Box sx={{ textAlign: 'center', py: 4 }}>
                    <Typography sx={{ color: '#999', fontWeight: 500 }}>
                      لا توجد حسابات حالياً
                    </Typography>
                  </Box>
                )}
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </Box>
    </Container>
  );
};

export default Dashboard;