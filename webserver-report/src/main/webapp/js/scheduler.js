/**
 * Scheduler controller
 */
function SchedulerCtrl($scope, $rootScope, $http, schedulerService) {

    $scope.schedulerId;
    $scope.applicationName;
    $scope.asName;
    $scope.serverName;
    $scope.requestRepeatIntervalInMinutes = 10; //default value
    $scope.state;
    $scope.endPointURL;
    $scope.allSchedulers = listSchedulers("");
    $scope.schedulers;
    $scope.filter = "";

    $scope.scheduler;


    function listSchedulers(filter){
        var promise = schedulerService.listSchedulers(filter);
        $scope.allSchedulers = promise;
        searchScheduler();
        return promise;
    }

    $scope.addScheduler = function () {
        $scope.schedulerId = null;
        var scheduler = createSchedulerFromScopeVariable();
        var promise = schedulerService.addScheduler(scheduler);

        promise.then(function (greeting) {
            $scope.schedulers = listSchedulers( $scope.filter );
            displaySuccessMessage('Scheduler successfully created');
        }, function (reason) {
            displayErrorMessage('Failed to create a new scheduler : ' + reason);
        });
    }

    $scope.deleteScheduler = function (schedulerId) {
        bootbox.confirm("Are you sure?", function (confirmed) {
            if (confirmed) {
                console.log("Scheduler delete " + schedulerId);
                var promise = schedulerService.deleteScheduler(schedulerId);
                promise.then(function () {
                    $scope.schedulers = listSchedulers( $scope.filter );
                    displaySuccessMessage('Scheduler successfully deleted');
                }, function (reason) {
                    displayErrorMessage('Failed to delete the scheduler : ' + reason);
                });
            } else {
                console.log("Scheduler deletion cancelled for " + schedulerId);
            }
        });
    }

    $scope.search = searchScheduler;

    function searchScheduler(){
        var sList = [];
        var promise = $scope.allSchedulers;
        promise.then(function(schedulers){
             for (var i=0; i < schedulers.length; i++)  {
                   var s =  schedulers[i];
                   var str =  s.applicationName + s.asName + s.serverName;
                   if(str == null || str == 0){
                        continue;
                   }
                   if (str.match($scope.filter) != null){
                         sList.push(s);
                   }
             }
             $scope.schedulers = sList;
       });
    }

    /**
     * Initialize the update form
     */
    $scope.initScheduler = function (scheduler) {
        var promise = schedulerService.findSchedulerById(scheduler.schedulerId);

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
        $scope.schedulerId = null;
    }

    $scope.updateScheduler = function (schedulerId) {
        var scheduler = createSchedulerFromScopeVariable();
        var promise = schedulerService.updateScheduler(scheduler);
        promise.then(function () {
            $scope.schedulers = listSchedulers( $scope.filter );
            displaySuccessMessage('Scheduler successfully updated');
            $scope.schedulerId = null;
        }, function (reason) {
            displayErrorMessage('Failed to update the scheduler : ' + reason);
        });
    }

    $scope.changeStatus = function(scheduler){
       var promise = schedulerService.changeStatus(scheduler);
       promise.then(function(){
            $scope.schedulers = schedulerService.listSchedulers( $scope.filter );
            displaySuccessMessage('Succeed to change status of the scheduler.' );
       }, function(reason){
            displayErrorMessage('Failed to change status of the scheduler : ' + reason);
       });
    };

     function displayErrorMessage( msg ){
            $('#msg').html(msg);
            $('#msgBox').removeClass().addClass('alert alert-error');
            $('#msgBox').show();
     }

    function displaySuccessMessage( msg ){
           $('#msg').html(msg);
           $('#msgBox').removeClass().addClass('alert alert-success');
           $('#msgBox').show();
    }

    function createSchedulerFromScopeVariable() {
        var scheduler = {
            schedulerId                     : $scope.schedulerId,
            applicationName                 : $scope.applicationName,
            asName                          : $scope.asName,
            serverName                      : $scope.serverName,
            endPointURL                     : $scope.endPointURL,
            requestRepeatIntervalInMinutes  : $scope.requestRepeatIntervalInMinutes,
            state                           : $scope.state
        }
        return scheduler;
    }
}