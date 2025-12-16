import api from './api';

const interestService = {
  /**
   * Apply interest to a specific account
   * POST /api/interest/accounts/{accountId}/apply
   */
  applyInterest: (accountId) => {
    return api.post(`/interest/accounts/${accountId}/apply`);
  },

  /**
   * Apply interest to all accounts
   * POST /api/interest/apply-all
   */
  applyAllInterest: () => {
    return api.post('/interest/apply-all');
  },

  /**
   * Change interest strategy for an account
   * POST /api/interest/accounts/{accountId}/change-strategy
   * Body: { strategyName: string }
   */
  changeStrategy: (accountId, strategyName) => {
    return api.post(`/interest/accounts/${accountId}/change-strategy`, { strategyName });
  },

  /**
   * Get interest report for an account
   * GET /api/interest/accounts/{accountId}/report
   */
  getInterestReport: (accountId) => {
    return api.get(`/interest/accounts/${accountId}/report`);
  },

  /**
   * Calculate future interest
   * GET /api/interest/accounts/{accountId}/future/{months}
   */
  calculateFutureInterest: (accountId, months) => {
    return api.get(`/interest/accounts/${accountId}/future/${months}`);
  },

  /**
   * Get all available strategies
   * GET /api/interest/strategies
   */
  getAllStrategies: () => {
    return api.get('/interest/strategies');
  },

  /**
   * Get supported strategies for an account type
   * GET /api/interest/strategies/{accountType}
   */
  getSupportedStrategies: (accountType) => {
    return api.get(`/interest/strategies/${accountType}`);
  },

  /**
   * Compare two strategies for an account
   * GET /api/interest/accounts/{accountId}/compare?strategy1=...&strategy2=...
   */
  compareStrategies: (accountId, strategy1, strategy2) => {
    return api.get(`/interest/accounts/${accountId}/compare`, {
      params: { strategy1, strategy2 }
    });
  },

  /**
   * Get effective interest rate for an account
   * GET /api/interest/accounts/{accountId}/rate
   */
  getEffectiveInterestRate: (accountId) => {
    return api.get(`/interest/accounts/${accountId}/rate`);
  }
};

export default interestService;
