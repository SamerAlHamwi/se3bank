import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import { useNavigate } from 'react-router-dom';
import { 
    TextField, Button, Select, MenuItem, FormControl, InputLabel, 
    Typography, Paper, Box, FormHelperText 
} from '@mui/material';

const CreateGroup = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        groupName: '',
        description: '',
        groupType: 'FAMILY',
        ownerId: '',
        maxAccounts: 5
    });
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        api.get('/users').then(response => {
            setUsers(response.data);
        });
    }, []);

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        api.post('/groups', formData)
            .then(() => {
                navigate('/manager/groups');
            })
            .catch(err => {
                console.error(err);
                setError('حدث خطأ أثناء إنشاء المجموعة. تأكد من البيانات المدخلة.');
                setLoading(false);
            });
    };

    return (
        <Box sx={{ p: 3, maxWidth: 600, mx: 'auto' }}>
            <Paper sx={{ p: 4 }}>
                <Typography variant="h5" gutterBottom>إنشاء مجموعة جديدة</Typography>
                
                {error && <Typography color="error" gutterBottom>{error}</Typography>}

                <form onSubmit={handleSubmit}>
                    <TextField
                        fullWidth
                        label="اسم المجموعة"
                        name="groupName"
                        value={formData.groupName}
                        onChange={handleChange}
                        required
                        margin="normal"
                    />
                    
                    <TextField
                        fullWidth
                        label="الوصف"
                        name="description"
                        value={formData.description}
                        onChange={handleChange}
                        multiline
                        rows={3}
                        margin="normal"
                    />

                    <FormControl fullWidth margin="normal">
                        <InputLabel>نوع المجموعة</InputLabel>
                        <Select
                            name="groupType"
                            value={formData.groupType}
                            onChange={handleChange}
                            label="نوع المجموعة"
                        >
                            <MenuItem value="FAMILY">عائلية</MenuItem>
                            <MenuItem value="BUSINESS">أعمال</MenuItem>
                            <MenuItem value="SAVINGS">ادخار مشترك</MenuItem>
                            <MenuItem value="PROJECT">مشروع</MenuItem>
                            <MenuItem value="OTHER">أخرى</MenuItem>
                        </Select>
                    </FormControl>

                    <FormControl fullWidth margin="normal" required>
                        <InputLabel>المالك</InputLabel>
                        <Select
                            name="ownerId"
                            value={formData.ownerId}
                            onChange={handleChange}
                            label="المالك"
                        >
                            {users.map(user => (
                                <MenuItem key={user.id} value={user.id}>
                                    {user.firstName} {user.lastName} ({user.username})
                                </MenuItem>
                            ))}
                        </Select>
                        <FormHelperText>اختر المستخدم المالك لهذه المجموعة</FormHelperText>
                    </FormControl>

                    <TextField
                        fullWidth
                        label="الحد الأقصى للحسابات"
                        name="maxAccounts"
                        type="number"
                        value={formData.maxAccounts}
                        onChange={handleChange}
                        margin="normal"
                    />

                    <Button 
                        type="submit" 
                        variant="contained" 
                        fullWidth 
                        sx={{ mt: 3 }}
                        disabled={loading}
                    >
                        {loading ? 'جاري الإنشاء...' : 'إنشاء المجموعة'}
                    </Button>
                </form>
            </Paper>
        </Box>
    );
};

export default CreateGroup;
