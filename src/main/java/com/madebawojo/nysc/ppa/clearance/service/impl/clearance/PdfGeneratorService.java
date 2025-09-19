package com.madebawojo.nysc.ppa.clearance.service.impl.clearance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class PdfGeneratorService {

    private final SpringTemplateEngine templateEngine;

    public byte[] generatePdfFromHtml(String templateName, Context context) throws Exception {
        String processedHtml = templateEngine.process(templateName, context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(processedHtml);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        }
    }
}
