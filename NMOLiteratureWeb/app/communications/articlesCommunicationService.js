var url_literature = 'http://ec2-18-219-56-191.us-east-2.compute.amazonaws.com:8188/literature';
var url_metadata = 'http://ec2-18-219-56-191.us-east-2.compute.amazonaws.com:8180/literature/metadata';
var url_pubmed = 'http://ec2-18-219-56-191.us-east-2.compute.amazonaws.com:8186/literature/pubmed';
var url_crosref = 'http://ec2-18-219-56-191.us-east-2.compute.amazonaws.com:8184/literature/crossref';
var url_search = 'http://ec2-18-219-56-191.us-east-2.compute.amazonaws.com:8187/literature';

angular.module('articles.communication', []).
        factory('articlesCommunicationService', function ($http) {
            var getResumeNumbers = function () {
                return $http.get(url_literature + '/count').then(function (response) {
                    return response.data;
                });
            };

            var getArticleList = function (collection, query, page) {
                var text = "";
                if (query != null) {
                    text = query + "&";
                }
                return $http.get(url_literature + "/query?collection=" + collection + "&" + text + "page=" + page).then(function (response) {
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
            var getPMIDFromTitle = function (title) {
                return $http.get(url_pubmed + '/pmid?db=pubmed&title=' + title).then(function (response) {
                    return response.data;
                });
            };
            var getCrosRef = function (doi) {
                return $http.get(url_crosref + '?doi=' + doi).then(function (response) {
                    return response.data;
                });
            };

            var getObjectId = function () {
                return $http.get(url_literature + '/objectId').then(function (response) {
                    return response.data;
                });
            };
            var getPortalList = function () {
                return $http.get(url_search + "/portals").then(function (response) {
                    return response.data;
                });
            };
            var updatePortalList = function (portalList) {
                return $http.put(url_search + "/portals", portalList).then(function (response) {
                    return response.data;
                });
            };
            var getLogList = function () {
                return $http.get(url_search + "/portals/log").then(function (response) {
                    return response.data;
                });
            };
            var launchSearch = function (canceller) {
                return $http.get(url_search + "/search/start", {timeout: canceller.promise}).then(function (response) {
                    return response.data;
                });
            };
            var stopSearch = function () {
                return $http.get(url_search + "/search/stop").then(function (response) {
                    return response.data;
                });
            };
            var getKeyWordList = function () {
                return $http.get(url_search + "/keywords").then(function (response) {
                    return response.data;
                });
            };
            var updateKeyWordList = function (keyWordList) {
                return $http.put(url_search + "/keywords", keyWordList).then(function (response) {
                    return response.data;
                });
            };
            var deleteKeyWordList = function (keyWordIdList) {
                return $http.delete(url_search + "/keywords?ids=" + keyWordIdList).then(function (response) {
                    return response.data;
                });
            };
            var removeArticle = function (idList) {
                return $http.delete(url_literature + "?ids=" + idList).then(function (response) {
                    return $http.delete(url_metadata + "?ids=" + idList).then(function (response) {
                        return response.data;
                    });
                });

            };
            var removeAllArticles = function (collection) {
                if (collection != null) {
                    return $http.delete(url_literature + "/removeAll?collection=" + collection).then(function (response) {
                    });
                } else {
                    return $http.delete(url_literature + "/removeAll").then(function (response) {
                        return $http.delete(url_metadata + "/removeAll").then(function (response) {
                            return response.data;
                        });
                    });
                }

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
                getPMIDFromTitle: getPMIDFromTitle,
                getCrosRef: getCrosRef,
                getArticleListByText: getArticleListByText,
                getObjectId: getObjectId,
                getPortalList: getPortalList,
                updatePortalList: updatePortalList,
                getLogList: getLogList,
                launchSearch: launchSearch,
                stopSearch: stopSearch,
                getKeyWordList: getKeyWordList,
                updateKeyWordList: updateKeyWordList,
                deleteKeyWordList: deleteKeyWordList,
                removeArticle: removeArticle,
                removeAllArticles: removeAllArticles

            };

        });
