package com.subra.aem.rjs.pdf;

import com.itextpdf.html2pdf.attach.ITagWorker;
import com.itextpdf.html2pdf.attach.ProcessorContext;
import com.itextpdf.html2pdf.attach.impl.DefaultTagWorkerFactory;
import com.itextpdf.html2pdf.html.node.IElementNode;

// Defines all the custom tag workers we have for Girlscouts.
public class TagWorkerFactory extends DefaultTagWorkerFactory {

	@Override
	public ITagWorker getCustomTagWorker(IElementNode tag, ProcessorContext context) {

		if(tag.name().equals("img")){
			return new LocalImageTagWorker(tag, context);
		}
		if(tag.name().equals("new-page")){
			return new NewPageTagWorker(tag, context);
		}

		return null;
	}
}
