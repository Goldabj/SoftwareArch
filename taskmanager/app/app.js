//Define an angular module for our app
var app = angular.module('myApp', []);

app.controller('tasksController', function($scope, $http) {
  getTask(); // Load all available tasks 
  function getTask(){  
    // TODO: GET 
  };
  $scope.addTask = function (task) {
    // TODO: POST
  };
  $scope.deleteTask = function (task) {
    if(confirm("Are you sure to delete this line?")){
      // TODO: DELETE
    }
  };
  $scope.toggleStatus = function(item, status, task) {
    if(status=='2'){status='0';}else{status='2';}
      // TODO: PUT
  };
  $scope.updateTask = function(item, status, task) {
    // TODO: PUT
  }

});
