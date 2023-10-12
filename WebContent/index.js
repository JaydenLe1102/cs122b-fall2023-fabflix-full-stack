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
function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    console.log(resultData)
    console.log("hello world hello world 12345")


    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["title"] +     // display star_name for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";

        //genres setup

        rowHTML += "<th>"

        for (j = 0; j < resultData[i]["genres"].length; j++){

            rowHTML += resultData[i]['genres'][j]

            if (j != resultData[i]["genres"].length - 1){
                rowHTML += ', '
            }
        }

        rowHTML += "</th>";

        //end: genres setup

        // single star link setup
        rowHTML += "<th>"

        for (j = 0; j < resultData[i]["stars"].length; j++){

            rowHTML += '<a href="single-star.html?id=' + resultData[i]['stars'][j]['star_id'] + '">'
                + resultData[i]['stars'][j]["name"] +     // display star_name for the link text
                '</a>'

            if (j != resultData[i]["stars"].length - 1){
                rowHTML += ', '
            }
        }

        rowHTML += "</th>";
        // end: single star link set up

        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "</tr>";

        console.log(rowHTML)

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});