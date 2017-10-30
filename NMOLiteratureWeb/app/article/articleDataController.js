angular.module('Articles').
        controller('ArticleDataController', function ($rootScope, $scope, $routeParams, $window, $filter, articlesCommunicationService) {
            $scope.error = '';
            $scope.radio = {
                status: 1,
                usage: [1]
            };
           
            $scope.usages = [
                {value: 1, text: 'Describing'}
            ];

            $rootScope.id = $routeParams.id;
            $rootScope.usage = [];
            $scope.article = {};
            $scope.article.authorList = [];
            if ($rootScope.id !== undefined) {
                articlesCommunicationService.findArticle($rootScope.id).then(function (data) {
                    $scope.article = data;
                    $rootScope.usage = $scope.article.usage;
                    $rootScope.articleStatus = $scope.article.articleStatus;
                    var usage = [];
                    $scope.usages.forEach(function (a) {
                        $scope.article.usage.forEach(function (b) {
                            if (a.text === b) {
                                usage.push(a.value);
                            }
                        });
                    });
                    $scope.radio = {
                        usage: usage
                    };

                }).catch(function () {
                    $scope.error = 'Error getting article details';
                });
            }

            $scope.opened = {};

            $scope.open = function ($event, elementOpened) {
                $event.preventDefault();
                $event.stopPropagation();

                $scope.opened[elementOpened] = !$scope.opened[elementOpened];
            };


            $scope.updateArticle = function () {
                $scope.article.usage = [];
                $rootScope.usage = [];
                $scope.usages.forEach(function (a) {
                    $scope.radio.usage.forEach(function (b) {
                        if (a.value === b) {
                            $scope.article.usage.push(a.text);
                            $rootScope.usage.push(a.text);
                        }
                    });
                });
                articlesCommunicationService.updateArticle($rootScope.id, $scope.article).then(function () {
                }).catch(function (response) {
                    if (response.status === 409) {
                        $scope.error = response.data.errorMessage;
                    } else {
                        $scope.error = 'Error updating article';
                    }
                });
                var describingNeurons = false;
                $scope.article.usage.forEach(function (element) {
                    if (element === "Describing") {
                        describingNeurons = true;
                    }
                });
                if (!describingNeurons) {
                    articlesCommunicationService.removeStatusReconstructions($rootScope.id).then(function (data) {
                    }).catch(function () {
                        $scope.error = 'Unable to save the status reconstructions ';
                    });
                }

            };

           
            $scope.getPubMed = function () {
                $rootScope.articleStatus = 'To evaluate';
                $scope.article.searchPortal = {};
                $scope.article.searchPortal.source = 'manual';
                articlesCommunicationService.getObjectId().then(function (data) {
                    $rootScope.id = data.id;
                }).catch(function () {
                    $scope.error = 'Error generating id for the new article';
                });
                $scope.error = '';
                articlesCommunicationService.getPubMed($scope.article.pmid).then(function (data) {
                    $scope.article.title = data.title;
                    $scope.article.pmid = data.pmid;
                    $scope.article.doi = data.doi;
                    $scope.article.journal = data.journal;
                    $scope.article.authorList = data.authorList;
                    $scope.article.publishedDate = new Date(data.publishedDate);
                    $scope.article.link = null;

                }).catch(function () {
                    $scope.error = 'Unable to get pubMed data';
                });
            };

            $scope.removeAuthor = function (index) {
                $scope.article.authorList.splice(index, 1);
            };

            $scope.addAuthor = function () {
                var author = {name: '', email: ''};
                $scope.article.authorList.push(author);
            };
            $scope.showUsage = function () {
                var selected = [];
                angular.forEach($scope.usages, function (s) {
                    if ($scope.radio.usage.indexOf(s.value) >= 0) {
                        selected.push(s.text);
                    }
                });
                return selected.length ? selected.join(', ') : 'Not set';
            };

           
            $scope.opened = {};

            $scope.open = function ($event, elementOpened) {
                $event.preventDefault();
                $event.stopPropagation();

                $scope.opened[elementOpened] = !$scope.opened[elementOpened];
            };
        });





