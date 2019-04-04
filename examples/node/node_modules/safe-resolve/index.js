'use strict'

module.exports = function(fp) {
  try {
    return require.resolve(fp)
  } catch (err) {
    return null
  }
}
