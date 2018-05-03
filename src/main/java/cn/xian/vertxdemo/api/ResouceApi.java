package cn.xian.vertxdemo.api;

import io.vertx.core.http.HttpServerRequest;
import top.onceio.core.beans.ApiMethod;
import top.onceio.core.mvc.annocations.Api;

@Api
public class ResouceApi {

	@Api(value="/upload",method = ApiMethod.POST)
	public void upload(HttpServerRequest req) {
		req.setExpectMultipart(true);
		req.uploadHandler(upload -> {
			upload.streamToFileSystem("tmp/"+upload.filename()+"."+upload.contentType());
		});
	}
	
	@Api(value="/s/*",method = ApiMethod.POST)
	public void staticResource(HttpServerRequest req) {
		  String file = "";
		  if (req.path().equals("/")) {
		    file = "index.html";
		  } else if (!req.path().contains("..")) {
		    file = req.path();
		  }
		  req.response().sendFile("s/" + file);
	}
}
