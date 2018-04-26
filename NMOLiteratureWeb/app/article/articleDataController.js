angular.module('Articles').
        controller('ArticleDataController', function ($rootScope, $scope, $routeParams, $window, $filter, articlesCommunicationService) {
            $rootScope.show = false;
            $scope.error = '';
            $scope.radio = {
                status: 1
            };

            $scope.articleSaved = false;
            $rootScope.articlePositive = false;

            $rootScope.id = $routeParams.id;
            $scope.article = {};
            $scope.article.authorList = [];

            if ($rootScope.id !== undefined) {
                articlesCommunicationService.findArticle($rootScope.id).then(function (data) {
                    $scope.articleSaved = true;
                    $scope.article = data;
                    $rootScope.articleStatus = $scope.article.articleStatus;

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

                articlesCommunicationService.updateArticle($rootScope.id, $scope.article).then(function () {
                    $scope.articleSaved = true;

                }).catch(function (response) {
                    if (response.status === 409) {
                        $scope.error = response.data.errorMessage;
                    } else {
                        $scope.error = 'Error updating article';
                    }
                });
            };


            $scope.getPubMed = function () {
                if ($rootScope.id == null) {
                    $rootScope.articleStatus = 'Pending evaluation';
                    $scope.article.searchPortal = [];
                    $scope.article.searchPortal.push({'name': 'manual', 'keyWordList': ['article added manually']});

                    articlesCommunicationService.getObjectId().then(function (data) {
                        $rootScope.id = data.id;
                    }).catch(function () {
                        $scope.error = 'Error generating id for the new article';
                    });
                }
                $scope.error = '';
                articlesCommunicationService.getPubMed($scope.article.pmid).then(function (data) {
                    replaceData($scope, data);
                }).catch(function () {
                    $scope.error = 'Unable to get pubMed data';
                });
            };
            $scope.getCrosRef = function () {

                if ($rootScope.id == null) {
                    $rootScope.articleStatus = 'Pending evaluation';
                    $scope.article.searchPortal = [];
                    $scope.article.searchPortal.push({'name': 'manual', 'keyWordList': ['article added manually']});

                    articlesCommunicationService.getObjectId().then(function (data) {
                        $rootScope.id = data.id;
                    }).catch(function () {
                        $scope.error = 'Error generating id for the new article';
                    });
                }
                $scope.error = '';
                articlesCommunicationService.getCrosRef($scope.article.doi).then(function (data) {
                    replaceData($scope, data);
                     if ($scope.article.pmid == null) {
                        //getPmid from title
                        articlesCommunicationService.getPMIDFromTitle(data.title).then(function (pmid) {
                            if ($scope.article.pmid !== null) {
                                $scope.article.pmid = pmid;
                            }
                        });
                    }
                }).catch(function () {
                    $scope.error = 'DOI not found in Crosef';
                });
            };

            $scope.removeAuthor = function (index) {
                $scope.article.authorList.splice(index, 1);
            };

            $scope.addAuthor = function () {
                var author = {name: '', email: ''};
                $scope.article.authorList.push(author);
            };

            $scope.opened = {};

            $scope.open = function ($event, elementOpened) {
                $event.preventDefault();
                $event.stopPropagation();

                $scope.opened[elementOpened] = !$scope.opened[elementOpened];
            };


            $scope.removeArticle = function () {
                if (confirm("You re about to remove the article, press OK to confirm otherwise press Cancel")) {
                    var idList = [];
                    idList.push($rootScope.id);
                    articlesCommunicationService.removeArticle(idList, $rootScope.articleStatus).then(function () {
                        $window.history.back();
                        $scope.$emit('child'); // going up!
                    }).catch(function (response) {
                        $scope.error = 'Error removing article';
                    });
                    window.close();
                    window.onunload = window.opener.location.reload();
                } else {
                }
            };

        });


var replaceData = function (scope, data) {
    if (scope.article.title == null || scope.article.title != data.title) {
        scope.article.title = data.title;
    }
    if (scope.article.pmid == null) {
        scope.article.pmid = data.pmid;
    }
    if (scope.article.doi == null || scope.article.title != data.doi) {
        scope.article.doi = data.doi;
    }
    if (scope.article.journal == null || scope.article.title != data.journal) {
        scope.article.journal = data.journal;
    }
    if (scope.article.authorList.length != data.authorList.length) {
        scope.article.authorList = data.authorList;
    }
    for (i = 0; i < scope.article.authorList.length; i++) {
        if (scope.article.authorList[i].email == null) {
            scope.article.authorList[i].email = data.authorList[i].email;
        }
    }
    if (scope.article.publishedDate == null || scope.article.title != data.publishedDate) {
        scope.article.publishedDate = new Date(data.publishedDate);
    }

};

