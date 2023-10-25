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
