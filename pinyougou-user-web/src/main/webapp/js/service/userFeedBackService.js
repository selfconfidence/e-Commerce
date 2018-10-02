app.service('userFeedBackService',function($http){
	this.saveFeedBack=function(feedBack){
	return	$http.post('../feedBack/save.do',feedBack);
	}
	this.findUserName=function(){
		return $http.get('../feedBack/initName.do');
	}
	this.findAll=function(){
		return $http.get('../feedBack/findAll.do');
	}
	this.deleteFeedBack=function(self_motionId){
		return $http.get('../feedBack/delete.do?self_motionId='+self_motionId);
	}
	this.findOne=function(self_motionId){
		return $http.get('../feedBack/findOne.do?self_motionId='+self_motionId);
	}
	this.update=function(entity){
		return $http.post('../feedBack/update.do',entity);
	}
	
});