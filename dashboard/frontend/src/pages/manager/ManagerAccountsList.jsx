import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Typography, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Button, Chip, Alert, CircularProgress, Tooltip, IconButton, Menu, MenuItem, Dialog, DialogTitle, DialogContent, DialogActions, Divider } from '@mui/material';
import { Add as AddIcon, Edit as EditIcon, Delete as DeleteIcon, MoreVert as MoreVertIcon, Analytics as AnalyticsIcon, Block as BlockIcon, CheckCircle as CheckCircleIcon, MonetizationOn } from '@mui/icons-material';
import api from '../../services/api';
import InterestManager from './InterestManager'; // Import the new component

const ManagerAccountsList = () => {
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [anchorEl, setAnchorEl] = useState(null);
  const [selectedAccount, setSelectedAccount] = useState(null);
  const [interestModalOpen, setInterestModalOpen] = useState(false);
  const navigate = useNavigate();

  const fetchAccounts = async () => {
    setLoading(true);
    try {
      const response = await api.get('/accounts');
      setAccounts(response.data);
    } catch (err) {
      setError('فشل في تحميل الحسابات');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAccounts();
  }, []);

  const handleMenuClick = (event, account) => {
    setAnchorEl(event.currentTarget);
    setSelectedAccount(account);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
    // Don't clear selected account here, needed for modals
  };

  const handleUpdateStatus = async (status) => {
    try {
      await api.patch(`/accounts/${selectedAccount.id}/status?status=${status}`);
      handleMenuClose();
      fetchAccounts(); // Refresh the list
    } catch (err) {
      setError(`فشل في تحديث حالة الحساب`);
    }
  };

  const handleEdit = () => {
    navigate(`/manager/edit-account/${selectedAccount.id}`);
    handleMenuClose();
  }

  const handleOpenInterestManager = () => {
    setInterestModalOpen(true);
    handleMenuClose();
  };

  const handleCloseInterestManager = () => {
    setInterestModalOpen(false);
    setSelectedAccount(null);
  };

  const getStatusChip = (status) => {
    const statusMap = {
      ACTIVE: { label: 'نشط', color: 'success' },
      FROZEN: { label: 'مجمد', color: 'warning' },
      CLOSED: { label: 'مغلق', color: 'error' },
    };
    const { label, color } = statusMap[status] || { label: status, color: 'default' };
    return <Chip label={label} color={color} size="small" sx={{ fontWeight: 'bold'}} />;
  };

  if (loading) {
    return <Box sx={{ display: 'flex', justifyContent: 'center' }}><CircularProgress /></Box>;
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: '#1E293B', mb: 3 }}>
        إدارة جميع الحسابات
      </Typography>
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Paper sx={{ borderRadius: 3, overflow: 'hidden' }}>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow sx={{ bgcolor: '#F1F5F9' }}>
                <TableCell>رقم الحساب</TableCell>
                <TableCell>نوع الحساب</TableCell>
                <TableCell>الحالة</TableCell>
                <TableCell align="right">الرصيد</TableCell>
                <TableCell>رقم المستخدم</TableCell>
                <TableCell align="center">إجراءات</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {accounts.map((account) => (
                <TableRow key={account.id} hover>
                  <TableCell sx={{ fontWeight: 'bold' }}>{account.accountNumber}</TableCell>
                  <TableCell>{account.accountType}</TableCell>
                  <TableCell>{getStatusChip(account.status)}</TableCell>
                  <TableCell align="right" sx={{ fontWeight: 'bold', color: '#10B981' }}>${account.balance.toLocaleString()}</TableCell>
                  <TableCell>{account.userId}</TableCell>
                  <TableCell align="center">
                    <Tooltip title="خيارات إضافية">
                      <IconButton onClick={(e) => handleMenuClick(e, account)}>
                        <MoreVertIcon />
                      </IconButton>
                    </Tooltip>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Paper>

      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleMenuClose}
      >
        <MenuItem onClick={handleEdit}><EditIcon sx={{ mr: 1 }} /> تعديل الحساب</MenuItem>
        <MenuItem onClick={handleOpenInterestManager}>
            <MonetizationOn sx={{ mr: 1 }} /> إدارة الفائدة
        </MenuItem>
        <Divider />
        <MenuItem onClick={() => handleUpdateStatus('ACTIVE')}><CheckCircleIcon sx={{ mr: 1 }} color="success"/> تفعيل</MenuItem>
        <MenuItem onClick={() => handleUpdateStatus('FROZEN')}><BlockIcon sx={{ mr: 1 }} color="warning"/> تجميد</MenuItem>
        <MenuItem onClick={() => handleUpdateStatus('CLOSED')}><DeleteIcon sx={{ mr: 1 }} color="error"/> إغلاق</MenuItem>
      </Menu>

      {/* Interest Management Dialog */}
      <Dialog open={interestModalOpen} onClose={handleCloseInterestManager} fullWidth maxWidth="sm">
        <DialogTitle>إدارة الفائدة للحساب: {selectedAccount?.accountNumber}</DialogTitle>
        <DialogContent>
          {selectedAccount && <InterestManager accountId={selectedAccount.id} />}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseInterestManager}>إغلاق</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default ManagerAccountsList;
