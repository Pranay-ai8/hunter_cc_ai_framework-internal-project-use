export const BASE_URL = process.env.BASE_URL || 'https://www.saucedemo.com/';

export const CREDENTIALS = {
  standard_user: 'secret_sauce',
  locked_out_user: 'secret_sauce',
  problem_user: 'secret_sauce',
  performance_glitch_user: 'secret_sauce',
  error_user: 'secret_sauce',
  visual_user: 'secret_sauce',
} as const;

export type SauceDemoUser = keyof typeof CREDENTIALS;
