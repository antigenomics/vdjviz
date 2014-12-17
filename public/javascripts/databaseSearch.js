(function () {
    var app = angular.module('databaseSearch', []);

    app.directive('database', function() {
        return {
            restrict: 'E',
            controller: ['$rootScope','$scope', '$http', '$location', function($rootScope, $scope, $http, $location) {

                //public variables

                //Functions
                $scope.showAdditionalInfo = showAdditionalInfo;
                $scope.hideAdditionalInfo = hideAdditionalInfo;
                $scope.isAdditionalInfo = isAdditionalInfo;
                $scope.searchInput = searchInput;
                $scope.isMainInfo = isMainInfo;
                $scope.isValid = isValid;
                $scope.isNoData = isNoData;
                $scope.isLoading = isLoading;
                $scope.isInitialized = isInitialized;

                //Variables
                $scope.initialized = false;
                $scope.noData = false;
                $scope.loading = false;
                $scope.searchResult = [];
                $scope.additionalInfo = {
                    data: null,
                    show: false
                };

                //On location change
                $rootScope.$on('$locationChangeSuccess', function() {
                    var newInfo = $location.search().info;
                    var input = $location.search().input;
                    if (input && input != $scope.input) {
                        $scope.initialized = false;
                        $scope.input = input;
                        searchInput();
                    }
                    if ($scope.searchResult.length > 0 && newInfo) {
                        angular.forEach($scope.searchResult, function(result) {
                            if (result.uid == newInfo) {
                                showAdditionalInfo(result);
                            }
                        })
                    } else if ($scope.searchResult.length > 0) {
                        hideAdditionalInfo();
                    }
                });

                //Initializing
                var input = $location.search().input;
                if (typeof input == 'string') {
                    $scope.input = input;
                    searchInput();
                } else {
                    $scope.input = 'CASSLAPGATNEKLFF';
                }

                function isValid() {
                    var regexp = /^[FLSYCWPHQRIMTNKVADEG]+$/;
                    return regexp.test($scope.input);
                }

                function showAdditionalInfo(result) {
                    $scope.additionalInfo.data = result;
                    $location.search('info', result.uid);
                    $location.search('input', result.input);
                    $scope.additionalInfo.show = true;
                }

                function searchInput() {
                    if (isValid() && !isLoading() && isNew()) {
                        $scope.loading = true;
                        $http.post('/database/api/search', {input: $scope.input})
                            .success(function (data) {
                                $location.search('input', $scope.input);
                                var info = $location.search().info;
                                $scope.searchResult = data;
                                $scope.noData = data.length == 0;
                                angular.forEach(data, function (result) {
                                    if (result.uid == info) {
                                        showAdditionalInfo(result);
                                    }
                                });
                                $scope.loading = false;
                                $scope.initialized = true;
                            })
                            .error(function () {
                                $location.search('input', $scope.input);
                                $scope.noData = true;
                                $scope.loading = false;
                            })

                    }
                }

                function hideAdditionalInfo() {
                    $location.search('info', null);
                    $scope.additionalInfo.show = false;
                }

                function isAdditionalInfo() {
                    return $scope.additionalInfo.show;
                }

                function isMainInfo() {
                    return !$scope.additionalInfo.show;
                }

                function isNoData() {
                    return $scope.noData;
                }

                function isLoading() {
                    return $scope.loading;
                }

                function isNew() {
                    return !isInitialized() || $scope.input != $location.search().input;
                }

                function isInitialized() {
                    return $scope.initialized;
                }
            }]
        }
    })
})();