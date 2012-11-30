function LoaderCtrl($scope, $http) {
    $scope.files = [];
    $scope.applicationName = "";
    $scope.nbElements = 0;
    $scope.startDate = 'dd-MM-yyyy';
    $scope.endDate = 'dd-MM-yyyy';


    $scope.addFile = function () {
        $scope.files.push({
            fileName: $scope.fileText,
            fileDone: false
        });
        $scope.fileText = '';
    };

    $scope.load = function () {
        console.log("load");
        $http({
            method: 'POST',
            url: '/report/services/MonitorConfig/' + $scope.applicationName,
            data: $scope.files
        })
            .success(function (data, status, headers, config) {
            console.log("success");
            $('#info').html(data);
            $('#info').show();
        })
            .error(function (data, status, headers, config) {
            $('#info').html(data);
            $('#info').toggleClass('alert-success');
            $('#info').show();
            console.log("error");
        });
    }

    $scope.stats = function () {
        console.log("get stats for " + $scope.applicationName);
        $http({
            method: 'GET',
            url: '/report/services/MonitorConfig/stats/' + $scope.applicationName
        })
            .success(function (data, status, headers, config) {
            console.log("success");
            $scope.nbElements = data.nbElements;
            /* TODO RETRIEVE OTHERS STATS*/
            $('#dataSources').html(Mustache.to_html($('#dataSources-template').html(), data));
            $('#ass').html(Mustache.to_html($('#ass-template').html(), data));
            $('#servers').html(Mustache.to_html($('#servers-template').html(), data));

        })
            .error(function (data, status, headers, config) {
            console.log("error");
            $('#info').html("Error trying to retrieve stats data");
            $('#info').toggleClass('alert-success');
            $('#info').show();
        });
    }

    $scope.purge = function () {
        bootbox.confirm("Are you sure?", function (confirmed) {
            if (confirmed) {
                console.log("Purge data for application " + $scope.applicationName);
                $http({
                    method: 'GET',
                    url: '/report/services/MonitorConfig/purge/' + $scope.applicationName
                })
                    .success(function (data, status, headers, config) {
                    console.log("success");
                    $('#info').html("Successfully deleted");
                    $('#info').show();
                })
                    .error(function (data, status, headers, config) {
                    console.log("error");
                    $('#info').html("Error during deletion");
                    $('#info').toggleClass('alert-success');
                    $('#info').show();
                });
            } else {
                console.log("Purge cancelled");
            }
        });
    }


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
}
LoaderCtrl.$inject = ['$scope', '$http'];