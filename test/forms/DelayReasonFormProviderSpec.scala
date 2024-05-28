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

package forms

import fixtures.messages.DelayReasonMessages
import forms.behaviours.OptionFieldBehaviours
import models.DelayReason
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.FormError
import play.api.i18n.{Messages, MessagesApi}

class DelayReasonFormProviderSpec extends OptionFieldBehaviours with GuiceOneAppPerSuite {

  val form = new DelayReasonFormProvider()()
  val fieldName = "value"
  val requiredKey = "delayReason.error.required"

  ".value" - {

    behave like optionsField[DelayReason](
      form,
      fieldName,
      validValues  = DelayReason.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  "Error Messages" - {

    Seq(DelayReasonMessages.English) foreach { messagesForLanguage =>

      implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

      s"when output for language code '${messagesForLanguage.lang.code}'" - {

        "have the correct error message for date required" in {
          messages(requiredKey) mustBe messagesForLanguage.requiredError
        }
      }
    }
  }
}
