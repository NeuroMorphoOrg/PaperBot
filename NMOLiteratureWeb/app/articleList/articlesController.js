angular.module('Articles').
        controller('ArticlesController', function ($rootScope, $scope, $routeParams, articlesCommunicationService, articlesService) {
            $rootScope.show = true;
            $scope.currentPage = 1;
            $scope.sortDirection = 'ASC';
            $scope.text = '';
            $scope.review = $routeParams.review;
            $scope.collection = $routeParams.usage;
            articlesService.findArticles($scope, $routeParams.usage, articlesCommunicationService);
            $scope.findArticlesByText = function (text) {
                $scope.text = text;
                articlesService.findArticles($scope, $routeParams.usage, articlesCommunicationService);
            };
            $scope.setPage = function () {
                articlesService.findArticles($scope, $routeParams.usage, articlesCommunicationService);
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
            };

            $scope.removeArticleDB = function () {
                if (confirm("You are about to erase the " + $scope.collection + " colletion from the database, press OK to confirm otherwise press Cancel")) {
                    $scope.cleaning = true;
                    articlesCommunicationService.removeAllArticles($scope.collection).then(function (data) {
                        $scope.cleaning = false;
                         window.location.reload();
                    }).catch(function () {
                        $scope.cleaning = false;
                        $scope.error = 'Error erasing the collection';
                    });
                } else {
                }
            };
            $scope.navigationUrl = function (id, review) {
                window.open('#/view/' +id + '/' +review, '_blank');
            };

            $scope.getArticlesBy = function (sortProperty) {
                if ($scope.sortDirection === 'ASC'){
                    $scope.sortDirection = 'DESC';
                }else{
                    $scope.sortDirection = 'ASC';
                }
                $scope.sortProperty = sortProperty;
                articlesService.findArticles($scope, $routeParams.usage, articlesCommunicationService);
            };
        });

