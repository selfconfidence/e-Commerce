var app=angular.module('pinyougou',[]);
//定义过滤器名称
app.filter('htmlFilter',['$sce',function($sce) {
	//一个过滤器做一件事
	return function(data){
		return $sce.trustAsHtml(data);
	}
}])