package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.supporter.form;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormUtils {

    private static final String HEAD_PATH = "src/main/resources/default/html-head.txt";
    private static final String FORM_NAME = "form.html";

    public static void generateHtml(FormHeader formHeader, FormBody formBody, String desFolder) {
        try {
            File d = new File(desFolder);
            if(!d.exists()) {
                d.mkdirs();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(HEAD_PATH)));

            String formPath = desFolder + "/" + FORM_NAME;

            FileOutputStream fos = new FileOutputStream(formPath);
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                pw.write(line);
                pw.write("\n");
            }
            br.close();
            pw.write(formHeader.getHtml());
            pw.write(formBody.getHtml());
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setQRCode(String srcFile, String desFolder, String image) {
        try {
            File d = new File(desFolder);
            if(!d.exists()) {
                d.mkdirs();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile), StandardCharsets.UTF_8));
            StringBuilder document = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                document.append(line);
                document.append("\n");
            }
            br.close();

            int index = document.indexOf("%s");
            document.replace(index, index + 2, image);
            index = document.indexOf("%s");
            String[] splits = image.split("[/.\\\\]");
            document.replace(index,index+2, splits[splits.length-2]);
            File file = new File(image);
            if(!file.exists()) {
                throw new FileNotFoundException("qr code path error!");
            }
            String id = file.getName().substring(0, file.getName().indexOf("."));
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDateTime now = LocalDateTime.now();
            String dest = desFolder + File.separator + id + "_" + dtf.format(now) + ".html";
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(dest), StandardCharsets.UTF_8));
            pw.write(document.toString());
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void convertXhtmlToPdf(String srcFolder, String desFolder) {
        try {
            File d = new File(desFolder);
            if(!d.exists()) {
                d.mkdirs();
            }
            File src = new File(srcFolder);
            if (!src.exists()) {
                throw new FileNotFoundException("File not exists!");
            }

            File[] subFiles = src.listFiles();

            if (subFiles == null) {
                throw new FileNotFoundException("Folder is empty!");
            }

            for (File file : subFiles) {
                // create pdf document
                Document document = new Document(PageSize.A4);
                // open file input stream
                FileInputStream fis = new FileInputStream(file);

                // convert name to .pdf
                StringBuilder fileName = new StringBuilder(file.getName());
                fileName.delete(fileName.lastIndexOf("."), fileName.length());
                fileName.append(".pdf");

                // open file output stream
                FileOutputStream fos = new FileOutputStream(new File(desFolder, fileName.toString()));

                // create writer
                PdfWriter writer = PdfWriter.getInstance(document, fos);

                // convert xhtml to pdf
                document.open();
                XMLWorkerHelper.getInstance().parseXHtml(writer, document, fis, Charset.forName("UTF-8"));
                document.close();
            }
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void setMultiQRCode(String srcFile, String desFolder, String qrFolder) {
        try {
            File src = new File(qrFolder);
            if (!src.exists()) {
                throw new FileNotFoundException("File not exists!");
            }

            File[] subFiles = src.listFiles();
            if (subFiles == null) {
                throw new FileNotFoundException("Folder is empty!");
            }

            for (File file : subFiles) {
                setQRCode(srcFile, desFolder, file.getAbsolutePath());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
