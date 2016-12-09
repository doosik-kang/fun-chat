package restapi.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import core.entities.User
import core.entities.Defines._
import org.joda.time.DateTime
import restapi.http.routes.entities._
import spray.json._

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object UserJsonFormat extends RootJsonFormat[User] {
    override def read(json: JsValue): User = json match {
      case jsObject: JsObject =>
        jsObject.getFields("userId", "name", "lastSeen") match {
          case Seq(userId, JsString(name), JsNumber(lastSeen)) =>
            User(userId.convertTo[Option[UserID]], name, new DateTime(lastSeen))
        }
      case _ => deserializationError("An error occurred while serializing User entity.")
    }

    override def write(obj: User): JsValue = JsObject(
      "userId"   -> obj.userId.toJson,
      "name"     -> JsString(obj.name),
      "lastSeen" -> JsNumber(obj.lastSeen.getMillis)
    )
  }

  implicit object UserInformationEntityJsonFormat extends RootJsonFormat[UserInformationEntity] {
    override def read(json: JsValue): UserInformationEntity = json match {
      case jsObject: JsObject =>
        jsObject.getFields("name", "lastSeen") match {
          case Seq(JsString(name), JsNumber(lastSeen)) =>
            UserInformationEntity(name, new DateTime(lastSeen))
        }
      case _ => deserializationError("An error occurred while serializing User entity.")
    }

    override def write(obj: UserInformationEntity): JsValue = JsObject(
      "name"     -> JsString(obj.name),
      "lastSeen" -> JsNumber(obj.lastSeen.getMillis)
    )
  }
}
