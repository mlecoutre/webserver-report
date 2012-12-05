'use strict';

/* Services */
angular.module('reportApp.services', [])
// Declare application Service factory
.factory('applicationsService', ['$http', '$q', function ($http, $q) {

    // Create the application service
    function ApplicationService($http, $q) {
        this.retrieveApps = function () {
            console.log("retrieveApps");
            var deferred = $q.defer();
            $http({
                method: 'GET',
                url: '/report/services/MonitorConfig/applications',
            })
                .success(function (data, status, headers, config) {
                return deferred.resolve(data);
            })
                .error(function (data, status, headers, config) {
                console.log("error");
            });
            return deferred.promise;
        }

        this.retrievePhysicalServers = function (applicationName) {
            console.log("retrievePhysicalServers");
            var deferred = $q.defer();
            $http({
                method: 'GET',
                url: '/report/services/MonitorConfig/servers/' + applicationName,
            })
                .success(function (data, status, headers, config) {
                return deferred.resolve(data);
            })
                .error(function (data, status, headers, config) {
                console.log("error");
            });
            return deferred.promise;
        }

        this.retrieveASS = function (applicationName) {
            console.log("retrieveASS");
            var deferred = $q.defer();
            $http({
                method: 'GET',
                url: '/report/services/MonitorConfig/ass/' + applicationName,
            })
                .success(function (data, status, headers, config) {
                return deferred.resolve(data);
            })
                .error(function (data, status, headers, config) {
                console.log("error");
            });
            return deferred.promise;
        }

        this.retrieveQCFs = function (applicationName, server, as) {
                    console.log("retrieveDataSources");
                    var deferred = $q.defer();
                    $http({
                        method: 'GET',
                        url: '/report/services/MonitorConfig/qcfs/' + applicationName + '/' + server + '/' + as
                    })
                        .success(function (data, status, headers, config) {
                        return deferred.resolve(data);
                    })
                        .error(function (data, status, headers, config) {
                        console.log("error");
                    });
                    return deferred.promise;
        }

        this.retrieveDataSources = function (applicationName, server, as) {
            console.log("retrieveDataSources");
            var deferred = $q.defer();
            $http({
                method: 'GET',
                url: '/report/services/MonitorConfig/dataSources/' + applicationName + '/' + server + '/' + as
            })
                .success(function (data, status, headers, config) {
                return deferred.resolve(data);
            })
                .error(function (data, status, headers, config) {
                console.log("error");
            });
            return deferred.promise;
        }

        this.getStats = function (applicationName) {
            console.log("applicationsService.getStats");
            var deferred = $q.defer();
            $http({
                method: 'GET',
                url: '/report/services/MonitorConfig/stats/' + applicationName
            })
                .success(function (data, status, headers, config) {
                console.log("success");
                return deferred.resolve(data);
            })
                .error(function (data, status, headers, config) {
                console.log("error");
            });
            return deferred.promise;
        }

        this.batchInsert = function (applicationName, files) {
            console.log("applicationsService.batchInsert");
            var deferred = $q.defer();
            $http({
                method: 'POST',
                url: '/report/services/MonitorConfig/' + applicationName,
                data: files
            })
                .success(function (data, status, headers, config) {
                console.log("success");
                return deferred.resolve(data);

            })
                .error(function (data, status, headers, config) {
                console.log("error");
            });
            return deferred.promise;
        }

        this.purge = function (applicationName) {
            $http({
                method: 'GET',
                url: '/report/services/MonitorConfig/purge/' + $scope.applicationName
            })
                .success(function (data, status, headers, config) {
                return deferred.resolve(data);
            })
                .error(function (data, status, headers, config) {
                return deferred.resolve(data);
            });
        }
    }
    // the Factory return an instance of the applicationService
    return new ApplicationService($http, $q);
}]).
value('version', '1.0.0');