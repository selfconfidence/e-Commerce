app.controller("adminController",function($scope,adminService){
	$scope.showAdminName=function(){
		adminService.showLoginName().success(
		  function(response){
			  $scope.adminName=response.adminName;
		  }		
		);
	}
	
	
}
		

)
	
