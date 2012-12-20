function JmsCtrl($scope, $http, applicationsService) {

    $scope.applicationName = "";
    $scope.as = "";
    $scope.server = "";
    $scope.qcf = "";

    $scope.startDate = moment().subtract('days', 7).hours(0).minutes(0).seconds(0).toDate(); //
    $scope.endDate = moment().add('days', 1).hours(0).minutes(0).seconds(0).toDate(); //

    $scope.applications = applicationsService.retrieveApps();
    $scope.ass = "";
    $scope.servers = "";
    $scope.qcfs = "";
    $scope.chart = initChart();

    $scope.doAppFocusOut = function () {
        $scope.ass = applicationsService.retrieveASS($scope.applicationName);
        $scope.servers = applicationsService.retrievePhysicalServers($scope.applicationName);
    }

    $scope.showQCF = function () {
        console.log("showQCF");
        $scope.qcfs = applicationsService.retrieveQCFs($scope.applicationName, $scope.server, $scope.as);
        $('#JMSBox').show();
    }

    $scope.display = function () {
        var type = "used-connections";
        console.log("display JMS");
        var strDate ='';
        if ($scope.startDate != null)
            strDate+= '&startDate='+$scope.startDate.getTime()
        if ($scope.endDate != null)
            strDate+= '&endDate=' + $scope.endDate.getTime();
        var reqA = $.ajax({
            type: 'GET',
            contentType: 'application/json',
            url: '/report/monitor?action=' + type + '&applicationName=' + $scope.applicationName + '&server=' + $scope.server + '&as=' + $scope.as + '&idObject=' + $scope.qcf + strDate
        });
        reqA.done(function (qcf) {
            $scope.chart.addSeries({
                type: 'spline',
                name: type,
                data: qcf
            });
        })
    }

    $scope.clear = function () {
        while (chart.series.length > 0)
        chart.series[0].remove(true);
    }

    function initChart() {
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'memoryContainer',
                zoomType: 'x'
            },
            title: 'JMS reports',
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
}