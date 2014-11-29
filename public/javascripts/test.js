$(document).ready(function() {

    nv.addGraph(function() {
        var chart = nv.models.multiBarChart()
                .reduceXTicks(true)   //If 'false', every single x-axis tick label will be rendered.
                .rotateLabels(0)      //Angle to rotate x-axis labels.
                .showControls(false)   //Allow user to switch between 'Grouped' and 'Stacked' mode.
                .groupSpacing(0.1)    //Distance between each group of bars.
                .stacked(true)
                .duration(0)
            ;

        chart.xAxis
            .tickFormat(d3.format(',f'));

        chart.yAxis
            .tickFormat(d3.format(',.1f'));


        d3.select('.main')
            .append("svg")
            .style("margin-top", 100)
            .style("height", "400px")
            .style("width", "100%")
            .style("overflow", "visible")
            .datum(sort(exampleData()))
            .call(chart);


        nv.utils.windowResize(chart.update);

        return chart;
    });

//Generate some nice data.
    function exampleData() {
        return stream_layers(4,20,.1).map(function(data, i) {
            var key, color;
            switch (i) {
                case 0: key = "T"; color = "#de2d26";
                    break;
                case 1: key = "G"; color = "#fec44f";
                    break;
                case 2: key = "A"; color = "#31a354";
                    break;
                case 3: key = "C"; color = "#3182bd";
                    break;
                default:
                    key = "!";
            }
            return {
                key: key,
                values: data,
                color: color
            };
        });
    }

    function sort(data) {
        for (var i = 0; i < data[0].values.length; i++) {
            var s = [];
            for (var j = 0; j < 4; j++) {
                data[j].values[i].color = data[j].color;
                data[j].values[i].key = data[j].key;
                s.push(data[j].values[i]);
            }
            for (var k = 0; k < 4; k++) {
                for (var t = k; t < 4; t++) {
                    if (s[t].y < s[k].y) {
                        var y = s[t];
                        s[t] = s[k];
                        s[k] = y;
                    }
                }
            }
            for (var j = 0; j < 4; j++) {
                data[j].values[i] = s[j];
            }
        }
	return data;
    }

    /* Inspired by Lee Byron's test data generator. */
    function stream_layers(n, m, o) {
        if (arguments.length < 3) o = 0;
        function bump(a) {
            var x = 1 / (.1 + Math.random()),
                y = 2 * Math.random() - .5,
                z = 10 / (.1 + Math.random());
            for (var i = 0; i < m; i++) {
                var w = (i / m - y) * z;
                a[i] += x * Math.exp(-w * w);
            }
        }
        return d3.range(n).map(function() {
            var a = [], i;
            for (i = 0; i < m; i++) a[i] = o + o * Math.random();
            for (i = 0; i < 5; i++) bump(a);
            return a.map(stream_index);
        });
    }

    /* Another layer generator using gamma distributions. */
    function stream_waves(n, m) {
        return d3.range(n).map(function(i) {
            return d3.range(m).map(function(j) {
                var x = 20 * j / m - i / 3;
                return 2 * x * Math.exp(-.5 * x);
            }).map(stream_index);
        });
    }

    function stream_index(d, i) {
        return {x: i, y: Math.max(0, d)};
        //return {x: i, y: Math.max(0, d)};
    }

    /*
    var svg = d3.select(".main")
        .append("svg")
        .style("width", "100px")
        .style("height", "100px")
        .style('overflow', 'visible');

    svg.append("text")
        .attr('x', 0)
        .attr('y', 400)
        .attr('textLength', 200)
        .attr('font-size', 300)
        .attr('lengthAdjust', 'spacingAndGlyphs')
        .text('A A');

    svg.select("text").attr('font-size', 200)
    */
});

