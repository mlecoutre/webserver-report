'use strict';

/* Directives */


angular.module('reportApp.directives', []).
directive('appVersion', ['version', function (version) {
    return function (scope, elm, attrs) {
        elm.text(version);
    };
}]).directive('ngFocusOut', function () {
    return function (scope, element, attrs) {
        element.bind('focusout', function () {
            scope.$eval(attrs.ngFocusOut)
        });
    }
}).directive('ngShowTab', function () {
    return {
        link: function (scope, element, attrs) {
            element.click(function (e) {
                e.preventDefault();
                $(element).tab('show');
            });
        }
    };
}).directive('ngTableAction', function () {
      return {
          link: function (scope, element, attrs) {
              element.click(function (e) {
                  e.preventDefault();
                  scope.$eval(attrs.ngTableAction)
              });
          }
      };
});