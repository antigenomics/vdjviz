/**
 * Created by bvdmitri on 13.11.14.
 */

(function () {
    var app = angular.module('accountPage', []);

    app.factory('data', ['$http', '$rootScope' ,function($http, $rootScope) {

        $rootScope.files = [];
        $rootScope.state = 'accountInformation';
        $rootScope.visualisationInfo = {
            fileIndex: -1,
            tab: 'V-J Usage'
        };

        function updateFilesList() {
            $http({method: 'GET', url: '/api/files'}).success(function(data) {
                $rootScope.files = data.data;
            })
        }

        function changeState(state) {
            $rootScope.state = state;
        }

        function getState() {
            return $rootScope.state;
        }

        function setFile(index) {
            $rootScope.state = 'file';
            $rootScope.visualisationInfo.fileIndex = index;
        }

        function getFileIndex() {
            return $rootScope.visualisationInfo.fileIndex;
        }

        function getVisualisationTab() {
            return $rootScope.visualisationInfo.tab;
        }

        function setVisualisationTab(tab) {
            $rootScope.visualisationInfo.tab = tab;
            updateVisualisationTab();
        }

        function updateVisualisationTab() {
            if ($rootScope.visualisationInfo.fileIndex < 0) {
                return;
            }
            var tab = getVisualisationTab();
            var param = {
                fileName: $rootScope.files[$rootScope.visualisationInfo.fileIndex].fileName,
                type: '',
                place: '.visualisation-results',
                height: 600
            };
            switch (tab) {
                case "V-J Usage":
                    param.type = 'vjusage';
                    param.svg_width = '70%';
                    param.width = 600;
                    getData(vjUsage, param);
                    break;
                case "Spectrotype":
                    param.type = 'spectrotype';
                    getData(spectrotype, param);
                    break;
                case "SpectrotypeV":
                    param.type = 'spectrotypeV';
                    getData(spectrotypeV, param);
                    break;
                case "Kernel Density":
                    param.type = 'kernelDensity';
                    getData(kernelDensity, param);
                    break;
                case "Annotation":
                    param.type = 'annotation';
                    getData(annotationTable, param);
                    break;
                default:
                    break;
            }
        }

        return {
            updateFilesList: updateFilesList,
            changeState: changeState,
            getState: getState,
            setFile: setFile,
            getFileIndex: getFileIndex,
            getVisualisationTab: getVisualisationTab,
            setVisualisationTab: setVisualisationTab,
            updateVisualisationTab: updateVisualisationTab
        }

    }]);

    app.controller('account', ['$rootScope', '$scope', '$http', 'data', function($rootScope, $scope, $http, data) {
        data.updateFilesList();
    }]);

    app.controller('filePanel', ['$scope','$log', 'data', function($scope, $log, data) {

        $scope.setActive = function(state) {
            data.changeState(state);
        };

        $scope.isActive = function(state) {
            return data.getState() === state;
        };

        $scope.setFile = function(index) {
            data.changeState('file');
            data.setFile(index);
            data.updateVisualisationTab();
        };

        $scope.isFile = function(index) {
            return data.getFileIndex() === index && data.getState() === 'file';
        }
    }]);

    app.controller('visualisation', ['$rootScope', '$scope', '$log', 'data', function($rootScope ,$scope, $log, data) {

        $scope.tab = 'V-J Usage';

        $scope.isActive = function (tab) {
            return data.getVisualisationTab() === tab;
        };

        $scope.setActive = function(tab) {
            data.setVisualisationTab(tab);
            $scope.tab = tab;
        };

        $scope.isState = function(state) {
            return data.getState() === state;
        };

    }]);
})();


function getData(handleData, param) {
    console.log(param.fileName);
    console.log(param.type);

    $.ajax({
        url: "/api/data",
        type: "post",
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify({
            "action": "data",
            "fileName": param.fileName,
            "type": param.type
        }),
        success: function (data) {
            if (!data) {
                location.reload();
            }
            switch (data["result"]) {
                case "success" :
                    handleData(data.data, param);
                    break;
                case "error" :
                    console.log("error1");
                    break;
                default :
                    console.log("error2");
                    break;
            }
        },
        error: function () {
            console.log("error3");
        }
    });
}

