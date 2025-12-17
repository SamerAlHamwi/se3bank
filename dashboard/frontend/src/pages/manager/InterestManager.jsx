import React, { useState, useEffect } from 'react';
import { Box, Typography, CircularProgress, Alert, Grid, Select, MenuItem, Button, FormControl, InputLabel } from '@mui/material';
import api from '../../services/api';

const InterestManager = ({ accountId, accountType }) => {
    const [report, setReport] = useState(null);
    const [strategies, setStrategies] = useState({});
    const [selectedStrategy, setSelectedStrategy] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const fetchReport = async () => {
        try {
            const reportResponse = await api.get(`/interest/accounts/${accountId}/report`);
            setReport(reportResponse.data);
            // Check if currentStrategy is a valid key in strategies, otherwise try to match by name or default
            setSelectedStrategy(reportResponse.data.currentStrategy);
        } catch (err) {
            setError('فشل في تحميل تقرير الفائدة');
        }
    };

    const fetchStrategies = async () => {
        try {
            const endpoint = accountType ? `/interest/strategies/${accountType}` : '/interest/strategies';
            const strategiesResponse = await api.get(endpoint);
            setStrategies(strategiesResponse.data);
        } catch (err) {
            setError('فشل في تحميل استراتيجيات الفائدة');
        }
    };

    useEffect(() => {
        setLoading(true);
        // Fetch strategies first, then report to ensure we can map strategy names correctly if needed
        const loadData = async () => {
            await fetchStrategies();
            await fetchReport();
            setLoading(false);
        };
        loadData();
    }, [accountId, accountType]);

    const handleStrategyChange = async () => {
        setLoading(true);
        setError('');
        setSuccess('');
        try {
            await api.post(`/interest/accounts/${accountId}/change-strategy`, { strategyName: selectedStrategy });
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
                            {Object.entries(strategies).map(([key, strategyObj]) => (
                                <MenuItem key={key} value={key}>
                                    {strategyObj.strategyName || strategyObj.name || key}
                                </MenuItem>
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
