/**
 * Scheduler controller
 */
function SchedulerCtrl($scope, $http, schedulerService) {

    $scope.schedulerId;
    $scope.applicationName;
    $scope.asName;
    $scope.serverName;
    $scope.requestRepeatIntervalInMinutes = 10; //default value
    $scope.state;
    $scope.endPointURL;
    $scope.schedulers = schedulerService.listSchedulers();

    $scope.addScheduler = function () {
        var scheduler = createSchedulerFromScopeVariable();
        var result = schedulerService.addScheduler(scheduler);
    }

    $scope.deleteScheduler = function (schedulerId) {
        bootbox.confirm("Are you sure?", function (confirmed) {
            if (confirmed) {
                console.log("Scheduler delete " + schedulerId);
                result = schedulerService.deleteScheduler(schedulerId);
            } else {
                console.log("Scheduler deletion cancelled for "+schedulerId);
            }
        });
    }

    /**
     * Initialize the update form
     */
    $scope.editScheduler = function (schedulerId) {
        var scheduler = schedulerService.findSchedulerById(schedulerId);
        $scope.schedulerId = scheduler.schedulerId;
        $scope.applicationName = scheduler.applicationName;
        $scope.asName = scheduler.asName;
        $scope.serverName = scheduler.serverName;
        $scope.requestRepeatIntervalInMinutes = scheduler.requestRepeatIntervalInMinutes;
        $scope.state = scheduler.state;
        $scope.endPointURL = scheduler.endPointURL;
        $('#editScheduler').tab('show');
    }

    $scope.updateScheduler = function (schedulerId) {
        var scheduler = createSchedulerFromScopeVariable();
        schedulerService.updateScheduler(scheduler);
    }

    $scope.startScheduler = function (schedulerId) {
        var scheduler = schedulerService.findSchedulerById(schedulerId);
        scheduler.state = "STARTED";
        schedulerService.updateScheduler(scheduler);
    }

    $scope.stopScheduler = function (schedulerId) {
        var scheduler = schedulerService.findSchedulerById(schedulerId);
        scheduler.state = "STOPPED";
        schedulerService.updateScheduler(scheduler);
    }

    function createSchedulerFromScopeVariable() {
        var scheduler = {
            applicationName: $scope.applicationName,
            asName: $scope.asName,
            serverName: $scope.serverName,
            endPointURL: $scope.endPointURL,
            requestRepeatIntervalInMinutes: $scope.requestRepeatIntervalInMinutes,
            state: $scope.state
        }
        return scheduler;
    }
}