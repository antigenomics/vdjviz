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

                //Variables
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
                    if (typeof newInfo === 'undefined') {
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
                    $location.search('info', result.hash);
                    $scope.additionalInfo.show = true;
                }

                function searchInput() {
                    if (isValid() && !isLoading()) {
                        $scope.loading = true;
                        $http.post('/database/api/search', {input: $scope.input})
                            .success(function (data) {
                                $location.search('input', $scope.input);
                                var info = $location.search().info;
                                $scope.searchResult = data;
                                $scope.noData = data.length == 0;
                                angular.forEach(data, function (result) {
                                    if (result.hash == info) {
                                        showAdditionalInfo(result);
                                    }
                                });
                                $scope.loading = false;
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
            }]
        }
    })
})();