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
import utils.ArrayUtils.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseSearchAPI extends Controller {

    @SecureSocial.UserAwareAction
    public static Result databaseSearchMainPage() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        if (user != null) {
            return ok(views.html.databaseSearch.databaseSearchMainPage.render(LocalUser.find.byId(user.identityId().userId()).getAccountUserName()));
        }
        return ok(views.html.databaseSearch.databaseSearchMainPage.render(null));
    }

    public static class DatabaseHit {
        public String input;
        public String alignment;
        public Float score;
        public Integer uid;
        public List<List<String>> annotations;
        public String[] annotationsHeader;

        public DatabaseHit(String input, String alignment, Float score, Integer uid, List<List<String>> annotations, String[] annotationsHeader) {
            this.input = input;
            this.alignment = alignment;
            this.score = score;
            this.uid = uid;
            this.annotations = annotations;
            this.annotationsHeader = annotationsHeader;
        }
    }

    public static Result searchInput() {
        JsonNode request = request().body().asJson();
        String input = request.findValue("input").asText();
        if (input.equals("")) {
            return badRequest("Empty input field");
        }
        Integer uid = 1;
        List<DatabaseHit> data = new ArrayList<>();
        CdrDatabase cdrDatabase = new CdrDatabase();
        CdrDatabaseSearcher cdrDatabaseSearcher = new CdrDatabaseSearcher(cdrDatabase);
        for (CdrSearchResult cdrSearchResult : cdrDatabaseSearcher.search(input)) {
            List<List<String>> annotations = new ArrayList<>();
            for (CdrEntry cdrEntry : cdrSearchResult.getCdrEntrySet()) {
                List<String> anNode = cdrEntry.getAnnotation();
                anNode.add(0, cdrEntry.j);
                anNode.add(0, cdrEntry.v);
                annotations.add(anNode);
            }
            data.add(new DatabaseHit(
                    input,                                                          //input
                    cdrSearchResult.getAlignment().getAlignmentHelper().toString(), //alignment
                    cdrSearchResult.getAlignment().getScore(),                      //score
                    uid++,                                                          //uid
                    annotations,                                                    //annotations
                    cdrDatabase.annotationHeader                                    //header
                    ));
        }
        return ok(Json.toJson(data));
    }

}
