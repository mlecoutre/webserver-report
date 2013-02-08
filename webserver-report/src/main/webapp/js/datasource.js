function DataSourceCtrl($scope, applicationsService) {


    $scope.applicationName = "";
    $scope.as = "";
    $scope.server = "";
    $scope.dataSource = "";

    $scope.startDate = moment().subtract('days', 7).hours(0).minutes(0).seconds(0).toDate(); //
    $scope.endDate = moment().add('days', 1).hours(0).minutes(0).seconds(0).toDate(); //

    $scope.ass = "";
    $scope.servers = "";
    $scope.dataSources;
    $scope.chart = initChart();

    $scope.applications = applicationsService.retrieveApps();
    if(localStorage.applicationName) {
        console.log("initFilterForm - retrieve data from cache");
        $scope.applicationName = localStorage.applicationName;
        $scope.as = localStorage.as;
        $scope.server = localStorage.server;
        $scope.ass = applicationsService.retrieveASS($scope.applicationName);
        $scope.servers = applicationsService.retrievePhysicalServers($scope.applicationName);
    }

    $scope.doAppFocusOut = function () {
        $scope.ass = "";
        $scope.servers = "";
        $scope.ass = applicationsService.retrieveASS($scope.applicationName);
        $scope.servers = applicationsService.retrievePhysicalServers($scope.applicationName);
    }

    $scope.showDS = function () {
        console.log("showDS");
        $scope.dataSources = applicationsService.retrieveDataSources($scope.applicationName, $scope.server, $scope.as);
        $('#dataSourceBox').show();
    };

  $scope.display = function () {
        localStorage.applicationName = $scope.applicationName;
        localStorage.server = $scope.server;
        localStorage.as = $scope.as;
        /*
        var as = $('#selAS').val();
        var server = $('#selServer').val();
        var dataSource = $('#selDataSource').val();
        var applicationName = $('#selApplicationName').val();
        */
        addDSChart(chart, 'used-connections', $scope.applicationName, $scope.server, $scope.as, $scope.dataSource);
    };

    $scope.clear = function () {
        while (chart.series.length > 0)
        chart.series[0].remove(true);
    };

    function initChart() {
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'memoryContainer',
                zoomType: 'x'
            },
            title: 'Monitoring reports',
            yAxis: {
                title: {
                    text: 'Nb opened connections'
                }
            },
            plotOptions: {
                spline: {
                    marker: {
                        radius: 0,
                        lineWidth: 0
                    }
                }
            },
            xAxis: {
                type: 'datetime',
            },
            tooltip: {
                formatter: function () {
                    return moment(this.x).format('LLLL') + ', ' + this.y +' connections';
                }
            },
            series: []
        });
        return chart;
    }

    function addDSChart(chart, type, applicationName, server, as, ds) {
        var strDate ='';
        if ($scope.startDate != null)
            strDate+= '&startDate='+$scope.startDate.getTime()
        if ($scope.endDate != null)
            strDate+= '&endDate=' + $scope.endDate.getTime();
        var reqA = $.ajax({
            type: 'GET',
            contentType: 'application/json',
            url: '/report/monitor?action=' + type + '&applicationName=' + applicationName + '&server=' + server + '&as=' + as + '&idObject=' + ds + strDate

        });
        reqA.done(function (mem) {
            chart.addSeries({
                type: 'spline',
                name: ds,
                data: mem
            });
        })
    }

}