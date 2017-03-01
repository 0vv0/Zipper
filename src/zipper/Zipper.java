package zipper;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

;

/**
 * Created by Oleksii.Sergiienko on 2/28/2017.
 */
@WebServlet(value = "/zip")
@MultipartConfig
public class Zipper extends HttpServlet {
    private final transient static Random random = new Random(LocalDateTime.now().getNano());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition",
                "attachment;filename=" + LocalDate.now().toString() + random.nextLong() + ".zip");
        ZipOutputStream zipOutputStream = new ZipOutputStream(resp.getOutputStream());
        String filename;
        for (Part part : req.getParts()) {
            if (part != null && part.getSize() > 0) {
                filename = getFileName(part);
                zip(filename, part.getInputStream(), zipOutputStream);
            }
        }
        zipOutputStream.flush();
        zipOutputStream.close();
    }

    private static void saveToFile(String id, Part part) throws IOException {
//        System.out.println(id);
        BufferedInputStream fileContent = new BufferedInputStream(part.getInputStream());
        FileOutputStream fw1 = new FileOutputStream(id);
        while (fileContent.available() > 0) {
            fw1.write(fileContent.read());
        }
        fw1.close();
        fileContent.close();

    }

    private static String getFileName(final Part part) {
        final String partHeader = part.getHeader("content-disposition");
        for (String content : partHeader.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(
                        content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return LocalDate.now().toString() + random.nextLong();
    }

    private static void zip(String name, InputStream inputStream, ZipOutputStream zipOutputStream) throws IOException {

        // a ZipEntry represents a file entry in the zip archive
        ZipEntry zipEntry = new ZipEntry(name);
        zipOutputStream.putNextEntry(zipEntry);

        byte[] buf = new byte[1024];
        int bytesRead;

        while ((bytesRead = inputStream.read(buf)) > 0) {
            zipOutputStream.write(buf, 0, bytesRead);
        }

        zipOutputStream.closeEntry();
    }
}
