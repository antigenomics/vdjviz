package controllers;

import com.antigenomics.vdjdb.core.db.CdrDatabase;
import com.antigenomics.vdjdb.core.db.CdrEntry;
import com.antigenomics.vdjdb.core.query.CdrDatabaseSearcher;
import com.antigenomics.vdjdb.core.query.CdrSearchResult;
import com.fasterxml.jackson.databind.JsonNode;
import models.LocalUser;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseSearchAPI extends Controller {

    @SecureSocial.UserAwareAction
    public static Result databaseSearchMainPage() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        if (user != null) {
            return ok(views.html.databaseSearch.databaseSearchMainPage.render(LocalUser.find.byId(user.identityId().userId()).account.userName));
        }
        return ok(views.html.databaseSearch.databaseSearchMainPage.render(null));
    }

    public static Result searchInput() {
        JsonNode request = request().body().asJson();
        String input = request.findValue("input").asText();
        if (input.equals("")) {
            return badRequest("Empty input field");
        }
        int hash = 0;
        List<Object> data = new ArrayList<>();
        CdrDatabase cdrDatabase = new CdrDatabase();
        CdrDatabaseSearcher cdrDatabaseSearcher = new CdrDatabaseSearcher(cdrDatabase);
        for (CdrSearchResult cdrSearchResult : cdrDatabaseSearcher.search(input)) {
            HashMap<String, Object> dataNode = new HashMap<>();
            dataNode.put("alignment", cdrSearchResult.getAlignment().getAlignmentHelper().toString());
            dataNode.put("score", cdrSearchResult.getAlignment().getScore());
            //TODO
            dataNode.put("hash", hash++);
            List<List<String>> annotations = new ArrayList<>();
            for (CdrEntry cdrEntry : cdrSearchResult.getCdrEntrySet()) {
                List<String> anNode = cdrEntry.getAnnotation();
                anNode.add(0, cdrEntry.j);
                anNode.add(0, cdrEntry.v);
                annotations.add(anNode);
            }
            dataNode.put("annotations", annotations);
            dataNode.put("annotationsHeader", cdrDatabase.annotationHeader);
            data.add(dataNode);
        }
        return ok(Json.toJson(data));
    }

}
