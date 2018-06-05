package utils.nextpage.$package$

import config.FrontendAppConfig
import identifiers.$className$Id
import models.$modelPackage$.$model$
import play.api.mvc.{Call, Request}
import utils.NextPage

trait $className$NextPage {

  implicit val $className;format="decap"$: NextPage[$className$Id.type, $model$] = {
    new NextPage[$className$Id.type, $model$] {
      override def get(b: $model$)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case _ => ???
        }
     }
  }
}