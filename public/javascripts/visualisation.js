function spectratype(data, param) {
    "use strict";
    nv.addGraph(function () {

        var place = d3.select(param.place);
        place.html("");
        var width = place.style('width');
        var height = param.height;
        var svg = place.append("div")
            .attr("id", "chart")
            .append("svg")
            .attr("id", "svg_spectratype_" + param.id)
            .style("height", height)
            .style("width", width)
            .attr('height', height) //fix for Firefox browser
            .attr('width', width) // fix for Firefox browser
            .style("overflow", "visible");

        var legend = nv.models.legend()
            .key(function(d) {
                return d['cdr3aa'] ? d['cdr3aa'] : 'Other';
            })
            .oneColumn(true)
            .margin({right: 100, top: 10})
            .rightAlign(true);


        var chart = nv.models.multiBarChart()
                .duration(1000)
                .reduceXTicks(false)   //If 'false', every single x-axis tick label will be rendered.
                .rotateLabels(0)      //Angle to rotate x-axis labels.
                .showControls(false)   //Allow user to switch between 'Grouped' and 'Stacked' mode.
                .showLegend(true)
                .groupSpacing(0.1)    //Distance between each group of bars.
                .height(height)
                .legend(legend)
                .stacked(true)
                .legendOnChart(true)
                .tooltip(function (key, x, y, e) {
                    if (key !== "Other") {
                        if (e.series.values[e.pointIndex].y !== 0) {
                            return '<h3>CDR3AA: ' + e.series.cdr3aa + '</h3>' +
                                '<p>Top :' + e.series.key + '</p>' +
                                '<p>Length : ' + x + '</p>' +
                                '<p>Frequency : ' + e.series.values[e.pointIndex].y + '</p>' +
                                '<p>CDR3NT : ' + e.series.name + '</p>' +
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
        for (var i = data.xMin; i <= data.xMax; i++) {
            if (i % 3 === 0) {
                xValues.push(i);
            }
        }

        chart.xAxis
            .axisLabel('CDR3 length')
            .tickValues(xValues)
            .tickFormat(d3.format(',f'));

        chart.yAxis
            .tickFormat(d3.format('%'));

        svg.datum(data.chart).call(chart);

        nv.utils.windowResize(chart.update);
        return chart;
    });
}

function spectratypeV(data, param) {
    console.log(data);
    "use strict";
    nv.addGraph(function () {
        var place = d3.select(param.place);
        place.html("");
        var width = place.style('width');
        var height = param.height;
        var svg = place.append("div")
            .style("margin-left", "auto")
            .style("margin-right", "auto")
            .attr("id", "chart")
            .append("svg")
            .attr("id", "svg_spectratypeV_" + param.id)
            .style("height", height)
            .style("width", width)
            .attr('height', height) //fix for Firefox browser
            .attr('width', width)   // fix for Firefox browser
            .style("overflow", "visible");

        var chart = nv.models.multiBarChart()
                .duration(1000)
                .reduceXTicks(false)   //If 'false', every single x-axis tick label will be rendered.
                .rotateLabels(0)      //Angle to rotate x-axis labels.
                .showControls(false)   //Allow user to switch between 'Grouped' and 'Stacked' mode.
                .showLegend(true)
                .groupSpacing(0.1)    //Distance between each group of bars.
                .height(height)
                .stacked(true)
                .tooltip(function (key, x, y, e) {
                    if (e.series.values[e.pointIndex].y !== 0) {
                        return '<h3>' + key + '</h3>' +
                            '<p>Length : ' + x + '</p>' +
                            '<p>Frequency : ' + e.series.values[e.pointIndex].y + '</p>';
                    } else {
                        return null;
                    }
                })
            ;

        var xValues = [];
        for (var i = data.xMin; i <= data.xMax; i++) {
            if (i % 3 === 0) {
                xValues.push(i);
            }
        }

        chart.xAxis
            .axisLabel('CDR3 length')
            .tickValues(xValues)
            .tickFormat(d3.format(',f'));

        chart.yAxis
            .tickFormat(function (d) {
                return Math.round(d * 100) / 10 + "%";
            });

        svg.datum(data.chart)
            .call(chart);

        nv.utils.windowResize(chart.update);
        return chart;
    });
}

function quantileSunbirstChart(data, param) {
    nv.addGraph(function () {

        var width = param.width,
            height = param.height,
            radius = Math.min(width, height) / 3,
            padding = 5;

        var x = d3.scale.linear()
            .range([0, 2 * Math.PI]);

        var y = d3.scale.sqrt()
            .range([0, radius]);

        var place = d3.select(param.place);
        place.html("");

        var legendSvg = place
            .append("div")
            .attr("class", "pull-right")
            .style("position", "absolute")
            .style("right", "0px")
            .style("margin-top", "25px");

        var chart = nv.models.legend()
            .width(300)
            .height(20)
            .key(function(d) {
                return d.label;
            })
            .margin({top: 0, left: 0, right: 0, bottom: 0})

        var keys = [
            {key: "Singleton", color: "#9e9ac8", label: "Singleton"},
            {key: "Doubleton", color: "#bcbddc", label: "Doubleton"},
            {key: "HighOrder", color: "#9ebcda", label: "High Order"},
            {key: "Q5", color: "#2171b5", label: "Quantile #5"},
            {key: "Q4", color: "#4292c6", label: "Quantile #4"},
            {key: "Q3", color: "#6baed6", label: "Quantile #3"},
            {key: "Q2", color: "#9ecae1", label: "Quantile #2"},
            {key: "Q1", color: "#c6dbef", label: "Quantile #1"}
        ];

        var svg = place
            .append("svg")
            .attr("id", "svg_quantileStats_" + param.id)
            .attr("class", "sunbirst")
            .style("display", "block")
            .style("overflow", "visible")
            .style("margin", "auto")
            .attr("width", width)
            .attr("height", height)
            .append("g")
            .attr("transform", "translate(" + width / 2 + "," + (height / 2 + 10) + ")");

        var partition = d3.layout.partition()
            .sort(null)
            .value(function (d) {
                return d.size;
            });

        var arc = d3.svg.arc()
            .startAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x))); })
            .endAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x + d.dx))); })
            .innerRadius(function(d) { return Math.max(0, d.y ? y(d.y) : d.y); })
            .outerRadius(function(d) { return Math.max(0, y(d.y + d.dy)) + 50; });

        var nodes = partition.nodes(data);

        var colors = ["#a50026", "#d73027", "#f46d43", "#fdae61", "#fee090", "#ffffbf", "#74add1", "#abd9e9", "#e0f3f8", "#bababa", "#DCDCDC"];
        var topCount = 0;

        var path = svg.selectAll("path")
            .data(nodes)
            .enter().append("path")
            .attr("id", function(d, i) { return "path-" + i; })
            .attr("d", arc)
            .style("fill", function(d) {
                var name = d.name;
                if (name === 'data') return '#ffffff';
                if (name === 'Other') return '#dcdcdc';
                var found = false;
                var color = "#ffffff";
                keys.forEach(function(d) {
                    if (d.key === name) { color = d.color; found = true; }
                });
                if (found) return color;
                return colors[colors.length - (topCount++ % colors.length) - 2];
            })
            .style("cursor", function(d) {
                if (d.children !== null) {
                    return "pointer";
                }
                return null;
            })
            .on("click", click);

        var cloneCount = 10;
        var text = svg.selectAll("text").data(nodes);
        var textEnter = text.enter().append("text")
            .style("fill-opacity", 1)
            .style("fill", function() {
                return "black";
                //return brightness(d3.rgb(colour(d))) < 125 ? "#eee" : "#000";
            })
            .style("font-size", function(d) {
                if (d.clonotype) {
                    var fontSize = 15 - Math.round(cloneCount / 2);
                    cloneCount--;
                    return fontSize + "px"
                }
                return "12px";
            })
            .attr("text-anchor", function(d) {
                return x(d.x + d.dx / 2) > Math.PI ? "end" : "start";
            })
            .attr("dy", ".2em")
            .attr("transform", function(d) {
                var multiline = (d.name || "").split(" ").length > 1,
                    angle = x(d.x + d.dx / 2) * 180 / Math.PI - 90,
                    rotate = angle + (multiline ? -.5 : 0);
                return "rotate(" + rotate + ")translate(" + (y(d.y) + padding) + ")rotate(" + (angle > 90 ? -180 : 0) + ")";
            })
            .on("click", click);
        textEnter.append("tspan")
            .attr("x", 0)
            .text(function(d) {
                if (d.name === "data") return null;
                var label = d.name;
                if (d.size !== 0) {
                    label += "  " + (d.size.toPrecision(2) * 100).toPrecision(2) + "%";
                }
                return label;
            });


        legendSvg
            .append("svg")
            .attr("width", 300)
            .attr("height", 20)
            .style("overflow", "visible")
            .datum(keys).call(chart);

        function click(d) {
            if (d.children) {
                path.transition()
                    .duration(500)
                    .attrTween("d", arcTween(d));
                text.style("visibility", function(e) {
                    return isParentOf(d, e) ? null : d3.select(this).style("visibility");
                })
                    .transition()
                    .duration(500)
                    .attrTween("text-anchor", function(d) {
                        return function() {
                            return x(d.x + d.dx / 2) > Math.PI ? "end" : "start";
                        };
                    })
                    .attrTween("transform", function(d) {
                        var multiline = (d.name || "").split(" ").length > 1;
                        return function() {
                            var angle = x(d.x + d.dx / 2) * 180 / Math.PI - 90,
                                rotate = angle + (multiline ? -.5 : 0);
                            return "rotate(" + rotate + ")translate(" + (y(d.y) + padding) + ")rotate(" + (angle > 90 ? -180 : 0) + ")";
                        };
                    })
                    .style("fill-opacity", function(e) { return isParentOf(d, e) ? 1 : 0; })
                    .each("end", function(e) {
                        d3.select(this).style("visibility", isParentOf(d, e) ? "visible" : "hidden");
                    });
            }
        }

        function isParentOf(p, c) {
            if (p == c) return true;
            if (p.children != null) {
                return p.children.some(function(d) {
                    return isParentOf(d, c);
                });
            }
            return false;
        }

        d3.select(self.frameElement).style("height", height + "px");

        // Interpolate the scales!
        function arcTween(d) {
            var xd = d3.interpolate(x.domain(), [d.x, d.x + d.dx]),
                yd = d3.interpolate(y.domain(), [d.y, 1]),
                yr = d3.interpolate(y.range(), [d.y ? 20 : 0, radius]);
            return function (d, i) {
                return i ? function (t) {
                    return arc(d);
                }
                    : function (t) {
                    x.domain(xd(t));
                    y.domain(yd(t)).range(yr(t));
                    return arc(d);
                };
            };
        }

    });
}

