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

function handleSessionData(resultData) {
	console.log('handleSessionData: populating session data from resultData')
	console.log('resultData: ' + JSON.stringify(resultData, null, 4))

	page_number = resultData['page_number']
	page_size = resultData['page_size']

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
				url: `api/browse/genre?genre=${resultData['browse_genre']}&page_number=${page_number}&page_size=${page_size}`, // Setting request url
				success: (resultData) => handleMovieResult(resultData), // Setting callback function to handle data returned successfully by the StarsServlet
			})
		} else {
			browseTitle = resultData['browse_title']

			jQuery.ajax({
				dataType: 'json', // Setting return data type
				method: 'GET', // Setting request method
				url: `api/browse/title?title=${resultData['browse_title']}&page_number=${page_number}&page_size=${page_size}`, // Setting request url
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
			url: `api/search?title=${searchTitle}&year=${searchYear}&director=${searchDirector}&star=${searchStar}&page_number=${page_number}&page_size=${page_size}`, // Setting request url
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
			url: `api/browse/genre?genre=${browseGenre}&page_number=${page_number}&page_size=${page_size}`, // Setting request url
			success: (resultData) => callback(resultData), // Setting callback function to handle data returned successfully by the StarsServlet
		})
	} else if (browseTitle) {
		// Makes the HTTP GET request and registers on success callback function handleStarResult
		jQuery.ajax({
			dataType: 'json', // Setting return data type
			method: 'GET', // Setting request method
			url: `api/browse/title?title=${browseTitle}&page_number=${page_number}&page_size=${page_size}`, // Setting request url
			success: (resultData) => callback(resultData), // Setting callback function to handle data returned successfully by the StarsServlet
		})
	} else if (searchTitle || searchDirector || searchYear || searchStar) {
		// Makes the HTTP GET request and registers on success callback function handleStarResult
		jQuery.ajax({
			dataType: 'json', // Setting return data type
			method: 'GET', // Setting request method
			url: `api/search?title=${searchTitle}&year=${searchYear}&director=${searchDirector}&star=${searchStar}&page_number=${page_number}&page_size=${page_size}`, // Setting request url
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
	console.log(resultData)
	console.log('handleMovieResult: populating movie table from resultData')
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

		rowHTML += '<th>' + resultData[i]['rating'] + '</th>'
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
	console.log(resultData)
	console.log('User is logged in')
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

// get params for browsing;
let browseGenre = getParameterByName('browse_genre')

let browseTitle = getParameterByName('browse_title')

console.log(browseTitle)
console.log(browseGenre)
// Done: get params for searching
let searchTitle = getParameterByName('search_title')
let searchYear = getParameterByName('search_year')
let searchDirector = getParameterByName('search_director')
let searchStar = getParameterByName('search_star')
console.log(searchTitle)
console.log(searchStar)

//perform browsing for the page
jQuery.ajax({
	dataType: 'json', // Setting return data type
	method: 'GET', // Setting request method
	url: 'api/login', // Setting request url, which is mapped by StarsServlet in Stars.java
	success: (resultData) => handleLoggedIn(resultData, handleMovieResult), // Setting callback function to handle data returned successfully by the StarsServlet
})
