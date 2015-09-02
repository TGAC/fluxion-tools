package net.sourceforge.fluxion.ajax.beans;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.beans.util.FileUploadListener;

import javax.servlet.http.HttpSession;

/**
 * An ajaxified bean for use with the fluxion-ajax framework that provides
 * functionality for monitoring the status of a file upload to the server.
 *
 * @author Rob Davey
 */
@Ajaxified
public class FileUploadProgressBean {
  public JSONObject checkUploadStatus(HttpSession session, JSONObject json) {
    FileUploadListener listener = (FileUploadListener)session.getAttribute("upload_listener");

    StringBuffer str = new StringBuffer();
    long bytesRead = 0, contentLength = 0;
    if (listener == null) {
      System.out.println("Null listener");
      return new JSONObject();
    }
    else {
      // Get the meta information
      bytesRead = listener.getBytesRead();
      contentLength = listener.getContentLength();
    }
    str.append("'bytes_read':'").append(bytesRead).append("',");
    str.append("'content_length':'").append(contentLength).append("',");

    return JSONObject.fromObject("{ " + str.toString() + " }");
  }
}
