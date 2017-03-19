$(document).ready(function() {
    var openObserver = Rx.Observer.create(function(e) {
        console.info('socket open');

        socket.onNext('test');
    });

    var closingObserver = Rx.Observer.create(function() {
        console.log('socket is about to close');
    });

    var socket = Rx.DOM.fromWebSocket(
        'ws://localhost:8787',
        null,
        openObserver,
        closingObserver
    );

    socket.subscribe(
        function(e) {
            console.log('message: %s', e.data);
            feedChart(e.data);
        },
        function(e) {
            // errors and "unclean" closes land here
            console.error('error: %s', e);
        },
        function() {
            // the socket has been closed
            console.info('socket closed');
        }
    );

    var totalPoints = 101;

    var sensors =  [];

    for (var i = 0; i < 9; i++ ) {
        sensors.push({
            sensorData : [],
            sensorPlotData : [],
            sensorPlot : $.plot('#chart-0'+(i+1), [], {
                grid: {
                    borderColor: "#f3f3f3",
                    borderWidth: 1,
                    tickColor: "#f3f3f3"
                },
                series: {
                    shadowSize: 0,
                    color: getChartColor(i)
                },
                lines: {
                    fill: true, //Converts the line chart to area chart
                    color: getChartColor(i)
                },
                yaxis: {
                    min: 0,
                    max: 100
                },
                xaxis: {
                    min: 0,
                    max: 100,
                    show: false
                }
            })
        });
    }

    function feedChart(jsonData) {

        var data = JSON.parse(jsonData);

        for (var i = 0, len = sensors.length; i < len; i++) {

            sensors[i].sensorData.push([data['sensor_'+(i+1)]]);
            if (sensors[i].sensorData.length > totalPoints) {
                sensors[i].sensorData.shift();
            }

            for (var j = 0; j < sensors[i].sensorData.length; j++) {
                sensors[i].sensorPlotData[j] = [j, sensors[i].sensorData[j]];
            }

            sensors[i].sensorPlot.setData([sensors[i].sensorPlotData]);
            sensors[i].sensorPlot.draw();
        }

    }

    function getChartColor(i) {
        switch(i) {
            case 0 :
                return "#3c8dbc";
                break;
            case 1 :
                return "#3cbc8d";
                break;
            case 2 :
                return "#8dbc3c";
                break;
            case 3 :
                return "#8d3cbc";
                break;
            case 4 :
                return "#bc3c8d";
                break;
            case 5 :
                return "#bc8d3c";
                break;
            case 6 :
                return "#bebebe";
                break;
            case 7 :
                return "#aaff00";
                break;
        }
    }
});
