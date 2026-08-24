import { decodeJwtPayload, isJwtExpired } from './jwt.util';

function makeToken(payload: Record<string, unknown>): string {
  const encode = (obj: unknown) => {
    const bytes = new TextEncoder().encode(JSON.stringify(obj));
    let binary = '';
    bytes.forEach((byte) => (binary += String.fromCharCode(byte)));
    return btoa(binary).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
  };

  return `${encode({ alg: 'none' })}.${encode(payload)}.signature`;
}

describe('jwt.util', () => {
  it('decodeJwtPayload reads the payload of a well-formed token', () => {
    const token = makeToken({ sub: 'user@example.com', exp: 9999999999 });

    expect(decodeJwtPayload(token)).toEqual({ sub: 'user@example.com', exp: 9999999999 });
  });

  it('decodeJwtPayload correctly decodes non-ASCII (UTF-8) claim values', () => {
    const token = makeToken({ sub: 'josé.gonçalves@example.com', exp: 9999999999 });

    expect(decodeJwtPayload(token)?.sub).toBe('josé.gonçalves@example.com');
  });

  it('decodeJwtPayload returns null for a malformed token', () => {
    expect(decodeJwtPayload('not-a-jwt')).toBeNull();
  });

  it('isJwtExpired is false for a token with a future exp', () => {
    const token = makeToken({ sub: 'user@example.com', exp: Math.floor(Date.now() / 1000) + 3600 });

    expect(isJwtExpired(token)).toBeFalse();
  });

  it('isJwtExpired is true for a token with a past exp', () => {
    const token = makeToken({ sub: 'user@example.com', exp: Math.floor(Date.now() / 1000) - 3600 });

    expect(isJwtExpired(token)).toBeTrue();
  });

  it('isJwtExpired is true when the token has no exp claim', () => {
    const token = makeToken({ sub: 'user@example.com' });

    expect(isJwtExpired(token)).toBeTrue();
  });
});
