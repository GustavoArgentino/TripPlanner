import { TestBed } from '@angular/core/testing';
import { MAT_DATE_LOCALE } from '@angular/material/core';

import { BrDateAdapter } from './br-date-adapter';

describe('BrDateAdapter', () => {
  let adapter: BrDateAdapter;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: MAT_DATE_LOCALE, useValue: 'pt-BR' }, BrDateAdapter]
    });
    adapter = TestBed.inject(BrDateAdapter);
  });

  it('parses dd/mm/yyyy as day-month-year, not month-day-year', () => {
    const date = adapter.parse('01/09/2026');

    expect(date).not.toBeNull();
    expect(date!.getFullYear()).toBe(2026);
    expect(date!.getMonth()).toBe(8); // September, 0-indexed
    expect(date!.getDate()).toBe(1);
  });

  it('parses a two-digit-day date that could be ambiguous as month-day', () => {
    const date = adapter.parse('10/09/2026');

    expect(date!.getMonth()).toBe(8); // September
    expect(date!.getDate()).toBe(10);
  });

  it('returns null for an invalid calendar date (31 of February)', () => {
    expect(adapter.parse('31/02/2026')).toBeNull();
  });

  it('returns null for a malformed string', () => {
    expect(adapter.parse('not-a-date')).toBeNull();
    expect(adapter.parse('')).toBeNull();
  });

  it('parses a sub-100 year without the Date constructor rolling it into 19xx', () => {
    const date = adapter.parse('01/01/0099');

    expect(date).not.toBeNull();
    expect(date!.getFullYear()).toBe(99);
  });

  it('passes a Date instance through unchanged', () => {
    const original = new Date(2026, 8, 1);
    expect(adapter.parse(original)).toBe(original);
  });
});
