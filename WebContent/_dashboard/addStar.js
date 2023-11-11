function handleAddStar(resultData) {
	console.log(resultData)

	if (resultData['success'] === true) {
		alert('Add Star Success')
		document.getElementById('starName').value = ''
		document.getElementById('birthYear').value = ''
	} else {
		alert('Add Star Failed')
	}
}

function registerSubmitEvent() {
	document
		.getElementById('star-form')
		.addEventListener('submit', function (event) {
			event.preventDefault() // Prevent form submission

			const starName = document.getElementById('starName').value
			const starBirthYear = document.getElementById('birthYear').value

			console.log(starName)
			console.log(starBirthYear)

			jQuery.ajax({
				dataType: 'json', // Setting return data type
				method: 'POST', // Setting request method
				url: 'api/addStar', // Setting request url, which is mapped by StarsServlet in Stars.java
				data: {
					name: starName,
					birthYear: starBirthYear,
				},
				success: (resultData) => handleAddStar(resultData), // Setting callback function to handle data returned successfully by the StarsServlet
			})
		})
}

function handleLoggedIn(resultData) {
	console.log(resultData)
	console.log('User is logged in')
	if (resultData['isLoggedIn'] === true) {
		registerSubmitEvent()
	} else {
		console.log('User is not logged in')
		window.location.replace('loginForm.html')
	}
}

//perform browsing for the page
jQuery.ajax({
	dataType: 'json', // Setting return data type
	method: 'GET', // Setting request method
	url: 'api/login', // Setting request url, which is mapped by StarsServlet in Stars.java
	success: (resultData) => handleLoggedIn(resultData), // Setting callback function to handle data returned successfully by the StarsServlet
})
