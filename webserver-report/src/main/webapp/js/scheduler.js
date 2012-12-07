/**
 * Scheduler controller
 */
function SchedulerCtrl($scope, $http, schedulerService) {

    $scope.schedulerId;
    $scope.applicationName;
    $scope.as;
    $scope.interval = 10; //default value
    $scope.status;
    $scope.url;
    $scope.schedulers = schedulerService.listSchedulers() ;

    /**
     *
     */
    $scope.addScheduler = function(){
        var scheduler = {
            applicationName :   $scope.applicationName,
            as              :   $scope.as,
            url             :   $scope.url,
            interval        :   $scope.interval,
            status          :   $scope.status
        }
        var result = schedulerService.addScheduler(scheduler);
    }

    $scope.deleteScheduler = function(schedId){
    console.log(">> "+schedId);
     bootbox.confirm("Are you sure?", function (confirmed) {
                if (confirmed) {
                    console.log("Scheduler delete" + $scope.applicationName);
                    result = schedulerService.deleteScheduler($scope.schedulerId);
                } else {
                    console.log("Scheduler deletion cancelled");
                }
            });
    }

    $scope.updateScheduler = function(){

    }

    $scope.startScheduler = function(){

    }

    $scope.stopScheduler = function(){

    }

}