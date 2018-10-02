app.service('uploadService',function($http){
	
	this.upload=function(){
		//html5新特性为文件上传操作的对象
		var formData = new FormData();
		//  file就是指文件上传中指定的文件名称
		formData.append("file",file.files[0]);
		return $http({
			url:"../upload.do",
		    method:"POST",
		    //当作参数去传递
		    data:formData,
		    //AngJS默认是使用JSON数据传递的为了保证数据的完成性需要设计类型为未定义的进行传递
		    //这样浏览器会默认帮我们转化为multipart-from-date
		    headers:{"Content-Type":undefined},
		    //进行序列化到formData中操作.
		    transformRequest: angular.identity
		}
		)
	}
	
});