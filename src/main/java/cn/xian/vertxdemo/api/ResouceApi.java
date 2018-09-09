package cn.xian.vertxdemo.api;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import top.onceio.core.beans.ApiMethod;
import top.onceio.core.mvc.annocations.Api;

@Api
public class ResouceApi {

	@Api(value="/upload",method = ApiMethod.POST)
	public void upload(RoutingContext  ctx) {
		HttpServerRequest req = ctx.request();
		req.setExpectMultipart(true);
		req.uploadHandler(upload -> {
			upload.exceptionHandler(cause -> {
				req.response().setChunked(false).end("Upload failed");
			});

			upload.endHandler(v -> {
				req.response().setChunked(false).end("Successfully uploaded to " + upload.filename());
			});
			upload.streamToFileSystem(upload.filename());
		});
	}
	
	@Api(value="/s/*",method = ApiMethod.GET)
	public void staticResource(HttpServerRequest req) {
		  String file = "";
		  if (req.path().equals("/")) {
		    file = "index.html";
		  } else if (!req.path().contains("..")) {
			  
		    file = req.path().replaceFirst("/", "");
		  }
		  req.response().sendFile(file);
	}

	@Api(value="/*",method = ApiMethod.OPTIONS)
	public void options(HttpServerRequest req) {
		req.response().setStatusCode(204);
		req.response().headers().set("Access-Control-Allow-Origin", "http://localhost:8080");
		req.response().headers().set("Access-Control-Allow-Methods", "GET,POST,PUT,PATCH,HEAD,OPTIONS,TRACE");
		req.response().headers().set("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
		req.response().end();
	}
}
