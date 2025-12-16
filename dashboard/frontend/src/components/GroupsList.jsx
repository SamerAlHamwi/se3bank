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
  Chip,
  Alert,
  CircularProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Tooltip,
} from '@mui/material';
import {
  Add,
  Edit,
  Delete,
  Info,
  AccountBalance,
  TrendingUp,
} from '@mui/icons-material';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';

const API_BASE_URL = 'http://localhost:9090/api';

export default function GroupsList() {
  const navigate = useNavigate();
  const { userId } = useParams();
  
  const [groups, setGroups] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [selectedGroupId, setSelectedGroupId] = useState(null);

  const token = localStorage.getItem('token');
  // Removed currentUser parsing as we get userId from params

  useEffect(() => {
    if (userId && userId !== 'undefined') {
      fetchGroups();
    }
  }, [userId]);

  const fetchGroups = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await axios.get(
        `${API_BASE_URL}/groups/user/${userId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setGroups(response.data);
    } catch (err) {
      console.error('خطأ في تحميل المجموعات:', err);
      setError('فشل في تحميل المجموعات');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    try {
      await axios.delete(`${API_BASE_URL}/groups/${selectedGroupId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setGroups(groups.filter(g => g.id !== selectedGroupId));
      setDeleteDialogOpen(false);
      setSelectedGroupId(null);
    } catch (err) {
      console.error('خطأ في حذف المجموعة:', err);
      setError('فشل في حذف المجموعة');
    }
  };

  const getGroupTypeText = (type) => {
    const types = {
      FAMILY: 'عائلية',
      BUSINESS: 'عملية',
      JOINT: 'مشتركة',
    };
    return types[type] || type;
  };

  const getGroupTypeColor = (type) => {
    const colors = {
      FAMILY: 'primary',
      BUSINESS: 'success',
      JOINT: 'warning',
    };
    return colors[type] || 'default';
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress size={50} sx={{ color: '#2563EB' }} />
      </Box>
    );
  }

  return (
    <Container maxWidth="lg">
      <Box sx={{ mb: 4 }}>
        <Box display="flex" alignItems="center" justifyContent="space-between" mb={3}>
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
            مجموعات الحسابات
          </Typography>
          <Button
            variant="contained"
            startIcon={<Add />}
            onClick={() => navigate('/dashboard/groups/create')}
            sx={{
              background: 'linear-gradient(135deg, #2563EB 0%, #7C3AED 100%)',
              textTransform: 'none',
              fontWeight: 700,
              borderRadius: 2,
              py: 1.2,
              px: 3,
            }}
          >
            إنشاء مجموعة جديدة
          </Button>
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

        {groups.length === 0 ? (
          <Card
            sx={{
              borderRadius: 3,
              boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)',
              border: '1px solid rgba(0, 0, 0, 0.05)',
              textAlign: 'center',
              py: 8,
            }}
          >
            <AccountBalance sx={{ fontSize: 60, color: '#D1D5DB', mb: 2 }} />
            <Typography variant="h6" sx={{ color: '#555', fontWeight: 600 }}>
              لا توجد مجموعات حسابات
            </Typography>
            <Typography variant="body2" sx={{ color: '#999', mt: 1 }}>
              ابدأ بإنشاء مجموعة جديدة لتنظيم حساباتك
            </Typography>
          </Card>
        ) : (
          <Grid container spacing={3}>
            {groups.map((group) => (
              <Grid item xs={12} sm={6} md={4} key={group.id}>
                <Card
                  sx={{
                    borderRadius: 3,
                    boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)',
                    border: '1px solid rgba(0, 0, 0, 0.05)',
                    height: '100%',
                    transition: 'all 0.3s ease',
                    '&:hover': {
                      transform: 'translateY(-8px)',
                      boxShadow: '0 12px 24px rgba(37, 99, 235, 0.15)',
                    },
                  }}
                >
                  <CardContent>
                    <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
                      <Typography
                        variant="h6"
                        component="h3"
                        sx={{
                          fontWeight: 700,
                          color: '#333',
                          flex: 1,
                        }}
                      >
                        {group.groupName}
                      </Typography>
                      <Chip
                        label={getGroupTypeText(group.groupType)}
                        color={getGroupTypeColor(group.groupType)}
                        size="small"
                        sx={{ fontWeight: 700, fontSize: '0.8rem' }}
                      />
                    </Box>

                    {group.description && (
                      <Typography
                        variant="body2"
                        sx={{
                          color: '#666',
                          mb: 2,
                          overflow: 'hidden',
                          textOverflow: 'ellipsis',
                          display: '-webkit-box',
                          WebkitLineClamp: 2,
                          WebkitBoxOrient: 'vertical',
                        }}
                      >
                        {group.description}
                      </Typography>
                    )}

                    <Box
                      sx={{
                        p: 1.5,
                        backgroundColor: '#F8FAFC',
                        borderRadius: 2,
                        mb: 2,
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                      }}
                    >
                      <Box>
                        <Typography
                          variant="caption"
                          sx={{
                            color: '#999',
                            fontWeight: 600,
                            fontSize: '0.75rem',
                          }}
                        >
                          عدد الحسابات
                        </Typography>
                        <Typography
                          variant="h6"
                          sx={{
                            fontWeight: 700,
                            color: '#2563EB',
                            mt: 0.5,
                          }}
                        >
                          {group.childCount || 0}
                        </Typography>
                      </Box>
                      <Box sx={{ textAlign: 'right' }}>
                        <Typography
                          variant="caption"
                          sx={{
                            color: '#999',
                            fontWeight: 600,
                            fontSize: '0.75rem',
                          }}
                        >
                          الرصيد الإجمالي
                        </Typography>
                        <Typography
                          variant="h6"
                          sx={{
                            fontWeight: 700,
                            color: '#10B981',
                            mt: 0.5,
                          }}
                        >
                          ${(group.totalBalance || 0).toFixed(2)}
                        </Typography>
                      </Box>
                    </Box>

                    <Box display="flex" gap={1} justifyContent="flex-end">
                      <Tooltip title="عرض التفاصيل">
                        <IconButton
                          size="small"
                          onClick={() => navigate(`/dashboard/groups/${group.id}`)}
                          sx={{
                            color: '#2563EB',
                            backgroundColor: '#EFF6FF',
                            transition: 'all 0.3s ease',
                            '&:hover': {
                              backgroundColor: '#DBEAFE',
                            },
                          }}
                        >
                          <Info fontSize="small" />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="تحويل">
                        <IconButton
                          size="small"
                          onClick={() => navigate(`/dashboard/groups/${group.id}/transfer`)}
                          sx={{
                            color: '#7C3AED',
                            backgroundColor: '#F3E8FF',
                            transition: 'all 0.3s ease',
                            '&:hover': {
                              backgroundColor: '#E9D5FF',
                            },
                          }}
                        >
                          <TrendingUp fontSize="small" />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="حذف">
                        <IconButton
                          size="small"
                          onClick={() => {
                            setSelectedGroupId(group.id);
                            setDeleteDialogOpen(true);
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
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        )}
      </Box>

      {/* Delete Dialog */}
      <Dialog
        open={deleteDialogOpen}
        onClose={() => setDeleteDialogOpen(false)}
        PaperProps={{
          sx: {
            borderRadius: 3,
            boxShadow: '0 20px 60px rgba(0, 0, 0, 0.3)',
          },
        }}
      >
        <DialogTitle sx={{ fontWeight: 700, color: '#333' }}>
          حذف المجموعة
        </DialogTitle>
        <DialogContent>
          <Typography sx={{ color: '#555', fontWeight: 500 }}>
            هل أنت متأكد من رغبتك في حذف هذه المجموعة؟ سيتم فقط حذف المجموعة وليس الحسابات التي تحتويها.
          </Typography>
        </DialogContent>
        <DialogActions sx={{ p: 2, gap: 1 }}>
          <Button
            onClick={() => setDeleteDialogOpen(false)}
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
            onClick={handleDelete}
            variant="contained"
            sx={{
              py: 1,
              px: 2.5,
              borderRadius: 1.5,
              background: 'linear-gradient(135deg, #EF4444 0%, #DC2626 100%)',
              textTransform: 'none',
              fontWeight: 700,
            }}
          >
            حذف
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
}
