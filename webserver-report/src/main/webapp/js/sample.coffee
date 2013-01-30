 window.FooCtrl = ($scope)->

    $scope.bar = "hi there"
    $scope.mDate = ""

    $scope.doSthg = () ->
   		console.log ("doSthg")
   		$scope.mDate = "toto"

   	$scope.doClick = () ->
   			console.log("toto")

