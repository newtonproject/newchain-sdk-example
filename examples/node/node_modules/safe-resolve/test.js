'use strict'

const test = require('tap').test
const path = require('path')
const safe = require('./')

test('should work', function(t) {
  t.plan(1)
  var fp = path.join(__dirname, 'index.js')
  var o = safe('./index.js')
  t.equal(o, fp)
})

test('should not throw', function(t) {
  t.plan(1)
  var o = safe('./biscuits.js')
  t.equal(o, null)
})
