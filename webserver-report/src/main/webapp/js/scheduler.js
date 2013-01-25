/**
 * Scheduler controller
 * http://docs.angularjs.org/api/ngResource.$resource
 */

function SchedulerCtrl($scope, Scheduler, $cacheFactory) {
    $scope.scheduler;
    $scope.allSchedulers = listSchedulers(); // all schedulers
    $scope.schedulers; // displayed schedulers once filter is applied
    $scope.filter = "";


    $scope.addScheduler = function() {
        console.log("addScheduler");
        Scheduler.save($scope.scheduler,
        function() {
            listSchedulers();
            displaySuccessMessage("Scheduler successfully created");
        }, function() {
            displayErrorMessage("ERROR, Scheduler unsuccessfully created");
        });
    }

    $scope.applyFilter = searchScheduler;
    $scope.refresh = listSchedulers;

    function listSchedulers() {
        Scheduler.query(

        function(schedulers) {
            $scope.allSchedulers = schedulers;
            searchScheduler(schedulers);
        });
    }

    $scope.initScheduler = function(scheduler) {
        console.log("initScheduler");
        var shed = Scheduler.get({
            schedulerId: scheduler.schedulerId
        }, function() {
            $scope.scheduler = scheduler;
            $('#titleEdit').click();
            $('#updateScheduler').show();
            $('#addScheduler').hide();
        });

    }

    $scope.cancel = function() {
        $scope.scheduler = null;
        listSchedulers();
        $('#reset').click();
        $('#titleList').click();
        $('#updateScheduler').hide();
        $('#addScheduler').show();
    }

    $scope.deleteScheduler = function(scheduler) {
        console.log("deleteScheduler")
        scheduler.$remove({
            schedulerId: scheduler.schedulerId
        }, function() {
            listSchedulers();
            displaySuccessMessage("Scheduler successfully deleted");

        }, function() {
            displayErrorMessage("ERROR, Scheduler unsuccessfully deleted");
        });
    }

    $scope.updateScheduler = function() {
        console.log("updateScheduler");
        $scope.scheduler.$save({
            schedulerId: $scope.scheduler.schedulerId
        }, function() {
            listSchedulers();
            displaySuccessMessage("Scheduler successfully updated");
            $('#reset').click();
            $('#titleList').click();
            $('#updateScheduler').hide();
            $('#addScheduler').show();
        }, function() {
            displayErrorMessage("ERROR, Scheduler unsuccessfully updated");
        });
    }

    $scope.changeStatus = function(scheduler) {
        console.log("changeStatus");
        if(scheduler.state == 'running') {
            scheduler.$stop({}, function() {
                listSchedulers();
                displaySuccessMessage("Scheduler successfully stopped");
            }, function() {
                displayErrorMessage("ERROR Scheduler UNsuccessfully stopped");
            });
        } else {
            scheduler.$start({}, function() {
                listSchedulers();
                displaySuccessMessage("Scheduler successfully started");
            }, function() {
                displayErrorMessage("ERROR Scheduler UNsuccessfully started");
            });
        }
    };

    function searchScheduler() {
        var sList = [];
        for(var i = 0; i < $scope.allSchedulers.length; i++) {
            var s = $scope.allSchedulers[i];
            var str = s.applicationName + s.asName + s.serverName;
            if(str == null || str == 0) {
                continue;
            }
            if(str.match($scope.filter) != null) {
                sList.push(s);
            }
        }
        $scope.schedulers = sList;
    }

    function displayErrorMessage(msg) {
        $('#msg').html(msg);
        $('#msgBox').removeClass().addClass('alert alert-error');
        $('#msgBox').show();
    }

    function displaySuccessMessage(msg) {
        $('#msg').html(msg);
        $('#msgBox').removeClass().addClass('alert alert-success');
        $('#msgBox').show();
    }
}