function spectrotype(data, param) {
    nv.addGraph(function () {
        d3.select(param.place).html("");
        var svg = d3.select(param.place).append("div")
            .attr("id", "chart")
            .append("svg")
            .attr("id", "svgtopng")
            .style("height", param.height + "px")
            .style("overflow", "visible");

        var chart = nv.models.multiBarChart()
                .transitionDuration(350)
                .reduceXTicks(false)   //If 'false', every single x-axis tick label will be rendered.
                .rotateLabels(0)      //Angle to rotate x-axis labels.
                .showControls(false)   //Allow user to switch between 'Grouped' and 'Stacked' mode.
                .showLegend(true)
                .groupSpacing(0.1)    //Distance between each group of bars.
                .height(param.height)
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

function spectrotypeV(data, param) {
    nv.addGraph(function () {
        d3.select(param.place).html("");
        var svg = d3.select(param.place).append("div")
            .style("margin-left", "auto")
            .style("margin-right", "auto")
            .attr("id", "chart")
            .append("svg")
            .attr("id", "svgtopng")
            .style("height", param.height + "px")
            .style("overflow", "visible");

        var chart = nv.models.multiBarChart()
                .transitionDuration(350)
                .reduceXTicks(false)   //If 'false', every single x-axis tick label will be rendered.
                .rotateLabels(0)      //Angle to rotate x-axis labels.
                .showControls(false)   //Allow user to switch between 'Grouped' and 'Stacked' mode.
                .showLegend(true)
                .groupSpacing(0.1)    //Distance between each group of bars.
                .height(param.height)
                .stacked(true)
                .tooltip(function (key, x, y, e, graph) {
                    return '<h3>' + key + '</h3>' +
                        '<p>Length : ' + x + '</p>' +
                        '<p>Frequency : ' + e.series.values[e.pointIndex].y + '</p>';
                })
            ;

        var xValues = [];
        for (var i = 0; i < 100; i++) {
            if (i % 3 == 0) {
                xValues.push(i);
            }
        }

        chart.xAxis
            .tickValues(xValues)
            .tickFormat(d3.format(',f'));

        chart.yAxis
            .tickFormat(function (d) {
                return Math.round(d * 10) + "%"
            });

        svg.datum(data)
            .call(chart);

        nv.utils.windowResize(chart.update);
        return chart;
    });
}

function kernelDensity(data, param) {
    nv.addGraph(function () {

        d3.select(param.place).html("");

        var svg = d3.select(param.place)
            .append("div")
            .attr("id", "chart")
            .append("svg")
            .attr("id", "svgtopng")
            .style("height", param.height + "px");

        var chart = nv.models.lineChart()
            .useInteractiveGuideline(false)
            .transitionDuration(350)
            .showLegend(true)
            .showYAxis(true)
            .showXAxis(true)
            .height(param.height)
            .xScale(d3.scale.log())
            .yScale(d3.scale.log());

        chart.xAxis
            .axisLabel('Clonotype frequency')
            .tickFormat(d3.format('.2s'));

        chart.yAxis
            .axisLabel('1-CDF')
            .tickFormat(d3.format('.02e'));

        svg.datum(data)
            .call(chart);

        nv.utils.windowResize(function () {
            chart.update()
        });
        return chart;
    });
}

function annotationTable(data, param) {
    d3.select(param.place).html("");
    var svg = d3.select(param.place)
        .append("div")
        .attr("class", "svg");

    var table = svg
            .append("table")
            .attr("id", "annotation_table")
            .attr("class", "table table-striped table-hover"),
        thead = table.append("thead").append("tr");

    thead.append("th").html("Frequency");
    thead.append("th").html("Count");
    thead.append("th").html("CDR3AA");
    thead.append("th").html("J");
    thead.append("th").html("V");

    var column = [
        {"data": "freq"},
        {"data": "count"},
        {"data": "query_cdr3aa"},
        {"data": "query_J"},
        {"data": "query_V"}
    ];

    for (var i = 0; i < data["header"].length; i++) {
        thead.append("th").html(data["header"][i]);
        column.push({"data": data["header"][i]});
    }

    var dataTable = $('#annotation_table').dataTable({
        "data": data["data"],
        "columns": column,
        'iDisplayLength': 10000,
        'order': [
            [0, "decs"]
        ],
        "scrollY": "600px",
        dom: 'T<"clear">frtiS',
        responsive: true,
        tableTools: {
            "sSwfPath": "../../assets/lib/dataTable/extensions/TableTools/swf/copy_csv_xls_pdf.swf"
        },
        "columnDefs": [
            {
                "width": "6%",
                "targets": 0
            },
            {
                "width": "6%",
                "targets": 1
            },
            {
                "render": function (data) {
                    var cdr3aa = data["cdr3aa"];
                    var vend = data["vend"] / 3;
                    var jstart = data["jstart"] / 3;
                    var pos = data["pos"];
                    if (vend >= 0 && jstart >= 0) {
                        var str = [
                            data["cdr3aa"].substring(0, vend + 1),
                            data["cdr3aa"].substring(vend + 1, jstart),
                            data["cdr3aa"].substring(jstart, data["cdr3aa"].length),
                        ];
                        if (pos != -1) {
                            if (pos <= vend) {
                                str[0] = str[0].substring(0, pos) + "<b><u>" + str[0].substring(pos, pos + 1) + "</u></b>" + str[0].substring(pos + 1, str[0].length);
                            } else if (pos > vend && pos < jstart) {
                                str[1] = str[1].substring(0, pos - vend) + "<b><u>" + str[1].substring(pos - vend, pos - vend + 1) + "</u></b>" + str[1].substring(pos - vend + 1, str[1].length);
                            } else if (pos >= jstart) {
                                str[2] = str[2].substring(0, pos - jstart) + "<b><u>" + str[2].substring(pos - jstart, pos - jstart + 1) + "</u></b>" + str[2].substring(pos - jstart + 1, str[2].length);
                            }
                        }
                        str[0] = "<text style='color : #31a354'>" + str[0] + "</text>";
                        str[2] = "<text style='color : #3182bd'>" + str[2] + "</text>";
                        return str.join("")
                    } else {
                        if (pos != -1) {
                            return data["cdr3aa"].substring(0, pos) + "<b><u>" + data["cdr3aa"].substring(pos, pos + 1) + "</u></b>" + data["cdr3aa"].substring(pos + 1, data["cdr3aa"].length)
                        } else {
                            return data["cdr3aa"];
                        }
                    }
                },
                "width": "20%",
                "targets": 2
            },
            {
                "render": function (data) {
                    if (!data["match"]) {
                        return "<div style='color: red;'>" + data["j"] + "</div>";
                    } else {
                        return data["j"];
                    }
                },
                "width": "10%",
                "targets": 3
            },
            {
                "render": function (data) {
                    if (!data["match"]) {
                        return "<div style='color: red;'>" + data["v"] + "</div>";
                    } else {
                        return data["v"];
                    }
                },
                "width": "10%",
                "targets": 4
            }
        ]
    });
}

function vjUsage(vjUsageData, param) {
    var height = param.height, margin = {b: 0, t: 40, l: 170, r: 50};


    d3.select(param.place).html("");
    var place = d3.select(param.place)
        .append("svg")
        .attr("id", "svgtopng")
        .style("display", "block")
        .style("margin", "auto")
        .style("width", param.svg_width)
        .style("overflow", "visible")
        .attr('height', (height + margin.b + margin.t));
    var svg = place.append("g")
        .attr("transform", "translate(" + margin.l + "," + margin.t + ")");
    var bP = createbP(param.height, param.width);
    var data = [
        {data: bP.partData(vjUsageData, 2), id: 'V-J-Usage-' + param["id"], header: ["V", "J"]}
    ];
    bP.draw(data, svg);
}

function createbP(height, bb) {
    var b = 30, buffMargin = 5, minHeight = 5;
    var c1 = [-165, 50], c2 = [-50, 140], c3 = [-10, 200]; //Column positions of labels.
    var colors = d3.scale.category20().range();
    var bP = {};

    bP.partData = function (data, p) {
        var sData = {};

        sData.keys = [
            d3.set(data.map(function (d) {
                return d[0];
            })).values().sort(function (a, b) {
                return ( a < b ? -1 : a > b ? 1 : 0);
            }),
            d3.set(data.map(function (d) {
                return d[1];
            })).values().sort(function (a, b) {
                return ( a < b ? -1 : a > b ? 1 : 0);
            })
        ];

        sData.data = [sData.keys[0].map(function (d) {
            return sData.keys[1].map(function (v) {
                return 0;
            });
        }),
            sData.keys[1].map(function (d) {
                return sData.keys[0].map(function (v) {
                    return 0;
                });
            })
        ];

        data.forEach(function (d) {
            sData.data[0][sData.keys[0].indexOf(d[0])][sData.keys[1].indexOf(d[1])] = d[p];
            sData.data[1][sData.keys[1].indexOf(d[1])][sData.keys[0].indexOf(d[0])] = d[p];
        });

        return sData;
    };

    function visualize(data) {
        var vis = {};

        function calculatePosition(a, s, e, b, m) {
            var total = d3.sum(a);
            var sum = 0, neededHeight = 0, leftoverHeight = e - s - 2 * b * a.length;
            var ret = [];

            a.forEach(
                function (d) {
                    var v = {};
                    v.percent = (total == 0 ? 0 : d / total);
                    v.value = d;
                    v.height = Math.max(v.percent * (e - s - 2 * b * a.length), m);
                    (v.height == m ? leftoverHeight -= m : neededHeight += v.height );
                    ret.push(v);
                }
            );

            var scaleFact = leftoverHeight / Math.max(neededHeight, 1), sum = 0;

            ret.forEach(
                function (d) {
                    d.percent = scaleFact * d.percent;
                    d.height = (d.height == m ? m : d.height * scaleFact);
                    d.middle = sum + b + d.height / 2;
                    d.y = s + d.middle - d.percent * (e - s - 2 * b * a.length) / 2;
                    d.h = d.percent * (e - s - 2 * b * a.length);
                    d.percent = (total == 0 ? 0 : d.value / total);
                    sum += 2 * b + d.height;
                }
            );
            return ret;
        }

        vis.mainBars = [
            calculatePosition(data.data[0].map(function (d) {
                return d3.sum(d);
            }), 0, height, buffMargin, minHeight),
            calculatePosition(data.data[1].map(function (d) {
                return d3.sum(d);
            }), 0, height, buffMargin, minHeight)
        ];

        vis.subBars = [
            [],
            []
        ];
        vis.mainBars.forEach(function (pos, p) {
            pos.forEach(function (bar, i) {
                calculatePosition(data.data[p][i], bar.y, bar.y + bar.h, 0, 0).forEach(function (sBar, j) {
                    sBar.key1 = (p == 0 ? i : j);
                    sBar.key2 = (p == 0 ? j : i);
                    vis.subBars[p].push(sBar);
                });
            });
        });
        vis.subBars.forEach(function (sBar) {
            sBar.sort(function (a, b) {
                return (a.key1 < b.key1 ? -1 : a.key1 > b.key1 ?
                    1 : a.key2 < b.key2 ? -1 : a.key2 > b.key2 ? 1 : 0 )
            });
        });

        vis.edges = vis.subBars[0].map(function (p, i) {
            return {
                key1: p.key1,
                key2: p.key2,
                y1: p.y,
                y2: vis.subBars[1][i].y,
                h1: p.h,
                h2: vis.subBars[1][i].h
            };
        });
        vis.keys = data.keys;
        return vis;
    }

    function arcTween(a) {
        var i = d3.interpolate(this._current, a);
        this._current = i(0);
        return function (t) {
            return edgePolygon(i(t));
        };
    }

    function drawPart(data, id, p) {
        d3.select("#" + id).append("g").attr("class", "part" + p)
            .attr("transform", "translate(" + ( p * (bb + b)) + ",0)");
        d3.select("#" + id).select(".part" + p).append("g").attr("class", "subbars");
        d3.select("#" + id).select(".part" + p).append("g").attr("class", "mainbars");

        var mainbar = d3.select("#" + id).select(".part" + p).select(".mainbars")
            .selectAll(".mainbar").data(data.mainBars[p])
            .enter().append("g").attr("class", "mainbar");

        mainbar.append("rect").attr("class", "mainrect")
            .attr("x", 0).attr("y", function (d) {
                return d.middle - d.height / 2;
            })
            .attr("width", b).attr("height", function (d) {
                return d.height;
            })
            .style("shape-rendering", "auto")
            .style("fill-opacity", 0).style("stroke-width", "0.5")
            .style("stroke", "black").style("stroke-opacity", 0);

        mainbar.append("text").attr("class", "barlabel")
            .attr("x", c1[p]).attr("y", function (d) {
                return d.middle + 5;
            })
            .text(function (d, i) {
                return data.keys[p][i];
            })
            .attr("text-anchor", "start");

        mainbar.append("text").attr("class", "barvalue")
            .attr("x", c2[p]).attr("y", function (d) {
                return d.middle + 5;
            })
            .text(function (d, i) {
                return d.value;
            })
            .attr("text-anchor", "end");

        mainbar.append("text").attr("class", "barpercent")
            .attr("x", c3[p]).attr("y", function (d) {
                return d.middle + 5;
            })
            .text(function (d, i) {
                return "( " + Math.round(100 * d.percent) + "%)";
            })
            .attr("text-anchor", "end").style("fill", "grey");

        d3.select("#" + id).select(".part" + p).select(".subbars")
            .selectAll(".subbar").data(data.subBars[p]).enter()
            .append("rect").attr("class", "subbar")
            .attr("x", 0).attr("y", function (d) {
                return d.y
            })
            .attr("width", b).attr("height", function (d) {
                return d.h
            })
            .style("fill", function (d) {
                return colors[d.key1];
            });
    }

    function drawEdges(data, id) {
        d3.select("#" + id).append("g").attr("class", "edges").attr("transform", "translate(" + b + ",0)");

        d3.select("#" + id).select(".edges").selectAll(".edge")
            .data(data.edges).enter().append("polygon").attr("class", "edge")
            .attr("points", edgePolygon).style("fill", function (d) {
                return colors[d.key1];
            })
            .style("opacity", 0.5).each(function (d) {
                this._current = d;
            });
    }

    function drawHeader(header, id) {
        d3.select("#" + id).append("g").attr("class", "header").append("text").text(header[2])
            .style("font-size", "20").attr("x", 108).attr("y", -20).style("text-anchor", "middle")
            .style("font-weight", "bold");

        [0, 1].forEach(function (d) {
            var h = d3.select("#" + id).select(".part" + d).append("g").attr("class", "header");

            h.append("text").text(header[d]).attr("x", (c1[d] - 5))
                .attr("y", -5).style("fill", "grey");

            h.append("text").text("Count").attr("x", (c2[d] - 10))
                .attr("y", -5).style("fill", "grey");

            h.append("line").attr("x1", c1[d] - 10).attr("y1", -2)
                .attr("x2", c3[d] + 10).attr("y2", -2).style("stroke", "black")
                .style("stroke-width", "1").style("shape-rendering", "crispEdges");
        });
    }

    function edgePolygon(d) {
        return [0, d.y1, bb, d.y2, bb, d.y2 + d.h2, 0, d.y1 + d.h1].join(" ");
    }

    function transitionPart(data, id, p) {
        var mainbar = d3.select("#" + id).select(".part" + p).select(".mainbars")
            .selectAll(".mainbar").data(data.mainBars[p]);

        mainbar.select(".mainrect").transition().duration(500)
            .attr("y", function (d) {
                return d.middle - d.height / 2;
            })
            .attr("height", function (d) {
                return d.height;
            });

        mainbar.select(".barlabel").transition().duration(500)
            .attr("y", function (d) {
                return d.middle + 5;
            });

        mainbar.select(".barvalue").transition().duration(500)
            .attr("y", function (d) {
                return d.middle + 5;
            }).text(function (d, i) {
                return d.value;
            });

        mainbar.select(".barpercent").transition().duration(500)
            .attr("y", function (d) {
                return d.middle + 5;
            })
            .text(function (d, i) {
                return "( " + Math.round(100 * d.percent) + "%)";
            });

        d3.select("#" + id).select(".part" + p).select(".subbars")
            .selectAll(".subbar").data(data.subBars[p])
            .transition().duration(500)
            .attr("y", function (d) {
                return d.y
            }).attr("height", function (d) {
                return d.h
            });
    }

    function transitionEdges(data, id) {
        d3.select("#" + id).append("g").attr("class", "edges")
            .attr("transform", "translate(" + b + ",0)");

        d3.select("#" + id).select(".edges").selectAll(".edge").data(data.edges)
            .transition().duration(500)
            .attrTween("points", arcTween)
            .style("opacity", function (d) {
                return (d.h1 == 0 || d.h2 == 0 ? 0 : 0.5);
            });
    }

    function transition(data, id) {
        transitionPart(data, id, 0);
        transitionPart(data, id, 1);
        transitionEdges(data, id);
    }

    bP.draw = function (data, svg) {
        data.forEach(function (biP, s) {
            svg.append("g")
                .attr("id", biP.id)
                .attr("transform", "translate(" + (550 * s) + ",0)");

            var visData = visualize(biP.data);
            drawPart(visData, biP.id, 0);
            drawPart(visData, biP.id, 1);
            drawEdges(visData, biP.id);
            drawHeader(biP.header, biP.id);

            [0, 1].forEach(function (p) {
                d3.select("#" + biP.id)
                    .select(".part" + p)
                    .select(".mainbars")
                    .selectAll(".mainbar")
                    .on("mouseover", function (d, i) {
                        return bP.selectSegment(data, p, i);
                    })
                    .on("mouseout", function (d, i) {
                        return bP.deSelectSegment(data, p, i);
                    });
            });
        });
    };

    bP.selectSegment = function (data, m, s) {
        data.forEach(function (k) {
            var newdata = {keys: [], data: []};

            newdata.keys = k.data.keys.map(function (d) {
                return d;
            });

            newdata.data[m] = k.data.data[m].map(function (d) {
                return d;
            });

            newdata.data[1 - m] = k.data.data[1 - m]
                .map(function (v) {
                    return v.map(function (d, i) {
                        return (s == i ? d : 0);
                    });
                });

            transition(visualize(newdata), k.id);

            var selectedBar = d3.select("#" + k.id).select(".part" + m).select(".mainbars")
                .selectAll(".mainbar").filter(function (d, i) {
                    return (i == s);
                });

            selectedBar.select(".mainrect").style("stroke-opacity", 1);
            selectedBar.select(".barlabel").style('font-weight', 'bold');
            selectedBar.select(".barvalue").style('font-weight', 'bold');
            selectedBar.select(".barpercent").style('font-weight', 'bold');
        });
    };

    bP.deSelectSegment = function (data, m, s) {
        data.forEach(function (k) {
            transition(visualize(k.data), k.id);

            var selectedBar = d3.select("#" + k.id).select(".part" + m).select(".mainbars")
                .selectAll(".mainbar").filter(function (d, i) {
                    return (i == s);
                });

            selectedBar.select(".mainrect").style("stroke-opacity", 0);
            selectedBar.select(".barlabel").style('font-weight', 'normal');
            selectedBar.select(".barvalue").style('font-weight', 'normal');
            selectedBar.select(".barpercent").style('font-weight', 'normal');
        });
    };

    return bP;
}

