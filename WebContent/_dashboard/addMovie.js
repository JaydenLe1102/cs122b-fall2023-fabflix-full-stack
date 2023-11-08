function handleAddMovie(resultData) {
	console.log(resultData)

	if (resultData['success'] === true) {
		alert('Add Movie Success')
		document.getElementById('starName').value = ''
		document.getElementById('birthYear').value = ''
	} else {
		alert('Add Movie Failed')
	}
}

function registerSubmitEvent() {
	document
		.getElementById('movie-form')
		.addEventListener('submit', function (event) {
			event.preventDefault() // Prevent form submission

			const title = document.getElementById('movieTitle').value
			const year = document.getElementById('year').value
			const director = document.getElementById('director').value
			const starName = document.getElementById('starName').value
			const starBirthYear = document.getElementById('birthYear').value
			const genreName = document.getElementById('genreName').value

			console.log({
				title,
				year,
				director,
				starName,
				starBirthYear,
				genreName,
			})

			jQuery.ajax({
				dataType: 'json',
				method: 'POST',
				url: 'api/addMovie',
				data: {
					title,
					year,
					director,
					starName,
					starBirthYear,
					genreName,
				},
				success: (resultData) => handleAddMovie(resultData),
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
