var CONSOLE_INFO = true;

(function () {

    var RenderState = Object.freeze({
        RENDERED: 0,
        RENDERING: 1,
        WAIT: 2
    });

    var htmlState = Object.freeze({
        ACCOUNT_INFORMATION: 0,
        FILE: 1,
        RAREFACTION: 2,
        SUMMARY: 3,
        COMPARING: 4
    });

    var app = angular.module('accountPage', []);

    app.directive('onLastRepeat', function() {
        return function(scope, element, attrs) {
            if (scope.$last) setTimeout(function(){
                scope.$emit('onRepeatLast', element, attrs);
            }, 1);
        };
    });


    //This factory handle account information
    //and user's files
    app.factory('accountInfo', ['$log', '$http', 'stateInfo', function($log, $http, stateInfo) {
        "use strict";
        //Private variables
        var maxFilesCount = 0, maxFileSize = 0, filesCount = 0;
        var email = "", firstName = "", lastName = "", userName = "";
        var rarefactionCache = false;
        var initialized = false;
        var initializeError = false;
        var files = {};
        var uid = 0;

        //Initializing account information
        if (CONSOLE_INFO) {
            $log.info('Initializing account information');
        }
        $http.get('/account/api/info')
            .success(function(response) {
                initialized = true;
                var info = response.data;
                email = info.email;
                firstName = info.firstName;
                lastName = info.lastName;
                userName = info.userName;
                filesCount = info.filesCount;
                maxFilesCount = info.filesInformation.maxFilesCount;
                maxFileSize = info.filesInformation.maxFileSize;
                //TODO rarefaction cache
                angular.forEach(info.filesInformation.files, function(file) {
                    files[file.fileName] = {
                        uid: uid++,
                        fileName: file.fileName,
                        state: file.state,
                        softwareTypeName: file.softwareTypeName,
                        meta: {
                            vjusage: {
                                cached: false,
                                comparing: false
                            },
                            spectratype: {
                                cached: false,
                                comparing: false
                            },
                            spectratypeV: {
                                cached: false,
                                comparing: false
                            },
                            quantileStats: {
                                cached: false,
                                comparing: false
                            },
                            annotation: {
                                cached: false,
                                comparing: false
                            }
                        }
                    }
                });
                if (CONSOLE_INFO) {
                    $log.info('Account initialized');
                    $log.info('User: ' + userName);
                    $log.info('Files count: ' + filesCount);
                    $log.info('Max files count: ' + maxFilesCount);
                    $log.info('Max file size: ' + maxFileSize);
                }
            })
            .error(function() {
                initializeError = true;
            });

        //Public api
        //-----------------------------//
        //Getter for user's files
        function getFiles() {
            return files;
        }

        //Delete file from server and client-side application
        function deleteFile(file) {
            if (CONSOLE_INFO) {
                $log.info('Trying to delete file ' + file.fileName);
            }
            $http.post('/account/api/delete', { action: 'delete', fileName: file.fileName })
                .success(function () {
                    if (stateInfo.isActiveFile(file) || Object.keys(files).length == 1) {
                        stateInfo.setActiveState(htmlState.ACCOUNT_INFORMATION);
                    } else if (!stateInfo.isActiveState(htmlState.FILE)) {
                        //TODO update visualisation tab
                    }
                    rarefactionCache = false;
                    delete files[file.fileName];

                    if (CONSOLE_INFO) {
                        $log.info('File ' + file.fileName + ' successfully deleted');
                    }
                }).error(function(response) {
                    if (CONSOLE_INFO) {
                        $log.error('Error while deleting file ' + file.fileName);
                        $log.error(response.message);
                    }
                });
        }

        //Delete file only from client-side application
        function deleteFile_client(file) {
            delete files[file.fileName];
        }

        //Delete all files
        function deleteAll() {
            if (CONSOLE_INFO) {
                $log.info('Trying to delete all files')
            }
            $http.post('/account/api/delete', {action: 'deleteAll'})
                .success(function () {
                    var file;
                    for (file in files) { if (files.hasOwnProperty(file)) { delete files[file]; } }
                    stateInfo.setActiveState(htmlState.ACCOUNT_INFORMATION);
                    if (CONSOLE_INFO) {
                        $log.info('All files successfully deleted');
                    }
                })
                .error(function() {
                    if (CONSOLE_INFO) {
                        $log.error('Error while deleting files');
                    }
                });
        }

        //Checking initializing
        function isInitialized() {
            return initialized === true;
        }

        //Checking initializing error
        function isInitializeError() {
            return initializeError === true;
        }

        //Checking on unique name of file
        function isFileAlreadyExist(fileName) {
            return fileName in files;
        }

        //Getter for max files count
        function getMaxFilesCount() {
            return maxFilesCount;
        }

        //Getter for max file size
        function getMaxFileSize() {
            return maxFileSize;
        }

        //Adding new file
        function addNewFile(file) {
            files[file.fileName] = {
                uid: uid++,
                fileName: file.fileName,
                state: RenderState.RENDERING,
                softwareTypeName: file.softwareTypeName,
                meta: {
                    vjusage: {
                        cached: false,
                        comparing: false
                    },
                    spectratype: {
                        cached: false,
                        comparing: false
                    },
                    spectratypeV: {
                        cached: false,
                        comparing: false
                    },
                    quantileStats: {
                        cached: false,
                        comparing: false
                    },
                    annotation: {
                        cached: false,
                        comparing: false
                    }
                }
            }
        }

        function changeFileState(file, state) {
            files[file.fileName].state = state;
        }
        //------------------------------//

        return {
            getFiles: getFiles,
            deleteFile: deleteFile,
            deleteFile_client: deleteFile_client,
            deleteAll: deleteAll,
            isInitialized: isInitialized,
            isInitializeError: isInitializeError,
            isFileAlreadyExist: isFileAlreadyExist,
            getMaxFilesCount: getMaxFilesCount,
            getMaxFileSize: getMaxFileSize,
            addNewFile: addNewFile,
            changeFileState: changeFileState

        }
    }]);

    //This factory handles global state of application
    app.factory('stateInfo', function() {
        "use strict";
        //Private variables
        var state = htmlState.ACCOUNT_INFORMATION;
        var activeFile = {};


        //Public api
        //------------------------------//
        //Setter for active file
        function setActiveFile(file) {
            state = htmlState.FILE;
            activeFile = file;
        }

        //Getter for active file
        function getActiveFile() {
            return activeFile;
        }

        //Checking active file
        function isActiveFile(file) {
            return activeFile === file && state === htmlState.FILE;
        }

        //Setter for global state
        function setActiveState(st) {
            state = st;
        }

        //Checking global state
        function isActiveState(st) {
            return state === st;
        }

        //Getter for global state
        function getActiveState() {
            return state;
        }
        //-------------------------------//


        return {
            setActiveFile: setActiveFile,
            getActiveFile: getActiveFile,
            isActiveFile: isActiveFile,
            setActiveState: setActiveState,
            getActiveState: getActiveState,
            isActiveState: isActiveState
        }
    });

    //Main Directive
    app.directive('accountPage', function () {
        return {
            restrict: 'E',
            controller: ['$scope', 'accountInfo', 'stateInfo', function ($scope, accountInfo, stateInfo) {
                //State info api
                $scope.isActiveState = stateInfo.isActiveState;
                $scope.getActiveFile = stateInfo.getActiveFile;


                $scope.isAccountInformation = function() {
                    return stateInfo.isActiveState(htmlState.ACCOUNT_INFORMATION);
                };

                $scope.isFileInformation = function() {
                    return stateInfo.isActiveState(htmlState.FILE);
                };

                $scope.isComparingInformation = function() {
                    return stateInfo.isActiveState(htmlState.COMPARING);
                };

                $scope.isSummaryInformation = function() {
                    return stateInfo.isActiveState(htmlState.SUMMARY);
                };

                $scope.isRarefactionInformation = function() {
                    return stateInfo.isActiveState(htmlState.RAREFACTION);
                };

                $scope.showStartPage = function() {
                    $scope.state = htmlState.ACCOUNT_INFORMATION;
                };

                $scope.isContain = function (file) {
                    angular.forEach($scope.files, function (f) {
                        if (file.fileName === f.fileName) {
                            return true;
                        }
                    });
                    return false;
                };

                $scope.updateVisualisationTab = function () {
                    var param = {};
                    switch ($scope.state) {
                        case htmlState.FILE:
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
                        case htmlState.RAREFACTION:
                            param.place = '.rarefaction-visualisation-tab';
                            param.fileName = 'all';
                            param.type = 'rarefaction';
                            param.needToCreateNew = needToCreateNew;
                            needToCreateNew = false;
                            getData(rarefactionPlot, param);
                            break;
                        case htmlState.SUMMARY:
                            param.place = '.summary-visualisation-tab';
                            param.fileName = 'all';
                            param.type = 'summary';
                            getData(summaryStats, param);
                            break;
                        default:
                            break
                    }
                };

                $scope.changeFileState = function (file, state) {
                    $scope.files[file.fileName].state = state;
                };

                $scope.isRenderingFilesExists = function() {
                    var exist = false;
                    angular.forEach($scope.files, function(file) {
                        if (file.state === RenderState.RENDERING) exist = true;
                    });
                    return exist;
                };
            }]
        }
    });

    //Sidebar Directive
    app.directive('filesSidebar', function () {
        return {
            restrict: 'E',
            controller: ['$scope', 'accountInfo', 'stateInfo', function ($scope, accountInfo, stateInfo) {
                //Variables
                $scope.files = accountInfo.getFiles();


                //Account Info api
                $scope.deleteFile = accountInfo.deleteFile;
                $scope.deleteAll = accountInfo.deleteAll;

                //State info api
                $scope.setActiveFile = stateInfo.setActiveFile;
                $scope.isActiveFile = stateInfo.isActiveFile;
                $scope.isActiveState = stateInfo.isActiveState;
                $scope.setActiveState = stateInfo.setActiveState;

                //Public functions
                $scope.showNewFilesTable = showNewFilesTable;
                $scope.isFilesEmpty = isFilesEmpty;
                $scope.isFileRendering = isFileRendering;
                $scope.setRarefactionState = setRarefactionState;
                $scope.setSummaryState = setSummaryState;
                $scope.setCompareState = setCompareState;
                $scope.isRarefactionState = isRarefactionState;
                $scope.isSummaryState = isSummaryState;
                $scope.isCompareState = isCompareState;
                $scope.showCompareModal = showCompareModal;

                function showCompareModal() {
                    $('#comparingAddButton').click();
                }

                function setRarefactionState() {
                    stateInfo.setActiveState(htmlState.RAREFACTION);
                }

                function setSummaryState() {
                    stateInfo.setActiveState(htmlState.SUMMARY);
                }

                function setCompareState() {
                    stateInfo.setActiveState(htmlState.COMPARING);
                }

                function isRarefactionState() {
                    return stateInfo.isActiveState(htmlState.RAREFACTION);
                }

                function isSummaryState() {
                    return stateInfo.isActiveState(htmlState.SUMMARY);
                }

                function isCompareState() {
                    return stateInfo.isActiveState(htmlState.COMPARING);
                }

                function showNewFilesTable() {
                    $("#add-new-file").click();
                }

                function isFileRendering(file) {
                    return file.state === RenderState.RENDERING;
                }

                function isFilesEmpty() {
                    return Object.keys($scope.files).length === 0;
                }

                $scope.$on('onRepeatLast', function () {
                    $('#side-menu').slimScroll({
                        height: '100%',
                        color: '#1abc9c',
                        railColor: '#1abc9c',
                        railOpacity: 0.8,
                        disableFadeOut: true,
                        wheelStep: 20
                    });
                });
            }]
        }
    });

    //File visualisation Directive
    app.directive('mainVisualisationContent', function () {
        return {
            restrict: 'E',
            controller: ['$scope', 'stateInfo', function ($scope, stateInfo) {
                var createTab = function (tabName, type, dataHandler, mainPlace, comparing, exportPng, exportType, comparingPlace) {
                    return {
                        tabName: tabName,
                        type: type,
                        dataHandler: dataHandler,
                        mainPlace: mainPlace,
                        comparing: comparing,
                        exportPng: exportPng,
                        exportType: exportType,
                        comparingPlace: comparingPlace
                    }
                };
                $scope.visualisationTabs = {
                    vjusage: createTab('V-J Usage', 'vjusage', vjUsage, 'visualisation-results-vjusage', true, true, ['JPEG'], 'comparing-vjusage-tab'),
                    spectratype: createTab('Spectratype', 'spectratype', spectratype, 'visualisation-results-spectratype', true, true, ['PNG', 'JPEG'], 'comparing-spectratype-tab'),
                    spectratypev: createTab('V Spectratype ', 'spectratypeV', spectratypeV, 'visualisation-results-spectratypeV', true, true, ['PNG', 'JPEG'], 'comparing-spectratypeV-tab'),
                    quantilestats: createTab('Quantile Plot', 'quantileStats', quantileSunbirstChart, 'visualisation-results-quantileStats', true, true, ['PNG', 'JPEG'], 'comparing-quantileStats-tab'),
                    annotation: createTab('Clonotypes', 'annotation', annotationTable, 'visualisation-results-annotation', false, false)
                };
                $scope.activeTab = $scope.visualisationTabs.vjusage;


                $scope.exportChartPng = function (file, tab, exportType) {
                    saveSvgAsPng(document.getElementById('svg_' + tab.type + '_' + file.uid), file.fileName + "_" + tab.type, 3, exportType);
                };

                $scope.setActiveTab = function (tab) {
                    $scope.activeTab = tab;
                    //TODO update visuzlisation
                };

                $scope.isActiveTab = function (tab) {
                    return $scope.activeTab === tab;
                };

                $scope.showFile = function (file) {
                    return stateInfo.isActiveFile(file);
                }
            }]
        }
    });

    //Account information Directive
    app.directive('accountInformation', function () {
        return {
            restrict: 'E',
            templateUrl: '/account/accountInformation'
        }
    });

    //Rarefaction tab Directive
    app.directive('rarefactionContent', function () {
        return {
            restrict: 'E',
            controller: ['$scope', function ($scope) {

                $scope.rarefactionExportTypes = ['JPEG'];
                $scope.area = 'Hide';

                $scope.changeArea = function() {
                    d3.select('.rarefaction-visualisation-tab')
                        .selectAll('.nv-area')
                        .style('visibility', function() {
                            switch ($scope.area) {
                                case 'Hide':
                                    return 'hidden';
                                case 'Show':
                                    return 'visible';
                                default:
                                    return 'visible';
                            }
                        });

                    if ($scope.area === 'Show') {
                        $scope.area = 'Hide'
                    } else {
                        $scope.area = 'Show'
                    }

                };

                $scope.exportRarefaction = function (type) {
                    saveSvgAsPng(document.getElementById('rarefaction-png-export'), 'rarefaction', 3, type);
                }
            }]
        }
    });

    //Upload support Directive
    app.directive('fileUpload', function () {
        return {
            restrict: 'E',
            controller: ['$scope', '$http', 'accountInfo', function ($scope, $http, accountInfo) {
                //Private var
                var uid = 0;
                var errors = Object.freeze({
                    FILES_COUNT_EXCEEDED_ERROR: 0,
                    UNIQUE_NAME_CONFLICT_ERROR: 1,
                    FILE_IS_TOO_LARGE_ERROR: 2
                });

                //Public var
                $scope.newFiles = {};
                $scope.uploadedFiles = [];
                $scope.commonSoftwareType = 'mitcr';

                //Public Functions
                $scope.isNameValid = isNameValid;
                $scope.isNewFilesEmpty = isNewFilesEmpty;
                $scope.addNewButton = addNewButton;
                $scope.changeCommonSoftwareType = changeCommonSoftwareType;
                $scope.uploadAll = uploadAll;
                $scope.uploadFile = uploadFile;
                $scope.isOk = isOk;
                $scope.isSuccess = isSuccess;
                $scope.isError = isError;
                $scope.deleteFromQuery = deleteFromQuery;


                function isNameValid(file){
                    var regexp = /^[a-zA-Z0-9_.+-]{1,40}$/;
                    var valid = regexp.test(file.fileName);
                    return valid;
                }

                function isNewFilesEmpty() {
                    return Object.keys($scope.newFiles).length;
                }

                function addNewButton() {
                    $("form input[type=file]").click();
                }

                function changeCommonSoftwareType() {
                    angular.forEach($scope.newFiles, function (file) {
                        if (file.wait)
                            file.softwareTypeName = $scope.commonSoftwareType;
                    });
                }

                function uploadAll() {
                    angular.forEach($scope.newFiles, function (file) {
                        uploadFile(file);
                    })
                }

                function uploadFile(file) {
                    if (isWait(file) && isNameValid(file)) {
                        updateTooltip(file, "Uploading");
                        file.data.formData = {
                            softwareTypeName: file.softwareTypeName,
                            fileName: file.fileName,
                            fileExtension: file.fileExtension,
                            uid: file.uid
                        };
                        file.wait = false;
                        file.data.submit();
                    }
                }

                function isOk(file) {
                    return file.result === 'ok' || file.result === 'success';
                }

                function isSuccess(file) {
                    return file.result === 'success';
                }

                function isError(file) {
                    return file.result === 'error';
                }

                function deleteFromQuery(file) {
                    delete $scope.newFiles[file.uid];
                }


                //Private Functions
                function filesCount() {
                    var added = Object.keys(accountInfo.getFiles()).length;
                    var waiting = 0;
                    angular.forEach($scope.newFiles, function (file) {
                        if (file.wait) waiting++;
                    });
                    return added + waiting;
                }

                function isRenderingFilesExist() {
                    var exist = false;
                    angular.forEach($scope.newFiles, function(file) {
                        if (file.state === RenderState.RENDERING) exist = true;
                    });
                    return exist;
                }

                function addNew(uid, fileName, fileExtension, data)  {
                    $scope.$apply(function () {
                        $scope.newFiles[uid] = {
                            uid: uid,
                            fileName: fileName,
                            softwareTypeName: 'mitcr',
                            fileExtension: fileExtension,
                            state: RenderState.WAIT,
                            wait: true,
                            tooltip: '',
                            progress: 0,
                            result: 'ok',
                            resultTooltip: '',
                            data: data
                        };
                    });
                    $('#invalid-name-popover-' + uid).popover({
                        trigger: 'focus'
                    });
                }

                function updateTooltip(file, tooltip) {
                    file.tooltip = tooltip;
                }

                function updateProgress(file, progress) {
                    $scope.$apply(function () {
                        file.progress = progress;
                    })
                }

                function updateResult(file, result) {
                    $scope.$apply(function () {
                        file.result = result;
                        file.state = RenderState.RENDERED;
                    })
                }

                function updateResultTooltip(file, resultTooltip) {
                    $scope.$apply(function () {
                        file.resultTooltip = resultTooltip;
                    })
                }

                function addNewError(uid, fileName, error) {
                    var resultTooltip;
                    switch (error) {
                        case errors.FILES_COUNT_EXCEEDED_ERROR:
                            resultTooltip = 'You have exceeded limit of files';
                            break;
                        case errors.UNIQUE_NAME_CONFLICT_ERROR:
                            resultTooltip = 'You should use unique names for your files';
                            break;
                        case errors.FILE_IS_TOO_LARGE_ERROR:
                            resultTooltip = 'File is too large';
                            break;
                        default:
                            resultTooltip = 'Server is unavailable';
                    }
                    $scope.$apply(function() {
                       $scope.newFiles[uid] = {
                           uid: uid,
                           fileName: fileName,
                           softwareTypeName: '',
                           wait: false,
                           result: 'error',
                           resultTooltip: resultTooltip
                       }
                    });
                }

                function isContain(fileName) {
                    var contain = false;
                    angular.forEach($scope.newFiles, function (file) {
                        if (file.fileName == fileName && file.wait) contain = true;
                    });
                    return accountInfo.isFileAlreadyExist(fileName)|| contain;
                }

                function isCountExceeded() {
                    return accountInfo.getMaxFilesCount() > 0 && filesCount() >= accountInfo.getMaxFilesCount();
                }

                function isSizeExceeded(file) {
                    return accountInfo.getMaxFileSize() > 0 && (file.size  / 1024 ) > accountInfo.getMaxFileSize();
                }

                function isWait(file) {
                    return file.wait;
                }

                $('#fileupload').fileupload({
                    url: '/account/api/upload',
                    dataType: 'json',
                    sequentialUploads: true,
                    dropZone: $('#new-files-dropzone'),
                    add: function (e, data) {
                        var file = data.files[0];
                        var originalFileName = file.name;
                        var fileName = originalFileName.substr(0, originalFileName.lastIndexOf('.')) || originalFileName;
                        var fileExtension = originalFileName.substr((~-originalFileName.lastIndexOf(".") >>> 0) + 2);
                        if (fileExtension != 'txt' && fileExtension != 'gz') {
                            fileName += fileExtension;
                            fileExtension = 'txt';
                        }
                        if (isCountExceeded()) {
                            addNewError(uid++, fileName, errors.FILES_COUNT_EXCEEDED_ERROR);
                        } else if (isContain(fileName)) {
                            addNewError(uid++, fileName, errors.UNIQUE_NAME_CONFLICT_ERROR);
                        } else if (isSizeExceeded(file)) {
                            addNewError(uid++, fileName, errors.FILE_IS_TOO_LARGE_ERROR);
                        } else {
                            addNew(uid++, fileName, fileExtension, data);
                        }
                    },
                    progress: function (e, data) {
                        var file = $scope.newFiles[data.formData.uid];
                        updateProgress(file, parseInt(data.loaded / data.total * 50, 10));
                    },
                    done: function (e, data) {
                        var file = $scope.newFiles[data.formData.uid];
                        switch (data.result["result"]) {
                            case "success" :
                                var socket = new WebSocket("ws://" + location.host + "/account/api/ws");
                                socket.onmessage = function (message) {
                                    var event = JSON.parse(message["data"]);
                                    switch (event["result"]) {
                                        case "ok" :
                                            switch (event["progress"]) {
                                                case "start" :
                                                    updateTooltip(file, "Computation");
                                                    accountInfo.addNewFile(file);
                                                    break;
                                                case "end" :
                                                    accountInfo.changeFileState(file, RenderState.RENDERED);
                                                    updateTooltip(file, "Success");
                                                    updateResult(file, 'success');
                                                    socket.close();
                                                    if (!isRenderingFilesExist()) {
                                                        //TODO update visualisation
                                                    }
                                                    break;
                                                default:
                                                    updateProgress(file, 50 + (event.progress / 2));
                                            }
                                            break;
                                        case "error" :
                                            socket.close();
                                            accountInfo.deleteFile_client(file);
                                            updateResult(file, 'error');
                                            updateResultTooltip(file, event["message"]);
                                            break;
                                        default:
                                            accountInfo.deleteFile_client(file);
                                            updateTooltip(file, "Server unavailable");
                                            break;
                                    }

                                };
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
                                break;
                            case "error" :
                                updateResult(file, 'error');
                                updateResultTooltip(file, data.result.message);
                                break;
                            default:
                                updateResult(file, 'error');
                                updateResultTooltip(file, "Server unavailable");
                        }

                    }
                });

                $('#new-files-table').on('hidden.bs.modal', function () {
                    angular.forEach($scope.newFiles, function (file) {
                        switch (file.result) {
                            case 'success':
                                $scope.$apply(function () {
                                    $scope.uploadedFiles.push({
                                        fileName: file.fileName,
                                        softwareTypeName: file.softwareTypeName
                                    });
                                    delete $scope.newFiles[file.uid];
                                });
                                break;
                            case 'error':
                                $scope.$apply(function () {
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

    //Comparing Directive
    app.directive('comparingContent', function () {
        return {
            restrict: 'E',
            controller: ['$scope', 'accountInfo', function ($scope, accountInfo) {

                $scope.comparingItems = [];
                $scope.files = accountInfo.getFiles();
                $scope.isComparing = isComparing;
                $scope.isRendered = isRendered;
                $scope.isRendering = isRendering;
                $scope.showAllItems = showAllItems;
                $scope.switchItem = switchItem;
                $scope.isItems = isItems;

                function isItems() {
                    return $scope.comparingItems.length > 0;
                }

                function isComparing(file, tab) {
                    return file.meta[tab.type].comparing;
                }

                function isRendered(file) {
                    return file.state === RenderState.RENDERED;
                }

                function isRendering(file) {
                    return file.state === RenderState.RENDERING;
                }

                function switchItem(file, tab) {
                    if (isComparing(file, tab)) {
                        deleteItem(file, tab)
                    } else {
                        showItem(file, tab);
                    }
                }

                function deleteItem(file, tab) {
                    for (var i = 0; i < $scope.comparingItems.length; ++i) {
                         if ($scope.comparingItems[i].fileName === file.fileName && $scope.comparingItems[i].tabName === tab.tabName) {
                             $scope.comparingItems.splice(i, 1);
                             $scope.files[file.fileName].meta[tab.type].comparing = false;
                             break;
                         }
                    }
                }

                function deleteAllItems(tab) {
                    var cleanedArray = [];
                    for (var i = 0; i < $scope.comparingItems.length; i++) {
                        if ($scope.comparingItems[i].tabName != tab.tabName) {
                            cleanedArray.push($scope.comparingItems[i]);
                        } else {
                            $rootScope.files[$scope.comparingItems[i].fileName].meta[tab.type].comparing = false;
                        }
                    }
                    $scope.comparingItems.splice(0, $scope.comparingItems.length);
                    $scope.comparingItems = cleanedArray;
                }

                function showItem(file, tab) {
                    if (file.state != RenderState.RENDERING) {
                        $scope.comparingItems.push({
                            fileName: file.fileName,
                            tabName: tab.tabName,
                            place: tab.comparingPlace,
                            uid: file.uid
                        });
                        $scope.files[file.fileName].meta[tab.type].comparing = true;
                        var param = {
                            fileName: file.fileName,
                            id: file.uid + '_comparing',
                            height: (tab.type === 'vjusage') ? 320 : 520,
                            width: 300,
                            type: tab.type,
                            place: '#id' + file.uid + ' .' + tab.comparingPlace
                        };
                        getData(tab.dataHandler, param, file);
                    }
                }

                function showAllItems(tab) {
                    var shown = 0;
                    angular.forEach($scope.files, function (file) {
                        if (!file.meta[tab.type].comparing) {
                            showItem(file, tab);
                            shown++;
                        }
                    });
                    if (shown == 0) {
                        deleteAllItems(tab);
                    }
                }
            }]
        }
    });

    //Block account page Directive
    app.directive('blockPage', function() {
        return {
            restrict: 'E',
            template: '<div class="block-page" ng-show="blockPage()">' +
                            '<div class="background"></div>' +
                            '<div class="info">' +
                                '<div class="text-info">' +
                                    '<text ng-hide="error()">Initializing...</text>' +
                                    '<text ng-show="error()">Error while initializing</text>' +
                                '</div>' +
                                '<div class="loading" ng-hide="error()">' +
                                    '<div class="wBall" id="wBall_1">' +
                                        '<div class="wInnerBall"></div>' +
                                    '</div>' +
                                    '<div class="wBall" id="wBall_2">' +
                                        '<div class="wInnerBall"></div>' +
                                    '</div>' +
                                    '<div class="wBall" id="wBall_3">' +
                                        '<div class="wInnerBall"></div>' +
                                    '</div>' +
                                    '<div class="wBall" id="wBall_4">' +
                                        '<div class="wInnerBall"></div>' +
                                    '</div>' +
                                    '<div class="wBall" id="wBall_5">' +
                                        '<div class="wInnerBall"></div>' +
                                    '</div>' +
                                '</div>' +
                            '</div>' +
                        '</div>',
            controller: ['$scope', 'accountInfo', function($scope, accountInfo) {
                $scope.blockPage = function() {
                    return !accountInfo.isInitialized();
                };

                $scope.error = function() {
                    return accountInfo.isInitializeError();
                }

            }]
        }
    });

    //Filter for comparing files
    app.filter('comparingFilter', function () {
        return function (input) {
            var filteredInput = [];
            angular.forEach(input, function (element) {
                if (element.comparing)
                    filteredInput.push(element);
            });
            return filteredInput;
        };
    });

})();

//Request data from server
function getData(handleData, param, file) {
        loading(param.place);
        $.ajax({
            url: "/account/api/data",
            type: "post",
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify({
                "action": "data",
                "fileName": param.fileName,
                "type": param.type,
                "new": param.needToCreateNew
            }),
            success: function (data) {
                if (!data) {
                    location.reload();
                }
                switch (data["result"]) {
                    case "success" :
                        handleData(data.data, param);
                        break;
                    default :
                        noDataAvailable(param, file);
                        break;
                }
            },
            error: function (data) {
                noDataAvailable(param, file);
            },
            complete: function(data) {
                //For automatic reload on logout
                if (data == null || typeof data.responseJSON === 'undefined') location.reload();
                if (data.responseJSON.message != null) console.log(data.responseJSON.message);
                loaded(param.place);
            }
        });
}

function spectratype(data, param) {
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
                if (d['cdr3aa'])
                    return d['cdr3aa'];
                return 'Other'
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
                    if (key != "Other") {
                        if (e.series.values[e.pointIndex].y != 0) {
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
        for (var i = 1; i < 100; i++) {
            if (i % 3 == 0) {
                xValues.push(i);
            }
        }

        chart.xAxis
            .axisLabel('CDR3 length')
            .tickValues(xValues)
            .tickFormat(d3.format(',f'));

        chart.yAxis
            .tickFormat(d3.format('%'));

        svg.datum(data).call(chart);

        nv.utils.windowResize(chart.update);
        return chart;
    });
}

function spectratypeV(data, param) {
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
            .axisLabel('CDR3 length')
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

        var legendSvg = place.append("svg")
            .attr("width", "100%")
            .attr("height", 20)
            .style("overflow", "visible");

        var chart = nv.models.legend()
            .width(100)
            .height(20)
            .key(function(d) {
                return d.label;
            })
            .margin({top: 0, left: 0, right: 0, bottom: 0});

        var keys = [
            {key: "data", "color": "#ffffff", label: ""},
            {key: "Singleton", color: "#9e9ac8", label: "Singleton"},
            {key: "Doubleton", color: "#bcbddc", label: "Doubleton"},
            {key: "HighOrder", color: "#9ebcda", label: "High Order"},
            {key: "Q1", color: "#c6dbef", label: "Quantile #1"},
            {key: "Q2", color: "#9ecae1", label: "Quantile #2"},
            {key: "Q3", color: "#6baed6", label: "Quantile #3"},
            {key: "Q4", color: "#4292c6", label: "Quantile #4"},
            {key: "Q5", color: "#2171b5", label: "Quantile #5"}
        ];

        legendSvg.datum(keys).call(chart);

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
                var found = false;
                var color = "#ffffff";
                keys.forEach(function(d) {
                    if (d.key === name) { color = d.color; found = true; };
                });
                if (found) return color;
                return colors[topCount++ % colors.length];
            })
            .style("cursor", function(d) {
                if (d.children != null) {
                    return "pointer"
                }
                return null;
            })
            .on("click", click);

        var text = svg.selectAll("text").data(nodes);
        var textEnter = text.enter().append("text")
            .style("fill-opacity", 1)
            .style("fill", function() {
                return "black";
                //return brightness(d3.rgb(colour(d))) < 125 ? "#eee" : "#000";
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
                if (d.name == "data") return null;
                var label = d.name;
                if (d.size != 0) {
                    label += "  " + (d.size.toPrecision(2) * 100).toPrecision(2) + "%";
                }
                return label;
            });

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
                    .style("fill-opacity", function(e) { return isParentOf(d, e) ? 1 : 1e-6; })
                    .each("end", function(e) {
                        d3.select(this).style("visibility", isParentOf(d, e) ? null : "hidden");
                    });
            }
        }

        function isParentOf(p, c) {
            if (p === c) return true;
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
                return i
                    ? function (t) {
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

function annotationTable(data, param) {
    var place = d3.select(param.place);
        place.html("");

    var displayLength = 1000;

    var table = place
            .append("table")
            .attr("id", "annotation_table_" + param.id)
            .attr("class", "table table-striped table-hover"),
        thead = table.append("thead").append("tr");

    thead.append("th").html("Index");
    thead.append("th").html("Frequency");
    thead.append("th").html("Count");
    thead.append("th").html("CDR3AA");
    thead.append("th").html("V");
    thead.append("th").html("D");
    thead.append("th").html("J");
    thead.append("th").html("CDR3NT");

    var column = [
        {"data": "index"},
        {"data": "freq"},
        {"data": "count"},
        {"data": "cdr3aa"},
        {"data": "v"},
        {"data": "d"},
        {"data": "j"},
        {"data": "cdr3nt"}
    ];

    var dataTable = $('#annotation_table_' + param.id).dataTable({
        dom: '<"pull-left"f><"clear">TrtS',
        scrollY: '600px',
        "data": data["data"],
        "columns": column,
        'order': [
            [2, "decs"]
        ],
        iDisplayLength: 1000,
        responsive: true,
        tableTools: {
            "sSwfPath": "../../assets/lib/dataTable/extensions/TableTools/swf/copy_csv_xls_pdf.swf"
        },
        "columnDefs": [
            {
                "width": "6%",
                "render": function(data) {
                    return (data * 100).toPrecision(2) + '%';
                },
                "targets": 1
            },
            {
                "width": "6%",
                "targets": 2
            },
            {
                "render": function (data) {
                    var cdr3aa = data["cdr3aa"];
                    var vend = Math.floor(data["vend"] / 3);
                    var dstart = Math.floor(data["dstart"] / 3);
                    var dend = Math.floor(data["dend"] / 3);
                    var jstart = Math.floor(data["jstart"] / 3);
                    var pos = data["pos"];
                    jstart = (jstart < 0) ? 10000 : jstart;
                    dstart = (dstart < 0) ? vend + 1 : dstart;
                    dend = (dend < 0) ? vend : dend;
                    while (vend >= jstart) jstart++;
                    while (dstart <= vend) dstart++;
                    while (dend >= jstart) dend--;

                    var createSubString = function (start, end, color) {
                        return {
                            start: start,
                            end: end,
                            color: color,
                            substring: cdr3aa.substring(start, end + 1)
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
                        arr.push(createSubString(0, vend, "#4daf4a"));
                    }

                    if (dstart - vend > 1) {
                        arr.push(createSubString(vend + 1, dstart - 1, "black"));
                    }

                    if (dstart > 0 && dend > 0 && dend >= dstart) {
                        arr.push(createSubString(dstart, dend, "#ec7014"));
                    }

                    if (jstart - dend > 1) {
                        arr.push(createSubString(dend + 1, jstart - 1, "black"));
                    }

                    if (jstart > 0) {
                        arr.push(createSubString(jstart, cdr3aa.length, "#377eb8"));
                    }

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
                },
                "width": "20%",
                "targets": 3
            },
            {
                "render": function(data) {
                    var cdr3aa = data["cdr3nt"];
                    var vend = data["vend"];
                    var dstart = data["dstart"];
                    var dend = data["dend"];
                    var jstart = data["jstart"];
                    var pos = data["pos"];
                    jstart = (jstart < 0) ? 10000 : jstart;
                    dstart = (dstart < 0) ? vend + 1 : dstart;
                    dend = (dend < 0) ? vend : dend;
                    while (vend >= jstart) jstart++;
                    while (dstart <= vend) dstart++;
                    while (dend >= jstart) dend--;
                    var createSubString = function (start, end, color) {
                        return {
                            start: start,
                            end: end,
                            color: color,
                            substring: cdr3aa.substring(start, end + 1)
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
                        arr.push(createSubString(0, vend, "#4daf4a"));
                    }

                    if (dstart - vend > 1) {
                        arr.push(createSubString(vend + 1, dstart - 1, "black"));
                    }

                    if (dstart > 0 && dend > 0 && dend >= dstart) {
                        arr.push(createSubString(dstart, dend, "#ec7014"));
                    }

                    if (jstart - dend > 1) {
                        arr.push(createSubString(dend + 1, jstart - 1, "black"));
                    }

                    if (jstart > 0) {
                        arr.push(createSubString(jstart, cdr3aa.length, "#377eb8"));
                    }

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
                },
                "targets": 7

            }
        ]
    });

    var $scrollViewer = $('#annotation_table_' + param.id + '_wrapper .dataTables_scrollBody');

    $scrollViewer.scroll(function() {

        if ($scrollViewer.scrollTop() - displayLength * 30 > 0) {
            $.ajax({
                url: "/account/api/annotation",
                type: "post",
                contentType: 'application/json; charset=utf-8',
                data: JSON.stringify({
                    "action": "data",
                    "fileName": param.fileName,
                    "index": 1
                }),
                success: function (data) {
                    console.log(data);
                },
                complete: function(data) {
                    //For automatic reload on logout
                    if (data == null || typeof data.responseJSON === 'undefined') location.reload();
                    if (data.responseJSON.message != null) console.log(data.responseJSON.message);
                }
            });
            $scrollViewer.unbind('scroll');
        }

    })

}

function vjUsage(data, param) {

    //var fill = d3.scale.category20c();
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
            return "rotate(" + (((d.startAngle + d.endAngle) / 2) * 180 / Math.PI - 90) + ")"
                + "translate(" + (r1 - 15) + ")"
                + (((d.startAngle + d.endAngle) / 2) > Math.PI ? "rotate(180)" : "");
        })
        .text(function (d) {
            return data.labels[d.index];
        });

    // Returns an event handler for fading a given chord group.
    function fade(opacity) {
        return function (g, i) {
            svg.selectAll(".chord path")
                .filter(function (d) {
                    return d.source.index != i && d.target.index != i;
                })
                .transition()
                .style("opacity", opacity);
        };
    }
}

function rarefactionPlot(data, param) {
    nv.addGraph(function () {
        var place = d3.select(param.place);
        place.html(""); //cleanup old chart

        var width = place.style('width');

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

function summaryStats(data, param) {

    var place = d3.select(param.place);
    place.html("");

    var table = place.append("table")
        .attr("id", "basicStatsTable")
        .attr("class", "table table-striped table-hover"),
        thead = table.append("thead").append("tr");

    thead.append("th").html("Sample");
    thead.append("th").html("Reads");
    thead.append("th").html("Diversity");
    thead.append("th").html("Mean clonotype frequency");
    thead.append("th").html("Geo. mean clonotype frequency");
    thead.append("th").html("Non-coding clonotypes");
    thead.append("th").html("NC clonotypes frequency");
    thead.append("th").html("Mean insert size");
    thead.append("th").html("Mean N(D)N size");
    thead.append("th").html("Mean CDR3 length");
    thead.append("th").html("Convergence");


    var column = [
        {"data": "Name"},                  //Sample
        {"data": "count"},                 //Reads
        {"data": "diversity"},             //Rarefaction
        {"data": "mean_frequency"},        //Mean clonotype frequency
        {"data": "geomean_frequency"},     //Median clonotype frequency
        {"data": "nc_diversity"},          //Non-coding clonotypes
        {"data": "nc_frequency"},          //NC clonotypes frequency
        {"data": "mean_insert_size"},      //Mean insert size
        {"data": "mean_ndn_size"},         //Mean N(d)N size
        {"data": "mean_cdr3nt_length"},    //Mean CDR3 length
        {"data": "convergence"}            //Convergence
    ];

    $('#basicStatsTable').dataTable({
        dom: '<"pull-left"f><"clear">TrtS',
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
                "targets": [3, 4]
            },
            {
                "render": function (data) {
                    return parseFloat(data).toFixed(2) * 100 + "%";
                },
                "targets": 6
            },
            {
                "render": function (data) {
                    return parseFloat(data).toFixed(2)
                },
                "targets": [7, 8, 9]
            },
            {
                "render": function(data) {
                    return data.substring(0, 6);
                },
                "targets": 10
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
    var d3Place = d3.select(place);
        d3Place.html("");
        d3Place.style("display", "block");

    var loading = d3Place.append("div").attr("class", "loading");
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

function noDataAvailable(param, file) {
    var place = d3.select(param.place);
        place.html("");
        place.append("div")
            .style("width", "100%")
            .style("height", "300px")
            .style("text-align", "center")
            .style("line-height", "300px")
            .append("b")
            .html("No Data Available");
    if (file) {
        file.meta[param.type].cached = false;
    }
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
