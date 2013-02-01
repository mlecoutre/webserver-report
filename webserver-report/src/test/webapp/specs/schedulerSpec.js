describe('Testing Scheduler service', function() {

    var schedulerCtrl, scope;

    var schedulersList = [{
        "serverName": "intsip51",
        "requestRepeatIntervalInMinutes": 10,
        "applicationName": "Antares",
        "asName": "AS_MQBUFFER",
        "maxHistoryToKeepInDays": 186,
        "initialState": "running",
        "endPointURL": "http://intsip51:9081/MonitoringServlet?diagnoseRes&log",
        "lastExecution": 1359736482715,
        "lastStatus": "OK",
        "schedulerId": "50c9ad35f8328b4692fee9d3",
        "state": "running"
    }, {
        "serverName": "dun-app-devf01",
        "requestRepeatIntervalInMinutes": 10,
        "applicationName": "Deploycenter",
        "asName": "AS_DEPLOYCENTER",
        "maxHistoryToKeepInDays": 186,
        "initialState": "running",
        "endPointURL": "http://web-deploycenter/deploymentcenter-ui/MonitoringServlet?diagnoseAll&log",
        "lastExecution": 1359736482715,
        "lastStatus": "OK",
        "schedulerId": "50c9ad84f8328b4692fee9d4",
        "state": "running"
    }];
    var mScheduler = {
        "serverName": "testServerName",
        "requestRepeatIntervalInMinutes": 10,
        "applicationName": "TestAppName",
        "asName": "AS_TEST",
        "maxHistoryToKeepInDays": 2,
        "initialState": "running",
        "endPointURL": "http://endpointURL",
        "lastExecution": 1359736482715,
        "lastStatus": "OK",
        "schedulerId": "schedulerId",
        "state": "running"
    };

    /**
     * Write a mock of the scheduler ngResource
     */
    angular.module('MockScheduler', []).factory('Scheduler', function() {
        function Scheduler() {
            this.query = function() {
                console.log("mock query");
                return schedulersList;
            }

            this.save = function(scheduler) {
                console.log("mock save scheduler ");
                schedulersList.push(scheduler);
            }
        }
        return new Scheduler();
    });

    beforeEach(module("reportApp")); // load reportApp angular application
    beforeEach(module('MockScheduler')); //overwrite scheduler factory with the MockScheduler
    // load the scheduler controler and inject resource with mock is needed
    beforeEach(inject(function($controller, Scheduler, $rootScope, applicationsService) {

        // creation d'un nouveau scope
        scope = $rootScope.$new();
        // creation du controller avec le nouveau scope
        schedulerCtrl = $controller("SchedulerCtrl", {
            $scope: scope,
            Scheduler: Scheduler,
            applicationsService: applicationsService
        });
    }));

    it('should exist', function() {
        expect( !! schedulerCtrl).toBe(true);
    });

    it('should have two schedulers', function() {
        expect(scope.schedulers.length).toEqual(2);
    });

    it('should succeed to use filter', function() {
        scope.mfilter = "D";
        scope.applyFilter();
        expect(scope.schedulers.length).toEqual(1);
    });

    it('should retrieve all elmt when click on refresh', function() {
        scope.mfilter = "";
        scope.refresh();
        expect(scope.schedulers.length).toEqual(2);
    });

    it('should add an element to the list when add a scheduler', function() {
        scope.scheduler = mScheduler;
        scope.addScheduler();
        expect(scope.schedulers.length).toEqual(3);
    })

})