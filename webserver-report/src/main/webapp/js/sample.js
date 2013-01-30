(function() {

  window.FooCtrl = function($scope) {
    $scope.bar = "hi there";
    $scope.mDate = "";
    $scope.doSthg = function() {
      console.log("doSthg");
      return $scope.mDate = "toto";
    };
    return $scope.doClick = function() {
      return console.log("toto");
    };
  };

}).call(this);
