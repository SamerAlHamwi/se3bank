import React, { useState, useEffect } from 'react';
import { Box, Typography, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, CircularProgress, Alert, Chip, useTheme, useMediaQuery } from '@mui/material';
import api from '../../services/api';
import { Person } from '@mui/icons-material';

const AllUsers = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down('md'));

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const response = await api.get('/users');
                // Sorting should be handled by backend if possible, or here if list is small.
                // Assuming backend returns list. We can sort by ID desc or created date if available to show newest.
                // For users, usually just listing them is fine. If needed, sort by userId desc (newest first)
                const sortedUsers = response.data.sort((a, b) => b.userId - a.userId);
                setUsers(sortedUsers);
                setError('');
            } catch (err) {
                console.error(err);
                setError('فشل في جلب المستخدمين');
            } finally {
                setLoading(false);
            }
        };

        fetchUsers();
    }, []);

    if (loading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', minHeight: '50vh' }}>
                <CircularProgress />
            </Box>
        );
    }

    if (error) {
        return (
            <Box sx={{ p: 3 }}>
                <Alert severity="error">{error}</Alert>
            </Box>
        );
    }

    return (
        <Box sx={{ p: 0, width: '100%', overflowX: 'auto' }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3, pt: 1 }}>
                <Typography variant="h5" sx={{ display: 'flex', alignItems: 'center', gap: 1, fontWeight: 'bold' }}>
                    <Person /> كل المستخدمين
                </Typography>
            </Box>

            <Paper sx={{ width: '100%', overflow: 'hidden', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1)' }}>
                <TableContainer sx={{ maxHeight: '75vh' }}>
                    <Table stickyHeader aria-label="users table">
                        <TableHead>
                            <TableRow>
                                <TableCell sx={{ fontWeight: 'bold', bgcolor: '#f9fafb' }}>#</TableCell>
                                <TableCell sx={{ fontWeight: 'bold', bgcolor: '#f9fafb' }}>اسم المستخدم</TableCell>
                                <TableCell sx={{ fontWeight: 'bold', bgcolor: '#f9fafb' }}>الاسم الكامل</TableCell>
                                {!isMobile && <TableCell sx={{ fontWeight: 'bold', bgcolor: '#f9fafb' }}>البريد الإلكتروني</TableCell>}
                                <TableCell sx={{ fontWeight: 'bold', bgcolor: '#f9fafb' }}>الأدوار</TableCell>
                                <TableCell sx={{ fontWeight: 'bold', bgcolor: '#f9fafb' }}>الحالة</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {users.map((user) => (
                                <TableRow key={user.userId} hover>
                                    <TableCell>{user.userId}</TableCell>
                                    <TableCell sx={{ fontWeight: 500 }}>{user.username}</TableCell>
                                    <TableCell>{user.firstName} {user.lastName}</TableCell>
                                    {!isMobile && <TableCell>{user.email}</TableCell>}
                                    <TableCell>
                                        <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap' }}>
                                            {user.roles?.map((role) => (
                                                <Chip 
                                                    key={role} 
                                                    label={role.replace('ROLE_', '')} 
                                                    size="small" 
                                                    color="primary" 
                                                    variant="outlined"
                                                    sx={{ fontSize: '0.7rem' }}
                                                />
                                            ))}
                                        </Box>
                                    </TableCell>
                                    <TableCell>
                                        <Chip 
                                            label={user.isActive ? 'نشط' : 'غير نشط'} 
                                            color={user.isActive ? 'success' : 'error'} 
                                            size="small"
                                            sx={{ fontWeight: 'bold' }}
                                        />
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Paper>
        </Box>
    );
};

export default AllUsers;
