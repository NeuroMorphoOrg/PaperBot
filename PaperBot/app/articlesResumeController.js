var articlesResumeModule = angular.module('ArticlesResume', ['articles.communication', 'articles.service']);

articlesResumeModule.controller('articles.controller', function ($scope, articlesCommunicationService, articlesService) {
    $scope.count = {};
    articlesService.getCountArticles($scope, articlesCommunicationService);
    articlesService.getYear($scope);
});
