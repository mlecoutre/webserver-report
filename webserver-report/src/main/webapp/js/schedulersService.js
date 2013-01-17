angular.module('schedService', ['ngResource']).
factory('Scheduler', function ($resource) {
    return $resource('/report/services/schedulers/:schedulerId/:action', {
        schedulerId: '@schedulerId'
    }, {
        query: {
            method: 'GET',
            isArray: true
        },
        start: {
             method: 'POST',
             params: {  schedulerId: '@schedulerId', action:"start"}
        },
        stop: {
             method: 'POST',
             params: {  schedulerId: '@schedulerId', action:"stop"}
        }
    });
});