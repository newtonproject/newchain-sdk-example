# safe-resolve

[![Build Status](https://travis-ci.org/evanlucas/safe-resolve.svg)](https://travis-ci.org/evanlucas/safe-resolve)
[![Coverage Status](https://coveralls.io/repos/evanlucas/safe-resolve/badge.svg?branch=master&service=github)](https://coveralls.io/github/evanlucas/safe-resolve?branch=master)

`require.resolve` without throwing

## Install

```bash
$ npm install --save safe-resolve
```

## Test

```bash
$ npm test
```

## Usage

```js
const safe = require('safe-resolve')

// index.js exists
safe('./index.js')
// returns the resolved filepath

// biscuits.js does not exist
safe('./biscuits.js')
// => null
```

## Author

Evan Lucas

## License

MIT (See `LICENSE` for more info)
