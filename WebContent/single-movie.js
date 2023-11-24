/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */

/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */

function handleLookup(query, doneCallback) {
	console.log("Autocomplete initiated");

	// Check if the query is in the cache
	var cachedData = localStorage.getItem(query);
	if (cachedData) {
		console.log("Using cached data for query: " + query);
		handleLookupAjaxSuccess(JSON.parse(cachedData), query, doneCallback);
	} else {
		console.log("Sending AJAX request to backend Java Servlet");
		// If not in cache, send the HTTP GET request to the Java Servlet endpoint hero-suggestion
		// with the query data
		jQuery.ajax({
			method: "GET",
			url: "api/autocomplete?query=" + escape(query),
			success: function (data) {
				// Store the data in the cache
				localStorage.setItem(query, JSON.stringify(data));

				// Pass the data, query, and doneCallback function into the success handler
				handleLookupAjaxSuccess(data, query, doneCallback);
			},
			error: function (errorData) {
				console.log("Lookup AJAX error");
				console.log(errorData);
			},
		});
	}
}

function handleLookupAjaxSuccess(data, query, doneCallback) {

	// Assuming data is already an object, not a JSON string
	var jsonData = data.slice(0, 10);

	// Transform movie data into suggestion objects
	var suggestions = jsonData.map(function (movie) {
		return {
			value: movie.title,
			data: movie,
		};
	});

	console.log("Used suggestion list: ", suggestions);

	// Call the callback function provided by the autocomplete library
	doneCallback({ suggestions: suggestions });
}

function handleSelectSuggestion(suggestion) {
	// TODO: jump to the specific result page based on the selected suggestion
	window.location.href = 'movie-list.html?movie_query=' + suggestion["value"];
}

$(document).ready(function () {
	$('#movieQuery').autocomplete({
		lookup: function (query, doneCallback) {
			handleLookup(query, doneCallback);
		},
		onSelect: function (suggestion) {
			handleSelectSuggestion(suggestion);
		},
		deferRequestBy: 300,
		minChars: 3,
	});
});


function handleNormalSearch(query) {
	window.location.href = 'movie-list.html?movie_query=' + encodeURIComponent(query);
}

$('#full-text-search-form').keypress(function(event) {
	// keyCode 13 is the enter key
	if (event.keyCode == 13) {
		// pass the value of the input box to the handler function
		handleNormalSearch($('#full-text-search-form').val())
	}
})

function getParameterByName(target) {
	// Get request URL
	let url = window.location.href
	// Encode target parameter name to url encoding
	target = target.replace(/[\[\]]/g, '\\$&')

	// Ues regular expression to find matched parameter value
	let regex = new RegExp('[?&]' + target + '(=([^&#]*)|&|#|$)'),
		results = regex.exec(url)
	if (!results) return null
	if (!results[2]) return ''

	// Return the decoded parameter value
	return decodeURIComponent(results[2].replace(/\+/g, ' '))
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {
	console.log('handleResult: populating movie info from resultData')

	// populate the star info h3
	// find the empty h3 body by id "star_info"
	let movieInfoElement = jQuery('#movie_info')
	movieTitle = resultData['movie_title']
	// append two html <p> created to the h3 body, which will refresh the page
	let movieInfo =
		'<p>Title: ' +
		resultData['movie_title'] +
		'</p>' +
		'<p>Year: ' +
		resultData['movie_year'] +
		'</p>' +
		'<p>Director: ' +
		resultData['movie_director'] +
		'</p>' +
		'<p>Rating: ' +
		resultData['movie_rating'] +
		'</p>'

	movieInfo += '<p>Genres: '

	const addedGenres = new Set() // Create a Set to store added genres

	for (let i = 0; i < resultData['genres'].length; i++) {
		const genreName = resultData['genres'][i]['genre_name']

		// Check if the genre has not been added before
		if (!addedGenres.has(genreName)) {
			movieInfo += genreName + ', '
			addedGenres.add(genreName) // Add the genre to the Set
		}
	}

	// Remove the trailing comma and close the paragraph
	movieInfo = movieInfo.slice(0, -2) + '</p>'

	movieInfoElement.append(movieInfo)

	console.log('handleResult: populating star table from resultData')

	// Populate the star table
	// Find the empty table body by id "movie_table_body"
	let starTableBodyElement = jQuery('#star_table_body')

	// Concatenate the html tags with resultData jsonObject to create table rows
	for (let i = 0; i < Math.min(10, resultData['stars'].length); i++) {
		let rowHTML = ''
		rowHTML += '<tr>'
		rowHTML +=
			'<th>' +
			// Add a link to single-star.html with id passed with GET url parameter
			'<a href="single-star.html?id=' +
			resultData['stars'][i]['star_id'] +
			'">' +
			resultData['stars'][i]['star_name'] + // display star_name for the link text
			'</a>' +
			'</th>'

		//set up star dob
		rowHTML += '<th>'
		if (resultData['stars'][i]['star_dob']) {
			rowHTML += resultData['stars'][i]['star_dob']
		} else {
			rowHTML += 'N/A'
		}
		rowHTML += '</th>'

		rowHTML += '<th>'
		rowHTML += resultData['stars'][i]['movie_count']
		rowHTML += '</th>'

		rowHTML += '</tr>'

		//end: set up star dob

		// Append the row created to the table body, which will refresh the page
		starTableBodyElement.append(rowHTML)
	}
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id')
let movieTitle = ''

function handleLoggedIn(resultData, callback) {
	console.log(resultData)

	if (resultData['isLoggedIn'] === true) {
		console.log('User is logged in')
		// Makes the HTTP GET request and registers on success callback function handleStarResult
		// Makes the HTTP GET request and registers on success callback function handleResult
		jQuery.ajax({
			dataType: 'json', // Setting return data type
			method: 'GET', // Setting request method
			url: 'api/single-movie?id=' + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
			success: (resultData) => callback(resultData), // Setting callback function to handle data returned successfully by the SingleStarServlet
		})
	} else {
		console.log('User is not logged in')
		window.location.replace('loginForm.html')
	}
}

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
	dataType: 'json', // Setting return data type
	method: 'GET', // Setting request method
	url: 'api/login', // Setting request url, which is mapped by StarsServlet in Stars.java
	success: (resultData) => handleLoggedIn(resultData, handleResult), // Setting callback function to handle data returned successfully by the SingleStarServlet
})

// Click event for the "Add to Cart" button
$('.add-to-cart').click(function () {
	// Call the addToCart function when the button is clicked
	addToCart()
})

// Add event listener to the "Add" buttons
function addToCart() {
	// Make an AJAX POST request to add the movie to the shopping cart
	jQuery.ajax({
		dataType: 'json',
		method: 'POST',
		url: 'api/index', // Change the URL to match your servlet mapping
		data: { item: movieTitle }, // Send the movie ID as the "item" parameter
		success: function (response) {
			// Handle the response from the server
			if (response && response.previousItems) {
				// Display a success message or update the UI to reflect the change in the shopping cart
				alert('Item added to the shopping cart')
			} else {
				alert('Failed to add item to the shopping cart')
			}
		},
		error: function () {
			alert('Failed to add item to the shopping cart')
		},
	})
}
