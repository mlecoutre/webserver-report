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
        console.log("load");
        data = applicationsService.batchInsert($scope.applicationName, $scope.files);
         $('#info').html(data);
         $('#info').show();
    }

    $scope.stats = function () {
        $scope.stats = applicationsService.getStats($scope.applicationName);
    }

    $scope.purge = function () {
        bootbox.confirm("Are you sure?", function (confirmed) {
            if (confirmed) {
                console.log("Purge data for application " + $scope.applicationName);
                result = applicationsService.purge($scope.applicationName);
                $('#info').html(result);
                $('#info').show();
            } else {
                console.log("Purge cancelled");
            }
        });
    }
    /*
$scope.remaining = function () {
    var count = 0;
    angular.forEach($scope.files, function (file) {
        count += file.done ? 0 : 1;
    });
    return count;
};

$scope.archive = function () {
    var oldFiles = $scope.files;
    $scope.files = [];
    angular.forEach(oldFiles, function (file) {
        if (!file.done) $scope.files.push(file);
    });
};
*/
}


