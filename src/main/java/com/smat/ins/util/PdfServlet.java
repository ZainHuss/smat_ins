package com.smat.ins.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.smat.ins.model.entity.CabinetLocation;
import com.smat.ins.model.service.CabinetLocationService;




@WebServlet("/pdfs/*")
public class PdfServlet extends HttpServlet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -8206284190583246914L;

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws FileNotFoundException, IOException{
		try {
			ServletContext context = getServletContext();
			WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);
			if (ctx != null) {
				CabinetLocationService cabinetLocationService = ctx.getBean("cabinetLocationService",
						CabinetLocationService.class);
				CabinetLocation cabinetLocation = cabinetLocationService.getDefaultLocation();
				String defaultLocation="";
				String os = System.getProperty("os.name").toLowerCase();
				if (os.contains("win")) {
					defaultLocation = cabinetLocation.getWinLocation();
				} else if (os.contains("osx")) {
					defaultLocation = cabinetLocation.getMacLocation();
				} else if (os.contains("nix") || os.contains("aix") || os.contains("nux")) {
					defaultLocation = cabinetLocation.getLinuxLocation();
				}
				
				File f = new File(defaultLocation + File.separator + req.getPathInfo());
				/////////////////////////////////////////////////////////////////////
				//// BIG WARNING: ////
				//// Normalize the path of the file and check if the user can ////
				//// legitimately access it, or you created a BIG security ////
				//// hole! If possible enhance it with system-level security, ////
				//// i.e. the user running the application server can access ////
				//// files only in the basePath and application server dirs. ////
				/////////////////////////////////////////////////////////////////////
				if (f.exists()) {
					OutputStream out = res.getOutputStream();
					// also set response headers for correct content type
					// or even cache headers, if you so desire
					byte[] buf = new byte[1024];
					int r;
					try (FileInputStream fis = new FileInputStream(f)) {
						while ((r = fis.read(buf)) >= 0) {
							out.write(buf, 0, r);
						}
					}
				} else
					res.sendError(HttpServletResponse.SC_NOT_FOUND);
			} else
				res.sendError(HttpServletResponse.SC_NOT_FOUND);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
