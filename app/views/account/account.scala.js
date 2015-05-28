@(shared: Boolean)

var CONSOLE_INFO = false;
var api_url = 'account';
var shared = false;

@if(shared){
    api_url = 'share';
    shared = true;
}

(function () {
    "use strict";
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
        COMPARING: 4,
        SAMPLE_COLLECTION: 5,
        MULTIPLE_SEARCH: 6,
        SHARING_INFO: 7
    });

    var app = angular.module('accountPage', ['ui.bootstrap', 'ngWebSocket']);

    app.directive('onLastRepeat', function() {
        return function(scope, element, attrs) {
            if (scope.$last) setTimeout(function(){
                scope.$emit('onRepeatLast', element, attrs);
            }, 1);
        };
    });

    //Notifications factory and directive
    app.factory('notifications', ['$log', '$timeout', function($log, $timeout) {
        var notifications = [];


        function getNotifications() {
            return notifications;
        }

        function deleteNotification(notification) {
            var id = notifications.indexOf(notification);
            if (id >= 0) notifications.splice(id, 1);
        }

        function addNotification(type, header, body) {
            var newNotification = {
                type: type,
                header: header,
                body: body
            };
            notifications.push(newNotification);
            $timeout(function() {
                deleteNotification(newNotification);
            }, (function() {
                switch (type) {
                    case 'error':
                        return 5000;
                    default:
                        return 3000;
                }
            }()));
        }

        function addErrorNotification(header, body) {
            addNotification('error', header, body);
        }

        function addInfoNotification(header, body) {
            addNotification('info', header, body);
        }

        function addSuccessNotification(header, body) {
            addNotification('success', header, body);
        }

        return {
            getNotifications: getNotifications,
            deleteNotification: deleteNotification,
            addNotification: addNotification,
            addErrorNotification: addErrorNotification,
            addInfoNotification: addInfoNotification,
            addSuccessNotification: addSuccessNotification
        };
    }]);

    app.directive('notifications', function() {
        return {
            restrict: 'E',
            controller: ['$scope', 'notifications', function($scope, notifications) {
                $scope.notifications = notifications.getNotifications();
                $scope.deleteNotification = notifications.deleteNotification;

                $scope.isNotificationError = function(notification) {
                    return notification.type === 'error';
                };

                $scope.isNotificationInfo = function(notification) {
                    return notification.type === 'info';
                };

                $scope.isNotificationSuccess = function(notification) {
                    return notification.type === 'success';
                };

                $scope.isNotificationsExist = function() {
                    return $scope.notifications.length > 0;
                };

            }]
        };
    });
    //--------------------------------------------------------//


    //This factory handle account information and user's files
    app.factory('accountInfo', ['$log', '$http', 'stateInfo', 'chartInfo', 'notifications', function($log, $http, stateInfo, chartInfo, notifications) {
        //Private variables
        var maxFilesCount = 0, maxFileSize = 0, filesCount = 0;
        var email = "", firstName = "", lastName = "", userName = "";
        var rarefactionCache = false;
        var initialized = false;
        var initializeError = false;
        var files = {};
        var sharedGroups = [];
        var uid = 0;

        //Initializing account information
        if (CONSOLE_INFO) {
            $log.info('Initializing account information');
        }
        $http.get('/' + api_url + '/api/info'@if(shared){+'/'+link})
            .success(function(response) {
                initialized = true;
                @if(!shared) {
                    var info = response.data;
                    email = info.email;
                    firstName = info.firstName;
                    lastName = info.lastName;
                    userName = info.userName;
                    filesCount = info.filesCount;
                    maxFilesCount = info.filesInformation.maxFilesCount;
                    maxFileSize = info.filesInformation.maxFileSize;
                    rarefactionCache = info.filesInformation.rarefactionCache;
                    angular.forEach(info.filesInformation.files, function (file) {
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
                        };
                    });
                    angular.copy(info.filesInformation.sharedGroups, sharedGroups);
                    if (Object.keys(files).length > 0) {
                        chartInfo.update_rarefaction(!rarefactionCache);
                        chartInfo.update_summary();
                    }
                    if (CONSOLE_INFO) {
                        $log.info('Account initialized');
                        $log.info('User: ' + userName);
                        $log.info('Files count: ' + filesCount);
                        $log.info('Max files count: ' + maxFilesCount);
                        $log.info('Max file size: ' + maxFileSize);
                    }
                }else{
                rarefactionCache = response.rarefactionCached;
                chartInfo.update_rarefaction(!rarefactionCache);
                chartInfo.update_summary();
                angular.forEach(response.sharedFilesNames, function (fileName) {
                    files[fileName] = {
                        uid: uid++,
                        fileName: fileName,
                        state: RenderState.Rendered,
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
                    };
                });
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

        //Getter for user's shared groups
        function getSharedGroups() {
            return sharedGroups;
        }

        //Delete file from server and client-side application
        function deleteFile(file) {
            if (CONSOLE_INFO) {
                $log.info('Trying to delete file ' + file.fileName);
            }
            $http.post('/account/api/delete', { action: 'delete', fileName: file.fileName })
                .success(function () {
                    delete files[file.fileName];
                    if (stateInfo.isActiveFile(file) || Object.keys(files).length === 1) {
                        stateInfo.setActiveState(htmlState.ACCOUNT_INFORMATION);
                    } else if (Object.keys(files).length > 0) {
                        chartInfo.update_rarefaction(true);
                        chartInfo.update_summary();
                    }
                    notifications.addSuccessNotification('Deleting', 'Sample ' + file.fileName + ' has been successfully deleted');
                    if (CONSOLE_INFO) {
                        $log.info('File ' + file.fileName + ' successfully deleted');
                    }
                }).error(function(response) {
                    notifications.addErrorNotification('Deleting', 'Error while deleting sample ' + file.fileName);
                    if (CONSOLE_INFO) {
                        $log.error('Error while deleting file ' + file.fileName);
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
                $log.info('Trying to delete all files');
            }
            $http.post('/account/api/delete', {action: 'deleteAll'})
                .success(function () {
                    var file;
                    for (file in files) { if (files.hasOwnProperty(file)) { delete files[file]; } }
                    stateInfo.setActiveState(htmlState.ACCOUNT_INFORMATION);
                    notifications.addSuccessNotification('Deleting', 'All samples have been successfully deleted');
                    if (CONSOLE_INFO) {
                        $log.info('All files successfully deleted');
                    }
                })
                .error(function() {
                    notifications.addErrorNotification('Deleting', 'Error while deleting samples');
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
            };
        }

        function changeFileState(file, state) {
            files[file.fileName].state = state;
        }

        function isRarefactionCached() {
            return rarefactionCache;
        }
        //------------------------------//

        return {
            getFiles: getFiles,
            getSharedGroups: getSharedGroups,
            deleteFile: deleteFile,
            deleteFile_client: deleteFile_client,
            deleteAll: deleteAll,
            isInitialized: isInitialized,
            isInitializeError: isInitializeError,
            isFileAlreadyExist: isFileAlreadyExist,
            getMaxFilesCount: getMaxFilesCount,
            getMaxFileSize: getMaxFileSize,
            addNewFile: addNewFile,
            changeFileState: changeFileState,
            isRarefactionCached: isRarefactionCached

        };
    }]);
    //--------------------------------------------------------//

    //This factory handles global state of application
    app.factory('stateInfo', ['chartInfo', 'mainVisualisationTabs', function(chartInfo, mainVisualisationTabs) {
        //Private variables
        var state = htmlState.ACCOUNT_INFORMATION;
        var activeFile = {};


        //Public api
        //------------------------------//
        //Setter for active file
        function setActiveFile(file) {
            if (activeFile !== file && file.state !== RenderState.RENDERING) {
                state = htmlState.FILE;
                activeFile = file;
                chartInfo.update_file(mainVisualisationTabs.getActiveTab(), file);
            }
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
            if (st !== htmlState.FILE) activeFile = {};
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
        };
    }]);
    //--------------------------------------------------------//

    //This factory requests data from server
    app.factory('chartInfo', ['$log', '$http', 'notifications', 'summaryStatsFactory', 'clonotypesTableFactory', function($log, $http, notifications, summaryStatsFactory, clonotypesTableFactory) {

        var oldFile = null;

        function update_file(tab, file) {
            if (tab.type === 'searchclonotypes') {return};
            if (typeof file === 'undefined') {
                file = oldFile;
            } else {
                oldFile = file;
            }

            var type = tab.type,
                dataHandler = tab.dataHandler,
                parameters = {
                    place: '#id' + file.uid + ' .' + tab.mainPlace,
                    fileName: file.fileName,
                    type: type,
                    id: file.uid,
                    height: 500,
                    width: 500
                };

            if (!file.meta[type].cached) {
                if (type !== 'annotation') {
                    loading(parameters.place);
                    $http.post('/' + api_url + '/api/data'@if(shared){+'/'+link}, {
                        action: 'data',
                        fileName: file.fileName,
                        type: type
                    })
                        .success(function (data) {
                            switch (data.result) {
                                case 'success':
                                    file.meta[type].cached = true;
                                    if (type === 'annotation') {
                                        clonotypesTableFactory.addData(file.fileName, data.data.data);
                                    } else dataHandler(data.data, parameters);
                                    break;
                                default:
                                    noDataAvailable(parameters, file);
                            }
                            loaded(parameters.place);
                        })
                        .error(function () {
                            if (CONSOLE_INFO) {
                                $log.error('Error while requesting data for file ' + parameters.file.fileName);
                            }
                            notifications.addErrorNotification(type, 'Error while requesting data for sample ' + parameters.file.fileName);
                            noDataAvailable(parameters, parameters.file);
                            loaded(parameters.place);
                        });
                } else {
                    clonotypesTableFactory.loadData(file.fileName, 1);
                    file.meta[type].cached = true;
                }
            }

        }

        function update_rarefaction(newRarefaction) {
            var parameters = {
                place: '.rarefaction-visualisation-tab',
                fileName: 'all',
                type: 'rarefaction'
            };
            loading(parameters.place);
            $http.post('/' + api_url + '/api/data'@if(shared){+'/'+link}, {
                action: 'data',
                type: 'rarefaction',
                fileName: 'all',
                'new': typeof newRarefaction === 'undefined' ? true : newRarefaction
            })
                .success(function (data) {
                    switch (data.result) {
                        case 'success':
                            rarefactionPlot(data.data, parameters);
                            break;
                        default:
                            noDataAvailable(parameters);
                    }
                    loaded(parameters.place);
                })
                .error(function(error) {
                    $log.info(error);
                    notifications.addErrorNotification('Rarefaction', 'Error while loading rarefaction data');
                    loaded(parameters.place);
                });

        }

        function update_summary() {
            loading('.summary-visualisation-tab');
            $http.post('/' + api_url + '/api/data'@if(shared){+'/'+link}, {
                action: 'data',
                fileName: 'all',
                type: 'summary'
            })
                .success(function(data) {
                    switch (data.result) {
                        case 'success':
                            summaryStatsFactory.addData(data.data);
                            break;
                        default:
                            noDataAvailable('.summary-visualisation-tab');
                    }
                    loaded('.summary-visualisation-tab');
                })
                .error(function() {
                    notifications.addErrorNotification('Summary', 'Error while loading summary data');
                    loaded('.summary-visualisation-tab');
                });
        }

        return {
            update_file: update_file,
            update_rarefaction: update_rarefaction,
            update_summary: update_summary
        };
    }]);
    //--------------------------------------------------------//

    //Main Directives
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

                $scope.isSampleCollection = function() {
                    return stateInfo.isActiveState(htmlState.SAMPLE_COLLECTION);
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

                $scope.isMultipleSampleSearch = function() {
                    return stateInfo.isActiveState(htmlState.MULTIPLE_SEARCH);
                };

                $scope.isSharingInformation = function() {
                    return stateInfo.isActiveState(htmlState.SHARING_INFO);
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
        };
    });
    //--------------------------------------------------------//

    //Sidebar Directives
    app.directive('filesSidebar', function () {
        return {
            restrict: 'E',
            scope: false,
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
                $scope.setSampleCollectionState = setSampleCollectionState;
                $scope.setMultipleSampleSearchState = setMultipleSampleSearchState;
                $scope.setSharingState = setSharingState;
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

                function setSampleCollectionState() {
                    stateInfo.setActiveState(htmlState.SAMPLE_COLLECTION);
                }

                function setMultipleSampleSearchState() {
                    stateInfo.setActiveState(htmlState.MULTIPLE_SEARCH);
                }

                function setSharingState() {
                    stateInfo.setActiveState(htmlState.SHARING_INFO);
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
                    $('.filterPopover').popover({
                        trigger: 'focus'
                    });
                    $('#side-menu').slimScroll({
                        height: '100%',
                        color: '#1abc9c',
                        railColor: '#1abc9c',
                        railOpacity: 0.8,
                        disableFadeOut: true,
                        wheelStep: 2
                    });
                });
            }]
        };
    });

    app.directive('sidebarClick', ['$parse', function($parse) {
        return {
            restrict: 'A',
            compile: function($element, attr) {
                var fn = $parse(attr['sidebarClick'], null, true);
                return function ngEventHandler(scope, element) {
                    element.on('click', function(event) {
                        element.parent().find('li.li_pointer').removeClass('current');
                        element.addClass('current');
                        var callback = function() {
                            fn(scope, {$event:event});
                        };
                        scope.$apply(callback);
                    });
                };
            }
        };
    }]);

    app.directive('ngShowRendering', function() {
        return {
            restrict: 'A',
            multiElement: true,
            link: function($scope, $element, $attrs) {
                var watcher = $scope.$watch($attrs.ngShowRendering, function ngShowRenderingAction(value) {
                    if (value) {
                        $element.removeClass('ng-hide');
                    } else {
                        $element.addClass('ng-hide');
                        watcher();
                    }
                });
            }
        };
    });

    app.directive('ngShowRendered', function() {
        return {
            restrict: 'A',
            multiElement: true,
            link: function($scope, $element, $attrs) {
                var watcher = $scope.$watch($attrs.ngShowRendered, function ngShowRenderedAction(value) {
                    if (value) {
                        $element.removeClass('ng-hide');
                        watcher();
                    } else {
                        $element.addClass('ng-hide');
                    }
                });
            }
        };
    });
    //--------------------------------------------------------//

    //File visualisation directive and factory
    app.factory('mainVisualisationTabs', ['chartInfo', function(chartInfo) {
        var createTab = function (tabName, type, dataHandler, mainPlace, comparing, exportType, comparingPlace) {
            return {
                tabName: tabName,
                type: type,
                dataHandler: dataHandler,
                mainPlace: mainPlace,
                comparing: comparing,
                exportType: exportType,
                comparingPlace: comparingPlace
            };
        };
        var visualisationTabs = {
            vjusage: createTab('V-J Usage', 'vjusage', vjUsage, 'visualisation-results-vjusage', true, ['JPEG'], 'comparing-vjusage-tab'),
            spectratype: createTab('Spectratype', 'spectratype', spectratype, 'visualisation-results-spectratype', true, ['PNG', 'JPEG'], 'comparing-spectratype-tab'),
            spectratypev: createTab('V Spectratype ', 'spectratypeV', spectratypeV, 'visualisation-results-spectratypeV', true, ['PNG', 'JPEG'], 'comparing-spectratypeV-tab'),
            quantilestats: createTab('Quantile Plot', 'quantileStats', quantileSunbirstChart, 'visualisation-results-quantileStats', true, ['PNG', 'JPEG'], 'comparing-quantileStats-tab'),
            annotation: createTab('Clonotypes', 'annotation', null, 'visualisation-results-annotation', false, [], ''),
            searchclonotypes: createTab('Search clonotypes', 'searchclonotypes', null, 'visualisation-results-searchclonotypes', false, [], '')
        };
        var sortedTabs = [
            visualisationTabs.vjusage,
            visualisationTabs.spectratype,
            visualisationTabs.spectratypev,
            visualisationTabs.quantilestats,
            visualisationTabs.annotation,
            visualisationTabs.searchclonotypes
        ];
        var activeTab = visualisationTabs.vjusage;

        function setActiveTab(t) {
            if (activeTab !== t) {
                activeTab = t;
                chartInfo.update_file(t);
            }

        }

        function getActiveTab() {
            return activeTab;
        }

        function isActiveTab(t) {
            if (typeof t === 'string') {
                return activeTab.type === t;
            } else return activeTab === t;
        }

        function getTabs() {
            return visualisationTabs;
        }

        function getSortedTabs() {
            return sortedTabs;
        }

        return {
            setActiveTab: setActiveTab,
            getActiveTab: getActiveTab,
            isActiveTab: isActiveTab,
            getTabs: getTabs,
            getSortedTabs: getSortedTabs
        };
    }]);

    app.directive('mainVisualisationContent', function () {
        return {
            restrict: 'E',
            controller: ['$scope', 'stateInfo', 'mainVisualisationTabs', function ($scope, stateInfo, mainVisualisationTabs) {
                $scope.visualisationTabs = mainVisualisationTabs.getTabs();
                $scope.sortedTabs = mainVisualisationTabs.getSortedTabs();

                $scope.exportChartPng = function (file, tab, exportType) {
                    saveSvgAsPng(document.getElementById('svg_' + tab.type + '_' + file.uid), file.fileName + "_" + tab.type, 3, exportType);
                };

                $scope.setActiveTab = function (tab) {
                    mainVisualisationTabs.setActiveTab(tab);
                    //TODO update visuzlisation
                };

                $scope.isActiveTab = mainVisualisationTabs.isActiveTab;

                $scope.showClonotypesTable = function() {
                    return $scope.isActiveTab('annotation');
                };

                $scope.showSearchClonotypesTable = function() {
                    return $scope.isActiveTab('searchclonotypes');
                };

                $scope.showFile = function (file) {
                    return stateInfo.isActiveFile(file);
                };
            }]
        };
    });
    //--------------------------------------------------------//

    //Clonotypes search directive (single sample)
    app.directive('searchClonotypesTable', function() {
        return {
            restrict: 'E',
            scope: true,
            controller: ['$scope', '$http', '$log', 'notifications', '$sce', function($scope, $http, $log, notifications, $sce) {
                var noMatchesFound = false;
                var loading = false;
                var init = false;

                $scope.sequenceString = '';
                $scope.searchResults = {};
                $scope.aminoAcid = 'true';
                $scope.filters = {
                    vFilter: '',
                    jFilter: ''
                };

                $scope.searchSequence = function() {
                    if ($scope.sequenceString.length <= 0) {
                        notifications.addInfoNotification('Search clonotypes', 'Missing CDR3 pattern');
                        return;
                    }
                    init = true;
                    loading = true;
                    noMatchesFound = false;
                    angular.extend($scope.searchResults, {});
                    $http.post('/' + api_url + '/api/annotation/search'@if(shared){+'/'+link}, {
                        fileName: $scope.file.fileName,
                        sequence: $scope.sequenceString,
                        aminoAcid: $scope.aminoAcid,
                        vFilter: $scope.filters.vFilter.replace(/ /g,'') !== '' ? $scope.filters.vFilter.replace(/ /g,'').split(',') : [],
                        jFilter: $scope.filters.jFilter.replace(/ /g,'') !== '' ? $scope.filters.jFilter.replace(/ /g,'').split(',') : []
                    })
                        .success(function(response) {
                            loading = false;
                            if (response.rows.length === 0) {
                                noMatchesFound = true;
                            } else {
                                angular.forEach(response.rows, function(row) {
                                    row.freq = (row.freq * 100).toPrecision(2) + '%';
                                    row.cdr = cdr3Transform(row.cdr, $sce);
                                });
                                angular.extend($scope.searchResults, response);
                            }
                        })
                        .error(function(response) {
                            init = true;
                            loading = false;
                            noMatchesFound = true;
                            if (CONSOLE_INFO) {
                                $log.error(response.message);
                            }
                            notifications.addErrorNotification('Search clonotypes', response.message);
                        });
                };

                $scope.isClonotypesSearchInit = function() {
                    return init;
                };

                $scope.isLoading = function() {
                    return loading;
                };

                $scope.isNoMatchesFound = function() {
                    return noMatchesFound;
                }


            }]
        };
    });
    //--------------------------------------------------------//


    //Clonotypes table factory and directive
    app.factory('clonotypesTableFactory', ['$sce', 'notifications', '$http', '$log', function($sce, notifications, $http, $log) {
        var data = {};

        function getData(fileName) {
            if (typeof data[fileName] === 'undefined') data[fileName] = {
                loading: true,
                loadError: false,
                sampleCount: 0,
                numberOfPages: 0,
                shift: 0,
                displayLength: 0,
                rows: []
            };
            return data[fileName];
        }

        function isLoading(fileName) {
            return function() {
                return data[fileName].loading;
            };
        }

        function isLoadingError(fileName) {
            return function() {
                return data[fileName].loadError;
            };
        }

        function addData(fileName, d) {
            d.rows.forEach(function(row) {
                row.freq = (row.freq * 100).toPrecision(2) + '%';
                row.cdr = cdr3Transform(row.cdr, $sce);
            });
            angular.extend(data[fileName], d);
            data[fileName].loading = false;
        }

        function loadData(fileName, page) {
            data[fileName].loading = true;
            $http.post('/' + api_url + '/api/annotation'@if(shared){+'/'+link}, {
                action: 'data',
                fileName: fileName,
                shift: page - 1
            })
                .success(function(response) {
                    data[fileName].loading = false;
                    addData(fileName, response);
                })
                .error(function() {
                    data[fileName].loading = false;
                    data[fileName].loadError = true;
                    notifications.addErrorNotification('Clonotypes table', 'Error while loading page ' + page);
                    if (CONSOLE_INFO) {
                        $log.error('Clonotypes table: Error while downloading page ' + page);
                    }
                });
        }

        return {
            getData: getData,
            addData: addData,
            loadData: loadData,
            loading: loading,
            loaded: loaded,
            isLoading: isLoading,
            isLoadingError: isLoadingError
        };

    }]);

    app.directive('clonotypesTable', function() {
        return {
            restrict: 'E',
            scope: true,
            controller: ['$scope', '$log', '$http', 'clonotypesTableFactory', function($scope, $log, $http, clonotypesTableFactory) {
                var fileName = $scope.file.fileName;
                var searchError = false;

                $scope.data = clonotypesTableFactory.getData(fileName);
                $scope.isLoading = clonotypesTableFactory.isLoading(fileName);
                $scope.isLoadingError = clonotypesTableFactory.isLoadingError(fileName);
                $scope.page = 1;
                $scope.searchString = '';
                $scope.searchResults = [];

                $scope.pageChanged = function() {
                    clonotypesTableFactory.loadData(fileName, $scope.page);
                };

                $scope.isSearchError = function() {
                    return searchError;
                };

                $scope.isResults = function() {
                    return $scope.searchResults.length > 0;
                };

                $scope.search = function() {
                    searchError = false;
                    $http.post('/account/api/annotation/search', {
                        fileName: fileName,
                        searchString: $scope.searchString
                    })
                        .success(function(response) {
                            $scope.searchResults = response;
                        })
                        .error(function() {
                            searchError = true;
                            $scope.searchResults.splice(0, $scope.searchResults.length);
                        });
                };

            }]
        };
    });
    //--------------------------------------------------------//

    //Rarefaction tab Directive
    app.directive('rarefactionContent', function () {
        return {
            restrict: 'E',
            controller: ['$scope', function ($scope) {

                $scope.rarefactionExportTypes = ['JPEG'];

                $scope.exportRarefaction = function (type) {
                    saveSvgAsPng(document.getElementById('rarefaction-png-export'), 'rarefaction', 3, type);
                };
            }]
        };
    });
    //--------------------------------------------------------//

    //Summary content directive and factory
    //--------------------------------------------------------//
    app.factory('summaryStatsFactory', function() {
        var data = [];


        function addData(d) {
            d = d.map(function(value) {
                return {
                    Name: value.Name,
                    count: value.count,
                    diversity: value.diversity,
                    mean_frequency: parseFloat(value.mean_frequency).toExponential(2),
                    geomean_frequency: parseFloat(value.geomean_frequency).toExponential(2),
                    nc_diversity: value.nc_diversity,
                    nc_frequency: parseFloat(value.nc_frequency).toFixed(2) * 100 + "%",
                    mean_insert_size: parseFloat(value.mean_insert_size).toFixed(2),
                    mean_ndn_size: parseFloat(value.mean_ndn_size).toFixed(2),
                    mean_cdr3nt_length: parseFloat(value.mean_cdr3nt_length).toFixed(2),
                    convergence: value.convergence.substring(0, 6)
                };
            });
            angular.copy(d, data);
        }

        function getData() {
            return data;
        }

        return {
            addData: addData,
            getData: getData
        };

    });

    app.directive('summaryStats', function() {
        return {
            restrict: 'E',
            controller: ['$scope', 'summaryStatsFactory', function($scope, summaryStatsFactory) {
                $scope.data = summaryStatsFactory.getData();

            }]
        };
    });
    //--------------------------------------------------------//

    //Upload support Directive
    //--------------------------------------------------------//
    app.directive('fileUpload', function () {
        return {
            restrict: 'E',
            controller: ['$scope', '$http', 'accountInfo', 'chartInfo', 'notifications', function ($scope, $http, accountInfo, chartInfo, notifications) {
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
                $scope.commonSoftwareType = 'vdjtools';

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
                    return regexp.test(file.fileName);
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
                    });
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

                function isWaitingFilesExist() {
                    var exist = false;
                    angular.forEach($scope.newFiles, function(file) {
                        if (file.state === RenderState.WAIT) exist = true;
                    });
                    return exist;
                }

                function addNew(uid, fileName, fileExtension, data)  {
                    $scope.$apply(function () {
                        $scope.newFiles[uid] = {
                            uid: uid,
                            fileName: fileName,
                            softwareTypeName: $scope.commonSoftwareType,
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
                    file.progress = progress;
                }

                function updateResult(file, result) {
                    file.result = result;
                    file.state = RenderState.RENDERED;
                }

                function updateState(file, state) {
                    file.state = state;
                }

                function updateResultTooltip(file, resultTooltip) {
                    file.resultTooltip = resultTooltip;
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
                       };
                    });
                }

                function isContain(fileName) {
                    var contain = false;
                    angular.forEach($scope.newFiles, function (file) {
                        if (file.fileName === fileName && file.wait) contain = true;
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
                    dropZone: $('#new-files-dropzone'),
                    add: function (e, data) {
                        var file = data.files[0];
                        var originalFileName = file.name;
                        var fileName = originalFileName.substr(0, originalFileName.lastIndexOf('.')) || originalFileName;
                        var fileExtension = originalFileName.substr((~-originalFileName.lastIndexOf(".") >>> 0) + 2);
                        if (fileExtension !== 'txt' && fileExtension !== 'gz') {
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
                        $scope.$apply(function() {
                            updateProgress(file, parseInt(data.loaded / data.total * 50, 10));
                        })
                    },
                    done: function (e, data) {
                        var file = $scope.newFiles[data.formData.uid];
                        switch (data.result.result) {
                            case "success" :
                                var socket = new WebSocket("ws://" + location.host + "/account/api/rendering/ws");
                                socket.onmessage = function (message) {
                                    var event = JSON.parse(message.data);
                                    switch (event.result) {
                                        case "ok" :
                                            switch (event.message) {
                                                case "start" :
                                                    accountInfo.addNewFile(file);
                                                    $scope.$apply(function() {
                                                        updateTooltip(file, "Computation");
                                                        updateState(file, RenderState.RENDERING);
                                                    });
                                                    break;
                                                case "end" :
                                                    accountInfo.changeFileState(file, RenderState.RENDERED);
                                                    $scope.$apply(function() {
                                                        updateTooltip(file, "Success");
                                                        updateResult(file, 'success');
                                                    });
                                                    if (!isRenderingFilesExist()) {
                                                        if (!isWaitingFilesExist()) {
                                                            notifications.addSuccessNotification('Uploading', 'All samples have been uploaded and rendered successfully');
                                                        }
                                                        chartInfo.update_rarefaction(true);
                                                        chartInfo.update_summary();
                                                    }
                                                    socket.close();
                                                    break;
                                                default:
                                                    $scope.$apply(function() {
                                                        updateProgress(file, 50 + (event.message / 2));
                                                    });
                                            }
                                            break;
                                        case "error" :
                                            accountInfo.deleteFile_client(file);
                                            notifications.addErrorNotification('Rendering', 'Error while rendering sample ' + file.fileName);
                                            $scope.$apply(function() {
                                                updateResult(file, 'error');
                                                updateResultTooltip(file, event.message);
                                            });
                                            socket.close();
                                            break;
                                        default:
                                            accountInfo.deleteFile_client(file);
                                            notifications.addErrorNotification('Rendering', 'Server is unavailable');
                                            $scope.$apply(function() {
                                                updateTooltip(file, "Server is unavailable");
                                            });
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
                                notifications.addErrorNotification('Rendering', 'Error while rendering sample ' + file.fileName);
                                break;
                            default:
                                updateResult(file, 'error');
                                updateResultTooltip(file, "Server is unavailable");
                                notifications.addErrorNotification('Rendering', 'Server is unavailable');
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
                    });
                });


            }]

        };
    });
    //--------------------------------------------------------//

    //Comparing Directive
    //--------------------------------------------------------//
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
                        deleteItem(file, tab);
                    } else {
                        showItem(file, tab);
                    }
                }

                function deleteItem(file, tab) {
                    for (var i = 0; i < $scope.comparingItems.length; ++i) {
                         if ($scope.comparingItems[i].fileName === file.fileName && $scope.comparingItems[i].tabName === tab.tabName) {
                             $scope.comparingItems.splice(i, 1);
                             file.meta[tab.type].comparing = false;
                             break;
                         }
                    }
                }

                function deleteAllItems(tab) {
                    var cleanedArray = [];
                    for (var i = 0; i < $scope.comparingItems.length; i++) {
                        if ($scope.comparingItems[i].tabName !== tab.tabName) {
                            cleanedArray.push($scope.comparingItems[i]);
                        } else {
                            $scope.files[$scope.comparingItems[i].fileName].meta[tab.type].comparing = false;
                        }
                    }
                    $scope.comparingItems.splice(0, $scope.comparingItems.length);
                    $scope.comparingItems = cleanedArray;
                }

                function showItem(file, tab) {
                    if (file.state !== RenderState.RENDERING) {
                        $scope.comparingItems.push({
                            fileName: file.fileName,
                            tabName: tab.tabName,
                            place: tab.comparingPlace,
                            uid: file.uid
                        });
                        file.meta[tab.type].comparing = true;
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
                    if (shown === 0) {
                        deleteAllItems(tab);
                    }
                }
            }]
        };
    });
    //--------------------------------------------------------//

    //Join samples factory and directive
    //--------------------------------------------------------//
    app.factory('sampleCollectionFactory', ['$http', '$log', '$websocket', 'notifications', '$sce', function($http, $log, $websocket, notifications, $sce) {

        var steps = Object.freeze({
            FILES_SELECT: 0,
            FILES_OPENING: 1,
            JOIN_RENDERING: 2,
            JOIN_INFORMATION: 3
        });

        var step = steps.FILES_SELECT;
        var initialized = false;
        var connectionError = false;
        var openProgress = 0;
        var jointrendering = false;
        var openingFile = '';
        var names = [];
        var treshData = {
            clonotypes: []
        };
        var data = {};

        var vGenes = '';
        var jGenes = '';

        angular.copy(treshData, data);

        var ws = $websocket("ws://" + location.host + "/" + api_url + "/api/samplecollection/ws"@if(shared){+"/"+link});

        ws.onOpen(function() {
            initialized = true;
            if (CONSOLE_INFO) {
                $log.info('WebSocket for sample collection was opened');
            }
        });

        ws.onError(function() {
            connectionError = true;
            notifications.addErrorNotification('Join samples', 'Connection error');
            if (CONSOLE_INFO) {
                $log.error('Error: WebSocket for sample collection is down');
            }
        });

        ws.onClose(function() {
            connectionError = true;
            notifications.addErrorNotification('Join samples', 'Connection error');
            if (CONSOLE_INFO) {
                $log.error('Error: WebSocket for sample collection is down');
            }
        });

        ws.onMessage(function(message) {
            var response = JSON.parse(message.data);
            switch (response.action) {
                case 'open':
                    openProgress = response.progress;
                    openingFile = response.message;
                    break;
                case 'opened':
                    step = steps.JOIN_RENDERING;
                    angular.copy(response.data.vGenes, vGenes);
                    angular.copy(response.data.jGenes, jGenes);
                    notifications.addSuccessNotification('Join samples', 'Loaded samples');
                    break;
                case 'rendered':
                    jointrendering = false;
                    step = steps.JOIN_INFORMATION;
                    joinHeapMap(response.data);
                    break;
                case 'error':
                    notifications.addErrorNotification('Join samples', response.message);
                    if (CONSOLE_INFO) {
                        $log.error('Error: ' + response.message);
                    }
                    break;
                default:
                    if (CONSOLE_INFO) {
                        $log.warn('Warning: invalid action in sample collection response: ' + response.action);
                    }
            }
        });

        function getVGenes() {
            return vGenes;
        }

        function getJGenes() {
            return jGenes;
        }

        function getStep() {
            return step;
        }

        function openGroup(n) {
            if (n.length < 2) {
                notifications.addInfoNotification('Join samples', 'You should select at least two samples');
                return;
            }
            names = n;
            step = steps.FILES_OPENING;
            ws.send(JSON.stringify({
                names: names,
                action: 'open'
            }));
        }

        function joinSamples(overlapType, occurenceTreshold, filters) {
            jointrendering = true;
            ws.send(JSON.stringify({
                action: 'render',
                joinParameters: {
                    overlapType: overlapType,
                    occurenceTreshold: occurenceTreshold,
                    vFilter: filters.vFilter.replace(/ /g,'') !== '' ? filters.vFilter.replace(/ /g,'').split(',') : [],
                    jFilter: filters.jFilter.replace(/ /g,'') !== '' ? filters.jFilter.replace(/ /g,'').split(',') : []
                }
            }));
        }

        function openAnotherFiles() {
            openProgress = 0;
            step = steps.FILES_SELECT;
            angular.copy(treshData, data);
        }

        function changeJoinParameters() {
            step = steps.JOIN_RENDERING;
            angular.copy(treshData, data);
        }

        function isJointRendering() {
            return jointrendering;
        }

        function getData() {
            return data;
        }

        function getOpenProgress() {
            return openProgress;
        }

        function getOpeningFile() {
            return openingFile;
        }

        function isInitialized() {
            return initialized;
        }

        function isConnectionError() {
            return connectionError;
        }

        function isFilesSelectStep() {
            return step === steps.FILES_SELECT;
        }

        function isFilesOpeningStep() {
            return step === steps.FILES_OPENING;
        }

        function isJoinRenderingStep() {
            return step === steps.JOIN_RENDERING;
        }

        function isJoinInformationStep() {
            return step === steps.JOIN_INFORMATION;
        }

        return {
            isInitialized: isInitialized,
            isConnectionError: isConnectionError,
            openGroup: openGroup,
            getOpenProgress: getOpenProgress,
            isJointRendering: isJointRendering,
            getOpeningFile: getOpeningFile,
            getStep: getStep,
            isFilesSelectStep: isFilesSelectStep,
            isFilesOpeningStep: isFilesOpeningStep,
            isJoinRenderingStep: isJoinRenderingStep,
            isJoinInformationStep: isJoinInformationStep,
            joinSamples: joinSamples,
            getData: getData,
            getVGenes: getVGenes,
            getJGenes: getJGenes,
            openAnotherFiles: openAnotherFiles,
            changeJoinParameters: changeJoinParameters
        };
    }]);

    app.directive('sampleCollection', function() {
        return {
            restrict: 'E',
            controller: ['$scope', '$log', 'sampleCollectionFactory', 'accountInfo', function($scope, $log, sampleCollectionFactory, accountInfo) {

                var steps = Object.freeze({
                    FILES_SELECT: 0,
                    FILES_OPENING: 1,
                    JOIN_INFORMATION: 2
                });

                //Sample collection factory API
                $scope.getData = sampleCollectionFactory.getData;
                $scope.getVGenes = sampleCollectionFactory.getVGenes;
                $scope.getJGenes = sampleCollectionFactory.getJGenes;
                $scope.getOpenProgress = sampleCollectionFactory.getOpenProgress;
                $scope.isJointRendering = sampleCollectionFactory.isJointRendering;
                $scope.getOpeningFile = sampleCollectionFactory.getOpeningFile;
                $scope.isInitialized = sampleCollectionFactory.isInitialized;
                $scope.isConnectionError = sampleCollectionFactory.isConnectionError;
                $scope.isFilesSelectStep = sampleCollectionFactory.isFilesSelectStep;
                $scope.isFilesOpeningStep = sampleCollectionFactory.isFilesOpeningStep;
                $scope.isJoinRenderingStep = sampleCollectionFactory.isJoinRenderingStep;
                $scope.isJoinInformationStep = sampleCollectionFactory.isJoinInformationStep;
                $scope.changeJoinParameters = sampleCollectionFactory.changeJoinParameters;
                $scope.openAnotherFiles = function() {
                    $scope.selectedFiles.splice(0, $scope.selectedFiles.length);
                    $scope.occurenceTreshold = 2;
                    sampleCollectionFactory.openAnotherFiles();
                };

                $scope.openGroup = function() {
                    var names = [];
                    $scope.selectedFiles.forEach(function(file) {
                        names.push(file.fileName);
                    });
                    sampleCollectionFactory.openGroup(names);
                };

                $scope.joinSamples = function() {
                    sampleCollectionFactory.joinSamples($scope.overlapType, $scope.occurenceTreshold, $scope.filters);
                };

                //Account info API
                $scope.files = accountInfo.getFiles();

                //Directive API
                $scope.selectedFiles = [];
                $scope.filters = {
                    vFilter: '',
                    jFilter: ''
                };
                $scope.occurenceTreshold = 2;
                $scope.overlapType = 'AminoAcid';

                $scope.selectAll = function() {
                    angular.forEach($scope.files, function(file) {
                        if (!$scope.isFileSelected(file)) $scope.selectFile(file);
                    });
                };

                $scope.selectFile = function(file) {
                    var index = $scope.selectedFiles.indexOf(file);
                    if (index >= 0) {
                        $scope.selectedFiles.splice(index, 1);
                    } else {
                        $scope.selectedFiles.push(file);
                    }
                };

                $scope.isFileSelected = function(file) {
                    return $scope.selectedFiles.indexOf(file) >= 0;
                };


            }]
        };
    });
    //--------------------------------------------------------//

    //Multiple samples search factory and directive
    //--------------------------------------------------------//
    app.factory('multipleSampleSearchFactory', ['$http', '$log', 'notifications', '$sce', function($http, $log, notifications, $sce) {
        var initialized = false;
        var loading = false;
        var results = [];

        function search(searchParameters) {
            if (searchParameters.selectedFiles.length === 0) {
                notifications.addInfoNotification('Multiple sample search', 'You should select at least one sample');
                return;
            }
            if (searchParameters.sequenceString.length === 0) {
                notifications.addInfoNotification('Multiple sample search', 'Missing CDR3 pattern');
                return;
            }
            initialized = true;
            loading = true;
            angular.copy([], results);
            $http.post('/' + api_url + '/api/annotation/multipleSearch'@if(shared){+'/'+link}, {
                sequence: searchParameters.sequenceString,
                aminoAcid: searchParameters.aminoAcid,
                selectedFiles: searchParameters.selectedFiles,
                vFilter: searchParameters.filters.vFilter.replace(/ /g,'') !== '' ? searchParameters.filters.vFilter.replace(/ /g,'').split(',') : [],
                jFilter: searchParameters.filters.jFilter.replace(/ /g,'') !== '' ? searchParameters.filters.jFilter.replace(/ /g,'').split(',') : []
            })
                .success(function(response) {
                    angular.forEach(response, function(result) {
                        angular.forEach(result.rows, function(row) {
                            row.freq = (row.freq * 100).toPrecision(2) + '%';
                            row.cdr = cdr3Transform(row.cdr, $sce);
                        });
                    });
                    angular.copy(response, results);
                    loading = false;
                })
                .error(function(response) {
                    if (CONSOLE_INFO) {
                        $log.info(response.message);
                    }
                    notifications.addErrorNotification('Multiple sample search', response.message);
                    loading = false;
                });
        }

        function isInitialized() {
            return initialized;
        }

        function isLoading() {
            return loading;
        }

        function getResults() {
            return results;
        }

        return {
            search: search,
            isInitialized: isInitialized,
            isLoading: isLoading,
            getResults: getResults
        };
    }]);

    app.directive('multipleSampleSearch', function() {
        return {
            restrict: 'E',
            controller: ['$scope', '$log', 'multipleSampleSearchFactory', function($scope, $log, multipleSampleSearchFactory) {

                //Multiple sample search factory API
                $scope.isInitialized_MultipleSampleSearch = multipleSampleSearchFactory.isInitialized;
                $scope.isLoading_MultipleSampleSearch = multipleSampleSearchFactory.isLoading;
                $scope.multipleSampleSearchResults = multipleSampleSearchFactory.getResults();

                //Directive API
                $scope.searchParameters = {
                    sequenceString: '',
                    aminoAcid: 'true',
                    selectedFiles: [],
                    filters: {
                        vFilter: '',
                        jFilter: ''
                    }
                };

                $scope.selectFile_MultipleSearchClonotypes = function(file) {
                    var index = $scope.searchParameters.selectedFiles.indexOf(file.fileName);
                    if (index < 0) {
                        $scope.searchParameters.selectedFiles.push(file.fileName);
                    } else {
                        $scope.searchParameters.selectedFiles.splice(index, 1);
                    }
                };

                $scope.selectAll_MultipleSearchClonotypes = function() {
                    angular.forEach($scope.files, function(file) {
                        if (!$scope.isFileSelected_MultipleSearchClonotypes(file))
                            $scope.selectFile_MultipleSearchClonotypes(file);
                    });
                };

                $scope.unselectAll_MultipleSearchClonotypes = function() {
                    angular.forEach($scope.files, function(file) {
                        if ($scope.isFileSelected_MultipleSearchClonotypes(file))
                            $scope.selectFile_MultipleSearchClonotypes(file);
                    });
                };

                $scope.revert_MultipleSearchClonotypes = function() {
                    angular.forEach($scope.files, function(file) {
                        $scope.selectFile_MultipleSearchClonotypes(file);
                    });
                };

                $scope.isFileSelected_MultipleSearchClonotypes = function(file) {
                    return $scope.searchParameters.selectedFiles.indexOf(file.fileName) >= 0;
                };

                $scope.searchMultipleSampleSequence = function() {
                    multipleSampleSearchFactory.search($scope.searchParameters);
                };
            }]
        };
    });
    //--------------------------------------------------------//


    //Sharing directive
    //--------------------------------------------------------//
    app.directive('sharing', function() {
        return {
            restrict: 'E',
            scope: false,
            controller: ['$scope', '$http', '$log', 'notifications', 'accountInfo', function($scope, $http, $log, notifications, accountInfo) {
                var isNew = false;
                var selectedFiles = [];

                $scope.sharingDescription = '';
                $scope.sharedGroups = accountInfo.getSharedGroups();

                $scope.isNewSharingFiles = function() {
                    return isNew;
                };

                $scope.addSharingFiles = function() {
                    selectedFiles.splice(0, selectedFiles.length);
                    $scope.sharingDescription = '';
                    isNew = true;
                };

                $scope.isFileSelected_Sharing = function(file) {
                    var index = selectedFiles.indexOf(file.fileName);
                    return index >= 0;
                };

                $scope.selectFile_Sharing = function(file) {
                    var index = selectedFiles.indexOf(file.fileName);
                    if (index >= 0) {
                        selectedFiles.splice(index, 1);
                    } else {
                        selectedFiles.push(file.fileName);
                    }
                };

                $scope.selectAll_Sharing = function() {
                    angular.forEach($scope.files, function(file) {
                        if (!$scope.isFileSelected_Sharing(file))
                            $scope.selectFile_Sharing(file);
                    });
                };

                $scope.unselectAll_Sharing = function() {
                    angular.forEach($scope.files, function(file) {
                        if ($scope.isFileSelected_Sharing(file))
                            $scope.selectFile_Sharing(file);
                    });
                };

                $scope.revert_Sharing = function() {
                    angular.forEach($scope.files, function(file) {
                        $scope.selectFile_Sharing(file);
                    });
                };

                $scope.deleteSharedGroup = function(group) {
                    $http.post('/account/api/deleteShared', {
                        link: group.link
                    })
                        .success(function(response) {
                            $scope.sharedGroups.splice($scope.sharedGroups.indexOf(group), 1);
                            notifications.addSuccessNotification('Sharing', 'Successfully deleted');
                        })
                        .error(function(error) {
                            if (CONSOLE_INFO) {
                                $log.error('Sharing', error.message);
                            }
                            notifications.addErrorNotification('Sharing', error.message);
                        });
                };

                $scope.copyShareLinkToClip = function(link) {
                    window.prompt("Copy to clipboard: Ctrl+C, Enter", location.host + "/share/" + link);
                };

                $scope.shareButton = function() {
                    if (selectedFiles.length === 0) {
                        notifications.addInfoNotification('Sharing', 'You should select at least one sample');
                        return;
                    }
                    $http.post('/account/api/share', {
                        selectedFiles: selectedFiles,
                        description: $scope.sharingDescription
                    })
                        .success(function(group) {
                            notifications.addSuccessNotification('Sharing', 'Successfully shared');
                            $scope.sharedGroups.push(group);
                            isNew = false;
                        })
                        .error(function(response) {
                            if (CONSOLE_INFO) {
                                $log.error('Sharing: ' + response.message);
                            }
                            notifications.addErrorNotification('Sharing', response.message);
                        });
                };


            }]
        };
    });

    //--------------------------------------------------------//

    //Block account page Directive
    //--------------------------------------------------------//
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
                };

            }]
        };
    });
    //--------------------------------------------------------//


    //Filter for comparing files
    //--------------------------------------------------------//
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
    //--------------------------------------------------------//

})();

