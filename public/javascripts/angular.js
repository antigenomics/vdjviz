/**
 * Created by bvdmitri on 13.11.14.
 */

(function () {
    var app = angular.module('accountPage', []);

    app.factory('data', ['$http', '$rootScope', function ($http, $rootScope) {

        $rootScope.files = {};
        $rootScope.state = 'accountInformation';
        $rootScope.visualisationInfo = {
            activeFileName: '',
            tab: 'V-J Usage'
        };

        var uid = 0;

        function updateFilesList() {
            $http({method: 'GET', url: '/api/files'}).success(function (data) {
                angular.forEach(data.data, function(file) {
                    $rootScope.files[file.fileName] = {
                        uid: uid++,
                        fileName: file.fileName,
                        state: file.state,
                        softwareTypeName: file.softwareTypeName,
                        data: {
                            vjusage: {
                                cached: false,
                                comparingCache: false,
                                data: [],
                                comparing: false
                            },
                            spectrotype: {
                                cached: false,
                                comparingCache: false,
                                data: [],
                                comparing: false
                            },
                            spectrotypeV: {
                                cached: false,
                                comparingCache: false,
                                data: [],
                                comparing: false
                            },
                            kernelDensity: {
                                cached: false,
                                comparingCache: false,
                                data: [],
                                comparing: false
                            },
                            annotation: {
                                cached: false,
                                comparingCache: false,
                                data: [],
                                comparing: false
                            }
                        }
                    }
                })
            })
        }

        function deleteAllFiles() {
            $rootScope.files = {};
        }

        function deleteFileFromList(fileName) {
            delete $rootScope.files[fileName];
        }

        function addFileToList(file) {
            $rootScope.files[file.fileName] = {
                uid: uid++,
                fileName: file.fileName,
                state: 'rendering',
                softwareTypeName: file.softwareTypeName,
                data: {
                    vjusage: {
                        cached: false,
                        comparingCache: false,
                        data: [],
                        comparing: false
                    },
                    spectrotype: {
                        cached: false,
                        comparingCache: false,
                        data: [],
                        comparing: false
                    },
                    spectrotypeV: {
                        cached: false,
                        comparingCache: false,
                        data: [],
                        comparing: false
                    },
                    kernelDensity: {
                        cached: false,
                        comparingCache: false,
                        data: [],
                        comparing: false
                    },
                    annotation: {
                        cached: false,
                        comparingCache: false,
                        data: [],
                        comparing: false
                    }
                }
            }
        }

        function changeFileState(file, state) {
            $rootScope.files[file.fileName].state = state;
        }

        function isFilesEmpty() {
            return Object.keys($rootScope.files).length;
        }

        function changeState(state) {
            if (state != 'file') {
                $rootScope.visualisationInfo.activeFileName = '';
            }
            $rootScope.state = state;
            updateVisualisationTab();
        }

        function getState() {
            return $rootScope.state;
        }

        function setFile(fileName) {
            $rootScope.state = 'file';
            $rootScope.visualisationInfo.activeFileName = fileName;
            updateVisualisationTab();
        }

        function getActiveFileName() {
            return $rootScope.visualisationInfo.activeFileName;
        }

        function getVisualisationTab() {
            return $rootScope.visualisationInfo.tab;
        }

        function setVisualisationTab(tab, file) {
            $rootScope.visualisationInfo.tab = tab;
            updateVisualisationTab();
        }

        function updateVisualisationTab() {
            var param = {};

            switch ($rootScope.state) {
                case 'file':
                    var tab = getVisualisationTab();
                    var file = $rootScope.files[getActiveFileName()];
                    param = {
                        fileName: getActiveFileName(),
                        type: '',
                        id: file.uid,
                        height: 600
                    };
                    switch (tab) {
                        case "V-J Usage":
                            if (!file.data.vjusage.cached) {
                                param.type = 'vjusage';
                                param.place = '#id' + file.uid + ' .visualisation-results-vjusage';
                                param.svg_width = '70%';
                                param.width = 600;
                                getData(vjUsage, param);
                                file.data.vjusage.cached = true;
                            }
                            break;
                        case "Spectrotype":
                            if (!file.data.spectrotype.cached) {
                                param.type = 'spectrotype';
                                param.place = '#id' + file.uid + ' .visualisation-results-spectrotype';
                                getData(spectrotype, param);
                                file.data.spectrotype.cached = true;
                            }
                            break;
                        case "SpectrotypeV":
                            if (!file.data.spectrotypeV.cached) {
                                param.type = 'spectrotypeV';
                                param.place = '#id' + file.uid + ' .visualisation-results-spectrotypeV';
                                getData(spectrotypeV, param);
                                file.data.spectrotypeV.cached = true;
                            }
                            break;
                        case "Kernel Density":
                            if (!file.data.kernelDensity.cached) {
                                param.type = 'kernelDensity';
                                param.place = '#id' + file.uid + ' .visualisation-results-kernelDensity';
                                getData(kernelDensity, param);
                                file.data.kernelDensity.cached = true;
                            }
                            break;
                        case "Annotation":
                            if (!file.data.annotation.cached) {
                                param.type = 'annotation';
                                param.place = '#id' + file.uid + ' .visualisation-results-annotation';
                                getData(annotationTable, param);
                                file.data.annotation.cached = true;
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                case 'diversity':
                    param.place = '.diversity-visualisation-tab';
                    param.fileName = 'all';
                    param.type = 'diversity';
                    getData(diversityStats, param);
                    break;
                case 'summary':
                    param.place = '.summary-visualisation-tab';
                    param.fileName = 'all';
                    param.type = 'summary';
                    getData(summaryStats, param);
                    break;
                default:
                    break
            }
        }

        function getFilesList() {
            return $rootScope.files;
        }

        function isContain(fileName) {
            return !!(fileName in $rootScope.files);

        }

        function changeComparingItem(fileName, item) {
            console.log(fileName);
            $rootScope.files[fileName].data[item].comparing = !$rootScope.files[fileName].data[item].comparing;
            $rootScope.files[fileName].data[item].comparingCache = true;
        }

        function getDataInfo(fileName) {
            return $rootScope.files[fileName].data;
        }

        return {
            updateFilesList: updateFilesList,
            changeState: changeState,
            getState: getState,
            setFile: setFile,
            getActiveFileName: getActiveFileName,
            getVisualisationTab: getVisualisationTab,
            setVisualisationTab: setVisualisationTab,
            updateVisualisationTab: updateVisualisationTab,
            getFilesList: getFilesList,
            isContain: isContain,
            isFilesEmpty: isFilesEmpty,
            deleteFileFromList: deleteFileFromList,
            addFileToList: addFileToList,
            changeFileState: changeFileState,
            deleteAllFiles: deleteAllFiles,
            changeComparingItem: changeComparingItem,
            getDataInfo: getDataInfo
        }

    }]);

    app.controller('account', ['$rootScope', '$scope', '$http', 'data', function ($rootScope, $scope, $http, data) {
        data.updateFilesList();
    }]);

    app.controller('visualisation', ['$rootScope', '$scope', '$log', 'data', function ($rootScope, $scope, $log, data) {

        $scope.tab = 'V-J Usage';

        $scope.isActiveTab = function (tab) {
            return data.getVisualisationTab() === tab;
        };

        $scope.setActiveTab = function (tab) {
            data.setVisualisationTab(tab);
            $scope.tab = tab;
        };

        $scope.isState = function (state) {
            return data.getState() === state;
        };

        $scope.isFile = function(fileName) {
            return fileName === data.getActiveFileName();
        }

    }]);


    app.controller('filePanel', ['$scope', '$log', '$http', 'data', function ($scope, $log, $http, data) {

        $scope.changeState = data.changeState;
        $scope.setFile = data.setFile;
        $scope.isFilesEmpty = data.isFilesEmpty;

        $scope.isState = function (state) {
            return data.getState() === state;
        };

        $scope.isFile = function (fileName) {
            return data.getState() === 'file' && data.getActiveFileName() === fileName;
        };

        $scope.isState = function (state) {
            return data.getState() === state;
        };

        $scope.deleteAll = function () {
            $http.post('/api/delete', {action: 'deleteAll'})
                .success(function () {
                    data.deleteAllFiles();
                    data.changeState('accountInformation');
                });
        };

        $scope.deleteFile = function(file) {
            $http.post('/api/delete', {
                action: 'delete',
                fileName: file.fileName
            }).success(function() {
                if (file.fileName == data.getActiveFileName(file.fileName)) {
                    data.changeState('accountInformation');
                } else if (data.getState() != 'file') {
                    data.updateVisualisationTab();
                }
                data.deleteFileFromList(file.fileName);
            });
        };

        $scope.isRendering = function(file) {
            return file.state === 'rendering';
        }
    }]);



    app.controller('fileUpload', ['$scope', '$http', '$log', 'data', function ($scope, $http, $log, account) {

        var uid = 0;

        $scope.newFiles = {};
        $scope.uploadedFiles = [];
        $scope.commonSoftwareType = 'mitcr';


        $scope.isEmpty = function() {
            var size = 0;
            angular.forEach($scope.newFiles, function() {
                size++;
            });
            return size;
        };

        $scope.addNewButton = function() {
            $("form input[type=file]").click();
        };

        $scope.addNew = function(uid, fileName, fileExtension, data) {
            $scope.$apply(function() {
                $scope.newFiles[uid] = {
                    uid: uid,
                    fileName: fileName,
                    softwareTypeName: 'mitcr',
                    fileExtension: fileExtension,
                    wait: true,
                    tooltip: '',
                    progress: 0,
                    result: 'ok',
                    resultTooltip: '',
                    data: data
                };
            })
        };

        $scope.addNewError = function(uid, fileName, error) {
            switch (error) {
                case 0:
                    $scope.$apply(function() {
                        $scope.newFiles[uid] = {
                            uid: uid,
                            fileName: fileName,
                            softwareTypeName: '',
                            wait: false,
                            result: 'error',
                            resultTooltip: 'You have exceeded limit of files'
                        };
                    });
                    break;
                case 1:
                    $scope.$apply(function() {
                        $scope.newFiles[uid] = {
                            uid: uid,
                            fileName: fileName,
                            softwareTypeName: '',
                            wait: false,
                            result: 'error',
                            resultTooltip: 'You should use unique names for your files'
                        };
                    });
                    break;
                default :
                    break;
            }
        };

        $scope.updateTooltip = function(file, tooltip) {
            $scope.$apply(function() {
                file.tooltip = tooltip;
            })
        };

        $scope.updateProgress = function(file, progress) {
            $scope.$apply(function() {
                file.progress = progress;
            })
        };

        $scope.updateResult = function(file, result) {
            $scope.$apply(function() {
                file.result = result;
            })
        };

        $scope.updateResultTooltip = function(file, resultTooltip) {
            $scope.$apply(function() {
                file.resultTooltip = resultTooltip;
            })
        };

        $scope.changeCommonSoftwareType = function() {
            angular.forEach($scope.newFiles, function(file, key) {
                file.softwareTypeName = $scope.commonSoftwareType;
            });
        };

        $scope.uploadAll = function () {
            angular.forEach($scope.newFiles, function(file, key) {
                $scope.uploadFile(file);
            })
        };

        $scope.uploadFile = function(file) {
            if (file.wait) {
                file.data.formData = {
                    softwareTypeName: file.softwareTypeName,
                    fileName: file.fileName,
                    fileExtension: file.fileExtension,
                    uid: file.uid
                };
                file.wait = false;
                file.data.submit();
            }
        };

        $scope.isOk = function(file) {
            return file.result === 'ok' || file.result === 'success';
        };

        $scope.isSuccess = function(file) {
            return file.result === 'success';
        };

        $scope.isError = function(file) {
            return file.result === 'error';
        };


        $('#fileupload').fileupload({
            url: '/api/upload',
            dataType: 'json',
            sequentialUploads: true,
            add: function (e, data) {
                var originalFileName = data.files[0].name;
                var fileName = originalFileName.substr(0, originalFileName.lastIndexOf('.')) || originalFileName;
                var fileExtension = originalFileName.substr((~-originalFileName.lastIndexOf(".") >>> 0) + 2);
                if (fileExtension != 'txt' && fileExtension != 'gz') {
                    fileName += fileExtension;
                    fileExtension = 'txt';
                }
                if (account.getFilesList().length >= 25) {
                    $scope.addNewError(uid++, fileName, 0);
                } else if (account.isContain(fileName)) {
                    $scope.addNewError(uid++, fileName, 1);
                } else {
                    $scope.addNew(uid++, fileName, fileExtension, data);
                }
            },
            progress: function (e, data) {
                var file = $scope.newFiles[data.formData.uid];
                $scope.updateTooltip(file, "Uploading");
                $scope.updateProgress(file, parseInt(data.loaded / data.total * 50, 10));
            },
            done: function (e, data) {
                var file = $scope.newFiles[data.formData.uid];
                switch (data.result["result"]) {
                    case "success" :
                        var socket = new WebSocket("ws://" + location.host + "/api/ws");
                        socket.onopen = function () {
                            var msg = {
                                type: "message",
                                action: "render",
                                data: {
                                    fileName: data.formData.fileName
                                }
                            };
                            socket.send(JSON.stringify(msg));
                        };
                        socket.onmessage = function (message) {
                            var event = JSON.parse(message["data"]);
                            switch (event["result"]) {
                                case "ok" :
                                    switch (event["progress"]) {
                                        case "start" :
                                            $scope.updateTooltip(file, "Computation");
                                            account.addFileToList(file);
                                            break;
                                        case "end" :
                                            account.changeFileState(file, 'rendered');
                                            account.updateVisualisationTab();
                                            $scope.updateTooltip(file, "Success");
                                            $scope.updateResult(file, 'success');
                                            socket.close();
                                            break;
                                        default:
                                            $scope.updateProgress(file, 50 + (event.progress / 2));
                                    }
                                    break;
                                case "error" :
                                    socket.close();
                                    account.deleteFileFromList(file.fileName);
                                    $scope.updateResult(file, 'error');
                                    $scope.updateResultTooltip(file, "Error while computation");
                                    break;
                                default:
                                    account.deleteFileFromList(file.fileName);
                                    $scope.updateTooltip(file, "Server unavailable");
                                    break;
                            }

                        };
                        break;
                    case "error" :
                        $scope.updateResult(file, 'error');
                        $scope.updateResultTooltip(file, data.result.message);
                        break;
                    default:
                        $scope.updateResult(file, 'error');
                        $scope.updateResultTooltip(file, "Server unavailable");
                }

            }
        });

        $('#new-files-table').on('hidden.bs.modal', function () {
            angular.forEach($scope.newFiles, function(file) {
                switch(file.result) {
                    case 'success':
                        $scope.$apply(function() {
                            $scope.uploadedFiles.push({
                                fileName: file.fileName,
                                softwareTypeName: file.softwareTypeName
                            });
                            delete $scope.newFiles[file.uid];
                        });
                        break;
                    case 'error':
                        $scope.$apply(function() {
                            delete $scope.newFiles[file.uid];
                        });
                        break;
                    default :
                        break;
                }
            })
        });

    }]);

    // Comparing tab controllers and service

    app.controller('comparingTable', ['$scope', 'data', function($scope, account) {
        $scope.showItem = function(file, item) {
            if (!account.getDataInfo(file.fileName)[item].comparingCache) {
                var param = {
                    fileName: file.fileName,
                    id: file.uid + '_comparing',
                    height: 500,
                    type: item
                };
                switch (item) {
                    case 'vjusage':
                        param.place = '#id' + file.uid + ' .comparing-vjusage-tab';
                        param.svg_width = '70%';
                        param.width = 600;
                        getData(vjUsage, param);
                        break;
                    case 'spectrotype':
                        param.place = '#id' + file.uid + ' .comparing-spectrotype-tab';
                        getData(spectrotype, param);
                        break;
                    case 'spectrotypeV':
                        param.place = '#id' + file.uid + ' .comparing-spectrotypev-tab';
                        getData(spectrotypeV, param);
                        break;
                    case 'kernelDensity':
                        param.place = '#id' + file.uid + ' .comparing-kerneldensity-tab';
                        getData(kernelDensity, param);
                        break;
                    default:
                        break;
                }
            }
            account.changeComparingItem(file.fileName, item);
        }
    }])

})();


function getData(handleData, param) {
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

function diversityStats(data, param) {
    nv.addGraph(function () {
        d3.select(param.place).html("");

        var svg = d3.select(param.place)
            .append("svg")
            .attr("id", "svgtopng")
            .style("height", "900px")
            .style("margin-top", "50px");

        var chart = nv.models.lineChart()
            .useInteractiveGuideline(true)
            .transitionDuration(350)
            .showLegend(true)
            .showYAxis(true)
            .showXAxis(true)
            .height(700);

        chart.xAxis
            .axisLabel('Count')
            .tickFormat(d3.format(',r'));

        chart.yAxis
            .axisLabel('CDR3AA')
            .tickFormat(d3.format('.02f'));


        svg.datum(data)
            .call(chart);

        nv.utils.windowResize(function () {
            chart.update()
        });
        return chart;
    });
}

function summaryStats(data, param) {
    var header = Object.keys(data[0]);
    d3.select(param.place).html("");
    var table = d3.select(param.place)
            .append("table")
            .attr("id", "basicStatsTable")
            .attr("class", "table table-striped table-hover"),
        thead = table.append("thead")
            .append("tr")
            .selectAll("th")
            .data(header)
            .enter()
            .append("th")
            .html(function (d) {
                return d;
            });
    var column = [];
    for (var i = 0; i < header.length; i++) {
        column.push({"data": header[i.toString()]});
    }
    $('#basicStatsTable').dataTable({
        dom: 'T<"clear">lfrtip',
        tableTools: {
            "sSwfPath": "../assets/lib/dataTable/extensions/TableTools/swf/copy_csv_xls_pdf.swf"
        },
        data: data,
        "columnDefs": [
            {
                "width": "10%",
                "targets": 0
            },
            {
                "render": function (data) {
                    return parseFloat(data).toExponential(2);
                },
                "targets": 4
            },
            {
                "render": function (data) {
                    return parseFloat(data).toExponential(2);
                },
                "targets": 5
            },
            {
                "render": function (data) {
                    return parseFloat(data).toFixed(2);
                },
                "targets": 6
            },
            {
                "render": function (data) {
                    return parseFloat(data).toFixed(2);
                },
                "targets": 7
            },
            {
                "render": function (data) {
                    return parseFloat(data).toExponential(2);
                },
                "targets": 8
            }
        ],
        "columns": column,
        'iDisplayLength': 100,
        'order': [
            [0, "asc"]
        ],
        responsive: true
    });
}
