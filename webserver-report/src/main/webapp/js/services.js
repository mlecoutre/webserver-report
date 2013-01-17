'use strict';

/* Services */
angular.module('reportApp.services', [])
// Declare application Service factory
.factory('applicationsService', ['$http', '$q', function ($http, $q) {

    // Create the application service
    function ApplicationService($http, $q) {
        this.retrieveApps = function () {
            var deferred = $q.defer();
            $http({
                method: 'GET',
                url: '/report/services/MonitorConfig/applications',
            })
                .success(function (data, status, headers, config) {
                console.log("SUCCESS ApplicationService - retrieveApps()");
                return deferred.resolve(data);
            })
                .error(function (data, status, headers, config) {
                console.log("ERROR ApplicationService - retrieveApps()");
                return deferred.reject(data);
            });
            return deferred.promise;
        }

        this.retrievePhysicalServers = function (applicationName) {
            var deferred = $q.defer();
            $http({
                method: 'GET',
                url: '/report/services/MonitorConfig/servers/' + applicationName,
            })
                .success(function (data, status, headers, config) {
                console.log("SUCCESS ApplicationService - retrievePhysicalServers()");
                return deferred.resolve(data);
            })
                .error(function (data, status, headers, config) {
                console.log("ERROR ApplicationService - retrievePhysicalServers()");
                return deferred.reject(data);
            });
            return deferred.promise;
        }

        this.retrieveASS = function (applicationName) {
            var deferred = $q.defer();
            $http({
                method: 'GET',
                url: '/report/services/MonitorConfig/ass/' + applicationName,
            })
                .success(function (data, status, headers, config) {
                console.log("SUCCESS ApplicationService - retrieveASS()");
                return deferred.resolve(data);
            })
                .error(function (data, status, headers, config) {
                console.log("ERROR ApplicationService - retrieveASS()");
                return deferred.reject(data);
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
                console.log("SUCCESS ApplicationService - retrieveQCFs()");
                return deferred.resolve(data);
            })
                .error(function (data, status, headers, config) {
                console.log("ERROR ApplicationService - retrieveQCFs()");
                return deferred.reject(data);
            });
            return deferred.promise;
        }

        this.retrieveDataSources = function (applicationName, server, as) {
            var deferred = $q.defer();
            $http({
                method: 'GET',
                url: '/report/services/MonitorConfig/dataSources/' + applicationName + '/' + server + '/' + as
            })
                .success(function (data, status, headers, config) {
                console.log("SUCCESS ApplicationService - retrieveDataSources()");
                return deferred.resolve(data);
            })
                .error(function (data, status, headers, config) {
                console.log("ERROR ApplicationService - retrieveDataSources()");
                return deferred.reject(data);
            });
            return deferred.promise;
        }

        this.getStats = function (applicationName) {
            var deferred = $q.defer();
            $http({
                method: 'GET',
                url: '/report/services/MonitorConfig/stats/' + applicationName
            })
                .success(function (data, status, headers, config) {
                console.log("SUCCESS ApplicationService - getStats()");
                return deferred.resolve(data);
            })
                .error(function (data, status, headers, config) {
                console.log("ERROR ApplicationService - getStats()");
                return deferred.reject(data);
            });
            return deferred.promise;
        }

        this.batchInsert = function (applicationName, files) {
            var deferred = $q.defer();
            $http({
                method: 'POST',
                url: '/report/services/MonitorConfig/' + applicationName,
                data: files
            })
                .success(function (data, status, headers, config) {
                console.log("SUCCESS ApplicationService - batchInsert()");
                return deferred.resolve(data);

            })
                .error(function (data, status, headers, config) {
                console.log("ERROR ApplicationService - batchInsert()");
                return deferred.reject(data);
            });
            return deferred.promise;
        }

        this.purge = function (applicationName) {
            var deferred = $q.defer();
            $http({
                method: 'GET',
                url: '/report/services/MonitorConfig/purge/' + applicationName
            })
                .success(function (data, status, headers, config) {
                console.log("SUCCESS ApplicationService - purge()");
                return deferred.resolve(data);
            })
                .error(function (data, status, headers, config) {
                console.log("ERROR ApplicationService - purge()");
                return deferred.resolve(data);
            });
        }
    }
    // the Factory return an instance of the applicationService
    return new ApplicationService($http, $q);
}]).
value('version', '1.0.0');