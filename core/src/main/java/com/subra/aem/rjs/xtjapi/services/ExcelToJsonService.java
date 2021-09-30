package com.subra.aem.rjs.xtjapi.services;

import com.day.cq.dam.api.Asset;

import java.io.InputStream;

/**
 * @author Raghava Joijode
 */
public interface ExcelToJsonService {

    String getJsonFromExcel(String excelPath);

    String getJsonFromExcel(Asset excelAsset);

    String getJsonFromExcel(InputStream excelInputStream);

    Asset getJsonAssetFromString(String jsonString);

    Asset getJsonAssetFromExcel(String excelPath);

    Asset getJsonAssetFromExcel(Asset excelAsset);

    Asset getJsonAssetFromExcel(InputStream excelInputStream);

}
