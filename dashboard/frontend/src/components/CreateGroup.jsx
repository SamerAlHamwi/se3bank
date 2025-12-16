import React, { useState } from 'react';
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
} from '@mui/material';
import { ArrowBack, Add } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

export default function CreateGroup() {
  const navigate = useNavigate();
  
  const [formData, setFormData] = useState({
    groupName: '',
    description: '',
    groupType: 'FAMILY',
    maxAccounts: 10,
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const currentUser = JSON.parse(localStorage.getItem('user') || '{}');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: name === 'maxAccounts' ? parseInt(value) : value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);

    try {
      await api.post('/groups', {
        ...formData,
        ownerId: currentUser.userId,
      });

      setSuccess('تم إنشاء المجموعة بنجاح');
      setTimeout(() => {
        navigate(`/dashboard/groups/user/${currentUser.userId}`);
      }, 1500);
    } catch (err) {
      console.error('خطأ في إنشاء المجموعة:', err);
      setError(
        err.response?.data?.message ||
        'فشل في إنشاء المجموعة. تحقق من البيانات المدخلة.'
      );
    } finally {
      setLoading(false);
    }
  };

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
            إنشاء مجموعة حسابات جديدة
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

            {success && (
              <Alert
                severity="success"
                sx={{
                  mb: 3,
                  borderRadius: 2,
                  boxShadow: '0 4px 12px rgba(16, 185, 129, 0.2)',
                }}
              >
                {success}
              </Alert>
            )}

            <form onSubmit={handleSubmit}>
              <TextField
                fullWidth
                label="اسم المجموعة"
                name="groupName"
                value={formData.groupName}
                onChange={handleChange}
                required
                margin="normal"
                placeholder="مثال: مجموعة العائلة"
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
                fullWidth
                label="الوصف"
                name="description"
                value={formData.description}
                onChange={handleChange}
                margin="normal"
                placeholder="أضف وصفاً للمجموعة (اختياري)"
                multiline
                rows={3}
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

              <FormControl
                fullWidth
                margin="normal"
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
                <InputLabel>نوع المجموعة</InputLabel>
                <Select
                  name="groupType"
                  value={formData.groupType}
                  onChange={handleChange}
                  label="نوع المجموعة"
                >
                  <MenuItem value="FAMILY">عائلية</MenuItem>
                  <MenuItem value="BUSINESS">عملية</MenuItem>
                  <MenuItem value="JOINT">مشتركة</MenuItem>
                </Select>
              </FormControl>

              <TextField
                fullWidth
                label="الحد الأقصى للحسابات"
                name="maxAccounts"
                type="number"
                value={formData.maxAccounts}
                onChange={handleChange}
                margin="normal"
                inputProps={{ min: 1, max: 100 }}
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
                  startIcon={loading ? <CircularProgress size={20} color="inherit" /> : <Add />}
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
                  {loading ? 'جاري الإنشاء...' : 'إنشاء المجموعة'}
                </Button>
              </Box>
            </form>
          </CardContent>
        </Card>
      </Box>
    </Container>
  );
}
