function HistogramData(url) {
    d3.select("svg").remove();
    d3.select(".svg").remove();
    $.getJSON(url, function(data){
        renderHistogram(data);
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
        renderAnnotationTable(data.data, data.header);

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
            .attr("id", "annotation_table")
            .attr("class", "table table-striped table-hover"),
        thead = table.append("thead").append("tr");
    thead.selectAll("th").data(header).enter()
        .append("th").html(function(d) {return d});
    var column = [];
    for (var i = 0; i < header.length; i++) {
        column.push({"data": header[i.toString()]});
    }
    $('#annotation_table').dataTable({
        data: data,
        "columns": column
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
        .append("th").html(function(d) {return d.data;});
    $('#annotation_table').dataTable({
        "data": data,
        "columns": header
    });
}




function renderHistogram(histogramData) {
    var width = $(window).width() - 500,
        barWidth = 18,
        maxHeight = 600,
        heightMultiplier = 1500;

    var colors = d3.scale.category20().range();

    var spectratype = histogramData["common"];
    var clonotypes = histogramData["clonotypes"];
    var barsHeight = [];

    for (var i = 0; i < spectratype.length; i++) {
        barsHeight[spectratype[i]["xCoordinate"]] = parseFloat(spectratype[i]["yCoordinate"]);
    }

    var svg = d3.select(".visualisation")
        .append("svg")
        .attr("class", "svg")
        .style("overflow", "visible")
        .style("display", "block")
        .style("margin-left", "auto")
        .style("margin-right", "auto");

    var xStart = histogramData["xAxis"][0]["start"];
    var xEnd = histogramData["xAxis"][0]["end"];

    var x = d3.scale.linear()
        .domain([xStart, xEnd])
        .range([0, (xEnd - xStart) * barWidth]);

    var xAxis = d3.svg.axis().scale(x)
        .orient("bottom").ticks(20);

    var chart = d3.select("svg")
        .attr("width", (xEnd - xStart) * barWidth)
        .attr("height", maxHeight);

    var bar = chart.selectAll("g")
        .data(spectratype)
        .enter().append("g")
        .style("cursor", "pointer")
        .on("mouseover", function(d,i) {
            $("#commonTip" + d["xCoordinate"]).animate({
                opacity: 1
            }, 150);
        })
        .on("mouseout", function(d,i) {
            $("#commonTip" + d["xCoordinate"]).animate({
                opacity: 0
            }, 150);
        })
        .attr("transform", function(d, i) { return "translate(" + (d["xCoordinate"] - xStart) * barWidth + "," + (maxHeight - d["yCoordinate"] * heightMultiplier)  +")"; });


    bar.append("rect")
        .attr("height", function(d) {return d["yCoordinate"] * heightMultiplier;})
        .attr("width", barWidth - 1)
        .style("fill", "#CFCFCF");

    bar.append("text")
        .attr("type", "spectratype");



    var tip = chart.selectAll("tip")
        .data(clonotypes)
        .enter().append("g")
        .attr("transform", "translate(10, 10)")
        .attr("id", function(d, i) {return "tip" + i;})
        .style("opacity", 0);


    tip.append("rect")
        .attr("height", "9em")
        .attr("width", function(d) {
            return (d["Cdr3nt"].length + 6) * 13;
        })
        .style("fill", function(d, i) {
            return colors[i];
        })
        .style("opacity", "0.5")



    var tipText = tip
        .append("text")
        .attr("x", 10)
        .attr("y", 7)
        .style("fill", "#000")
        .attr("font-size", 16);

    //TODO ? tiptext
    tipText.append("tspan")
        .attr("x", 10)
        .attr("y", "1.4em")
        .html(function(d) {
            return "Clonotype: " + d["Cdr3nt"];
        });

    tipText.append("tspan")
        .attr("x", 10)
        .attr("y", "2.8em")
        .html(function(d) {
            return "Cdr3aa: " + d["Cdr3aa"];
        });

    tipText.append("tspan")
        .data(clonotypes)
        .attr("x", 10)
        .attr("y", "4.2em")
        .html(function(d) {
            return "Length: " + d["Cdr3nt"].length;
        });

    tipText.append("tspan")
        .data(clonotypes)
        .attr("x", 10)
        .attr("y", "5.6em")
        .html(function(d) {
            return "V  : " + d["v"];
        });

    tipText.append("tspan")
        .data(clonotypes)
        .attr("x", 10)
        .attr("y", "7em")
        .html(function(d) {
            return "J  : " + d["j"];
        });


    var commonTip = chart.selectAll("commonTip")
        .data(spectratype)
        .enter().append("g")
        .attr("transform", "translate(10, 10)")
        .attr("id", function(d, i) {return "commonTip" + d["xCoordinate"];})
        .style("opacity", 0);


    commonTip.append("rect")
        .attr("height", "5em")
        .attr("width", 500)
        .style("fill", "#CFCFCF")
        .style("opacity", "0.5");



    var commonTipText = commonTip
        .append("text")
        .attr("x", 10)
        .attr("y", 7)
        .style("fill", "#000")
        .attr("font-size", 16);

    //TODO ? tiptext
    commonTipText.append("tspan")
        .attr("x", 10)
        .attr("y", "1.4em")
        .html(function(d) {
            return "Length:  " + d["xCoordinate"];
        });
    commonTipText.append("tspan")
        .attr("x", 10)
        .attr("y", "2.8em")
        .html(function(d) {
            return "Freq:  " + d["yCoordinate"];
        });


    var clonotypeBar = chart.selectAll("clonotype")
        .data(clonotypes)
        .enter().append("g")
        .attr("class", "clonotype")
        .on("mouseover", function(d,i) {
            $("#tip" + i).animate({
                opacity: 1
            });
        })
        .on("mouseout", function(d,i) {
            $("#tip" + i).animate({
                opacity: 0
            });
        })
        .attr("id", function(d, i) {
            return i;
        })
        .style("cursor", "pointer")
        .attr("transform", function(d, i) {
            barsHeight[d["xCoordinate"]] += parseFloat(d["yCoordinate"]);
            return "translate(" + (d["xCoordinate"] - xStart) * barWidth + "," + (maxHeight - barsHeight[d["xCoordinate"]] * heightMultiplier)  +")";
        });


    clonotypeBar.append("rect")
        .attr("height", function(d) {return d["yCoordinate"] * heightMultiplier;})
        .attr("width", barWidth - 1)
        .style("fill", function(d, i) { return colors[i] });

    svg.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + maxHeight + ")")
        .call(xAxis);
}


function renderVJUsage(vdjUsageData) {
    var width = $(window).width() - 500, height = 800, margin = {b: 0, t: 40, l: 170, r: 50};

    var svg = d3.select(".visualisation")
        .append("svg").attr('width', width).attr('height', (height + margin.b + margin.t))
        .append("g").attr("transform", "translate(" + margin.l + "," + margin.t + ")");

    var data = [
        {data: bP.partData(vdjUsageData, 2), id: 'V-J-Usage', header: ["V", "J", "V-J Usage"]}
    ];

    bP.draw(data, svg);
}

!function(){
    var bP={};
    var b=30, bb=$(window).width() - 1000, height=700, buffMargin=5, minHeight=5;
    var c1=[-165, 50], c2=[-50, 160], c3=[-10, 250]; //Column positions of labels.
    var colors = d3.scale.category20().range();

    bP.partData = function(data,p){
        var sData={};

        sData.keys=[
            d3.set(data.map(function(d){ return d[0];})).values().sort(function(a,b){ return ( a<b? -1 : a>b ? 1 : 0);}),
            d3.set(data.map(function(d){ return d[1];})).values().sort(function(a,b){ return ( a<b? -1 : a>b ? 1 : 0);})
        ];

        sData.data = [	sData.keys[0].map( function(d){ return sData.keys[1].map( function(v){ return 0; }); }),
            sData.keys[1].map( function(d){ return sData.keys[0].map( function(v){ return 0; }); })
        ];

        data.forEach(function(d){
            sData.data[0][sData.keys[0].indexOf(d[0])][sData.keys[1].indexOf(d[1])]=d[p];
            sData.data[1][sData.keys[1].indexOf(d[1])][sData.keys[0].indexOf(d[0])]=d[p];
        });

        return sData;
    }

    function visualize(data){
        var vis ={};
        function calculatePosition(a, s, e, b, m){
            var total=d3.sum(a);
            var sum=0, neededHeight=0, leftoverHeight= e-s-2*b*a.length;
            var ret =[];

            a.forEach(
                function(d){
                    var v={};
                    v.percent = (total == 0 ? 0 : d/total);
                    v.value=d;
                    v.height=Math.max(v.percent*(e-s-2*b*a.length), m);
                    (v.height==m ? leftoverHeight-=m : neededHeight+=v.height );
                    ret.push(v);
                }
            );

            var scaleFact=leftoverHeight/Math.max(neededHeight,1), sum=0;

            ret.forEach(
                function(d){
                    d.percent = scaleFact*d.percent;
                    d.height=(d.height==m? m : d.height*scaleFact);
                    d.middle=sum+b+d.height/2;
                    d.y=s + d.middle - d.percent*(e-s-2*b*a.length)/2;
                    d.h= d.percent*(e-s-2*b*a.length);
                    d.percent = (total == 0 ? 0 : d.value/total);
                    sum+=2*b+d.height;
                }
            );
            return ret;
        }

        vis.mainBars = [
            calculatePosition( data.data[0].map(function(d){ return d3.sum(d);}), 0, height, buffMargin, minHeight),
            calculatePosition( data.data[1].map(function(d){ return d3.sum(d);}), 0, height, buffMargin, minHeight)
        ];

        vis.subBars = [[],[]];
        vis.mainBars.forEach(function(pos,p){
            pos.forEach(function(bar, i){
                calculatePosition(data.data[p][i], bar.y, bar.y+bar.h, 0, 0).forEach(function(sBar,j){
                    sBar.key1=(p==0 ? i : j);
                    sBar.key2=(p==0 ? j : i);
                    vis.subBars[p].push(sBar);
                });
            });
        });
        vis.subBars.forEach(function(sBar){
            sBar.sort(function(a,b){
                return (a.key1 < b.key1 ? -1 : a.key1 > b.key1 ?
                    1 : a.key2 < b.key2 ? -1 : a.key2 > b.key2 ? 1: 0 )});
        });

        vis.edges = vis.subBars[0].map(function(p,i){
            return {
                key1: p.key1,
                key2: p.key2,
                y1:p.y,
                y2:vis.subBars[1][i].y,
                h1:p.h,
                h2:vis.subBars[1][i].h
            };
        });
        vis.keys=data.keys;
        return vis;
    }

    function arcTween(a) {
        var i = d3.interpolate(this._current, a);
        this._current = i(0);
        return function(t) {
            return edgePolygon(i(t));
        };
    }

    function drawPart(data, id, p){
        d3.select("#"+id).append("g").attr("class","part"+p)
            .attr("transform","translate("+( p*(bb+b))+",0)");
        d3.select("#"+id).select(".part"+p).append("g").attr("class","subbars");
        d3.select("#"+id).select(".part"+p).append("g").attr("class","mainbars");

        var mainbar = d3.select("#"+id).select(".part"+p).select(".mainbars")
            .selectAll(".mainbar").data(data.mainBars[p])
            .enter().append("g").attr("class","mainbar");

        mainbar.append("rect").attr("class","mainrect")
            .attr("x", 0).attr("y",function(d){ return d.middle-d.height/2; })
            .attr("width",b).attr("height",function(d){ return d.height; })
            .style("shape-rendering","auto")
            .style("fill-opacity",0).style("stroke-width","0.5")
            .style("stroke","black").style("stroke-opacity",0);

        mainbar.append("text").attr("class","barlabel")
            .attr("x", c1[p]).attr("y",function(d){ return d.middle+5;})
            .text(function(d,i){ return data.keys[p][i];})
            .attr("text-anchor","start" );

        mainbar.append("text").attr("class","barvalue")
            .attr("x", c2[p]).attr("y",function(d){ return d.middle+5;})
            .text(function(d,i){ return d.value ;})
            .attr("text-anchor","end");

        mainbar.append("text").attr("class","barpercent")
            .attr("x", c3[p]).attr("y",function(d){ return d.middle+5;})
            .text(function(d,i){ return "( "+Math.round(100*d.percent)+"%)" ;})
            .attr("text-anchor","end").style("fill","grey");

        d3.select("#"+id).select(".part"+p).select(".subbars")
            .selectAll(".subbar").data(data.subBars[p]).enter()
            .append("rect").attr("class","subbar")
            .attr("x", 0).attr("y",function(d){ return d.y})
            .attr("width",b).attr("height",function(d){ return d.h})
            .style("fill",function(d){ return colors[d.key1];});
    }

    function drawEdges(data, id){
        d3.select("#"+id).append("g").attr("class","edges").attr("transform","translate("+ b+",0)");

        d3.select("#"+id).select(".edges").selectAll(".edge")
            .data(data.edges).enter().append("polygon").attr("class","edge")
            .attr("points", edgePolygon).style("fill",function(d){ return colors[d.key1];})
            .style("opacity",0.5).each(function(d) { this._current = d; });
    }

    function drawHeader(header, id){
        d3.select("#"+id).append("g").attr("class","header").append("text").text(header[2])
            .style("font-size","20").attr("x",108).attr("y",-20).style("text-anchor","middle")
            .style("font-weight","bold");

        [0,1].forEach(function(d){
            var h = d3.select("#"+id).select(".part"+d).append("g").attr("class","header");

            h.append("text").text(header[d]).attr("x", (c1[d]-5))
                .attr("y", -5).style("fill","grey");

            h.append("text").text("Count").attr("x", (c2[d]-10))
                .attr("y", -5).style("fill","grey");

            h.append("line").attr("x1",c1[d]-10).attr("y1", -2)
                .attr("x2",c3[d]+10).attr("y2", -2).style("stroke","black")
                .style("stroke-width","1").style("shape-rendering","crispEdges");
        });
    }

    function edgePolygon(d){
        return [0, d.y1, bb, d.y2, bb, d.y2+d.h2, 0, d.y1+d.h1].join(" ");
    }

    function transitionPart(data, id, p){
        var mainbar = d3.select("#"+id).select(".part"+p).select(".mainbars")
            .selectAll(".mainbar").data(data.mainBars[p]);

        mainbar.select(".mainrect").transition().duration(500)
            .attr("y",function(d){ return d.middle-d.height/2;})
            .attr("height",function(d){ return d.height;});

        mainbar.select(".barlabel").transition().duration(500)
            .attr("y",function(d){ return d.middle+5;});

        mainbar.select(".barvalue").transition().duration(500)
            .attr("y",function(d){ return d.middle+5;}).text(function(d,i){ return d.value ;});

        mainbar.select(".barpercent").transition().duration(500)
            .attr("y",function(d){ return d.middle+5;})
            .text(function(d,i){ return "( "+Math.round(100*d.percent)+"%)" ;});

        d3.select("#"+id).select(".part"+p).select(".subbars")
            .selectAll(".subbar").data(data.subBars[p])
            .transition().duration(500)
            .attr("y",function(d){ return d.y}).attr("height",function(d){ return d.h});
    }

    function transitionEdges(data, id){
        d3.select("#"+id).append("g").attr("class","edges")
            .attr("transform","translate("+ b+",0)");

        d3.select("#"+id).select(".edges").selectAll(".edge").data(data.edges)
            .transition().duration(500)
            .attrTween("points", arcTween)
            .style("opacity",function(d){ return (d.h1 ==0 || d.h2 == 0 ? 0 : 0.5);});
    }

    function transition(data, id){
        transitionPart(data, id, 0);
        transitionPart(data, id, 1);
        transitionEdges(data, id);
    }

    bP.draw = function(data, svg){
        data.forEach(function(biP,s){
            svg.append("g")
                .attr("id", biP.id)
                .attr("transform","translate("+ (550*s)+",0)");

            var visData = visualize(biP.data);
            drawPart(visData, biP.id, 0);
            drawPart(visData, biP.id, 1);
            drawEdges(visData, biP.id);
            drawHeader(biP.header, biP.id);

            [0,1].forEach(function(p){
                d3.select("#"+biP.id)
                    .select(".part"+p)
                    .select(".mainbars")
                    .selectAll(".mainbar")
                    .on("mouseover",function(d, i){ return bP.selectSegment(data, p, i); })
                    .on("mouseout",function(d, i){ return bP.deSelectSegment(data, p, i); });
            });
        });
    }

    bP.selectSegment = function(data, m, s){
        data.forEach(function(k){
            var newdata =  {keys:[], data:[]};

            newdata.keys = k.data.keys.map( function(d){ return d;});

            newdata.data[m] = k.data.data[m].map( function(d){ return d;});

            newdata.data[1-m] = k.data.data[1-m]
                .map( function(v){ return v.map(function(d, i){ return (s==i ? d : 0);}); });

            transition(visualize(newdata), k.id);

            var selectedBar = d3.select("#"+k.id).select(".part"+m).select(".mainbars")
                .selectAll(".mainbar").filter(function(d,i){ return (i==s);});

            selectedBar.select(".mainrect").style("stroke-opacity",1);
            selectedBar.select(".barlabel").style('font-weight','bold');
            selectedBar.select(".barvalue").style('font-weight','bold');
            selectedBar.select(".barpercent").style('font-weight','bold');
        });
    }

    bP.deSelectSegment = function(data, m, s){
        data.forEach(function(k){
            transition(visualize(k.data), k.id);

            var selectedBar = d3.select("#"+k.id).select(".part"+m).select(".mainbars")
                .selectAll(".mainbar").filter(function(d,i){ return (i==s);});

            selectedBar.select(".mainrect").style("stroke-opacity",0);
            selectedBar.select(".barlabel").style('font-weight','normal');
            selectedBar.select(".barvalue").style('font-weight','normal');
            selectedBar.select(".barpercent").style('font-weight','normal');
        });
    }
    this.bP = bP;
}();