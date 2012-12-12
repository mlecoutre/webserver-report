(function ($) {
    /** DOCUMENT READY - INITIALIZATION **/
    $(document).ready(function () {
        chart = initChart();
        initApplication();
    });

        function initApplication() {
            var reqDS = $.ajax({
                type: 'GET',
                contentType: 'application/json',
                url: '/report/services/MonitorConfig/applications'
            });
            reqDS.done(function (applications) {
                $('#applications').html(Mustache.to_html($('#options-template').html(), applications));
            });
        }

    function initASs(applicationName) {
        var reqDS = $.ajax({
            type: 'GET',
            contentType: 'application/json',
            url: '/report/services/MonitorConfig/ass/' + applicationName
        });
        reqDS.done(function (ass) {
            $('#ass').html(Mustache.to_html($('#options-template').html(), ass));
        });
    }

    function initServers(applicationName) {
        var reqDS = $.ajax({
            type: 'GET',
            contentType: 'application/json',
            url: '/report/services/MonitorConfig/servers/' + applicationName
        });
        reqDS.done(function (servers) {
            $('#servers').html(Mustache.to_html($('#options-template').html(), servers));
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

        var as = $('#selAS').val();
        var server = $('#selServer').val();
        var applicationName = $('#selApplicationName').val();
        addSerie(chart, 'threads', applicationName, server, as);

    });

    $('#clear').click(function () {
        while (chart.series.length > 0)
        chart.series[0].remove(true);
    });

    function initChart() {
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'memoryContainer',
                zoomType: 'x'
            },
            title: 'Threads report',
            yAxis: {
                title: {
                    text: 'nb threads'
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
                    return moment(this.x).format('LLLL') + ', ' + this.y +' threads';
                }
            },
            series: []
        });
        return chart;
    }

    function addSerie(chart, type, applicationName, server, as) {
        var reqA = $.ajax({
            type: 'GET',
            contentType: 'application/json',
            url: '/report/monitor?action=' + type + '&applicationName=' + applicationName + '&server=' + server + '&as=' + as
        });
        reqA.done(function (mem) {
            chart.addSeries({
                type: 'spline',
                name: as,
                data: mem
            });
        })
    }

})(jQuery);