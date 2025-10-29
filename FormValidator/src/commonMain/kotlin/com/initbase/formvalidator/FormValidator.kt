package com.initbase.formvalidator

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.initbase.formvalidator.FormValidator.Flow.*
import com.initbase.formvalidator.FormValidator.Type.*

/**
 * Used to reference the validator in nested components.
 * ```LocalFormValidator.current```
 * */
val LocalFormValidator = compositionLocalOf { FormValidator() }
typealias validationField = FormValidator.ValidationField<*>
typealias customValidationResponse = Pair<Boolean, String?>

/**
 * @property errorMessage The error message of the first or last field validation based on the [Flow]. When [flow] is [Flow.Splash] this field is [Flow.fallbackErrorMessage]
 * @property onValidate CallBack invoked after validation with result. This is an internal call back for the library, use [valid]
 * @property valid Observe to get notified on validation changes
 * */
class FormValidator(val fields: List<validationField> = emptyList(), val flow: Flow = Down) {
    var onValidate: (Boolean) -> Unit = {}
    var errorMessage by mutableStateOf<String?>(null)
    val valid = mutableStateOf(false)

    /**
     * Describes the direction and type of validation in the form
     * @param fallbackErrorMessage The error message for the form in the case of  [Flow.Splash]
     * */
    enum class Flow(val fallbackErrorMessage: String = "Fill all required fields") {
        /**
         * The form is validated from top to bottom and the first field that is invalid causes the form to be invalid.
         * [FormValidator.errorMessage] is set to the error message of the first invalid item
         * */
        Down,

        /**
         * The form is validated from bottom to top and the last field that is invalid causes the form to be invalid.
         * [FormValidator.errorMessage] is set to the error message of the first invalid item
         * */
        Up,

        /**
         * All fields are validated at once and each field([ValidationField]) invokes [ValidationField.onError].
         * [FormValidator.errorMessage] is set to null
         * */
        Splash
    }

    /**
     * The Type of validation to perform on [ValidationField]
     * */
    sealed class Type<out T> {
        /**
         * Value provided to validation field must not be null.
         *
         * In the case of [String] the value must also not be blank
         * */
        object Required : Type<Nothing>()

        /**
         * Value provided to validation field must.
         *
         * In the case of [String] the value character length must be greater than[template].
         *
         * In the case of [Number] the value must be greater than [template].
         *
         * Returns false for other cases
         * @param T The type parameter of the [ValidationField]
         * @property template The value that is used to validate the value provided to the [ValidationField]
         * */
        data class MustBeMoreThan<T>(val template: T) : Type<T>()

        /**
         * Value provided to validation field must.
         *
         * In the case of [String] the value character length less than [template].
         *
         * In the case of [Number] the value must be greater than [template].
         *
         * Returns false for other cases
         * @param T The type parameter of the [ValidationField]
         * @property template The value that is used to validate the value provided to the [ValidationField]
         * */
        data class MustBeLessThan<T>(val template: T) : Type<T>()

        /**
         * Value provided to [ValidationField].
         *
         * In the case of [Number] be between([min] and [max] inclusive)  [min] and [max].
         *
         * Returns false for other cases
         * @property min The minimum value of the [ValidationField]
         * @property max The maximum value of the [ValidationField]
         * */
        data class MustBeInRange(
            val min: Number = 0,
            val max: Number = 100
        ) : Type<Number>()

        /**
         * Value provided to validation field is validated base on equality.
         *
         * In the case of [String]
         *  - if [template] is [String]=> value must match [template]
         *  - if [template] is [Number] => value character length must be equal to [template]
         *
         * In the case os [Number]
         *  - value must be equal to the [Number] value of template
         *
         * In the case of [java.lang.Object] (Custom class)
         *  - value must be equal to [template]
         *
         * returns false for other cases
         * @param T The type parameter of the [ValidationField]
         * @property template The value that is used to validate the value provided to the [ValidationField]
         * */
        data class MustBeEqualTo<T>(val template: T) : Type<T>()

        /**
         * Implement your own validation logic.
         * @param valid A callback that provides the value of the validation field and returns a pair of values.
         *
         * [Pair.first]=> The result of your validation
         *
         * [Pair.second]=> The errorMessage of your validation. Return null if validation passes
         * */
        data class Custom<T>(val valid: ((T) -> customValidationResponse)) : Type<T>()

        /**
         * Validates that the value provided to [ValidationField] matches and email patters
         */
        object Email : Type<Nothing>()

        /**
         * Value is always valid. Always returns true.
         */
        object Optional : Type<Nothing>()
    }


