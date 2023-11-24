/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
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

function handleMovieResult(resultData) {
	console.log('handleMovieResult: populating movie table from resultData')

	// Populate the star table
	// Find the empty table body by id "star_table_body"
	let movieTableBodyElement = jQuery('#movie_table_body')

	// Iterate through resultData, no more than 10 entries
	for (let i = 0; i < resultData.length; i++) {
		// Concatenate the html tags with resultData jsonObject

		const ranking = i + 1

		let rowHTML = ''

		rowHTML += '<tr>'
		rowHTML += '<th>' + ranking + '</th>' // row number
		rowHTML +=
			'<th>' +
			// Add a link to single-star.html with id passed with GET url parameter
			'<a href="single-movie.html?id=' +
			resultData[i]['movie_id'] +
			'">' +
			resultData[i]['title'] + // display star_name for the link text
			'</a>' +
			'</th>'
		rowHTML += '<th>' + resultData[i]['year'] + '</th>'
		rowHTML += '<th>' + resultData[i]['director'] + '</th>'

		//genres setup

		rowHTML += '<th>'

		let j
		for (j = 0; j < resultData[i]['genres'].length; j++) {
			rowHTML += resultData[i]['genres'][j]

			if (j !== resultData[i]['genres'].length - 1) {
				rowHTML += ', '
			}
		}

		rowHTML += '</th>'

		//end: genres setup

		// single star link setup
		rowHTML += '<th>'

		for (j = 0; j < resultData[i]['stars'].length; j++) {
			rowHTML +=
				'<a href="single-star.html?id=' +
				resultData[i]['stars'][j]['star_id'] +
				'">' +
				resultData[i]['stars'][j]['name'] + // display star_name for the link text
				'</a>'

			if (j != resultData[i]['stars'].length - 1) {
				rowHTML += ', '
			}
		}

		rowHTML += '</th>'
		// end: single star link set up

		rowHTML += '<th>' + resultData[i]['rating'] + '</th>'
		rowHTML +=
			'<th><button class="btn btn-success add-to-cart" data-movie-title="' +
			resultData[i]['title'] +
			'" onclick="addToCart(\'' +
			resultData[i]['title'] +
			'\')">Add</button></th>'
		rowHTML += '</tr>'
		console.log(rowHTML)

		// Append the row created to the table body, which will refresh the page
		movieTableBodyElement.append(rowHTML)
	}
}

function handleLoggedIn(resultData, callback) {
	console.log(resultData)

	if (resultData['isLoggedIn'] === true) {
		console.log('User is logged in')
		// Makes the HTTP GET request and registers on success callback function handleStarResult
		jQuery.ajax({
			dataType: 'json', // Setting return data type
			method: 'GET', // Setting request method
			url: 'api/movies', // Setting request url, which is mapped by StarsServlet in Stars.java
			success: (resultData) => callback(resultData), // Setting callback function to handle data returned successfully by the StarsServlet
		})
	} else {
		console.log('User is not logged in')
		window.location.replace('loginForm.html')
	}
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

jQuery.ajax({
	dataType: 'json', // Setting return data type
	method: 'GET', // Setting request method
	url: 'api/login', // Setting request url, which is mapped by StarsServlet in Stars.java
	success: (resultData) => handleLoggedIn(resultData, handleMovieResult), // Setting callback function to handle data returned successfully by the StarsServlet
})

// Add event listener to the "Add" buttons
function addToCart(movieTitle) {
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
