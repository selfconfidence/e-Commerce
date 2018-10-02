app.controller('userFeedBackController',function($scope,userFeedBackService){
	$scope.initUserName=function(){
		userFeedBackService.findUserName().success(
		 function(response){
			$scope.userId= JSON.parse(response);
		 }		
		);
	}
	$scope.findAll=function(){
		userFeedBackService.findAll().success(
		      function(response){
		    	  $scope.list=response
		      }		
		);
	}
	
	$scope.saveContent=function(){
		var object;
		  if($scope.entity.self_motionId==''|| $scope.entity.self_motionId==null){
			object=  userFeedBackService.saveFeedBack($scope.entity);
			alert("添加");
		  }else{
			  alert('更新')
			 object= userFeedBackService.update($scope.entity);
		  }
		
		  object.success(
		  function(response){
			  if(response.flag){
				  $scope.findAll();
			  }else{
				  alert(response.messAge);
			  }
		  }		
		);
	}
	
	$scope.deleteFeedBack=function(feedBackId){
		userFeedBackService.deleteFeedBack(feedBackId).success(
		       function(response){
		    	   if(response.flag){
		    		   $scope.findAll();
		    	   }else{
		    		   alert($scope.messAge);
		    	   }
		       }		
		);
	}
	$scope.findOne=function(id){
		userFeedBackService.findOne(id).success(
		     function(response){
		    	 $scope.entity=response;
		     }		
		);
	}
	
});