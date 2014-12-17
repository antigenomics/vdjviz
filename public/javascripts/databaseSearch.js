(function () {
    var app = angular.module('databaseSearch', []);

    app.directive('database', function() {
        return {
            restrict: 'E',
            controller: ['$scope', '$http', function($scope, $http) {
                $scope.input = 'CASSLAPGATNEKLFF';
                $scope.searchResult = [];
                $scope.additionalInfo = {
                    data: null,
                    show: false
                };
                $scope.showAdditionalInfo = false;
                $scope.searchInput = function() {
                    //loading(".loading-place");
                    $http.post('/database/api/search', { input: $scope.input })
                        .success(function(data) {
                            //loaded(".loading-place");
                            $scope.searchResult = data;
                        })
                        .error(function() {
                            //loaded(".loading-place");
                        })
                };

                $scope.showAdditionalInfo = function(result) {
                    $scope.additionalInfo.data = result;
                    console.log(result);
                    $scope.additionalInfo.show = true;
                };

                $scope.hideAdditionalInfo = function() {
                    $scope.additionalInfo.show = false;
                };

                $scope.isAdditionalInfo = function() {
                    return $scope.additionalInfo.show;
                };

                $scope.isMainInfo = function() {
                    return !$scope.additionalInfo.show;
                };


            }]
        }
    })
})();

function loading(place) {
    var d3Place = d3.select(place);
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