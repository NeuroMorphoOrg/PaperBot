angular.module('Articles').
        controller('MetadataController', function ($rootScope, $scope, $routeParams, $window, $filter, articlesCommunicationService, articlesService) {


            // Values for the metadata options, can be updated retrieve values from database
            // udating "key"
//            articlesCommunicationService.getMetadataValues("key").then(function (data) {
//                $scope.tracingOptionList = data;
//            }).catch(function () {
//                $scope.error = 'Error updating metadata';
//            });
            $scope.statuses = [
                {value: 1, text: 'Pending evaluation'},
                {value: 2, text: 'Positive'},
                {value: 3, text: 'Negative'},
                {value: 4, text: 'Inaccessible'}

            ];
            var collection = $rootScope.articleStatus;
            var status = 0;

            if ($routeParams.id !== undefined) {
                articlesCommunicationService.findMetadata($rootScope.id).then(function (data) {
                    $scope.metadata = data;
                    if ($rootScope.articleStatus === 'Evaluated') {
                        $rootScope.articleStatus = $scope.metadata.articleStatus;
                    }
                    $scope.statuses.forEach(function (element) {
                        if (element.text === $rootScope.articleStatus) {
                            status = element.value;
                        }
                    });
                    $scope.radio = {
                        status: status
                    };

                    $scope.review = $routeParams.review;
                }).catch(function () {
                    $scope.error = 'Error finding metadata';
                });

            }
            $scope.showStatus = function () {
                var selected = $filter('filter')($scope.statuses, {value: $scope.radio.status});
                return ($scope.radio.status && selected.length) ? selected[0].text : 'Not set';
            };


            $scope.updateMetadata = function () {
                $scope.statuses.forEach(function (element) {
                    if (element.value === $scope.radio.status) {
                        collection = element.text;
                    }
                });
                if ($routeParams.review === '1') {
                    $scope.metadata.articleStatus = collection;
                    collection = 'Evaluated';
                }

                articlesCommunicationService.updateCollection($rootScope.id, collection).then(function () {
                    articlesCommunicationService.updateMetadata($rootScope.id, $scope.metadata).then(function () {
                    }).catch(function () {
                        $scope.error = 'Error updating metadata';
                    });
                    articlesService.getCountArticles($scope, articlesCommunicationService);
                    if (collection === 'Positive') {
                        $rootScope.articlePositive = true;
                    }
                }).catch(function () {
                    $scope.error = 'Error updating collection';
                });


            };
        });



