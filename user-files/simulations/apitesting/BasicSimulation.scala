package apitesting

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BasicSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("https://reqres.in/api") // Here is the root for all relative URLs
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
  
  val header = Map(
    "Content-Type" -> "application/json"
  )

  object ListOfUsers {
    val listOfUsers = exec(
      http("List of users")
      .get("/users")
      .headers(header)
      .queryParam("page", "2")
      .check(status.is(200))
    )
  }

  object successfullyLogin {
    val successfullyLogin = exec(
      http("successfully login")
      .post("/login")
      .headers(header)
      .body(ElFileBody("successfully-login.json"))
      .asJson
      .check(status.is(200))
    )
  }

  object UnsuccessfullyLogin {
    val unsuccessfullyLogin = exec(
      http("Unsuccessfully login")
      .post("/login")
      .headers(header)
      .body(ElFileBody("unsuccessfully-login.json"))
      .asJson
      .check(status.is(400))
    )
  }

  object CreateUser {
    val createUser = exec(
      http("Create user")
      .post("/users")
      .headers(header)
      .body(ElFileBody("create-user.json"))
      .asJson
      .check(status.is(201))
      .check(jsonPath("$.name").saveAs("name"))
      .check(jsonPath("$.job").saveAs("job"))
    )
  }

  object UpdateUser {
    val updateUser = exec(
      http("Update user")
      .put("/users")
      .headers(header)
      .body(ElFileBody("update-user.json"))
      .asJson
      .check(status.is(200))
    )
  }
  
  val basicTestingScn = scenario("Testing").exec(
    ListOfUsers.listOfUsers,
    successfullyLogin.successfullyLogin,
    UnsuccessfullyLogin.unsuccessfullyLogin,
    CreateUser.createUser,
    UpdateUser.updateUser
  )

  setUp(
    basicTestingScn.inject(atOnceUsers(2)
    ).protocols(httpProtocol)
  )
}
/*
  nothingFor(duration): Pausa por um determinado período.
  atOnceUsers(nbUsers): Injeta um determinado número de usuários de uma só vez.
  rampUsers(nbUsers) over(duration): Injeta um determinado número de usuários com uma rampa linear por um determinado período.
  constantUsersPerSec(rate) during(duration): Injeta usuários a uma taxa constante, definida em usuários por segundo, durante uma determinada duração. Os usuários serão injetados em intervalos regulares.
  constantUsersPerSec(rate) during(duration) randomized: Injeta usuários a uma taxa constante, definida em usuários por segundo, durante uma determinada duração. Os usuários serão injetados em intervalos aleatórios.
  rampUsersPerSec(rate1) to (rate2) during(duration): Injeta usuários da taxa inicial para a taxa alvo, definida em usuários por segundo, durante uma determinada duração. Os usuários serão injetados em intervalos regulares.
  rampUsersPerSec(rate1) to(rate2) during(duration) randomized: Injeta usuários da taxa inicial para a taxa alvo, definida em usuários por segundo, durante uma determinada duração. Os usuários serão injetados em intervalos aleatórios.
  splitUsers(nbUsers) into(injectionStep) separatedBy(duration): Execute repetidamente a etapa de injeção definida, separada por uma pausa da duração especificada, até atingir nbUsers , o número total de usuários a injetar.
  splitUsers(nbUsers) into(injectionStep1) separatedBy(injectionStep2): Execute repetidamente a primeira etapa de injeção definida ( injectionStep1 ), separada pela execução da segunda etapa de injeção ( injeçãoStep2 ) até atingir nbUsers , o número total de usuários a injetar.
  heavisideUsers(nbUsers) over(duration): Injeta um determinado número de usuários após uma aproximação suave da função passo a passo estendida até uma determinada duração.
*/