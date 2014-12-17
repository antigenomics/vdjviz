import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.antigenomics.vdjdb.core.db.CdrDatabase;
import com.antigenomics.vdjdb.core.query.CdrDatabaseSearcher;
import com.antigenomics.vdjdb.core.query.CdrSearchResult;
import com.antigenomics.vdjtools.db.BrowserResult;
import com.antigenomics.vdjtools.db.CdrMatch;
import com.antigenomics.vdjtools.db.DatabaseBrowser;
import com.antigenomics.vdjtools.sample.Sample;
import com.fasterxml.jackson.databind.JsonNode;
import com.milaboratory.core.tree.TreeSearchParameters;
import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.data.DynamicForm;
import play.data.validation.ValidationError;
import play.data.validation.Constraints.RequiredValidator;
import play.i18n.Lang;
import play.libs.F;
import play.libs.F.*;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;


/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {

    @Test
    public void annotationTest() {
        
    }


}
