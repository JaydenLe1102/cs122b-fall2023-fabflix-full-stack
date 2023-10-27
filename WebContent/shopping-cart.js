let cart = $("#cart");

/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle session response");
    console.log(resultDataJson);
    console.log(resultDataJson["sessionID"]);

    // show the session information
    $("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
    $("#lastAccessTime").text("Last access time: " + resultDataJson["lastAccessTime"]);

    // show cart information
    handleCartArray(resultDataJson["previousItems"]);
}

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleCartArray(resultArray) {
    console.log(resultArray);
    let item_list = $("#item_list");
    // change it to html list
    let res = "<ul>";
    for (let i = 0; i < resultArray.length; i++) {
        // each item will be in a bullet point
        res += "<li>" + resultArray[i] + "</li>";
    }
    res += "</ul>";

    // clear the old array and show the new array in the frontend
    item_list.html("");
    item_list.append(res);
}

/**
 * Submit form content with POST method
 * @param cartEvent
 */
function handleCartInfo(cartEvent) {
    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the URL defined in the HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    cartEvent.preventDefault();

    $.ajax("api/index", {
        method: "POST",
        data: cart.serialize(),
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            handleCartArray(resultDataJson["previousItems"]);
        }
    });

    // clear input form
    cart[0].reset();
}

$.ajax("api/index", {
    method: "GET",
    success: function (resultDataString) {
        handleSessionData(resultDataString);
        updateCartTable(); // Call this function to load the shopping cart table
    }
});

// Bind the submit action of the form to an event handler function
cart.submit(handleCartInfo);

// Function to update the shopping cart table
function updateCartTable() {
    // Make an AJAX GET request to fetch shopping cart items
    $.ajax("/api/index", {
        method: "GET",
        success: function (resultDataString) {
            let resultDataJson = JSON.parse(resultDataString);
            let cartTableBody = $("#cart-table-body");
            cartTableBody.empty();

            // Loop through shopping cart items and populate the table
            for (let item of resultDataJson.previousItems) {
                // Create a new row in the table for each item
                let row = $("<tr></tr>");
                row.append("<td>" + item.title + "</td>");
                row.append("<td><button class='btn btn-secondary decrease-quantity' data-title='" + item.title + "'>-</button> " + item.quantity + " <button class='btn btn-secondary increase-quantity' data-title='" + item.title + "'>+</button></td>");
                row.append("<td>$" + item.price.toFixed(2) + "</td>");

                // Calculate the item's total price based on quantity
                let itemTotalPrice = item.price * item.quantity;
                row.append("<td>$" + itemTotalPrice.toFixed(2) + "</td>");
                // Add a delete button that calls a function to remove the item
                row.append("<td><button class='btn btn-danger delete-item' data-title='" + item.title + "'>Delete</button></td>");
                cartTableBody.append(row);
            }
        }
    });
}
