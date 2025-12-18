import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import api from '../../services/api';
import { 
    Paper, Typography, List, ListItem, ListItemText, Divider, 
    Button, Box, Grid, Chip, Card, CardContent, FormControl, InputLabel, Select, MenuItem, FormHelperText 
} from '@mui/material';

const GroupDetailsPage = () => {
    const { groupId } = useParams();
    const [group, setGroup] = useState(null);
    const [accounts, setAccounts] = useState([]); // حسابات المجموعة
    const [allAccounts, setAllAccounts] = useState([]); // كل الحسابات المتاحة للإضافة
    const [newAccountId, setNewAccountId] = useState('');
    const [loading, setLoading] = useState(true);

    const fetchGroupData = () => {
        setLoading(true);
        // جلب تفاصيل المجموعة
        api.get(`/groups/${groupId}`)
            .then(res => {
                setGroup(res.data);
                // جلب حسابات المجموعة (Child Accounts)
                // في الاستجابة التي أرسلتها، يوجد childAccounts كمصفوفة فارغة،
                // ولكن هناك endpoint مخصص /groups/{groupId}/accounts
                // سأستخدم الـ endpoint المخصص لضمان الحصول على أحدث البيانات
                return api.get(`/groups/${groupId}/accounts`);
            })
            .then(res => {
                setAccounts(res.data);
                setLoading(false);
            })
            .catch(err => {
                console.error(err);
                setLoading(false);
            });
    };

    const fetchAllAccounts = () => {
        // جلب كل الحسابات لملء القائمة المنسدلة
        // نفترض أن هذا الـ endpoint موجود ويعيد قائمة الحسابات
        // إذا كان يحتاج صلاحيات مدير، فتأكد من أن المستخدم الحالي لديه الصلاحية
        api.get('/accounts') 
            .then(res => setAllAccounts(res.data))
            .catch(err => console.error("Error fetching all accounts:", err));
    };

    useEffect(() => {
        fetchGroupData();
        fetchAllAccounts();
    }, [groupId]);

    const handleAddAccount = () => {
        if (!newAccountId) return;
        api.post(`/groups/${groupId}/accounts/${newAccountId}`)
            .then(() => {
                setNewAccountId('');
                fetchGroupData(); // تحديث البيانات
            })
            .catch(err => console.error(err));
    };

    const handleRemoveAccount = (accountId) => {
        if (window.confirm('هل أنت متأكد من إزالة هذا الحساب من المجموعة؟')) {
            api.delete(`/groups/${groupId}/accounts/${accountId}`)
                .then(() => {
                    fetchGroupData();
                })
                .catch(err => console.error(err));
        }
    };

    if (loading || !group) return <Typography sx={{ p: 3 }}>جاري التحميل...</Typography>;

    // تصفية الحسابات التي يمكن إضافتها (ليست موجودة بالفعل في المجموعة)
    // ملاحظة: قد نحتاج لفلترة الحسابات التي تنتمي لمجموعات أخرى أيضاً، 
    // ولكن الباك إند سيمنع ذلك ويعيد خطأ. للعرض، سنستبعد الحسابات الموجودة في هذه المجموعة حالياً.
    const availableAccounts = allAccounts.filter(
        acc => !accounts.some(groupAcc => groupAcc.id === acc.id)
    );

    return (
        <Box sx={{ p: 3 }}>
            {/* تفاصيل المجموعة */}
            <Paper sx={{ p: 3, mb: 3 }}>
                <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                    <Typography variant="h4" color="primary">{group.groupName}</Typography>
                    <Chip 
                        label={group.status} 
                        color={group.status === 'ACTIVE' ? 'success' : 'error'} 
                    />
                </Box>
                
                <Typography variant="subtitle1" color="textSecondary" gutterBottom>
                    {group.description}
                </Typography>

                <Divider sx={{ my: 2 }} />

                <Grid container spacing={3}>
                    <Grid item xs={12} sm={6} md={3}>
                        <Typography variant="subtitle2" color="textSecondary">رقم المجموعة</Typography>
                        <Typography variant="body1" fontWeight="bold">{group.accountNumber}</Typography>
                    </Grid>
                    <Grid item xs={12} sm={6} md={3}>
                        <Typography variant="subtitle2" color="textSecondary">نوع المجموعة</Typography>
                        <Typography variant="body1">{group.groupType}</Typography>
                    </Grid>
                    <Grid item xs={12} sm={6} md={3}>
                        <Typography variant="subtitle2" color="textSecondary">المالك</Typography>
                        <Typography variant="body1">
                            {group.owner ? `${group.owner.firstName} ${group.owner.lastName}` : 'غير محدد'}
                        </Typography>
                    </Grid>
                    <Grid item xs={12} sm={6} md={3}>
                        <Typography variant="subtitle2" color="textSecondary">تاريخ الإنشاء</Typography>
                        <Typography variant="body1">
                            {new Date(group.createdAt).toLocaleDateString('ar-EG')}
                        </Typography>
                    </Grid>
                    
                    <Grid item xs={12} sm={6} md={3}>
                        <Typography variant="subtitle2" color="textSecondary">عدد الحسابات</Typography>
                        <Typography variant="body1">
                            {group.childCount} / {group.maxAccounts}
                        </Typography>
                    </Grid>
                    <Grid item xs={12} sm={6} md={3}>
                        <Typography variant="subtitle2" color="textSecondary">الرصيد الكلي</Typography>
                        <Typography variant="body1" color="success.main" fontWeight="bold">
                            {group.totalBalance?.toLocaleString()}
                        </Typography>
                    </Grid>
                     <Grid item xs={12} sm={6} md={3}>
                        <Typography variant="subtitle2" color="textSecondary">متوسط الرصيد</Typography>
                        <Typography variant="body1">
                            {group.averageBalance?.toLocaleString()}
                        </Typography>
                    </Grid>
                </Grid>
            </Paper>

            {/* إضافة حساب */}
            <Paper sx={{ p: 3, mb: 3 }}>
                <Typography variant="h6" gutterBottom>إضافة حساب للمجموعة</Typography>
                <Box sx={{ display: 'flex', gap: 2, alignItems: 'flex-start' }}>
                    <FormControl sx={{ minWidth: 300 }}>
                        <InputLabel id="select-account-label">اختر حساباً لإضافته</InputLabel>
                        <Select
                            labelId="select-account-label"
                            value={newAccountId}
                            label="اختر حساباً لإضافته"
                            onChange={(e) => setNewAccountId(e.target.value)}
                        >
                            <MenuItem value="">
                                <em>اختر حساباً...</em>
                            </MenuItem>
                            {availableAccounts.map((acc) => (
                                <MenuItem key={acc.id} value={acc.id}>
                                    {acc.accountNumber} - {acc.user?.firstName} {acc.user?.lastName} ({acc.accountType}) - {acc.balance}
                                </MenuItem>
                            ))}
                        </Select>
                        <FormHelperText>يمكنك فقط إضافة الحسابات التي لا تنتمي لمجموعة أخرى</FormHelperText>
                    </FormControl>
                    <Button 
                        variant="contained" 
                        onClick={handleAddAccount}
                        disabled={!newAccountId}
                        sx={{ mt: 1 }}
                    >
                        إضافة
                    </Button>
                </Box>
            </Paper>

            {/* قائمة الحسابات */}
            <Paper sx={{ p: 3 }}>
                <Typography variant="h5" gutterBottom>حسابات المجموعة</Typography>
                <List>
                    {accounts.length === 0 ? (
                        <Typography color="textSecondary" sx={{ py: 2 }}>
                            لا يوجد حسابات في هذه المجموعة حالياً.
                        </Typography>
                    ) : (
                        accounts.map(account => (
                            <React.Fragment key={account.id}>
                                <ListItem
                                    secondaryAction={
                                        <Button 
                                            variant="outlined" 
                                            color="error" 
                                            size="small"
                                            onClick={() => handleRemoveAccount(account.id)}
                                        >
                                            إزالة من المجموعة
                                        </Button>
                                    }
                                >
                                    <ListItemText 
                                        primary={
                                            <Typography variant="subtitle1" fontWeight="bold">
                                                {account.accountNumber}
                                            </Typography>
                                        }
                                        secondary={
                                            <React.Fragment>
                                                <Typography variant="body2" component="span" color="textPrimary">
                                                    الرصيد: {account.balance?.toLocaleString()}
                                                </Typography>
                                                <br />
                                                <Typography variant="body2" component="span">
                                                    النوع: {account.accountType} | الحالة: {account.status}
                                                </Typography>
                                                {account.user && (
                                                    <React.Fragment>
                                                        <br />
                                                        <Typography variant="caption">
                                                            المالك: {account.user.firstName} {account.user.lastName}
                                                        </Typography>
                                                    </React.Fragment>
                                                )}
                                            </React.Fragment>
                                        }
                                    />
                                </ListItem>
                                <Divider component="li" />
                            </React.Fragment>
                        ))
                    )}
                </List>
            </Paper>
        </Box>
    );
};

export default GroupDetailsPage;
