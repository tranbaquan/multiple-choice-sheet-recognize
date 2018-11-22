package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.supporter.form;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.logging.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;

public class FormUtils extends Logger {

    public static final String HEAD_PATH = "src/main/resources/default/html-head.txt";
    public static final String HEADER_PATH = "src/main/resources/default/html-header.txt";
    public static final String CONTENT_PATH = "src/main/resources/default/html-content.txt";
    public static final String FORM_NAME = "form.html";

    public static FormHeader getFormHeader(String file) {
        try {
            FormHeader formHeader = new FormHeader();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = br.readLine()) != null) {
                addToHeader(line, formHeader);
            }
            br.close();
            return formHeader;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("File header not found!", e);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Reading file error!", e);
        }
        return null;
    }

    public static FormBody getFormBody(String file) {
        try {
            FormBody formBody = new FormBody();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = br.readLine()) != null) {
                formBody.addCandidate(line);
            }
            br.close();
            return formBody;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void generateHtml(FormHeader formHeader, FormBody formBody, String desFolder) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(HEAD_PATH)));

            String formPath = desFolder  + "/" + FORM_NAME;

            FileOutputStream fos = new FileOutputStream(formPath);
            PrintWriter pw = new PrintWriter(fos);
            String line;
            while ((line = br.readLine()) != null) {
                pw.write(line);
                pw.write("\n");
            }
            br.close();

            br = new BufferedReader(new InputStreamReader(new FileInputStream(HEADER_PATH)));
            StringBuffer stringBuffer = new StringBuffer();
            while ((line = br.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append("\n");
            }
            br.close();
            pw.write(String.format(stringBuffer.toString(), formHeader.getHtml()));

            br = new BufferedReader(new InputStreamReader(new FileInputStream(CONTENT_PATH)));
            stringBuffer = new StringBuffer();
            while ((line = br.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append("\n");
            }
            pw.write(String.format(stringBuffer.toString(), formBody.getHtml()));

            pw.flush();
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addToHeader(String line, FormHeader formHeader) {
        String[] split = line.split(": ");
        if (split.length != 2) {

        }
        switch (split[0]) {
            case HeaderType.TITLE:
                formHeader.addTitle(split[1]);
                break;
            case HeaderType.LOGO:
                formHeader.setLogo(split[1]);
                break;
            case HeaderType.PAPER_NAME:
                formHeader.setPaperName(split[1]);
                break;
            case HeaderType.SUB_TITLE:
                formHeader.addSubTitle(split[1]);
                break;
        }
    }

    public static void setQRCode(String srcFile,String desFolder, String qrCode) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile)));
            StringBuilder document = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                document.append(line);
                document.append("\n");
            }
            br.close();

            int index = document.indexOf("%s");
            document.replace(index, index+2, qrCode);

            String dest = desFolder + "/" + System.currentTimeMillis() + ".html";

            PrintWriter pw = new PrintWriter(new FileOutputStream(dest));
            pw.write(document.toString());
            pw.flush();
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void convertXhtmlToPdf(String srcFolder, String desFolder) {
        try {
            File src = new File(srcFolder);
            if (!src.exists()) {
                throw new FileNotFoundException("File not exists!");
            }

            File[] subFiles = src.listFiles();

            if(subFiles == null) {
                throw new FileNotFoundException("Folder is empty!");
            }

            for (File file: subFiles) {
                // create pdf document
                Document document = new Document(PageSize.A4);
                // open file input stream
                FileInputStream fis = new FileInputStream(file);

                // convert name to .pdf
                StringBuffer fileName = new StringBuffer(file.getName());
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
