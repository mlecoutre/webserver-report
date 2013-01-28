'use strict';


// Declare app level module which depends on filters, and services
angular.module('reportApp', ['reportApp.filters', 'reportApp.services', 'reportApp.directives', 'schedService', 'ui']).
  config(['$routeProvider', function($routeProvider) {
    $routeProvider.
        when('/loader', {
         templateUrl: 'partials/loader.html',
         controller: LoaderCtrl
        }).
        when('/scheduler', {templateUrl: 'partials/scheduler.html', controller: SchedulerCtrl}).
        when('/index', {templateUrl: 'partials/index.html'}).
        when('/about', {templateUrl: 'partials/about.html'}).
        when('/memory', {templateUrl: 'partials/memory.html', controller: MemoryCtrl}).
        when('/datasource', {templateUrl: 'partials/datasource.html', controller: DataSourceCtrl}).
        when('/sample', {templateUrl: 'partials/sample.html'}).
        when('/thread', {templateUrl: 'partials/thread.html', controller: ThreadCtrl}).
        when('/jms', {templateUrl: 'partials/jms.html', controller: JmsCtrl}).
        otherwise({redirectTo: '/index'});
  }]);

