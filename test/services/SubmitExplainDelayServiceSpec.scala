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

package services

import base.SpecBase
import fixtures.SubmitExplainDelayFixtures
import mocks.connectors.MockSubmitExplainDelayConnector
import mocks.services.MockAuditingService
import models.audit.SubmitExplainDelayAudit
import models.submitExplainDelay.SubmitExplainDelayModel
import models.{DelayReason, DelayType, SubmitExplainDelayException, UnexpectedDownstreamResponseError}
import pages.{DelayDetailsPage, DelayReasonPage, DelayTypePage}
import play.api.test.FakeRequest
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class SubmitExplainDelayServiceSpec extends SpecBase with MockSubmitExplainDelayConnector with SubmitExplainDelayFixtures with MockAuditingService {

  implicit val hc = HeaderCarrier()
  implicit val ec = ExecutionContext.global

  lazy val testService = new SubmitExplainDelayService(mockSubmitExplainDelayConnector, mockAuditingService)

  ".submit(ern: String, submission: SubmitExplainDelayModel)" - {

    "should submit, audit and return a success response" - {

      "when connector receives a success from downstream" in {

        val userAnswers = emptyUserAnswers
          .set(DelayTypePage, DelayType.ReportOfReceipt)
          .set(DelayReasonPage, DelayReason.Other)
          .set(DelayDetailsPage, Some("more information"))

        val request = dataRequest(FakeRequest(), userAnswers)

        val submission = SubmitExplainDelayModel(getMovementResponseModel)(userAnswers)

        MockSubmitExplainDelayConnector.submit(testErn, submission).returns(Future.successful(Right(successResponse)))

        MockAuditingService.audit(
          SubmitExplainDelayAudit(
            "credId",
            "internalId",
            "ern",
            submission,
            Right(successResponse)
          )
        ).noMoreThanOnce()

        testService.submit(testErn, testArc)(hc, request).futureValue mustBe successResponse
      }
    }

    "should submit, audit and return a failure response" - {

      "when connector receives a failure from downstream" in {

        val userAnswers = emptyUserAnswers
          .set(DelayTypePage, DelayType.ReportOfReceipt)
          .set(DelayReasonPage, DelayReason.Other)
          .set(DelayDetailsPage, Some("more information"))

        val request = dataRequest(FakeRequest(), userAnswers)
        val submission = SubmitExplainDelayModel(getMovementResponseModel)(userAnswers)

        MockSubmitExplainDelayConnector.submit(testErn, submission).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        MockAuditingService.audit(
          SubmitExplainDelayAudit(
            "credId",
            "internalId",
            "ern",
            submission,
            Left(UnexpectedDownstreamResponseError)
          )
        ).noMoreThanOnce()

        intercept[SubmitExplainDelayException](await(testService.submit(testErn, testArc)(hc, request))).getMessage mustBe
          s"Failed to submit Explain a Delay to emcs-tfe for ern: '$testErn' & arc: '$testArc'"
      }
    }
  }
}
