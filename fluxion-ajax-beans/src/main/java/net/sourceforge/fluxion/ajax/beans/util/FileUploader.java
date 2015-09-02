package net.sourceforge.fluxion.ajax.beans.util;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URL;
import java.io.IOException;
import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: davey Date: 08-Jan-2009 Time: 15:35:52 To
 * change this template use File | Settings | File Templates.
 */
public class FileUploader {

  public static URL handleFileUpload(
      HttpServletRequest httpServletRequest, String destinationDir)
      throws IOException {
    return handleFileUpload(httpServletRequest, new File(destinationDir));
  }

  public static URL handleFileUpload(
      HttpServletRequest httpServletRequest, File destinationDir)
      throws IOException {
    HttpSession session = httpServletRequest.getSession(false);
    // list of parsed accession numbers
    // create file upload factory and upload servlet
    FileItemFactory factory = new DiskFileItemFactory();
    ServletFileUpload upload = new ServletFileUpload(factory);
    FileUploadListener listener = new FileUploadListener();
    session.setAttribute("upload_listener", listener);
    upload.setProgressListener(listener);
    URL uploadedFileURL = null;

    try {
      List uploadedItems = upload.parseRequest(httpServletRequest);
      for (Object uploadedItem : uploadedItems) {
        FileItem fileItem = (FileItem) uploadedItem;
        if (!fileItem.isFormField()) {
          if (fileItem.getSize() > 0) {
            // get the fileItemName (full path)
            String fileItemName = fileItem.getName();

            // Ignore the path and get the filename
            String fileName = fileItemName.substring(
                fileItemName.lastIndexOf(File.separator) + 1,
                fileItemName.length());

            // Create new File objects
            if (!destinationDir.exists()) {
              destinationDir.mkdirs();
            }
            File uploadedFile = new File(destinationDir, fileName);
            if (!uploadedFile.exists()) {
              uploadedFile.createNewFile();
            }
            fileItem.write(uploadedFile);

            uploadedFileURL = uploadedFile.toURI().toURL();

            // update fileMap with stats for success
            httpServletRequest.getSession().setAttribute("file", uploadedFileURL.toString());
          }
        }
      }
      return uploadedFileURL;
    }
    catch (FileUploadException e) {
      throw new IOException(
          "A FileUploadException occurred whilst uploading a file");
    }
    catch (Exception e) {
      throw new IOException("An exception occurred whilst uploading a file");
    }
  }
}
