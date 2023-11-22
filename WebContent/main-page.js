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




function handleGenres(resultData) {
    console.log(resultData)
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
    console.log(resultData)

    if (resultData['isLoggedIn'] === true) {
        console.log('User is logged in')
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

function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated")
    console.log("sending AJAX request to backend Java Servlet")

    // TODO: if you want to check past query results first, you can do it here

    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data
    jQuery.ajax({
        "method": "GET",
        // generate the request url from the query.
        // escape the query string to avoid errors caused by special characters
        "url": "api/autocomplete?query=" + escape(query),
        "success": function(data) {
            // pass the data, query, and doneCallback function into the success handler
            handleLookupAjaxSuccess(data, query, doneCallback)
        },
        "error": function(errorData) {
            console.log("lookup ajax error")
            console.log(errorData)
        }
    })
}

function handleLookupAjaxSuccess(data, query, doneCallback) {

    // TODO Caching
    console.log("lookup ajax successful")
    // Assuming data is already an object, not a JSON string
    var jsonData = data.slice(0, 10);

    // Transform movie data into suggestion objects
    var suggestions = jsonData.map(function(movie) {
        return {
            value: movie.title,
            data: movie
        };
    });

    // Call the callback function provided by the autocomplete library
    doneCallback({ suggestions: suggestions });
}

function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion
    window.location.href = 'movie-list.html?movie_query=' + suggestion["value"];
    console.log("you select " + suggestion["value"])
}

$(document).ready(function() {
    $('#movieQuery').autocomplete({
        lookup: function (query, doneCallback) {
            console.log("Hello");
            handleLookup(query, doneCallback)
        },
        onSelect: function(suggestion) {
            handleSelectSuggestion(suggestion)
        },
        // set delay time
        deferRequestBy: 300,
        minChars: 3,
        // there are some other parameters that you might want to use to satisfy all the requirements
        // TODO: add other parameters, such as minimum characters
    });
});


function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
    window.location.href = 'movie-list.html?movie_query=' + encodeURIComponent(query);
}

$('#full-text-search-form').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#full-text-search-form').val())
    }
})
