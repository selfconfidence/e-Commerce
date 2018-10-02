app.service("adminService",function($http){
	this.showLoginName=function(){
		return $http.get("../login/admin.do");
	}
})
	
