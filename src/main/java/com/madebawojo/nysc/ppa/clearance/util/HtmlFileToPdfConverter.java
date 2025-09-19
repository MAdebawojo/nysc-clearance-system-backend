//package com.madebawojo.nysc.ppa.clearance.util;
//
//import com.itextpdf.html2pdf.HtmlConverter;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.io.IOException;
//
//@Service
//public class HtmlFileToPdfConverter {
//    public static void main(String[] args) {
//        String inputHtmlPath = "input.html"; // Path to your HTML file
//        String outputPath = "output.pdf";
//
//        try {
//            HtmlConverter.convertToPdf(new File(inputHtmlPath), new File(outputPath));
//            System.out.println("PDF created successfully from HTML file.");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}