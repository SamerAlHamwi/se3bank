import api from './api';

const bankingService = {
  /**
   * Process a payment (Simulated)
   * POST /api/payments/process
   */
  processPayment: (paymentData) => {
    // paymentData: { amount: number, currency: string, provider: string (optional), cardNumber, etc }
    return api.post('/payments/process', paymentData);
  },

  /**
   * Open a new account (Facade)
   * POST /api/banking/accounts/open
   */
  openAccount: (accountData) => {
    // accountData: { userId: number, accountType: string, initialDeposit: number, currency: string }
    return api.post('/banking/accounts/open', accountData);
  },

  /**
   * Transfer money (Facade)
   * POST /api/banking/transfer
   */
  transferMoney: (transferData) => {
    // transferData: { fromAccountId: number, toAccountId: number, amount: number, description: string }
    return api.post('/banking/transfer', transferData);
  },

  /**
   * Withdraw money (Facade)
   * POST /api/banking/withdraw
   */
  withdrawMoney: (withdrawData) => {
    // withdrawData: { accountId: number, amount: number }
    return api.post('/banking/withdraw', withdrawData);
  },

  /**
   * Deposit money (Facade)
   * POST /api/banking/deposit
   */
  depositMoney: (depositData) => {
    // depositData: { accountId: number, amount: number }
    return api.post('/banking/deposit', depositData);
  },

  /**
   * Get account summary (Facade)
   * GET /api/banking/accounts/{accountNumber}/summary
   */
  getAccountSummary: (accountNumber) => {
    return api.get(`/banking/accounts/${accountNumber}/summary`);
  },

  /**
   * Get user summary (Facade)
   * GET /api/banking/users/{userId}/summary
   */
  getUserSummary: (userId) => {
    return api.get(`/banking/users/${userId}/summary`);
  }
};

export default bankingService;
