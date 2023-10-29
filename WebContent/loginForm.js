function handleResult(resultData) {
	console.log(resultData)

	if (resultData['success'] === true) {
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
	}
}

document
	.getElementById('login-form')
	.addEventListener('submit', function (event) {
		event.preventDefault() // Prevent form submission

		// Get form values
		const email = document.getElementById('email').value
		const password = document.getElementById('password').value

		// Validate form values
		// Send form values to server

		jQuery.ajax({
			dataType: 'json', // Setting return data type
			method: 'POST', // Setting request method
			url: 'api/login?email=' + email + '&password=' + password, // Setting request url, which is mapped by StarsServlet in Stars.java
			success: (resultData) => handleResult(resultData), // Setting callback function to handle data returned successfully by the SingleStarServlet
			error: function (jqXHR, textStatus, errorThrown) {
				// Callback for a failed request

				// Access JSON error response, if available
				if (jqXHR.responseJSON) {
					// You can access the error JSON here
					console.log('Error JSON:', jqXHR.responseJSON)
					handleResult(jqXHR.responseJSON)
				}

				// You can also access other error information
				console.log('Status Code:', jqXHR.status)
				console.log('Status Text:', jqXHR.statusText)
			},
		})
	})
