package com.wjw.mongo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

@RestController
public class MongoController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @GetMapping(value = "/removeNewFeaturesInRole", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object removeNewFeaturesInRole(
            @RequestParam(name = "remove", required = false, defaultValue = "false") boolean remove) {
        List<Object> ids = new ArrayList<>();
        ids.add(241901148045501d);
        ids.add(241901148045502d);
        ids.add(241901148045503d);
        ids.add(241901148045504d);
        ids.add(241901148045505d);
        ids.add(241901148045501l);
        ids.add(241901148045502l);
        ids.add(241901148045503l);
        ids.add(241901148045504l);
        ids.add(241901148045505l);
        Query query = new Query(Criteria.where("featureIds").in(ids));
        List<Map> roles = mongoTemplate.find(query, Map.class, "role");
        if (remove) {
            BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, "role");
            roles.forEach(e -> {
                List<Number> featureIds = (List<Number>) e.get("featureIds");
                featureIds.removeAll(ids);
                Query updateQuery = new Query(Criteria.where("_id").is(e.get("_id")));
                Update update = new Update();
                update.set("featureIds", featureIds);
                ops.updateOne(updateQuery, update);
            });
            ops.execute();
        }
        return roles.stream().map(e -> e.get("_id") + "_" + e.get("name")).collect(Collectors.toList());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @GetMapping(value = "/removeNewFeaturesInOld", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object removeNewFeaturesInOld(
            @RequestParam(name = "remove", required = false, defaultValue = "false") boolean remove) {
        List<Object> adminRoles = new ArrayList<>();
        adminRoles.add(233109349990402d);
        adminRoles.add(233109349990402l);
        adminRoles.add(233109349990403d);
        adminRoles.add(233109349990403l);
        adminRoles.add(241901148045368d);
        adminRoles.add(241901148045368l);
        adminRoles.add(241901148045365d);
        adminRoles.add(241901148045365l);
        List<Object> ids = new ArrayList<>();
        ids.add(241901148045501d);
        ids.add(241901148045502d);
        ids.add(241901148045501l);
        ids.add(241901148045502l);
        Query query = new Query(Criteria.where("featureIds")
                .in(Lists.newArrayList(241901148045455d, 241901148045455l, 241901148045461d, 241901148045461l)).and("_id")
                .nin(adminRoles));
        List<Map> roles = mongoTemplate.find(query, Map.class, "role");
        if (!CollectionUtils.isEmpty(roles) && remove) {
            BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, "role");
            roles.forEach(e -> {
                List<Number> featureIds = (List<Number>) e.get("featureIds");
                featureIds.removeAll(ids);
                Query updateQuery = new Query(Criteria.where("_id").is(e.get("_id")));
                Update update = new Update();
                update.set("featureIds", featureIds);
                ops.updateOne(updateQuery, update);
            });
            ops.execute();
        }
        return roles.stream().map(e -> e.get("_id")).collect(Collectors.toList());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @GetMapping(value = "/fixWidgetNoOrgId", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object fixWidgetNoOrgId(@RequestParam(name = "modify", required = false, defaultValue = "false") boolean modify)
            throws JSONException, ClassNotFoundException {
        Query query = new Query(Criteria.where("organizationId").is(null));
        List<Map> widgets = mongoTemplate.find(query, Map.class, "crisisWidget");
        widgets = widgets.stream().filter(e -> e.containsKey("dashboardId")).collect(Collectors.toList());
        List<Object> dashboardIds = widgets.stream().map(e -> e.get("dashboardId")).distinct().collect(Collectors.toList());
        Map<Object, List<Map>> dashboardIdWidgetGroup =
                widgets.stream().collect(Collectors.groupingBy(e -> e.get("dashboardId")));
        query = new Query(Criteria.where("_id").in(dashboardIds));
        List<Map> dashboards = mongoTemplate.find(query, Map.class, "crisisDashboard");
        Map<Object, Object> dashboardIdOrgMap =
                dashboards.stream().collect(Collectors.toMap(e -> e.get("_id").toString(), e -> e.get("organizationId")));
        List<Map> invalidWidgets = new ArrayList<>();
        for (Object dashboardId : dashboardIdWidgetGroup.keySet()) {
            List<Map> widgetGroup = dashboardIdWidgetGroup.get(dashboardId);
            Object orgId = dashboardIdOrgMap.get(dashboardId);
            if (orgId != null) {
                for (Map w : widgetGroup) {
                    w.put("organizationId", orgId);
                }
            } else {
                for (Map w : widgetGroup) {
                    invalidWidgets.add(w);
                    widgets.remove(w);
                }
            }
        }
        System.out.println("Invalid widgets: " + invalidWidgets.stream().map(e -> e.get("_id")).collect(Collectors.toList()));
        if (!CollectionUtils.isEmpty(widgets) && modify) {
            BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, "crisisWidget");
            widgets.forEach(e -> {
                Query updateQuery = new Query(Criteria.where("_id").is(e.get("_id")));
                Update update = new Update();
                update.set("organizationId", e.get("organizationId"));
                ops.updateOne(updateQuery, update);
            });
            ops.execute();
        }
        return widgets.stream().collect(Collectors.toMap(e -> e.get("_id"), e -> e.get("organizationId")));

    }

    @GetMapping(value = "/query/{collectionName}/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void query(@PathVariable(name = "collectionName") String collectionName, @PathVariable(name = "id") String id)
            throws JSONException, ClassNotFoundException {
        System.out.println(mongoTemplate.findById(id, Map.class, collectionName));
    }
    
    @SuppressWarnings("rawtypes")
    @GetMapping(value = "/dashboard/uniqueIndex", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object uniqueIndex(@RequestParam(name = "modify", required = false, defaultValue = "false") boolean modify)
            throws JSONException, ClassNotFoundException {
        Query query = new Query(Criteria.where("scopeType").is("CRISISEVENT"));
        List<Map> dashboards = mongoTemplate.find(query, Map.class, "crisisDashboard");
        if (!CollectionUtils.isEmpty(dashboards) && modify) {
            BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, "crisisDashboard");
            dashboards.forEach(e -> {
                Query updateQuery = new Query(Criteria.where("_id").is(e.get("_id")));
                Update update = new Update();
                update.set("deleteFlag", e.get("_id"));
                ops.updateOne(updateQuery, update);
            });
            ops.execute();
        }
        return dashboards.stream().map(e -> e.get("_id") + "_" + e.get("name") + "_" + e.get("status")).collect(Collectors.toList());
        
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @GetMapping(value = "/taskListTemplate/download", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object taskListTemplateDownload()
            throws JSONException, ClassNotFoundException {
        Query query = new Query(Criteria.where("status").is("A"));
        List<Map> crisisTaskTemplates = mongoTemplate.find(query, Map.class, "crisisTaskTemplate");
        Set<String> output = new HashSet<>();
        for (Map taskTemplate : crisisTaskTemplates) {
            if (!Objects.isNull(taskTemplate.get("owner"))) {
                Map owner = (Map) taskTemplate.get("owner");
                if (!Objects.isNull(owner.get("contactOwners"))) {
                    List<Map> contacts = (List) owner.get("contactOwners");
                    if (!CollectionUtils.isEmpty(contacts)) {
                        for (Map contact : contacts) {
                            if (Objects.isNull(contact.get("ownerName"))) {
                                output.add(taskTemplate.get("_id").toString());
                            }
                        }
                    }
                }
            }
            
        }
        Map<String, Set<String>> idMap = new HashMap<>();
        idMap.put("ids", output);
        return idMap;
        
    }

}
