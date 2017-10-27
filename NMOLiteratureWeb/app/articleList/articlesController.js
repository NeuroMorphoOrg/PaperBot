angular.module('Articles').
        controller('ArticlesController', function ($scope, $routeParams, articlesCommunicationService, articlesService) {
            $scope.currentPage = 1;
            $scope.text = '';
            $scope.review = $routeParams.review;
            var query = ["articleStatus=" + $routeParams.usage];

            articlesService.findArticles($scope, query, articlesCommunicationService);


            $scope.findArticlesByText = function (text) {
                $scope.text = text;
                articlesService.findByText($scope, $routeParams.usage, articlesCommunicationService);
            };
            $scope.setPage = function () {
                if ($scope.text !== null && $scope.text !== '') {
                    articlesService.findByText($scope, $routeParams.usage, articlesCommunicationService);
                } else {
                    articlesService.findArticles($scope, query, articlesCommunicationService);

                }
            };

            $scope.getKeyWordSet = function (portalList) {
                var keyWordSet = new Set();
                if (portalList != null) {
                    portalList.forEach(function (element) {
                        (element.keyWordList.forEach(function (elementList) {
                            keyWordSet.add(elementList);
                        }));
                    });
                } else {
                    keyWordSet.add("Article added manually");
                }
                var keyWordArray = [];
                keyWordSet.forEach(function (element) {
                    keyWordArray.push(element);
                });
                return keyWordArray;
            };

            $scope.acceptEvaluatedArticle = function (id, metadata, index) {
                var articleStatus = metadata.articleStatus;
                metadata.articleStatus = null;
                articlesCommunicationService.updateCollection(id, articleStatus).then(function () {
                    $scope.$emit('child'); // going up!
                    $scope.articlePage.content.splice(index, 1);
                    $scope.articlePage.totalElements--;
                    $scope.lastElement--;
                }).catch(function () {
                    $scope.error = 'Error accepting article';
                });
                if (articleStatus === 'Positive') {
                    articlesCommunicationService.getStatusReconstructions(id).then(function (data) {
                        var reconstructions = data;
                        if (reconstructions.reconstructionsStatusList=== null ||
                                reconstructions.reconstructionsStatusList.length === 0) {
                            var reconstructionsStatusList = [];
                            reconstructionsStatusList.push({
                                id: 1,
                                statusDetails: 'To be requested',
                                nReconstructions: metadata.nReconstructions

                            });

                            articlesCommunicationService.updateStatusReconstructions(id, reconstructionsStatusList).then(function (data) {
                            }).catch(function () {
                                $scope.error = 'Unable to save the status reconstructions ';
                            });
                        }
                    });

                }
            };

        });

