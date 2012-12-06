'use strict';


// Declare app level module which depends on filters, and services
angular.module('reportApp', ['reportApp.filters', 'reportApp.services', 'reportApp.directives']).
  config(['$routeProvider', function($routeProvider) {
    $routeProvider.
        when('/loader', {
         templateUrl: 'partials/loader.html',
         controller: LoaderCtrl
        }).
        when('/scheduler', {templateUrl: 'partials/scheduler.html', controller: SchedulerCtrl}).
        when('/index', {templateUrl: 'partials/index.html'}).
        when('/about', {templateUrl: 'partials/about.html'}).
        when('/memory', {templateUrl: 'partials/memory.html'}).
        when('/datasource', {templateUrl: 'partials/datasource.html'}).
        when('/thread', {templateUrl: 'partials/thread.html'}).
        when('/jms', {templateUrl: 'partials/jms.html', controller: JmsCtrl}).
        otherwise({redirectTo: '/index'});
  }]);

