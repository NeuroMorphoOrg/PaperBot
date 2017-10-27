var articles = angular.module('Articles', ['ngRoute', 'ui.bootstrap', 'xeditable', 'checklist-model', 'articles.communication', 'articles.service']);


function articlesRouteConfig($routeProvider) {
    $routeProvider.
            when('/articles/:usage/:review', {
                controller: 'ArticlesController',
                templateUrl: 'articles.html'
            }).
             when('/addArticle', {
                controller: 'ArticleDataController',
                templateUrl: '/NMOLiteratureWeb/article/article.html'
            }).
             when('/review/:usage/:review', {
                controller: 'ArticlesController',
                templateUrl: 'reviewEvaluatedArticles.html'
            }).
            when('/view/:id/:review', {
                controller: 'ArticleDataController',
                templateUrl: '/NMOLiteratureWeb/article/article.html'
            }).
            when('/positiveArticles/:usage', {
                controller: 'PositiveArticlesController',
                templateUrl: 'positiveArticles.html',
            }).
            otherwise({
                redirectTo: 'articles/:usage'
            });
}
articles.config(articlesRouteConfig);

articles.controller('Controller', function ($scope, articlesCommunicationService, articlesService) {
    $scope.count={};
    articlesService.getCountArticles($scope, articlesCommunicationService);
    $scope.$on('child', function (event, data) {
        articlesService.getCountArticles($scope, articlesCommunicationService);
    });

});


