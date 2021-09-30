package com.subra.aem.rjs.examples.documentlibrary.services.impl;

import com.adobe.granite.security.user.util.AuthorizableUtil;
import com.day.cq.commons.jcr.JcrConstants;
import com.subra.aem.rjs.core.utils.RJSResourceUtils;
import com.subra.aem.rjs.examples.documentlibrary.beans.Asset;
import com.subra.aem.rjs.examples.documentlibrary.services.DocumentLibraryService;
import org.apache.sling.api.resource.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@Component(service = DocumentLibraryService.class)
@ServiceDescription("WorkFlowReportExportService Service Configuration")
public class DocumentLibraryServiceImpl implements DocumentLibraryService {

    private static final String TEAM = "Team";
    private static final String EXCEL_ICON = "/etc/designs/intranet/catalog_images/excel-icon.png";
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference
    private ResourceResolverFactory resolverFactory;

    private static String dateFormatting(String unformattedDate) throws ParseException {
        String s1 = unformattedDate.replace('T', ' ');
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date dt1 = df1.parse(s1);
        DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        return df2.format(dt1);
    }

    private Asset createFile(Resource damResource) {
        String date = "14/06/2016 06:34 AM";
        ValueMap properties = damResource.getChild("jcr:content/metadata").getValueMap();
        ValueMap prop = damResource.getChild(JcrConstants.JCR_CONTENT).getValueMap();
        String type = properties.get("dc:format", String.class);

        String imageType = getImageType(type);
        String modifiedById = prop.get(JcrConstants.JCR_LASTMODIFIED, String.class);
        String modifiedByName = TEAM;
        if (modifiedById != null) {
            modifiedByName = getFormattedName(modifiedById);
        }

        String modifiedDate = prop.get(JcrConstants.JCR_LASTMODIFIED, "14/06/2016 06:34 am");

        try {
            date = dateFormatting(modifiedDate);
        } catch (ParseException e) {
            log.error(e.getMessage());
        }

        Asset asset = new Asset();
        asset.setImageType(imageType);
        asset.setModifiedBy(modifiedByName);
        asset.setModifiedDate(date);
        asset.setName(damResource.getName());
        asset.setPath(damResource.getPath());
        asset.setTitle(damResource.getName());
        asset.setType(type);
        return asset;
    }

    private Asset createFolder(Resource damResource) {
        String date = "14/06/2016 06:34 am";
        String type = "folder";
        String imageType = "/etc/designs/intranet/catalog_images/folder-icon-512x512.png";
        Node n = damResource.adaptTo(Node.class);
        Node n1;
        if (damResource.getChild(JcrConstants.JCR_CONTENT) != null) {
            n1 = damResource.getChild(JcrConstants.JCR_CONTENT).adaptTo(Node.class);
        } else {
            n1 = n;
        }
        String modifiedDate = null;
        try {
            modifiedDate = n.hasProperty(JcrConstants.JCR_CREATED) ? n.getProperty(JcrConstants.JCR_CREATED).getString()
                    : "";
        } catch (RepositoryException e) {
            log.error(e.getMessage());
        }
        try {
            if (modifiedDate != null)
                date = dateFormatting(modifiedDate);

        } catch (ParseException e) {
            log.error(e.getMessage());
        }
        String createdBy = null;
        try {
            createdBy = n.hasProperty(JcrConstants.JCR_CREATED_BY)
                    ? n.getProperty(JcrConstants.JCR_CREATED_BY).getString()
                    : TEAM;
        } catch (RepositoryException e) {
            log.error(e.getMessage());
        }
        String createdByName = createdBy;
        if (createdBy != null || TEAM.equalsIgnoreCase(createdBy)) {
            createdByName = getFormattedName(createdBy);
        }
        String title = null;
        try {
            title = n1.hasProperty(JcrConstants.JCR_TITLE) ? n1.getProperty(JcrConstants.JCR_TITLE).getString()
                    : n.getName();
        } catch (RepositoryException e) {
            log.error(e.getMessage());
        }
        Asset folder = new Asset();
        folder.setImageType(imageType);
        folder.setModifiedBy(createdByName);
        folder.setModifiedDate(date);
        folder.setName(damResource.getName());
        folder.setPath(damResource.getPath());
        folder.setTitle(title);
        folder.setType(type);
        return folder;
    }

