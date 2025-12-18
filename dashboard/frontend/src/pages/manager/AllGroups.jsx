import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import { Link } from 'react-router-dom';
import { Button, Typography, Paper, List, ListItem, ListItemText, Divider, Box, CircularProgress } from '@mui/material';

const AllGroups = () => {
    const [groups, setGroups] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        api.get('/groups')
            .then(response => {
                setGroups(response.data);
                setLoading(false);
            })
            .catch(error => {
                console.error("Error fetching groups:", error);
                setLoading(false);
            });
    }, []);

    if (loading) {
        return <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}><CircularProgress /></Box>;
    }

    return (
        <Paper sx={{ p: 2 }}>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                <Typography variant="h4">كل المجموعات</Typography>
                <Button variant="contained" component={Link} to="/manager/create-group">
                    إنشاء مجموعة جديدة
                </Button>
            </Box>
            <List>
                {groups.map(group => (
                    <React.Fragment key={group.id}>
                        <ListItem button component={Link} to={`/manager/groups/${group.id}`}>
                            <ListItemText 
                                primary={group.groupName}
                                secondary={
                                    group.owner 
                                    ? `المالك: ${group.owner.firstName} ${group.owner.lastName}`
                                    : 'المالك: غير محدد'
                                }
                            />
                        </ListItem>
                        <Divider />
                    </React.Fragment>
                ))}
            </List>
        </Paper>
    );
};

export default AllGroups;
