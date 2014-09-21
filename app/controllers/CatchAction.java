package controllers;

import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import utils.ExceptionMailer;

public class CatchAction extends Action<Catch> {

    public F.Promise<play.mvc.Result> call(Http.Context ctx) {
        try {
            return delegate.call(ctx);
        } catch (Throwable e) {
            if (configuration.send())
                ExceptionMailer.send(e);
            else
                e.printStackTrace();
            return null;
        }
    }
}
