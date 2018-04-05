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

            };
            
            var findByText = function (scope, usage, articlesCommunicationService, status) {
                articlesCommunicationService.getArticleListByText(usage, scope.text, scope.currentPage - 1).then(function (data) {
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

                return {
                    findByText: findByText
                };

            };
            var calculatePages = function (scope) {
                scope.firstElement = (scope.currentPage - 1) * (scope.articlePage.size) + 1;
                if (scope.articlePage.last) {
                    scope.lastElement = scope.articlePage.totalElements;
                } else {
                    scope.lastElement = scope.currentPage * scope.articlePage.numberOfElements;
                }
            };
            var findArticles = function (scope, collection, query, articlesCommunicationService) {
                articlesCommunicationService.getArticleList(collection, query, scope.currentPage - 1).then(function (data) {
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
            };
           
            return {
                getCountArticles: getCountArticles,
                findByText: findByText,
                findArticles: findArticles,
                calculatePages: calculatePages
            };

        });
