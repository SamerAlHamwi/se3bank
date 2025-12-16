import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Paper, Typography, Box, IconButton } from '@mui/material';
import { ArrowForwardIos } from '@mui/icons-material';

const AccountCard = ({ account }) => {
  const navigate = useNavigate();

  const handleAccountClick = () => {
    navigate(`/customer/account/${account.id}`);
  };

  const getAccountIcon = (type) => {
    // A simple function to return a representative icon/emoji for the account type
    switch(type) {
        case 'SAVINGS': return '🏦';
        case 'CHECKING': return '📄';
        case 'BUSINESS': return '💼';
        default: return '💰';
    }
  }

  return (
    <Paper 
      elevation={2} 
      onClick={handleAccountClick} 
      sx={{
        p: 3,
        borderRadius: '12px',
        cursor: 'pointer',
        transition: 'all 0.3s ease',
        border: '1px solid #E2E8F0',
        '&:hover': {
          transform: 'translateY(-4px)',
          boxShadow: '0 10px 20px rgba(0,0,0,0.1)',
          borderColor: '#4F46E5',
        }
      }}
    >
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Box>
            <Typography variant="h2" sx={{ mb: 1, fontSize: '2.5rem' }}>{getAccountIcon(account.accountType)}</Typography>
            <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#1E293B' }}>{account.accountType}</Typography>
            <Typography variant="body2" sx={{ color: '#64748B' }}>{account.accountNumber}</Typography>
        </Box>
        <Box sx={{ textAlign: 'right' }}>
            <Typography variant="body2" sx={{ color: '#64748B', mb: 1 }}>الرصيد المتاح</Typography>
            <Typography variant="h5" sx={{ fontWeight: 'bold', color: '#10B981' }}>
            ${account.availableBalance.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
            </Typography>
        </Box>
      </Box>
    </Paper>
  );
};

export default AccountCard;
