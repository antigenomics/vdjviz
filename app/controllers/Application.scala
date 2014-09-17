package controllers

import play.api._
import play.api.mvc._
import java.io.File
import play.api.Play.current
import java.nio.file.{Files, Path}

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("File Upload In Play"))
  }

  def FilesList = Action {
    val k = new java.io.File("/home/bvdmitri/temp").listFiles
    Ok(views.html.fileslist(k))
  }

  def uploadFile = Action(parse.multipartFormData) { request =>
    request.body.file("fileUpload").map { file =>
      val Filename = file.filename
      val contentType = file.contentType.get
      file.ref.moveTo(new File("/home/bvdmitri/temp/" + file.filename))
    }.getOrElse {
      Redirect(routes.Application.index)
    }
    Ok("File has been uploaded")
  }


}