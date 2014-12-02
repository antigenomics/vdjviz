/**
 * Created by bvdmitri on 13.11.14.
 */

(function () {
    var app = angular.module('accountPage', []);

    //Main Directive
    app.directive('accountPage', function() {
        return {
            restrict: 'E',
            controller: ['$scope', '$http', function($scope, $http) {
                //private parameters
                var uid = 0;

                //public parameters
                $scope.files = {};
                $scope.state = 'accountInformation';
                $scope.activeFileName = '';
                $scope.visualisationTabs = {
                    vjusage: { tabName: 'V-J Usage ', type: 'vjusage', mainPlace: 'visualisation-results-vjusage', comparing: true, comparingPlace: 'comparing-vjusage-tab', dataHandler: vjUsage},
                    spectrotype: { tabName: 'Spectrotype ', type: 'spectrotype', mainPlace: 'visualisation-results-spectrotype', comparing: true, comparingPlace: 'comparing-spectrotype-tab',  dataHandler: spectrotype},
                    spectrotypev: { tabName: 'SpectrotypeV', type: 'spectrotypeV', mainPlace: 'visualisation-results-spectrotypeV', comparing: true, comparingPlace: 'comparing-spectrotypeV-tab', dataHandler: spectrotypeV},
                    sizeclassifying: { tabName: 'Size Classifying', type: 'sizeClassifying', mainPlace: 'visualisation-results-sizeClassifying', comparing: true, comparingPlace: 'comparing-sizeClassifying-tab', dataHandler: sizeClassifying },
                    annotation: { tabName: 'Annotation', type: 'annotation', mainPlace: 'visualisation-results-annotation', comparing: false, dataHandler: annotationTable}
                }
                ;
                $scope.activeTab = $scope.visualisationTabs.vjusage;

                $scope.updateFilesList = function() {
                    $http({method: 'GET', url: '/api/files'}).success(function (data) {
                        angular.forEach(data, function(file) {
                            $scope.files[file.fileName] = {
                                uid: uid++,
                                fileName: file.fileName,
                                state: file.state,
                                softwareTypeName: file.softwareTypeName,
                                meta: {
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
                                    sizeClassifying: {
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
                };

                $scope.getActiveFile = function() {
                    return $scope.files[$scope.activeFileName];
                };

                $scope.isContain = function(file) {
                    angular.forEach($scope.files, function(f) {
                        if (file.fileName === f.fileName) {
                            return true;
                        }
                    })
                    return false;
                };

                $scope.addFileToList = function addFileToList(file) {
                    $scope.files[file.fileName] = {
                        uid: uid++,
                        fileName: file.fileName,
                        state: 'rendering',
                        softwareTypeName: file.softwareTypeName,
                        meta: {
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
                            sizeClassifying: {
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
                };

                $scope.updateFilesList();

                $scope.updateVisualisationTab = function () {
                    var param = {};

                    switch ($scope.state) {
                        case 'file':
                            var file = $scope.files[$scope.activeFileName];
                            param = {
                                fileName: file.fileName,
                                type: $scope.activeTab.type,
                                id: file.uid,
                                height: 500,
                                width: 500
                            };
                            if (!file.meta[$scope.activeTab.type].cached) {
                                param.place = '#id' + file.uid + ' .' + $scope.activeTab.mainPlace;
                                getData($scope.activeTab.dataHandler, param, file);
                                file.meta[$scope.activeTab.type].cached = true;
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
                };

                $scope.deleteFileFromList = function(fileName) {
                    delete $scope.files[fileName];
                };

                $scope.changeFileState = function(file, state) {
                    $scope.files[file.fileName].state = state;
                }
            }]
        }
    });

    //Sidebar Directive
    app.directive('filesSidebar', function() {
        return {
            restrict: 'E',
            templateUrl: '/account/filesSidebar',
            tranclude: true,
            require: '^accountPage',
            controller: ['$scope', '$rootScope', '$http', function ($scope, $rootScope, $http) {

                $scope.setActiveState = function(state) {
                    $rootScope.state = state;
                    $rootScope.updateVisualisationTab();
                };

                $scope.setActiveFile = function(file) {
                    $rootScope.activeFileName = file.fileName;
                    $scope.setActiveState('file');
                };

                $scope.isRendering = function(file) {
                    return file.state === 'rendering';
                };

                $scope.isActiveState = function(state) {
                    return $rootScope.state === state;
                };

                $scope.isActiveFile = function(file) {
                    return file.fileName === $rootScope.activeFileName && $rootScope.isActiveState('file');
                };

                $scope.isFilesEmpty = function() {
                    var size = 0;
                    angular.forEach($rootScope.files, function(file) {
                        size++;
                    });
                    return size;
                };

                $scope.deleteFile = function(file) {
                    $http.post('/api/delete', {
                        action: 'delete',
                        fileName: file.fileName
                    }).success(function() {
                        if (file.fileName === $rootScope.activeFileName) {
                            $rootScope.state = 'accountInformation'
                        } else if ($rootScope.state != 'file') {
                            $rootScope.updateVisualisationTab();
                        }
                        $rootScope.deleteFileFromList(file.fileName);
                    });
                };

                $scope.deleteAll = function () {
                    $http.post('/api/delete', {action: 'deleteAll'})
                        .success(function () {
                            $rootScope.files = {};
                            $rootScope.state = 'accountInformation';
                        });
                };
            }]
        }});

    app.directive('mainVisualisationContent', function() {
        return {
            restrict: 'E',
            templateUrl: '/account/mainVisualisationContent',
            tranclude: true,
            require: '^accountPage',
            controller: ['$scope', '$rootScope', function($scope, $rootScope) {

                $scope.setActiveTab = function(tab) {
                    $rootScope.activeTab = tab;
                    $rootScope.updateVisualisationTab();
                };

                $scope.isActiveTab = function(tab) {
                    return $rootScope.activeTab === tab;
                };

                $scope.showTab = function() {
                    return $rootScope.state === 'file';
                };

                $scope.showFile = function(file) {
                    return file.fileName === $rootScope.activeFileName;
                }
            }]
        }
    });

    app.directive('accountInformation', function() {
        return {
            restrict: 'E',
            templateUrl: '/account/accountInformation',
            transclude: false,
            require: '^accountPage',
            controller: ['$scope', '$rootScope', function($scope, $rootScope) {
                $scope.showAccountInformation = function() {
                    return $rootScope.state === 'accountInformation';
                }
            }]
        }
    });

    app.directive('diversityContent', function() {
        return {
            restrict: 'E',
            templateUrl: '/account/diversityContent',
            transclude: false,
            require: '^accountPage',
            controller: ['$scope', '$rootScope', function($scope, $rootScope) {
                $scope.showDiversity = function() {
                    return $rootScope.state === 'diversity';
                }
            }]
        }
    });

    app.directive('summaryContent', function() {
        return {
            restrict: 'E',
            templateUrl: '/account/summaryContent',
            tranclude: false,
            require: '^accountPage',
            controller: ['$scope', '$rootScope', function($scope, $rootScope) {
                $scope.showSummary = function() {
                    return $rootScope.state === 'summary';
                }
            }]
        }
    });

    app.directive('fileUpload', function() {
        return {
            restrict: 'E',
            templateUrl: '/account/fileUpload',
            tranclude: true,
            require: '^accountPage',
            controller: ['$scope', '$http', '$rootScope', function ($scope, $http, $rootScope) {

                var uid = 0;

                $scope.newFiles = {};
                $scope.uploadedFiles = [];
                $scope.commonSoftwareType = 'mitcr';


                $scope.isNewFilesEmpty = function() {
                    return Object.keys($scope.newFiles).length;
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
                    angular.forEach($scope.newFiles, function(file) {
                        file.softwareTypeName = $scope.commonSoftwareType;
                    });
                };

                $scope.uploadAll = function () {
                    angular.forEach($scope.newFiles, function(file) {
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
                    dropZone: $('#new-files-dropzone'),
                    add: function (e, data) {
                        var originalFileName = data.files[0].name;
                        var fileName = originalFileName.substr(0, originalFileName.lastIndexOf('.')) || originalFileName;
                        var fileExtension = originalFileName.substr((~-originalFileName.lastIndexOf(".") >>> 0) + 2);
                        if (fileExtension != 'txt' && fileExtension != 'gz') {
                            fileName += fileExtension;
                            fileExtension = 'txt';
                        }
                        if ($rootScope.files.length >= 25) {
                            $scope.addNewError(uid++, fileName, 0);
                        } else if ($rootScope.isContain(fileName)) {
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
                                                    $rootScope.addFileToList(file);
                                                    break;
                                                case "end" :
                                                    $rootScope.changeFileState(file, 'rendered')
                                                    $rootScope.updateVisualisationTab();
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
                                            $rootScope.deleteFileFromList(file.fileName);
                                            $scope.updateResult(file, 'error');
                                            $scope.updateResultTooltip(file, event["message"]);
                                            break;
                                        default:
                                            $rootScope.deleteFileFromList(file.fileName);
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

            }]

        }
    });

    app.directive('comparingContent', function() {
        return {
            restrict: 'E',
            templateUrl: '/account/comparingContent',
            tranclude: false,
            require: '^accountPage',
            controller: ['$scope', '$rootScope', function($scope, $rootScope) {

                $scope.showComparing = function() {
                    return $rootScope.state === 'comparing';
                };

                $scope.showItem = function(file, tab) {
                    if (!$rootScope.files[file.fileName].meta[tab.type].comparingCache) {
                        var param = {
                            fileName: file.fileName,
                            id: file.uid + '_comparing',
                            height:  (tab.type === 'vjusage') ? 320 : 520,
                            width: 300,
                            type: tab.type,
                            place: '#id' + file.uid + ' .' + tab.comparingPlace
                        };
                        getData(tab.dataHandler, param, file);
                    }
                    $rootScope.files[file.fileName].meta[tab.type].comparing = !$rootScope.files[file.fileName].meta[tab.type].comparing;
                    $rootScope.files[file.fileName].meta[tab.type].comparingCache = true;
                };

                $scope.showAllItems = function(tab) {
                    var shown = 0;
                    angular.forEach($rootScope.files, function(file) {
                        if (!file.meta[tab.type].comparing) {
                            $scope.showItem(file, tab);
                            shown++;
                        }
                    });
                    if (shown == 0) {
                        angular.forEach($rootScope.files, function(file) {
                            $scope.showItem(file, tab);
                        })
                    }
                }
            }]
        }
    });

    app.filter('comparingFilter', function() {
        return function(input) {
            var filteredInput = [];
            angular.forEach(input, function(element) {
                if (element.comparing)
                    filteredInput.push(element);
            });
            return filteredInput;
        };
    });

})();



function getData(handleData, param, file) {
    if (typeof file != 'undefined' && file.meta[param.type].data.length != 0) {
        handleData(file.meta[param.type].data, param);
    } else {
        loading(param.place);
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
                loaded(param.place);
                if (!data) {
                    location.reload();
                }
                switch (data["result"]) {
                    case "success" :
                        handleData(data.data, param);
                        if (typeof file != 'undefined') {
                            file.meta[param.type].data = data.data;
                        }
                        break;
                    case "error" :
                        d3.select(param.place).html("No data available");
                        break;
                    default :
                        d3.select(param.place).html("No data available");
                        break;
                }
            },
            error: function () {
                location.reload();
            }
        });
    }
}

function spectrotype(data, param) {
    nv.addGraph(function () {

        var place = d3.select(param.place).html("");
        console.log(place);
        console.log(param.place);
        var svg = d3.select(param.place).append("div")
            .attr("id", "chart")
            .append("svg")
            .attr("id", "svg_spectrotype_" + param.id)
            .style("height", param.height)
            .style("width", "100%")
            .style("overflow", "visible");

        var chart = nv.models.multiBarChart()
                .duration(1000)
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
            .attr("id", "svg_spectrotypeV_" + param.id)
            .style("height", param.height + "px")
            .style("width", "100%")
            .style("overflow", "visible");

        var chart = nv.models.multiBarChart()
                .duration(1000)
                .reduceXTicks(false)   //If 'false', every single x-axis tick label will be rendered.
                .rotateLabels(0)      //Angle to rotate x-axis labels.
                .showControls(false)   //Allow user to switch between 'Grouped' and 'Stacked' mode.
                .showLegend(true)
                .groupSpacing(0.1)    //Distance between each group of bars.
                .height(param.height)
                .stacked(true)
                .tooltip(function (key, x, y, e, graph) {
                    if (e.series.values[e.pointIndex].y != 0) {
                        return '<h3>' + key + '</h3>' +
                            '<p>Length : ' + x + '</p>' +
                            '<p>Frequency : ' + e.series.values[e.pointIndex].y + '</p>';
                    } else {
                        return null;
                    }
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

function sizeClassifying(data, param) {
    nv.addGraph(function() {
        d3.select(param.place).html("");
        var svg = d3.select(param.place).append("div")
            .attr("id", "chart")
            .append("svg")
            .attr("id", "svg_kernelDensity_" + param.id)
            .style("height", param.height + "px")
            .style("width", "100%")
            .style("overflow", "visible");


        var chart = nv.models.pieChart()
                .x(function(d) { return d.label })
                .y(function(d) { return d.value })
                .showLabels(true)     //Display pie labels
                .labelThreshold(.05)  //Configure the minimum slice size for labels to show up
                .labelType("percent") //Configure what type of data to show in the label. Can be "key", "value" or "percent"
                .donut(true)          //Turn on Donut mode. Makes pie chart look tasty!
                .donutRatio(0.35)     //Configure how big you want the donut hole size to be.
            ;

        svg
            .datum(data)
            .transition().duration(350)
            .call(chart);

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
            .attr("id", "annotation_table_" + param.id)
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

    var dataTable = $('#annotation_table_' + param.id).dataTable({
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

function vjUsage(data, param) {

    var fill = d3.scale.category10();

    // Visualize
    var chord = d3.layout.chord()
        .padding(.05)
        .sortSubgroups(d3.descending)
        .matrix(data.matrix);

    var width = param.width,
        height = param.height,
        r1 = height / 2,
        innerRadius = Math.min(width, height) * .41,
        outerRadius = innerRadius * 1.1,
        outer;

    var svg = d3.select(param.place)
        .append("svg")
        .attr("width", width + 200)
        .attr("height", height + 200)
        .style("width", width+200)
        .style("height", height+200)
        .style("display", "block")
        .style("margin", "auto")
        .attr("id", "svg_vjusage_" + param.id)
        .append("g")
        .attr("transform", "translate(" + (width+200) / 2 + "," + (height+200) / 2 + ")");

    svg.append("g").selectAll("path")
        .data(chord.groups)
        .enter().append("path")
        .attr("class", "arc")
        .style("fill", function(d) {
            return d.index < 4 ? '#444444' : fill(d.index);
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
        .style("fill", function(d) { return fill(d.target.index); })
        .style("opacity", 0.7);

    svg.append("g").selectAll(".arc")
        .data(chord.groups)
        .enter().append("svg:text")
        .attr("dy", ".35em")
        .attr("text-anchor", function(d) { return ((d.startAngle + d.endAngle) / 2) > Math.PI ? "end" : null; })
        .attr("transform", function(d) {
            return "rotate(" + (((d.startAngle + d.endAngle) / 2) * 180 / Math.PI - 90) + ")"
                + "translate(" + (r1 - 15) + ")"
                + (((d.startAngle + d.endAngle) / 2) > Math.PI ? "rotate(180)" : "");
        })
        .text(function(d) {
            return data.labels[d.index];
        });

    // Returns an event handler for fading a given chord group.
    function fade(opacity) {
        return function(g, i) {
            svg.selectAll(".chord path")
                .filter(function(d) { return d.source.index != i && d.target.index != i; })
                .transition()
                .style("opacity", opacity);
        };
    }
}

function diversityStats(data, param) {
    nv.addGraph(function () {
        d3.select(param.place).html("");

        var svg = d3.select(param.place)
            .append("svg")
            .attr("id", "diversity-png-export")
            .style("height", "500px")
            .style("width", "100%")
            .style("margin-top", "50px");

        var chart = nv.models.lineChart()
            .useInteractiveGuideline(true)
            .duration(1000)
            .showLegend(true)
            .showYAxis(true)
            .showXAxis(true)
            .height(500);

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

function loading(place) {
    d3.select(place).style("display", "block");
    var loading = d3.select(place).append("div").attr("class", "loading");
    loading.append("div").attr("class", "wBall").attr("id", "wBall_1").append("div").attr("class", "wInnerBall");
    loading.append("div").attr("class", "wBall").attr("id", "wBall_2").append("div").attr("class", "wInnerBall");
    loading.append("div").attr("class", "wBall").attr("id", "wBall_3").append("div").attr("class", "wInnerBall");
    loading.append("div").attr("class", "wBall").attr("id", "wBall_4").append("div").attr("class", "wInnerBall");
    loading.append("div").attr("class", "wBall").attr("id", "wBall_5").append("div").attr("class", "wInnerBall");
}

function loaded(place) {
    d3.select(place)
        .select(".loading")
        .remove();
}

$(document).bind('dragover', function (e) {
    var dropZone = $('#new-files-dropzone'),
        timeout = window.dropZoneTimeout;
    if (!timeout) {
        dropZone.addClass('in');
    } else {
        clearTimeout(timeout);
    }
    var found = false,
        node = e.target;
    do {
        if (node === dropZone[0]) {
            found = true;
            break;
        }
        node = node.parentNode;
    } while (node != null);
    if (found) {
        dropZone.addClass('hover');
    } else {
        dropZone.removeClass('hover');
    }
    window.dropZoneTimeout = setTimeout(function () {
        window.dropZoneTimeout = null;
        dropZone.removeClass('in hover');
    }, 100);
});
