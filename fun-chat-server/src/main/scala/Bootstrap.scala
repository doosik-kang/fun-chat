import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import core.authentication._
import core.authentication.tokenGenerators._
import core.db.{DatabaseContext, FlywayService}
import restapi.http.HttpService
import restapi.http.routes.HttpRouter
import utils.Configuration

import scala.concurrent.ExecutionContext

class Bootstrap {

  def startup(): Unit = {
    implicit val actorSystem                     = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val ec: ExecutionContext            = actorSystem.dispatcher

    val config        = new Configuration()
    val dbc           = new DatabaseContext()
    val flywayService = new FlywayService(config)
    flywayService.migrateDatabaseSchema()

    val userAuthenticator =
      new UserAuthenticator(SecretKeyHashUtils.validate, AuthTokenGenerator.generate, dbc.credentialsDao)
    val authService = new AuthenticationService(userAuthenticator, dbc.usersDao)
    val httpRouter  = new HttpRouter(dbc, authService)
    val httpService = new HttpService(httpRouter, config)
    httpService.start()
  }
}