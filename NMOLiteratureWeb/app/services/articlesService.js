angular.module('articles.service', []).
        factory('articlesService', function () {
            var getCountArticles = function (scope, articlesCommunicationService) {
                articlesCommunicationService.getResumeNumbers().then(function (data) {
                    scope.count.negative = data.Negative;
                    scope.count.inaccessible = data.Inaccessible;
                    scope.count.toEvaluate = data['Pending evaluation'];
                    scope.count.evaluated = data.Evaluated;
                    scope.count.positive = data.Positive;
                }).catch(function () {
                    scope.error = 'unable to get the articles resume numbers';
                });
                articlesCommunicationService.getLogList().then(function (data) {
                    scope.count.log = data[0];
                }).catch(function () {
                    scope.error = 'unable to get the articles resume numbers';
                });

            };

            var calculatePages = function (scope) {
                scope.firstElement = (scope.currentPage - 1) * (scope.articlePage.size) + 1;
                if (scope.articlePage.last) {
                    scope.lastElement = scope.articlePage.totalElements;
                } else {
                    scope.lastElement = scope.currentPage * scope.articlePage.numberOfElements;
                }
            };
            var findArticles = function (scope, collection, articlesCommunicationService) {
                if (scope.text == null) {
                    console.log("Nunca entra por awui: getArticleList");
                    articlesCommunicationService.getArticleList(collection, scope.currentPage - 1).then(function (data) {
                        scope.articlePage = data;
                        data.content.forEach(function (a) {
                            articlesCommunicationService.findMetadata(a.id).then(function (data2) {
                                a.metadata = data2;
                            });
                        });
                        calculatePages(scope);
                    }).catch(function () {
                        scope.error = 'unable to get the article list';
                    });
                } else {
                    articlesCommunicationService.getArticleListByText(collection, scope.text, scope.currentPage - 1, scope.sortDirection, scope.sortProperty).then(function (data) {
                        scope.articlePage = data;
                        if (collection === 'Positive' || collection === 'Evaluated') {
                            data.content.forEach(function (a) {
                                articlesCommunicationService.findMetadata(a.id).then(function (data2) {
                                    a.metadata = data2;
                                });
                            });
                        }
                        calculatePages(scope);
                    }).catch(function () {
                        scope.error = 'unable to get the article list';
                    });

                }
            };

            return {
                getCountArticles: getCountArticles,
                findArticles: findArticles,
                calculatePages: calculatePages
            };

        });
