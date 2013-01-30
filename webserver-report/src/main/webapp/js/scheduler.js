(function() {

  window.SchedulerCtrl = function($scope, Scheduler, applicationsService) {
    var listSchedulers, searchScheduler;
    $scope.scheduler;
    $scope.schedulers = Scheduler.query();
    $scope.mfilter = "";
    $scope.allSchedulers = $scope.schedulers;
    listSchedulers = function() {
      console.log("list scheduler");
      return Scheduler.query(function(schedulers) {
        $scope.allSchedulers = schedulers;
        return $scope.schedulers = searchScheduler(schedulers);
      });
    };
    searchScheduler = function(schedulers) {
      var s, _i, _len, _results;
      _results = [];
      for (_i = 0, _len = schedulers.length; _i < _len; _i++) {
        s = schedulers[_i];
        if ((s.applicationName + s.asName + s.serverName).match($scope.mfilter)) {
          _results.push(s);
        }
      }
      return _results;
    };
    $scope.addScheduler = function() {
      console.log("addScheduler");
      return Scheduler.save($scope.scheduler, function() {
        listSchedulers();
        return applicationsService.displaySuccessMessage("Scheduler successfully created");
      }, function() {
        return applicationsService.displayErrorMessage("ERROR, Scheduler unsuccessfully created");
      });
    };
    $scope.applyFilter = function() {
      return $scope.schedulers = searchScheduler($scope.allSchedulers, $scope.mfilter);
    };
    $scope.refresh = function() {
      return listSchedulers();
    };
    $scope.initScheduler = function(scheduler) {
      var shed;
      console.log("initScheduler");
      return shed = Scheduler.get({
        schedulerId: scheduler.schedulerId
      }, function() {
        $scope.scheduler = scheduler;
        $('#titleEdit').click();
        $('#updateScheduler').show();
        return $('#addScheduler').hide();
      });
    };
    $scope.cancel = function() {
      $scope.scheduler = null;
      listSchedulers();
      $('#reset').click();
      $('#titleList').click();
      $('#updateScheduler').hide();
      return $('#addScheduler').show();
    };
    $scope.deleteScheduler = function(scheduler) {
      console.log("deleteScheduler");
      return scheduler.$remove({
        schedulerId: scheduler.schedulerId
      }, function() {
        listSchedulers();
        return applicationsService.displaySuccessMessage("Scheduler successfully deleted");
      }, function() {
        return applicationsService.displayErrorMessage("ERROR, Scheduler unsuccessfully deleted");
      });
    };
    $scope.updateScheduler = function() {
      console.log("updateScheduler");
      return $scope.scheduler.$save({
        schedulerId: $scope.scheduler.schedulerId
      }, function() {
        listSchedulers();
        applicationsService.displaySuccessMessage("Scheduler successfully updated");
        $('#reset').click();
        $('#titleList').click();
        $('#updateScheduler').hide();
        return $('#addScheduler').show();
      }, function() {
        return applicationsService.displayErrorMessage("ERROR, Scheduler unsuccessfully updated");
      });
    };
    return $scope.changeStatus = function(scheduler) {
      console.log("changeStatus");
      if (scheduler.state === 'running') {
        return scheduler.$stop({}, function() {
          listSchedulers();
          return applicationsService.displaySuccessMessage("Scheduler successfully stopped");
        }, function() {
          return applicationsService.displayErrorMessage("ERROR Scheduler UNsuccessfully stopped");
        });
      } else {
        return scheduler.$start({}, function() {
          listSchedulers();
          return applicationsService.displaySuccessMessage("Scheduler successfully started");
        }, function() {
          return applicationsService.displayErrorMessage("ERROR Scheduler UNsuccessfully started");
        });
      }
    };
  };

}).call(this);
