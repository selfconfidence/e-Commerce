//服务层
app.service('contentService',function($http){
	    	
    this.findCateGoryId=function(cateGoryId){
    	return $http.get("content/findCategoryId.do?cateGoryId="+cateGoryId);
    }   	
});
