function SchedulerCtrl($scope, $http, schedulerService) {

    $scope.applicationName
    $scope.as;
    $scope.interval = 10; //default value

    $scope.schedulers = schedulerService.listSchedulers() ;



    $scope.add = function(){


    }

    $scope.startScheduler = function(){

    }

    $scope.stopScheduler = function(){

    }

}