function vjUsage(data, param) {
    "use strict";
    var fill = function(i) {
        return data.colors[i % data.colors.length];
    };
    // Visualize
    var chord = d3.layout.chord()
        .padding(.03)
        .matrix(data.matrix)
        .sortSubgroups(d3.ascending);

    var width = param.width,
        height = param.height,
        r1 = height / 1.7,
        innerRadius = Math.min(width, height) * .49,
        outerRadius = innerRadius * 1.1;

    var svg = d3.select(param.place)
        .append("svg")
        .attr("class", "vjusage")
        .style("width", width + 200)
        .style("height", height + 200)
        .attr("width", width + 200)     //fix for Firefox browser
        .attr("height", height + 200)   //fix for Firefox browser
        .style("display", "block")
        .style("margin", "auto")
        .attr("id", "svg_vjusage_" + param.id)
        .append("g")
        .attr("transform", "translate(" + (width + 200) / 2 + "," + (height + 200) / 2 + ")");

    svg.append("g").selectAll("path")
        .data(chord.groups)
        .enter().append("path")
        .attr("class", "arc")
        .style("fill", function (d) {
            return fill(d.index);
        })
        .attr("d", d3.svg.arc().innerRadius(innerRadius).outerRadius(outerRadius))
        .on("mouseover", fade(.1))
        .on("mouseout", fade(.7));

    svg.append("g")
        .attr("class", "chord")
        .selectAll("path")
        .data(chord.chords)
        .enter().append("path")
        .attr("d", d3.svg.chord().radius(innerRadius))
        .style("fill", function (d) {
            return fill(d.target.index);
        })
        .style("opacity", 0.7);

    svg.append("g").selectAll(".arc")
        .data(chord.groups)
        .enter().append("svg:text")
        .attr("dy", ".35em")
        .attr("text-anchor", function (d) {
            return ((d.startAngle + d.endAngle) / 2) > Math.PI ? "end" : null;
        })
        .attr("transform", function (d) {
            return "rotate(" + (((d.startAngle + d.endAngle) / 2) * 180 / Math.PI - 90) + ")" +
                "translate(" + (r1 - 15) + ")" +
                (((d.startAngle + d.endAngle) / 2) > Math.PI ? "rotate(180)" : "");
        })
        .text(function (d) {
            return data.labels[d.index];
        });

    // Returns an event handler for fading a given chord group.
    function fade(opacity) {
        return function (g, i) {
            svg.selectAll(".chord path")
                .filter(function (d) {
                    return d.source.index !== i && d.target.index !== i;
                })
                .transition()
                .style("opacity", opacity);
        };
    }
}

