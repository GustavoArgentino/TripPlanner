import { Injectable } from '@angular/core';
import { NativeDateAdapter } from '@angular/material/core';

const DATE_PATTERN = /^(\d{1,2})\/(\d{1,2})\/(\d{4})$/;

/**
 * NativeDateAdapter.parse() delegates to the JS Date.parse() heuristics for
 * slash-separated strings, which read them as MM/DD/YYYY regardless of
 * MAT_DATE_LOCALE — so typing "01/09/2026" (1 de setembro) silently becomes
 * January 9th. This adapter parses typed input as DD/MM/YYYY explicitly;
 * display formatting is untouched (inherited from NativeDateAdapter, already
 * locale-aware via Intl).
 */
@Injectable()
export class BrDateAdapter extends NativeDateAdapter {
  override parse(value: unknown): Date | null {
    if (value instanceof Date) {
      return value;
    }

    if (typeof value !== 'string') {
      return null;
    }

    const match = value.trim().match(DATE_PATTERN);
    if (!match) {
      return null;
    }

    const day = Number(match[1]);
    const month = Number(match[2]);
    const year = Number(match[3]);

    // Not `new Date(year, month, day)`: the Date constructor special-cases
    // two-digit years (0-99) as 19xx. setFullYear doesn't have that pitfall
    // — the same workaround NativeDateAdapter itself uses internally.
    const date = new Date();
    date.setFullYear(year, month - 1, day);
    date.setHours(0, 0, 0, 0);

    // Reject values that overflowed into a different month (e.g. 31/02).
    return date.getFullYear() === year && date.getMonth() === month - 1 && date.getDate() === day
      ? date
      : null;
  }
}
