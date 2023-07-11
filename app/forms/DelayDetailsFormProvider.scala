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

import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms.{optional, text => playText}
import play.api.data.validation.{Constraint, Invalid, Valid}

import javax.inject.Inject

class DelayDetailsFormProvider @Inject() extends Mappings {

  def apply(fieldMandatory: Boolean): Form[Option[String]] = {

    def isMandatory: Constraint[Option[String]] =
      Constraint {
        case value if fieldMandatory && (value.isEmpty || value.exists(_.isEmpty)) =>
          Invalid("delayDetails.error.required")
        case _ =>
          Valid
      }

    Form(
      "value" ->
        optional(
          playText
            .transform[String](
              _.replace("\n", " ")
                .replace("\r", " ")
                .replaceAll(" +", " ")
                .trim,
              identity
            )
            .verifying(maxLength(TEXTAREA_MAX_LENGTH, s"delayDetails.error.length"))
            .verifying(regexpUnlessEmpty(ALPHANUMERIC_REGEX, s"delayDetails.error.character"))
            .verifying(regexpUnlessEmpty(XSS_REGEX, s"delayDetails.error.xss"))
        )
          .verifying(isMandatory)
    )

  }
}
