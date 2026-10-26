import { test } from 'node:test'
import assert from 'node:assert/strict'
import { formatDateOnly, clinicToday, ageOn } from '../src/lib/dates.ts'

test('prescription calendar date stays unchanged across browser time zones', () => {
  const original = process.env.TZ
  try {
    for (const zone of ['America/El_Salvador', 'UTC', 'Asia/Tokyo']) {
      process.env.TZ = zone
      assert.equal(formatDateOnly('2026-10-25'), '25/10/2026')
    }
  } finally {
    if (original === undefined) delete process.env.TZ
    else process.env.TZ = original
  }
})

test('clinic day changes at Salvador midnight instead of UTC midnight', () => {
  assert.equal(clinicToday(new Date('2026-10-26T05:59:59Z')), '2026-10-25')
  assert.equal(clinicToday(new Date('2026-10-26T06:00:00Z')), '2026-10-26')
})

test('age changes on the birthday, not on the preceding day', () => {
  assert.equal(ageOn('2000-10-25', '2026-10-24'), 25)
  assert.equal(ageOn('2000-10-25', '2026-10-25'), 26)
  assert.equal(ageOn('2000-10-25', '2026-10-26'), 26)
})
