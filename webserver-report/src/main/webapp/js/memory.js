function MemoryCtrl($scope, $http, applicationsService, $cacheFactory) {

    $scope.applicationName;
    $scope.as;
    $scope.server;

    $scope.startDate = moment().subtract('days', 7).hours(0).minutes(0).seconds(0).toDate(); //
    $scope.endDate = moment().add('days', 1).hours(0).minutes(0).seconds(0).toDate(); //
    $scope.ass = "";
    $scope.servers = "";
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


    $scope.doAppFocusOut = function() {
        $scope.ass = "";
        $scope.servers = "";
        $scope.ass = applicationsService.retrieveASS($scope.applicationName);
        $scope.servers = applicationsService.retrievePhysicalServers($scope.applicationName);
    }

    $scope.display = function() {
        localStorage.applicationName = $scope.applicationName;
        localStorage.server = $scope.server;
        localStorage.as = $scope.as;

        addMemoryChart(chart, 'free-memory', $scope.applicationName, $scope.server, $scope.as);
        addMemoryChart(chart, 'available-memory', $scope.applicationName, $scope.server, $scope.as);
        addMemoryChart(chart, 'total-memory', $scope.applicationName, $scope.server, $scope.as);
        addMemoryChart(chart, 'max-memory', $scope.applicationName, $scope.server, $scope.as);
    }

    $scope.clear = function() {
        while(chart.series.length > 0)
        chart.series[0].remove(true);
    }

    function initChart() {
        console.log("Initialize");


        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'memoryContainer',
                zoomType: 'x'
            },
            title: 'Monitoring reports',
            yAxis: {
                title: {
                    text: 'Megabytes'
                }
            },
            plotOptions: {
                spline: {
                    marker: {
                        radius: 0,
                        lineColor: '#666666',
                        lineWidth: 0
                    }
                }
            },
            xAxis: {
                type: 'datetime',
            },
            tooltip: {
                formatter: function() {
                    return moment(this.x).format('LLLL') + ', ' + this.y + ' Mb';
                }
            },
            series: []
        });
        return chart;
    }

    function addMemoryChart(chart, type, applicationName, server, as) {

        var strDate = '';
        if($scope.startDate != null) strDate += '&startDate=' + $scope.startDate.getTime()
        if($scope.endDate != null) strDate += '&endDate=' + $scope.endDate.getTime();

        var reqA = $.ajax({
            type: 'GET',
            contentType: 'application/json',
            url: '/report/monitor?action=' + type + '&applicationName=' + applicationName + '&server=' + server + '&as=' + as + strDate
        });
        reqA.done(function(mem) {
            chart.addSeries({
                type: 'spline',
                name: type,
                data: mem
            });
        })
    }
}