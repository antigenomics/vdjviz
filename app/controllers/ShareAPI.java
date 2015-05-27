package controllers;

import models.SharedGroup;

import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.server.ServerResponse;

public class ShareAPI extends Controller {

    public static Result sharedAccount(String link) {
        SharedGroup byLink = SharedGroup.findByLink(link);
        if (byLink == null)
            return ok(views.html.commonPages.notFound.render("share/" + link));
        return ok(views.html.account.accountMainPage.render(true, link));
    }

    public static F.Promise<Result> info(final String link) {
        return F.Promise.promise(new F.Function0<Result>() {
            @Override
            public Result apply() throws Throwable {
                SharedGroup sharedGroup = SharedGroup.findByLink(link);
                if (sharedGroup == null)
                    return badRequest(Json.toJson(new ServerResponse("error", "Invalid request")));
                return ok();
            }
        });
    }
}
