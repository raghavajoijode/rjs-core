package com.subra.aem.rjs.pdf;


import com.day.cq.dam.api.Asset;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;


@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BadgeExporterModel {

    public static final String JCR_CONTENT_METADATA = "jcr:content/metadata";
    @SlingObject
    private ResourceResolver resourceResolver;

    @ValueMapValue
    private String[] badgePaths;

    @ValueMapValue
    private String daisy1Color;

    @ValueMapValue
    private String daisy2Color;

    @ValueMapValue
    private String daisy3Color;

    @ValueMapValue
    private String daisy4Color;

    @ValueMapValue
    private String daisy5Color;

    @ValueMapValue
    private String daisy6Color;

    private Map<String, String> colorMap;

    private List<BadgeDTO> badges;

    private static final Logger log = LoggerFactory.getLogger(BadgeExporterModel.class);


    class BadgeComparator implements Comparator<Resource> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());

        public int compare(Resource badge1, Resource badge2) {
            try {
                if (badge1 != null && badge2 != null) {
                    ValueMap b1ValMap = badge1.getChild(JCR_CONTENT_METADATA).getValueMap();
                    ValueMap b2ValMap = badge2.getChild(JCR_CONTENT_METADATA).getValueMap();
                    String b1Name = b1ValMap.get("dc:title", String.class);
                    String b2Name = b2ValMap.get("dc:title", String.class);
                    return b1Name.compareTo(b2Name);
                }
            } catch (Exception e) {
                log.error("Error occurred while comparing {} and {}", badge1, badge2);
                e.printStackTrace();
            }
            return 0;
        }
    }

    @PostConstruct
    public void activate() {
        updateColorMap();
        badges = new LinkedList<>();
        badgePaths = new String[] {"/content/dam/iqos/global/marketing/brand/logo"};
        if (badgePaths != null && badgePaths.length > 0) {
            for (String path : badgePaths) {
                if (path != null && path.trim().length() > 0) {
                    try {
                        getBadges(path);
                    } catch (Exception e) {
                        log.error("Error occurred while rendering badge images at {}", path);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public List<BadgeDTO> getBadges() {
        return badges;
    }

    private void getBadges(String path) {
        Resource content = resourceResolver.getResource(path);
        if (content != null && content.hasChildren()) {
            Iterable<Resource> badgeResources = content.getChildren();
            List<Resource> sortedBadgeList = new ArrayList<>();
            for (Resource badge : badgeResources) {
                if (badge.isResourceType("dam:Asset")) {
                    sortedBadgeList.add(badge);
                }
            }
            Collections.sort(sortedBadgeList, new BadgeComparator());
            for (Resource badge : sortedBadgeList) {
                updateBadgeItem(badge);
            }
        }
    }

    private void updateBadgeItem(Resource badge) {
        try {
            String badgePath = badge.getPath();
            Asset asset = badge.adaptTo(Asset.class);
            Resource badgeMetadata = badge.getChild(JCR_CONTENT_METADATA);
            Optional.of(badgeMetadata).ifPresent(r -> {
                ValueMap props = r.adaptTo(ValueMap.class);
                BadgeDTO badgeItem = new BadgeDTO(badge, props);
                badgeItem.setRegImageSrc(StringUtils.defaultIfBlank(asset.getPath(), badgePath));
                badgeItem.setSmallImageSrc(StringUtils.defaultIfBlank(asset.getPath(), badgePath));
                updateBadgeFromTags(props.get("cq:tags", String[].class), badgeItem);
                badges.add(badgeItem);
            });
        } catch (Exception e) {
            log.error("Error occurred while rendering badge at {}", badge.getPath());
            e.printStackTrace();
        }
    }


    private void updateColorMap() {
        colorMap = new HashMap<>();
        colorMap.put("daisy1", daisy1Color);
        colorMap.put("daisy2", daisy2Color);
        colorMap.put("daisy3", daisy3Color);
        colorMap.put("daisy4", daisy4Color);
        colorMap.put("daisy5", daisy5Color);
        colorMap.put("daisy6", daisy6Color);
    }

    private void updateBadgeFromTags(String[] tags, BadgeDTO badgeItemDTO) {
        List<String> filters = new LinkedList<>();
        if (tags != null && tags.length > 0) {
            for (String tagString : tags) {
                String[] tagsArr = tagString.toLowerCase().split("/");
                if (tagsArr.length > 3) {
                    updateBadgeFilterAndColor(badgeItemDTO, filters, tagsArr);
                }
            }
        }
        badgeItemDTO.setFilter(filters.stream().collect(Collectors.joining(" ")));
    }

    private void updateBadgeFilterAndColor(BadgeDTO badgeItemDTO, List<String> filters, String[] tagsArr) {
        for (int i = 3; i < tagsArr.length; i++) {
            String filter = tagsArr[i].replace(" ", "");
            filters.add(filter);
            String extistingColor = badgeItemDTO.getBackGroundColor();
            String pottentialNewColor = colorMap.get(filter);
            if (StringUtils.isBlank(extistingColor) && StringUtils.isNotBlank(pottentialNewColor)) {
                badgeItemDTO.setBackGroundColor(pottentialNewColor);
            }
        }
    }

}