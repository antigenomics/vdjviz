/**
 * Created by bvdmitri on 05.12.14.
 */

$(document).ready(function() {

    var render = function() {
        var string = d3.select("#string").node().value;
        var vend = Math.floor(d3.select("#vend").node().value / 3);
        var dstart = Math.floor(d3.select("#dstart").node().value / 3);
        var dend = Math.floor(d3.select("#dend").node().value / 3);
        var jstart = Math.floor(d3.select("#jstart").node().value / 3);
        jstart = (jstart < 0) ? 10000 : jstart;
        dstart = (dstart < 0) ? vend + 1 : dstart;
        dend = (dend < 0) ? vend : dend;
        var pos = d3.select("#pos").node().value;
        while (vend >= jstart) jstart++;
        while (dstart <= vend) dstart++;
        while (dend >= jstart) dend--;

        var createSubString = function(start, end, color) {
            var substring = string.substring(start, end + 1);
            return {
                start: start,
                end: end,
                color: color,
                substring: substring
            }
        };

        var insert = function (index, str, insertString) {
            if (index > 0)
                return str.substring(0, index) + insertString + str.substring(index, str.length);
            else
                return insertString + str;
        };

        var arr = [];

        if (vend >= 0) {
            arr.push(createSubString(0, vend, "green"));
        }

        if (dstart - vend > 1) {
            arr.push(createSubString(vend + 1, dstart - 1, "black"));
        }

        if (dstart > 0 && dend > 0 && dend >= dstart) {
            arr.push(createSubString(dstart, dend, "red"));
        }

        if (jstart - dend > 1) {
            arr.push(createSubString(dend + 1, jstart - 1, "black"));
        }

        if (jstart > 0) {
            arr.push(createSubString(jstart, string.length, "blue"));
        }

        d3.select("#result").html(function() {
            var result = "";
            for (var i = 0; i < arr.length; i++) {
                var element = arr[i];
                if (pos >= element.start && pos <= element.end) {
                    var newPos = pos - element.start;
                    element.substring = insert(newPos + 1, element.substring, '</u></b>');
                    element.substring = insert(newPos, element.substring, '<b><u>');
                }
                result += '<text style="color: ' + element.color + '">' + element.substring + '</text>';
            }
            return result;
        });

    };

    $("input").change(function() {
        render();
    });

    $("#button").click(function() {
        render();
    })
    render();
});
