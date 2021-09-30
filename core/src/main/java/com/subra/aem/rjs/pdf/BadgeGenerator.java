package com.subra.aem.rjs.pdf;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.attach.ITagWorkerFactory;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.woff2.Woff2Converter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;
import com.subra.aem.rjs.pdf.servlets.PDFGeneratorServlet;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BadgeGenerator {

	public static final String FONT_LOCATION = "/etc/designs/gsusa/fonts/trefoilsans-regular.woff2";
	public static final String BOLD_FONT_LOCATION = "/etc/designs/gsusa/fonts/trefoilsans-bold.woff2";

	private String badgeHtml;
	private OutputStream outputStream;

	public BadgeGenerator(String html, OutputStream output){
		this.badgeHtml = html;
		this.outputStream = output;
	}

	public Document generatePdf() throws IOException {

		WriterProperties writerProperties = new WriterProperties();

		PdfWriter pdfWriter = new PdfWriter(outputStream, writerProperties);

		PdfDocument pdfDoc = new PdfDocument(pdfWriter);

		//Set meta tags
		PdfDocumentInfo pdfMetaData = pdfDoc.getDocumentInfo();
		pdfMetaData.setAuthor("Random Author");
		pdfMetaData.addCreationDate();
		pdfMetaData.setKeywords("Test PDF");
		pdfMetaData.setSubject("Badge Explorer");
		pdfMetaData.setTitle("Badge Explorer");

		// pdf conversion
		ConverterProperties props = new ConverterProperties();

		// Setup custom tagworker factory for pulling images straight from the DAM.
		ITagWorkerFactory tagWorkerFactory = new TagWorkerFactory();

		ResourceResolver resourceResolver = PDFGeneratorServlet.resolverLocal.get();

		props.setTagWorkerFactory(tagWorkerFactory);

		//FontProvider fontFactory = new DefaultFontProvider(true, true, true);
		//fontFactory.addFont(getFontProgram(FONT_LOCATION, resourceResolver));
		//fontFactory.addFont(getFontProgram(BOLD_FONT_LOCATION, resourceResolver));


		//FontSet fontSet = new FontSet();
		//fontSet.addFont(getFontData(FONT_LOCATION, resourceResolver), null, "Trefoil Sans Web");
		//fontSet.addFont(getFontData(BOLD_FONT_LOCATION, resourceResolver), null, "Trefoil Sans Web Bold");
		FontSet fontSet = new FontSet();
		//fontSet.addFont(getFontData(FONT_LOCATION, resourceResolver), null, "Trefoil Sans Web");
		//fontSet.addFont(getFontData(BOLD_FONT_LOCATION, resourceResolver), null, "Trefoil Sans Web Bold");


		FontProvider fontFactory = new FontProvider(fontSet);



		props.setFontProvider(fontFactory);
		props.setImmediateFlush(false);
		Document doc = HtmlConverter.convertToDocument(new ByteArrayInputStream(badgeHtml.getBytes(StandardCharsets.UTF_8)) , pdfDoc, props);

		doc.close();

		return doc;
	}

	private static byte[] getFontData(String location, ResourceResolver resourceResolver) throws IOException {


		Resource fontResource = resourceResolver.resolve(location);

		byte[] fontData;
		try {
			fontData = IOUtils.toByteArray(fontResource.adaptTo(InputStream.class));
		} catch (NullPointerException | IOException e) {
			fontData = new byte[0];
		}
		return Woff2Converter.convert(fontData);

	}

	private static FontProgram getFontProgram(String location, ResourceResolver resourceResolver) throws IOException {

		Resource fontResource = resourceResolver.resolve(FONT_LOCATION);

		byte[] fontData;
		try {
			fontData = IOUtils.toByteArray(fontResource.adaptTo(InputStream.class));
		} catch (IOException e) {
			fontData = new byte[0];
		}

		return FontProgramFactory.createFont(fontData);

	}
}
