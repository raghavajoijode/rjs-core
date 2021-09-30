package com.subra.aem.rjs.pdf;

import com.itextpdf.html2pdf.attach.ITagWorker;
import com.itextpdf.html2pdf.attach.ProcessorContext;
import com.itextpdf.html2pdf.attach.impl.layout.HtmlPageBreak;
import com.itextpdf.html2pdf.attach.impl.layout.HtmlPageBreakType;
import com.itextpdf.html2pdf.html.node.IElementNode;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.renderer.AreaBreakRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;

public class NewPageTagWorker implements ITagWorker {

	public NewPageTagWorker(IElementNode element, ProcessorContext context) { }

	@Override
	public IPropertyContainer getElementResult() {
		return new HtmlPageBreak(HtmlPageBreakType.ALWAYS){

			@Override
			protected IRenderer makeNewRenderer() {
				return new AreaBreakRenderer(this){

					@Override
					public void draw(DrawContext drawContext) {
						// Override the exception that doesn't do anything!
						//throw new UnsupportedOperationException();
					}
				};
			}
		};
	}

	@Override
	public void processEnd(IElementNode iElementNode, ProcessorContext processorContext) {}

	@Override
	public boolean processContent(String s, ProcessorContext processorContext) {
		return true;
	}

	@Override
	public boolean processTagChild(ITagWorker iTagWorker, ProcessorContext processorContext) {
		return true;
	}
}
