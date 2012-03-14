package com.github.jrwest.scalamachine.core

/**
 * Created by IntelliJ IDEA.
 * User: jordanrw
 * Date: 3/11/12
 * Time: 11:35 PM
 */

trait WebmachineDecisions {

  /* Service Available? */
  lazy val b13: Decision = Decision("v3b13", true, (r: Resource) => r.serviceAvailable(_: ReqRespData, _: Context), b12, 503)

  /* Known Methods */
  lazy val b12: Decision = Decision("v3b12", (r: Resource) => r.knownMethods(_: ReqRespData, _: Context), (l: List[HTTPMethod], d: ReqRespData) => l.contains(d.method), b11, 501)

  /* URI Too Long? */
  lazy val b11: Decision = Decision("v3b11", true, (r: Resource) => r.uriTooLong(_: ReqRespData, _: Context), 414, b10)

  /* Allowed Methods */
  lazy val b10: Decision =
    Decision(
      "v3b10",
      (r: Resource) => r.allowedMethods(_: ReqRespData, _: Context),
      (l: List[HTTPMethod], d: ReqRespData) => l.contains(d.method),
      b9,
      (r: SimpleResult[List[HTTPMethod]]) => r.data.setStatusCode(405).setResponseHeader("Allow", r.value.map(_.toString).mkString(", "))
    )

  /* Malformed Request? */
  lazy val  b9: Decision = Decision("v3b9", true, (r: Resource) => r.isMalformed(_: ReqRespData, _: Context), 400, b8)

  /* Is Authorized? */
  lazy val  b8: Decision = Decision(
    "v3b8",
    AuthSuccess,
    (r: Resource) => r.isAuthorized(_: ReqRespData, _: Context),
    b7,
    (r: SimpleResult[AuthResult]) => r.value match {
      case AuthFailure(headerVal) => r.data.setResponseHeader("WWW-Authenticate", headerVal).setStatusCode(401);
      case _ => r.data
    }
  )
  
  lazy val  b7: Decision = null
}