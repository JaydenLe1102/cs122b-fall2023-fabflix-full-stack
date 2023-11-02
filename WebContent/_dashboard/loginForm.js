function handleResult(resultData) {
    console.log(resultData)

    if (resultData['success'] === true) {

        var currentURL = window.location.href;
        console.log("Current URL: " + currentURL);

        var currentRoot = window.location.protocol + "//" + window.location.host;
        console.log("Current Root: " + currentRoot);
        alert('Login Success')
		
        window.location.replace('./main-page.html')
    } else {
        if (resultData['reason'] === 'email') {
            alert('Email does not exist')
        } else if (resultData['reason'] === 'password') {
            alert('Password is incorrect')
        } else if (resultData['reason'] === 'already') {
            alert('You have already logged in')
        } else {
            alert('Login Failed')
        }
        // Clear all form fields
        document.getElementById('email').value = ''
        document.getElementById('password').value = ''

        resetRecaptcha()
    }
}

function resetRecaptcha() {
    grecaptcha.reset()
    reCaptchaToken = null
}

const form = document.getElementById('login-form')
let reCaptchaToken = null

function onRecaptchaVerify(token) {
    // This function is called when the ReCAPTCHA verification is successful
    // You can now submit the form
    reCaptchaToken = token
}

document
    .getElementById('login-form')
    .addEventListener('submit', function (event) {
        event.preventDefault() // Prevent form submission
        if (reCaptchaToken !== null) {
            // Get form values
            const email = document.getElementById('email').value
            const password = document.getElementById('password').value

            // Validate form values (add your validation logic here)

            // Send form values and reCAPTCHA token to the server
            jQuery.ajax({
                dataType: 'json',
                method: 'POST',
                url: 'api/login',
                data: {
                    email: email,
                    password: password,
                    reCaptchaToken: reCaptchaToken, // Send the reCAPTCHA token to the server
                },
                success: function (resultData) {
                    handleResult(resultData)
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log('Status Code:', jqXHR.status)
                    console.log('Status Text:', jqXHR.statusText)
                    if (jqXHR.status === 498) {
                        alert('Recaptcha failed. You are a robot')
                        document.getElementById('email').value = ''
                        document.getElementById('password').value = ''
                        resetRecaptcha()

                        return
                    }

                    if (jqXHR.responseJSON) {
                        console.log('Error JSON:', jqXHR.responseJSON)
                        handleResult(jqXHR.responseJSON)
                    }
                },
            })
        } else {
            alert('Please verify you are not a robot')
        }
        // Execute the reCAPTCHA verification
    })
