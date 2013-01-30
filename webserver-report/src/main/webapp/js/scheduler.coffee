# Scheduler controller
#http://docs.angularjs.org/api/ngResource.$resource
window.SchedulerCtrl = ($scope, Scheduler, applicationsService) ->

    $scope.scheduler
    $scope.schedulers  =  Scheduler.query() # applicationsService.displayed schedulers once filter is applied
    $scope.mfilter = ""
    $scope.allSchedulers = $scope.schedulers

    listSchedulers = () ->
        console.log("list scheduler")
        Scheduler.query(
            (schedulers) ->
                $scope.allSchedulers = schedulers
                $scope.schedulers = searchScheduler(schedulers)
        )

    searchScheduler = (schedulers) ->
        s for s in  schedulers when (s.applicationName + s.asName + s.serverName).match ($scope.mfilter)

    $scope.addScheduler = () ->
        console.log("addScheduler")
        Scheduler.save($scope.scheduler,
            () ->
                listSchedulers()
                applicationsService.displaySuccessMessage("Scheduler successfully created")
        ,   () ->
            applicationsService.displayErrorMessage("ERROR, Scheduler unsuccessfully created")
        )

    $scope.applyFilter = () ->  
         $scope.schedulers = searchScheduler($scope.allSchedulers, $scope.mfilter)

    $scope.refresh = () -> listSchedulers()

    $scope.initScheduler = (scheduler) ->
        console.log("initScheduler")
        shed = Scheduler.get({ schedulerId: scheduler.schedulerId}, 
            () ->
                $scope.scheduler = scheduler
                $('#titleEdit').click()
                $('#updateScheduler').show()
                $('#addScheduler').hide()
        )

    $scope.cancel = () ->
        $scope.scheduler = null
        listSchedulers()
        $('#reset').click()
        $('#titleList').click()
        $('#updateScheduler').hide()
        $('#addScheduler').show()

    $scope.deleteScheduler = (scheduler) ->
        console.log("deleteScheduler")
        scheduler.$remove( {  schedulerId: scheduler.schedulerId }
        , () ->
            listSchedulers()
            applicationsService.displaySuccessMessage("Scheduler successfully deleted")
        , () ->
            applicationsService.displayErrorMessage("ERROR, Scheduler unsuccessfully deleted")
        )

    $scope.updateScheduler = () ->
        console.log("updateScheduler")
        $scope.scheduler.$save( { schedulerId: $scope.scheduler.schedulerId },
         () ->
            listSchedulers()
            applicationsService.displaySuccessMessage("Scheduler successfully updated")
            $('#reset').click()
            $('#titleList').click()
            $('#updateScheduler').hide()
            $('#addScheduler').show()
        , () ->
            applicationsService.displayErrorMessage("ERROR, Scheduler unsuccessfully updated")
        )

    $scope.changeStatus = (scheduler) ->
        console.log("changeStatus")
        if scheduler.state == 'running'
            scheduler.$stop({}, () ->
                listSchedulers()
                applicationsService.displaySuccessMessage("Scheduler successfully stopped")
            , () ->
                applicationsService.displayErrorMessage("ERROR Scheduler UNsuccessfully stopped")
            )
        else 
            scheduler.$start({}, () ->
                listSchedulers()
                applicationsService.displaySuccessMessage("Scheduler successfully started")
            , () ->
                applicationsService.displayErrorMessage("ERROR Scheduler UNsuccessfully started")
            )

    

  