function SampleCtrl($scope, Scheduler){

    $scope.schedulers = Scheduler.query();
    $scope.scheduler = new Scheduler();

    $scope.orderProp = 'applicationName';

    $scope.addScheduler = function(){
             console.log(">>"+$scope.scheduler.applicationName);
             $scope.scheduler.$save();
    }

   $scope.findById = function(){
         var card = CreditCard().get({_id:1});
   }
}