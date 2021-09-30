package com.subra.aem.rjs.examples.documentlibrary.services;

import org.apache.sling.api.resource.Resource;
import org.json.JSONArray;
import org.json.JSONException;

public interface DocumentLibraryService {
    JSONArray extractAssetList(Resource imageFolder) throws JSONException;
}
