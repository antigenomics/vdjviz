/**
 * Created by bvdmitri on 13.11.14.
 */

(function () {
    var app = angular.module('accountPage', []);

    app.controller('accountController', ['$http', '$log', function ($http, $log) {

        var account = this;
        account.files = [];
        account.state = {
            active: -1,
            fileComputationResults: false
        };

        $http({method: 'GET', 'url': 'api/files'}).success(function (data) {
            account.files = data.data;
        });

        account.setActive = function (row) {
            account.state.active = row;
            if (isInteger(row)) {
                account.state.fileComputationResults = true;
                var param = {
                    height: 500,
                    width: 400,
                    svg_width: "100%",
                    place: d3.select(".visualisation-results")
                };
                getData(spectrotype, "spectrotype", account.files[row].fileName, param);
            } else {
                account.state.fileComputationResults = false;
            }
        };

        account.isActive = function (row) {
            return account.state.active === row;
        };

    }]);
})();

function computationResults(type, fileName) {

}

function isInteger(a) {
    return isFinite(a) && 0 == a % 1
}

function spectrotype(data, parameters) {
    nv.addGraph(function () {
        var svg = parameters["place"].append("div")
            .attr("id", "chart")
            .append("svg")
            .attr("id", "svgtopng")
            .style("height", parameters["height"] + "px")
            .style("overflow", "visible");

        var chart = nv.models.multiBarChart()
                .transitionDuration(350)
                .reduceXTicks(false)   //If 'false', every single x-axis tick label will be rendered.
                .rotateLabels(0)      //Angle to rotate x-axis labels.
                .showControls(false)   //Allow user to switch between 'Grouped' and 'Stacked' mode.
                .showLegend(true)
                .groupSpacing(0.1)    //Distance between each group of bars.
                .height(parameters["height"])
                .stacked(true)
                .tooltip(function (key, x, y, e, graph) {
                    if (key != "Other") {
                        if (e.series.values[e.pointIndex].y != 0) {
                            return '<h3>' + e.series.name + '</h3>' +
                                '<p>Length : ' + x + '</p>' +
                                '<p>Frequency : ' + e.series.values[e.pointIndex].y + '</p>' +
                                '<p>CDR3AA : ' + e.series.cdr3aa + '</p>' +
                                '<p>V : ' + e.series.v + '</p>' +
                                '<p>J : ' + e.series.j + '</p>';
                        } else {
                            return null;
                        }

                    } else {
                        return '<h3>Other</h3>' +
                            '<p>Length : ' + x + '</p>' +
                            '<p>Frequency : ' + e.series.values[e.pointIndex].y + '</p>';
                    }
                })
            ;

        var xValues = [];
        for (var i = 1; i < 100; i++) {
            if (i % 3 == 0) {
                xValues.push(i);
            }
        }

        chart.xAxis
            .tickValues(xValues)
            .tickFormat(d3.format(',f'));

        chart.yAxis
            .tickFormat(d3.format('%'));

        svg.datum(data)
            .call(chart);

        nv.utils.windowResize(chart.update);
        return chart;
    });
}

function getData(handleData, type, fileName, parameters) {
    parameters["place"].html("");
    $.ajax({
        url: "/api/data",
        type: "post",
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify({
            "action": "data",
            "fileName": fileName,
            "type": type
        }),
        success: function (data) {
            if (!data) {
                location.reload();
            }
            switch (data["result"]) {
                case "success" :
                    handleData(data["data"], fileName, parameters);
                    break;
                case "error" :
                    break;
                default :
                    window.location.replace("/");
                    break;
            }
        },
        error: function () {
            console.log("asd");
            //location.reload();
        }
    });
}
