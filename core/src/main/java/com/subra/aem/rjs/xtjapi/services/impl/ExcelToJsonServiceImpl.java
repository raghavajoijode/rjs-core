package com.subra.aem.rjs.xtjapi.services.impl;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subra.aem.rjs.core.UserMapperService;
import com.subra.aem.rjs.xtjapi.services.ExcelToJsonService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.constants.HttpType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Raghava Joijode
 */
@Component(immediate = true, service = ExcelToJsonService.class)
public class ExcelToJsonServiceImpl implements ExcelToJsonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelToJsonServiceImpl.class);
    private static final String FILENAME = "product_simple";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public String getJsonFromExcel(String excelPath) {
        Asset excelAsset = null;
        try (ResourceResolver resourceResolver = getResourceResolver()) {
            excelAsset = resourceResolver.getResource(excelPath).adaptTo(Asset.class);
        } catch (LoginException e) {
            LOGGER.error("Exception occurred initializing resourceResolver");
        }
        return getJsonFromExcel(excelAsset);
    }

    @Override
    public String getJsonFromExcel(Asset excelAsset) {
        return excelAsset != null ? getJsonFromExcel(excelAsset.getOriginal().getStream()) : null;
    }

    @Override
    public String getJsonFromExcel(InputStream excelInputStream) {
        return convertExcelInputStreamToJson(excelInputStream);
    }

    @Override
    public Asset getJsonAssetFromString(String jsonString) {
        return convertStringToJsonAsset(jsonString, FILENAME, HttpType.MEDIA_TYPE_JSON.value());
    }

    @Override
    public Asset getJsonAssetFromExcel(String excelPath) {
        return convertStringToJsonAsset(getJsonFromExcel(excelPath), FILENAME, HttpType.MEDIA_TYPE_JSON.value());
    }

    @Override
    public Asset getJsonAssetFromExcel(Asset excelAsset) {
        return convertStringToJsonAsset(getJsonFromExcel(excelAsset), excelAsset.getName(),
                HttpType.MEDIA_TYPE_JSON.value());
    }

    @Override
    public Asset getJsonAssetFromExcel(InputStream excelInputStream) {
        return convertStringToJsonAsset(getJsonFromExcel(excelInputStream), FILENAME,
                HttpType.MEDIA_TYPE_JSON.value());
    }

    private String convertExcelInputStreamToJson(InputStream excelInputStream) {
        try (Workbook workbook = new XSSFWorkbook(excelInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            Row firstRow = rowIterator.hasNext() ? rowIterator.next() : null;
            Map<String, Object> finalMap = new HashMap<>();
            if (firstRow != null) {
                while (rowIterator.hasNext()) {
                    Row nextRow = rowIterator.next();
                    Iterator<Cell> firstRowCellIterator = firstRow.cellIterator();
                    Iterator<Cell> nextRowCellIterator = nextRow.cellIterator();
                    Map<String, String> values = new HashMap<>();
                    while (firstRowCellIterator.hasNext()) {
                        values.put(getCellValue(firstRowCellIterator.next()), getCellValue(nextRowCellIterator.next()));
                    }
                    finalMap.put(getCellValue(nextRow.getCell(0)), values);
                }
            }
            return new ObjectMapper().writeValueAsString(finalMap);
        } catch (IOException e) {
            LOGGER.error("Exception occurred at ExcelToJsonService -> {}", e.getMessage());
        } finally {
            try {
                excelInputStream.close();
            } catch (IOException e) {
                LOGGER.error("Exception occurred while closing excelInputStream {}, at ExcelToJsonService -> {}",
                        excelInputStream, e.getMessage());
            }
        }
        return null;
    }

    private String getCellValue(Cell cell) {
        switch (cell.getCellTypeEnum()) {
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue()).replace(".0", "");
            default:
                return cell.getStringCellValue();
        }
    }

    private Asset convertStringToJsonAsset(String jsonString, String fileName, String mimeType) {
        AssetManager assetManager = null;
        try (ResourceResolver resourceResolver = getResourceResolver()) {
            assetManager = resourceResolver.adaptTo(AssetManager.class);
        } catch (LoginException e) {
            LOGGER.error("Exception occurred initializing resourceResolver");
        }
        if (assetManager != null) {
            String filePath = "/content/dam/core/api/xtj/out/json/" + fileName + "." + StringUtils.substringAfterLast(mimeType, "/");
            return assetManager.createAsset(filePath, new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8)), mimeType, true);
        }
        return null;
    }

    protected ResourceResolver getResourceResolver() throws LoginException {
        return resourceResolverFactory.getServiceResourceResolver(Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, UserMapperService.ADMIN_SERVICE.value()));
    }

}