function rarefactionPlot(data, param) {
    "use strict";
    nv.addGraph(function () {
        var place = d3.select(param.place);
        place.html(""); //cleanup old chart

        var width = $(document).width() - 350;

        var svg = d3.select(param.place)
            .append("svg")
            .attr("id", "rarefaction-png-export")
            .style("height", "900px")
            .style("width", width);

        var chart = nv.models.lineRarefactionChart()
            .useInteractiveGuideline(true)
            .duration(500)
            .showLegend(false)
            .showYAxis(true)
            .showXAxis(true)
            .height(800);

        chart.xAxis
            .showMaxMin(false)
            .axisLabel('Sample size')
            .tickFormat(d3.format(',r'));

        chart.yAxis
            .axisLabel('Diversity, clonotypes')
            .tickFormat(d3.format('d'));


        svg.datum(data)
            .call(chart);

        return chart;
    });
}

function joinHeapMap(data) {
    "use strict";
    var margin = { top: 0, right: 10, bottom: 50, left: 170 },
        col_number = data.colLabel.length,
        row_number = data.rowLabel.length,
        cellSize = 15,
        width = cellSize * col_number,
        height = cellSize * row_number,
        legendElementWidth = cellSize * 1.5,
        colors = [
            "#ffffff",
            "#f7fcf0",
            "#e0f3db",
            "#ccebc5",
            "#a8ddb5",
            "#7bccc4",
            "#4eb3d3",
            "#2b8cbe",
            "#0868ac",
            "#084081",
            "black"
        ],
        rowLabel = data.rowLabel.map(function(el) {
            return cdrTransform(el);
        }),
        colLabel = data.colLabel,
        numLabel = data.rowLabel.map(function() {
            return 0;
        });

    var colorScale = d3.scale.quantile()
        .domain([-10 , 0])
        .range(colors);

    var place = d3.select(".joinHeapMap");
    place.html("");

    if (rowLabel.length === 0) {
        place.append("text").text("Not found");
        return;
    }

    var svg = place.append("svg")
            .attr("width", "100%")
            .attr("height", height + margin.top + margin.bottom + 300)
            .append("g")
            .style("overflow", "visible")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")")
        ;



    svg.append("g")
        .append("text")
        .attr("class", "mono")
        .attr("x", 0)
        .attr("y", 50)
        .html("Log10 clonotype frequency");

    var legend = svg
        .selectAll(".legend")
        .data([-10,-9,-8,-7,-6,-5,-4,-3,-2,-1,0])
        .enter().append("g")
        .attr("class", "legend");

    legend.append("rect")
        .attr("x", function(d, i) { return legendElementWidth * i; })
        .attr("y", 0)
        .attr("width", legendElementWidth)
        .attr("height", cellSize)
        .style("fill", function(d, i) { return colors[i]; });

    legend.append("text")
        .attr("class", "mono")
        .text(function(d) { return d; })
        .attr("width", legendElementWidth)
        .attr("x", function(d, i) { return legendElementWidth * i; })
        .attr("y", (cellSize*2));

    var rowLabels = svg.append("g")
            .selectAll(".rowLabelg")
            .data(rowLabel)
            .enter()
            .append("text")
            .html(function (d) {
                return d.cdr3aa_tspan;
            })
            .attr("x", 0)
            .attr("y", function (d, i) { return i * cellSize + 200; })
            .style("text-anchor", "end")
            .attr("transform", "translate(-6," + cellSize / 1.5 + ")")
            .attr("class", function (d,i) { return "rowLabel mono r"+i;} )
        ;


    var colLabels = svg.append("g")
            .selectAll(".colLabelg")
            .data(colLabel)
            .enter()
            .append("text")
            .text(function (d) { return d; })
            .attr("x", -200)
            .attr("y", function (d, i) { return i * cellSize; })
            .style("text-anchor", "left")
            .attr("transform", "translate("+cellSize/2 + ",-6) rotate(-90)")
            .attr("class",  function (d,i) { return "colLabel mono c"+i;} )
        ;


    function stopEvent() {
        d3.event.preventDefault();
        d3.event.sourceEvent.stopPropagation();
        d3.event.preventDefault();
        d3.event.stopPropagation();
        d3.event.stopImmediatePropagation();
    }

    var heatMap = svg
        .append("g")
        .attr("class", "cell_heat_map g3")
        .selectAll(".heatmap")
        .data(data.values, function(d) {
            var row = d.row;
            if (d.value != -10) {
                numLabel[row - 1] += 1;
            }
            return d.row + ':' + d.col;
        })
        .enter()
        .append("svg:rect")
        .attr("class", "cell_heat_map cell cell-border")
        .attr("x", function(d) { return (d.col - 1) * cellSize; })
        .attr("y", function(d) { return (d.row - 1) * cellSize + 200; })
        .attr("width", function(d) { return cellSize; })
        .attr("height", function(d) { return cellSize; })
        .style("fill", function(d) { return colorScale(d.value); })
        .on("mousedown", stopEvent)
        .on("click", stopEvent)
        .on("mouseover", function(d){
            //highlight text
            d3.select(this).classed("cell-hover",true);
            d3.selectAll(".rowLabel").classed("text-highlight",function(r,ri){ return ri==(d.row-1);});
            d3.selectAll(".colLabel").classed("text-highlight",function(c,ci){ return ci==(d.col-1);});

            //Update the tooltip position and value
            d3.select("#heatmap_tooltip")
                .style("left", (d3.event.pageX - 250) + "px")
                .style("top", (d3.event.pageY - 250) + "px")
                .select("#heatmap_tooltip_value")
                .html("CDR3AA: " +
                rowLabel[d.row-1].cdr3aa_text +
                "<br> V: " + rowLabel[d.row - 1].v +
                "<br> J: " + rowLabel[d.row - 1].j +
                "<br> Sample: " + colLabel[d.col-1] +
                "<br> Convergence: " + d.convergence +
                "<br> Frequency: " + d.frequency.toPrecision(2));
            //Show the tooltip
            d3.select("#heatmap_tooltip").classed("hidden", false);
        })
        .on("mouseout", function(){
            d3.select(this).classed("cell-hover",false);
            d3.selectAll(".rowLabel").classed("text-highlight",false);
            d3.selectAll(".colLabel").classed("text-highlight",false);
            d3.select("#heatmap_tooltip").classed("hidden", true);
        });


    var numCol = (colLabel.length + 2) * cellSize;

    svg.append("g")
        .selectAll(".colLabelg")
        .data(["Times found"])
        .enter()
        .append("text")
        .text(function (d) { return d; })
        .attr("x", -200)
        .attr("y", function (d, i) { return (colLabel.length + 1) * cellSize; })
        .style("text-anchor", "left")
        .attr("transform", "translate("+cellSize/2 + ",-6) rotate(-90)")
        .attr("class",  function (d,i) { return "colLabel mono c"+i;} )
    ;

    var numLabels = svg.append("g")
            .selectAll(".numLabelg")
            .data(numLabel)
            .enter()
            .append("text")
            .html(function (d) {
                return d;
            })
            .attr("x", numCol)
            .attr("y", function (d, i) { return i * cellSize + 200; })
            .style("text-anchor", "end")
            .attr("transform", "translate(-6," + cellSize / 1.5 + ")")
            .attr("class", function (d,i) { return "numLabel mono r"+i;} )
        ;

    var sa=d3.select(".g3")
            .on("mousedown", function() {
                return null;
            })
            .on("mousemove", function() {
                return null;
            })
            .on("mouseup", function() {
                return null;
            })
            .on("mouseout", function() {
                return null;
            })
        ;

    function cdrTransform(cdr) {
        var cdr3aa = cdr.cdr3aa,
        //cdr3nt = cdr.cdr3nt,
        //vend_nt = cdr.vend,
        //dstart_nt = (cdr.dstart < 0) ? vend_nt + 1 : cdr.dstart,
        //dend_nt = (cdr.dend < 0) ? vend_nt : cdr.dend,
        //jstart_nt = (cdr.jstart < 0) ? 10000 : cdr.jstart,
            vend_aa = Math.floor(cdr.vend / 3),
            dstart_aa = (Math.floor(cdr.dstart / 3) < 0) ? vend_aa + 1 : Math.floor(cdr.dstart / 3),
            dend_aa = (Math.floor(cdr.dend / 3) < 0) ? vend_aa : Math.floor(cdr.dend / 3),
            jstart_aa = (Math.floor(cdr.jstart / 3) < 0) ? 10000 : Math.floor(cdr.jstart / 3);

        var //cdr3nt_arr = [],
            cdr3aa_arr = [];

        //while (vend_nt >= jstart_nt) jstart_nt++;
        while (vend_aa >= jstart_aa) jstart_aa++;
        //while (dstart_nt <= vend_nt) dstart_nt++;
        while (dstart_aa <= vend_aa) dstart_aa++;
        //while (dend_nt >= jstart_nt) dend_nt--;
        while (dend_aa >= jstart_aa) dend_aa--;

        //if (vend_nt >= 0) {
        //    cdr3nt_arr.push(createSubstring(cdr3nt, 0, vend_nt, "#4daf4a"));
        //}

        if (vend_aa >= 0) {
            cdr3aa_arr.push(createSubstring(cdr3aa, 0, vend_aa, "#4daf4a"));
        }

        //if (dstart_nt - vend_nt > 1) {
        //    cdr3nt_arr.push(createSubstring(cdr3nt, vend_nt + 1, dstart_nt - 1, "black"));
        //}

        if (dstart_aa - vend_aa > 1) {
            cdr3aa_arr.push(createSubstring(cdr3aa, vend_aa + 1, dstart_aa - 1, "black"));
        }

        //if (dstart_nt > 0 && dend_nt > 0 && dend_nt >= dstart_nt) {
        //    cdr3nt_arr.push(createSubstring(cdr3nt, dstart_nt, dend_nt, "#ec7014"));
        //}

        if (dstart_aa > 0 && dend_aa > 0 && dend_aa >= dstart_aa) {
            cdr3aa_arr.push(createSubstring(cdr3aa, dstart_aa, dend_aa, "#ec7014"));
        }

        //if (jstart_nt - dend_nt > 1) {
        //    cdr3nt_arr.push(createSubstring(cdr3nt, dend_nt + 1, jstart_nt - 1, "black"));
        //}

        if (jstart_aa - dend_aa > 1) {
            cdr3aa_arr.push(createSubstring(cdr3aa, dend_aa + 1, jstart_aa - 1, "black"));
        }

        //if (jstart_nt > 0) {
        //    cdr3nt_arr.push(createSubstring(cdr3nt, jstart_nt, cdr3nt.length, "#377eb8"));
        //}

        if (jstart_aa > 0) {
            cdr3aa_arr.push(createSubstring(cdr3aa, jstart_aa, cdr3aa.length, "#377eb8"));
        }

        //var cdr3nt_result = "", element, i = 0;
        //for (i = 0; i < cdr3nt_arr.length; i++) {
        //element = cdr3nt_arr[i];
        //cdr3nt_result += '<text style="color: ' + element.color + '">' + element.substring + '</text>';
        //}
        var cdr3aa_result_tspan = "";
        var cdr3aa_result_text = "";
        var element;
        for (var i = 0; i < cdr3aa_arr.length; i++) {
            element = cdr3aa_arr[i];
            cdr3aa_result_tspan += '<tspan fill="' + element.color + '">' + element.substring + '</tspan>';
            cdr3aa_result_text += '<text style="color: ' + element.color + '">' + element.substring + '</text>';
        }
        //cdr.cdr3aa = $sce.trustAsHtml(cdr3aa_result);
        //cdr.cdr3nt = $sce.trustAsHtml(cdr3nt_result);
        cdr.cdr3aa_tspan = cdr3aa_result_tspan;
        cdr.cdr3aa_text = cdr3aa_result_text;
        return cdr;
    }
}