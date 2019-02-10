package servlet;

import edu.rosehulman.bd.annotations.RequestMapping;
import edu.rosehulman.bd.annotations.RootMapping;
import edu.rosehulman.bd.protocol.api.IHttpRequest;
import edu.rosehulman.bd.protocol.api.IHttpResponse;
import edu.rosehulman.bd.protocol.api.Protocol;
import edu.rosehulman.bd.servlet.api.Servlet;

@RootMapping(url = "/TestServlet")
public class TestServlet extends Servlet {

	@Override
	public void init() {
		
	}

	@Override
	public void destroy() {
		
	}

	@Override
	public void defaultHandler(IHttpRequest req, IHttpResponse res) {
		res.setVersion(Protocol.VERSION);
		res.setStatus(404);
		res.setPhrase(Protocol.NOT_FOUND_TEXT);
	}
	
	@RequestMapping(method = "GET", url = "/")
	public void handleGet(IHttpRequest req, IHttpResponse res) {
		res.setStatus(200);
		res.setPhrase(Protocol.OK_TEXT);
		String body = "<h1> hello there </h1>";
		res.setBody(body);
	}
	
	@RequestMapping(method = "GET", url = "/dog/{dogId}")
	public void handleGetDog(IHttpRequest req, IHttpResponse res) {
		res.setStatus(200);
		res.setPhrase(Protocol.OK_TEXT);
		String body = "<h1> hello there </h1> + <h2> you are on dog #" + req.getHeader("dogId") + "'s page";
		res.setBody(body);
	}
	
	@RequestMapping(method = "POST", url = "/") 
	public void handlePost(IHttpRequest req, IHttpResponse res) {
		res.setStatus(200);
		res.setPhrase(Protocol.OK_TEXT);
		String body = "<h1> Posted file </h1>";
		body = body + new String(req.getBody());
		res.setBody(body);
	}
	
	@RequestMapping(method = "DELETE", url = "/") 
	public void handleDelete(IHttpRequest req, IHttpResponse res) {
		res.setStatus(200);
		res.setPhrase(Protocol.OK_TEXT);
		String body = "<h1> Deleting File </h1>";
		body += "<h2>" + req.getUri() + "</h2>";
		res.setBody(body);
	}
	
	@RequestMapping(method = "PUT", url = "/") 
	public void handlePut(IHttpRequest req, IHttpResponse res) {
		res.setStatus(200);
		res.setPhrase(Protocol.OK_TEXT);
		String body = "<h1> Putting File </h1>";
		res.setBody(body);
	}
	
	@RequestMapping(method = "HEAD", url = "/") 
	public void handleHead(IHttpRequest req, IHttpResponse res) {
		res.setStatus(200);
		res.setPhrase(Protocol.OK_TEXT);
		String body = "<h1> Processed HEAD req </h1>";
		res.setBody(body);
	}
}
