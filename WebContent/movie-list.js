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

const SORT_OPTION = [
	'Title ↑, Rating ↑',
	'Title ↑, Rating ↓',
	'Title ↓, Rating ↑',
	'Title ↓, Rating ↓',
	'Rating ↑, Title ↑',
	'Rating ↑, Title ↓',
	'Rating ↓, Title ↑',
	'Rating ↓, Title ↓',
]

function handleSessionData(resultData) {

	page_number = resultData['page_number']
	page_size = resultData['page_size']
	sort_option = resultData['sort_option']

	document.getElementById('SortByBtn').innerText = SORT_OPTION[sort_option]
	document.getElementById('ItemsPerPage').innerText = resultData['page_size']
	document.getElementById('pageText').innerText = resultData['page_number']

	if (page_number === 1) {
		document.getElementById('PrevLi').classList.add('disabled')
	} else {
		document.getElementById('PrevLi').classList.remove('disabled')
	}

	if (resultData['isBrowsed']) {
		if (resultData['browse_genre'] != null) {
			browseGenre = resultData['browse_genre']

			jQuery.ajax({
				dataType: 'json', // Setting return data type
				method: 'GET', // Setting request method
				url: `api/browse/genre?genre=${resultData['browse_genre']}&page_number=${page_number}&page_size=${page_size}&sort_option=${sort_option}`, // Setting request url
				success: (resultData) => handleMovieResult(resultData), // Setting callback function to handle data returned successfully by the StarsServlet
			})
		} else {
			browseTitle = resultData['browse_title']

			jQuery.ajax({
				dataType: 'json', // Setting return data type
				method: 'GET', // Setting request method
				url: `api/browse/title?title=${resultData['browse_title']}&page_number=${page_number}&page_size=${page_size}&sort_option=${sort_option}`, // Setting request url
				success: (resultData) => handleMovieResult(resultData), // Setting callback function to handle data returned successfully by the StarsServlet
			})
		}
	} else if (resultData['isSearch']) {
		searchTitle = resultData['search_title']
		searchYear = resultData['search_year']
		searchDirector = resultData['search_director']
		searchStar = resultData['search_star']

		jQuery.ajax({
			dataType: 'json', // Setting return data type
			method: 'GET', // Setting request method
			url: `api/search?title=${searchTitle}&year=${searchYear}&director=${searchDirector}&star=${searchStar}&page_number=${page_number}&page_size=${page_size}&sort_option=${sort_option}`, // Setting request url
			success: (resultData) => handleMovieResult(resultData), // Setting callback function to handle data returned successfully by the StarsServlet
		})
	} else if (resultData['isFullSearch']) {
		movieQuery = resultData['movie_query']
		jQuery.ajax({
			dataType: 'json', // Setting return data type
			method: 'GET', // Setting request method
			url: `api/full-search?query=${movieQuery}&page_number=${page_number}&page_size=${page_size}&sort_option=${sort_option}`, // Setting request url
			success: (resultData) => handleMovieResult(resultData), // Setting callback function to handle data returned successfully by the StarsServlet
		})
	}
}

function updateTable(page_number, page_size, callback) {
	//empty old table
	let movieTableBodyElement = jQuery('#movie_table_body')
	movieTableBodyElement.empty()

	//Items per page change
	if (browseGenre) {
		// Makes the HTTP GET request and registers on success callback function handleStarResult
		jQuery.ajax({
			dataType: 'json', // Setting return data type
			method: 'GET', // Setting request method
			url: `api/browse/genre?genre=${browseGenre}&page_number=${page_number}&page_size=${page_size}&sort_option=${sort_option}`, // Setting request url
			success: (resultData) => callback(resultData), // Setting callback function to handle data returned successfully by the StarsServlet
		})
	} else if (browseTitle) {
		// Makes the HTTP GET request and registers on success callback function handleStarResult
		jQuery.ajax({
			dataType: 'json', // Setting return data type
			method: 'GET', // Setting request method
			url: `api/browse/title?title=${browseTitle}&page_number=${page_number}&page_size=${page_size}&sort_option=${sort_option}`, // Setting request url
			success: (resultData) => callback(resultData), // Setting callback function to handle data returned successfully by the StarsServlet
		})
	} else if (searchTitle || searchDirector || searchYear || searchStar) {
		// Makes the HTTP GET request and registers on success callback function handleStarResult
		jQuery.ajax({
			dataType: 'json', // Setting return data type
			method: 'GET', // Setting request method
			url: `api/search?title=${searchTitle}&year=${searchYear}&director=${searchDirector}&star=${searchStar}&page_number=${page_number}&page_size=${page_size}&sort_option=${sort_option}`, // Setting request url
			success: (resultData) => callback(resultData), // Setting callback function to handle data returned successfully by the StarsServlet
		})
	} else if (movieQuery) {
		// Makes the HTTP GET request and registers on success callback function handleStarResult
		jQuery.ajax({
			dataType: 'json', // Setting return data type
			method: 'GET', // Setting request method
			url: `api/full-search?movie_query=${movieQuery}&page_number=${page_number}&page_size=${page_size}&sort_option=${sort_option}`, // Setting request url
			success: (resultData) => callback(resultData), // Setting callback function to handle data returned successfully by the StarsServlet
		})
	} else {
		// Makes the HTTP GET request and registers on success callback function handleStarResult
		jQuery.ajax({
			dataType: 'json', // Setting return data type
			method: 'GET', // Setting request method
			url: 'api/movielist', // Setting request url
			success: (resultData) => handleSessionData(resultData), // Setting callback function to handle data returned successfully by the StarsServlet
		})
	}
}

