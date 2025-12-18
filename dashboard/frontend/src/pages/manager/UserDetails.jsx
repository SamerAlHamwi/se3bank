import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../../services/api';
import { 
    Paper, Typography, Box, Grid, Chip, Divider, List, ListItem, 
    ListItemText, Button, Card, CardContent 
} from '@mui/material';
import { Group, Person } from '@mui/icons-material';

const UserDetails = () => {
    const { userId } = useParams();
    const [user, setUser] = useState(null);
    const [groups, setGroups] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const userRes = await api.get(`/users/${userId}`);
                setUser(userRes.data);
                
                // Fetch groups for this user
                // Endpoint defined in request: /api/groups/user/{userId}
                const groupsRes = await api.get(`/groups/user/${userId}`);
                setGroups(groupsRes.data);
            } catch (err) {
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [userId]);

    if (loading) return <Typography sx={{ p: 3 }}>جاري التحميل...</Typography>;
    if (!user) return <Typography sx={{ p: 3 }}>المستخدم غير موجود</Typography>;

    return (
        <Box sx={{ p: 3 }}>
            {/* User Info Section */}
            <Paper sx={{ p: 3, mb: 3 }}>
                <Box display="flex" alignItems="center" gap={1} mb={2}>
                    <Person color="primary" />
                    <Typography variant="h5">تفاصيل المستخدم</Typography>
                </Box>
                <Divider sx={{ mb: 2 }} />
                
                <Grid container spacing={3}>
                    <Grid item xs={12} md={6}>
                        <Typography variant="subtitle2" color="textSecondary">اسم المستخدم</Typography>
                        <Typography variant="body1">{user.username}</Typography>
                    </Grid>
                    <Grid item xs={12} md={6}>
                        <Typography variant="subtitle2" color="textSecondary">الاسم الكامل</Typography>
                        <Typography variant="body1">{user.firstName} {user.lastName}</Typography>
                    </Grid>
                    <Grid item xs={12} md={6}>
                        <Typography variant="subtitle2" color="textSecondary">البريد الإلكتروني</Typography>
                        <Typography variant="body1">{user.email}</Typography>
                    </Grid>
                    <Grid item xs={12} md={6}>
                        <Typography variant="subtitle2" color="textSecondary">الحالة</Typography>
                        <Chip 
                            label={user.isActive ? 'نشط' : 'غير نشط'} 
                            color={user.isActive ? 'success' : 'error'} 
                            size="small" 
                        />
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="subtitle2" color="textSecondary">الأدوار</Typography>
                        <Box sx={{ display: 'flex', gap: 1, mt: 0.5 }}>
                            {user.roles?.map(role => (
                                <Chip key={role} label={role.replace('ROLE_', '')} variant="outlined" />
                            ))}
                        </Box>
                    </Grid>
                </Grid>
            </Paper>

            {/* Groups Section */}
            <Paper sx={{ p: 3 }}>
                <Box display="flex" alignItems="center" gap={1} mb={2}>
                    <Group color="primary" />
                    <Typography variant="h5">مجموعات المستخدم</Typography>
                </Box>
                <Divider sx={{ mb: 2 }} />

                {groups.length === 0 ? (
                    <Typography color="textSecondary">لا ينتمي هذا المستخدم لأي مجموعة.</Typography>
                ) : (
                    <Grid container spacing={2}>
                        {groups.map(group => (
                            <Grid item xs={12} sm={6} md={4} key={group.id}>
                                <Card variant="outlined">
                                    <CardContent>
                                        <Typography variant="h6" gutterBottom>
                                            {group.groupName}
                                        </Typography>
                                        <Typography variant="body2" color="textSecondary" gutterBottom>
                                            {group.description}
                                        </Typography>
                                        <Typography variant="caption" display="block" sx={{ mt: 1 }}>
                                            النوع: {group.groupType}
                                        </Typography>
                                        <Typography variant="caption" display="block">
                                            رقم الحساب: {group.accountNumber}
                                        </Typography>
                                        <Button 
                                            component={Link} 
                                            to={`/manager/groups/${group.id}`} 
                                            size="small" 
                                            sx={{ mt: 2 }}
                                        >
                                            عرض التفاصيل
                                        </Button>
                                    </CardContent>
                                </Card>
                            </Grid>
                        ))}
                    </Grid>
                )}
            </Paper>
        </Box>
    );
};

export default UserDetails;
