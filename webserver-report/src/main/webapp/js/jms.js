function JmsCtrl($scope, $http, applicationsService) {

    $scope.applicationName = "";
    $scope.as = "";
    $scope.server = "";
    $scope.qcf = "";

    $scope.applications = applicationsService.retrieveApps();
    $scope.ass = "";
    $scope.servers = "";
    $scope.qcfs = "";
    $scope.chart = initChart();

    $scope.doAppFocusOut = function () {
        console.log("doAppFocusOut");
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
        var reqA = $.ajax({
            type: 'GET',
            contentType: 'application/json',
            url: '/report/monitor?action=' + type + '&applicationName=' + $scope.applicationName + '&server=' + $scope.server + '&as=' + $scope.as + '&idObject=' + $scope.qcf
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
                renderTo: 'memoryContainer'
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

                    return $.datepicker.formatDate('yy-mm-dd', (new Date(this.x))) + ' : ' + this.y;
                    //return this.y + "Mb";
                }
            },
            series: []
        });
        return chart;
    }
}