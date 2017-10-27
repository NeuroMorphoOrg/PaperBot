var url_literature = 'http://localhost:8188/literature';
var url_reconstructions_status = 'http://localhost:8182/literature/reconstructions/status';
var url_metadata = 'http://localhost:8180/literature/metadata';
var url_pubmed = 'http://localhost:8186/literature/pubmed';

angular.module('articles.communication', []).
        factory('articlesCommunicationService', function ($http) {
            var getResumeNumbers = function () {
                return $http.get(url_literature + '/count').then(function (response) {
                    return response.data;
                });
            };

            var getArticleList = function (query, page) {
                var text = "";
                for (var i in query) {
                    text = text + query[i] + "&";
                }
                return $http.get(url_literature + "/query?" + text + "page=" + page).then(function (response) {
                    return response.data;
                });
            };
            var getArticleListByText = function (status, text, page) {
                return $http.get(url_literature + "/status/" + status + "?text=" + text + "&page=" + page).then(function (response) {
                    return response.data;
                });
            };

            var updateArticle = function (id, article) {
                return $http.put(url_literature + '/' + id, article).then(function (response) {
                    return response.data;
                });
            };


            var findMetadata = function (id) {
                return $http.get(url_metadata + '/' + id).then(function (response) {
                    return response.data;
                });
            };

            var updateMetadata = function (id, metadata) {
                return $http.put(url_metadata + '/' + id, metadata).then(function (response) {
                    return response.data;
                });
            };

            var getMetadataValues = function (key) {
                return $http.get(url_metadata + '/values?key=' + key).then(function (response) {
                    return response.data;
                });
            };

            var updateCollection = function (id, articleStatus) {
                return $http.put(url_literature + '/collection/' + id + "?articleStatus=" + articleStatus).then(function (response) {
                    return response.data;
                });
            };

            var updateSearch = function (id, search) {
                return $http.put(url_literature + '/search/' + id, search).then(function (response) {
                    return response.data;
                });
            };

            var findArticle = function (id) {
                return $http.get(url_literature + '/' + id).then(function (response) {
                    return response.data;
                });
            };

            var findArticleByPmid = function (pmid) {
                return $http.get(url_literature + '?pmid=' + pmid).then(function (response) {
                    return response.data;
                });
            };

            var getPubMed = function (pmid) {
                return $http.get(url_pubmed + '?db=pubmed&pmid=' + pmid).then(function (response) {
                    return response.data;
                });
            };

            var getObjectId = function () {
                return $http.get(url_literature + '/objectId').then(function (response) {
                    return response.data;
                });
            };

            return {
                getResumeNumbers: getResumeNumbers,
                getArticleList: getArticleList,
                updateArticle: updateArticle,
                findMetadata: findMetadata,
                getMetadataValues: getMetadataValues,
                updateMetadata: updateMetadata,
                updateCollection: updateCollection,
                updateSearch: updateSearch,
                findArticle: findArticle,
                findArticleByPmid: findArticleByPmid,
                getPubMed: getPubMed,
                getArticleListByText: getArticleListByText,
                getObjectId: getObjectId

            };

        });
