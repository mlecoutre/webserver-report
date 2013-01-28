(function() {

  window.FooCtrl = function($scope) {
    $scope.bar = "hi there";
    $scope.mDate = "";
    return $scope.doSthg = function() {
      console.log("doSthg");
      return $scope.mDate = "toto";
    };
  };

}).call(this);
