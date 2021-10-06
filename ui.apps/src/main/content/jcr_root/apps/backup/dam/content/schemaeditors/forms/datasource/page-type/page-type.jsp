<%@page session="false" import="
                  org.apache.sling.api.resource.Resource,
                  org.apache.sling.api.resource.ResourceUtil,
                  org.apache.sling.api.resource.ValueMap,
                  org.apache.sling.api.resource.ResourceResolver,
                  org.apache.sling.api.resource.ResourceMetadata,
                  org.apache.sling.api.wrappers.ValueMapDecorator,
                  java.util.List,
                  java.util.ArrayList,
                  java.util.HashMap,
                  java.util.Locale,
				  java.util.Collections,
				  java.util.Iterator,
				  com.day.cq.tagging.Tag,
				  com.day.cq.tagging.TagManager,
                  com.adobe.granite.ui.components.ds.DataSource,
                  com.adobe.granite.ui.components.ds.EmptyDataSource,
                  com.adobe.granite.ui.components.ds.SimpleDataSource,
                  com.adobe.granite.ui.components.ds.ValueMapResource,
                  com.day.cq.wcm.api.Page,
                  com.day.cq.wcm.api.PageManager"%><%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><%
%><cq:defineObjects/><%

// set fallback

request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
Iterator<Page> pageIterator = Collections.emptyIterator();
ResourceResolver resolver = resource.getResourceResolver();
Resource rolesTagResource  = resolver.getResource("/etc/tags/demo/page-type");

	final Tag roletag = rolesTagResource.adaptTo(Tag.class);
    final Iterator<Tag> iter = roletag.listChildren();
	ValueMap vm = null; 

	List<Resource> fakeResourceList = new ArrayList<Resource>();
			vm = new ValueMapDecorator(new HashMap<String, Object>());
               vm.put("value","default");
				vm.put("text","Default");
 
				fakeResourceList.add(new ValueMapResource(resolver, new ResourceMetadata(), "nt:unstructured", vm));

	while (iter.hasNext()) {
				
				vm = new ValueMapDecorator(new HashMap<String, Object>());
                final Tag rolechildTag = iter.next();
				vm.put("value",rolechildTag.getName());
				vm.put("text",rolechildTag.getTitle());
 
				fakeResourceList.add(new ValueMapResource(resolver, new ResourceMetadata(), "nt:unstructured", vm));
            }

//Create a DataSource that is used to populate the drop-down control
DataSource ds = new SimpleDataSource(fakeResourceList.iterator());
request.setAttribute(DataSource.class.getName(), ds);
 
%>