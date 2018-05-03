package cn.xian.vertxdemo.api;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import top.onceio.core.beans.ApiMethod;
import top.onceio.core.mvc.annocations.Api;

@Api
public class ResouceApi {

	@Api(value="/upload",method = ApiMethod.POST)
	public void upload(RoutingContext  event) {
		System.out.println("--> upload  ");
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
