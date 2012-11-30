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

    function initDataSourceList() {
        var as = $('#selAS').val();
        var server = $('#selServer').val();
        var applicationName = $('#selApplicationName').val();
        var reqDS = $.ajax({
            type: 'GET',
            contentType: 'application/json',
            url: '/report/services/MonitorConfig/dataSources/' + applicationName + '/' + server + '/' + as
        });
        reqDS.done(function (dataSources) {
            $('#dataSources').html(Mustache.to_html($('#options-template').html(), dataSources));
            $('#dataSourceBox').show();
            $('#showDS').toggleClass('btn-primary');
            $('#display').toggleClass('btn-primary');
        });
    }

    $("#selApplicationName").focusout(function(){
        if(applicationName !== null && applicationName!= ""){
            var applicationName = $('#selApplicationName').val();
            initServers(applicationName);
            initASs(applicationName);
         }
    });

    $('#showDS').click(function () {
        initDataSourceList();
    });

    $('#display').click(function () {
        var as = $('#selAS').val();
        var server = $('#selServer').val();
        var dataSource = $('#selDataSource').val();
        var applicationName = $('#selApplicationName').val();
        addDSChart(chart, 'ds-used-connections', applicationName, server, as, dataSource);
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

    function addDSChart(chart, type, applicationName, server, as, ds) {
        var reqA = $.ajax({
            type: 'GET',
            contentType: 'application/json',
            url: '/report/monitor?action=' + type + '&applicationName=' + applicationName + '&server=' + server + '&as=' + as + '&dataSource=' + ds

        });
        reqA.done(function (mem) {
            chart.addSeries({
                type: 'spline',
                name: ds,
                data: mem
            });
        })
    }

})(jQuery);