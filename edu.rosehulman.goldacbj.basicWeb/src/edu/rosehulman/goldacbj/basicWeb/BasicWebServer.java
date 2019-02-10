package edu.rosehulman.goldacbj.basicWeb;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class BasicWebServer
 */
@WebServlet("/BasicWebServer")
public class BasicWebServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private FileDao fileDao;

	@Override
	public void init() {
		String dataDirPath = this.getServletContext().getRealPath("/data");
		this.fileDao = new FileDao(dataDirPath + "/file.txt");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		getServletContext().log("perfroming GET");
		if (!fileDao.exists()) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("text/html");
		response.getWriter().write(fileDao.getContent());
		response.setDateHeader("Last-Modified", fileDao.lastModified());
		response.getWriter().flush();
		response.getWriter().close();
	}

	@Override
	protected void doHead(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!fileDao.exists()) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.setDateHeader("Last-Modified", fileDao.lastModified());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String prevContent = fileDao.getContent();
		String newContent = "";
		String incoming = "";
		while ((incoming = request.getReader().readLine()) != null) {
			newContent += incoming;
		}

		String totalContent = prevContent + newContent;
		boolean written = fileDao.write(totalContent);

		if (written) {
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(totalContent);
			response.getWriter().flush();
			response.getWriter().close();
			return;
		} // else

		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return;
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (fileDao.exists()) {
			boolean deleted = fileDao.delete();
			if (deleted) {
				resp.setStatus(HttpServletResponse.SC_OK);
				return;
			} else {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
		}
		resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		return;
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String newContent = "";
		String incoming = "";
		while ((incoming = req.getReader().readLine()) != null) {
			newContent += incoming;
		}

		boolean written = fileDao.write(newContent);

		if (written) {
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().write(newContent);
			resp.getWriter().flush();
			resp.getWriter().close();
			return;
		} // else

		resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return;
	}

}
