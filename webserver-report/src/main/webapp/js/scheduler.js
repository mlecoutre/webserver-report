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
        scheduler.schedulerId = null;
        var promise = schedulerService.addScheduler(scheduler);

        promise.then(function (greeting) {
            $scope.schedulers = schedulerService.listSchedulers();
            $('#msg').html('Scheduler successfully created');
            $('#msgBox').show();
        }, function (reason) {
            $('#msg').html('Failed to create a new scheduler : ' + reason);
            $('#msgBox').show();
        });
    }

    $scope.deleteScheduler = function (schedulerId) {
        bootbox.confirm("Are you sure?", function (confirmed) {
            if (confirmed) {
                console.log("Scheduler delete " + schedulerId);
                var promise = schedulerService.deleteScheduler(schedulerId);
                promise.then(function () {
                    $scope.schedulers = schedulerService.listSchedulers();
                    $('#msg').html('Scheduler successfully deleted');
                    $('#msgBox').show();
                }, function (reason) {
                    $('#msg').html('Failed to delete the scheduler : ' + reason);
                    $('#msgBox').show();
                });
            } else {
                console.log("Scheduler deletion cancelled for " + schedulerId);
            }
        });
    }

    /**
     * Initialize the update form
     */
    $scope.editScheduler = function (schedulerId) {
        var promise = schedulerService.findSchedulerById(schedulerId);

        promise.then(function (scheduler) {
            $scope.schedulerId = scheduler.schedulerId;
            $scope.applicationName = scheduler.applicationName;
            $scope.asName = scheduler.asName;
            $scope.serverName = scheduler.serverName;
            $scope.requestRepeatIntervalInMinutes = scheduler.requestRepeatIntervalInMinutes;
            $scope.state = scheduler.state;
            $scope.endPointURL = scheduler.endPointURL;

            $('#titleEdit').click();
            $('#updateScheduler').show();
            $('#addScheduler').hide();
        });
    }

    $scope.cancel = function () {
        $('#reset').click();
        $('#titleList').click();
        $('#updateScheduler').hide();
        $('#addScheduler').show();
    }

    $scope.updateScheduler = function (schedulerId) {
        var scheduler = createSchedulerFromScopeVariable();
        var promise = schedulerService.updateScheduler(scheduler);
        promise.then(function () {
            $scope.schedulers = schedulerService.listSchedulers();
            $('#msg').html('Scheduler successfully updated');
            $('#msgBox').show();
        }, function (reason) {
            $('#msg').html('Failed to update the scheduler : ' + reason);
            $('#msgBox').show();
        });
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