//Request data from server
function getData(handleData, param, file) {
        "use strict";
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
                switch (data.result) {
                    case "success" :
                        handleData(data.data, param);
                        break;
                    default :
                        noDataAvailable(param, file);
                        break;
                }
            },
            error: function () {
                noDataAvailable(param, file);
            },
            complete: function(data) {
                //For automatic reload on logout
                if (data === null || typeof data.responseJSON === 'undefined') location.reload();
                if (data.responseJSON.message !== null) console.log(data.responseJSON.message);
                loaded(param.place);
            }
        });
}

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
        for (var i = 1; i < 100; i++) {
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

        svg.datum(data).call(chart);

        nv.utils.windowResize(chart.update);
        return chart;
    });
}

function spectratypeV(data, param) {
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
        for (var i = 0; i < 100; i++) {
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
                return Math.round(d * 10) + "%";
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
                    if (d.key === name) { color = d.color; found = true; }
                });
                if (found) return color;
                return colors[topCount++ % colors.length];
            })
            .style("cursor", function(d) {
                if (d.children !== null) {
                    return "pointer";
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
                if (d.name === "data") return null;
                var label = d.name;
                if (d.size !== 0) {
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

    var margin = { top: 0, right: 10, bottom: 50, left: 300 },
        col_number = data.colLabel.length,
        row_number = data.rowLabel.length,
        cellSize = 15,
        width = cellSize * col_number, // - margin.left - margin.right,
        height = cellSize * row_number , // - margin.top - margin.bottom,
        //gridSize = Math.floor(width / 24),
        legendElementWidth = cellSize * 1.5,
        hcrow = data.rowLabel.map(function(d, i) {
            return i + 1;
        }),
        hccol = data.colLabel.map(function(d, i) {
            return i + 1;
        }),
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
        colLabel = data.colLabel;

    var colorScale = d3.scale.quantile()
        .domain([-10 , 0])
        .range(colors);

    var place = d3.select(".joinHeapMap");
        place.html("");

    if (rowLabel.length === 0) {
        place.append("text").text("No data available");
        return;
    }

    var svg = place.append("svg")
            .attr("width", "100%")
            .attr("height", height + margin.top + margin.bottom + 300)
            .append("g")
            .style("overflow", "visible")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")")
        ;



    var legend = svg.selectAll(".legend")
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
            .attr("y", function (d, i) { return hcrow.indexOf(i+1) * cellSize + 200; })
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
            .attr("y", function (d, i) { return hccol.indexOf(i+1) * cellSize; })
            .style("text-anchor", "left")
            .attr("transform", "translate("+cellSize/2 + ",-6) rotate (-90)")
            .attr("class",  function (d,i) { return "colLabel mono c"+i;} )
    ;

    var heatMap = svg.append("g").attr("class","g3")
            .selectAll(".cellg")
            .data(data.values ,function(d){return d.row+":"+d.col;})
            .enter()
            .append("rect")
            .attr("x", function(d) { return hccol.indexOf(d.col) * cellSize; })
            .attr("y", function(d) { return hcrow.indexOf(d.row) * cellSize + 200; })
            .attr("class", function(d){return "cell cell-border cr"+(d.row-1)+" cc"+(d.col-1);})
            .attr("width", cellSize)
            .attr("height", cellSize)
            .style("fill", function(d) { return colorScale(d.value); })
            /* .on("click", function(d) {
             var rowtext=d3.select(".r"+(d.row-1));
             if(rowtext.classed("text-selected")==false){
             rowtext.classed("text-selected",true);
             }else{
             rowtext.classed("text-selected",false);
             }
             })*/
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
                        "<br> Log10 frequency: " + d.value +
                        "<br> Frequency: " + d.frequency.toPrecision(2));
                //Show the tooltip
                d3.select("#heatmap_tooltip").classed("hidden", false);
            })
            .on("mouseout", function(){
                d3.select(this).classed("cell-hover",false);
                d3.selectAll(".rowLabel").classed("text-highlight",false);
                d3.selectAll(".colLabel").classed("text-highlight",false);
                d3.select("#heatmap_tooltip").classed("hidden", true);
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

function createSubstring(cdr, start, end, color) {
    "use strict";
    return {
        start: start,
        end: end,
        color: color,
        substring: cdr.substring(start, end + 1)
    };
}

function cdr3Transform(cdr, $sce) {
    "use strict";
    var cdr3aa = cdr.cdr3aa,
        cdr3nt = cdr.cdr3nt,
        vend_nt = cdr.vend,
        dstart_nt = (cdr.dstart < 0) ? vend_nt + 1 : cdr.dstart,
        dend_nt = (cdr.dend < 0) ? vend_nt : cdr.dend,
        jstart_nt = (cdr.jstart < 0) ? 10000 : cdr.jstart,
        vend_aa = Math.floor(cdr.vend / 3),
        dstart_aa = (Math.floor(cdr.dstart / 3) < 0) ? vend_aa + 1 : Math.floor(cdr.dstart / 3),
        dend_aa = (Math.floor(cdr.dend / 3) < 0) ? vend_aa : Math.floor(cdr.dend / 3),
        jstart_aa = (Math.floor(cdr.jstart / 3) < 0) ? 10000 : Math.floor(cdr.jstart / 3);

    var cdr3nt_arr = [],
        cdr3aa_arr = [];

    while (vend_nt >= jstart_nt) jstart_nt++;
    while (vend_aa >= jstart_aa) jstart_aa++;
    while (dstart_nt <= vend_nt) dstart_nt++;
    while (dstart_aa <= vend_aa) dstart_aa++;
    while (dend_nt >= jstart_nt) dend_nt--;
    while (dend_aa >= jstart_aa) dend_aa--;

    if (vend_nt >= 0) {
        cdr3nt_arr.push(createSubstring(cdr3nt, 0, vend_nt, "#4daf4a"));
    }

    if (vend_aa >= 0) {
        cdr3aa_arr.push(createSubstring(cdr3aa, 0, vend_aa, "#4daf4a"));
    }

    if (dstart_nt - vend_nt > 1) {
        cdr3nt_arr.push(createSubstring(cdr3nt, vend_nt + 1, dstart_nt - 1, "black"));
    }

    if (dstart_aa - vend_aa > 1) {
        cdr3aa_arr.push(createSubstring(cdr3aa, vend_aa + 1, dstart_aa - 1, "black"));
    }

    if (dstart_nt > 0 && dend_nt > 0 && dend_nt >= dstart_nt) {
        cdr3nt_arr.push(createSubstring(cdr3nt, dstart_nt, dend_nt, "#ec7014"));
    }

    if (dstart_aa > 0 && dend_aa > 0 && dend_aa >= dstart_aa) {
        cdr3aa_arr.push(createSubstring(cdr3aa, dstart_aa, dend_aa, "#ec7014"));
    }

    if (jstart_nt - dend_nt > 1) {
        cdr3nt_arr.push(createSubstring(cdr3nt, dend_nt + 1, jstart_nt - 1, "black"));
    }

    if (jstart_aa - dend_aa > 1) {
        cdr3aa_arr.push(createSubstring(cdr3aa, dend_aa + 1, jstart_aa - 1, "black"));
    }

    if (jstart_nt > 0) {
        cdr3nt_arr.push(createSubstring(cdr3nt, jstart_nt, cdr3nt.length, "#377eb8"));
    }

    if (jstart_aa > 0) {
        cdr3aa_arr.push(createSubstring(cdr3aa, jstart_aa, cdr3aa.length, "#377eb8"));
    }

    var cdr3nt_result = "", element, i = 0;
    for (i = 0; i < cdr3nt_arr.length; i++) {
        element = cdr3nt_arr[i];
        cdr3nt_result += '<text style="color: ' + element.color + '">' + element.substring + '</text>';
    }
    var cdr3aa_result = "";
    for (i = 0; i < cdr3aa_arr.length; i++) {
        element = cdr3aa_arr[i];
        cdr3aa_result += '<text style="color: ' + element.color + '">' + element.substring + '</text>';
    }
    cdr.cdr3aa = $sce.trustAsHtml(cdr3aa_result);
    cdr.cdr3nt = $sce.trustAsHtml(cdr3nt_result);
    return cdr;
}

function loading(place) {
    "use strict";
    var d3Place = d3.select(place);
        d3Place.html("");
        d3Place.style("display", "block");

    var loadingPlace = d3Place.append("div").attr("class", "loading");
    loadingPlace.append("div").attr("class", "wBall").attr("id", "wBall_1").append("div").attr("class", "wInnerBall");
    loadingPlace.append("div").attr("class", "wBall").attr("id", "wBall_2").append("div").attr("class", "wInnerBall");
    loadingPlace.append("div").attr("class", "wBall").attr("id", "wBall_3").append("div").attr("class", "wInnerBall");
    loadingPlace.append("div").attr("class", "wBall").attr("id", "wBall_4").append("div").attr("class", "wInnerBall");
    loadingPlace.append("div").attr("class", "wBall").attr("id", "wBall_5").append("div").attr("class", "wInnerBall");
}

function loaded(place) {
    "use strict";
    d3.select(place)
        .select(".loading")
        .remove();
}

function noDataAvailable(param, file) {
    "use strict";
    var p = param.place ? param.place : param;
    var place = d3.select(p);
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

$(document).ready(function() {
    $(document).bind('dragover', function (e) {
        "use strict";
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
        } while (node !== null);
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

    $('.data_popover').popover({
        trigger: 'focus'
    });
});

function testWatchers() {
    "use strict";
    var root = $(document.getElementsByTagName('body'));
    var watchers = [];

    var f = function (element) {
        if (element.data().hasOwnProperty('$scope')) {
            angular.forEach(element.data().$scope.$$watchers, function (watcher) {
                watchers.push(watcher);
            });
        }

        angular.forEach(element.children(), function (childElement) {
            f($(childElement));
        });
    };

    f(root);

    return watchers.length;
}
