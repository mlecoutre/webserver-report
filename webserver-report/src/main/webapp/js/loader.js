function LoaderCtrl($scope, $http, applicationsService) {
    $scope.files = [];
    $scope.applicationName = "";
    $scope.startDate = 'dd-MM-yyyy';
    $scope.endDate = 'dd-MM-yyyy';

    $scope.applications = applicationsService.retrieveApps();
    $scope.stats;

    $scope.addFile = function () {
        $scope.files.push({
            fileName: $scope.fileText,
            fileDone: false
        });
        $scope.fileText = '';
    };

    //button load, that trigger the batch insert
    $scope.load = function () {

        var promise = applicationsService.batchInsert($scope.applicationName, $scope.files);
        promise.then(function(data){
            displaySuccessMessage("Files loaded successfully: "+data);
              $scope.files = [];
        }, function(reason){
             displayErrorMessage("Error loading files: "+ reason);
        });
    }

   $scope.reset = function(){
         $scope.files = [];
         $scope.applicationName = "";
   }

    $scope.initStats = function () {
         var promise = applicationsService.getStats($scope.applicationName);
         promise.then(function(data){
            $scope.stats = data;
         }, function(reason){
            displayErrorMessage("Error loading files: "+ reason);
         });
    }

    $scope.purge = function () {
        bootbox.confirm("Are you sure?", function (confirmed) {
            if (confirmed) {
                console.log("Purge data for application " + $scope.applicationName);
                result = applicationsService.purge($scope.applicationName);
                var promise = applicationsService.batchInsert($scope.applicationName, $scope.files);
                promise.then(function(data){
                          displaySuccessMessage("Application deleted successfully: "+data);
                }, function(reason){
                           displayErrorMessage("Error deleting application: "+ reason);
                });
            } else {
                console.log("Purge cancelled");
            }
        });
    }

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
}


