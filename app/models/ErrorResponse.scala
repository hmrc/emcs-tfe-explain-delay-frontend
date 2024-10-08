/*
 * Copyright 2023 HM Revenue & Customs
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

package models

import play.api.libs.json.{JsonValidationError => PlayJsonValidationError}

import scala.util.control.NoStackTrace

sealed trait ErrorResponse {
  val message: String
}

case object UnexpectedDownstreamResponseError extends ErrorResponse {
  val message = "Unexpected downstream response status"
}

case object JsonValidationError extends ErrorResponse {
  val message = "JSON validation error"
}

case class BadRequestError(msg: String) extends ErrorResponse {
  val message = s"Bad Request returned from downstream service. With message: $msg"
}

case class SubmitExplainDelayException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse
case class UserAnswersException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse
case class MissingMandatoryPage(message: String) extends Exception(message) with NoStackTrace with ErrorResponse
case class TraderKnownFactsException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse

case class NRSBrokerJsonParsingError(errors: Seq[PlayJsonValidationError]) extends ErrorResponse {
  val message = s"Errors parsing JSON, errors: $errors"
}

case class IdentityDataException(message: String) extends ErrorResponse
