angular
    .module('globalErrors', [])
    .config(function($provide, $httpProvider, $compileProvider) {
        var elementsList = $();

        var showMessage = function(content, mClass, time) {
        console.log("showMessage");
            $('<div/>')
                .addClass('message')
                .addClass(mClass)
                .hide()
                .fadeIn('fast')
                .delay(time)
                .fadeOut('fast', function() { $(this).remove(); })
                .appendTo(elementsList)
                .text(content);
        };

        $httpProvider.responseInterceptors.push(function($timeout) {
            return function(promise) {
                return promise.then(function(successResponse) {
                    if (successResponse.config.method.toUpperCase() != 'GET')
                        showMessage('Success', 'successMessage', 5000);
                    return successResponse;

                }, function(errorResponse) {
                    switch (errorResponse.status) {
                        case 401:
                            showMessage('Wrong username or password', 'errorMessage', 20000);
                            break;
                        case 403:
                            showMessage('You don\'t have the right to do this', 'errorMessage', 20000);
                            break;
                        case 500:
                            showMessage('Server internal error: ' + errorResponse.data, 'errorMessage', 20000);
                            break;
                        default:
                            showMessage('Error ' + errorResponse.status + ': ' + errorResponse.data, 'errorMessage', 20000);
                    }
                    return errorResponse;
                });
            };
        });

        $compileProvider.directive('appMessages', function() {
            var directiveDefinitionObject = {
                link: function(scope, element, attrs) { elementsList.push($(element)); }
            };
            return directiveDefinitionObject;
        });
    });