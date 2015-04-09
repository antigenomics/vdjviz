package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Account;
import models.LocalUser;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;

public class AnnotationTableController extends Controller {

    public static Result annotationData() {
        //Identifying user using the Secure Social API
        //Return data for chart
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;

        class AnnotationTableRequest {
            public int iDisplayStart;
            public int iDisplayLength;
            public int iColumns;
            public String sSearch;
            public boolean bRegex;
            public String sEcho;
        }

        class AnnotationTableResponse {
            public int iTotalRecords;
            public int iTotalDisplayRecords;
            public String sEcho;
            public Object aaData;

            public AnnotationTableResponse(int iTotalRecords, int iTotalDisplayRecords, String sEcho, Object aaData) {
                this.iTotalRecords = iTotalRecords;
                this.iTotalDisplayRecords = iTotalDisplayRecords;
                this.sEcho = sEcho;
                this.aaData = aaData;
            }
        }


        return ok();
    }

}
