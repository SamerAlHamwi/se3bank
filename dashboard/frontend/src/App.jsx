import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import Login from './components/Login';
import Register from './components/Register';

// Admin/Shared Components
import Home from './components/Home';
import Dashboard from './components/Dashboard';
import CreateAccount from './components/CreateAccount';

// Customer Pages & Components
import CustomerLayout from './pages/customer/CustomerLayout'; 
import CustomerDashboard from './pages/customer/CustomerDashboard';
import CustomerAccountDetails from './pages/customer/AccountDetails';
import InternalTransfer from './pages/customer/InternalTransfer';
import ExternalTransfer from './pages/customer/ExternalTransfer';
import CustomerMyTransfers from './pages/customer/MyTransfers';
import CustomerMakePayment from './pages/customer/MakePayment';

// Teller Pages & Components
import TellerLayout from './pages/teller/TellerLayout';
import CheckAccount from './pages/teller/CheckAccount';

// Manager Pages & Components
import ManagerLayout from './pages/manager/ManagerLayout';
import Withdraw from './pages/manager/Withdraw';
import Deposit from './pages/manager/Deposit';
import PendingTransactions from './pages/manager/PendingTransactions';
import ManagerAccountsList from './pages/manager/ManagerAccountsList';
import EditAccount from './pages/manager/EditAccount';
import AllUsers from './pages/manager/AllUsers';
import AllTransactions from './pages/manager/AllTransactions';
import AllGroups from './pages/manager/AllGroups';
import CreateGroup from './pages/manager/CreateGroup';
import GroupDetailsPage from './pages/manager/GroupDetailsPage';
import UserDetails from './pages/manager/UserDetails';
import ManagerFeatures from './pages/manager/ManagerFeatures'; // Import the new component


const theme = createTheme({
  direction: 'rtl',
  palette: {
    primary: {
      main: '#2563EB',
    },
    secondary: {
      main: '#7C3AED',
    },
    background: {
      default: '#F8FAFC',
      paper: '#FFFFFF',
    },
  },
  typography: {
    fontFamily: '"Segoe UI", "Helvetica Neue", sans-serif',
  },
  shape: {
    borderRadius: 12,
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/" element={<Navigate to="/login" />} />

        {/* Admin (Legacy) Routes */}
        <Route path="/dashboard" element={<Home />}>
          <Route index element={<Dashboard />} />
        </Route>

        {/* Customer Routes */}
        <Route path="/customer" element={<CustomerLayout />}>
          <Route path="dashboard" element={<CustomerDashboard />} />
          <Route path="account/:accountId" element={<CustomerAccountDetails />} />
          <Route path="internal-transfer" element={<InternalTransfer />} />
          <Route path="external-transfer" element={<ExternalTransfer />} />
          <Route path="my-transfers" element={<CustomerMyTransfers />} />
          <Route path="make-payment" element={<CustomerMakePayment />} />
        </Route>

        {/* Teller Routes */}
        <Route path="/teller" element={<TellerLayout />}>
          <Route path="dashboard" element={<CustomerDashboard />} />
          <Route path="internal-transfer" element={<InternalTransfer />} />
          <Route path="external-transfer" element={<ExternalTransfer />} />
          <Route path="my-transfers" element={<CustomerMyTransfers />} />
          <Route path="make-payment" element={<CustomerMakePayment />} />
          <Route path="create-account" element={<CreateAccount />} />
          <Route path="all-accounts" element={<ManagerAccountsList />} /> { /* Re-using Manager's list */}
          <Route path="check-account" element={<CheckAccount />} />
        </Route>

        {/* Manager Routes */}
        <Route path="/manager" element={<ManagerLayout />}>
            <Route path="dashboard" element={<CustomerDashboard />} />
            <Route path="internal-transfer" element={<InternalTransfer />} />
            <Route path="my-transfers" element={<CustomerMyTransfers />} />
            <Route path="make-payment" element={<CustomerMakePayment />} />
            <Route path="pending-transactions" element={<PendingTransactions />} />
            <Route path="withdraw" element={<Withdraw />} />
            <Route path="deposit" element={<Deposit />} />
            <Route path="create-account" element={<CreateAccount />} />
            <Route path="all-accounts" element={<ManagerAccountsList />} />
            <Route path="edit-account/:accountId" element={<EditAccount />} />
            <Route path="check-account" element={<CheckAccount />} />
            <Route path="all-users" element={<AllUsers />} />
            <Route path="user/:userId" element={<UserDetails />} />
            <Route path="all-transactions" element={<AllTransactions />} />
            <Route path="all-groups" element={<AllGroups />} />
            <Route path="create-group" element={<CreateGroup />} />
            <Route path="groups/:groupId" element={<GroupDetailsPage />} />
            <Route path="features" element={<ManagerFeatures />} /> {/* Add this route */}
        </Route>

      </Routes>
    </ThemeProvider>
  );
}

export default App;
