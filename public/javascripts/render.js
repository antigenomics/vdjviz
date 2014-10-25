function renderDiversityData(url) {
    $.getJSON(url, function(data) {
        nv.addGraph(function() {
            var chart = nv.models.lineChart()
                    .margin({left: 100})
                    .useInteractiveGuideline(true)
                    .transitionDuration(350)
                    .showLegend(true)
                    .showYAxis(true)
                    .showXAxis(true)
                    .height(700)
                ;

            chart.xAxis
                .axisLabel('Count')
                .tickFormat(d3.format(',r'));

            chart.yAxis
                .axisLabel('CDR3AA')
                .tickFormat(d3.format('.02f'));

            d3.select('#chart svg')
                .datum(data)
                .call(chart);

            nv.utils.windowResize(function() { chart.update() });
            return chart;
        });
    });
}

function HistogramData(url) {
    d3.select("svg").remove();
    d3.select(".svg").remove();
    $.getJSON(url, function(data){
        nv.addGraph(function() {
            var svg = d3.select(".visualisation")
                .append("div")
                .attr("id", "chart")
                .append("svg");

            var chart = nv.models.multiBarChart()
                    .transitionDuration(350)
                    .reduceXTicks(true)   //If 'false', every single x-axis tick label will be rendered.
                    .rotateLabels(0)      //Angle to rotate x-axis labels.
                    .showControls(false)   //Allow user to switch between 'Grouped' and 'Stacked' mode.
                    .showLegend(true)
                    .groupSpacing(0.1)    //Distance between each group of bars.
                    .height(700)
                    .stacked(true)
                    .tooltip(function(key, x, y, e, graph) {
                        if (key != "Common") {
                            return '<h3>' + e.series.name + '</h3>' +
                                '<p>Length : ' + x + '</p>' +
                                '<p>Frequency : ' + e.series.values[e.pointIndex].y + '</p>' +
                                '<p>CDR3AA : ' + e.series.cdr3aa + '</p>' +
                                '<p>V : ' + e.series.v + '</p>' +
                                '<p>J : ' + e.series.j + '</p>';
                        } else {
                            return '<h3>Common</h3>' +
                                '<p>Length : ' + x + '</p>' +
                                '<p>Frequency : ' + e.series.values[e.pointIndex].y + '</p>';
                        }
                })
                ;

            chart.xAxis
                .tickFormat(d3.format(',f'));

            chart.yAxis
                .tickFormat(d3.format(',.2e'));

            d3.select('#chart svg')
                .datum(data)
                .call(chart);

            nv.utils.windowResize(chart.update);

            return chart;
        });
    });
}

function HistogramVData(url) {
    d3.select("svg").remove();
    d3.select(".svg").remove();
    $.getJSON(url, function(data){
        nv.addGraph(function() {
            var svg = d3.select(".visualisation")
                .append("div")
                .attr("id", "chart")
                .append("svg");

            var chart = nv.models.multiBarChart()
                    .transitionDuration(350)
                    .reduceXTicks(true)   //If 'false', every single x-axis tick label will be rendered.
                    .rotateLabels(0)      //Angle to rotate x-axis labels.
                    .showControls(false)   //Allow user to switch between 'Grouped' and 'Stacked' mode.
                    .showLegend(true)
                    .groupSpacing(0.1)    //Distance between each group of bars.
                    .height(700)
                    .stacked(true)
                    .tooltip(function(key, x, y, e, graph) {
                            return '<h3>' + key + '</h3>' +
                                '<p>Length : ' + x + '</p>' +
                                '<p>Frequency : ' + e.series.values[e.pointIndex].y + '</p>';
                    })
                ;

            chart.xAxis
                .tickFormat(d3.format(',f'));

            chart.yAxis
                .tickFormat(d3.format(',.2e'));

            d3.select('#chart svg')
                .datum(data)
                .call(chart);

            nv.utils.windowResize(chart.update);

            return chart;
        });
    });
}

function VJUsage(url) {
    d3.select("svg").remove();
    d3.select(".svg").remove();
    var vdjUsageData = [];
    $.getJSON(url, function(data){
        $.each(data, function(key, value){
            var item = [value.vSegment, value.jSegment, value.relationNum ];
            vdjUsageData.push(item);

        });
    renderVJUsage(vdjUsageData);
    });
}

