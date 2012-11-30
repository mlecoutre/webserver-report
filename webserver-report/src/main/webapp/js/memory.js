(function ($) {
    /** DOCUMENT READY - INITIALIZATION **/
    $(document).ready(function () {
        chart = initChart();
    });

    function initASs(applicationName) {
        var reqDS = $.ajax({
            type: 'GET',
            contentType: 'application/json',
            url: '/report/services/MonitorConfig/ass/' + applicationName
        });
        reqDS.done(function (ass) {
            $('#ass').html(Mustache.to_html($('#ass-template').html(), ass));
        });
    }

    function initServers(applicationName) {
        var reqDS = $.ajax({
            type: 'GET',
            contentType: 'application/json',
            url: '/report/services/MonitorConfig/servers/' + applicationName
        });
        reqDS.done(function (servers) {
            $('#servers').html(Mustache.to_html($('#servers-template').html(), servers));
        });
    }

    $("#selApplicationName").focusout(function () {
        if (applicationName !== null && applicationName != "") {
            var applicationName = $('#selApplicationName').val();
            initServers(applicationName);
            initASs(applicationName);
        }
    });

    $('#display').click(function () {
        var applicationName = $('#selApplicationName').val();
        var as = $('#selAS').val();
        var server = $('#selServer').val();
        addMemoryChart(chart, 'free-memory', applicationName, server, as);
        addMemoryChart(chart, 'available-memory', applicationName, server, as);
        addMemoryChart(chart, 'total-memory', applicationName, server, as);
        addMemoryChart(chart, 'max-memory', applicationName, server, as);
    });

    $('#clear').click(function () {
        while (chart.series.length > 0)
        chart.series[0].remove(true);
    });

    function initChart() {
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'memoryContainer'
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
                formatter: function () {

                    return $.datepicker.formatDate('yy-mm-dd', (new Date(this.x))) + ' : ' + this.y;
                    //return this.y + "Mb";
                }
            },
            series: []
        });
        return chart;
    }

    function addMemoryChart(chart, type, applicationName, server, as) {
        var reqA = $.ajax({
            type: 'GET',
            contentType: 'application/json',
            url: '/report/monitor?action=' + type + '&applicationName=' + applicationName + '&server=' + server + '&as=' + as
        });
        reqA.done(function (mem) {
            chart.addSeries({
                type: 'spline',
                name: type,
                data: mem
            });
        })
    }

})(jQuery);