    private String getFormattedName(String createdBy) {
        try (ResourceResolver resourceResolver = RJSResourceUtils.getAdminServiceResourceResolver(resolverFactory)) {
            return AuthorizableUtil.getFormattedName(resourceResolver, createdBy);
        } catch (LoginException e) {
            log.error("Error...", e);
        }
        return createdBy;
    }

    @Override
    public JSONArray extractAssetList(Resource imageFolder) throws JSONException {
        List<Asset> folderList = new ArrayList<>();
        List<Asset> fileList = new ArrayList<>();
        for (Resource damResource : imageFolder.getChildren()) {
            if (damResource.getResourceType().equals("dam:Asset")) {
                Asset file = createFile(damResource);
                fileList.add(file);

            } else if (damResource.getResourceType().equals("sling:OrderedFolder")
                    || damResource.getResourceType().equals("sling:Folder")) {
                Asset folder = createFolder(damResource);
                folderList.add(folder);
            }
        }

        JSONArray assetList = new JSONArray();
        Collections.sort(folderList);

        for (Asset asset : folderList) {
            log.info(" folder new  name  {}  title {}", asset.getName(), asset.getTitle());
            JSONObject obj = new JSONObject();
            obj.put("imageType", asset.getImageType());
            obj.put("modifiedBy", asset.getModifiedBy());
            obj.put("modifiedDate", asset.getModifiedDate());
            obj.put("name", asset.getName());
            obj.put("path", asset.getPath());
            obj.put("title", asset.getTitle());
            obj.put("type", asset.getType());
            assetList.put(obj);

        }
        Collections.sort(fileList);
        for (Asset asset : fileList) {
            JSONObject obj = new JSONObject();
            obj.put("imageType", asset.getImageType());
            obj.put("modifiedBy", asset.getModifiedBy());
            obj.put("modifiedDate", asset.getModifiedDate());
            obj.put("name", asset.getName());
            obj.put("path", asset.getPath());
            obj.put("title", asset.getTitle());
            obj.put("type", asset.getType());
            assetList.put(obj);
        }
        return assetList;
    }

    private String getImageType(String type) {
        String imageType = null;
        switch (type) {
            // pdf
            case "application/pdf":
                imageType = "/etc/designs/intranet/catalog_images/pdf-icon.png";
                break;

            // Images
            case "image/jpeg":
                imageType = "/etc/designs/intranet/catalog_images/JPG.gif";
                break;

            case "image/gif":
                imageType = "/etc/designs/intranet/catalog_images/image-icon.png";
                break;

            case "image/png":
                imageType = "/etc/designs/intranet/catalog_images/image-icon.png";
                break;

            // excel
            case "application/vnd.ms-excel.sheet.binary.macroenabled.12":
                imageType = EXCEL_ICON;
                break;

            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                imageType = EXCEL_ICON;
                break;

            case "application/vnd.ms-excel":
                imageType = EXCEL_ICON;
                break;

            case "application/vnd.ms-excel.sheet.macroenabled.12":
                imageType = EXCEL_ICON;
                break;

            // word document
            case "application/msword":
                imageType = "/etc/designs/intranet/catalog_images/word-icon.png";
                break;

            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                imageType = "/etc/designs/intranet/catalog_images/word-icon.png";
                break;

            // powerpoint
            case "application/vnd.ms-powerpoint":
                imageType = "/etc/designs/intranet/catalog_images/ppt-icon.png";
                break;

            case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
                imageType = "/etc/designs/intranet/catalog_images/ppt-icon.png";
                break;

            // Text
            case "text/plain":
                imageType = "/etc/designs/intranet/catalog_images/images.png";
                break;

            // mp3
            case "audio/mpeg":
                imageType = "/etc/designs/intranet/catalog_images/iTunes-mp3.png";
                break;

            // mp3
            case "video/x-ms-wmv":
                imageType = "/etc/designs/intranet/catalog_images/abc.jpg";
                break;

            // swf
            case "application/x-shockwave-flash":
                imageType = "/etc/designs/intranet/catalog_images/swf-img.jpg";
                break;

            default:
                imageType = "/etc/designs/intranet/catalog_images/unknown-icon.png";
                break;

        }
        return imageType;
    }

}
