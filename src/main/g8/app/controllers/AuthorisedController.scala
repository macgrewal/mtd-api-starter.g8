/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Request, Result}
import services.EnrolmentsAuthService
import uk.gov.hmrc.auth.core.{AuthorisationException, MissingBearerToken}
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class AuthorisedController extends BaseController {

  val enrolmentsAuthService: EnrolmentsAuthService

  def authorisedAction(block: Request[AnyContent] => Future[Result]): Action[AnyContent] = Action.async {
    implicit request =>

      enrolmentsAuthService.authorised() {
        block(request)
      } recoverWith {
        case _: MissingBearerToken => Future.successful(Unauthorized(Json.obj()))
        case _: AuthorisationException => Future.successful(Forbidden(Json.obj()))
      }
  }
}
