import React, { useState, useEffect } from 'react';
import { 
  Box, 
  Typography, 
  Paper, 
  Grid, 
  Card, 
  CardContent, 
  CardHeader, 
  Avatar, 
  CircularProgress, 
  Alert 
} from '@mui/material';
import StarIcon from '@mui/icons-material/Star';
import SecurityIcon from '@mui/icons-material/Security';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import api from '../../services/api';

const ManagerFeatures = () => {
  const [features, setFeatures] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchFeatures = async () => {
      try {
        const response = await api.get('/decorators/info');
        setFeatures(response.data);
      } catch (err) {
        console.error("Error fetching features:", err);
        setError('فشل في تحميل قائمة الميزات');
      } finally {
        setLoading(false);
      }
    };

    fetchFeatures();
  }, []);

  const getIcon = (type) => {
    switch (type) {
      case 'OVERDRAFT_PROTECTION':
        return <AccountBalanceWalletIcon />;
      case 'INSURANCE':
        return <SecurityIcon />;
      case 'PREMIUM_SERVICES':
        return <StarIcon />;
      default:
        return <TrendingUpIcon />;
    }
  };

  const getColor = (type) => {
    switch (type) {
      case 'OVERDRAFT_PROTECTION':
        return '#2563EB'; // Blue
      case 'INSURANCE':
        return '#10B981'; // Green
      case 'PREMIUM_SERVICES':
        return '#F59E0B'; // Amber
      default:
        return '#7C3AED'; // Purple
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: '#1E293B', mb: 3 }}>
        الميزات الإضافية للنظام
      </Typography>
      
      <Typography variant="subtitle1" gutterBottom sx={{ color: '#64748B', mb: 4 }}>
        استعراض جميع الميزات والديكورات المتاحة التي يمكن إضافتها لحسابات العملاء.
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      <Grid container spacing={3}>
        {features.map((feature, index) => (
          <Grid item xs={12} md={4} key={index}>
            <Card 
              elevation={3} 
              sx={{ 
                height: '100%', 
                borderRadius: '16px',
                transition: 'transform 0.2s',
                '&:hover': {
                  transform: 'translateY(-5px)',
                  boxShadow: '0 12px 20px rgba(0,0,0,0.1)'
                }
              }}
            >
              <CardHeader
                avatar={
                  <Avatar sx={{ bgcolor: getColor(feature.type) }}>
                    {getIcon(feature.type)}
                  </Avatar>
                }
                title={
                  <Typography variant="h6" fontWeight="bold">
                    {feature.displayName}
                  </Typography>
                }
                subheader={feature.type}
              />
              <CardContent>
                <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.6 }}>
                  {feature.description}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
};

export default ManagerFeatures;
