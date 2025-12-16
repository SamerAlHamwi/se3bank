import React, { useState, useEffect } from 'react';
import { Box, Typography, CircularProgress, Alert, Grid, Select, MenuItem, Button, FormControl, InputLabel } from '@mui/material';
import api from '../../services/api';

const InterestManager = ({ accountId }) => {
    const [report, setReport] = useState(null);
    const [strategies, setStrategies] = useState({});
    const [selectedStrategy, setSelectedStrategy] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const fetchReport = async () => {
        try {
            const reportResponse = await api.get(`/api/interest/report/${accountId}`);
            setReport(reportResponse.data);
            setSelectedStrategy(reportResponse.data.currentStrategy);
        } catch (err) {
            setError('فشل في تحميل تقرير الفائدة');
        }
    };

    const fetchStrategies = async () => {
        try {
            const strategiesResponse = await api.get('/api/interest/strategies');
            setStrategies(strategiesResponse.data);
        } catch (err) {
            setError('فشل في تحميل استراتيجيات الفائدة');
        }
    };

    useEffect(() => {
        setLoading(true);
        Promise.all([fetchReport(), fetchStrategies()]).finally(() => setLoading(false));
    }, [accountId]);

    const handleStrategyChange = async () => {
        setLoading(true);
        setError('');
        setSuccess('');
        try {
            await api.post(`/api/interest/strategy/${accountId}`, { strategyName: selectedStrategy });
            setSuccess('تم تغيير استراتيجية الفائدة بنجاح!');
            fetchReport(); // Refresh report to show updated strategy
        } catch (err) {
            setError('فشل في تغيير الاستراتيجية');
        } finally {
            setLoading(false);
        }
    };

    if (loading && !report) {
        return <Box sx={{ display: 'flex', justifyContent: 'center', my: 3 }}><CircularProgress /></Box>;
    }

    if (error) {
        return <Alert severity="error">{error}</Alert>;
    }

    if (!report) {
        return <Alert severity="info">لا توجد بيانات لعرضها.</Alert>;
    }

    return (
        <Box>
            {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}
            <Grid container spacing={2}>
                <Grid item xs={6}><Typography>الرصيد الحالي:</Typography></Grid>
                <Grid item xs={6}><Typography fontWeight="bold">${report.currentBalance.toLocaleString()}</Typography></Grid>
                
                <Grid item xs={6}><Typography>الفائدة الشهرية المتوقعة:</Typography></Grid>
                <Grid item xs={6}><Typography fontWeight="bold" color="success.main">+${report.monthlyInterest.toFixed(2)}</Typography></Grid>

                <Grid item xs={6}><Typography>المعدل السنوي الفعلي:</Typography></Grid>
                <Grid item xs={6}><Typography fontWeight="bold">{report.effectiveAnnualRate.toFixed(2)}%</Typography></Grid>

                <Grid item xs={12}><hr style={{border: 'none', borderTop: '1px solid #eee'}} /></Grid>

                <Grid item xs={12}>
                    <FormControl fullWidth margin="normal">
                        <InputLabel>استراتيجية الفائدة الحالية</InputLabel>
                        <Select
                            value={selectedStrategy}
                            onChange={(e) => setSelectedStrategy(e.target.value)}
                            label="استراتيجية الفائدة الحالية"
                        >
                            {Object.entries(strategies).map(([key, name]) => (
                                <MenuItem key={key} value={key}>{name}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </Grid>
                <Grid item xs={12}>
                    <Button onClick={handleStrategyChange} variant="contained" disabled={loading} fullWidth>
                        {loading ? <CircularProgress size={24} /> : 'تغيير الاستراتيجية'}
                    </Button>
                </Grid>
            </Grid>
        </Box>
    );
};

export default InterestManager;