    /**
     * Corresponds to a field in a form and performs validation on the fields value
     * @param value The field value to be validated
     * @param name The the Field name, it is added to the error message to make it more descriptive. e.g 'Address is not valid'
     * @param type The type of validation that would be performed on this field. see [Type]
     * @param onError Callback that provides error message of validation. Called twice,
     *  - First before validation to reset error state
     *  - Second after validation to provide error message
     * */
    class ValidationField<T>(
        val value: T? = null,
        var name: String = "Field",
        val type: Type<T?> = Required,
        val onError: (String?) -> Unit = {}
    ) {
        var errorMessage: String? = null
            private set
        private val EMAIL_ADDRESS_PATTERN: Regex =
            Regex("[a-zA-Z0-9+._%\\-]{1,256}@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+")

        fun valid(): Boolean {
            val valid: Boolean
            var defaultErrorMessage: String? = "$name is not Valid"
            valid = if (value == null && type != Optional) {
                defaultErrorMessage = "$name is required"
                false
            } else
                when (value) {
                    is String -> {
                        when (type) {
                            Required -> {
                                defaultErrorMessage = "$name is required"
                                value.isNotBlank()
                            }

                            is MustBeMoreThan -> {
                                val length = type.template.toString().length
                                defaultErrorMessage =
                                    "$name must be longer than $length characters"
                                value.length > length
                            }

                            is MustBeLessThan -> {
                                val length = type.template.toString().length
                                defaultErrorMessage =
                                    "$name must be shorter than $length characters"
                                value.length < length
                            }

                            is MustBeInRange -> {
                                defaultErrorMessage =
                                    "$name must be in range ${type.min} - ${type.max}"
                                false
                            }

                            is MustBeEqualTo -> {
                                val template = type.template
                                when (template) {
                                    is String -> {
                                        defaultErrorMessage = "$name must match to $template"
                                        value == template
                                    }

                                    is Number -> {
                                        val length = template.toString().length
                                        defaultErrorMessage = "$name must be $length characters"
                                        value.length == length
                                    }

                                    else -> {
                                        defaultErrorMessage = "$name is not valid"
                                        false
                                    }
                                }
                            }

                            Email -> {
                                defaultErrorMessage = if (value.isEmpty())
                                    "Enter a valid email"
                                else
                                    "$value is not a valid email"
                                EMAIL_ADDRESS_PATTERN.matches(value)
                            }

                            is Custom -> {
                                val response = type.valid.invoke(value)
                                defaultErrorMessage = response.second
                                response.first
                            }

                            Optional -> true
                        }
                    }

                    is Number -> {
                        when (type) {
                            Required -> {
                                defaultErrorMessage = "$name is required"
                                false
                            }

                            is MustBeMoreThan -> {
                                val template = (type.template.toString()).toFloatOrNull() ?: 0f
                                defaultErrorMessage = "$name must be greater than $template"
                                value.toFloat() > template
                            }

                            is MustBeLessThan -> {
                                val template = type.template
                                defaultErrorMessage = "$name must be less than $template"
                                if (template is Number) {
                                    value.toFloat() < template.toFloat()
                                } else
                                    false
                            }

                            is MustBeInRange -> {
                                defaultErrorMessage =
                                    "$name must be in range ${type.min} - ${type.max}"
                                val mValue = value.toFloat()
                                mValue >= type.min.toFloat() && mValue <= type.max.toFloat()
                            }

                            is MustBeEqualTo -> {
                                val template = (type.template.toString()).toFloatOrNull() ?: 0f
                                defaultErrorMessage = "$name must be equal to $template"
                                value == template
                            }

                            Email -> {
                                defaultErrorMessage = "$name is not valid"
                                false
                            }

                            Optional ->
                                true

                            is Custom -> {
                                val response = type.valid.invoke(value)
                                defaultErrorMessage = response.second
                                response.first
                            }
                        }
                    }
                    //Field Type is not String or Number
                    else -> {
                        when (type) {
                            Required -> {
                                defaultErrorMessage = "$name is required"
                                value != null
                            }

                            Email -> {
                                defaultErrorMessage = "$name is not valid"
                                false
                            }

                            is MustBeEqualTo -> {
                                defaultErrorMessage = "$name does not match template"
                                value == type.template
                            }

                            is MustBeInRange -> {
                                defaultErrorMessage =
                                    "$name must be in range ${type.min} - ${type.max}"
                                false
                            }

                            is MustBeLessThan -> {
                                defaultErrorMessage = "$name must be less than ${type.template}"
                                false
                            }

                            is MustBeMoreThan -> {
                                defaultErrorMessage =
                                    "$name must be greater than ${type.template}"
                                false
                            }

                            Optional -> true
                            is Custom -> {
                                val response = type.valid.invoke(value)
                                defaultErrorMessage = response.second
                                response.first
                            }
                        }
                    }
                }
            if (errorMessage == null)
                errorMessage = defaultErrorMessage
            onError.invoke(
                if (valid)
                    null
                else errorMessage
            )
            return valid
        }
    }

    /**
     * Invokes validation on each field in the form.
     * @return **True** if all fields are valid and **false** otherwise
     * */
    fun validate(): Boolean {
        var valid = true
        errorMessage = null
        //reset the error state of each field
        fields.forEach {
            it.onError.invoke(null)
        }
        val invalidField: ValidationField<*>? = when (flow) {
            Down -> {
                fields.firstOrNull {
                    !it.valid()
                }
            }

            Up -> {
                fields.lastOrNull {
                    !it.valid()
                }
            }

            Splash -> {
                val results = mutableListOf<Boolean>()
                fields.forEach {
                    results.add(it.valid())
                }
                valid = results.all { it }
                fields.firstOrNull()
            }
        }
        invalidField?.let {
            errorMessage = if (flow == Splash)
                flow.fallbackErrorMessage
            else
                it.errorMessage
            valid = false
        }
        onValidate.invoke(valid)
        this.valid.value = (valid)
        return valid
    }
}