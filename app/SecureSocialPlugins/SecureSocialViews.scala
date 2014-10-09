package SecureSocialPlugins

import play.api.mvc.{RequestHeader, Request}
import play.api.templates.{Txt, Html}
import securesocial.controllers.Registration.RegistrationInfo
import securesocial.controllers.TemplatesPlugin
import securesocial.core.{Identity, SecuredRequest, SocialUser}
import play.api.data.Form
import securesocial.core.SecureSocial._
import securesocial.controllers.PasswordChange.ChangeInfo

class SecureSocialViews(application: play.Application) extends TemplatesPlugin {

    override def getSignUpPage[A](implicit request: Request[A], form: Form[RegistrationInfo], token: String): Html = {
      views.html.Secure.Registration.signUp(form, token)
    }

    override def getLoginPage[A](implicit request: Request[A], form: Form[(String, String)],
                                 msg: Option[String] = None): Html =
    {
      views.html.Secure.login(form, msg)
    }

    override def getStartSignUpPage[A](implicit request: Request[A], form: Form[String]): Html = {
      views.html.Secure.Registration.startSignUp(form)
    }

    override def getStartResetPasswordPage[A](implicit request: Request[A], form: Form[String]): Html = {
      views.html.Secure.Registration.startResetPassword(form)
    }

    def getResetPasswordPage[A](implicit request: Request[A], form: Form[(String, String)], token: String): Html = {
      views.html.Secure.Registration.resetPasswordPage(form, token)
    }

    def getPasswordChangePage[A](implicit request: SecuredRequest[A], form: Form[ChangeInfo]): Html = {
      views.html.Secure.passwordChange(form)
    }

    /*
   def getSignUpEmail(token: String)(implicit request: play.api.mvc.RequestHeader): String = {
     views.html.custom.mails.signUpEmail(token).body
   }
   */

    def getSignUpEmail(token: String)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
      (None, Some(securesocial.views.html.mails.signUpEmail(token)))
    }


    /*
    def getAlreadyRegisteredEmail(user: SocialUser)(implicit request: play.api.mvc.RequestHeader): String = {
      views.html.custom.mails.alreadyRegisteredEmail(user).body
    }
    */

    def getAlreadyRegisteredEmail(user: Identity)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
      (None, Some(securesocial.views.html.mails.alreadyRegisteredEmail(user)))
    }

    /*
    def getWelcomeEmail(user: SocialUser)(implicit request: play.api.mvc.RequestHeader): String = {
      views.html.custom.mails.welcomeEmail(user).body
    }
    */
    def getWelcomeEmail(user: Identity)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
      (None, Some(securesocial.views.html.mails.welcomeEmail(user)))
    }

    /*
    def getUnknownEmailNotice()(implicit request: play.api.mvc.RequestHeader): String = {
      views.html.custom.mails.unknownEmailNotice(request).body
    }
    */

    def getUnknownEmailNotice()(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
      (None, Some(securesocial.views.html.mails.unknownEmailNotice(request)))
    }

    /*
   def getSendPasswordResetEmail(user: SocialUser, token: String)(implicit request: play.api.mvc.RequestHeader): String = {
     views.html.custom.mails.passwordResetEmail(user, token).body
   }
   */
    def getSendPasswordResetEmail(user: Identity, token: String)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
      (None, Some(securesocial.views.html.mails.passwordResetEmail(user, token)))
    }

    /*
    def getPasswordChangedNoticeEmail(user: SocialUser)(implicit request: play.api.mvc.RequestHeader): String = {
      views.html.custom.mails.passwordChangedNotice(user).body
    }
    */
    def getPasswordChangedNoticeEmail(user: Identity)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
      (None, Some(securesocial.views.html.mails.passwordChangedNotice(user)))
    }


    def getNotAuthorizedPage[A](implicit request: Request[A]): Html = {
      securesocial.views.html.notAuthorized()
    }
}