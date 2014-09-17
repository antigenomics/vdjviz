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
    var s = ""
    k.foreach((file:File) => s += file.getAbsolutePath)
    Ok(views.html.fileslist(k))
  }

  def uploadFile = Action(parse.multipartFormData) { request =>
    request.body.file("fileUpload").map { video =>
      val videoFilename = video.filename
      val contentType = video.contentType.get
      video.ref.moveTo(new File("/home/bvdmitri/temp/" + video.filename))
    }.getOrElse {
      Redirect(routes.Application.index)
    }
    Ok("File has been uploaded")
  }


}