function AnnotationTable(url) {
    d3.select("svg").remove();
    d3.select(".svg").remove();
    $.getJSON(url, function(data) {
        var header = Object.keys(data[0]);
        renderAnnotationTable(data, header)
    })
}

function BasicStatsTable(url) {
    d3.select("svg").remove();
    d3.select(".svg").remove();
    $.getJSON(url, function(data) {
        var header = Object.keys(data[0]);
        renderBasicStatsTable(data, header);
    })
}

function renderBasicStatsTable(data, header) {
    var svg = d3.select(".visualisation")
        .append("div")
        .attr("class", "svg");
    var table = svg
            .append("table")
            .attr("id", "basicStatsTable")
            .attr("class", "table table-striped table-hover"),
        thead = table.append("thead").append("tr");
    thead.selectAll("th").data(header).enter()
        .append("th").html(function(d) {return d});
    var column = [];
    for (var i = 0; i < header.length; i++) {
        column.push({"data": header[i.toString()]});
    }


    $('#basicStatsTable').dataTable({
        dom: 'T<"clear">lfrtip',
        tableTools : {
            "sSwfPath": "../assets/javascripts/dataTable/extensions/TableTools/swf/copy_csv_xls_pdf.swf"
        },
        data: data,
        "columnDefs": [
            {
                "width" : "10%",
                "targets" : 0
            },
            {
                "render": function (data, type, row) {
                    return parseFloat(data).toExponential(2);
                },
                "targets": 4
            },
            {
                "render": function (data, type, row) {
                    return parseFloat(data).toExponential(2);
                },
                "targets": 5
            },
            {
                "render": function (data, type, row) {
                    return parseFloat(data).toFixed(2);
                },
                "targets": 6
            },
            {
                "render": function (data, type, row) {
                    return parseFloat(data).toFixed(2);
                },
                "targets": 7
            },
            {
                "render": function (data, type, row) {
                    return parseFloat(data).toExponential(2);
                },
                "targets": 8
            }
        ],
        "columns": column,
        'iDisplayLength': 100,
        'order' : [[ 0, "asc" ]],
        responsive: true
    });
}

function renderAnnotationTable(data, header) {
    var svg = d3.select(".visualisation")
        .append("div")
        .attr("class", "svg");

    var table = svg
            .append("table")
            .attr("id", "annotation_table")
            .attr("class", "table table-striped table-hover"),
        thead = table.append("thead").append("tr");
    thead.selectAll("th").data(header).enter()
        .append("th").html(function(d) {return d;});

    var column = [];
    for (var i = 0; i < header.length; i++) {
        column.push({"data": header[i.toString()]});
    }

    $('#annotation_table').dataTable({
        "data": data,
        "columns": column,
        'iDisplayLength': 100,
        'order': [[ 0 , "decs"]],
        dom: 'T<"clear">lfrtip',
        responsive: true,
        tableTools : {
            "sSwfPath": "../../assets/javascripts/dataTable/extensions/TableTools/swf/copy_csv_xls_pdf.swf"
        },
        "columnDefs" : [
            {
                "width" : "10%",
                "targets" : 0
            }
        ]
    });
}

var bP = {};
function renderVJUsage(vdjUsageData) {
    var height = 600, margin = {b: 0, t: 40, l: 170, r: 50};

    var svg = d3.select(".visualisation")
        .append("svg").attr('height', (height + margin.b + margin.t))
        .append("g").attr("transform", "translate(" + $(".visualisation").width() / 3 + "," + margin.t + ")");
    createbP();
    var data = [
        {data: bP.partData(vdjUsageData, 2), id: 'V-J-Usage', header: ["V", "J", "V-J Usage"]}
    ];
    bP.draw(data, svg);
}

function createbP() {
        var b = 30, bb = $(".visualisation svg").width() / 4, height = 500, buffMargin = 5, minHeight = 5;
        var c1 = [-165, 50], c2 = [-50, 160], c3 = [-10, 250]; //Column positions of labels.
        var colors = d3.scale.category20().range();

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

            sData.data = [    sData.keys[0].map(function (d) {
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
        }

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
        }

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
        }

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
        }
        this.bP = bP;
}