function updateSelectedItem(itemText, id) {
	//update text
	document.getElementById(id).textContent = itemText + ' '

	if (id === 'ItemsPerPage') {
		//reset movie list

		page_size = Number(itemText)

		document.getElementById('pageText').innerText = 1
		page_number = 1

		updateTable(1, page_size, handleMovieResult)
	} else if (id === 'SortByBtn') {
		//reset movie list
		if (itemText === 'Title ↑, Rating ↑') {
			sort_option = 0
		} else if (itemText === 'Title ↑, Rating ↓') {
			sort_option = 1
		} else if (itemText === 'Title ↓, Rating ↑') {
			sort_option = 2
		} else if (itemText === 'Title ↓, Rating ↓') {
			sort_option = 3
		} else if (itemText === 'Rating ↑, Title ↑') {
			sort_option = 4
		} else if (itemText === 'Rating ↑, Title ↓') {
			sort_option = 5
		} else if (itemText === 'Rating ↓, Title ↑') {
			sort_option = 6
		} else if (itemText === 'Rating ↓, Title ↓') {
			sort_option = 7
		}

		document.getElementById('pageText').innerText = 1
		page_number = 1

		updateTable(page_number, page_size, handleMovieResult)
	}
}

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

function handleMovieResult(resultData) {

	// Populate the star table
	// Find the empty table body by id "star_table_body"
	let movieTableBodyElement = jQuery('#movie_table_body')

	// Iterate through resultData, no more than 10 entries
	for (let i = 0; i < resultData.length; i++) {
		// Concatenate the html tags with resultData jsonObject

		offset = (page_number - 1) * page_size

		const ranking = offset + i + 1

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
			rowHTML +=
				'<a href="movie-list.html?browse_genre=' +
				resultData[i]['genres'][j] +
				'" ' +
				'>'
			rowHTML += resultData[i]['genres'][j]
			rowHTML += '</a>'

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

			if (j !== resultData[i]['stars'].length - 1) {
				rowHTML += ', '
			}
		}

		rowHTML += '</th>'
		// end: single star link set up

		rowHTML += '<th>'

		if (resultData[i]['rating'] === null) {
			rowHTML += 'N/A'
		} else {
			rowHTML += resultData[i]['rating']
		}

		rowHTML += '</th>'
		rowHTML +=
			'<th><button class="btn btn-success add-to-cart" data-movie-title="' +
			resultData[i]['title'] +
			'" onclick="addToCart(\'' +
			resultData[i]['title'] +
			'\')">Add</button></th>'
		rowHTML += '</tr>'

		// Append the row created to the table body, which will refresh the page
		movieTableBodyElement.append(rowHTML)
	}

	if (resultData.length < page_size) {
		document.getElementById('NextLi').classList.add('disabled')
	} else {
		document.getElementById('NextLi').classList.remove('disabled')
	}
}

function handleLoggedIn(resultData, callback) {
	if (resultData['isLoggedIn'] === true) {
		updateTable(1, 10, callback)
	} else {
		console.log('User is not logged in')
		window.location.replace('loginForm.html')
	}
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Attach the onclick event to the link
document.getElementById('NextBtn').onclick = () => {
	const newPageNum = Number(document.getElementById('pageText').innerText) + 1
	page_number = newPageNum
	document.getElementById('pageText').innerText = newPageNum

	if (newPageNum > 1) {
		document.getElementById('PrevLi').classList.remove('disabled')
	}

	updateTable(newPageNum, page_size, handleMovieResult)

	// alert('Link clicked!Page: ' + document.getElementById('pageText').innerText)
}

document.getElementById('PrevBtn').onclick = () => {
	const newPageNum = Number(document.getElementById('pageText').innerText) - 1
	page_number = newPageNum
	document.getElementById('pageText').innerText = newPageNum

	if (newPageNum === 1) {
		document.getElementById('PrevLi').classList.add('disabled')
	}

	updateTable(newPageNum, page_size, handleMovieResult)
}

let page_number = 1
let page_size = 10
let sort_option = 0

// get params for browsing;
let browseGenre = getParameterByName('browse_genre')

let browseTitle = getParameterByName('browse_title')

// Done: get params for searching
let searchTitle = getParameterByName('search_title')
let searchYear = getParameterByName('search_year')
let searchDirector = getParameterByName('search_director')
let searchStar = getParameterByName('search_star')

// get params for full search
let movieQuery = getParameterByName('movie_query')

//perform browsing for the page
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
	jQuery.ajax({
		method: "GET",
		url: "api/movie-from-id?title=" + escape(suggestion["value"]),
		success: function (data) {
			console.log(data);
			window.location.href = 'single-movie.html?id=' + data["id"];
		},
		error: function (errorData) {
			console.log("Lookup AJAX error");
			console.log(errorData);
		},
	});
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