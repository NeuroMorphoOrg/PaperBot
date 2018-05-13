angular.module('articles.service', []).
        factory('articlesService', function () {
             var getYear = function (scope) {
                scope.year = new Date().getFullYear();

            };

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
                
                    articlesCommunicationService.getArticleListByText(collection, scope.text, scope.currentPage - 1, scope.sortDirection, scope.sortProperty).then(function (data) {
                        scope.articlePage = data.articlePage;
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
           

            return {
                getYear: getYear,
                getCountArticles: getCountArticles,
                findArticles: findArticles,
                calculatePages: calculatePages
            };

        });
