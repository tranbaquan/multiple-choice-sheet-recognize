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
import java.util.HashMap;

public class FormUtils {

    private static final String HEAD_PATH = "src/main/resources/default/html-head.txt";
    private static final String FORM_NAME = "form.html";
    private static final HashMap<Character, String> htmlEncodeChars = new HashMap<>();

    static {
        htmlEncodeChars.put('<', "&lt;");
        htmlEncodeChars.put('>', "&gt;");
        htmlEncodeChars.put('&', "&amp;");
        htmlEncodeChars.put('\"', "&quot;");
        htmlEncodeChars.put('\'', "&apos;");
        htmlEncodeChars.put('-', "&minus;");
        htmlEncodeChars.put('=', "&equals;");
        htmlEncodeChars.put('[', "&lsqb;");
        htmlEncodeChars.put(']', "&rsqb;");
        htmlEncodeChars.put('_', "&lowbar;");
        htmlEncodeChars.put('`', "&grave;");
        htmlEncodeChars.put('{', "&lcub;");
        htmlEncodeChars.put('|', "&verbar;");
        htmlEncodeChars.put('}', "&rcub;");
    }

    public void generateHtml(FormHeader formHeader, FormBody formBody, String desFolder) throws IOException {
        File d = new File(desFolder);
        if (!d.exists()) {
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
    }

    public void setQRCode(String srcFile, String desFolder, String image) throws IOException {
        File d = new File(desFolder);
        if (!d.exists()) {
            d.mkdirs();
        }
        StringBuilder document = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile), StandardCharsets.UTF_8));
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
        document.replace(index, index + 2, splits[splits.length - 2]);
        File file = new File(image);
        if (!file.exists()) {
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
    }


    public void setMultiQRCode(String srcFile, String desFolder, String qrFolder) throws IOException {
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


    public StringBuilder appendQrCode(StringBuilder data, String image) {
        int index = data.indexOf("%s");
        data.replace(index, index + 2, image);
        index = data.indexOf("%s");
        String[] splits = image.split("[/.\\\\]");
        String hex = splits[splits.length - 2];
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < hex.length()-1; i += 2) {
            char letter = (char) Integer.parseInt(hex.substring(i, i + 2), 16);
            if (htmlEncodeChars.get(letter) != null) {
                b.append(htmlEncodeChars.get(letter));
            } else {
                b.append(letter);
            }
        }
        data.replace(index, index + 2, b.toString());
        return data;
    }

    public void convertXhtmlToPdf(String srcFolder, String desFolder) throws IOException, DocumentException {

        File d = new File(desFolder);
        if (!d.exists()) {
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
    }

    public void convertXhtmlToOnePdf(String srcFolder, String desFolder) {
        try {
            File d = new File(desFolder);
            if (!d.exists()) {
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
            // create pdf document
            Document document = new Document(PageSize.A4);
            String fileName = "output.pdf";
            FileOutputStream fos = new FileOutputStream(new File(desFolder, fileName));
            PdfWriter writer = PdfWriter.getInstance(document, fos);
            document.open();
            // open file input stream
            for (File file : subFiles) {
                FileInputStream fis = new FileInputStream(file);
                // convert xhtml to pdf
                XMLWorkerHelper.getInstance().parseXHtml(writer, document, fis, Charset.forName("UTF-8"));
                document.newPage();
            }
            document.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }


    public void optConvert(String srcFile, String outputFile, String qrFolder) throws IOException, DocumentException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile), StandardCharsets.UTF_8));
        StringBuilder s = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            s.append(line);
            s.append("\n");
        }
        br.close();

        File src = new File(qrFolder);
        if (!src.exists()) {
            throw new FileNotFoundException("File not exists!");
        }
        File d = new File(outputFile);
        Document document = new Document(PageSize.A4);
        FileOutputStream fos = new FileOutputStream(d);
        PdfWriter writer = PdfWriter.getInstance(document, fos);
        document.open();

        File[] subFiles = src.listFiles();
        if (subFiles == null) {
            throw new FileNotFoundException("Folder is empty!");
        }
        StringBuilder data;
        for (File file : subFiles) {
            data = new StringBuilder(s);
            appendQrCode(data, file.getAbsolutePath());
            XMLWorkerHelper.getInstance().parseXHtml(writer, document, new StringReader(data.toString()));
            document.newPage();
        }
        document.close();

    }
}
