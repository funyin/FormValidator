# FormValidator [![](https://jitpack.io/v/funyin/FormValidator.svg)](https://jitpack.io/#funyin/FormValidator)
A form validation library for android jetpack compose

## Features
- Determine Validation Flow.
    __Flow.Dowm__|__Flow.Up__|__Flow.Splash__
- Custom Validation
- Validation Snackbar
- Validation State Callbacks
- Library is well documented

## Preview


https://user-images.githubusercontent.com/38915569/149603789-1f47436b-b8f9-44a6-98a6-6ec389a75e7b.mp4



## Example
```kotlin
@Composable
    fun ScreenContent() {
        var name by remember { mutableStateOf("") }
        var nameError by remember { mutableStateOf<String?>(null) }
        var email by remember { mutableStateOf("") }
        var emailError by remember { mutableStateOf<String?>(null) }
        val nameField = "Name"
        val emailField = "Email"
        val validator = FormValidator(
            fields = listOf(
                ValidationField(
                    value = name,
                    name = nameField,
                    onError = {
                        nameError = it
                    }),
                ValidationField(
                    value = email,
                    onError = {
                        emailError = it
                    }, type = FormValidator.Type.Email
                )
            )
        )
        Form(
            validator = validator,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            AppTextField(
                label = nameField,
                value = name,
                placeholder = "Enter name",
                onValueChanged = { name = it },
                errorMessage = nameError
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppTextField(
                label = emailField,
                value = email,
                placeholder = "Enter email",
                onValueChanged = { email = it },
                errorMessage = emailError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(40.dp))
            AppButton(text = "Submit") {
                if(validator.validate())
                    showToast(validator.errorMessage)
            }
        }
    }
```

## Getting started
### Step 1. Add the JitPack repository to your build file
```gradle
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

### Step 2. Add the dependency
```gradle
dependencies {
	        implementation 'com.github.funyin:FormValidator:1.0.0'
	}
```

[![BuyMeAShawrma.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1619907239535/KqJOyu-70.png)](https://www.buymeacoffee.com/funyinkash) 
