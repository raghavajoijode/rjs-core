package com.subra.aem.rjs.core.component.base;

import com.adobe.cq.export.json.ComponentExporter;

public interface SubraComponent extends ComponentExporter {

	boolean isEmpty();

	@Override
	default String getExportedType() {
		throw new UnsupportedOperationException();
	}

}
