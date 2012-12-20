angular.module('schedService', ['ngResource']).
    factory('Scheduler', function($resource){
  return $resource('/report/services/schedulers', {}, {
    query: {method:'GET', isArray:true}
  });
});