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


function handleGenres(resultData) {
    let genreList = jQuery('#GenreList')
    for (let i = 0; i < resultData.length; i++) {
        const genre = resultData[i]
        genreList.append(
            '<a href="movie-list.html?browse_genre=' +
            genre +
            '" ' +
            "style='color: black' " +
            '>' +
            '<li class ="liMain">' +
            genre +
            '</li>' +
            '</a>'
        )
    }
}


function handleLoggedIn(resultData) {

    if (resultData['isLoggedIn'] === true) {
        // Makes the HTTP GET request and registers on success callback function handleStarResult
        let numberList = jQuery('#BrowseNumber')
        for (let i = 0; i <= 9; i++) {
            numberList.append(
                '<a href="movie-list.html?browse_title=' +
                i +
                '" ' +
                "style='color: black' " +
                '>' +
                '<li class ="liMain">' +
                i +
                '</li>' +
                '</a>'
            )
        }

        let letterList = jQuery('#BrowseLetter')
        for (let i = 65; i <= 90; i++) {
            const letter = String.fromCharCode(i)
            letterList.append(
                '<a href="movie-list.html?browse_title=' +
                letter +
                '" ' +
                "style='color: black' " +
                '>' +
                '<li class ="liMain">' +
                letter +
                '</li>' +
                '</a>'
            )
        }

        letterList.append(
            '<a href="movie-list.html?browse_title=*' +
            '" ' +
            "style='color: black' " +
            '>' +
            '<li class ="liMain">' +
            '*' +
            '</li>' +
            '</a>'
        )

        jQuery.ajax({
            dataType: 'json', // Setting return data type
            method: 'GET', // Setting request method
            url: 'api/genres', // Setting request url, which is mapped by StarsServlet in Stars.java
            success: (resultData) => handleGenres(resultData), // Setting callback function to handle data returned successfully by the StarsServlet
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
    success: (resultData) => handleLoggedIn(resultData), // Setting callback function to handle data returned successfully by the StarsServlet
})

