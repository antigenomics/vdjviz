(function () {
    var app = angular.module('databaseSearch', []);

    app.directive('database', function() {
        return {
            restrict: 'E',
            controller: ['$rootScope','$scope', '$http', '$location', function($rootScope, $scope, $http, $location) {

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

                //private
                var initialized = false;
                var noData = false;
                var loading = false;

                //public
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
                        initialized = false;
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
                        loading = true;
                        $http.post('/database/api/search', {input: $scope.input})
                            .success(function (data) {
                                $scope.searchResult = data;
                                //Show additional info
                                var info = $location.search().info;
                                var found = false;
                                angular.forEach(data, function (result) {
                                    if (result.uid == info) {
                                        showAdditionalInfo(result);
                                        found = true;
                                    }
                                });
                                if (!found) {
                                    $location.search('info', null);
                                }
                            })
                            .error(function() {
                                $scope.searchResult = [];
                                $location.search('info', null);
                            })
                            .finally(function() {
                                $location.search('input', $scope.input);
                                noData = $scope.searchResult.length == 0;
                                loading = false;
                                initialized = true;
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
                    return noData;
                }

                function isLoading() {
                    return loading;
                }

                function isNew() {
                    return !isInitialized() || $scope.input != $location.search().input;
                }

                function isInitialized() {
                    return initialized;
                }
            }]
        }
    })
})();