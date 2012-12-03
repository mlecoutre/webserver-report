'use strict';


// Declare app level module which depends on filters, and services
angular.module('reportApp', ['reportApp.filters', 'reportApp.services', 'reportApp.directives']).
  config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/loader', {
         templateUrl: 'partials/partial1.html',
         controller: LoaderCtrl
        }
    );
    $routeProvider.otherwise({redirectTo: '/loader'});
  }]);

