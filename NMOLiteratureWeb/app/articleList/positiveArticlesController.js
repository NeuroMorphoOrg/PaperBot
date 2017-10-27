angular.module('Articles').
        controller('PositiveArticlesController', function ($scope, $routeParams, articlesCommunicationService, articlesService) {
            $scope.dataStatus = [];
            $scope.currentPage = 1;
            $scope.text = '';
            $scope.pmidList = {};
            $scope.pmidList.values = [];

            var query = ["articleStatus=Positive", "usage=" + $routeParams.usage];


            articlesService.findArticles($scope, query, articlesCommunicationService);

            $scope.setPage = function () {
                $scope.filterDetails;
                if ($scope.text !== null && $scope.text !== '') {
                    articlesService.findByText($scope, "Positive", articlesCommunicationService);
                } else if ($scope.filterDetails === undefined) {
                    articlesService.findArticles($scope, query, articlesCommunicationService);
                } else {
                    articlesService.findBySpecificDetails($scope.filterDetails, articlesCommunicationService, $scope);
                }
            };

            $scope.findArticlesByText = function (text) {
                $scope.text = text;
                articlesService.findByText($scope, "Positive", articlesCommunicationService);
            };

            $scope.filterReconstructionsStatus = function () {
                articlesService.findBySpecificDetails($scope.filterDetails, articlesCommunicationService, $scope);
            };

            $scope.submitStatus = function (article) {
                articlesCommunicationService.updateStatusReconstructions(article.id, article.reconstructionsStatusList).then(function (data) {
                    article.updated = true;
                }).catch(function () {
                    article.error = true;
                });

            };
            
            $scope.pmidList = [];
           
        });
