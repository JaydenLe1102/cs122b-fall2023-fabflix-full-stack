$(document).ready(function () {
    // Retrieve the salesData parameter from the URL
    const params = new URLSearchParams(window.location.search);
    const salesDataString = params.get('salesData');

    // Parse the salesData JSON string into an object
    const salesDataObject = JSON.parse(decodeURIComponent(salesDataString));

    // Convert the object into an array using Object.values()
    const salesData = Object.values(salesDataObject);

    // Verify the data structure to ensure it's an array of objects
    console.log(salesData);

    // Populate sales table with data
    salesData.forEach(sale => {
        const price = 100; // Default price per item
        const total = price * sale.quantity;

        $('#sales-table-body').append(`
            <tr>
                <td>${sale.saleId}</td>
                <td>${sale.movieTitle}</td>
                <td>${sale.quantity}</td>
                <td>$${price.toFixed(2)}</td>
                <td>$${total.toFixed(2)}</td>
            </tr>
        `);
    });

    // Calculate grand total
    const grandTotal = salesData.reduce((acc, sale) => acc + (sale.quantity * 100), 0);

    // Display grand total
    $('#grand-total').text(`$${grandTotal.toFixed(2)}`);
});

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