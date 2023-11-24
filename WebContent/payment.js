$(document).ready(function () {
	// Function to get customer ID
	getCustomerIDAndHandlePayment()
})

function getCustomerIDAndHandlePayment() {
	// Retrieve the customer ID through an AJAX GET request
	$.ajax({
		dataType: 'json',
		method: 'GET',
		url: 'api/login',
		success: function (resultData) {
			if (resultData['isLoggedIn'] === true) {
				const customerID = resultData['customerId']
				if (customerID) {
					// If logged in, handle the payment
					handlePayment(customerID)
				} else {
					alert('Customer ID not found')
				}
			}
		},
		error: function (xhr, status, error) {
			alert('Error retrieving customer ID: ' + error)
		},
	})
}

function handlePayment(customerID) {
	$('#payment-form').submit(function (event) {
		event.preventDefault()
		const firstName = $('#firstName').val()
		const lastName = $('#lastName').val()
		const creditCardNumber = $('#creditCardNumber').val()
		const expirationString = $('#expirationDate').val()
		const totalPrice = $('#totalPrice').val()

		// Convert the expiration string to a JavaScript Date object
		const expirationDate = new Date(expirationString)
		// Format the expiration date to match the SQL DATE format (YYYY-MM-DD)
		const formattedExpiration = expirationDate.toISOString().split('T')[0]

		$.ajax({
			type: 'POST',
			url: `api/payment?customerID=${customerID}&firstName=${firstName}&lastName=${lastName}&creditCardNumber=${creditCardNumber}&expirationDate=${formattedExpiration}`,
			contentType: 'application/json',
			success: function (response) {
				if (response.success) {
					// Get the sales data from the response and convert it to a string
					var salesData = JSON.stringify(response.sales)
					console.log(salesData)
					// URL encode the JSON string
					const encodedSalesData = encodeURIComponent(salesData)
					// Redirect to confirmation.html with sales data as a query parameter
					window.location.href = `confirmation.html?salesData=${encodedSalesData}`
				} else {
					alert(
						'Transaction Failed: ' +
							response.error +
							'. Reenter Payment information'
					)
				}
			},
			error: function (xhr, status, error) {
				alert(
					'Error processing payment: ' + error + '. Reenter Payment information'
				)
			},
		})
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