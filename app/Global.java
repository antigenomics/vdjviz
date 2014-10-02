import play.GlobalSettings;
import play.api.mvc.EssentialFilter;




public class Global extends GlobalSettings {

    @Override
    public <T extends EssentialFilter> Class<T>[] filters() {
        //Class[] filters = {CSRFFilter.class};
        Class[] filters = {};
        return filters;
    }
}