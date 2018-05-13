var url_literature = 'http://129.174.10.65:8443/literature';
var url_metadata = 'http://129.174.10.65:8443/metadata';
var url_pubmed = 'http://129.174.10.65:8443/pubmed';
var url_crossref = 'http://129.174.10.65:8443/crossref';
var url_search = 'http://129.174.10.65:8443/search';

angular.module('articles.communication', []).
        factory('articlesCommunicationService', function ($http) {
            var getResumeNumbers = function () {
                return $http.get(url_literature + '/count').then(function (response) {
                    return response.data;
                });
            };

            var getArticleListByText = function (status, text, page, sortDirection, sortProperty) {
                return $http.get(url_literature + "/status/" + status + "?text=" + text
                        + "&page=" + page + "&sortDirection=" + sortDirection + "&sortProperty=" + sortProperty).then(function (response) {
                    return response.data;
                });
            };

            var updateArticle = function (article, id, collection, update) {
                return $http.put(url_literature + '/' + collection + '/' + id + '?update=' + update, article).then(function (response) {
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
                return $http.put(url_literature + '/status/' + id + "?articleStatus=" + articleStatus).then(function (response) {
                    return response.data;
                });
            };

            var updateSearch = function (id, search) {
                return $http.put(url_literature + '/' + id, search).then(function (response) {
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
            var getCrossRef = function (doi) {
                return $http.get(url_crossref + '?doi=' + doi).then(function (response) {
                    return response.data;
                });
            };
            var getPdf = function (id) {
                return $http.get(url_crossref + '/load/' + id, { responseType : 'arraybuffer' }).then(function (response) {
                    return response.data;
                });
            };
            var downloadPdf = function (id, doi) {
                return $http.get(url_crossref + '/download/' + '?doi=' + doi + '&id=' + id + "&download=true").then(function (response) {
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
                return $http.get(url_search + "/start", {timeout: canceller.promise}).then(function (response) {
                    return response.data;
                });
            };
            var stopSearch = function () {
                return $http.get(url_search + "/stop").then(function (response) {
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
            var removeArticle = function (idList, collection) {
                return $http.delete(url_literature + "?ids=" + idList).then(function (response) {
                    if (collection === 'Positive' || collection === 'Evaluated') {
                        return $http.delete(url_metadata + "?ids=" + idList).then(function (response) {
                            return response.data;
                        });
                    }
                });

            };
            var removeAllArticles = function (collection) {
                console.log(collection);
                if (collection != null) {
                    return $http.delete(url_literature + "/removeAll?status=" + collection).then(function (response) {
                    });
                } else {
                    return $http.delete(url_literature + "/removeAll").then(function (response) {
                        if (collection === 'Positive' || collection === 'Evaluated') {
                            return $http.delete(url_metadata + "/removeAll").then(function (response) {
                                return response.data;
                            });
                        }
                    });
                }

            };
            return {
                getResumeNumbers: getResumeNumbers,
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
                getCrossRef: getCrossRef,
                getPdf: getPdf,
                downloadPdf: downloadPdf,
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
