import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { 
  Box, Typography, Paper, TextField, Button, CircularProgress, 
  Alert, MenuItem, FormControl, InputLabel, Select,
  List, ListItem, ListItemText, ListItemSecondaryAction, IconButton,
  Dialog, DialogTitle, DialogContent, DialogActions,
  Divider, Chip, Grid
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import AddCircleIcon from '@mui/icons-material/AddCircle';
import StarIcon from '@mui/icons-material/Star';
import api from '../../services/api';

const EditAccount = () => {
  const { accountId } = useParams();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    status: '',
    interestRate: '',
    overdraftLimit: '',
    minimumBalance: '',
    balance: ''
  });
  const [decorators, setDecorators] = useState([]);
  const [availableTypes, setAvailableTypes] = useState([]);
  const [accountFeatures, setAccountFeatures] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  
  // Dialog state for adding decorator
  const [openDialog, setOpenDialog] = useState(false);
  const [decoratorForm, setDecoratorForm] = useState({
    decoratorType: '',
    overdraftLimit: '',
    coverageAmount: '',
    insuranceType: '',
    tierLevel: '',
    description: ''
  });

  useEffect(() => {
    fetchAccountData();
    fetchDecoratorInfo();
  }, [accountId]);

  const fetchAccountData = async () => {
    try {
      const [accountRes, decoratorsRes, featuresRes] = await Promise.all([
        api.get(`/accounts/${accountId}`),
        api.get(`/decorators/account/${accountId}/active`),
        api.get(`/decorators/account/${accountId}/features`)
      ]);
      
      const { status, interestRate, overdraftLimit, minimumBalance, balance } = accountRes.data;
      setFormData({ status, interestRate, overdraftLimit, minimumBalance, balance });
      setDecorators(decoratorsRes.data);
      setAccountFeatures(featuresRes.data);
    } catch (err) {
      setError('فشل في تحميل بيانات الحساب');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchDecoratorInfo = async () => {
    try {
      const res = await api.get('/decorators/info');
      if (Array.isArray(res.data)) {
        setAvailableTypes(res.data);
      } else {
        // Fallback if the API returns string or old format (for compatibility)
        // This is just a safeguard
        setAvailableTypes([
          { type: 'OVERDRAFT_PROTECTION', displayName: 'حماية السحب على المكشوف' },
          { type: 'INSURANCE', displayName: 'تأمين' },
          { type: 'PREMIUM_SERVICES', displayName: 'خدمات مميزة' }
        ]);
      }
    } catch (err) {
      console.error("Failed to fetch decorator info", err);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const requestData = {
        ...formData,
        interestRate: formData.interestRate ? parseFloat(formData.interestRate) : null,
        overdraftLimit: formData.overdraftLimit ? parseFloat(formData.overdraftLimit) : null,
        minimumBalance: formData.minimumBalance ? parseFloat(formData.minimumBalance) : null,
        balance: formData.balance ? parseFloat(formData.balance) : null,
      }
      await api.put(`/accounts/${accountId}`, requestData);
      setSuccess('تم تحديث الحساب بنجاح!');
      // Don't navigate away immediately so they can manage decorators
      setTimeout(() => setSuccess(''), 3000);
      setLoading(false);
    } catch (err) {
      setError(err.response?.data?.message || 'فشل في تحديث الحساب');
      setLoading(false);
    }
  };

  const handleRemoveDecorator = async (decoratorId) => {
    if(!window.confirm('هل أنت متأكد من إزالة هذه الميزة؟')) return;
    
    try {
      await api.delete(`/decorators/${decoratorId}`);
      fetchAccountData(); // Refresh list
    } catch (err) {
      setError('فشل في إزالة الديكور');
    }
  };

  const handleAddDecorator = async () => {
    try {
      const requestData = {
        accountId: parseInt(accountId),
        decoratorType: decoratorForm.decoratorType,
        description: decoratorForm.description,
        ...(decoratorForm.decoratorType === 'OVERDRAFT_PROTECTION' && {
          overdraftLimit: parseFloat(decoratorForm.overdraftLimit)
        }),
        ...(decoratorForm.decoratorType === 'INSURANCE' && {
          coverageAmount: parseFloat(decoratorForm.coverageAmount),
          insuranceType: decoratorForm.insuranceType
        }),
        ...(decoratorForm.decoratorType === 'PREMIUM_SERVICES' && {
          tierLevel: decoratorForm.tierLevel
        })
      };

      await api.post('/decorators', requestData);
      setOpenDialog(false);
      setDecoratorForm({
        decoratorType: '',
        overdraftLimit: '',
        coverageAmount: '',
        insuranceType: '',
        tierLevel: '',
        description: ''
      });
      fetchAccountData();
    } catch (err) {
      alert(err.response?.data?.message || 'فشل في إضافة الديكور');
    }
  };

  if (loading && !formData.status) {
    return <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}><CircularProgress /></Box>;
  }

  return (
    <Box sx={{ pb: 4 }}>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: '#1E293B', mb: 3 }}>
        إدارة الحساب والميزات
      </Typography>

      <Grid container spacing={3}>
        {/* Account Details Form */}
        <Grid item xs={12} md={6}>
          <Paper elevation={3} sx={{ p: 4, borderRadius: '16px' }}>
            <Typography variant="h6" gutterBottom sx={{ mb: 2 }}>تفاصيل الحساب الأساسية</Typography>
            
            {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
            {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}
            
            <Box component="form" onSubmit={handleSubmit}>
                <FormControl fullWidth margin="normal">
                    <InputLabel>الحالة</InputLabel>
                    <Select name="status" value={formData.status || ''} onChange={handleChange} label="الحالة">
                        <MenuItem value="ACTIVE">نشط</MenuItem>
                        <MenuItem value="FROZEN">مجمد</MenuItem>
                        <MenuItem value="CLOSED">مغلق</MenuItem>
                    </Select>
                </FormControl>
                <TextField name="balance" label="الرصيد" value={formData.balance || ''} onChange={handleChange} type="number" fullWidth margin="normal" />
                <TextField name="interestRate" label="معدل الفائدة (%)" value={formData.interestRate || ''} onChange={handleChange} type="number" fullWidth margin="normal" />
                <TextField name="overdraftLimit" label="حد السحب على المكشوف (الأساسي)" value={formData.overdraftLimit || ''} onChange={handleChange} type="number" fullWidth margin="normal" />
                <TextField name="minimumBalance" label="الحد الأدنى للرصيد" value={formData.minimumBalance || ''} onChange={handleChange} type="number" fullWidth margin="normal" />
              
                <Button type="submit" variant="contained" disabled={loading} fullWidth sx={{ mt: 3, py: 1.5 }}>
                    {loading ? <CircularProgress size={24} color="inherit" /> : 'حفظ التعديلات الأساسية'}
                </Button>
            </Box>
          </Paper>
        </Grid>

        {/* Decorators Section */}
        <Grid item xs={12} md={6}>
          <Paper elevation={3} sx={{ p: 4, borderRadius: '16px', height: '100%' }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
              <Typography variant="h6">الميزات الإضافية (Decorators)</Typography>
              <Button 
                variant="outlined" 
                startIcon={<AddCircleIcon />}
                onClick={() => setOpenDialog(true)}
              >
                إضافة ميزة
              </Button>
            </Box>
            
            <Box sx={{ mb: 2 }}>
               <Typography variant="subtitle2" color="textSecondary">الميزات النشطة حالياً:</Typography>
               <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mt: 1 }}>
                 {accountFeatures.map((feature, idx) => (
                   <Chip key={idx} label={feature} size="small" color="primary" variant="outlined" icon={<StarIcon />} />
                 ))}
               </Box>
            </Box>

            <Divider sx={{ my: 2 }} />

            <List>
              {decorators.length === 0 ? (
                <Typography color="textSecondary" align="center">لا توجد ميزات إضافية مفعلة</Typography>
              ) : (
                decorators.map((decorator) => (
                  <ListItem key={decorator.id} divider>
                    <ListItemText 
                      primary={decorator.decoratorName} 
                      secondary={
                        <>
                          <Typography variant="caption" display="block">
                             {decorator.description || 'لا يوجد وصف'}
                          </Typography>
                          <Typography variant="caption" color="primary">
                            الرسوم الشهرية: {decorator.monthlyFee}
                          </Typography>
                        </>
                      } 
                    />
                    <ListItemSecondaryAction>
                      <IconButton edge="end" aria-label="delete" onClick={() => handleRemoveDecorator(decorator.id)} color="error">
                        <DeleteIcon />
                      </IconButton>
                    </ListItemSecondaryAction>
                  </ListItem>
                ))
              )}
            </List>
          </Paper>
        </Grid>
      </Grid>

      {/* Add Decorator Dialog */}
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>إضافة ميزة جديدة</DialogTitle>
        <DialogContent>
          <FormControl fullWidth margin="normal">
            <InputLabel>نوع الميزة</InputLabel>
            <Select 
              value={decoratorForm.decoratorType} 
              onChange={(e) => setDecoratorForm({...decoratorForm, decoratorType: e.target.value})}
              label="نوع الميزة"
            >
              {availableTypes.map((dt) => (
                <MenuItem key={dt.type} value={dt.type}>
                  {dt.displayName}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          {decoratorForm.decoratorType === 'OVERDRAFT_PROTECTION' && (
             <TextField
               fullWidth margin="normal"
               label="حد السحب الإضافي"
               type="number"
               value={decoratorForm.overdraftLimit}
               onChange={(e) => setDecoratorForm({...decoratorForm, overdraftLimit: e.target.value})}
             />
          )}

          {decoratorForm.decoratorType === 'INSURANCE' && (
            <>
               <TextField
                 fullWidth margin="normal"
                 label="مبلغ التغطية"
                 type="number"
                 value={decoratorForm.coverageAmount}
                 onChange={(e) => setDecoratorForm({...decoratorForm, coverageAmount: e.target.value})}
               />
               <FormControl fullWidth margin="normal">
                  <InputLabel>نوع التأمين</InputLabel>
                  <Select
                    value={decoratorForm.insuranceType}
                    onChange={(e) => setDecoratorForm({...decoratorForm, insuranceType: e.target.value})}
                    label="نوع التأمين"
                  >
                    <MenuItem value="THEFT">سرقة</MenuItem>
                    <MenuItem value="FRAUD">احتيال</MenuItem>
                    <MenuItem value="LOSS">فقدان</MenuItem>
                  </Select>
               </FormControl>
            </>
          )}

          {decoratorForm.decoratorType === 'PREMIUM_SERVICES' && (
             <FormControl fullWidth margin="normal">
                <InputLabel>المستوى</InputLabel>
                <Select
                  value={decoratorForm.tierLevel}
                  onChange={(e) => setDecoratorForm({...decoratorForm, tierLevel: e.target.value})}
                  label="المستوى"
                >
                  <MenuItem value="GOLD">ذهبي</MenuItem>
                  <MenuItem value="PLATINUM">بلاتيني</MenuItem>
                  <MenuItem value="DIAMOND">ماسي</MenuItem>
                </Select>
             </FormControl>
          )}

          <TextField
             fullWidth margin="normal"
             label="وصف (اختياري)"
             value={decoratorForm.description}
             onChange={(e) => setDecoratorForm({...decoratorForm, description: e.target.value})}
             multiline rows={2}
           />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>إلغاء</Button>
          <Button onClick={handleAddDecorator} variant="contained">إضافة</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default EditAccount;
