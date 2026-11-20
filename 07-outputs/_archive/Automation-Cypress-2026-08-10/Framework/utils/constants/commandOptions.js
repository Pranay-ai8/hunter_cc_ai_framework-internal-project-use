/**
 * Shared Cypress command option objects and HTTP method constants.
 * Reuse these instead of inlining literal option objects / HTTP verb
 * strings in specs or page objects.
 */
export const force = { force: true };

export const assertionChainer = {
  visible: 'be.visible',
  notVisible: 'not.be.visible',
  exist: 'exist',
  notExist: 'not.exist',
  disabled: 'be.disabled',
  enabled: 'be.enabled',
  haveText: 'have.text',
  containText: 'contain.text',
  haveValue: 'have.value',
  haveLength: 'have.length',
};

export const requestMethod = {
  get: 'GET',
  post: 'POST',
  put: 'PUT',
  delete: 'DELETE',
  patch: 'PATCH',
};
