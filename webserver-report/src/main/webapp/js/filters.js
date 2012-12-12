'use strict';

/* Filters */

angular.module('reportApp.filters', []).
  filter('interpolate', ['version', function(version) {
    return function(text) {
      return String(text).replace(/\%VERSION\%/mg, version);
    }
  }]).filter('displayStatus', function() {
        return function(input) {
            if (input == 'OK')
                return '\u2713';
            return 'ERR: '+input;
        };
  });;