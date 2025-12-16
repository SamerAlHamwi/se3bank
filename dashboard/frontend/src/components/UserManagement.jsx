import React, { useState, useEffect } from 'react';
import {
  Container,
  Paper,
  Typography,
  Box,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Button,
  Chip,
  IconButton,
  TextField,
  InputAdornment,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  CircularProgress,
  Tooltip,
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import BlockIcon from '@mui/icons-material/Block';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import SecurityIcon from '@mui/icons-material/Security';
import userService from '../services/userService';

const UserManagement = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [roleDialogOpen, setRoleDialogOpen] = useState(false);
  const [selectedUserId, setSelectedUserId] = useState(null);
  const [selectedRole, setSelectedRole] = useState('');
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const response = await userService.getAllUsers();
      setUsers(response.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    if (!searchTerm) {
      fetchUsers();
      return;
    }
    try {
      setLoading(true);
      const response = await userService.searchUsers(searchTerm);
      setUsers(response.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleStatusChange = async (userId, currentStatus) => {
    try {
      await userService.setUserStatus(userId, !currentStatus);
      fetchUsers();
    } catch (err) {
      console.error(err);
    }
  };

  const handleOpenRoleDialog = (userId) => {
    setSelectedUserId(userId);
    setRoleDialogOpen(true);
  };

  const handleAddRole = async () => {
    if (!selectedRole) return;
    try {
      await userService.addRole(selectedUserId, selectedRole);
      setRoleDialogOpen(false);
      fetchUsers();
    } catch (err) {
      console.error(err);
    }
  };

  const filteredUsers = users; // Search is handled by API or can be client-side if preferred

  if (loading && users.length === 0) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="50vh">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Container maxWidth="xl" sx={{ mt: 4, mb: 4 }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" fontWeight="bold" color="primary">
          User Management
        </Typography>
        <Box display="flex" gap={2}>
           <TextField
              size="small"
              placeholder="Search users..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon />
                  </InputAdornment>
                ),
              }}
           />
           <Button variant="contained" onClick={handleSearch}>Search</Button>
        </Box>
      </Box>

      <TableContainer component={Paper} elevation={2}>
        <Table>
          <TableHead sx={{ bgcolor: 'primary.main' }}>
            <TableRow>
              <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>ID</TableCell>
              <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Full Name</TableCell>
              <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Username</TableCell>
              <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Email</TableCell>
              <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Roles</TableCell>
              <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Status</TableCell>
              <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {users.map((user) => (
              <TableRow key={user.id} hover>
                <TableCell>{user.id}</TableCell>
                <TableCell>{user.fullName}</TableCell>
                <TableCell>{user.username}</TableCell>
                <TableCell>{user.email}</TableCell>
                <TableCell>
                  {user.roles && user.roles.map((role) => (
                    <Chip 
                      key={role} 
                      label={role.replace('ROLE_', '')} 
                      size="small" 
                      color="secondary" 
                      variant="outlined" 
                      sx={{ mr: 0.5, mb: 0.5 }} 
                    />
                  ))}
                </TableCell>
                <TableCell>
                  <Chip 
                    label={user.active ? 'Active' : 'Inactive'} 
                    color={user.active ? 'success' : 'error'} 
                    size="small" 
                  />
                </TableCell>
                <TableCell>
                  <Tooltip title="Toggle Status">
                    <IconButton 
                      color={user.active ? 'error' : 'success'}
                      onClick={() => handleStatusChange(user.id, user.active)}
                    >
                      {user.active ? <BlockIcon /> : <CheckCircleIcon />}
                    </IconButton>
                  </Tooltip>
                  <Tooltip title="Manage Roles">
                    <IconButton 
                      color="primary"
                      onClick={() => handleOpenRoleDialog(user.id)}
                    >
                      <SecurityIcon />
                    </IconButton>
                  </Tooltip>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Role Dialog */}
      <Dialog open={roleDialogOpen} onClose={() => setRoleDialogOpen(false)}>
        <DialogTitle>Add Role to User</DialogTitle>
        <DialogContent sx={{ minWidth: 300, mt: 1 }}>
          <FormControl fullWidth>
            <InputLabel>Role</InputLabel>
            <Select
              value={selectedRole}
              label="Role"
              onChange={(e) => setSelectedRole(e.target.value)}
            >
              <MenuItem value="ROLE_ADMIN">Admin</MenuItem>
              <MenuItem value="ROLE_MANAGER">Manager</MenuItem>
              <MenuItem value="ROLE_TELLER">Teller</MenuItem>
              <MenuItem value="ROLE_CUSTOMER">Customer</MenuItem>
            </Select>
          </FormControl>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setRoleDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleAddRole} variant="contained">Add Role</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default UserManagement;
